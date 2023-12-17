package ru.sir.richard.boss.service;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import ru.sir.richard.boss.config.DbConfig;
import ru.sir.richard.boss.config.LoaderConfig;
import ru.sir.richard.boss.dao.WikiDao;
import ru.sir.richard.boss.data.raw.CategoryDataRaw;
import ru.sir.richard.boss.data.raw.ManufacturerDataRaw;
import ru.sir.richard.boss.data.raw.ProductDataRaw;
import ru.sir.richard.boss.model.data.Product;
import ru.sir.richard.boss.model.data.ProductImage;
import ru.sir.richard.boss.model.data.ProductSpecialPrice;
import ru.sir.richard.boss.model.utils.DateTimeUtils;
import ru.sir.richard.boss.model.utils.Pair;

@Service("converterServiceOld")
public class ConverterServiceImplOld {
		
	private final Logger logger = LoggerFactory.getLogger(ConverterServiceImpl.class);
	
	@Autowired  
	private WikiDao wiki;
	
	@Autowired  
	protected JdbcTemplate jdbcTemplate;
	
	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

		
	public void createDescriptions() {
		final String sqlSelectProduct = "select * from p326995_pm.sr_v_product_light /*where product_id = 10158*/ order by product_id";
		final String sqlUpdateProduct = "update p326995_pm.oc_product_description "
				+ "  set meta_title = ?,"
				+ "      meta_description = ?"
				+ "  where product_id = ? and language_id = 2";
		
		
		List<Product> srProducts = this.jdbcTemplate.query(sqlSelectProduct,
				new Object[]{},				
				new RowMapper<Product>() {
					@Override
		            public Product mapRow(ResultSet rs, int rowNum) throws SQLException {
													
						Product item = new Product(rs.getInt("PRODUCT_ID"), rs.getString("NAME"));
						item.setModel(rs.getString("MODEL"));
						item.setSku(rs.getString("SKU"));
						item.setQuantity(rs.getInt("QUANTITY"));
						item.setPrice(rs.getBigDecimal("PRICE"));					
						item.getStore().setDescription(rs.getString("product_description"));
						item.getStore().setMetaDescription(rs.getString("product_meta_description"));
						item.getStore().setMetaTitle(rs.getString("product_meta_title"));
						item.getStore().setMetaKeyword(rs.getString("product_meta_keyword"));
						
														
						return item;
		            }
		        });	
		
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
		String description;
		String title;		
		for (Product product : srProducts) {
			//logger.info("{}, {}, {}, {}", product.getId(), product.getName(), product.getStore().getMetaTitle(), product.getStore().getMetaDescription());
			
			String name = product.getName().replace("&quot;", "");
			String model = product.getModel().replace("&quot;", "");
			
			stringPrice = df.format(product.getPrice()); 
			description = String.format(formatDescriptionOne, name, stringPrice);
			if (description.length() > 250) {
				description = String.format(formatDescriptionTwo, name, stringPrice);			
			}			
			title = String.format(formatTitleOne, name);
			if (title.length() > 70) {
				title = String.format(formatTitleTwo, name);			
			}
			if (title.length() > 70) {
				title = String.format(formatTitleThree, name);			
			}	
			if (title.length() > 70) {
				title = String.format(formatTitleFour, name);			
			}
			if (title.length() > 70) {
				title = String.format(formatTitleFour, model);			
			}
			if (title.length() < 40) {
				title = String.format(formatTitleFour, name);			
			}	
			
			if (title.length() < 40 || title.length() > 70) {
				logger.info("title({}): {}", title.length(), title);	
				logger.info("description({}): {}", description.length(), description);		
			}
			if (description.length() < 150 || description.length() > 250) {
				logger.info("description({}): {}", description.length(), description);				
			}	
			
			/*
			Product nProduct = wiki.createProductDescriptionMeta(product.getId());
			title = nProduct.getStore().getMetaTitle();
			description = nProduct.getStore().getMetaDescription();
			*/
			
			this.jdbcTemplate.update(sqlUpdateProduct, new Object[] {
					title,
					description,
					product.getId()});	
		}	
	}
	

	public void copyProductSr4Pm(int startProductId) {
		
		final String sqlSelectSrProduct = "SELECT * FROM p326995_mysirric.oc_product p, oc_product_description pd "
				+ "  where p.product_id = pd.product_id and pd.language_id = 4 and "
				+ "        p.product_id >= ? "
				+ "  order by p.product_id";	
		List<Product> srProducts = this.jdbcTemplate.query(sqlSelectSrProduct,
				new Object[]{startProductId},				
				new RowMapper<Product>() {
					@Override
		            public Product mapRow(ResultSet rs, int rowNum) throws SQLException {
													
						Product item = new Product(rs.getInt("PRODUCT_ID"), rs.getString("NAME"));
						item.setModel(rs.getString("MODEL"));
						item.setSku(rs.getString("SKU"));
						item.setQuantity(rs.getInt("QUANTITY"));
						item.getStore().setPicture(rs.getString("IMAGE"));
						item.setPrice(rs.getBigDecimal("PRICE"));
						item.getStore().setWeight(rs.getBigDecimal("WEIGHT"));
						item.getStore().setLength(rs.getInt("LENGTH") < 0 ? 10 : rs.getInt("LENGTH"));
						item.getStore().setWidth(rs.getInt("WIDTH") < 0 ? 10 : rs.getInt("WIDTH"));
						item.getStore().setHeight(rs.getInt("HEIGHT") < 0 ? 10 : rs.getInt("HEIGHT"));
						item.setCategory(wiki.getCategoryById(rs.getInt("CATEGORY_GROUP_ID")));
						if (rs.getInt("COMPOSITE") == 1) {
							item.setComposite(true);							
						} else {
							item.setComposite(false);
						}	
						item.getStore().setDescription(rs.getString("description"));
										
						item.getStore().setJan(rs.getString("JAN"));
						item.getStore().setIsbn(rs.getString("ISBN"));						
						item.getStore().setStockStatusId(rs.getInt("STOCK_STATUS_ID"));
						item.getStore().setManufacturerId(rs.getInt("MANUFACTURER_ID"));
						item.getStore().setPoints(rs.getInt("POINTS"));						
						item.getStore().setAvailableDate(rs.getDate("DATE_AVAILABLE"));		
						item.getStore().setAddedDate(DateTimeUtils.timestampToDate(rs.getTimestamp("DATE_ADDED")));		
						try {
							item.getStore().setModifiedDate(DateTimeUtils.timestampToDate(rs.getTimestamp("DATE_MODIFIED")));
						} catch (java.sql.SQLException e) {
							item.getStore().setModifiedDate(DateTimeUtils.timestampToDate(rs.getTimestamp("DATE_ADDED")));
						}
						item.getStore().setWeightClassId(rs.getInt("WEIGHT_CLASS_ID"));
						item.getStore().setLengthClassId(rs.getInt("LENGTH_CLASS_ID"));
						item.getStore().setStatus(rs.getInt("STATUS"));						
									
						item.getStore().setMetaDescription(rs.getString("meta_description"));
						item.getStore().setMetaTitle(rs.getString("NAME"));
						item.getStore().setMetaKeyword(rs.getString("meta_keyword"));
						item.getStore().setTag(rs.getString("tag"));
						item.getStore().setMinimum(rs.getInt("MINIMUM"));
						item.getStore().setSortOrder(rs.getInt("SORT_ORDER"));
						
						item.getStore().setImages(loadSrProductImages(item.getId()));
						item.getStore().setSpecialPrices(loadSrProductSpecialPrices(item.getId()));						
						item.setSeoUrl(loadSrProductSeoUrl(item.getId()));
														
						return item;
		            }
		        });	
		
		for (Product product : srProducts) {
			deletePmProduct(product.getId());
			addPmProduct(product);			
		}
	}
	
	private void deletePmProduct(int productId) {
		logger.info("deleteProduct: {}", productId);
		
		final String sqlDeleteProductImages = "DELETE FROM p326995_pm.oc_product_image WHERE product_id = ?";
		final String sqlDeleteProductDescription = "DELETE FROM p326995_pm.oc_product_description WHERE product_id = ?";
		final String sqlDeleteProductDiscount = "DELETE FROM p326995_pm.oc_product_discount WHERE product_id = ?";
		final String sqlDeleteProductSpecial = "DELETE FROM p326995_pm.oc_product_special WHERE product_id = ?";		
		final String sqlDeleteProductLayout = "DELETE FROM p326995_pm.oc_product_to_layout WHERE product_id = ?";
		final String sqlDeleteProductStore = "DELETE FROM p326995_pm.oc_product_to_store WHERE product_id = ?";		
		final String sqlDeleteProductSeoUrl = "DELETE FROM p326995_pm.oc_seo_url WHERE query = ?";
		final String sqlDeleteProduct = "DELETE FROM p326995_pm.oc_product WHERE product_id = ?";
				
		Connection conn = null;
		PreparedStatement pstmt = null;
		try {
			Class.forName(DbConfig.JDBC_DRIVER);
			conn = DriverManager.getConnection(DbConfig.DB_PM_PRODUCTION_URL);
			pstmt = conn.prepareStatement(sqlDeleteProductImages);
			pstmt.setInt(1, productId);
			pstmt.executeUpdate();
			
			pstmt = conn.prepareStatement(sqlDeleteProductDescription);
			pstmt.setInt(1, productId);
			pstmt.executeUpdate();
			
			pstmt = conn.prepareStatement(sqlDeleteProductDiscount);
			pstmt.setInt(1, productId);
			pstmt.executeUpdate();
			
			pstmt = conn.prepareStatement(sqlDeleteProductSpecial);
			pstmt.setInt(1, productId);
			pstmt.executeUpdate();
			
			pstmt = conn.prepareStatement(sqlDeleteProductLayout);
			pstmt.setInt(1, productId);
			pstmt.executeUpdate();
			
			pstmt = conn.prepareStatement(sqlDeleteProductStore);
			pstmt.setInt(1, productId);
			pstmt.executeUpdate();
			
			pstmt = conn.prepareStatement(sqlDeleteProductSeoUrl);
			pstmt.setString(1, "product_id=" + productId);
			pstmt.executeUpdate();
			
			pstmt = conn.prepareStatement(sqlDeleteProduct);
			pstmt.setInt(1, productId);
			pstmt.executeUpdate();
						

		} catch (SQLException se) {
			se.printStackTrace();
		} catch (Exception se) {
			se.printStackTrace();
		} 				
	}	

	private void addPmProduct(Product product) {
		logger.info("addProduct: {}", product.getId());

		Connection conn = null;
		PreparedStatement pstmt = null;
		
		final String keyWord = "data/";
		final String replaceWord = "catalog/products/";
		String newImage;
		
		int pstmtIndex;		
		try {
			Class.forName(DbConfig.JDBC_DRIVER);
			conn = DriverManager.getConnection(DbConfig.DB_PM_PRODUCTION_URL);
			
			// product
			final String sqlInsertProduct = "INSERT INTO p326995_pm.oc_product"
					+ " (product_id, model, jan,"
					+ "  location, sku, upc, ean, isbn, mpn, tax_class_id,"
					+ "  quantity, stock_status_id, image, manufacturer_id, shipping, price, points, date_available,"
					+ "  weight, weight_class_id, length, width, height, length_class_id, subtract, minimum, sort_order,"
					+ "  status, viewed, date_added, date_modified, category_group_id, composite)"
					+ " VALUES"
					+ " (?, ?, ?,"
					+ "  ?, ?, ?, ?, ?, ?, ?,"
					+ "  ?, ?, ?, ?, ?, ?, ?, ?,"
					+ "  ?, ?, ?, ?, ?, ?, ?, ?, ?,"
					+ "  ?, ?, ?, ?, ?, ?)";		
			
			pstmt = conn.prepareStatement(sqlInsertProduct);						
			pstmtIndex = 1;
			pstmt.setInt(pstmtIndex++, product.getId());
			pstmt.setString(pstmtIndex++, product.getModel());
			pstmt.setString(pstmtIndex++, product.getStore().getJan());
			pstmt.setString(pstmtIndex++, "");
			pstmt.setString(pstmtIndex++, "");
			pstmt.setString(pstmtIndex++, "");
			pstmt.setString(pstmtIndex++, "");
			pstmt.setString(pstmtIndex++, product.getStore().getIsbn());
			pstmt.setString(pstmtIndex++, "");
			pstmt.setInt(pstmtIndex++, 12); // tax_class_id
			pstmt.setInt(pstmtIndex++, product.getQuantity());
			pstmt.setInt(pstmtIndex++, product.getStore().getStockStatusId());
			
			newImage = StringUtils.replace(product.getStore().getPicture(), keyWord, replaceWord);
			
			pstmt.setString(pstmtIndex++, newImage);
			pstmt.setInt(pstmtIndex++, product.getStore().getManufacturerId());
			pstmt.setInt(pstmtIndex++, 1); // shipping
			pstmt.setBigDecimal(pstmtIndex++, product.getPrice());
			pstmt.setInt(pstmtIndex++, product.getStore().getPoints());
			pstmt.setTimestamp(pstmtIndex++, DateTimeUtils.dateToTimestamp(product.getStore().getAvailableDate()));
			
			pstmt.setBigDecimal(pstmtIndex++, product.getStore().getWeight());
			pstmt.setInt(pstmtIndex++, product.getStore().getWeightClassId());
			
			pstmt.setInt(pstmtIndex++, product.getStore().getLength());
			pstmt.setInt(pstmtIndex++, product.getStore().getWidth());
			pstmt.setInt(pstmtIndex++, product.getStore().getHeight());			
			pstmt.setInt(pstmtIndex++, product.getStore().getLengthClassId());
			pstmt.setInt(pstmtIndex++, 1); // subtract
			pstmt.setInt(pstmtIndex++, product.getStore().getMinimum());
			pstmt.setInt(pstmtIndex++, product.getStore().getSortOrder());
			
			pstmt.setInt(pstmtIndex++, product.getStore().getStatus());
			pstmt.setInt(pstmtIndex++, product.getStore().getViewed());
			pstmt.setTimestamp(pstmtIndex++, DateTimeUtils.dateToTimestamp(product.getStore().getAddedDate()));
			pstmt.setTimestamp(pstmtIndex++, DateTimeUtils.dateToTimestamp(product.getStore().getModifiedDate()));			
			pstmt.setInt(pstmtIndex++, product.getCategory().getId());
			if (product.isComposite()) {
				pstmt.setInt(pstmtIndex++, 1);	
			} else {
				pstmt.setInt(pstmtIndex++, 0);
			}
			pstmt.executeUpdate();			
			
			// product_description			
			final String sqlInsertProductDescription = "INSERT INTO p326995_pm.oc_product_description"
					+ " (product_id, language_id, name, description, meta_description, meta_keyword, meta_title, tag)"
					+ " VALUES"
					+ " (?, ?, ?, ?, ?, ?, ?, ?)";
			
			pstmt = conn.prepareStatement(sqlInsertProductDescription);			
			pstmtIndex = 1;
			pstmt.setInt(pstmtIndex++, product.getId());
			pstmt.setInt(pstmtIndex++, 2); // language_id = rus			
			pstmt.setString(pstmtIndex++, product.getName());
			pstmt.setString(pstmtIndex++, product.getStore().getDescription());
			pstmt.setString(pstmtIndex++, product.getStore().getMetaDescription());
			pstmt.setString(pstmtIndex++, product.getStore().getMetaKeyword());
			pstmt.setString(pstmtIndex++, product.getStore().getMetaTitle());
			pstmt.setString(pstmtIndex++, product.getStore().getTag());	
			pstmt.executeUpdate();			
			
			pstmt = conn.prepareStatement(sqlInsertProductDescription);			
			pstmtIndex = 1;
			pstmt.setInt(pstmtIndex++, product.getId());
			pstmt.setInt(pstmtIndex++, 1); // language_id = en			
			pstmt.setString(pstmtIndex++, product.getName());
			pstmt.setString(pstmtIndex++, product.getStore().getDescription());
			pstmt.setString(pstmtIndex++, product.getStore().getMetaDescription());
			pstmt.setString(pstmtIndex++, product.getStore().getMetaKeyword());
			pstmt.setString(pstmtIndex++, product.getStore().getMetaTitle());
			pstmt.setString(pstmtIndex++, product.getStore().getTag());	
			pstmt.executeUpdate();
			
			// product_image
			final String sqlInsertProductImages = "INSERT INTO p326995_pm.oc_product_image"
					+ " (product_id, image, sort_order)"
					+ " VALUES"
					+ " (?, ?, ?)";
			
			for (ProductImage productImage : product.getStore().getImages()) {
				pstmt = conn.prepareStatement(sqlInsertProductImages);			
				pstmtIndex = 1;
				pstmt.setInt(pstmtIndex++, product.getId());	
				
				newImage = StringUtils.replace(productImage.getImage(), keyWord, replaceWord);
								
				pstmt.setString(pstmtIndex++, newImage);
				pstmt.setInt(pstmtIndex++, productImage.getSortOrder());
				pstmt.executeUpdate();	
			}
			
			// product_special
			final String sqlInsertProductSpecial = "INSERT INTO p326995_pm.oc_product_special"
					+ " (product_id, customer_group_id, priority, price, date_start, date_end)"
					+ " VALUES"
					+ " (?, ?, ?, ?, ?, ?)";
			for (ProductSpecialPrice specialPrice : product.getStore().getSpecialPrices()) {
				pstmt = conn.prepareStatement(sqlInsertProductSpecial);			
				pstmtIndex = 1;
				pstmt.setInt(pstmtIndex++, product.getId());
				pstmt.setInt(pstmtIndex++, 1); // customer_group_id
				pstmt.setInt(pstmtIndex++, specialPrice.getPriority());
				pstmt.setBigDecimal(pstmtIndex++, specialPrice.getPrice());
				pstmt.setTimestamp(pstmtIndex++, DateTimeUtils.dateToTimestamp(specialPrice.getPeriod().getStart()));
				pstmt.setTimestamp(pstmtIndex++, DateTimeUtils.dateToTimestamp(specialPrice.getPeriod().getEnd()));
				pstmt.executeUpdate();	
			}			
			
			final String sqlInsertProductLayout = "INSERT INTO p326995_pm.oc_product_to_layout"
					+ " (product_id, store_id, layout_id)"
					+ " VALUES"
					+ " (?, 0, 0)";
			pstmt = conn.prepareStatement(sqlInsertProductLayout);			
			pstmtIndex = 1;
			pstmt.setInt(pstmtIndex++, product.getId());					
			pstmt.executeUpdate();	
						
			final String sqlInsertProductStore = "INSERT INTO p326995_pm.oc_product_to_store"
					+ " (product_id, store_id)"
					+ " VALUES"
					+ " (?, 0)";
			pstmt = conn.prepareStatement(sqlInsertProductStore);			
			pstmtIndex = 1;
			pstmt.setInt(pstmtIndex++, product.getId());					
			pstmt.executeUpdate();	
						
			// seo_url
			final String sqlInsertSeoUrl = "INSERT INTO p326995_pm.oc_seo_url"
					+ " (store_id, language_id, query, keyword)"
					+ " VALUES"
					+ " (0, 2, ?, ?)";
			pstmt = conn.prepareStatement(sqlInsertSeoUrl);			
			pstmtIndex = 1;
			pstmt.setString(pstmtIndex++, "product_id=" + product.getId());
			pstmt.setString(pstmtIndex++, product.getSeoUrl());
			pstmt.executeUpdate();	
			
		} catch (SQLException se) {
			se.printStackTrace();
		} catch (Exception se) {
			se.printStackTrace();
		}		
		
	}
	
	private List<ProductImage> loadSrProductImages(int productId) {

		final String sqlSelectProductImages = "SELECT * FROM p326995_mysirric.oc_product_image"
				+ "  WHERE product_id = ?"
				+ "  ORDER BY sort_order";
			
		List<ProductImage> productImages = this.jdbcTemplate.query(sqlSelectProductImages,
				new Object[]{productId},				
				new RowMapper<ProductImage>() {
					@Override
		            public ProductImage mapRow(ResultSet rs, int rowNum) throws SQLException {
						ProductImage productImage = new ProductImage(rs.getInt("product_image_id"));
						productImage.setImage(rs.getString("image"));
						productImage.setSortOrder(rs.getInt("sort_order"));
								                             		                     		                             
		                return productImage;
		            }
		        });
		return productImages;
	}
	
	private List<ProductSpecialPrice> loadSrProductSpecialPrices(int productId) {
		
		final String sqlSelectProductSpecialPrices = "SELECT * FROM p326995_mysirric.oc_product_special"
				+ "  WHERE product_id = ?"
				+ "  ORDER BY product_special_id";
			
		List<ProductSpecialPrice> productSpecialPrices = this.jdbcTemplate.query(sqlSelectProductSpecialPrices,
				new Object[]{productId},				
				new RowMapper<ProductSpecialPrice>() {
					@SuppressWarnings({ "unchecked", "rawtypes" })
					@Override
		            public ProductSpecialPrice mapRow(ResultSet rs, int rowNum) throws SQLException {
						ProductSpecialPrice productSpecialPrice = new ProductSpecialPrice(rs.getInt("product_special_id"));
						productSpecialPrice.setPriority(rs.getInt("priority"));
						productSpecialPrice.setPrice(rs.getBigDecimal("price"));
						productSpecialPrice.setPeriod(new Pair(rs.getDate("DATE_START"), rs.getDate("DATE_END")));
		                return productSpecialPrice;
		            }
		        });
		return productSpecialPrices;
		
	}
	
	private String loadSrProductSeoUrl(int productId) {
		final String sqlSelectProductSeoUrl = "select * from p326995_mysirric.oc_url_alias"
				+ " where query = ? and (keyword is not null or keyword != '')"
				+ " order by url_alias_id";
			
		String query = "product_id=" + productId;
		List<String> productUrls = this.jdbcTemplate.query(sqlSelectProductSeoUrl,
				new Object[]{query},				
				new RowMapper<String>() {
					@Override
		            public String mapRow(ResultSet rs, int rowNum) throws SQLException {
						return rs.getString("keyword");
		            }
		        });
		if (productUrls != null && productUrls.size() > 0) {
			return productUrls.get(0);
		} else {
			return "";
		}
	}
		
	
	private void convertImage() {
		
		String keyWord = "data/home/gsm-rozetka";
		String replaceWord = "catalog/products/home/gsm-rozetki";
		
		final String sqlSelectProduct = "SELECT * FROM p326995_pm_test2.old_product p where p.image like ? order by product_id";	
		List<ProductDataRaw> products = this.jdbcTemplate.query(sqlSelectProduct,
				new Object[]{"%gsm-rozetka%"},				
				new RowMapper<ProductDataRaw>() {
					@Override
		            public ProductDataRaw mapRow(ResultSet rs, int rowNum) throws SQLException {
						ProductDataRaw product = new ProductDataRaw();						
						product.setId(rs.getInt("product_id"));		   	                
						product.setImage(rs.getString("image"));						
						product.setImages(getConvertProductImages(product));
		                return product;
		            }
		        });
		
		final String sqlUpdateProduct = "update p326995_pm_test2.oc_product set image = ? where product_id = ?";
		
		final String sqlDeleteProductImages = "DELETE FROM p326995_pm_test2.oc_product_image where product_id = ?";
		final String sqlInsertProductImages = "INSERT INTO p326995_pm_test2.oc_product_image"
				+ " (product_id, image, sort_order)"
				+ " VALUES"
				+ " (?, ?, ?)";
		//int indexR;
		for (ProductDataRaw product : products) {
			
			//keyWord = "data/repeller/bird/korshun";
			//replaceWord = "catalog/products/repeller/bird/bioakustika/korshun";
			
			String oldImage = product.getImage();			
			/*
			indexR = StringUtils.indexOf(oldImage , keyWord) + StringUtils.length(keyWord); 
			String newImage = "catalog/products/home/" + replaceWord + "/" + "datchiki/" + StringUtils.substring(oldImage, indexR - 2);
			*/
			String newImage = StringUtils.replace(oldImage, keyWord, replaceWord);
	
			logger.debug("product.id: {}", product.getId());
			logger.debug("product.old.image: {}", oldImage);
			logger.debug("product.new.image: {}", newImage);
			logger.debug("****");
					
		
			this.jdbcTemplate.update(sqlUpdateProduct, new Object[] {	
					newImage,
					product.getId()});

			// image			
			this.jdbcTemplate.update(sqlDeleteProductImages, new Object[] {product.getId()});
			int imageIndex = 1;
			for (String oldAddImage : product.getImages()) {
				//keyWord = "catalog/products/home/gsm-rozetka";
				//replaceWord = "catalog/products/home/gsm-rozetki";
				
				/*
				indexR = StringUtils.indexOf(oldAddImage, keyWord) + StringUtils.length(keyWord); 
				String newAddImage = "catalog/products/home/" + replaceWord + StringUtils.substring(oldAddImage, indexR);
				*/
				String newAddImage = StringUtils.replace(oldAddImage, keyWord, replaceWord);
				logger.debug("product.image[" + imageIndex + "].old: {}", oldAddImage);
				logger.debug("product.image[" + imageIndex + "].new: {}", newAddImage);
								
		
				this.jdbcTemplate.update(sqlInsertProductImages, new Object[] { 
						product.getId(),					
						newAddImage,
						imageIndex
				});
			
				imageIndex++;				
			}
			
			
		}
		
	}
	
	private List<String> getConvertProductImages(ProductDataRaw inProduct) {

		final String sqlSelectOldProductImages = "SELECT * FROM p326995_pm_test2.old_product_image"
				+ "  WHERE product_id = ?"
				+ "  ORDER BY sort_order";
			
		List<String> oldProductImages = this.jdbcTemplate.query(sqlSelectOldProductImages,
				new Object[]{inProduct.getId()},				
				new RowMapper<String>() {
					@Override
		            public String mapRow(ResultSet rs, int rowNum) throws SQLException {
						String image = rs.getString("image");
						
		                             		                     		                             
		                return image;
		            }
		        });
		return oldProductImages;
	}
	

	private void loadProducts() {
		//final String sqlDeleteProducts = "DELETE FROM p326995_pm_test2.oc_product where product_id = 941";
		//final String sqlDeleteProducts = "DELETE FROM p326995_pm_test2.oc_product where product_id < 28";
		final String sqlDeleteProducts = "DELETE FROM p326995_pm_test2.oc_product where product_id in (28,32,33,35,36,40)";
		this.jdbcTemplate.update(sqlDeleteProducts, new Object[] {});
				
		//final String sqlSelectOldProducts = "SELECT * FROM p326995_pm_test2.old_product where product_id = 941 ORDER BY product_id";
		//final String sqlSelectOldProducts = "SELECT * FROM p326995_pm_test2.old_product where product_id < 28 or product_id > 50 ORDER BY product_id";
		final String sqlSelectOldProducts = "SELECT * FROM p326995_pm_test2.old_product where product_id in (28,32,33,35,36,40) ORDER BY product_id";
		logger.debug("product.select.start");
		List<ProductDataRaw> oldProducts = this.jdbcTemplate.query(sqlSelectOldProducts,
				new Object[]{},				
				new RowMapper<ProductDataRaw>() {
					@Override
		            public ProductDataRaw mapRow(ResultSet rs, int rowNum) throws SQLException {
						ProductDataRaw product = new ProductDataRaw();			                
		                product.setId(rs.getInt("product_id"));
		   	                
		                product.setModel(rs.getString("model"));
		                product.setJan(rs.getString("jan"));
		                product.setSku(rs.getString("sku"));		                
		                product.setQuantity(rs.getString("quantity"));
		                product.setStockStatusId(rs.getInt("stock_status_id"));
		                product.setImage(rs.getString("image"));
		                product.setManufacturerId(rs.getInt("manufacturer_id"));
		                product.setPrice(rs.getString("price"));
		                product.setDateAvailable(rs.getTimestamp("date_available"));
		                
		                product.setWeight(rs.getBigDecimal("weight"));
		                product.setWeightClassId(rs.getInt("weight_class_id"));		                
		                product.setLength(rs.getBigDecimal("length"));
		                product.setWidth(rs.getBigDecimal("width"));
		                product.setHeight(rs.getBigDecimal("height"));
		                product.setLengthClassId(rs.getInt("length_class_id"));
		                
		                product.setMinimum(rs.getInt("minimum"));
		                product.setSubtract(1);
		                product.setSortOrder(rs.getInt("sort_order"));
		                product.setStatus(rs.getInt("status"));
		                		                
		                product.setDateAdded(rs.getTimestamp("date_added"));
		                product.setDateModified(rs.getTimestamp("date_modified"));
		                
		                product.setViewed(rs.getInt("viewed"));
		                product.setCategoryGroupId(rs.getInt("category_group_id"));
		                product.setComposite(rs.getInt("composite"));
		                
		                product = getProductDescription(product);
		                product.setImages(getProductImages(product));
		                product.setSpecialPrices(getProductSpecialPrices(product));
		                product.setCategories(getProductCategories(product));
		                product = setSeo(product);
		                             		                     		                             
		                return product;
		            }
		        });
		
		logger.debug("product.select.end");
		final String sqlInsertProduct = "INSERT INTO p326995_pm_test2.oc_product"
				+ " (product_id, model, jan,"
				+ "  location, sku, upc, ean, isbn, mpn, tax_class_id,"
				+ "  quantity, stock_status_id, image, manufacturer_id, shipping, price, points, date_available,"
				+ "  weight, weight_class_id, length, width, height, length_class_id, subtract, minimum, sort_order,"
				+ "  status, viewed, date_added, date_modified, category_group_id, composite)"
				+ " VALUES"
				+ " (?, ?, ?,"
				+ "  ?, ?, ?, ?, ?, ?, ?,"
				+ "  ?, ?, ?, ?, ?, ?, ?, ?,"
				+ "  ?, ?, ?, ?, ?, ?, ?, ?, ?,"
				+ "  ?, ?, ?, ?, ?, ?)";
				
		final String sqlDeleteProductDescription = "DELETE FROM p326995_pm_test2.oc_product_description where product_id = ?";
		final String sqlInsertProductDescription = "INSERT INTO p326995_pm_test2.oc_product_description"
				+ " (product_id, language_id, name, description, meta_description, meta_keyword, meta_title, tag)"
				+ " VALUES"
				+ " (?, ?, ?, ?, ?, ?, ?, ?)";
		
		final String sqlDeleteProductImages = "DELETE FROM p326995_pm_test2.oc_product_image where product_id = ?";
		final String sqlInsertProductImages = "INSERT INTO p326995_pm_test2.oc_product_image"
				+ " (product_id, image, sort_order)"
				+ " VALUES"
				+ " (?, ?, ?)";
		
		final String sqlDeleteProductSpecial = "DELETE FROM p326995_pm_test2.oc_product_special where product_id = ?";
		final String sqlInsertProductSpecial = "INSERT INTO p326995_pm_test2.oc_product_special"
				+ " (product_id, customer_group_id, priority, price)"
				+ " VALUES"
				+ " (?, ?, ?, ?)";	

		final String sqlSelectCountProductCategory = "select COUNT(*) COUNT_ID from p326995_pm_test2.oc_product_to_category WHERE product_id = ? and category_id = ?";
		final String sqlDeleteProductCategory = "DELETE FROM p326995_pm_test2.oc_product_to_category where product_id = ?";
		final String sqlInsertProductCategory = "INSERT INTO p326995_pm_test2.oc_product_to_category"
				+ " (product_id, category_id, main_category)"
				+ " VALUES"
				+ " (?, ?, ?)";
		
		final String sqlDeleteProductLayout = "DELETE FROM p326995_pm_test2.oc_product_to_layout where product_id = ?";
		final String sqlInsertProductLayout = "INSERT INTO p326995_pm_test2.oc_product_to_layout"
				+ " (product_id, store_id, layout_id)"
				+ " VALUES"
				+ " (?, 0, 0)";
		
		
		final String sqlDeleteProductStore = "DELETE FROM p326995_pm_test2.oc_product_to_store where product_id = ?";
		final String sqlInsertProductStore = "INSERT INTO p326995_pm_test2.oc_product_to_store"
				+ " (product_id, store_id)"
				+ " VALUES"
				+ " (?, 0)";
		
		final String sqlDeleteSeoUrl = "DELETE FROM p326995_pm_test2.oc_seo_url where store_id = ? and language_id = ? and query = ?";
		final String sqlInsertSeoUrl = "INSERT INTO p326995_pm_test2.oc_seo_url"
				+ " (store_id, language_id, query, keyword)"
				+ " VALUES"
				+ " (?, ?, ?, ?)";
		
		logger.debug("product.insert.start");
		int productIndex = 0;
		for (ProductDataRaw product : oldProducts) {
			
			String image = product.getImage();
			image = image.replace("data", "catalog/products");
				
			this.jdbcTemplate.update(sqlInsertProduct, new Object[] { 
					product.getId(), 
					product.getModel(), 
					product.getJan(),
					"", product.getSku(), "", "", "", "", 12,					
					Integer.valueOf(product.getQuantity().trim()), 
					product.getStockStatusId(),					
					image,					
					product.getManufacturerId(),
					1,
					new BigDecimal(product.getPrice()),
					0,
					product.getDateAvailable(),
					product.getWeight(),
					product.getWeightClassId(),
					product.getLength(),
					product.getWeight(),
					product.getHeight(),
					product.getLengthClassId(),
					product.getSubtract(),
					product.getMinimum(),
					product.getSortOrder(),
					product.getStatus(),
					product.getViewed(),
					product.getDateAdded(),
					product.getDateModified(),					
					product.getCategoryGroupId(),
					product.getComposite()	
			});				

			String newDescription = product.getDescription();
			newDescription = newDescription.replaceAll("/image/data/", "/image/catalog/products/");
					
			this.jdbcTemplate.update(sqlDeleteProductDescription, new Object[] {product.getId()});			
			this.jdbcTemplate.update(sqlInsertProductDescription, new Object[] { 
					product.getId(),
					1,
					product.getName(), 
					newDescription,
					product.getMetaDescription(),
					product.getMetaKeyword(),
					product.getName(),					
					product.getTag()
			});			
			this.jdbcTemplate.update(sqlInsertProductDescription, new Object[] { 
					product.getId(),
					2,
					product.getName(), 
					newDescription,
					product.getMetaDescription(),
					product.getMetaKeyword(),
					product.getName(),				
					product.getTag()
			});	
			// image
			this.jdbcTemplate.update(sqlDeleteProductImages, new Object[] {product.getId()});
			int imageIndex = 1;
			for (String addImage : product.getImages()) {
				
				this.jdbcTemplate.update(sqlInsertProductImages, new Object[] { 
						product.getId(),					
						addImage,
						imageIndex
				});
				imageIndex++;				
			}
			// special_price
			this.jdbcTemplate.update(sqlDeleteProductSpecial, new Object[] {product.getId()});
			int pricePriority = 0;
			for (BigDecimal specialPrice : product.getSpecialPrices()) {
				
				
				this.jdbcTemplate.update(sqlInsertProductSpecial, new Object[] { 
							product.getId(),					
							1,
							pricePriority,
							specialPrice//,
							//DateTimeUtils.defaultFormatStringToDate("01.01.0001"),
							//DateTimeUtils.defaultFormatStringToDate("01.09.2020")
					});
					pricePriority++;
				 
				imageIndex++;				
			}	
			// category
			this.jdbcTemplate.update(sqlDeleteProductCategory, new Object[] {product.getId()});
			for (CategoryDataRaw category : product.getCategories()) {
				
				CategoryDataRaw newCategory = convertProductCategory(product, category);
				if (newCategory != null && newCategory.getId() > 0) {
					
					Integer countId = this.jdbcTemplate.queryForObject(sqlSelectCountProductCategory,
					        new Object[]{product.getId(), newCategory.getId()},
					        new RowMapper<Integer>() {
								@Override
					            public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
					            	return rs.getInt("COUNT_ID");
					            }
					        });
					if (countId > 0) {
						continue;
					}					
					this.jdbcTemplate.update(sqlInsertProductCategory, new Object[] { 
							product.getId(),					
							newCategory.getId(),
							category.getMainCategory()
					});
				} else {
					logger.debug("category is orpheous:{},{},{},{},{}", productIndex, category.getId(), category.getName(), product.getId(), product.getName());
				}
			}
			// layout
			this.jdbcTemplate.update(sqlDeleteProductLayout, new Object[] {product.getId()});
			this.jdbcTemplate.update(sqlInsertProductLayout, new Object[] {product.getId()});			
			this.jdbcTemplate.update(sqlDeleteProductStore, new Object[] {product.getId()});
			this.jdbcTemplate.update(sqlInsertProductStore, new Object[] {product.getId()});
			
			// seo			
			this.jdbcTemplate.update(sqlDeleteSeoUrl, new Object[] {0, 2, product.getSeoKey()});
			this.jdbcTemplate.update(sqlInsertSeoUrl, new Object[] {0, 2, product.getSeoKey(), product.getSeoUrl()});
					
			logger.debug("product.finished:{},{}", product.getId(), product.getName());
			productIndex++;
		}
		logger.debug("product.insert.end");
	}
	
	protected ProductDataRaw setSeo(ProductDataRaw product) {
		final String sqlSelectOldProductsSeo = "SELECT * FROM p326995_pm_test2.old_url_alias where query = ?";
		String seoKey = "product_id=" + product.getId();
		String seoUrl = "";
		
		List<String> oldProductSeos = this.jdbcTemplate.query(sqlSelectOldProductsSeo,
				new Object[]{seoKey},				
				new RowMapper<String>() {
					@Override
		            public String mapRow(ResultSet rs, int rowNum) throws SQLException {
						String keyword = rs.getString("keyword");		                             		                     		                             
		                return keyword;
		            }
		        });
		
		if (oldProductSeos.size() > 0) {
			seoUrl = oldProductSeos.get(0);
		} 
		product.setSeoKey(seoKey);
		product.setSeoUrl(seoUrl);
		
		return product;
	}

	private CategoryDataRaw convertProductCategory(ProductDataRaw product, CategoryDataRaw oldCategory) {
		
		Map<String, String> transformer = new HashMap<String, String>();
		transformer.put("Автокормушки", "Кошкам и собакам");
		transformer.put("Автопоилки", "Кошкам и собакам");
		transformer.put("Беспроводные GSM сигнализации Ajax", "Беспроводные сигнализации Ajax");
		transformer.put("Беспроводные GSM сигнализации Sapsan", "Беспроводные сигнализации Sapsan");
		transformer.put("Беспроводные датчики для GSM сигнализаций", "Беспроводные датчики для сигнализаций");
		
		transformer.put("Газовые баллоны (на резьбе)", "Топливо");
		transformer.put("Газовые баллоны (цанговые)", "Топливо");
		transformer.put("Городские рюкзаки", "Рюкзаки и сумки");
		transformer.put("Для автомобиля", "Автомобиль");
		transformer.put("Для безопасности", "Безопасность");
		transformer.put("Для детей", "Детям");		
		transformer.put("Для дачи", "ДОМ, ДАЧА, ОФИС");
		transformer.put("Для детей и родителей", "Детям и родителям");
		transformer.put("Для дома", "ДОМ, ДАЧА, ОФИС");
		transformer.put("Для здоровья", "Здоровье");
		transformer.put("Для кошки и собаки", "Кошкам и собакам");
		transformer.put("Для охоты и рыбалки", "ОХОТА, РЫБАЛКА, ТУРИЗМ");
		transformer.put("Для путешествий", "ОХОТА, РЫБАЛКА, ТУРИЗМ");
		transformer.put("Детекторы газа, дыма, пожара", "Автономные извещатели");
		transformer.put("Жидкое топливо", "Топливо");
		transformer.put("Отпугиватели клещей и других насекомых", "Отпугиватели клещей, тараканов и других насекомых");
		transformer.put("Отпугиватели крыс и мышей", "Отпугиватели грызунов");
		transformer.put("Отпугиватели тараканов, клопов", "Отпугиватели клещей, тараканов и других насекомых");
		transformer.put("Проводные GSM сигнализации", "Проводные сигнализации");
		transformer.put("Пропановые громпушки", "Пропановые пушки");
		transformer.put("Термокружки и термосы", "Термосы");
		
		final String sqlSelectCategoryByName = "SELECT * FROM p326995_pm_test2.oc_category_description where language_id=2 and name = ?";		
		List<CategoryDataRaw> newCategories = this.jdbcTemplate.query(sqlSelectCategoryByName,
				new Object[]{oldCategory.getName().trim()},				
				new RowMapper<CategoryDataRaw>() {
					@Override
		            public CategoryDataRaw mapRow(ResultSet rs, int rowNum) throws SQLException {
						CategoryDataRaw newCategory = new CategoryDataRaw();			                
						newCategory.setId(rs.getInt("category_id"));
						newCategory.setName(rs.getString("name"));
		   	         		                     		                             
		                return newCategory;
		            }
		        });
		if (newCategories.size() == 0) {
			String newCategoryName = transformer.get(oldCategory.getName().trim());
			if (newCategoryName == null) {
				return null;
			}					
			newCategories = this.jdbcTemplate.query(sqlSelectCategoryByName,
					new Object[]{newCategoryName},				
					new RowMapper<CategoryDataRaw>() {
						@Override
			            public CategoryDataRaw mapRow(ResultSet rs, int rowNum) throws SQLException {
							CategoryDataRaw newCategory = new CategoryDataRaw();			                
							newCategory.setId(rs.getInt("category_id"));
							newCategory.setName(rs.getString("name"));	
							newCategory.setMainCategory(1);							
			                return newCategory;
			            }
			        });
			if (newCategories.size() == 0) {
				return null;
			}
			return newCategories.get(0);
		} else {
			return newCategories.get(0);
		}
	}

	private ProductDataRaw getProductDescription(ProductDataRaw inProduct) {
		final String sqlSelectOldProductDescription = "SELECT * FROM p326995_pm_test2.old_product_description"
				+ "  WHERE product_id = ? AND language_id = 4";
		
		ProductDataRaw result = inProduct;		
		
		ProductDataRaw detailProduct = this.jdbcTemplate.queryForObject(sqlSelectOldProductDescription,
		        new Object[]{inProduct.getId()},
		        new RowMapper<ProductDataRaw>() {
					@Override
		            public ProductDataRaw mapRow(ResultSet rs, int rowNum) throws SQLException {
						ProductDataRaw tmpProduct = new ProductDataRaw();		            			
		                tmpProduct.setName(rs.getString("name"));
		                tmpProduct.setDescription(rs.getString("description"));
		                tmpProduct.setMetaDescription(rs.getString("meta_description"));
		                tmpProduct.setMetaKeyword(rs.getString("meta_keyword"));
		                tmpProduct.setTag(rs.getString("tag"));	
		                return tmpProduct;
		            }
		        });
		inProduct.setName(detailProduct.getName());
		inProduct.setDescription(detailProduct.getDescription());
		inProduct.setMetaDescription(detailProduct.getMetaDescription());
		inProduct.setMetaKeyword(detailProduct.getMetaKeyword());
		inProduct.setTag(detailProduct.getTag());				
		return result;
	}
	
	private List<String> getProductImages(ProductDataRaw inProduct) {

		final String sqlSelectOldProductImages = "SELECT * FROM p326995_pm_test2.old_product_image"
				+ "  WHERE product_id = ?"
				+ "  ORDER BY sort_order";
			
		List<String> oldProductImages = this.jdbcTemplate.query(sqlSelectOldProductImages,
				new Object[]{inProduct.getId()},				
				new RowMapper<String>() {
					@Override
		            public String mapRow(ResultSet rs, int rowNum) throws SQLException {
						String image = rs.getString("image");
						image = image.replace("data", "catalog/products");
		                             		                     		                             
		                return image;
		            }
		        });
		return oldProductImages;
	}
	
	private List<BigDecimal> getProductSpecialPrices(ProductDataRaw inProduct) {

		final String sqlSelectOldProductSpecialPrices = "SELECT * FROM p326995_pm_test2.old_product_special"
				+ "  WHERE product_id = ?"
				+ "  ORDER BY priority";
			
		List<BigDecimal> oldProductSpecialPrices = this.jdbcTemplate.query(sqlSelectOldProductSpecialPrices,
				new Object[]{inProduct.getId()},				
				new RowMapper<BigDecimal>() {
					@Override
		            public BigDecimal mapRow(ResultSet rs, int rowNum) throws SQLException {
						BigDecimal price = rs.getBigDecimal("price");
						
		                             		                     		                             
		                return price;
		            }
		        });
		return oldProductSpecialPrices;
	}
	
	protected Set<CategoryDataRaw> getProductCategories(ProductDataRaw inProduct) {

		final String sqlSelectOldProductCategories = "select pc.*, c.name \r\n"
				+ "  from old_product_to_category pc, old_category_description c \r\n" + 
				"    where pc.category_id = c.category_id and pc.product_id  = ? and c.language_id = 4 ";
			
		List<CategoryDataRaw> oldProductCategories = this.jdbcTemplate.query(sqlSelectOldProductCategories,
				new Object[]{inProduct.getId()},				
				new RowMapper<CategoryDataRaw>() {
					@Override
		            public CategoryDataRaw mapRow(ResultSet rs, int rowNum) throws SQLException {
						CategoryDataRaw category = new CategoryDataRaw();
						category.setId(rs.getInt("category_id"));
						category.setName(rs.getString("name"));
						category.setMainCategory(rs.getInt("main_category"));                       
		                return category;
		            }
		        });
		return new HashSet<CategoryDataRaw>(oldProductCategories); 
	}
}
