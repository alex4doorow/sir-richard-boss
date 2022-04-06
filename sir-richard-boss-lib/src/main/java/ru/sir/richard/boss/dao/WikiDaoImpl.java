package ru.sir.richard.boss.dao;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import ru.sir.richard.boss.config.DbConfig;
import ru.sir.richard.boss.model.data.Address;
import ru.sir.richard.boss.model.data.Product;
import ru.sir.richard.boss.model.data.ProductCategory;
import ru.sir.richard.boss.model.data.SupplierStock;
import ru.sir.richard.boss.model.data.SupplierStockProduct;
import ru.sir.richard.boss.model.data.conditions.ConditionResult;
import ru.sir.richard.boss.model.data.conditions.ProductConditions;
import ru.sir.richard.boss.model.types.CrmTypes;
import ru.sir.richard.boss.model.types.OrderAmountTypes;
import ru.sir.richard.boss.model.types.PaymentDeliveryMethods;
import ru.sir.richard.boss.model.types.ProductTypes;
import ru.sir.richard.boss.model.types.SupplierTypes;
import ru.sir.richard.boss.model.utils.DateTimeUtils;
import ru.sir.richard.boss.model.utils.Pair;
import ru.sir.richard.boss.model.utils.TextUtils;

@Repository
public class WikiDaoImpl extends AnyDaoImpl implements WikiDao {

	private final Logger logger = LoggerFactory.getLogger(WikiDaoImpl.class);
	
	private List<ProductCategory> categories;
	private List<Product> products;

	@Override
	public void init() {
		logger.debug("WikiDaoImpl.init()");
		this.categories = instanceCategories();
		this.products = instanceProducts();
		instanceProductsSpecialPrice();
		instanceProductsYandexOffer();
		logger.debug("products.size(): {}", products.size());		
		/*
		for (Product item : products) {
			logger.debug("product():{},{},{}", item.getId(), item.getName(), item.getPrice());			
		}
		*/	
		clearSession();
	}

	@Override
	public List<ProductCategory> getCategories() {
		return categories;
	}	
	
	@Override
	public List<Product> getProducts() {
		return products;
	}

	@Override
	public ProductCategory getCategoryById(int categoryId) {
		if (categories == null) {
			return null;
		}		
		for (ProductCategory category: categories) {
			if (categoryId == category.getId()) {
				return category;
			}	
		}
		return null;		
	}
	
	@Override
	public Product getProductById(int productId) {

		if (products == null) {
			return null;
		}
		for (Product product: products) {
			if (productId == product.getId()) {
				return product;
			}			
		}
		return null;		
	}
	
	@Override
	public Product getDbProductById(int productId) {		
		final String sqlSelectProduct = "SELECT p.* FROM sr_v_product p WHERE p.product_id = ?";
		final String sqlSelectYmOffer = "SELECT shopsku shop_sku, supplier_stock, yandex_seller, yandex_sku, special_price"
				+ " FROM oc_yb_offers WHERE product_id = ? ";
		final String sqlSelectOzonOffer = "SELECT SUPPLIER_STOCK, MARKETPLACE_SELLER, market_sku, special_price"
				+ " FROM sr_marketpace_offer WHERE product_id = ? AND marketplace_type = 5";
		
		final String sqlSelectProductCount = "SELECT count(*) count_id FROM sr_v_product p WHERE p.product_id = ?";				
		int countProduct = this.jdbcTemplate.queryForObject(sqlSelectProductCount,
		        new Object[]{productId},
		        new RowMapper<Integer>() {
					@Override
		            public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
		            	return rs.getInt("COUNT_ID");	
		            }
		        });
		if (countProduct == 0) {
			logger.error("countProduct == 0: {}", productId);
			return null;
		}		
		
		Product product = this.jdbcTemplate.queryForObject(sqlSelectProduct,
		        new Object[]{productId},
		        new RowMapper<Product>() {
					@Override
		            public Product mapRow(ResultSet rs, int rowNum) throws SQLException {						
						Product product = new Product(rs.getInt("PRODUCT_ID"), TextUtils.removeHTMLShit(rs.getString("NAME")));

						product.setModel(TextUtils.removeHTMLShit(rs.getString("MODEL")));												
						product.setSku(rs.getString("SKU"));
						product.setDeliveryName(rs.getString("DELIVERY_NAME"));
						product.setPrice(rs.getBigDecimal("PRODUCT_PRICE"));						
						product.setPriceWithoutDiscount(rs.getBigDecimal("PRODUCT_PRICE"));
																	
						product.setQuantity(rs.getInt("PRODUCT_QUANTITY"));
						product.setCategory(getCategoryById(rs.getInt("CATEGORY_ID")));	
						product.setStockQuantity(rs.getInt("STOCK_QUANTITY"));
						if (rs.getBigDecimal("SUPPLIER_PRICE") == null) {
							product.setSupplierPrice(BigDecimal.ZERO);	
						} else {
							product.setSupplierPrice(rs.getBigDecimal("SUPPLIER_PRICE"));			
						}
						if (rs.getBigDecimal("WEIGHT").compareTo(BigDecimal.ZERO) == 1) {
							if (rs.getInt("WEIGHT_CLASS_ID") == 1) {
								// kg
								product.getStore().setWeight(rs.getBigDecimal("WEIGHT"));
							} else {
								// g --> kg
								product.getStore().setWeight(rs.getBigDecimal("WEIGHT").divide(BigDecimal.valueOf(1000), 4, RoundingMode.HALF_UP));
							}
						} else {
							product.getStore().setWeight(BigDecimal.valueOf(0.5));
						}						
						product.getStore().setLength(rs.getInt("LENGTH") < 0 ? 10 : rs.getInt("LENGTH"));
						product.getStore().setWidth(rs.getInt("WIDTH") < 0 ? 10 : rs.getInt("WIDTH"));
						product.getStore().setHeight(rs.getInt("HEIGHT") < 0 ? 10 : rs.getInt("HEIGHT"));
						
						if (rs.getInt("STATUS") == 1) {
							product.setVisible(true);
						} else {
							product.setVisible(false);
						}						
						
						if (rs.getString("JAN").equals("0") || rs.getString("JAN").equals("102")) {
							product.setDeliveryMethod(PaymentDeliveryMethods.CURRENT);
						} else if (rs.getString("JAN").equals("101")) {
							product.setDeliveryMethod(PaymentDeliveryMethods.FULL);
						} else if (rs.getString("JAN").equals("103")) {
							product.setDeliveryMethod(PaymentDeliveryMethods.PVZ_FREE);
						}
						
						if (rs.getString("ISBN").equals("101")) {
							product.setType(ProductTypes.ADDITIONAL);
						} else {
							product.setType(ProductTypes.MAIN);
						}		
						
						product.getStore().setMetaTitle(rs.getString("PRODUCT_META_TITLE"));
						product.getStore().setMetaDescription(rs.getString("PRODUCT_META_DESCRIPTION"));
						product.getStore().setMetaKeyword(rs.getString("PRODUCT_META_KEYWORD"));
						
						if (StringUtils.isNoneEmpty(rs.getString("SKU").trim())) {
							
							PreparedStatement pstmtOffer = null;
							Connection conn = null;
							try {
								Class.forName(DbConfig.JDBC_DRIVER);
								conn = DriverManager.getConnection(DbConfig.DB_PM_PRODUCTION_URL);
																					
								pstmtOffer = conn.prepareStatement(sqlSelectYmOffer);
								pstmtOffer.setInt(1,  product.getId());
								
								ResultSet rsOffer = pstmtOffer.executeQuery();
								while (rsOffer.next()) {
									product.setSku(rsOffer.getString("SHOP_SKU"));
									product.getMarket(CrmTypes.YANDEX_MARKET).setMarketSku(rsOffer.getString("YANDEX_SKU"));
																											
									if (rsOffer.getInt("SUPPLIER_STOCK") == 1) {
										product.getMarket(CrmTypes.YANDEX_MARKET).setSupplierStock(true);
									}
									if (rsOffer.getInt("YANDEX_SELLER") == 1) {
										product.getMarket(CrmTypes.YANDEX_MARKET).setMarketSeller(true);
									}
									product.getMarket(CrmTypes.YANDEX_MARKET).setSpecialPrice(rsOffer.getBigDecimal("SPECIAL_PRICE"));									
								}			
								
							} catch (SQLException se) {
								logger.error("SQLException: ", se);
							} catch (Exception e) {
								logger.error("Exception: ", e);
							} finally {			
								try {
									if (pstmtOffer != null) {
										conn.close();
									}
								} catch (SQLException se) {
									// nothing...				
								} 
								try {
									if (conn != null) {
										conn.close();
									}
								} catch (SQLException se) {
									logger.error("SQLException: ", se);				
								} 
							}
							
							pstmtOffer = null;
							conn = null;
							try {
								Class.forName(DbConfig.JDBC_DRIVER);
								conn = DriverManager.getConnection(DbConfig.DB_PM_PRODUCTION_URL);
																					
								pstmtOffer = conn.prepareStatement(sqlSelectOzonOffer);
								pstmtOffer.setInt(1,  product.getId());
								
								ResultSet rsOffer = pstmtOffer.executeQuery();
								while (rsOffer.next()) {
					
									product.getMarket(CrmTypes.OZON).setMarketSku(rsOffer.getString("MARKET_SKU"));
																											
									if (rsOffer.getInt("SUPPLIER_STOCK") == 1) {
										product.getMarket(CrmTypes.OZON).setSupplierStock(true);
									}
									if (rsOffer.getInt("MARKETPLACE_SELLER") == 1) {
										product.getMarket(CrmTypes.OZON).setMarketSeller(true);
									}	
									product.getMarket(CrmTypes.OZON).setSpecialPrice(rsOffer.getBigDecimal("SPECIAL_PRICE"));
								}			
								
							} catch (SQLException se) {
								logger.error("SQLException: ", se);
							} catch (Exception e) {
								logger.error("Exception: ", e);
							} finally {			
								try {
									if (pstmtOffer != null) {
										conn.close();
									}
								} catch (SQLException se) {
									// nothing...				
								} 
								try {
									if (conn != null) {
										conn.close();
									}
								} catch (SQLException se) {
									logger.error("SQLException: ", se);				
								} 
							}	
							
							
							
							
							
							
						}
		                return product;
		            }
		        });
		
		return getDbProductByIdSetSpecialPrice(product);
	}
	
	private Product getDbProductByIdSetSpecialPrice(Product product) {
		
		final String sqlSelectSpecialPrices = "SELECT * FROM oc_product_special ps"
				+ "  WHERE (product_id = ?) and (((date_start is null or date_start < ?) and (date_end is null or date_end < ?))"
				+ "      OR ((date_start is null or date_start < ?) and (date_end > ?))"
				+ "      OR ((date_start < ?) and (date_end is null or date_end < ?)))"
				+ "  ORDER BY PRODUCT_ID";
		Date zeroDate;
		try {
			zeroDate = DateTimeUtils.defaultFormatStringToDate("01.01.0001");
		} catch (ParseException e) {
			zeroDate = null;
		}
		List<Product> specialProducts = this.jdbcTemplateSlave.query(sqlSelectSpecialPrices,
				new Object[]{
						product.getId(),
						zeroDate, zeroDate, 
						zeroDate, DateTimeUtils.sysDate(),
						DateTimeUtils.sysDate(), zeroDate 
						},
				new RowMapper<Product>() {
					@Override
					public Product mapRow(ResultSet rs, int rowNum) throws SQLException {
						Product item = new Product();
						BigDecimal specialPrice = rs.getBigDecimal("PRICE");				
						if (specialPrice != null && specialPrice != BigDecimal.ZERO) {
							
							item.setPrice(specialPrice);							
							item.setPriceWithDiscount(specialPrice);
							
						}		
						return item;
					}
				});
		if (specialProducts.size() > 0) {
			Product item = specialProducts.get(0);			
			product.setPrice(item.getPrice());
			product.setPriceWithDiscount(item.getPriceWithDiscount());
		}		
		return product;		
		
	}
	
	private ConditionResult createSQLQueryListProductsByConditions(ProductConditions productConditions) {
		ConditionResult conditionResult = new ConditionResult();
		
		String result = "SELECT * FROM sr_v_product_light p WHERE ";		

		boolean isDiscretCondition = false;
		if (StringUtils.isNotEmpty(productConditions.getName())) {			
			result += "(name like \"%" + TextUtils.escapingQuotes(productConditions.getName()) + "%\")";
			isDiscretCondition = true;
		}
		if (StringUtils.isNotEmpty(productConditions.getSku())) {
			if (isDiscretCondition) {
				result += " AND ";
			}
			result += "(sku like \"%" + productConditions.getSku() + "%\")";
			isDiscretCondition = true;
		}
		if (productConditions.getProductId() != 0) {
			if (isDiscretCondition) {
				result += " AND ";
			}
			result += "(product_id = " + productConditions.getProductId() + ")";
			isDiscretCondition = true;
		} 
		result += " ORDER BY product_id";
		conditionResult.setPeriodExist(false);
		conditionResult.setConditionText(result);		
		logger.debug("createSQLQueryListProductsByConditions: {}", result);
		return conditionResult;	
	}
	
	@Override
	public List<Product> listProductsByConditions(ProductConditions productConditions) {	
		logger.debug("listProductsByConditions(): {}", productConditions);
		/*
		if (productConditions.getProductId() <= 0 && (StringUtils.isEmpty(productConditions.getName()) || productConditions.getName().trim().length() < 3)) {
			return new ArrayList<Product>();
		}
		*/		
		ConditionResult conditionResult = createSQLQueryListProductsByConditions(productConditions);
		final String sqlSelectProducts = conditionResult.getConditionText();
		
		List<Product> products = this.jdbcTemplate.query(sqlSelectProducts,
				new Object[]{},				
				new RowMapper<Product>() {
					@Override
		            public Product mapRow(ResultSet rs, int rowNum) throws SQLException {
						Product product = new Product(rs.getInt("PRODUCT_ID"), TextUtils.removeHTMLShit(rs.getString("NAME")));
						product.setModel(TextUtils.removeHTMLShit(rs.getString("MODEL")));
						product.setSku(rs.getString("SKU"));
						product.setDeliveryName(rs.getString("DELIVERY_NAME"));
						product.setPrice(rs.getBigDecimal("PRICE"));
						product.setQuantity(rs.getInt("QUANTITY"));
							
						if (rs.getInt("STATUS") == 1) {
							product.setVisible(true);
						} else {
							product.setVisible(false);
						}						
						
						if (rs.getString("JAN").equals("0") || rs.getString("JAN").equals("102")) {
							product.setDeliveryMethod(PaymentDeliveryMethods.CURRENT);
						} else if (rs.getString("JAN").equals("101")) {
							product.setDeliveryMethod(PaymentDeliveryMethods.FULL);
						} else if (rs.getString("JAN").equals("103")) {
							product.setDeliveryMethod(PaymentDeliveryMethods.PVZ_FREE);
						}
						
						if (rs.getString("ISBN").equals("101")) {
							product.setType(ProductTypes.ADDITIONAL);
						} else {
							product.setType(ProductTypes.MAIN);
						}						
						
		                return product;
		            }
		        });
		return products;
	}
	
	private ConditionResult createSQLQueryListOzonProductsByConditions(ProductConditions productConditions) {
		ConditionResult conditionResult = new ConditionResult();
		/*

		 SELECT p.product_id, p.name, p.model, p.stock_quantity, p.product_quantity, p.product_price, p.supplier_price, p.supplier_id, p.sku sku, p.status p_status,
				p.sku shop_sku, ybo.market_sku, ybo.supplier_stock, ybo.marketplace_seller
				FROM sr_marketpace_offer ybo, sr_v_product p 
				WHERE (ybo.product_id = p.product_id)
                AND (marketplace_type = 5); 
		  
		  
		*/		
		String result = "SELECT p.product_id, p.name, p.model, p.stock_quantity, p.product_quantity, p.product_price, p.supplier_price, p.supplier_id, p.sku sku, p.status p_status, " 
				+ " p.sku shop_sku, ybo.market_sku, ybo.supplier_stock, ybo.marketplace_seller, ybo.special_price" 
				+ " FROM sr_marketpace_offer ybo, sr_v_product p " 
				+ " WHERE (ybo.product_id = p.product_id) "
                + " AND (marketplace_type = 5) " ; 
	
		if (productConditions.getOzonSellerExist() > -1) {
			result += " and (marketplace_seller = " + productConditions.getOzonSellerExist() + ")";			
		}
		if (productConditions.getSupplierStockExist() > -1) {
			result += " and (supplier_stock = " + productConditions.getSupplierStockExist() + ")";			
		}
		
		if (productConditions.getSuppliers() != null && productConditions.getSuppliers().size() > 0) {			
			result += " and (supplier_id in (" + productConditions.getIdsSuppliers() + "))";
		}
		result += " ORDER BY ybo.marketplace_seller desc, p.supplier_id, p.category_id, p.sku";
		conditionResult.setPeriodExist(false);
		conditionResult.setConditionText(result);		
		logger.debug("createSQLQueryListOzonProductsByConditions: {}", result);
		return conditionResult;	
		
	}
	
	private ConditionResult createSQLQueryListYmProductsByConditions(ProductConditions productConditions) {
		ConditionResult conditionResult = new ConditionResult();
		/*
		select p.product_id, p.name, p.model, p.stock_quantity, p.product_quantity, p.product_price, p.supplier_price, p.supplier_id, p.sku sku, p.status p_status, ybo.shopSku shop_sku, ybo.yandex_sku, ybo.status ybo_status, ybo.supplier_stock, ybo.yandex_seller 
		  from oc_yb_offers ybo, sr_v_product p
		  where ybo.product_id = p.product_id 
		  order by p.sku
		*/
		String result = "SELECT p.product_id, p.name, p.model, p.stock_quantity, p.product_quantity, p.product_price, p.supplier_price, p.supplier_id, p.sku sku, p.status p_status, "
				+ " ybo.shopSku shop_sku, ybo.yandex_sku, ybo.status ybo_status, ybo.supplier_stock, ybo.yandex_seller, ybo.special_price"
				+ " FROM oc_yb_offers ybo, sr_v_product p "
				+ " WHERE (ybo.product_id = p.product_id) ";
	
		if (productConditions.getYandexSellerExist() > -1) {
			result += " and (yandex_seller = " + productConditions.getYandexSellerExist() + ")";			
		}
		if (productConditions.getSupplierStockExist() > -1) {
			result += " and (supplier_stock = " + productConditions.getSupplierStockExist() + ")";			
		}
		
		if (productConditions.getSuppliers().size() > 0) {			
			result += " and (supplier_id in (" + productConditions.getIdsSuppliers() + "))";
		}
		result += " ORDER BY ybo.yandex_seller desc, p.supplier_id, p.category_id, p.sku";
		conditionResult.setPeriodExist(false);
		conditionResult.setConditionText(result);		
		logger.debug("createSQLQueryListYmProductsByConditions: {}", result);
		return conditionResult;	
	}
	
	@Override
	public List<Product> listYmProductsByConditions(ProductConditions productConditions) {	
		logger.debug("listYmProductsByConditions(): {}", productConditions);
		
		/*
		if (productConditions.getProductId() <= 0 && (StringUtils.isEmpty(productConditions.getName()) || productConditions.getName().trim().length() < 3)) {
			return new ArrayList<Product>();
		}
		*/		
		ConditionResult conditionResult = createSQLQueryListYmProductsByConditions(productConditions);
		final String sqlSelectProducts = conditionResult.getConditionText();
		
		List<Product> products = this.jdbcTemplate.query(sqlSelectProducts,
				new Object[]{},				
				new RowMapper<Product>() {
					@Override
		            public Product mapRow(ResultSet rs, int rowNum) throws SQLException {
						/*
						select p.product_id, p.name, p.model, p.stock_quantity, p.product_quantity, p.product_price, p.supplier_price, p.supplier_id, p.sku sku, p.status p_status, 
						       ybo.shopSku shop_sku, ybo.yandex_sku, ybo.status ybo_status, ybo.supplier_stock, ybo.yandex_seller 
						  from oc_yb_offers ybo, sr_v_product p
						  where ybo.product_id = p.product_id 
						  order by p.sku
						*/						
						Product product = new Product(rs.getInt("PRODUCT_ID"), TextUtils.removeHTMLShit(rs.getString("NAME")));
						product.setModel(TextUtils.removeHTMLShit(rs.getString("MODEL")));
						product.setSku(rs.getString("SHOP_SKU"));						
						product.setPrice(rs.getBigDecimal("PRODUCT_PRICE"));
						product.setPriceWithoutDiscount(rs.getBigDecimal("PRODUCT_PRICE"));
						
						product.setQuantity(rs.getInt("PRODUCT_QUANTITY"));						
						product.setStockQuantity(rs.getInt("STOCK_QUANTITY"));
							
						if (rs.getInt("P_STATUS") == 1) {
							product.setVisible(true);
						} else {
							product.setVisible(false);
						}
						
						product.getMarket(CrmTypes.YANDEX_MARKET).setMarketSku(rs.getString("YANDEX_SKU"));
						product.getMarket(CrmTypes.YANDEX_MARKET).setSpecialPrice(rs.getBigDecimal("SPECIAL_PRICE"));
						if (rs.getInt("SUPPLIER_STOCK") == 1) {
							product.getMarket(CrmTypes.YANDEX_MARKET).setSupplierStock(true);
						}
						if (rs.getInt("YANDEX_SELLER") == 1) {
							product.getMarket(CrmTypes.YANDEX_MARKET).setMarketSeller(true);
						}

						product = getDbProductByIdSetSpecialPrice(product);						
		                return product;
		            }
		        });
		
		return products;
	}	
	
	@Override
	public List<Product> listOzonProductsByConditions(ProductConditions productConditions) {	
		logger.debug("listOzonProductsByConditions(): {}", productConditions);
		
		ConditionResult conditionResult = createSQLQueryListOzonProductsByConditions(productConditions);
		final String sqlSelectProducts = conditionResult.getConditionText();
		
		List<Product> products = this.jdbcTemplate.query(sqlSelectProducts,
				new Object[]{},				
				new RowMapper<Product>() {
					@Override
		            public Product mapRow(ResultSet rs, int rowNum) throws SQLException {
						/*
						select p.product_id, p.name, p.model, p.stock_quantity, p.product_quantity, p.product_price, p.supplier_price, p.supplier_id, p.sku sku, p.status p_status, 
						       ybo.shopSku shop_sku, ybo.yandex_sku, ybo.status ybo_status, ybo.supplier_stock, ybo.yandex_seller 
						  from oc_yb_offers ybo, sr_v_product p
						  where ybo.product_id = p.product_id 
						  order by p.sku
						*/						
						Product product = new Product(rs.getInt("PRODUCT_ID"), TextUtils.removeHTMLShit(rs.getString("NAME")));
						product.setModel(TextUtils.removeHTMLShit(rs.getString("MODEL")));
						product.setSku(rs.getString("SHOP_SKU"));						
						product.setPrice(rs.getBigDecimal("PRODUCT_PRICE"));
						product.setPriceWithoutDiscount(rs.getBigDecimal("PRODUCT_PRICE"));
						
						product.setQuantity(rs.getInt("PRODUCT_QUANTITY"));						
						product.setStockQuantity(rs.getInt("STOCK_QUANTITY"));
							
						if (rs.getInt("P_STATUS") == 1) {
							product.setVisible(true);
						} else {
							product.setVisible(false);
						}
						
						product.getMarket(CrmTypes.OZON).setMarketSku(rs.getString("MARKET_SKU"));
						product.getMarket(CrmTypes.OZON).setSpecialPrice(rs.getBigDecimal("SPECIAL_PRICE"));
						
						if (rs.getInt("SUPPLIER_STOCK") == 1) {
							product.getMarket(CrmTypes.YANDEX_MARKET).setSupplierStock(true);
						}
						if (rs.getInt("MARKETPLACE_SELLER") == 1) {
							product.getMarket(CrmTypes.OZON).setMarketSeller(true);
						}

						product = getDbProductByIdSetSpecialPrice(product);						
		                return product;
		            }
		        });
		
		return products;
	}
		
	/**
	 * @param isProduct списать расход из oc_product
	 * @param isStock списать с нашего склада
	 * @param isSynchronize просто актуализировать остаток товаров у нас в бэке (например, загрузка лида из опенкарта)
	 */
	@Override
	public void updateDeltaQuantityProduct2(Product product, int deltaQuantity, boolean isProduct, boolean isStock, boolean isSynchronize) {
		
		final String sqlUpdateProductPrice = "UPDATE oc_product "
				+ "	SET quantity = quantity - ? "
				+ "	WHERE product_id = ?";
		final String sqlUpdateSupplierStockProduct = "UPDATE sr_stock s"
				+ "  SET quantity = quantity - ?"
				+ "  WHERE s.id = ?";
		
		
		if (isSynchronize) {
			Product dbProduct = getDbProductById(product.getId());
			int newWikiProductQuantity = dbProduct.getQuantity();
			if (newWikiProductQuantity <= 0) {
				newWikiProductQuantity = 0;
			}
			Product wikiProduct = getProductById(dbProduct.getId());
			wikiProduct.setQuantity(newWikiProductQuantity);			
			
		}
		
		if (isProduct) {
			SupplierStockProduct supplierStockProduct = supplierStockProductFindByProductId(product.getId());
			if (supplierStockProduct != null) {
				if (supplierStockProduct.getProduct().isComposite()) {
					// это комплект
					this.jdbcTemplate.update(sqlUpdateProductPrice,	new Object[] { 				
							deltaQuantity,				
							supplierStockProduct.getProduct().getId()});	
					
					Product dbProduct = getDbProductById(supplierStockProduct.getProduct().getId());
					int newWikiProductQuantity = dbProduct.getQuantity();
					if (newWikiProductQuantity <= 0) {
						newWikiProductQuantity = 0;
					}					
					Product wikiProduct = getProductById(dbProduct.getId());
					wikiProduct.setQuantity(newWikiProductQuantity);
										
					for (Product slave : supplierStockProduct.getProduct().getKitComponents()) {	
						SupplierStockProduct supplierStockSlaveProduct = supplierStockProductFindByProductId(slave.getId());
						if (supplierStockSlaveProduct == null) {
							break;
						}
						int slaveQuantity = supplierStockSlaveProduct.getProduct().getQuantity();
						int deltaSlaveQuantity = slave.getSlaveQuantity() * deltaQuantity;
						
						slaveQuantity = slaveQuantity - deltaSlaveQuantity;	
						
						this.jdbcTemplate.update(sqlUpdateProductPrice,	new Object[] { 				
								deltaSlaveQuantity,				
								slave.getId()});
						
									
						wikiProduct = getProductById(slave.getId());
						wikiProduct.setQuantity(slaveQuantity);
					}
					
				} else {
					// это штучный товар
					
					this.jdbcTemplate.update(sqlUpdateProductPrice,	new Object[] { 				
							deltaQuantity,				
							product.getId()});	
					Product dbProduct = getDbProductById(product.getId());
					int newWikiProductQuantity = dbProduct.getQuantity();
					if (newWikiProductQuantity <= 0) {
						newWikiProductQuantity = 0;
					}
					Product wikiProduct = getProductById(dbProduct.getId());
					wikiProduct.setQuantity(newWikiProductQuantity);
					
					
					
				}
					
			}
			
			
			
			
			
		}
		if (isStock) {
			
			SupplierStockProduct supplierStockProduct = supplierStockProductFindByProductId(product.getId());
			if (supplierStockProduct != null) {
				if (supplierStockProduct.getProduct().isComposite()) {
					// это комплект
					for (Product slave : supplierStockProduct.getProduct().getKitComponents()) {	
						SupplierStockProduct supplierStockSlaveProduct = supplierStockProductFindByProductId(slave.getId());
						if (supplierStockSlaveProduct == null) {
							break;
						}
						int slaveQuantity = supplierStockSlaveProduct.getProduct().getStockQuantity();
						int deltaSlaveQuantity = slave.getSlaveQuantity() * deltaQuantity;
						
						slaveQuantity = slaveQuantity - deltaSlaveQuantity;						
						this.jdbcTemplate.update(sqlUpdateSupplierStockProduct, new Object[] { 				
								deltaSlaveQuantity,
								supplierStockSlaveProduct.getId()});
																	
						Product wikiProduct = getProductById(slave.getId());
						wikiProduct.setStockQuantity(slaveQuantity);
					}
				} else {
					// это штучный товар
					int newWikiStockQuantity = supplierStockProduct.getProduct().getCompositeStockQuantity();			
					newWikiStockQuantity = newWikiStockQuantity - deltaQuantity;
					if (newWikiStockQuantity <= 0) {
						newWikiStockQuantity = 0;
					}
					this.jdbcTemplate.update(sqlUpdateSupplierStockProduct, new Object[] { 				
							deltaQuantity,
							supplierStockProduct.getId()});
					Product wikiProduct = getProductById(supplierStockProduct.getProduct().getId());
					wikiProduct.setStockQuantity(newWikiStockQuantity);
				}				
			}
		}
	}
	
	@Override
	public void updateQuantityProduct(int productId, int quantity) {
		
		final String sqlUpdateProductPrice = "UPDATE oc_product "
				+ "	SET quantity = ? "
				+ "	WHERE product_id = ?";
		
		this.jdbcTemplate.update(sqlUpdateProductPrice,	new Object[] { 				
					quantity,				
					productId});
		
		Product wikiProduct = getProductById(productId);
		wikiProduct.setQuantity(quantity);		
		
	}
	
	@Override
	public void updateQuantityProductsBySuplierStockProducts(List<Product> products) {
		
		for (Product item : products) {
			SupplierStockProduct supplierStockProduct = supplierStockProductFindByProductId(item.getId());
			
			int stockQuantity = 0;
			if (supplierStockProduct.getProduct().isComposite()) {
				// это комплект
				/*
				for (Product slave : supplierStockProduct.getProduct().getKitComponents()) {	
					SupplierStockProduct supplierStockSlaveProduct = supplierStockProductFindByProductId(slave.getId());
					if (supplierStockSlaveProduct == null) {
						break;
					}
					
					int slaveQuantity = supplierStockSlaveProduct.getProduct().getStockQuantity();
					int deltaSlaveQuantity = slave.getSlaveQuantity() * deltaQuantity;
					
					slaveQuantity = slaveQuantity - deltaSlaveQuantity;						
																					
					Product wikiProduct = getProductById(slave.getId());
					wikiProduct.setStockQuantity(slaveQuantity);
				}
				*/
			} else {
				stockQuantity = supplierStockProduct.getProduct().getStockQuantity();
				updateQuantityProduct(item.getId(), stockQuantity);				
			}			
		}
	}
	
	@Override
	public void updatePriceAndQuantityProduct(Product product) {
		logger.debug("updatePriceAndQuantityProduct(): {}", product);
		
		final String sqlUpdateProductPrice = "UPDATE oc_product "
				+ "	SET quantity = ?, "
				+ "		price = ?"
				+ "	WHERE product_id = ?";
				
		final String sqlDeleteProductSpecialPrice = "DELETE FROM oc_product_special WHERE product_id = ?";
		this.jdbcTemplate.update(sqlDeleteProductSpecialPrice, new Object[] { 				
				product.getId()});
		
		BigDecimal price = product.getPriceWithoutDiscount();
		if (price == null || price.equals(BigDecimal.ZERO)) {
			price = product.getPrice();
		}
		this.jdbcTemplate.update(sqlUpdateProductPrice,	new Object[] { 				
				product.getQuantity(),				
				price,
				product.getId()});
		Product wikiProduct = getProductById(product.getId());
		wikiProduct.setQuantity(product.getQuantity());
								
		if (product.getPriceWithDiscount() != null && product.getPriceWithDiscount().compareTo(BigDecimal.ZERO) > 0) {			
			final String sqlInsertProductSpecialPrice = "INSERT INTO oc_product_special "
					+ "(product_id, customer_group_id, priority, price, date_start, date_end) " 
					+ "values "
					+ "(?, 1, 0, ?, '0000-00-00', '0000-00-00');";
			
			BigDecimal priceWith = product.getPriceWithDiscount();
			if (priceWith == null || priceWith.equals(BigDecimal.ZERO)) {
				priceWith = product.getPriceWithDiscount();
			}
			this.jdbcTemplate.update(sqlInsertProductSpecialPrice, new Object[] { 				
					product.getId(), 
					priceWith});			
		}		
	}
	
	@Override
	public void updateYmOfferProduct(Product product) {
		
		final String sqlSelectYmOffer = "select shopsku SHOP_SKU from oc_yb_offers"
				+ " where product_id = ?";
		
		final String sqlInsertYmOffer = "INSERT INTO oc_yb_offers (product_id, shopSku, yandex_sku, supplier_stock, yandex_seller, special_price) VALUES (?, ?, ?, ?, ?, ?)";
		//final String sqlDeleteYmOffer = "DELETE FROM oc_yb_offers WHERE product_id = ?";
		final String sqlUpdateYmOffer = "update oc_yb_offers "
				+ "  set shopSku = ?, "
				+ "  yandex_sku = ?, "
				+ "  supplier_stock = ?, "
				+ "  yandex_seller = ?, "
				+ "  special_price = ? "
				+ "  where product_id = ?";
	
		List<String> productSkus = this.jdbcTemplate.query(sqlSelectYmOffer,
				new Object[]{product.getId()},				
				new RowMapper<String>() {
					@Override
		            public String mapRow(ResultSet rs, int rowNum) throws SQLException {
						return rs.getString("SHOP_SKU");
		            }
		        });	
		
		if ((productSkus == null || productSkus.size() == 0) && (!product.getMarket(CrmTypes.YANDEX_MARKET).isMarketSeller())) {
			// 1) записи нет, оператор флаг "на ЯМ" не поставил
			return;
		} 
		if ((productSkus == null || productSkus.size() == 0) && (product.getMarket(CrmTypes.YANDEX_MARKET).isMarketSeller())) {
			// 2) записи нет, а оператор установил флаг "на ЯМ"			
			this.jdbcTemplate.update(sqlInsertYmOffer, new Object[] { 				
						product.getId(),
						StringUtils.isEmpty(product.getSku()) ? product.getSku() : product.getSku().toUpperCase(),
						product.getMarket(CrmTypes.YANDEX_MARKET).getMarketSku(),
						product.getMarket(CrmTypes.YANDEX_MARKET).isSupplierStock() ? 1 : 0,
						product.getMarket(CrmTypes.YANDEX_MARKET).isMarketSeller() ? 1 : 0,
						product.getMarket(CrmTypes.YANDEX_MARKET).getSpecialPrice()});
			return;
		}	
		// запись есть, мы ее правим
		this.jdbcTemplate.update(sqlUpdateYmOffer, new Object[] {
					StringUtils.isEmpty(product.getSku()) ? product.getSku() : product.getSku().toUpperCase(),
					product.getMarket(CrmTypes.YANDEX_MARKET).getMarketSku(),
					product.getMarket(CrmTypes.YANDEX_MARKET).isSupplierStock() ? 1 : 0,
					product.getMarket(CrmTypes.YANDEX_MARKET).isMarketSeller() ? 1 : 0,
					product.getMarket(CrmTypes.YANDEX_MARKET).getSpecialPrice(),							
					product.getId()});			
	}
	
	@Override
	public void updateOzonOfferProduct(Product product) {
		
		final String sqlSelectOzonOffer = "select * from sr_marketpace_offer"
				+ " where product_id = ? and marketplace_type = ?";
		
		final String sqlInsertOzonOffer = "insert into sr_marketpace_offer (product_id, market_sku, supplier_stock, marketplace_seller, special_price, marketplace_type) values (?, ?, ?, ?, ?, ?)";

		final String sqlUpdateOzonOffer = "update sr_marketpace_offer "
				+ "  set market_sku = ?, "
				+ "      supplier_stock = ?, "
				+ "      marketplace_seller = ?, "
				+ "      special_price = ?"				
				+ "  where product_id = ? and marketplace_type = ?";
	
		List<String> productSkus = this.jdbcTemplate.query(sqlSelectOzonOffer,
				new Object[]{product.getId(), CrmTypes.OZON.getId()},				
				new RowMapper<String>() {
					@Override
		            public String mapRow(ResultSet rs, int rowNum) throws SQLException {
						return rs.getString("PRODUCT_ID");
		            }
		        });	
		
		if ((productSkus == null || productSkus.size() == 0) && (!product.getMarket(CrmTypes.OZON).isMarketSeller())) {
			// 1) записи нет, оператор флаг "на OZON" не поставил
			return;
		} 
		if ((productSkus == null || productSkus.size() == 0) && (product.getMarket(CrmTypes.OZON).isMarketSeller())) {
			// 2) записи нет, а оператор установил флаг "на OZON"			
			this.jdbcTemplate.update(sqlInsertOzonOffer, new Object[] { 				
						product.getId(),
						product.getMarket(CrmTypes.OZON).getMarketSku(),
						product.getMarket(CrmTypes.OZON).isSupplierStock() ? 1 : 0,
						product.getMarket(CrmTypes.OZON).isMarketSeller() ? 1 : 0,
						product.getMarket(CrmTypes.OZON).getSpecialPrice(),
						CrmTypes.OZON.getId()});
			return;
		}	
		// запись есть, мы ее правим
		this.jdbcTemplate.update(sqlUpdateOzonOffer, new Object[] {
					
					product.getMarket(CrmTypes.OZON).getMarketSku(),
					product.getMarket(CrmTypes.OZON).isSupplierStock() ? 1 : 0,
					product.getMarket(CrmTypes.OZON).isMarketSeller() ? 1 : 0,
					product.getMarket(CrmTypes.OZON).getSpecialPrice(),
					product.getId(),
					CrmTypes.OZON.getId()});			
	}
	
	@Override
	public void updateYmOfferMapping(List<Product> products) {
		final String sqlUpdateYmOffer = "update oc_yb_offers "
				+ "  set yandex_sku = ? "
				+ "  where product_id = ?";
		
		for (Product item : products) {
			if (StringUtils.isNoneEmpty(item.getMarket(CrmTypes.YANDEX_MARKET).getMarketSku())) {
				this.jdbcTemplate.update(sqlUpdateYmOffer, new Object[] {
						item.getMarket(CrmTypes.YANDEX_MARKET).getMarketSku(),
						item.getId()});
			}
		}
	}
	
	@Override
	public void updateProductDescriptionMeta(Product product) {
		logger.debug("updateProductDescriptionMeta():{}", product);
				
		final String sqlUpdateProduct = "UPDATE oc_product "
				+ "	SET sku = ?,"
				+ "     status = ?,"
				+ "     jan = ?,"
				+ "     isbn = ?,"
				+ "     category_group_id = ?,"
				+ "     delivery_name = ?"
				+ "	WHERE product_id = ?";

		this.jdbcTemplate.update(sqlUpdateProduct, new Object[] { 				
				StringUtils.isEmpty(product.getSku()) ? product.getSku() : product.getSku().toUpperCase(),	
				product.isVisible() ? 1 : 0,
						String.valueOf(product.getDeliveryMethod().getId()),
						String.valueOf(product.getType().getIsbn()),
						product.getCategory().getId(),
						product.getDeliveryName(),
				product.getId()});
		
		final String sqlUpdateProductDescription = "UPDATE oc_product_description "
				+ "  SET meta_title = ?,"
				+ "      meta_description = ?,"
				+ "      meta_keyword = ?"				
				+ "  WHERE product_id = ? and language_id = 2";
				
		this.jdbcTemplate.update(sqlUpdateProductDescription, new Object[] { 				
				product.getStore().getMetaTitle(),				
				product.getStore().getMetaDescription(),
				product.getStore().getMetaKeyword(),
				product.getId()});
	}
	
	@Override
	public Product createProductDescriptionMeta(Product product) {
		
		String formatDescriptionOne = "%s, цена %s руб. Товар в наличии. Гарантия: 12 месяцев. Официальный дилер производителя. Быстро доставим по России. Осмотр посылки и оплата на месте.";
		String formatDescriptionTwo = "%s, цена %s руб. Товар в наличии. Официальный дилер производителя. Быстро доставим по России. Оплата на месте.";
		
		String formatTitleOne = "Купить %s в интернет-магазине приборМАСТЕР";
		String formatTitleTwo = "Купить %s в приборМАСТЕР";
		String formatTitleThree = "%s в приборМАСТЕР";
		String formatTitleFour = "%s";
				
		DecimalFormat df = new DecimalFormat();
		df.setMaximumFractionDigits(2);
		df.setMinimumFractionDigits(0);
		df.setGroupingUsed(false);
		
		String stringPrice;
		String metaDescription;
		String metaTitle;		
		
		String name = product.getName().replace("&quot;", "");
		String model = product.getModel().replace("&quot;", "");
		
		stringPrice = df.format(product.getPrice()); 
		metaDescription = String.format(formatDescriptionOne, name, stringPrice);
		if (metaDescription.length() > 250) {
			metaDescription = String.format(formatDescriptionTwo, name, stringPrice);			
		}			
		metaTitle = String.format(formatTitleOne, name);
		if (metaTitle.length() > 70) {
			metaTitle = String.format(formatTitleTwo, name);			
		}
		if (metaTitle.length() > 70) {
			metaTitle = String.format(formatTitleThree, name);			
		}	
		if (metaTitle.length() > 70) {
			metaTitle = String.format(formatTitleFour, name);			
		}
		if (metaTitle.length() > 70) {
			metaTitle = String.format(formatTitleFour, model);			
		}
		if (metaTitle.length() < 40) {
			metaTitle = String.format(formatTitleFour, name);			
		}	
		
		if (metaTitle.length() < 40 || metaTitle.length() > 70) {
			logger.info("title({}): {}", metaTitle.length(), metaTitle);	
			logger.info("description({}): {}", metaDescription.length(), metaDescription);		
		}
		if (metaDescription.length() < 150 || metaDescription.length() > 250) {
			logger.info("description({}): {}", metaDescription.length(), metaDescription);				
		}	
		
		product.getStore().setMetaTitle(metaTitle);
		product.getStore().setMetaDescription(metaDescription);
				
		return product;		
		
		
	}
	
	@Override
	public Product createProductDescriptionMeta(int productId) {
		
		Product product = getDbProductById(productId);
		
		product = createProductDescriptionMeta(product);
		
		/*
		
		String formatDescriptionOne = "%s, цена %s руб. Товар в наличии. Гарантия: 12 месяцев. Официальный дилер производителя. Быстро доставим по России. Осмотр посылки и оплата на месте.";
		String formatDescriptionTwo = "%s, цена %s руб. Товар в наличии. Официальный дилер производителя. Быстро доставим по России. Оплата на месте.";
		
		String formatTitleOne = "Купить %s в интернет-магазине приборМАСТЕР";
		String formatTitleTwo = "Купить %s в приборМАСТЕР";
		String formatTitleThree = "%s в приборМАСТЕР";
		String formatTitleFour = "%s";
				
		DecimalFormat df = new DecimalFormat();
		df.setMaximumFractionDigits(2);
		df.setMinimumFractionDigits(0);
		df.setGroupingUsed(false);
		
		String stringPrice;
		String metaDescription;
		String metaTitle;		
		
		String name = product.getName().replace("&quot;", "");
		String model = product.getModel().replace("&quot;", "");
		
		stringPrice = df.format(product.getPrice()); 
		metaDescription = String.format(formatDescriptionOne, name, stringPrice);
		if (metaDescription.length() > 250) {
			metaDescription = String.format(formatDescriptionTwo, name, stringPrice);			
		}			
		metaTitle = String.format(formatTitleOne, name);
		if (metaTitle.length() > 70) {
			metaTitle = String.format(formatTitleTwo, name);			
		}
		if (metaTitle.length() > 70) {
			metaTitle = String.format(formatTitleThree, name);			
		}	
		if (metaTitle.length() > 70) {
			metaTitle = String.format(formatTitleFour, name);			
		}
		if (metaTitle.length() > 70) {
			metaTitle = String.format(formatTitleFour, model);			
		}
		if (metaTitle.length() < 40) {
			metaTitle = String.format(formatTitleFour, name);			
		}	
		
		if (metaTitle.length() < 40 || metaTitle.length() > 70) {
			logger.info("title({}): {}", metaTitle.length(), metaTitle);	
			logger.info("description({}): {}", metaDescription.length(), metaDescription);		
		}
		if (metaDescription.length() < 150 || metaDescription.length() > 250) {
			logger.info("description({}): {}", metaDescription.length(), metaDescription);				
		}	
		
		product.getStore().setMetaTitle(metaTitle);
		product.getStore().setMetaDescription(metaDescription);
				*/
		return product;		
	}

	// реализовано только для 1-го склада
	@Override
	public void updateSupplierStockPrice(Product product) {
		
		logger.debug("updateStockSupplierPrice():{}", product);
		
		if (product.getMainSupplier() == null) {
			return;
		}
		
		final String sqlSelectMaxIdStock = "SELECT MAX(id) id FROM sr_stock s "
				+ "  WHERE s.product_id = ? "
				+ "    AND s.stock_id = 1 AND s.supplier_id = ?";
		Integer stockId = this.jdbcTemplate.queryForObject(sqlSelectMaxIdStock,
		        new Object[]{
		        		product.getId(),
		        		product.getMainSupplier().getId()
		        		},
		        new RowMapper<Integer>() {			
					@Override
		            public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
		            	return Integer.valueOf(rs.getInt("ID"));	
		            }
		        });
		
		if (stockId > 0) {
			
			final String sqlUpdateStock = "UPDATE sr_stock s "
					+ "  SET supplier_price = ? "
					+ "  WHERE s.id = ?";
			
			this.jdbcTemplate.update(sqlUpdateStock, new Object[] { 				
					product.getSupplierPrice(),	
					stockId});
			
		} else {

			final String sqlInsertStock = "INSERT INTO sr_stock"
					+ " (product_id, stock_id, supplier_id, supplier_price, quantity)"
					+ " VALUES"
					+ " (?, ?, ?, ?, ?)";
			
			this.jdbcTemplate.update(sqlInsertStock, new Object[] {
					product.getId(),
					1,
					product.getMainSupplier().getId(),
					product.getSupplierPrice(),
					0});			
		}
	}	
	
	@Override
	public SupplierStock getSupplierStock(SupplierTypes supplier, ProductCategory productCategory) {
		
		String sqlSelectSupplierStockProducts = "SELECT * FROM sr_v_stock s "
				+ "  WHERE s.supplier_id = ? ";
		
		if (productCategory != null && productCategory.getId() > 0) {
			sqlSelectSupplierStockProducts += " AND s.category_group_id = " + productCategory.getId();			
		}
		sqlSelectSupplierStockProducts += "  ORDER BY s.category_group, s.category_annotation, s.product_name";
		
		List<SupplierStockProduct> supplierStockProducts = this.jdbcTemplate.query(sqlSelectSupplierStockProducts,
				new Object[]{supplier.getId()},
				new RowMapper<SupplierStockProduct>() {
					@Override
		            public SupplierStockProduct mapRow(ResultSet rs, int rowNum) throws SQLException {
		            	Product product = getProductById(rs.getInt("PRODUCT_ID"));
		            	product.setCategory(getCategoryById(rs.getInt("CATEGORY_GROUP_ID")));
		            	product.setSupplierPrice(rs.getBigDecimal("SUPPLIER_PRICE"));
		            	product.setStockQuantity(rs.getInt("QUANTITY"));
		            	
		            	//product.setPrice(rs.getBigDecimal("PRODUCT_PRICE"));
		            	//product.setQuantity(rs.getInt("PRODUCT_QUANTITY"));
		            			            	
		            	SupplierStockProduct supplierStockProduct = new SupplierStockProduct(product);
		            	supplierStockProduct.setId(rs.getInt("ID"));
		            	supplierStockProduct.setComment(rs.getString("COMMENT"));
		            	supplierStockProduct.setSupplier(SupplierTypes.getValueById(rs.getInt("SUPPLIER_ID")));
		                return supplierStockProduct;
		            }
		        });
		
		BigDecimal supplierStockAmmount = BigDecimal.ZERO;
		BigDecimal billStockAmmount = BigDecimal.ZERO;		
		for (SupplierStockProduct item : supplierStockProducts) {
			int quantity = item.getProduct().getStockQuantity();
			supplierStockAmmount = supplierStockAmmount.add(item.getProduct().getSupplierPrice().multiply(BigDecimal.valueOf(quantity)));
			billStockAmmount = billStockAmmount.add(item.getProduct().getPrice().multiply(BigDecimal.valueOf(quantity)));
		}
		
		SupplierStock stock = new SupplierStock(supplierStockProducts);
		stock.setAmount(OrderAmountTypes.SUPPLIER, supplierStockAmmount);
		stock.setAmount(OrderAmountTypes.BILL, billStockAmmount);
				
		final String sqlSelectAllSupplierStockProducts = "SELECT * FROM sr_v_stock s "
				+ "  ORDER BY s.category_group, s.category_annotation, s.product_id";
		
		List<SupplierStockProduct> allSupplierStockProducts = this.jdbcTemplate.query(sqlSelectAllSupplierStockProducts,
				new Object[]{},
				new RowMapper<SupplierStockProduct>() {
					@Override
		            public SupplierStockProduct mapRow(ResultSet rs, int rowNum) throws SQLException {
		            	Product product = getProductById(rs.getInt("PRODUCT_ID"));
		            	//product.setCategory(getCategoryById(rs.getInt("CATEGORY_GROUP_ID")));
		            	product.setSupplierPrice(rs.getBigDecimal("SUPPLIER_PRICE"));
		            	product.setStockQuantity(rs.getInt("QUANTITY"));
		            	
		            	//product.setPrice(rs.getBigDecimal("PRODUCT_PRICE"));
		            			            	
		            	SupplierStockProduct supplierStockProduct = new SupplierStockProduct(product);
		            	supplierStockProduct.setId(rs.getInt("ID"));

		                return supplierStockProduct;
		            }
		        });
		
		BigDecimal totalSupplierStockAmount = BigDecimal.ZERO;
		BigDecimal totalBillStockAmount = BigDecimal.ZERO;		
		for (SupplierStockProduct item : allSupplierStockProducts) {
			int quantity = item.getProduct().getStockQuantity();
			totalSupplierStockAmount = totalSupplierStockAmount.add(item.getProduct().getSupplierPrice().multiply(BigDecimal.valueOf(quantity)));
			totalBillStockAmount = totalBillStockAmount.add(item.getProduct().getPrice().multiply(BigDecimal.valueOf(quantity)));
		}
		stock.setAmount(OrderAmountTypes.TOTAL_SUPPLIER, totalSupplierStockAmount);
		stock.setAmount(OrderAmountTypes.TOTAL_BILL, totalBillStockAmount);
		return stock;			
	}
	
	@Override
	public SupplierStockProduct supplierStockProductFindById(int id) {
		final String sqlSelectSupplierStockProduct = "SELECT * FROM sr_v_stock s WHERE s.id = ? ";
		
		SupplierStockProduct supplierStockProduct = this.jdbcTemplate.queryForObject(sqlSelectSupplierStockProduct,
		        new Object[]{id},
		        new RowMapper<SupplierStockProduct>() {
					@Override
		            public SupplierStockProduct mapRow(ResultSet rs, int rowNum) throws SQLException {
						Product product = getProductById(rs.getInt("PRODUCT_ID"));
						product.setCategory(getCategoryById(rs.getInt("CATEGORY_GROUP_ID")));
		            	product.setSupplierPrice(rs.getBigDecimal("SUPPLIER_PRICE"));
		            	product.setStockQuantity(rs.getInt("QUANTITY"));
		            	
		            	product.setPrice(rs.getBigDecimal("PRODUCT_PRICE")); 
		            	// TODO SELECT * FROM p326995_mysirric.oc_product_special where product_id=761
		            	product.setQuantity(rs.getInt("PRODUCT_QUANTITY"));
						
		            	SupplierStockProduct supplierStockProduct = new SupplierStockProduct(product);
		            	supplierStockProduct.setId(rs.getInt("ID"));
		            	supplierStockProduct.setSupplier(SupplierTypes.getValueById(rs.getInt("SUPPLIER_ID")));
		            	supplierStockProduct.setComment(rs.getString("COMMENT"));
		                return supplierStockProduct;
		            }
		        });
		
		
		return supplierStockProduct;
	}
	
	@Override
	public SupplierStockProduct supplierStockProductFindByProductId(int productId) {
		final String sqlSelectSupplierStockProduct = "SELECT * FROM sr_v_stock s WHERE s.product_id = ? ";		
		
		List<SupplierStockProduct> supplierStockProducts = this.jdbcTemplate.query(sqlSelectSupplierStockProduct,
				new Object[]{productId},				
				new RowMapper<SupplierStockProduct>() {
					@Override
		            public SupplierStockProduct mapRow(ResultSet rs, int rowNum) throws SQLException {		                
						Product product = getProductById(rs.getInt("PRODUCT_ID"));
						product.setCategory(getCategoryById(rs.getInt("CATEGORY_GROUP_ID")));
		            	product.setSupplierPrice(rs.getBigDecimal("SUPPLIER_PRICE"));
		            	product.setStockQuantity(rs.getInt("QUANTITY"));
		            	
		            	product.setPrice(rs.getBigDecimal("PRODUCT_PRICE")); 
		            	// TODO SELECT * FROM p326995_mysirric.oc_product_special where product_id=761
		            	product.setQuantity(rs.getInt("PRODUCT_QUANTITY"));
						
		            	SupplierStockProduct supplierStockProduct = new SupplierStockProduct(product);
		            	supplierStockProduct.setId(rs.getInt("ID"));
		            	supplierStockProduct.setSupplier(SupplierTypes.getValueById(rs.getInt("SUPPLIER_ID")));
		                return supplierStockProduct;					
		            }
		        });
		if (supplierStockProducts.size() == 0) {
			return null;
		}
		return supplierStockProducts.get(0);		
	}
	
	@Override
	public int addSupplierStockProduct(SupplierStockProduct supplierStockProduct) {
		
		final String sqlInsertSupplierStockProduct = "INSERT into sr_stock "
				+ "	(product_id, stock_id, supplier_id, supplier_price, quantity, comment)"
				+ " VALUES"
				+ " (?, ?, ?, ?, ?, ?)";
		this.jdbcTemplate.update(sqlInsertSupplierStockProduct, new Object[] { 				
				supplierStockProduct.getProduct().getId(),
				1,
				supplierStockProduct.getSupplier().getId(),
				supplierStockProduct.getProduct().getSupplierPrice(),
				supplierStockProduct.getProduct().getStockQuantity(),
				supplierStockProduct.getComment()});
		
		final int result = getLastInsert();
				
		final String sqlUpdateProduct = "UPDATE oc_product "
				+ "	SET category_group_id = ?"
				+ "	WHERE product_id = ?";		
		this.jdbcTemplate.update(sqlUpdateProduct, new Object[] { 				
				supplierStockProduct.getProduct().getCategory().getId(),				
				supplierStockProduct.getProduct().getId()});
		
		Product wikiProduct = getProductById(supplierStockProduct.getProduct().getId());
		if (wikiProduct != null) {
			wikiProduct.setStockQuantity(supplierStockProduct.getProduct().getStockQuantity());
			wikiProduct.setQuantity(supplierStockProduct.getProduct().getQuantity());
		}
		return result;
	}	
	
	@Override
	public void updateSupplierStockProduct(SupplierStockProduct supplierStockProduct) {
		final String sqlUpdateSupplierStockProduct = "UPDATE sr_stock s"
				+ "  SET product_id = ?, "
				+ "      stock_id = ?,"
				+ "      supplier_id = ?,"
				+ "      supplier_price = ?, "
				+ "      quantity = ?, "
				+ "      comment = ? "
				+ "  WHERE s.id = ?";
		
		this.jdbcTemplate.update(sqlUpdateSupplierStockProduct, new Object[] { 				
				supplierStockProduct.getProduct().getId(),
				1,
				supplierStockProduct.getSupplier().getId(),
				supplierStockProduct.getProduct().getSupplierPrice(),
				supplierStockProduct.getProduct().getStockQuantity(),
				supplierStockProduct.getComment(),
				supplierStockProduct.getId()});
	
		BigDecimal price = supplierStockProduct.getProduct().getPriceWithoutDiscount();
		if (price == null || price.equals(BigDecimal.ZERO)) {
			price = supplierStockProduct.getProduct().getPrice();
		}
		
		if (supplierStockProduct.getProduct().getPriceWithoutDiscount() == null || 
				supplierStockProduct.getProduct().getPriceWithoutDiscount() == BigDecimal.ZERO || 
				supplierStockProduct.getProduct().getPrice().compareTo(supplierStockProduct.getProduct().getPriceWithDiscount()) == 1) {
			final String sqlUpdateProduct = "UPDATE oc_product "
					+ "	SET category_group_id = ?, "
					+ " quantity = ?, "
					+ " price = ?"
					+ "	WHERE product_id = ?";		
			this.jdbcTemplate.update(sqlUpdateProduct, new Object[] { 				
					supplierStockProduct.getProduct().getCategory().getId(),
					supplierStockProduct.getProduct().getQuantity(),
					supplierStockProduct.getProduct().getPrice(),
					supplierStockProduct.getProduct().getId()});
			Product wikiProduct = getProductById(supplierStockProduct.getProduct().getId());		
			wikiProduct.setPrice(supplierStockProduct.getProduct().getPrice());
		} else {
			final String sqlUpdateProduct = "UPDATE oc_product "
					+ "	SET category_group_id = ?, "
					+ " quantity = ?"
					+ "	WHERE product_id = ?";		
			this.jdbcTemplate.update(sqlUpdateProduct, new Object[] { 				
					supplierStockProduct.getProduct().getCategory().getId(),
					supplierStockProduct.getProduct().getQuantity(),
					supplierStockProduct.getProduct().getId()});
			// TODO специальный остаток с дисконтом
		}				
				
		Product wikiProduct = getProductById(supplierStockProduct.getProduct().getId());		
		wikiProduct.setStockQuantity(supplierStockProduct.getProduct().getStockQuantity());
		wikiProduct.setQuantity(supplierStockProduct.getProduct().getQuantity());
		wikiProduct.setSupplierPrice(supplierStockProduct.getProduct().getSupplierPrice());
	} 
	
	@Override
	public void deleteSupplierStockProduct(int supplierStockProductId) {
		final String sqlUpdateSupplierStockProduct = "DELETE FROM sr_stock WHERE id = ?";
		this.jdbcTemplate.update(sqlUpdateSupplierStockProduct, new Object[] {
				supplierStockProductId});	
	}
	
	@Override
	public void supplierStockProductSaveOrUpdate(SupplierStockProduct supplierStockProduct) {
		if (supplierStockProduct.isNew()) {
			addSupplierStockProduct(supplierStockProduct);
		} else {
			updateSupplierStockProduct(supplierStockProduct);			
		}
	}
	
	@Override
	public BigDecimal ejectTotalAmountsByConditions(OrderAmountTypes amountType, Pair<Date> period) {	
		
		final String sqlSelectTotalAmounts = "SELECT SUM(amount) SUM_AMOUNT FROM sr_period_total_amount pta"
				+ "  WHERE pta.amount_type = ? "
				+ "    AND pta.period_in between ? and ?";
		BigDecimal sumAmount = this.jdbcTemplate.queryForObject(sqlSelectTotalAmounts,
		        new Object[]{amountType.getId(), period.getStart(), period.getEnd()},
		        new RowMapper<BigDecimal>() {
					@Override
		            public BigDecimal mapRow(ResultSet rs, int rowNum) throws SQLException {
		            	return rs.getBigDecimal("SUM_AMOUNT");	
		            }
		        });
		return sumAmount == null ? BigDecimal.ZERO : sumAmount;
	}
	
	private List<ProductCategory> instanceCategories() {

		logger.debug("instanceCategories()");
			
		
		final String sqlSelectCategories = "SELECT * FROM sr_wiki_category_product where id > 0 ORDER BY annotation";
		List<ProductCategory> categories = this.jdbcTemplate.query(sqlSelectCategories,
				new RowMapper<ProductCategory>() {
					@Override
					public ProductCategory mapRow(ResultSet rs, int rowNum) throws SQLException {
						ProductCategory category = new ProductCategory(rs.getInt("ID"), rs.getString("ANNOTATION"));
						category.setGroup(rs.getString("TYPE_GROUP"));
						return category;
					}
				});
		
		ProductCategory category = new ProductCategory(0, "не определена");
		category.setGroup("прочие");
		categories.add(category);
		
		return categories;
	}
	
	private List<Product> instanceProducts() {
		
		final int COMPOSYTE_TYPE = 1;

		logger.debug("instanceProducts()");
		final String sqlSelectProducts = "SELECT p.* FROM sr_v_product p"			
				+ " ORDER BY p.product_id";

		List<Product> products = this.jdbcTemplate.query(sqlSelectProducts,
				new RowMapper<Product>() {
					@Override
					public Product mapRow(ResultSet rs, int rowNum) throws SQLException {
						Product item = new Product(rs.getInt("PRODUCT_ID"), TextUtils.removeHTMLShit(rs.getString("NAME")));
						item.setModel(TextUtils.removeHTMLShit(rs.getString("MODEL")));
						item.setSku(rs.getString("SKU"));
						item.setDeliveryName(rs.getString("DELIVERY_NAME"));
					
						item.setPrice(rs.getBigDecimal("PRODUCT_PRICE"));
						item.setQuantity(rs.getInt("PRODUCT_QUANTITY"));
						item.setCategory(getCategoryById(rs.getInt("CATEGORY_ID")));	
						item.setStockQuantity(rs.getInt("STOCK_QUANTITY"));					
						
						if (rs.getBigDecimal("SUPPLIER_PRICE") == null) {
							item.setSupplierPrice(BigDecimal.ZERO);
							item.setMainSupplier(null);
						} else {
							item.setSupplierPrice(rs.getBigDecimal("SUPPLIER_PRICE"));
							item.setMainSupplier(SupplierTypes.getValueById(rs.getInt("SUPPLIER_ID")));
						}
						if (rs.getBigDecimal("WEIGHT").compareTo(BigDecimal.ZERO) == 1) {
							if (rs.getInt("WEIGHT_CLASS_ID") == 1) {
								// kg
								item.getStore().setWeight(rs.getBigDecimal("WEIGHT"));
							} else {
								// g --> kg
								item.getStore().setWeight(rs.getBigDecimal("WEIGHT").divide(BigDecimal.valueOf(1000), 4, RoundingMode.HALF_UP));
							}
						} else {
							item.getStore().setWeight(BigDecimal.valueOf(0.5));
						}						
						item.getStore().setLength(rs.getInt("LENGTH") < 0 ? 10 : rs.getInt("LENGTH"));
						item.getStore().setWidth(rs.getInt("WIDTH") < 0 ? 10 : rs.getInt("WIDTH"));
						item.getStore().setHeight(rs.getInt("HEIGHT") < 0 ? 10 : rs.getInt("HEIGHT"));
						
						if (rs.getInt("STATUS") == 1) {
							item.setVisible(true);
						} else {
							item.setVisible(false);
						}
						if (rs.getString("JAN").equals("0") || rs.getString("JAN").equals("102")) {
							item.setDeliveryMethod(PaymentDeliveryMethods.CURRENT);
						} else if (rs.getString("JAN").equals("101")) {
							item.setDeliveryMethod(PaymentDeliveryMethods.FULL);
						} else if (rs.getString("JAN").equals("103")) {
							item.setDeliveryMethod(PaymentDeliveryMethods.PVZ_FREE);
						}
						
						if (rs.getString("ISBN").equals("101")) {
							item.setType(ProductTypes.ADDITIONAL);
						} else {
							item.setType(ProductTypes.MAIN);
						}	
						
						if (rs.getInt("COMPOSITE") == COMPOSYTE_TYPE) {
							item.setComposite(true);							
						}
						/*
						if (StringUtils.isNoneEmpty(rs.getString("SKU").trim())) {
							
							PreparedStatement pstmtOffer = null;
							Connection conn = null;
							try {
								Class.forName(DbConfig.JDBC_DRIVER);
								conn = DriverManager.getConnection(DbConfig.DB_PM_PRODUCTION_URL);
																					
								pstmtOffer = conn.prepareStatement(sqlSelectYmOffer);
								pstmtOffer.setInt(1,  item.getId());
								
								ResultSet rsOffer = pstmtOffer.executeQuery();
								while (rsOffer.next()) {
																		
									if (rsOffer.getInt("SUPPLIER_STOCK") == 1) {
										item.getYm().setSupplierStock(true);
									}
									if (rsOffer.getInt("YANDEX_SELLER") == 1) {
										item.getYm().setYandexSeller(true);
									}	
								}			
								
							} catch (SQLException se) {
								logger.error("SQLException: ", se);
							} catch (Exception e) {
								logger.error("Exception: ", e);
							} finally {			
								try {
									if (pstmtOffer != null) {
										conn.close();
									}
								} catch (SQLException se) {
									// nothing...				
								} 
								try {
									if (conn != null) {
										conn.close();
									}
								} catch (SQLException se) {
									logger.error("SQLException: ", se);				
								} 
							}							
						}		
						*/				
						return item;
					}
				});
		
		for (Product item : products) {
			if (item.isComposite()) {
				item.setKitComponents(compositeProductByType(item.getId(), COMPOSYTE_TYPE));
			}			
		}		
		return products;
	}
	
	private List<Product> instanceProductsYandexOffer() {
		
		logger.debug("instanceProductsYandexOffer()");
		final String sqlSelectSpecialPrices = "SELECT * FROM oc_yb_offers ybo"
				+ "  ORDER BY PRODUCT_ID";	
		List<Product> specialProducts = this.jdbcTemplate.query(sqlSelectSpecialPrices,
				new Object[]{},
				new RowMapper<Product>() {
					@Override
					public Product mapRow(ResultSet rs, int rowNum) throws SQLException {
						Product item = getProductById(rs.getInt("PRODUCT_ID"));
						if (item != null) {
							item.getMarket(CrmTypes.YANDEX_MARKET).setMarketSku("YANDEX_SKU");						
							if (rs.getInt("SUPPLIER_STOCK") == 1) {
								item.getMarket(CrmTypes.YANDEX_MARKET).setSupplierStock(true);
							}
							if (rs.getInt("YANDEX_SELLER") == 1) {
								item.getMarket(CrmTypes.YANDEX_MARKET).setMarketSeller(true);
							}	
						}					
						return item;
					}
				});
		return specialProducts;			
	}
	
	private void clearSession() {
		logger.debug("clearSession(): {}", DateTimeUtils.sysDate());	
		final String sqlDeleteSessions = "DELETE FROM oc_session WHERE expire < (sysdate() - INTERVAL 1 DAY)";
		this.jdbcTemplate.update(sqlDeleteSessions, new Object[] {});
	}
	
	private Set<Product> compositeProductByType(int productId, int slaveType) {	
			
		final String sqlSelectCompositeProducts = "SELECT * FROM sr_product_composite pc "
				+ "  WHERE pc.master_product_id = ? AND pc.slave_type = ?"
				+ "  ORDER BY pc.slave_product_id";
		List<Product> compositeProducts = this.jdbcTemplate.query(sqlSelectCompositeProducts,
				new Object[]{productId, slaveType},
				new RowMapper<Product>() {
					@Override
					public Product mapRow(ResultSet rs, int rowNum) throws SQLException {						
						Product slave = getDbProductById(rs.getInt("SLAVE_PRODUCT_ID"));
						slave.setSlaveQuantity(rs.getInt("SLAVE_QUANTITY"));
						return slave;
					}
				});		
		Set<Product> result = new HashSet<>(compositeProducts);		
		return result;
	}
	
	private List<Product> instanceProductsSpecialPrice() {
		
		logger.debug("setProductsSpecialPrice()");
		final String sqlSelectSpecialPrices = "SELECT * FROM oc_product_special ps"
				+ "  WHERE (((date_start is null or date_start < ?) and (date_end is null or date_end < ?))"
				+ "      OR ((date_start is null or date_start < ?) and (date_end > ?))"
				+ "      OR ((date_start < ?) and (date_end is null or date_end < ?)))"
				+ "  ORDER BY PRODUCT_ID";
		Date zeroDate;
		try {
			zeroDate = DateTimeUtils.defaultFormatStringToDate("01.01.0001");
		} catch (ParseException e) {
			zeroDate = null;
		}
		List<Product> specialProducts = this.jdbcTemplate.query(sqlSelectSpecialPrices,
				new Object[]{
						zeroDate, zeroDate, 
						zeroDate, DateTimeUtils.sysDate(),
						DateTimeUtils.sysDate(), zeroDate 
						},
				new RowMapper<Product>() {
					@Override
					public Product mapRow(ResultSet rs, int rowNum) throws SQLException {
						Product item = getProductById(rs.getInt("PRODUCT_ID")); 
						BigDecimal specialPrice = rs.getBigDecimal("PRICE");
				
						if (specialPrice != null && specialPrice != BigDecimal.ZERO && item != null) {
							logger.debug("setProductsSpecialPrice():{},{},{}", item.getId(), item.getName(), specialPrice);
							
							item.setPriceWithoutDiscount(item.getPrice());
							item.setPriceWithDiscount(specialPrice);							
							item.setPrice(specialPrice);
						}		
						return item;
					}
				});
		return specialProducts;
	}
		
	@Override
	public List<Product> findProductsByName(String contextString) {		
		final int MAX_COUNT = 20;
		return findProductsByNames(contextString, MAX_COUNT);		
	}
	
	@Override
	public Product findSingleProductByName(String contextString) {
		List<Product> products = findProductsByNames(contextString, 1);
		if (products == null || products.size() == 0) {
			return null;
		}
		return products.get(0);
	}
	
	@Override
	public String findCdekPvzByAddress(Address address) {
		
		String city = TextUtils.cutCityFromAddress(address.getAddress());
		String streetAddress = TextUtils.cutStreetFromAddress(address.getAddress());
		if (StringUtils.isEmpty(city)) {
			return "";
		}		
		logger.debug("findCodeCdekPvzByAddress()");		
		final String sqlSelectSingleCityCdekPvz = "SELECT COUNT(code) count, MIN(code) single_code FROM sr_wiki_cdek_pvz w"
				+ "  WHERE w.city = ?";
		
		ProductCategory pvz = this.jdbcTemplate.queryForObject(sqlSelectSingleCityCdekPvz,
		        new Object[]{city},
		        new RowMapper<ProductCategory>() {
					@Override
		            public ProductCategory mapRow(ResultSet rs, int rowNum) throws SQLException {
						ProductCategory result = new ProductCategory(rs.getInt("COUNT"), rs.getString("SINGLE_CODE")); 
		            	return result;	
		            }
		        });
		
		if (pvz.getId() == 1) {
			return pvz.getName();
		}
		final String sqlSelectCdekPvz = "SELECT * FROM sr_wiki_cdek_pvz w"
				+ "  WHERE w.city = ? AND w.address like ?";
		List<String> pvzs = this.jdbcTemplate.query(sqlSelectCdekPvz,
				new Object[]{city, streetAddress},
				new RowMapper<String>() {
					@Override
					public String mapRow(ResultSet rs, int rowNum) throws SQLException {
						return rs.getString("CODE"); 
					}
				});
		if (pvzs.size() >= 1) {
			return pvzs.get(0);
		} else {
			return "";	
		}
	}
	
	private List<Product> findProductsByNames(String contextString, int maxCount) {		
		
		if (products == null) {
			return null;
		}		
		List<Product> fullResults = new ArrayList<Product>(); 	
		
		if (StringUtils.isEmpty(contextString) || StringUtils.isEmpty(contextString.trim()) || contextString.trim().length() < 3) {
			return fullResults;
		}
		//String[] contextBullets = {contextString, contextString.replace("-", " "), contextString.replace("-", "")};
		String[] contextBullets = {contextString};
		for (String contextBullet : contextBullets) {
			fullResults = findProductsByContext(fullResults, contextBullet);			
		}		
		
		int i = 0;
		List<Product> results = new ArrayList<Product>();
		for (Product resultProduct : fullResults) {
			results.add(resultProduct);
			if (i >= maxCount) {
				break;
			}			
			i++;			
		}
		
		Collections.sort(results, new Comparator<Product>() {
		    @Override
		    public int compare(Product lhs, Product rhs) {
		    	return lhs.getName().compareTo(rhs.getName());

		    }
		});
		
		return results;
	}
	
	@Override
	public Product findProductBySku(String sku) {
		for (Product product : getProducts()) {
			if (StringUtils.equalsIgnoreCase(product.getSku(), sku)) {
				return product;
			}
		}
		return null;
	}
	
	private List<Product> findProductsByContext(List<Product> findedProducts, String contextString) {	
		List<Product> result = findedProducts;
		for (Product product : products) {
			if (product.getType() == ProductTypes.MAIN && !product.isVisible()) {
				continue;
			}			
			if (StringUtils.indexOfIgnoreCase(product.getName(), contextString) >= 0 
					|| StringUtils.indexOfIgnoreCase(product.getModel(), contextString) >= 0 
					|| StringUtils.indexOfIgnoreCase(product.getSku(), contextString) >= 0) {
				result.add(product);
			}			
		}
		return result;		
	}	
}
