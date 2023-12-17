package ru.sir.richard.boss.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import ru.sir.richard.boss.dao.AnyDaoImpl;
import ru.sir.richard.boss.dao.WikiDao;
import ru.sir.richard.boss.model.data.Product;
import ru.sir.richard.boss.model.data.ProductCategory;
import ru.sir.richard.boss.model.utils.DateTimeUtils;
import ru.sir.richard.boss.model.utils.NumberUtils;

@Service("yandexMarketService")
public class YandexMarketServiceImpl extends AnyDaoImpl implements YandexMarketService {
	
	private final Logger logger = LoggerFactory.getLogger(YandexMarketServiceImpl.class);
	
	public static final String FTP_PATH = "/www/pribormaster.ru/download/yandex_market/";
	public static final String YML_FILE_PATH = "c:\\business\\www\\pribormaster.ru\\yandex.market\\yandex-market-4.xml";
	public static final String EXCEL_FILE_PATH = "c:\\business\\www\\pribormaster.ru\\yandex.market\\marketplace-stock-1.xlsx";
			
	@Autowired
	private WikiDao wikiDao;

	@Override
	public void run() {
		logger.debug("run(): {}", "start");
		wikiDao.init();
		List<ProductCategory> productCategories = getProductCategories(); 
		List<Product> products = getProducts(productCategories);
		exportYml(productCategories, products);
		//exportStock(productCategories, products);
		
		//upload2ftp();		
		logger.debug("run(): {}", "finish");		
	}

	/*
	private void upload2ftp() {	
				
		FTPClient ftp = new FTPClient();
        ftp.addProtocolCommandListener(new PrintCommandListener(new PrintWriter(System.out)));

        try {
			ftp.connect("p326995.ftp.ihc.ru");
			int reply = ftp.getReplyCode();
	        if (!FTPReply.isPositiveCompletion(reply)) {
	            ftp.disconnect();
	            throw new IOException("Exception in connecting to FTP Server");
	        }

	        ftp.login("p326995", "pnyyuR7UJs");
	        ftp.enterLocalPassiveMode();
	        
	        logger.debug("login: {}", "success");	        
	        
	        FTPFile[] files = ftp.listFiles(FTP_PATH);	        
	        Collection<String> cfiles = Arrays.stream(files).map(FTPFile::getName).collect(Collectors.toList());	        
	        logger.debug("cfiles before: {}", cfiles);	        	        
	        
	        File initialFile = new File(YML_FILE_PATH);	        
	        ftp.storeFile(FTP_PATH + "yandex-market-3.xml", new FileInputStream(initialFile));
	        
	        files = ftp.listFiles(FTP_PATH);	        
	        cfiles = Arrays.stream(files).map(FTPFile::getName).collect(Collectors.toList());	        
	        logger.debug("cfiles after: {}", cfiles);	        
				        
	        ftp.disconnect();
	        logger.debug("disconnect: {}", "success");	        
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}        
	}
	*/
	
	private ProductCategory findProductCategory(List<ProductCategory> productCategories, int productCategoryId) {
		for (ProductCategory productCategory : productCategories) {
			if (productCategory.getId() == productCategoryId) {
				return productCategory;
			} 
		}
		return null;
	}
		
	private String getAtomSeoUrl(boolean isCategory, int objectId) {
		//SELECT * FROM p326995_pm.oc_seo_url where query in ('category_id=18', 'category_id=20', 'category_id=36', 'product_id=28');
		
		String queryObject = isCategory ? "category_id=" + objectId : "product_id=" + objectId;				
		final String sqlSelectSeoUrl = "SELECT * FROM oc_seo_url where query = ?";				
		List<String> seoUrls = this.jdbcTemplate.query(sqlSelectSeoUrl,
				new Object[]{queryObject},				
				new RowMapper<String>() {
					@Override
		            public String mapRow(ResultSet rs, int rowNum) throws SQLException {
						return rs.getString("keyword");
		            }
		        });		
		if (seoUrls == null || seoUrls.size() == 0) {
			return "";
		} else {
			return seoUrls.get(0);
		}
	}
	
	private String getProductSeoUrl(int productId) {
		return getAtomSeoUrl(false, productId).trim();
	}
	
	private String getCategorySeoUrl(int productCategoryId, List<ProductCategory> productCategories, String priorAtomSeoUrl) {
		// https://pribormaster.ru/katalog/borba-s-vreditelyami/otpugivateli-ptits/professionalnye-bioakusticheskie-pribory/
		ProductCategory productCategory = findProductCategory(productCategories, productCategoryId);
		String atomSeoUrl = getAtomSeoUrl(true, productCategoryId);
		if (StringUtils.isNoneEmpty(priorAtomSeoUrl)) {
			atomSeoUrl = atomSeoUrl + "/" + priorAtomSeoUrl;
		}
		if (productCategory.getParentId() > 0) {
			ProductCategory parentCategory = findProductCategory(productCategories, productCategory.getParentId());
			if (parentCategory == null) {
				return atomSeoUrl.trim();	
			}			
			String tAtomSeoUrl = getCategorySeoUrl(parentCategory.getId(), productCategories, atomSeoUrl);
			atomSeoUrl = tAtomSeoUrl;
		}
		return atomSeoUrl.trim();
	}
	
	private List<ProductCategory> getProductCategories() {
		
		String selectedItems = getSettingValue("feed_yandex_market_categories");
		
		final String sqlSelectCategories = "SELECT cd.name, c.category_id, c.parent_id FROM oc_category c\r\n "
				+ " LEFT JOIN oc_category_description cd ON (c.category_id = cd.category_id)\r\n"
				+ " LEFT JOIN oc_category_to_store c2s ON (c.category_id = c2s.category_id)\r\n"
				+ " WHERE cd.language_id = 2 \r\n"
				+ " AND c2s.store_id = 0\r\n"
				+ " AND c.status = '1' \r\n "
				+ " AND c.sort_order <> '-1' \r\n"
				+ "AND c.category_id in (" + selectedItems + ")";
		
		logger.debug("exportYml.sqlSelectCategories: {}", sqlSelectCategories);
		
		List<ProductCategory> productCategories = this.jdbcTemplate.query(sqlSelectCategories,
				new RowMapper<ProductCategory>() {
			
					@Override
		            public ProductCategory mapRow(ResultSet rs, int rowNum) throws SQLException {
						ProductCategory productCategory = new ProductCategory();			                
						productCategory.setId(rs.getInt("CATEGORY_ID"));
						productCategory.setParentId(rs.getInt("PARENT_ID"));
						productCategory.setName(rs.getString("NAME"));						
		                return productCategory;
		            }
		        });
		
		for (ProductCategory productCategory : productCategories) {			
			String categoryUrl = getCategorySeoUrl(productCategory.getId(), productCategories, "");
			productCategory.setSeoKey(categoryUrl);			
			logger.debug("categoryUrl: {}", categoryUrl);
		}		
		return productCategories;		
	}
	
	private String getYmlSku(int productId, String sku) {
		String result = "";
		if (StringUtils.isEmpty(sku)) {
			result = String.valueOf(productId);
		} else {						
			result = sku.toUpperCase().trim();
			result = StringUtils.replace(result, " ", "-");
			result = StringUtils.replace(result, "_", "-");
			result = StringUtils.replace(result, "+", "-PLUS");
		}
		return result;
		
	}
	
	private String getSqlTextProducts() {
		String selectedItems = getSettingValue("feed_yandex_market_categories");

		//String exceptProductIds = "54, 55, 776, 921, 987";
		String inProductIds = "997,96";
		
		final String sqlSelectProducts = "SELECT p.*, pd.name, pd.description, m.name AS manufacturer, m.country_origin, p2c.category_id, p.price AS price,\r\n" + 
				"(SELECT ps.price FROM oc_product_special ps \r\n" + 
				"WHERE ps.product_id = p.product_id \r\n" + 
				"AND ps.customer_group_id = 1 \r\n" + 
				"AND ((ps.date_start = '0000-00-00' OR ps.date_start < NOW()) \r\n" + 
				"AND (ps.date_end = '0000-00-00' OR ps.date_end > NOW())) \r\n" + 
				"ORDER BY ps.priority ASC, ps.price ASC LIMIT 1) AS special \r\n" + 
				"FROM oc_product p \r\n" + 
				"JOIN oc_product_to_category AS p2c ON (p.product_id = p2c.product_id) \r\n" + 
				"LEFT JOIN oc_manufacturer m ON (p.manufacturer_id = m.manufacturer_id) \r\n" + 
				"LEFT JOIN oc_product_description pd ON (p.product_id = pd.product_id) \r\n" + 
				"LEFT JOIN oc_product_to_store p2s ON (p.product_id = p2s.product_id) \r\n" + 
				"WHERE p2s.store_id = 0 \r\n" + 
				"AND pd.language_id = 2 AND p.date_available <= NOW() AND p.status = '1' AND (p.stock_status_id != 1) \r\n" +
				//"AND p.quantity > 0 \r\n" +
				"AND category_id in ("+ selectedItems + ")\r\n" +
				//"AND category_id = 28\r\n" +
				"AND p.product_id IN (" + inProductIds + ")\r\n" +
				
				"GROUP BY p.product_id";
		logger.debug("exportYml.sqlSelectProducts: {}", sqlSelectProducts);
		return sqlSelectProducts;
	}
	
	private List<Product> getProducts(List<ProductCategory> productCategories) {
		 
		final String sqlSelectProducts = getSqlTextProducts();		
		List<Product> products = this.jdbcTemplate.query(sqlSelectProducts,
				new RowMapper<Product>() {			
					@Override
		            public Product mapRow(ResultSet rs, int rowNum) throws SQLException {
						/*
						# product_id, model, sku, upc, ean, jan, isbn, mpn, location, quantity, stock_status_id, image, manufacturer_id, shipping, price, points, tax_class_id, date_available, weight, weight_class_id, length, width, height, length_class_id, subtract, minimum, sort_order, status, viewed, date_added, date_modified, category_group_id, composite, name, description, manufacturer, category_id, price, special
						'28', 'ЭкоСнайпер LS-987BF', 'LS-987BF', '', '', '101', '', '', '', '100', '5', 'catalog/products/repeller/bird/uz/ls-998bf/ekosnajper-ls-987bf-1.jpg', '5', '1', '4100.0000', '0', '12', '2009-02-03', '146.40000000', '2', '0.00000000', '146.40000000', '0.00000000', '1', '1', '1', '101', '1', '7176', '2009-02-03 16:06:50', '2020-10-11 17:29:34', '103', '0', 'Ультразвуковой отпугиватель птиц ЭкоСнайпер LS-987BF', '&lt;p&gt;&lt;strong&gt;Ультразвуковой &lt;/strong&gt;отпугиватель птиц ЭкоСнайпер LS-987BF работает в &lt;strong&gt;бесшумном &lt;/strong&gt;диапазоне. Имеет &lt;strong&gt;датчик движения&lt;/strong&gt;. &lt;strong&gt;Автономен&lt;/strong&gt;, - может работать от батареек &quot;Крона&quot; 9В. Для усиления эффекта оснащен стробами. Площадь защиты до &lt;strong&gt;85 м²&lt;/strong&gt;.&lt;/p&gt;\r\n\r\n&lt;div align=&quot;center&quot;&gt;&lt;strong&gt;&lt;img alt=&quot;Ультразвуковой отпугиватель птиц LS-998BF вместе с коробкой&quot; src=&quot;/image/catalog/products/repeller/bird/uz/ls-998bf/ls-987bf-info-1.jpg&quot; style=&quot;width: 100%;&quot;&gt;&lt;/strong&gt;\r\n\r\n&lt;p&gt;&amp;nbsp;&lt;/p&gt;\r\n&lt;/div&gt;\r\n\r\n&lt;p&gt;Адаптер 220В и элементы питания в комплект не входят. Приобретаются отдельно.&lt;/p&gt;\r\n\r\n&lt;h3&gt;Преимущества&lt;/h3&gt;\r\n\r\n&lt;ul&gt;\r\n	&lt;li&gt;бесшумная работа&lt;/li&gt;\r\n	&lt;li&gt;датчик движения&lt;/li&gt;\r\n	&lt;li&gt;два режима: ультразвук + вспышки&lt;/li&gt;\r\n	&lt;li&gt;широкий угол обзора&lt;/li&gt;\r\n	&lt;li&gt;режим работы “автомат”&lt;/li&gt;\r\n	&lt;li&gt;против любых птиц&lt;/li&gt;\r\n&lt;/ul&gt;\r\n\r\n&lt;h3&gt;Как работает отпугиватель птиц LS-987BF&lt;/h3&gt;\r\n\r\n&lt;p&gt;Отпугиватель обладает комплексным характером работы, воздействует на птиц ультразвуковым сигналом и световым стробом. ЭкоСнайпер LS-987BF обнаруживает появление птиц на расстоянии до 12 метров, с углом обзора 70°.&lt;/p&gt;\r\n\r\n&lt;p&gt;В результате, отпугивание птиц происходит с помощью постоянно меняющегося ультразвукового сигнала и встроенного светового строба. Площадь защиты около 85 кв.м. Прибор устанавливается на высоте 1 - 2.5 м над землей. Устройство оснащено защитным кожухом. ЭкоСнайпер LS-987BF работает от двух стандартных батареек класса «Крона» (9 вольт).&lt;/p&gt;\r\n\r\n&lt;p&gt;В режиме ожидания заряд батареек не расходуется.&lt;/p&gt;\r\n\r\n&lt;h3&gt;Область применения&lt;/h3&gt;\r\n\r\n&lt;p&gt;Прибор применяется для защиты крыш, балконов, мансард.&lt;/p&gt;\r\n\r\n&lt;div align=&quot;center&quot;&gt;&lt;strong&gt;&lt;img alt=&quot;Ультразвуковой отпугиватель птиц LS-998BF вместе с коробкой&quot; src=&quot;/image/catalog/products/repeller/bird/uz/ls-998bf/ekosnajper-ls-987bf-8.jpg&quot; style=&quot;width: 100%;&quot;&gt;&lt;/strong&gt;\r\n\r\n&lt;p&gt;Ультразвуковой отпугиватель птиц LS-998BF вместе с коробкой&lt;/p&gt;\r\n&lt;/div&gt;\r\n\r\n&lt;h3&gt;Технические характеристики&lt;/h3&gt;\r\n&lt;ul&gt;\r\n  &lt;li&gt;Диапазон частот: 17000 Гц – 24000 Гц (постоянно меняется)&lt;/li&gt;\r\n  &lt;li&gt;Питание: две батарейки 9В, адаптер питания 220-240В AC/9В DC 200 мА (приобретается отдельно)&lt;/li&gt;\r\n  &lt;li&gt;Время задержки излучения: около 25 секунд&lt;/li&gt;\r\n  &lt;li&gt;Время прогрева прибора: около 30 секунд&lt;/li&gt;\r\n  &lt;li&gt;Зона покрытия: 85 м2, 70° по горизонтали, 9° по вертикали, дальность 12 м.&lt;/li&gt;\r\n  &lt;li&gt;Срок службы мигающего светодиода: 100 000 часов (11 лет)&lt;/li&gt;\r\n  &lt;li&gt;Размеры: 110 х 100 х 95 мм&lt;/li&gt;\r\n  &lt;li&gt;Вес: 320 г&lt;/li&gt;\r\n  &lt;li&gt;Страна производства: Тайвань&lt;/li&gt;\r\n&lt;/ul&gt;\r\n\r\n&lt;h3&gt;Комплектация&lt;/h3&gt;\r\n\r\n&lt;ul&gt;\r\n	&lt;li&gt;прибор Экоснайпер LS-987BF&lt;/li&gt;\r\n	&lt;li&gt;крепеж&lt;/li&gt;\r\n&lt;/ul&gt;\r\n\r\n&lt;p&gt;&amp;nbsp;&lt;/p&gt;\r\n&lt;div&gt;&lt;u&gt;&lt;span&gt;Производитель&lt;/span&gt;&lt;/u&gt;: &lt;strong&gt;ЭКОСНАЙПЕР&lt;/strong&gt;&lt;/div&gt;\r\n\r\n&lt;div&gt;&lt;u&gt;&lt;span&gt;Гарантия&lt;/span&gt;&lt;/u&gt;: &lt;strong&gt;12 мес.&lt;/strong&gt;&lt;/div&gt;\r\n\r\n&lt;p&gt;&amp;nbsp;&lt;/p&gt;\r\n\r\n&lt;h3&gt;Инструкция&lt;/h3&gt;\r\n&lt;p&gt;Инструкцию по отпугивателю птиц &quot;ЭКОСНАЙПЕР LS-987BF&quot; вы можете скачать&amp;nbsp;&lt;u&gt;&lt;strong&gt;&lt;a href=&quot;/archive/manual/repeller/bird/LS-987BF_instr.pdf&quot; target=&quot;_blank&quot;&gt;здесь&lt;/a&gt;&lt;/strong&gt;&lt;/u&gt;&lt;/p&gt;\r\n\r\n&lt;p&gt;&amp;nbsp;&lt;/p&gt;\r\n&lt;h3&gt;Сертификат&lt;/h3&gt;\r\n&lt;div align=&quot;center&quot;&gt;&lt;a href=&quot;/image/catalog/products/repeller/mole/ls-997mr/sert/ekosnajper-sert-1.pdf&quot; target=&quot;_blank&quot;&gt;&lt;img alt=&quot;Сертификат&quot; src=&quot;/image/catalog/products/repeller/mole/ls-997mr/sert/ekosnajper-sert-1-001-s.jpg&quot; style=&quot;width: 500px; height: 619px;&quot;&gt;&lt;/a&gt;&lt;/div&gt;\r\n', 'ЭКОСНАЙПЕР', '20', '4100.0000', NULL
						*/
						Product product = new Product();			                
						product.setId(rs.getInt("PRODUCT_ID"));
						product.setModel(rs.getString("MODEL"));
						String ymlSku = getYmlSku(rs.getInt("PRODUCT_ID"), rs.getString("SKU"));
						/*
						if (StringUtils.isEmpty(rs.getString("SKU"))) {
							product.setSku(String.valueOf(rs.getInt("PRODUCT_ID")));							
						} else {						
							product.setSku(rs.getString("SKU").toUpperCase());
						}
						*/
						product.setSku(ymlSku);						
						product.setName(rs.getString("NAME"));		
						
						//product.setPrice(rs.getBigDecimal("PRICE"));
						
						product.setPrice(wikiDao.getProductById(product.getId()).getPrice());
						product.setQuantity(rs.getInt("QUANTITY"));
						product.getStore().setMinimum(rs.getInt("MINIMUM"));
						
						product.getStore().setManufacturer(rs.getString("MANUFACTURER"));
						product.getStore().setCountryOrigin(rs.getString("COUNTRY_ORIGIN"));
						
						String pictureCashUrl = rs.getString("IMAGE");
						pictureCashUrl = rs.getString("IMAGE").replace(".jpg", "-600x600.jpg");						
						String pictureUrl = "https://pribormaster.ru/image/cache/" + pictureCashUrl;
								
						product.getStore().setPicture(pictureUrl);
				
						/*
						ProductCategory productCategory = new ProductCategory();
						productCategory.setId(rs.getInt("CATEGORY_ID"));
						product.setCategory(productCategory);
						*/
						
						ProductCategory productCategory = findProductCategory(productCategories, rs.getInt("CATEGORY_ID"));
						product.setCategory(productCategory);
						
						String categoryUrl = productCategory.getSeoKey();						
						String productUrl = "https://pribormaster.ru/" + categoryUrl + "/" + getProductSeoUrl(product.getId());
						logger.debug("productUrl: {}", productUrl);
						product.setSeoUrl(productUrl);
		        
				        String htmlDescription = rs.getString("DESCRIPTION");				        
				        htmlDescription = StringUtils.replace(htmlDescription, "&lt;/", "</");
				        htmlDescription = StringUtils.replace(htmlDescription, "&lt;", "<").replaceAll("&gt;", ">");
				        htmlDescription = StringUtils.replace(htmlDescription, "&amp;nbsp;", " ");
				        
				        String description = Jsoup.clean(htmlDescription, new Whitelist());
				        if (description.length() >= 3000) {
				        	description = description.substring(0, 2999);
				        }
				        product.getStore().setDescription(description);	
				        
				        product.getStore().setLengthClassId(rs.getInt("LENGTH_CLASS_ID"));
				        // СМ, KG			        
				        
				        // 1 cm 2 mm
				        if (rs.getInt("LENGTH_CLASS_ID") == 1) {
				        	// cm
				        	product.getStore().setLength(rs.getInt("LENGTH") <= 0 ? 10 : rs.getInt("LENGTH"));
							product.getStore().setWidth(rs.getInt("WIDTH") <= 0 ? 10 : rs.getInt("WIDTH"));
							product.getStore().setHeight(rs.getInt("HEIGHT") <= 0 ? 10 : rs.getInt("HEIGHT"));
				        } else {
				        	// mm
				        	product.getStore().setLength(rs.getInt("LENGTH") <= 0 ? 10 : rs.getInt("LENGTH") / 10);
							product.getStore().setWidth(rs.getInt("WIDTH") <= 0 ? 10 : rs.getInt("WIDTH") / 10);
							product.getStore().setHeight(rs.getInt("HEIGHT") <= 0 ? 10 : rs.getInt("HEIGHT") / 10);
				        }
				        //1 kg 2 g
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

		                return product;
		            }
		        });
		
		return products;
	}
	
	private String getYmlProductDimensions(Product product) {
		// <dimensions>11/10/9</dimensions>
		//длина, ширина, высота
		int l = product.getStore().getLength() > 0 ? product.getStore().getLength() : 10;
		int w = product.getStore().getWidth() > 0 ? product.getStore().getWidth() : 10;
		int h = product.getStore().getHeight() > 0 ? product.getStore().getHeight() : 10;
		
		return l + "/" + w + "/" + h;
	}
	
	private String getYmlProductWeight(Product product) {
		//<weight>0.3</weight>
		//Вес товара в килограммах с учетом упаковки.
		return NumberUtils.formatNumber(product.getStore().getWeight(), "##0.0").replace(",", ".");
	}	
	

	private void exportStock(List<ProductCategory> productCategories, List<Product> products) {
		
		List<Product> stockProducts = new ArrayList<Product>();
		for (Product product : products) {
			/*
			 SupplierStockProduct stockProduct = wikiDao.supplierStockProductFindByProductId(product.getId());
			 if (stockProduct.getProduct().getStockQuantity() <= 0) {
				 continue;
			 }
			 
			
			 product.setQuantity(stockProduct.getProduct().getStockQuantity());
			 */
			 stockProducts.add(product);
		}
		exportStockToExcel(stockProducts);
	}
	
	private void exportStockToExcel(List<Product> products) {
		
		XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Остатки");
         
        int rowCount = -1;
        
        Row row;
        Cell cell;
        int columnCount;
        
        
        row = sheet.createRow(++rowCount);
        columnCount = -1; 
    	cell = row.createCell(++columnCount);
    	cell.setCellValue("Комментарий");
    	cell = row.createCell(++columnCount);
    	cell.setCellValue("Ваш SKU");
    	cell = row.createCell(++columnCount);
    	cell.setCellValue("Название товара");
    	cell = row.createCell(++columnCount);
    	cell.setCellValue("Доступное количество товара");
    	
    	row = sheet.createRow(++rowCount);
        columnCount = -1; 
    	cell = row.createCell(++columnCount);
    	cell.setCellValue("");
    	cell = row.createCell(++columnCount);
    	cell.setCellValue("Уникальный идентификатор товара. Обязательное поле.");
    	cell = row.createCell(++columnCount);
    	cell.setCellValue("По схеме: тип товара + бренд или производитель + модель + отличительные характеристики.");
    	cell = row.createCell(++columnCount);
    	cell.setCellValue("Общее количество товара, доступное для продажи на маркетплейсе и зарезервированное под заказы. Обязательное поле.");
                    
        for (Product product : products) {
        	row = sheet.createRow(++rowCount);
        	columnCount = -1;        	
        	
        	cell = row.createCell(++columnCount);
        	cell.setCellValue("");
        	cell = row.createCell(++columnCount);
        	cell.setCellValue(product.getSku());
        	cell = row.createCell(++columnCount);
        	cell.setCellValue("");        	
        	cell = row.createCell(++columnCount);
        	cell.setCellValue(product.getQuantity());
        	
        }
         
        /*
        for (Object[] aBook : bookData) {
            Row row = sheet.createRow(++rowCount);
             
            int columnCount = 0;
             
            for (Object field : aBook) {
                Cell cell = row.createCell(++columnCount);
                if (field instanceof String) {
                    cell.setCellValue((String) field);
                } else if (field instanceof Integer) {
                    cell.setCellValue((Integer) field);
                }
            }
             
        }
        */
         
		FileOutputStream outputStream;
		try {
			outputStream = new FileOutputStream(EXCEL_FILE_PATH);
			workbook.write(outputStream);
			workbook.close();
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}	


	private void exportYml(List<ProductCategory> productCategories, List<Product> products) {
		
		try {
			 
            DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance(); 
            DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();
 
            Document document = documentBuilder.newDocument();
 
            // root element
            Element root = document.createElement("yml_catalog");
            document.appendChild(root);
            
            Attr attrRootDate = document.createAttribute("date");
            attrRootDate.setValue(DateTimeUtils.formatDate(new Date(), "yyyy-MM-dd HH:mm"));
            root.setAttributeNode(attrRootDate);
 
            // employee element
            Element shop = document.createElement("shop"); 
            root.appendChild(shop);
            
            Element shopName = document.createElement("name");
            shopName.appendChild(document.createTextNode("приборМАСТЕР"));            
            shop.appendChild(shopName);
            
            Element shopCompany = document.createElement("company");
            shopCompany.appendChild(document.createTextNode("ИП ФЕДОРОВ АЛЕКСЕЙ АНАТОЛЬЕВИЧ"));            
            shop.appendChild(shopCompany);
            
            Element shopUrl = document.createElement("url");
            shopUrl.appendChild(document.createTextNode("https://pribormaster.ru"));            
            shop.appendChild(shopUrl);
            
            Element currencies = document.createElement("currencies");
            shop.appendChild(currencies);
            
            Element currencyRur = document.createElement("currency");            
            Attr attrCurrencyRurId = document.createAttribute("id");
            attrCurrencyRurId.setValue("RUR");
            currencyRur.setAttributeNode(attrCurrencyRurId);
            Attr attrCurrencyRurRate = document.createAttribute("rate");
            attrCurrencyRurRate.setValue("1");
            currencyRur.setAttributeNode(attrCurrencyRurRate);
                        
            Element deliveryOptions = document.createElement("delivery-options");
            shop.appendChild(deliveryOptions);
          
            Element deliveryOption = document.createElement("option");            
            Attr deliveryOptionCost = document.createAttribute("cost");
            deliveryOptionCost.setValue("300");
            deliveryOption.setAttributeNode(deliveryOptionCost);          
            Attr deliveryOptionDays = document.createAttribute("days");
            deliveryOptionDays.setValue("1-2");
            deliveryOption.setAttributeNode(deliveryOptionDays);
            deliveryOptions.appendChild(deliveryOption);     
            
            Element categories = document.createElement("categories");
            shop.appendChild(categories);
            
            for (ProductCategory productCategory : productCategories) {            	
            	
            	Element categoryElement = document.createElement("category");            
                Attr attrCategoryElementId = document.createAttribute("id");
                attrCategoryElementId.setValue(String.valueOf(productCategory.getId()));
                categoryElement.setAttributeNode(attrCategoryElementId);
               
                if (productCategory.getParentId() > 0) {
                	 Attr attrCategoryElementParentId = document.createAttribute("parentId");
                	 attrCategoryElementParentId.setValue(String.valueOf(productCategory.getParentId()));
                     categoryElement.setAttributeNode(attrCategoryElementParentId);
                }
                categoryElement.appendChild(document.createTextNode(productCategory.getName()));
                categories.appendChild(categoryElement);
    		}            
            
            Element offers = document.createElement("offers");
            shop.appendChild(offers);
            
            for (Product product : products) {
            	
                Element offerElement = document.createElement("offer");            
                Attr attrOfferElementId = document.createAttribute("id");
                attrOfferElementId.setValue(String.valueOf(product.getId()));
                offerElement.setAttributeNode(attrOfferElementId); 

                Attr attrOfferElementType = document.createAttribute("type");
                attrOfferElementType.setValue("vendor.model");
                offerElement.setAttributeNode(attrOfferElementType);

                Attr attrOfferElementAvalable = document.createAttribute("available");
                attrOfferElementAvalable.setValue("true");
                offerElement.setAttributeNode(attrOfferElementAvalable);
                
                Element offerElementUrl = document.createElement("url"); 
                offerElementUrl.appendChild(document.createTextNode(product.getSeoUrl()));
                offerElement.appendChild(offerElementUrl);
                
                Element shopSkuElementUrl = document.createElement("shop-sku"); 
                shopSkuElementUrl.appendChild(document.createTextNode(product.getSku()));
                offerElement.appendChild(shopSkuElementUrl);
                
                Element offerElementPrice = document.createElement("price");                
                String sPrice = NumberUtils.formatNumber(product.getPrice(), "##0.00").replace(",", ".");                
                offerElementPrice.appendChild(document.createTextNode(sPrice));
                offerElement.appendChild(offerElementPrice);
                
                Element offerElementCurreny = document.createElement("currencyId"); 
                offerElementCurreny.appendChild(document.createTextNode("RUR"));
                offerElement.appendChild(offerElementCurreny);
                
                Element offerElementCategory = document.createElement("categoryId"); 
                offerElementCategory.appendChild(document.createTextNode(String.valueOf(product.getCategory().getId())));
                offerElement.appendChild(offerElementCategory);
                // TODO type=Own
                
                Element offerElementPicture = document.createElement("picture"); 
                offerElementPicture.appendChild(document.createTextNode(product.getStore().getPicture()));
                offerElement.appendChild(offerElementPicture);
                
                Element offerElementDelivery = document.createElement("delivery"); 
                offerElementDelivery.appendChild(document.createTextNode("true"));
                offerElement.appendChild(offerElementDelivery);
          
                Element offerElementTypeVendor = document.createElement("vendor"); 
                offerElementTypeVendor.appendChild(document.createTextNode(product.getStore().getManufacturer()));
                offerElement.appendChild(offerElementTypeVendor);
                
                Element offerElementModel = document.createElement("model"); 
                offerElementModel.appendChild(document.createTextNode(product.getModel()));
                offerElement.appendChild(offerElementModel);
                
                Element offerElementCount = document.createElement("count"); 
                offerElementCount.appendChild(document.createTextNode(String.valueOf(product.getQuantity())));
                offerElement.appendChild(offerElementCount);
                
                Element offerElementMonimum = document.createElement("min-delivery-pieces"); 
                offerElementMonimum.appendChild(document.createTextNode(String.valueOf(product.getStore().getMinimum())));
                offerElement.appendChild(offerElementMonimum);     
                   
                Element offerElementManufacturer = document.createElement("manufacturer"); 
                offerElementManufacturer.appendChild(document.createTextNode(product.getStore().getManufacturer()));
                offerElement.appendChild(offerElementManufacturer);
               
                Element offerElementManufacturerCountry = document.createElement("country_of_origin"); 
                offerElementManufacturerCountry.appendChild(document.createTextNode(product.getStore().getCountryOrigin()));
                offerElement.appendChild(offerElementManufacturerCountry);
                                            
                Element offerElementDescription = document.createElement("description"); 
                offerElementDescription.appendChild(document.createTextNode(product.getStore().getDescription()));
                offerElement.appendChild(offerElementDescription);
                
                Element offerElementServiceLifeDays = document.createElement("service-life-days"); 
                offerElementServiceLifeDays.appendChild(document.createTextNode("P5Y"));
                offerElement.appendChild(offerElementServiceLifeDays);
                
                Element offerElementWarrantyDays = document.createElement("warranty-days"); 
                offerElementWarrantyDays.appendChild(document.createTextNode("P1Y"));
                offerElement.appendChild(offerElementWarrantyDays);
                
                String ymlProductDimensions = getYmlProductDimensions(product);
                Element offerElementDimensions = document.createElement("dimensions"); 
                offerElementDimensions.appendChild(document.createTextNode(ymlProductDimensions));
                offerElement.appendChild(offerElementDimensions);
                
                String ymlProductWeight = getYmlProductWeight(product);
                Element offerElementWeight = document.createElement("weight"); 
                offerElementWeight.appendChild(document.createTextNode(ymlProductWeight));
                offerElement.appendChild(offerElementWeight);
                                
                Element offerElementLeadTime = document.createElement("leadtime"); 
                offerElementLeadTime.appendChild(document.createTextNode("2"));
                offerElement.appendChild(offerElementLeadTime);
                
                Element offerElementDeliveryWeekDays = document.createElement("delivery-weekdays"); 
                offerElement.appendChild(offerElementDeliveryWeekDays);
                
                Element offerElementDeliveryWeekDay1 = document.createElement("delivery-weekday"); 
                offerElementDeliveryWeekDay1.appendChild(document.createTextNode("MONDAY"));
                offerElementDeliveryWeekDays.appendChild(offerElementDeliveryWeekDay1);
                
                Element offerElementDeliveryWeekDay2 = document.createElement("delivery-weekday");
                offerElementDeliveryWeekDay2.appendChild(document.createTextNode("TUESDAY"));
                offerElementDeliveryWeekDays.appendChild(offerElementDeliveryWeekDay2);
                
                Element offerElementDeliveryWeekDay3 = document.createElement("delivery-weekday");
                offerElementDeliveryWeekDay3.appendChild(document.createTextNode("WEDNESDAY"));
                offerElementDeliveryWeekDays.appendChild(offerElementDeliveryWeekDay3);
                
                Element offerElementDeliveryWeekDay4 = document.createElement("delivery-weekday");
                offerElementDeliveryWeekDay4.appendChild(document.createTextNode("THURSDAY"));
                offerElementDeliveryWeekDays.appendChild(offerElementDeliveryWeekDay4);
                
                Element offerElementDeliveryWeekDay5 = document.createElement("delivery-weekday");
                offerElementDeliveryWeekDay5.appendChild(document.createTextNode("FRIDAY"));
                offerElementDeliveryWeekDays.appendChild(offerElementDeliveryWeekDay5);                

                offers.appendChild(offerElement);
            }                        
                                  
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource domSource = new DOMSource(document);
            StreamResult streamResult = new StreamResult(new File(YML_FILE_PATH));
 
            document.setXmlVersion("1.0");
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            //transformer.setOutputProperty(OutputKeys.CDATA_SECTION_ELEMENTS, "yml_catalog SYSTEM");
            //transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, "shops.dtd");

            transformer.transform(domSource, streamResult);
 
            logger.debug("creating XML File: {}", "done");
            
        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (TransformerException tfe) {
            tfe.printStackTrace();
        }	
		
	}
	
	private String getSettingValue(String key) {
		//SELECT * FROM p326995_pm.oc_setting s where s.key like 'feed_yandex_market_categories';		
		final String sqlSelectSetting = "SELECT * FROM oc_setting s where s.key = ?";				
		List<String> values = this.jdbcTemplate.query(sqlSelectSetting,
				new Object[]{key},				
				new RowMapper<String>() {
					@Override
		            public String mapRow(ResultSet rs, int rowNum) throws SQLException {
						return rs.getString("value");
		            }
		        });	
		
		if (values == null || values.size() == 0) {
			return "";
		} else {
			return values.get(0).trim();
		}
	}

}
