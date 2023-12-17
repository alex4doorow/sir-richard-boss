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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.sql.DataSource;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import ru.sir.richard.boss.config.DbConfig;
import ru.sir.richard.boss.config.LoaderConfig;
import ru.sir.richard.boss.dao.WikiDao;
import ru.sir.richard.boss.model.data.Product;
import ru.sir.richard.boss.model.data.ProductCategory;
import ru.sir.richard.boss.model.types.SupplierTypes;
import ru.sir.richard.boss.model.utils.DateTimeUtils;
import ru.sir.richard.boss.utils.PricerUtils;

@Service("converterService")
public class ConverterServiceImpl implements ConverterService {
		
	private final Logger logger = LoggerFactory.getLogger(ConverterServiceImpl.class);
	
	private Connection conn = null;
	
	@Autowired  
	private WikiDao wiki;
	
	@Autowired  
	protected JdbcTemplate jdbcTemplate;
	
	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	@Override
	public void run() {
		try {
			Class.forName(DbConfig.JDBC_DRIVER);
			conn = DriverManager.getConnection(DbConfig.DB_PM_PRODUCTION_URL);
		} catch (SQLException se) {
			logger.error("SQLException:", se);
		} catch (Exception se) {
			logger.error("Exception:", se);
		}		
		
		logger.debug("run():{}", "start");
		List<Product> excelData = loadFromExcel();		
		loadProducts(excelData);
		logger.debug("run():{}", "finish");
	}
	
	
	@SuppressWarnings("deprecation")
	private List<Product> loadFromExcel() {
		HSSFWorkbook myExcelBook;
		List<Product> outputData = null;
		try {
			
			myExcelBook = new HSSFWorkbook(new FileInputStream(LoaderConfig.FILE_PATH_PRICE_4_CONVERT_SLEDOPYT_IN));
			HSSFSheet myExcelSheet = myExcelBook.getSheetAt(0);
			
			HSSFRow row;

			final int startIndex = 0;
			outputData = new ArrayList<Product>();			
		
			int cellIndex = 0;
			for (int i = startIndex; i <= myExcelSheet.getLastRowNum(); i++) {
				row = myExcelSheet.getRow(i);
				
				if (row.getCell(0) != null && row.getCell(0).getCellType() == HSSFCell.CELL_TYPE_STRING) {
					
					cellIndex = 0;
					String sku = row.getCell(cellIndex).getStringCellValue();
					
					cellIndex = 2;
					String name = row.getCell(cellIndex).getStringCellValue();
					
					cellIndex = 9;
					String sPrice;
					if (row.getCell(cellIndex).getCellType() == XSSFCell.CELL_TYPE_NUMERIC) {
						sPrice = String.valueOf(row.getCell(cellIndex).getNumericCellValue());						
					} else {
						sPrice = row.getCell(cellIndex).getStringCellValue();
					}
					BigDecimal price = PricerUtils.getPrice(sPrice);		
					
					cellIndex = 10;
					String sSupplierPrice;
					if (row.getCell(cellIndex).getCellType() == XSSFCell.CELL_TYPE_NUMERIC) {
						sSupplierPrice = String.valueOf(row.getCell(cellIndex).getNumericCellValue());						
					} else {
						sSupplierPrice = row.getCell(cellIndex).getStringCellValue();
					}
					BigDecimal supplierPrice = PricerUtils.getPrice(sSupplierPrice);
					
					logger.debug("product: {}, {}, {}, {}, {}", i, sku, name, sPrice, sSupplierPrice);
					
					Product product = new Product(i, name);
					product.setSku(sku.trim());
					product.setPrice(price);
					product.setSupplierPrice(supplierPrice);					
					outputData.add(product);
				}				
	
			}			
			myExcelBook.close();
			//logger.debug("{}", outputData);	
			
		} catch (FileNotFoundException e) {
			logger.error("FileNotFoundException:", e);
		} catch (IOException e) {
			logger.error("IOException:", e);			
		}
		return outputData;	
	}
	
	private void loadProducts(List<Product> excelData) {
		for (Product excelProduct : excelData) {
			
			Product product = createProductByExcelData(excelProduct);
			
			int productId = findPmProduct(product);
			if (productId > 0) {
				logger.info("exist product:{}", product.getSku());
				deletePmProduct(productId);			
				
			} 
			//addPmProduct(product, 220); // 220 лески
			//addPmProduct(product, 221); // 221 балансиры
			addPmProduct(product, 222); // 222 ящики и коробки
			
			
		}		
	}	
	
	private int findPmProduct(Product product) {
		final String sqlSelectProductBySku = "SELECT * FROM p326995_pm.oc_product p where p.sku = ?";
		
		List<Product> existProducts = this.jdbcTemplate.query(sqlSelectProductBySku,
				new Object[]{product.getSku()},				
				new RowMapper<Product>() {
					@Override
		            public Product mapRow(ResultSet rs, int rowNum) throws SQLException {													
						Product item = new Product(rs.getInt("PRODUCT_ID"), "");						
						item.setSku(rs.getString("SKU"));						
						return item;
		            }
		        });	
		
		if (existProducts != null && existProducts.size() > 0) {
			return existProducts.get(0).getId();
		}
		return 0;
	}
	
	private int getLastProduct(Connection conn) throws SQLException {			
		int result = 0;
		//final String sqlSelectLastInsert = "SELECT LAST_INSERT_ID() AS LAST_ID";
		
		final String sqlSelectLastInsert = "SELECT max(product_id) LAST_ID from oc_product";
			
		PreparedStatement pstmt = conn.prepareStatement(sqlSelectLastInsert);
		ResultSet resultSet = pstmt.executeQuery();		
        while (resultSet.next()) {
        	result = resultSet.getInt("LAST_ID");
        	break;
        }
        return result;
	}
	
	private void deletePmProduct(int productId) {
		logger.info("deleteProduct: {}", productId);
		
		final String sqlDeleteProductCategories = "DELETE FROM p326995_pm.oc_product_to_category WHERE product_id = ?";
		final String sqlDeleteProductYm = "DELETE FROM p326995_pm.oc_yb_offers WHERE product_id = ?";
		final String sqlDeleteProductStock = "DELETE FROM p326995_pm.sr_stock WHERE product_id = ?";
				
		final String sqlDeleteProductImages = "DELETE FROM p326995_pm.oc_product_image WHERE product_id = ?";
		final String sqlDeleteProductDescription = "DELETE FROM p326995_pm.oc_product_description WHERE product_id = ?";
		final String sqlDeleteProductDiscount = "DELETE FROM p326995_pm.oc_product_discount WHERE product_id = ?";
		final String sqlDeleteProductSpecial = "DELETE FROM p326995_pm.oc_product_special WHERE product_id = ?";		
		final String sqlDeleteProductLayout = "DELETE FROM p326995_pm.oc_product_to_layout WHERE product_id = ?";
		final String sqlDeleteProductStore = "DELETE FROM p326995_pm.oc_product_to_store WHERE product_id = ?";		
		final String sqlDeleteProductSeoUrl = "DELETE FROM p326995_pm.oc_seo_url WHERE query = ?";
		final String sqlDeleteProduct = "DELETE FROM p326995_pm.oc_product WHERE product_id = ?";
				

		PreparedStatement pstmt = null;
		try {
		
			pstmt = conn.prepareStatement(sqlDeleteProductStock);
			pstmt.setInt(1, productId);
			pstmt.executeUpdate();
			
			
			pstmt = conn.prepareStatement(sqlDeleteProductYm);
			pstmt.setInt(1, productId);
			pstmt.executeUpdate();
			
			pstmt = conn.prepareStatement(sqlDeleteProductCategories);
			pstmt.setInt(1, productId);
			pstmt.executeUpdate();
			
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
			logger.error("SQLException:", se);
		} catch (Exception se) {
			logger.error("Exception:", se);
		} 				
		
	}
			
	private void addPmProduct(Product product, int categoryId) {
		
		logger.info("addProduct: {}", product.getSku());
		
		PreparedStatement pstmt = null;		
		int pstmtIndex;		
		try {

			int productId = getLastProduct(conn);
			productId++;
			product.setId(productId);			
			
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
			pstmt.setInt(pstmtIndex++, productId);
			pstmt.setString(pstmtIndex++, product.getModel());
			pstmt.setString(pstmtIndex++, product.getStore().getJan());
			pstmt.setString(pstmtIndex++, "");
			pstmt.setString(pstmtIndex++, product.getSku());
			pstmt.setString(pstmtIndex++, "");
			pstmt.setString(pstmtIndex++, "");
			pstmt.setString(pstmtIndex++, product.getStore().getIsbn());
			pstmt.setString(pstmtIndex++, "");
			pstmt.setInt(pstmtIndex++, 12); // tax_class_id
			pstmt.setInt(pstmtIndex++, product.getQuantity());
			pstmt.setInt(pstmtIndex++, product.getStore().getStockStatusId());
			
			pstmt.setString(pstmtIndex++, product.getStore().getPicture());
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

	
			logger.info("product: {}, {}", product.getSku(), product.getId());
	
			
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
			/*
			final String sqlInsertSeoUrl = "INSERT INTO p326995_pm.oc_seo_url"
					+ " (store_id, language_id, query, keyword)"
					+ " VALUES"
					+ " (0, 2, ?, ?)";
			pstmt = conn.prepareStatement(sqlInsertSeoUrl);			
			pstmtIndex = 1;
			pstmt.setString(pstmtIndex++, "product_id=" + product.getId());
			pstmt.setString(pstmtIndex++, product.getSeoUrl());
			pstmt.executeUpdate();
			*/	
			
		} catch (SQLException se) {
			logger.error("SQLException:", se);

		} catch (Exception se) {
			logger.error("Exception:", se);

		}	
		
		final String sqlInsertProductCategory = "INSERT INTO oc_product_to_category"
				+ " (product_id, category_id, main_category)"
				+ " VALUES"
				+ " (?, ?, ?)";			
		this.jdbcTemplate.update(sqlInsertProductCategory, new Object[] { 
				product.getId(),					
				categoryId,
				1
		});
		
		// ym
		final String sqlInsertYmOffer = "INSERT INTO oc_yb_offers (product_id, shopSku, yandex_sku, supplier_stock, yandex_seller) values (?, ?, ?, ?, ?)";		
		this.jdbcTemplate.update(sqlInsertYmOffer, new Object[] { 				
				product.getId(),
				product.getSku(),
				"",
				0,
				1});
		
		// stock
		
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

	private Product createProductByExcelData(Product excelProduct) {
		Product result = new Product(excelProduct.getId(), excelProduct.getName());
		result.setSku(excelProduct.getSku());
		result.setPrice(excelProduct.getPrice());
		result.setSupplierPrice(excelProduct.getSupplierPrice());		
		result.setModel(excelProduct.getSku());		
		result.setQuantity(0);
		result.setMainSupplier(SupplierTypes.SLEDOPYT_YANDEX_MARKET_FISHING);
		
		String emptyPicturePath = "catalog/products/empty-500x500.jpg";
		result.getStore().setPicture(emptyPicturePath);		
		result.getStore().setWeight(BigDecimal.ONE);
		result.getStore().setLength(12);
		result.getStore().setWidth(12);
		result.getStore().setHeight(12);
		
		
		ProductCategory productCategory = new ProductCategory(1104, "для рыбалки");		
		result.setCategory(productCategory);		
		result.setComposite(false);
		
		result = wiki.createProductDescriptionMeta(result);
		result.getStore().setDescription(excelProduct.getName());
		result.getStore().setMetaKeyword(result.getSku());
				
		result.getStore().setJan("101");
		result.getStore().setIsbn("101");						
		result.getStore().setStockStatusId(5);
		result.getStore().setManufacturerId(80);
		result.getStore().setPoints(0);						
		result.getStore().setAvailableDate(DateTimeUtils.sysDate());		
		result.getStore().setAddedDate(DateTimeUtils.sysDate());
		result.getStore().setModifiedDate(new Date());
		
		result.getStore().setWeightClassId(1);
		result.getStore().setLengthClassId(1);
		result.getStore().setStatus(0);	
		
		result.getStore().setTag("");
		result.getStore().setMinimum(1);
		result.getStore().setSortOrder(0);

		result.setSeoUrl(excelProduct.getSku());

		return result;
	}

	

}
