package ru.sir.richard.boss.dao;

import lombok.Data;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;

import ru.sir.richard.boss.model.data.*;
import ru.sir.richard.boss.model.data.conditions.ConditionResult;
import ru.sir.richard.boss.model.data.conditions.ProductConditions;
import ru.sir.richard.boss.model.types.*;
import ru.sir.richard.boss.model.utils.DateTimeUtils;
import ru.sir.richard.boss.model.utils.Pair;
import ru.sir.richard.boss.model.utils.TextUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.*;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.*;

@Component
@Scope("singleton")
@Slf4j
public class WikiDao extends AnyDaoImpl {
	
	private final int COMPOSYTE_TYPE = 1;

	private List<ProductCategory> categories = new ArrayList<>();
	private List<Product> products = new ArrayList<>();

	public void init(boolean isSheduller) {
		
		if (isSheduller && this.products.size() > 0) {
			return;
		}
		this.categories = instanceCategories();
		this.products = instanceProducts();
		instanceProductsSpecialPrice();
		instanceProductsYandexOffer();
		instanceProductsOzonOffer();		
		log.debug("products.size(): {}", products.size());
		
		//products.forEach(item -> logger.debug("product():{},{},{}", item.getId(), item.getName(), item.getPrice()));
		/*
		for (Product item : products) {
			logger.debug("product():{},{},{}", item.getId(), item.getName(), item.getPrice());			
		}
		*/	
		clearSession();
		log.debug("WikiDaoImpl.init() done");
	}

	public List<ProductCategory> getCategories() {
		return categories;
	}	
	
	public List<Product> getProducts() {
		return products;
	}

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
	
	private Product createProduct4RowMapperMapRow(ResultSet rs, int rowNum, boolean isSingle) throws SQLException {
				
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
			product.setMainSupplier(null);
		} else {
			product.setSupplierPrice(rs.getBigDecimal("SUPPLIER_PRICE"));	
			product.setMainSupplier(SupplierTypes.getValueById(rs.getInt("SUPPLIER_ID")));
		}		
		product.setSupplierQuantity(rs.getInt("SUPPLIER_QUANTITY"));		
		
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
		
		if (isSingle && StringUtils.isNoneEmpty(rs.getString("SKU").trim())) {	
			
			
			final String sqlSelectYmOffer = "SELECT * FROM sr_marketpace_offer WHERE marketplace_type = ? and product_id = ?";
			/*
			final String sqlSelectYmOffer2 = "SELECT shopsku shop_sku, supplier_stock, yandex_seller, yandex_sku, special_price"
					+ " FROM oc_yb_offers WHERE product_id = ? ";
			*/
			final String sqlSelectOzonOffer = "SELECT SUPPLIER_STOCK, MARKETPLACE_SELLER, market_sku, special_price"
					+ " FROM sr_marketpace_offer WHERE product_id = ? AND marketplace_type = 5";

			JdbcTemplate jdbcTemplateOffer = new JdbcTemplate(dataSource);														
			jdbcTemplateOffer.query(sqlSelectYmOffer,
					new Object[] { CrmTypes.YANDEX_MARKET.getId(), product.getId() },
					new int[] { Types.INTEGER, Types.INTEGER },
					new RowMapper<Product>() {
						@Override
			            public Product mapRow(ResultSet rs, int rowNum) throws SQLException {
							Product pOffer = new Product(product.getId(), product.getName());							
	
							product.getMarket(CrmTypes.YANDEX_MARKET).setMarketSku(rs.getString("MARKET_SKU"));																									
							if (rs.getInt("SUPPLIER_STOCK") == 1) {
								product.getMarket(CrmTypes.YANDEX_MARKET).setSupplierStock(true);
							}
							if (rs.getInt("MARKETPLACE_SELLER") == 1) {
								product.getMarket(CrmTypes.YANDEX_MARKET).setMarketSeller(true);
							}
							product.getMarket(CrmTypes.YANDEX_MARKET).setSpecialPrice(rs.getBigDecimal("SPECIAL_PRICE"));
			                	                
			                return pOffer;
			            }
			        });	
						
			jdbcTemplateOffer = new JdbcTemplate(dataSource);														
			jdbcTemplateOffer.query(sqlSelectOzonOffer,
					new Object[] { product.getId() },
					new int[] { Types.INTEGER },
					new RowMapper<Product>() {
						@Override
			            public Product mapRow(ResultSet rs, int rowNum) throws SQLException {
							Product pOffer = new Product(product.getId(), product.getName());
							
							product.getMarket(CrmTypes.OZON).setMarketSku(rs.getString("MARKET_SKU"));											
							if (rs.getInt("SUPPLIER_STOCK") == 1) {
								product.getMarket(CrmTypes.OZON).setSupplierStock(true);
							}
							if (rs.getInt("MARKETPLACE_SELLER") == 1) {
								product.getMarket(CrmTypes.OZON).setMarketSeller(true);
							}	
							product.getMarket(CrmTypes.OZON).setSpecialPrice(rs.getBigDecimal("SPECIAL_PRICE"));
							
			                return pOffer;
			            }
			        });			
		}	
		if (rs.getInt("COMPOSITE") == COMPOSYTE_TYPE) {
			product.setComposite(true);							
		}
        return product;
		
	}
	
	public Product getDbProductById(int productId) {	
		
		final String sqlSelectProduct = "SELECT p.* FROM sr_v_product p WHERE p.product_id = ?";
		final String sqlSelectProductCount = "SELECT count(*) count_id FROM sr_v_product p WHERE p.product_id = ?";
				
		int countProduct = this.jdbcTemplate.queryForObject(sqlSelectProductCount,
		        new Object[]{ productId },
		        new int[] { Types.INTEGER },
		        new RowMapper<Integer>() {
					@Override
		            public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
		            	return rs.getInt("COUNT_ID");	
		            }
		        });
		       
		if (countProduct == 0) {
			log.error("countProduct == 0: {}", productId);
			return null;
		}		
		
		Product product = this.jdbcTemplate.queryForObject(sqlSelectProduct,
		        new Object[]{ productId },
		        new int[] { Types.INTEGER },
		        new RowMapper<Product>() {
					@Override
		            public Product mapRow(ResultSet rs, int rowNum) throws SQLException {
						
						return createProduct4RowMapperMapRow(rs, rowNum, true);
						
		            }
		        });
		
		if (product.isComposite()) {
			product.setKitComponents(compositeProductByType(productId, COMPOSYTE_TYPE));
		}
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
		JdbcTemplate jdbcTemplateSlave = new JdbcTemplate(dataSource);
		List<Product> specialProducts = jdbcTemplateSlave.query(sqlSelectSpecialPrices,
				new Object[]{
						product.getId(),
						zeroDate, zeroDate, 
						zeroDate, DateTimeUtils.sysDate(),
						DateTimeUtils.sysDate(), zeroDate 
						},
				new int[] { Types.INTEGER, Types.DATE, Types.DATE, Types.DATE, Types.DATE, Types.DATE, Types.DATE }, 
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
		
		String result = "SELECT p.price product_price, p.quantity product_quantity, 0 stock_quantity, 0 supplier_price, 0 supplier_id, 0 supplier_quantity, 0 category_id, p.* "
				+ "FROM sr_v_product_light p WHERE ";
		
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
		log.debug("createSQLQueryListProductsByConditions: {}", result);
		return conditionResult;	
	}
	
	public List<Product> listProductsByConditions(ProductConditions productConditions) {
		log.debug("listProductsByConditions(): {}", productConditions);

		ConditionResult conditionResult = createSQLQueryListProductsByConditions(productConditions);
		final String sqlSelectProducts = conditionResult.getConditionText();
		
		List<Product> products = this.jdbcTemplate.query(sqlSelectProducts,
				new Object[] { },	
				new int[] { },
				new RowMapper<Product>() {
					@Override
		            public Product mapRow(ResultSet rs, int rowNum) throws SQLException {						
						return createProduct4RowMapperMapRow(rs, rowNum, false);
		            }
		        });
		return products;
	}
	
	private ConditionResult createSQLQueryListOzonProductsByConditions(ProductConditions productConditions) {
		ConditionResult conditionResult = new ConditionResult();
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
		log.debug("createSQLQueryListOzonProductsByConditions: {}", result);
		return conditionResult;	
		
	}
	
	private ConditionResult createSQLQueryListYmProductsByConditions(ProductConditions productConditions) {
		ConditionResult conditionResult = new ConditionResult();
		String result = "SELECT p.product_id, p.name, p.model, p.stock_quantity, p.product_quantity, p.product_price, p.supplier_price, p.supplier_id, p.sku sku, p.status p_status, "
				+ " p.sku, ybo.market_sku, ybo.supplier_stock, ybo.marketplace_seller, ybo.special_price"
				+ " FROM sr_marketpace_offer ybo, sr_v_product p "
				+ " WHERE (ybo.marketplace_type = 4) and (ybo.product_id = p.product_id) ";
	
		if (productConditions.getYandexSellerExist() > -1) {
			result += " and (marketplace_seller = " + productConditions.getYandexSellerExist() + ")";			
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
		log.debug("createSQLQueryListYmProductsByConditions: {}", result);
		return conditionResult;	
	}
	
	public List<Product> listYmProductsByConditions(ProductConditions productConditions) {	
		log.debug("listYmProductsByConditions(): {}", productConditions);
	
		ConditionResult conditionResult = createSQLQueryListYmProductsByConditions(productConditions);
		final String sqlSelectProducts = conditionResult.getConditionText();
		
		List<Product> products = this.jdbcTemplate.query(sqlSelectProducts,
				new Object[] { },
				new int[] { },
				new RowMapper<Product>() {
					@Override
		            public Product mapRow(ResultSet rs, int rowNum) throws SQLException {
								
						Product product = new Product(rs.getInt("PRODUCT_ID"), TextUtils.removeHTMLShit(rs.getString("NAME")));
						product.setModel(TextUtils.removeHTMLShit(rs.getString("MODEL")));
						product.setSku(rs.getString("SKU"));						
						product.setPrice(rs.getBigDecimal("PRODUCT_PRICE"));
						product.setPriceWithoutDiscount(rs.getBigDecimal("PRODUCT_PRICE"));
						
						product.setQuantity(rs.getInt("PRODUCT_QUANTITY"));						
						product.setStockQuantity(rs.getInt("STOCK_QUANTITY"));

						if (rs.getInt("P_STATUS") == 1) {
							product.setVisible(true);
						} else {
							product.setVisible(false);
						}
						
						product.getMarket(CrmTypes.YANDEX_MARKET).setMarketSku(rs.getString("MARKET_SKU"));
						product.getMarket(CrmTypes.YANDEX_MARKET).setSpecialPrice(rs.getBigDecimal("SPECIAL_PRICE"));
						if (rs.getInt("SUPPLIER_STOCK") == 1) {
							product.getMarket(CrmTypes.YANDEX_MARKET).setSupplierStock(true);
						}
						if (rs.getInt("MARKETPLACE_SELLER") == 1) {
							product.getMarket(CrmTypes.YANDEX_MARKET).setMarketSeller(true);
						}

						product = getDbProductByIdSetSpecialPrice(product);						
		                return product;
		            }
		        });
		
		return products;
	}	
	
	public List<Product> listOzonProductsByConditions(ProductConditions productConditions) {	
		log.debug("listOzonProductsByConditions(): {}", productConditions);
		
		ConditionResult conditionResult = createSQLQueryListOzonProductsByConditions(productConditions);
		final String sqlSelectProducts = conditionResult.getConditionText();
		
		List<Product> products = this.jdbcTemplate.query(sqlSelectProducts,
				new Object[] { },
				new int[] { },
				new RowMapper<Product>() {
					@Override
		            public Product mapRow(ResultSet rs, int rowNum) throws SQLException {
					
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
	
	private Result4UpdateProductStock checkUpdateProductStockByPhase(Product product, CrmTypes crmType, OrderStatuses phase) {
		Result4UpdateProductStock result4UpdateProductStock = new Result4UpdateProductStock();
		
		SupplierStockProduct supplierStockProduct = supplierStockProductFindByProductId(product.getId());
		
		if (supplierStockProduct.getProduct().isComposite()) {
			
			if ((crmType == CrmTypes.YANDEX_MARKET || crmType == CrmTypes.OPENCART) && phase == OrderStatuses.BID)   {
				result4UpdateProductStock.setSlaveFront(true);				
				
			} else if ((crmType == CrmTypes.YANDEX_MARKET || crmType == CrmTypes.OPENCART) && phase == OrderStatuses.APPROVED)   {
				result4UpdateProductStock.setSlaveBack(true);
				
			} else if ((crmType.isSimple() || crmType == CrmTypes.OZON) && phase == OrderStatuses.APPROVED)   {
				result4UpdateProductStock.setProductFront(true);
				result4UpdateProductStock.setSlaveFront(true);
				result4UpdateProductStock.setSlaveBack(true);
			}			
			
		} else {
			
			if ((crmType == CrmTypes.YANDEX_MARKET || crmType == CrmTypes.OPENCART) && phase == OrderStatuses.APPROVED)   {
				result4UpdateProductStock.setProductBack(true);
				
			} else if ((crmType.isSimple() || crmType == CrmTypes.OZON) && phase == OrderStatuses.APPROVED) {
				result4UpdateProductStock.setProductFront(true);
				result4UpdateProductStock.setProductBack(true);				
			}				
		}		
		return result4UpdateProductStock;		
	}
	
	public void updateDbProductPrice(int productId, BigDecimal price) {
		final String sqlUpdateProductPrice = "UPDATE oc_product"
				+ "	SET price = ?"
				+ "	WHERE product_id = ?";		
		JdbcTemplate jdbcTemplateUpdate = new JdbcTemplate(dataSource);
		jdbcTemplateUpdate.update(sqlUpdateProductPrice, new Object[] { price, productId });		
	}
	
	public void updateDbProductQuantity(int productId, int quantity) {
		final String sqlUpdateProductPrice = "UPDATE oc_product"
				+ "	SET quantity = ?"
				+ "	WHERE product_id = ?";		
		JdbcTemplate jdbcTemplateUpdate = new JdbcTemplate(dataSource);
		jdbcTemplateUpdate.update(sqlUpdateProductPrice, new Object[] { quantity, productId });		
	}
	
	public void updateDbProductQuantityByDelta(int productId, int deltaQuantity) {
		final String sqlUpdateProductPrice = "UPDATE oc_product"
				+ "	SET quantity = quantity - ?"
				+ "	WHERE product_id = ?";		
		JdbcTemplate jdbcTemplateUpdate = new JdbcTemplate(dataSource);
		jdbcTemplateUpdate.update(sqlUpdateProductPrice, new Object[] { deltaQuantity, productId });		
	}
	
	public void updateDbProductStock(int supplierProductId, int deltaQuantity) {
		
		final String sqlUpdateSupplierStockProduct = "UPDATE sr_stock s"
				+ " SET quantity = quantity - ?"
				+ " WHERE s.id = ?";			
		JdbcTemplate jdbcTemplateUpdate = new JdbcTemplate(dataSource);
		jdbcTemplateUpdate.update(sqlUpdateSupplierStockProduct, new Object[] { deltaQuantity, supplierProductId });
		
	}
	
	private void recalculateCompositesAfterSlavesExecute(Product masterProduct) {
		
		final String sqlSelectComposites = "select master_product_id from sr_product_composite pc, sr_v_product p \r\n"
				+ "  where slave_product_id in (select slave_product_id from sr_product_composite where master_product_id = ?) \r\n"
				+ "    and pc.master_product_id = p.product_id\r\n"
				+ "    and p.status = 1\r\n"
				+ "  group by master_product_id";
		
		
		JdbcTemplate jdbcTemplateComposites = new JdbcTemplate(dataSource);
		List<Product> compositeProducts = jdbcTemplateComposites.query(sqlSelectComposites,
				new Object[] { masterProduct.getId() },
				new int[] { Types.INTEGER },
				new RowMapper<Product>() {
					@Override
		            public Product mapRow(ResultSet rs, int rowNum) throws SQLException {								
						Product product = getProductById(rs.getInt("master_product_id"));											
		                return product;
		            }
		        });
		        
		for (Product composite : compositeProducts) {
			Set<Integer> compositeSlavesQuantities = new HashSet<Integer>();
			for (Product slave : composite.getKitComponents()) {
				Product realSlave = getProductById(slave.getId());
				
				int deltaSlaveQuantity = slave.getSlaveQuantity(); 
				int deltaQuantity = realSlave.getQuantity(); 
				compositeSlavesQuantities.add(deltaQuantity / deltaSlaveQuantity); 
			}			
			int quantityCompositeValue = Collections.min(compositeSlavesQuantities);			
			updateDbProductQuantity(composite.getId(), quantityCompositeValue);
			Product wikiProduct = getProductById(composite.getId());
			wikiProduct.setQuantity(quantityCompositeValue);			
		}
	}
	
	/**
	 * Обновление остатков товаров на витрине и беке по флагам из result4UpdateProductStock
	 * @param product
	 * @param deltaQuantity
	 * @param result4UpdateProductStock
	 */
	private void updateDeltaQuantityProduct(Product product, int deltaQuantity, Result4UpdateProductStock result4UpdateProductStock) {

		SupplierStockProduct supplierStockProduct = supplierStockProductFindByProductId(product.getId());
		
		if (supplierStockProduct != null) {
						
			if (result4UpdateProductStock.isProductFront()) {
				int newWikiQuantity = supplierStockProduct.getProduct().getQuantity();			
				newWikiQuantity = newWikiQuantity - deltaQuantity;
								
				updateDbProductQuantityByDelta(supplierStockProduct.getProduct().getId(), deltaQuantity);	
				
				Product wikiProduct = getProductById(supplierStockProduct.getProduct().getId());
				wikiProduct.setQuantity(newWikiQuantity);
			}
			if (result4UpdateProductStock.isProductBack()) {
				int newWikiStockQuantity = supplierStockProduct.getProduct().getStockQuantity();			
				newWikiStockQuantity = newWikiStockQuantity - deltaQuantity;
							
				updateDbProductStock(supplierStockProduct.getId(), deltaQuantity);
							
				Product wikiProduct = getProductById(supplierStockProduct.getProduct().getId());
				wikiProduct.setStockQuantity(newWikiStockQuantity);
			}

			if (supplierStockProduct.getProduct().isComposite()) {								
				// это комплект. списываем компоненты slavы
				for (Product slave : supplierStockProduct.getProduct().getKitComponents()) {	
					SupplierStockProduct supplierStockSlaveProduct = supplierStockProductFindByProductId(slave.getId());
					
					if (supplierStockSlaveProduct == null) {
						break;
					}									
					if (result4UpdateProductStock.isSlaveBack()) {
						
						int slaveQuantity = slave.getStockQuantity();
						int deltaSlaveQuantity = slave.getSlaveQuantity() * deltaQuantity;
						
						slaveQuantity = slaveQuantity - deltaSlaveQuantity;							
						
						updateDbProductStock(supplierStockSlaveProduct.getId(), deltaSlaveQuantity);
												
						Product wikiProduct = getProductById(slave.getId());
						wikiProduct.setStockQuantity(slaveQuantity);
												
					}
					if (result4UpdateProductStock.isSlaveFront()) {
						int slaveQuantity = slave.getQuantity();
						int deltaSlaveQuantity = slave.getSlaveQuantity() * deltaQuantity;
						
						slaveQuantity = slaveQuantity - deltaSlaveQuantity;	
												
						updateDbProductQuantityByDelta(slave.getId(), deltaSlaveQuantity);
						Product wikiProduct = getProductById(slave.getId());
						wikiProduct.setQuantity(slaveQuantity);
					}
				}
				// перебрать все комплекты (где встречаются наши слейвы) и актуализировать на фронте - после списания всех слейвов
				if (result4UpdateProductStock.isSlaveFront()) {					
					recalculateCompositesAfterSlavesExecute(supplierStockProduct.getProduct());					
				}
			}			
		}		
	}	
	
	/**
	 * Обновляет остатки продуктов (на витрине и на нашем складе) после прохода заказа через фазы (заявка - подтверждено)
	 * @param product
	 * @param deltaQuantity
	 * @param crmType [CrmTypes.YANDEX_MARKET, CrmTypes.OZON, обычный лид]
	 * @param phase [заявка маркета, OrderStatuses.BID, OrderStatuses.APPROVED]
	 */
	public void updateDeltaQuantityProduct(Product product, 
			int deltaQuantity, 
			CrmTypes crmType, 
			OrderStatuses phase) {
		
		Result4UpdateProductStock result4UpdateProductStock = checkUpdateProductStockByPhase(product, crmType, phase);		
		updateDeltaQuantityProduct(product, deltaQuantity, result4UpdateProductStock);
	}
	
	public void updatePriceAndQuantityProduct(Product product) {
		log.debug("updatePriceAndQuantityProduct(): {}", product);
		
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
	
	public List<Product> updateCheaterProductsRollback(CrmTypes crmTypes) {
		
		final String sqlSelectCheaterMarketplaceOffers = "SELECT distinct product_id, cheater_price, cheater_price_delta, etalon_price, cheater_rate" +
				" FROM sr_marketpace_offer " +
				" WHERE (marketplace_seller = 1 and marketplace_type = ? and cheater_type = 1 and etalon_price is not null)";
		
		JdbcTemplate jdbcTemplateSlave = new JdbcTemplate(dataSource);
		List<Product> cheaterProducts = jdbcTemplateSlave.query(sqlSelectCheaterMarketplaceOffers,
				new Object[] {crmTypes.getId()},
				new int[] {Types.INTEGER},
				new RowMapper<Product>() {
					@Override
					public Product mapRow(ResultSet rs, int rowNum) throws SQLException {
						Product marketpaceOfferProduct = getProductById(rs.getInt("PRODUCT_ID"));
						marketpaceOfferProduct.getMarket(crmTypes).setCheaterPrice(rs.getBigDecimal("CHEATER_PRICE"));
						marketpaceOfferProduct.getMarket(crmTypes).setCheaterPriceDelta(rs.getBigDecimal("CHEATER_PRICE_DELTA"));
						marketpaceOfferProduct.getMarket(crmTypes).setEtalonPrice(rs.getBigDecimal("ETALON_PRICE"));
						return marketpaceOfferProduct;
					}
				});

		final String sqlUpdateCheaterOffer = "update sr_marketpace_offer" +
				" set cheater_price = null, etalon_price = null, cheater_price_delta = null"
				+ "  where product_id = ? and marketplace_type = ?";
		for (Product cheaterProduct: cheaterProducts) {
				updateDbProductPrice(cheaterProduct.getId(), cheaterProduct.getMarket(crmTypes).getEtalonPrice());

				cheaterProduct.setPrice(cheaterProduct.getMarket(crmTypes).getEtalonPrice());
				cheaterProduct.setPriceWithDiscount(cheaterProduct.getMarket(crmTypes).getEtalonPrice());
				cheaterProduct.setPriceWithoutDiscount(cheaterProduct.getMarket(crmTypes).getEtalonPrice());
				this.jdbcTemplate.update(sqlUpdateCheaterOffer, new Object[] {
						cheaterProduct.getId(),
						crmTypes.getId()});
		}
		return cheaterProducts;
	}
	
	public List<Product> updateCheaterProductsStart(CrmTypes crmTypes) {
		
		final String sqlSelectCheaterMarketplaceOffers = "SELECT distinct product_id, cheater_price, cheater_price_delta, etalon_price, cheater_rate" +
				" FROM sr_marketpace_offer " +
				" WHERE (marketplace_seller = 1 and marketplace_type = ? and cheater_type = 1 and etalon_price is null)";
		
		JdbcTemplate jdbcTemplateSlave = new JdbcTemplate(dataSource);
		List<Product> cheaterProducts = jdbcTemplateSlave.query(sqlSelectCheaterMarketplaceOffers,
				new Object[] {crmTypes.getId()},
				new int[] {Types.INTEGER},
				new RowMapper<Product>() {
					@Override
					public Product mapRow(ResultSet rs, int rowNum) throws SQLException {

						Product marketpaceOfferProduct = getProductById(rs.getInt("PRODUCT_ID"));
						BigDecimal rawCheaterRate = rs.getBigDecimal("CHEATER_RATE");
						if (rawCheaterRate == null) {
							rawCheaterRate = BigDecimal.valueOf(5);
						}
						BigDecimal cheaterRate = rawCheaterRate.divide(BigDecimal.valueOf(100), 2, RoundingMode.CEILING);

						BigDecimal etalonPrice = marketpaceOfferProduct.getPrice();
						BigDecimal cheaterPriceDelta = etalonPrice.multiply(cheaterRate).add(BigDecimal.ONE);
						cheaterPriceDelta = cheaterPriceDelta.setScale(0, RoundingMode.HALF_UP);

						BigDecimal cheaterPrice = etalonPrice.subtract(cheaterPriceDelta);
						marketpaceOfferProduct.setPrice(cheaterPrice);
						marketpaceOfferProduct.setPriceWithDiscount(cheaterPrice);
						marketpaceOfferProduct.setPriceWithoutDiscount(etalonPrice);

						marketpaceOfferProduct.getMarket(crmTypes).setCheaterPrice(cheaterPrice);
						marketpaceOfferProduct.getMarket(crmTypes).setCheaterPriceDelta(cheaterPriceDelta);
						marketpaceOfferProduct.getMarket(crmTypes).setEtalonPrice(etalonPrice);
						return marketpaceOfferProduct;
					}
				});	
		
		final String sqlUpdateCheaterOffer = "update sr_marketpace_offer" +
				" set cheater_price = ?, cheater_price_delta = ?, etalon_price = ?"
				+ "  where product_id = ? and marketplace_type = ?";
		
		for (Product cheaterProduct : cheaterProducts) {
			this.jdbcTemplate.update(sqlUpdateCheaterOffer, new Object[] {
					cheaterProduct.getMarket(crmTypes).getCheaterPrice(),
					cheaterProduct.getMarket(crmTypes).getCheaterPriceDelta(),
					cheaterProduct.getMarket(crmTypes).getEtalonPrice(),
					cheaterProduct.getId(),					
					crmTypes.getId()});
			updateDbProductPrice(cheaterProduct.getId(), cheaterProduct.getMarket(crmTypes).getCheaterPrice());
		}
		return cheaterProducts;
	}
	
	public void updateYmOfferProduct(Product product) {
		
		final String sqlSelectYmOffer = "SELECT product_id from sr_marketpace_offer WHERE product_id = ? and marketplace_type = ?";		
		final String sqlInsertYmOffer = "INSERT INTO sr_marketpace_offer (product_id, market_sku, supplier_stock, marketplace_seller, special_price, cheater_type, marketplace_type) VALUES (?, ?, ?, ?, ?, ?, ?)";
		final String sqlUpdateYmOffer = "update sr_marketpace_offer set"
				+ " market_sku = ?,"
				+ " supplier_stock = ?,"
				+ " marketplace_seller = ?,"
				+ " special_price = ?,"
				+ " cheater_type = ?"				
				+ " where product_id = ? and marketplace_type = ?";
	
		List<String> productSkus = this.jdbcTemplate.query(sqlSelectYmOffer,
				new Object[]{ product.getId(), CrmTypes.YANDEX_MARKET.getId() },	
				new int[] { Types.INTEGER, Types.INTEGER },
				new RowMapper<String>() {
					@Override
		            public String mapRow(ResultSet rs, int rowNum) throws SQLException {
						return rs.getString("PRODUCT_ID");
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
						product.getMarket(CrmTypes.YANDEX_MARKET).getMarketSku(),
						product.getMarket(CrmTypes.YANDEX_MARKET).isSupplierStock() ? 1 : 0,
						product.getMarket(CrmTypes.YANDEX_MARKET).isMarketSeller() ? 1 : 0,
						product.getMarket(CrmTypes.YANDEX_MARKET).getSpecialPrice(),						
						0, 
						CrmTypes.YANDEX_MARKET.getId()});
			return;
		}	
		// запись есть, мы ее правим
		this.jdbcTemplate.update(sqlUpdateYmOffer, new Object[] {					
					product.getMarket(CrmTypes.YANDEX_MARKET).getMarketSku(),
					product.getMarket(CrmTypes.YANDEX_MARKET).isSupplierStock() ? 1 : 0,
					product.getMarket(CrmTypes.YANDEX_MARKET).isMarketSeller() ? 1 : 0,
					product.getMarket(CrmTypes.YANDEX_MARKET).getSpecialPrice(),
					0,
					product.getId(),
					CrmTypes.YANDEX_MARKET.getId()});			
	}
	
	public void updateOzonOfferProduct(Product product) {
		
		final String sqlSelectOzonOffer = "SELECT * from sr_marketpace_offer WHERE product_id = ? and marketplace_type = ?";
		
		final String sqlInsertOzonOffer = "insert into sr_marketpace_offer (product_id, market_sku, supplier_stock, marketplace_seller, special_price, marketplace_type) values (?, ?, ?, ?, ?, ?)";

		final String sqlUpdateOzonOffer = "update sr_marketpace_offer "
				+ "  set market_sku = ?, "
				+ "      supplier_stock = ?, "
				+ "      marketplace_seller = ?, "
				+ "      special_price = ?"				
				+ "  where product_id = ? and marketplace_type = ?";
	
		List<String> productSkus = this.jdbcTemplate.query(sqlSelectOzonOffer,
				new Object[] { product.getId(), CrmTypes.OZON.getId() },
				new int[] { Types.INTEGER, Types.INTEGER },
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
	
	public void updateYmOfferMapping(List<Product> products) {
		final String sqlUpdateYmOffer = "update sr_marketpace_offer set market_sku = ?"
				+ " WHERE marketplace_type = ? and product_id = ?";
		
		for (Product item : products) {
			if (StringUtils.isNoneEmpty(item.getMarket(CrmTypes.YANDEX_MARKET).getMarketSku())) {
				this.jdbcTemplate.update(sqlUpdateYmOffer, new Object[] {
						item.getMarket(CrmTypes.YANDEX_MARKET).getMarketSku(),
						CrmTypes.YANDEX_MARKET.getId(),
						item.getId()});
			}
		}
	}
	
	public void updateProductDescriptionMeta(Product product) {
		log.debug("updateProductDescriptionMeta():{}", product);
				
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
			log.info("title({}): {}", metaTitle.length(), metaTitle);
			log.info("description({}): {}", metaDescription.length(), metaDescription);
		}
		if (metaDescription.length() < 150 || metaDescription.length() > 250) {
			log.info("description({}): {}", metaDescription.length(), metaDescription);
		}	
		
		product.getStore().setMetaTitle(metaTitle);
		product.getStore().setMetaDescription(metaDescription);
				
		return product;						
	}
	
	public Product createProductDescriptionMeta(int productId) {
		
		Product product = getDbProductById(productId);		
		product = createProductDescriptionMeta(product);
		return product;		
	}

	// реализовано только для 1-го склада
	public void updateSupplierStockPrice(Product product) {
		
		log.debug("updateStockSupplierPrice():{}", product);
		if (product.getMainSupplier() == null) {
			return;
		}
		final String sqlSelectMaxIdStock = "SELECT MAX(id) id FROM sr_stock s "
				+ "WHERE s.product_id = ? "
				+ "AND s.stock_id = 1 AND s.supplier_id = ?";
		Integer stockId = this.jdbcTemplate.queryForObject(sqlSelectMaxIdStock,
		        new Object[]{ product.getId(), product.getMainSupplier().getId() },
		        new int[] {Types.INTEGER, Types.INTEGER },
		        new RowMapper<Integer>() {			
					@Override
		            public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
		            	return Integer.valueOf(rs.getInt("ID"));	
		            }
		        });
		
		if (stockId > 0) {			
			final String sqlUpdateStock = "UPDATE sr_stock s "
					+ "  SET supplier_price = ?, supplier_quantity = ?"
					+ "  WHERE s.id = ?";
			
			this.jdbcTemplate.update(sqlUpdateStock, new Object[] { 				
					product.getSupplierPrice(),
					product.getSupplierQuantity(),					
					stockId});
			
		} else {
			final String sqlInsertStock = "INSERT INTO sr_stock"
					+ " (product_id, stock_id, supplier_id, supplier_price, supplier_quantity, quantity)"
					+ " VALUES"
					+ " (?, ?, ?, ?, ?, ?)";
			
			this.jdbcTemplate.update(sqlInsertStock, new Object[] {
					product.getId(),
					1,
					product.getMainSupplier().getId(),
					product.getSupplierPrice(),
					product.getSupplierQuantity(),					
					0});			
		}
	}	

	private List<SupplierStockProduct> supplierStockProductsFindByObjectId(int objectId, String sqlSelectSupplier) {
		
		List<SupplierStockProduct> supplierStockProducts = this.jdbcTemplate.query(sqlSelectSupplier,
				new Object[] { objectId },
				new int[] { Types.INTEGER },
				new RowMapper<SupplierStockProduct>() {
					@Override
		            public SupplierStockProduct mapRow(ResultSet rs, int rowNum) throws SQLException {		                
						Product product = getProductById(rs.getInt("PRODUCT_ID"));						
		            	product.setSupplierPrice(rs.getBigDecimal("SUPPLIER_PRICE"));
		            	product.setSupplierQuantity(rs.getInt("SUPPLIER_QUANTITY"));
		            	product.setStockQuantity(rs.getInt("QUANTITY"));		            	
		            	product.setPrice(rs.getBigDecimal("PRODUCT_PRICE"));
		            	product.setQuantity(rs.getInt("PRODUCT_QUANTITY"));
		            	SupplierStockProduct supplierStockProduct = new SupplierStockProduct(product);
		            	supplierStockProduct.setId(rs.getInt("ID"));
		            	supplierStockProduct.setSupplier(SupplierTypes.getValueById(rs.getInt("SUPPLIER_ID")));
		                return supplierStockProduct;					
		            }
		        });
		return supplierStockProducts;
	}
	
	private SupplierStockProduct supplierStockProductFindByObjectId(int objectId, String sqlSelectSupplier) {
		List<SupplierStockProduct> supplierStockProducts = supplierStockProductsFindByObjectId(objectId, sqlSelectSupplier);

		if (supplierStockProducts.size() == 0) {
			return null;
		}
		return supplierStockProducts.get(0);						
	}	
	
	public SupplierStock getSupplierStock(SupplierTypes supplier, ProductCategory productCategory) {
		
		String sqlSelectSupplierStockProducts = "SELECT * FROM sr_v_stock s WHERE s.supplier_id = ? ";
		
		if (productCategory != null && productCategory.getId() > 0) {
			sqlSelectSupplierStockProducts += " AND s.category_group_id = " + productCategory.getId();			
		}
		sqlSelectSupplierStockProducts += "  ORDER BY s.category_group, s.category_annotation, s.product_name";
		
		List<SupplierStockProduct> supplierStockProducts = supplierStockProductsFindByObjectId(supplier.getId(), sqlSelectSupplierStockProducts);
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

		return stock;			
	}	
	
	public SupplierStock getSupplierStocks() {
		
		final String sqlSelectAllSupplierStockProducts = "SELECT * FROM sr_v_stock s ORDER BY s.category_group, s.category_annotation, s.product_id";
		List<SupplierStockProduct> allSupplierStockProducts = this.jdbcTemplate.query(sqlSelectAllSupplierStockProducts,
				new Object[] { },
				new int[] { },
				new RowMapper<SupplierStockProduct>() {
					@Override
		            public SupplierStockProduct mapRow(ResultSet rs, int rowNum) throws SQLException {
		            	Product product = getProductById(rs.getInt("PRODUCT_ID"));
		            	product.setSupplierPrice(rs.getBigDecimal("SUPPLIER_PRICE"));
		            	product.setSupplierQuantity(rs.getInt("SUPPLIER_QUANTITY"));
		            	product.setStockQuantity(rs.getInt("QUANTITY"));		 	
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
		
		SupplierStock stock = new SupplierStock(allSupplierStockProducts);
		stock.setAmount(OrderAmountTypes.TOTAL_SUPPLIER, totalSupplierStockAmount);
		stock.setAmount(OrderAmountTypes.TOTAL_BILL, totalBillStockAmount);
		return stock;
	}
	
	public SupplierStockProduct supplierStockProductFindById(int id) {
		final String sqlSelectSupplierStock = "SELECT * FROM sr_v_stock s WHERE s.id = ? ";
		return supplierStockProductFindByObjectId(id, sqlSelectSupplierStock);
	}
	
	public SupplierStockProduct supplierStockProductFindByProductId(int productId) {
		final String sqlSelectSupplierStockProduct = "SELECT * FROM sr_v_stock s WHERE s.product_id = ? ";	
		return supplierStockProductFindByObjectId(productId, sqlSelectSupplierStockProduct);	
	}
	
	public int addSupplierStockProduct(SupplierStockProduct supplierStockProduct) {
		
		final String sqlInsertSupplierStockProduct = "INSERT into sr_stock "
				+ "	(product_id, stock_id, supplier_id, supplier_price, supplier_quantity, quantity, comment)"
				+ " VALUES"
				+ " (?, ?, ?, ?, ?, ?, ?)";		
		GeneratedKeyHolder generatedKeyHolder = new GeneratedKeyHolder();
		int rowsAffected = jdbcTemplate.update(connection -> {            
            PreparedStatement preparedStatement = connection.prepareStatement(sqlInsertSupplierStockProduct, Statement.RETURN_GENERATED_KEYS);
            int index = 1;
            preparedStatement.setInt(index++, supplierStockProduct.getProduct().getId());
            preparedStatement.setInt(index++, 1);
            preparedStatement.setInt(index++, supplierStockProduct.getSupplier().getId());
            preparedStatement.setBigDecimal(index++, supplierStockProduct.getProduct().getSupplierPrice());
            preparedStatement.setInt(index++, supplierStockProduct.getProduct().getSupplierQuantity());
            preparedStatement.setInt(index++, supplierStockProduct.getProduct().getStockQuantity());
            preparedStatement.setString(index++, supplierStockProduct.getComment());
            return preparedStatement;	            
        }, generatedKeyHolder);
		final int result = getLastInsertByGeneratedKeyHolder(generatedKeyHolder, rowsAffected);

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
	
	public void updateSupplierStockProduct(SupplierStockProduct supplierStockProduct) {
		final String sqlUpdateSupplierStockProduct = "UPDATE sr_stock s"
				+ "  SET product_id = ?, "
				+ "      stock_id = ?,"
				+ "      supplier_id = ?,"
				+ "      supplier_price = ?, "
				+ "      supplier_quantity = ?, "
				+ "      quantity = ?, "
				+ "      comment = ? "
				+ "  WHERE s.id = ?";
		
		this.jdbcTemplate.update(sqlUpdateSupplierStockProduct, new Object[] { 				
				supplierStockProduct.getProduct().getId(),
				1,
				supplierStockProduct.getSupplier().getId(),
				supplierStockProduct.getProduct().getSupplierPrice(),
				supplierStockProduct.getProduct().getSupplierQuantity(),
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
		}				
				
		Product wikiProduct = getProductById(supplierStockProduct.getProduct().getId());		
		wikiProduct.setStockQuantity(supplierStockProduct.getProduct().getStockQuantity());
		wikiProduct.setQuantity(supplierStockProduct.getProduct().getQuantity());
		wikiProduct.setSupplierPrice(supplierStockProduct.getProduct().getSupplierPrice());
	} 
	
	public void deleteSupplierStockProduct(int supplierStockProductId) {
		final String sqlUpdateSupplierStockProduct = "DELETE FROM sr_stock WHERE id = ?";
		this.jdbcTemplate.update(sqlUpdateSupplierStockProduct, new Object[] {
				supplierStockProductId});	
	}
	
	public void supplierStockProductSaveOrUpdate(SupplierStockProduct supplierStockProduct) {
		if (supplierStockProduct.isNew()) {
			addSupplierStockProduct(supplierStockProduct);
		} else {
			updateSupplierStockProduct(supplierStockProduct);			
		}
	}
	
	public BigDecimal ejectTotalAmountsByConditions(OrderAmountTypes amountType, Pair<Date> period) {	
		
		final String sqlSelectTotalAmounts = "SELECT SUM(amount) SUM_AMOUNT FROM sr_period_total_amount pta"
				+ "  WHERE pta.amount_type = ? "
				+ "    AND pta.period_in between ? and ?";
		BigDecimal sumAmount = this.jdbcTemplate.queryForObject(sqlSelectTotalAmounts,
		        new Object[] { amountType.getId(), period.getStart(), period.getEnd() },
		        new int[] { Types.INTEGER, Types.DATE, Types.DATE },
		        new RowMapper<BigDecimal>() {
					@Override
		            public BigDecimal mapRow(ResultSet rs, int rowNum) throws SQLException {
		            	return rs.getBigDecimal("SUM_AMOUNT");	
		            }
		        });
		return sumAmount == null ? BigDecimal.ZERO : sumAmount;
	}
	
	private List<ProductCategory> instanceCategories() {

		log.debug("instanceCategories()");
					
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

		log.debug("instanceProducts()");
		final String sqlSelectProducts = "SELECT p.* FROM sr_v_product p ORDER BY p.product_id";

		List<Product> products = this.jdbcTemplate.query(sqlSelectProducts,
				new RowMapper<Product>() {
					@Override
					public Product mapRow(ResultSet rs, int rowNum) throws SQLException {
						
						return createProduct4RowMapperMapRow(rs, rowNum, false);
			
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
		
		log.debug("instanceProductsYandexOffer()");
		final String sqlSelectSpecialPrices = "SELECT * FROM sr_marketpace_offer ybo WHERE marketplace_type = 4"
				+ "  ORDER BY PRODUCT_ID";	
		List<Product> specialProducts = this.jdbcTemplate.query(sqlSelectSpecialPrices,
				new Object[] { },
				new int[] { },
				new RowMapper<Product>() {
					@Override
					public Product mapRow(ResultSet rs, int rowNum) throws SQLException {
						Product item = getProductById(rs.getInt("PRODUCT_ID"));
						if (item != null) {
							item.getMarket(CrmTypes.YANDEX_MARKET).setMarketSku(rs.getString("market_sku"));
							if (rs.getInt("SUPPLIER_STOCK") == 1) {
								item.getMarket(CrmTypes.YANDEX_MARKET).setSupplierStock(true);
							}
							if (rs.getInt("MARKETPLACE_SELLER") == 1) {
								item.getMarket(CrmTypes.YANDEX_MARKET).setMarketSeller(true);
							}	
						}					
						return item;
					}
				});
		return specialProducts;			
	}
	
	private List<Product> instanceProductsOzonOffer() {
		
		log.debug("instanceProductsOzonOffer()");
		final String sqlSelectSpecialPrices = "SELECT PRODUCT_ID, SUPPLIER_STOCK, MARKETPLACE_SELLER, market_sku, special_price"
				+ " FROM sr_marketpace_offer WHERE marketplace_type = 5";	
		List<Product> specialProducts = this.jdbcTemplate.query(sqlSelectSpecialPrices,
				new Object[] { },
				new int[] { },
				new RowMapper<Product>() {
					@Override
					public Product mapRow(ResultSet rs, int rowNum) throws SQLException {
						Product item = getProductById(rs.getInt("PRODUCT_ID"));
						if (item != null) {
							item.getMarket(CrmTypes.OZON).setMarketSku(rs.getString("market_sku"));
							if (rs.getInt("SUPPLIER_STOCK") == 1) {
								item.getMarket(CrmTypes.OZON).setSupplierStock(true);
							}
							if (rs.getInt("MARKETPLACE_SELLER") == 1) {
								item.getMarket(CrmTypes.OZON).setMarketSeller(true);
							}	
						}					
						return item;
					}
				});
		return specialProducts;			
	}
	
	private void clearSession() {
		log.debug("clearSession(): {}", DateTimeUtils.sysDate());
		final String sqlDeleteSessions = "DELETE FROM oc_session WHERE expire < (sysdate() - INTERVAL 1 DAY)";
		this.jdbcTemplate.update(sqlDeleteSessions, new Object[] {});
	}
	
	private Set<Product> compositeProductByType(int productId, int slaveType) {	
			
		final String sqlSelectCompositeProducts = "SELECT * FROM sr_product_composite pc"
				+ " WHERE pc.master_product_id = ? AND pc.slave_type = ?"
				+ " ORDER BY pc.slave_product_id";
		
		JdbcTemplate jdbcTemplateComposite = new JdbcTemplate(dataSource);
		List<Product> compositeProducts = jdbcTemplateComposite.query(sqlSelectCompositeProducts,
				new Object[] {productId, slaveType},
				new int[] { Types.INTEGER, Types.INTEGER },
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
		
		log.debug("setProductsSpecialPrice()");
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
				new int[] {Types.DATE, Types.DATE, Types.DATE, Types.DATE, Types.DATE, Types.DATE},
				new RowMapper<Product>() {
					@Override
					public Product mapRow(ResultSet rs, int rowNum) throws SQLException {
						Product item = getProductById(rs.getInt("PRODUCT_ID")); 
						BigDecimal specialPrice = rs.getBigDecimal("PRICE");
				
						if (specialPrice != null && specialPrice != BigDecimal.ZERO && item != null) {
							log.debug("setProductsSpecialPrice():{},{},{}", item.getId(), item.getName(), specialPrice);
							
							item.setPriceWithoutDiscount(item.getPrice());
							item.setPriceWithDiscount(specialPrice);							
							item.setPrice(specialPrice);
						}		
						return item;
					}
				});
		return specialProducts;
	}
		
	public List<Product> findProductsByName(String contextString) {		
		final int MAX_COUNT = 20;
		return findProductsByNames(contextString, MAX_COUNT);		
	}
	
	public Product findSingleProductByName(String contextString) {
		List<Product> products = findProductsByNames(contextString, 1);
		if (products == null || products.size() == 0) {
			return null;
		}
		return products.get(0);
	}
	
	public String findCdekPvzByAddress(Address address) {
		
		String city = TextUtils.cutCityFromAddress(address.getAddress());
		String streetAddress = TextUtils.cutStreetFromAddress(address.getAddress());
		if (StringUtils.isEmpty(city)) {
			return "";
		}		
		log.debug("findCodeCdekPvzByAddress()");
		final String sqlSelectSingleCityCdekPvz = "SELECT COUNT(code) count, MIN(code) single_code FROM sr_wiki_cdek_pvz w"
				+ "  WHERE w.city = ?";		
		ProductCategory pvz = this.jdbcTemplate.queryForObject(sqlSelectSingleCityCdekPvz,
		        new Object[] { city },
		        new int[] { Types.VARCHAR },
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
				new int[] { Types.VARCHAR, Types.VARCHAR },
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
		
	/**
	 * Контейнер параметров для метода списания остатков товара с фронта и бэка
	 * @author alex4doorow
	 *
	 */
	@Data
	@ToString
	private static class Result4UpdateProductStock {
		// товар на фронте и бэке (как одиночный, так и комплект)
		private boolean isProductFront;
		private boolean isProductBack;		
		// элемент комплекта на фронте и бэке (только для комплекта)		
		private boolean isSlaveFront;
		private boolean isSlaveBack;
	}
}
