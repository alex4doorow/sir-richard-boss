package ru.sir.richard.boss.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ru.sir.richard.boss.config.LoaderConfig;
import ru.sir.richard.boss.dao.AnyDaoImpl;
import ru.sir.richard.boss.dao.WikiDao;
import ru.sir.richard.boss.data.raw.ProductDataRaw;
import ru.sir.richard.boss.model.data.Product;
import ru.sir.richard.boss.model.types.CrmTypes;
import ru.sir.richard.boss.model.types.SupplierTypes;
import ru.sir.richard.boss.model.utils.FileUtils;
import ru.sir.richard.boss.utils.PricerUtils;

@Service("pricerService")
public class PricerServiceImpl extends AnyDaoImpl implements PricerService {
		
	private static final int BUFFER_SIZE = 4096;
	
	private final Logger logger = LoggerFactory.getLogger(PricerServiceImpl.class);
	
	private Map<String, Product> translator;	
	
	@Autowired
	private WikiDao wikiDao;
		
	@Override
	public void run() {
		/*
		logger.debug("run():{}", "start");
		wikiDao.init();		
		translator = PricerUtils.createTranslatorData();
		List<ProductDataRaw> raws = loadFromExcelSititek();
		saveToDbSititek(raws);			
		logger.debug("run():{}", "finish");
		*/
	}
	
	/**
     * Downloads a file from a URL
     * @param fileURL HTTP URL of the file to be downloaded
     * @param saveDir path of the directory to save the file
     * @throws IOException
     */
    private void downloadFile(String fileURL, String saveDir) throws IOException {
    	    	
        URL url = new URL(fileURL);
        HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
        httpConn.setRequestMethod("GET");        
        String loginPassword = "dealer:fLR7MRrI";
        String base64LoginPassword = Base64.getEncoder().encodeToString(loginPassword.getBytes());
        httpConn.setRequestProperty("authorization", "Basic " + base64LoginPassword);
        
        httpConn.setRequestProperty("Content-type", "application/vnd.ms-excel");
        httpConn.setRequestProperty("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9"); 
        httpConn.setRequestProperty("accept-encoding", "gzip, deflate, br");
        httpConn.setRequestProperty("accept-language", "ru,en;q=0.9");

        int responseCode = httpConn.getResponseCode();
 
        // always check HTTP response code first
        if (responseCode == HttpURLConnection.HTTP_OK) {
            String fileName = "";
            String disposition = httpConn.getHeaderField("Content-Disposition");
            String contentType = httpConn.getContentType();
            int contentLength = httpConn.getContentLength();
 
            if (disposition != null) {
                // extracts file name from header field
                int index = disposition.indexOf("filename=");
                if (index > 0) {
                    fileName = disposition.substring(index + 10,
                            disposition.length() - 1);
                }
            } else {
                // extracts file name from URL
                fileName = fileURL.substring(fileURL.lastIndexOf("/") + 1,
                        fileURL.length());
            }
 
            System.out.println("Content-Type = " + contentType);
            System.out.println("Content-Disposition = " + disposition);
            System.out.println("Content-Length = " + contentLength);
            System.out.println("fileName = " + fileName);
 
            // opens input stream from the HTTP connection
            InputStream inputStream = httpConn.getInputStream();
            String saveFilePath = saveDir + File.separator + fileName;
             
            // opens an output stream to save into file
            FileOutputStream outputStream = new FileOutputStream(saveFilePath);
 
            int bytesRead = -1;
            byte[] buffer = new byte[BUFFER_SIZE];
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
 
            outputStream.close();
            inputStream.close();
 
            System.out.println("File downloaded");
        } else {
            System.out.println("No file to download. Server replied HTTP code: " + responseCode);
        }
        httpConn.disconnect();
    }
	
	
	
	@Override
	public void runSititek() {
		
		logger.info("run(): {}, {}", SupplierTypes.SITITEK.getAnnotation(), "start");
		try {
			downloadFile("https://www.sititek.ru/dealer/" + LoaderConfig.FILE_NAME_PRICE_SITITEK_IN, LoaderConfig.DIR_PATH_PRICE_SITITEK_IN);
		} catch (IOException e) {
			logger.error("IOException:", e);			
		}
		wikiDao.init();		
		translator = PricerUtils.createSititekTranslatorData();
		List<ProductDataRaw> raws = loadFromExcelSititek();
		saveToDbSititek(raws);
		logger.info("run(): {}, {}", SupplierTypes.SITITEK.getAnnotation(), "finish");
		
	}
	
	@Override
	public void runSledopyt() {
		logger.info("run(): {}, {}", SupplierTypes.SLEDOPYT.getAnnotation(), "start");
		wikiDao.init();	
		
		Set<ProductDataRaw> raws = new HashSet<ProductDataRaw>();
		final File folder = new File(LoaderConfig.FILE_PATH_PRICE_SLEDOPYT_IN);
		Set<String> fileNames = FileUtils.listFilesForFolder(folder);
		
		for (String fileName : fileNames) {
			String fullFileName = LoaderConfig.FILE_PATH_PRICE_SLEDOPYT_IN + fileName;
			logger.debug("run(): {}, {}", SupplierTypes.SLEDOPYT.getAnnotation() + " loadFromExcel", fullFileName);
			raws = loadFromExcelSledopyt(fullFileName, raws);
			
		}
		saveToDbSledopyt(raws);		
		logger.info("run(): {}, {}", SupplierTypes.SLEDOPYT.getAnnotation(), "finish");
	}
	
	@Override
	public void runEcosniper() {
		
		logger.info("run(): {}, {}", SupplierTypes.Z1_VEK.getAnnotation(), "start");
		wikiDao.init();	
		
		List<ProductDataRaw> raws = new ArrayList<ProductDataRaw>();
		final File folder = new File(LoaderConfig.FILE_PATH_PRICE_ECOSNIPER_IN);
		Set<String> fileNames = FileUtils.listFilesForFolder(folder);
		
		for (String fileName : fileNames) {
			if (fileName.startsWith("Электроника для борьбы с вредителями Экоснайпер")) {
				
				String fullFileName = LoaderConfig.FILE_PATH_PRICE_ECOSNIPER_IN + fileName;
				logger.debug("run(): {}, {}", SupplierTypes.Z1_VEK.getAnnotation() + " loadFromExcel", fullFileName);
				raws = loadFromExcelEcosniper(fullFileName, raws);
				saveToDbEcosniper(raws);
				break;
				
			}
			
			
			
		}
		//saveToDbSledopyt(raws);		
		logger.info("run(): {}, {}", SupplierTypes.Z1_VEK.getAnnotation(), "finish");
		
	}
	
	@SuppressWarnings("deprecation")
	private List<ProductDataRaw> loadFromExcelEcosniper(String fileName, List<ProductDataRaw> productRaws) {
		
		HSSFWorkbook myExcelBook;		
		List<ProductDataRaw> outputData = null;
		try {
			myExcelBook = new HSSFWorkbook(new FileInputStream(fileName));
			HSSFSheet myExcelSheet = myExcelBook.getSheetAt(0);
			FormulaEvaluator evaluator = myExcelBook.getCreationHelper().createFormulaEvaluator();	
			
			HSSFRow row;
			String sku;
			String productName;
			String price = "";
			final int startIndex = 7;
			outputData = new ArrayList<ProductDataRaw>();
			for (int i = startIndex; i <= myExcelSheet.getLastRowNum(); i++) {
				row = myExcelSheet.getRow(i);
				ProductDataRaw raw = null;
				if (row.getCell(1) != null && row.getCell(1).getCellType() == HSSFCell.CELL_TYPE_STRING) {					
					sku = row.getCell(1).getStringCellValue().trim();					
					
					String checkString = "";					
					if (row.getCell(0).getCellType() == HSSFCell.CELL_TYPE_STRING) {
						checkString = row.getCell(0).getStringCellValue();				
					}
					if (checkString == null || StringUtils.equalsIgnoreCase(checkString.trim(), "ВНИМАНИЕ!")) {
						break;
					}					
					productName = row.getCell(2).getStringCellValue();															
					try {
						CellValue cellValue = evaluator.evaluate(row.getCell(6));
						price = String.valueOf(cellValue.getNumberValue()); 	

					} catch (java.lang.IllegalStateException e) {
						logger.error("error price: {}, {}", sku, price);						
					}					
					//logger.debug("sku: {}, {}", sku, price);
										
					if (sku.equalsIgnoreCase("GK-2C")) {							
						raw = new ProductDataRaw(productName, "0", price, "0");
						raw.setSku("GK-2C-WHITE");					
						outputData.add(raw);
						raw = new ProductDataRaw(productName, "0", price, "0");
						raw.setSku("GK-2C-BLACK");					
						outputData.add(raw);
					} else if (sku.startsWith("GP2-4A")) {
						raw = new ProductDataRaw(productName, "0", price, "0");
						raw.setSku("GP2-4A");					
						outputData.add(raw);							
					} else if (sku.startsWith("GF- 4WD")) {
						raw = new ProductDataRaw(productName, "0", price, "0");
						raw.setSku("GF-4WD");					
						outputData.add(raw);							
					} else if (sku.equalsIgnoreCase("Батарейка D типа (алкалиновая)")) {
						raw = new ProductDataRaw(productName, "0", price, "0");
						raw.setSku("SAMSUNG-PLEOMAX-LR20");					
						outputData.add(raw);							
					} else if (sku.equalsIgnoreCase("Батарейка 9В (Крона)")) {
						raw = new ProductDataRaw(productName, "0", price, "0");
						raw.setSku("VARTA-9V-KRONA");					
						outputData.add(raw);							
					} else {
						raw = new ProductDataRaw(productName, "0", price, "0");
						raw.setSku(sku);					
						outputData.add(raw);							
					}
				} 				
			}			
			myExcelBook.close();
			
		} catch (FileNotFoundException e) {
			logger.error("FileNotFoundException:", e);
		} catch (IOException e) {
			logger.error("IOException:", e);			
		}
		return outputData;			
	}
	
	private List<Product> saveToDbEcosniper(List<ProductDataRaw> productRaws) {
		List<Product> results = new ArrayList<Product>();
		if (productRaws == null) {
			return results;
		}
		for (ProductDataRaw productDataRaw : productRaws) {
			Product product = wikiDao.findProductBySku(productDataRaw.getSku());
			
			
			if (product != null) {
				BigDecimal price;
				try {
					price = PricerUtils.getPrice(productDataRaw.getPrice());
					
				} catch (java.lang.NumberFormatException e) {
					logger.error("PriceUtils.getPrice() price: <{}>, {}", productDataRaw.getPrice(), productDataRaw.getSku());
					price = BigDecimal.ZERO;
				}
				product.setPrice(price);
				//logger.debug("product: {}, {}, {}", product.getId(), product.getSku(), product.getPrice());
				wikiDao.updatePriceAndQuantityProduct(product);
			} else {				
				logger.debug("product null: {}, {}", productDataRaw.getSku(), productDataRaw.getPrice());				
			}	
							
			results.add(product);
		}
		return results;
	}	
		
	@SuppressWarnings("deprecation")
	private Set<ProductDataRaw> loadFromExcelSledopyt(String fileName, Set<ProductDataRaw> outputData) {
				
		XSSFWorkbook myExcelBook;		
		try {
			
			myExcelBook = new XSSFWorkbook(new FileInputStream(fileName));
			XSSFSheet myExcelSheet = myExcelBook.getSheetAt(0);
			
			XSSFRow row;
			String quantity;
			String productName;
			String vendorCode;
			String price;
			String supplierPrice;
			final int startIndex = 8;
			int cellIndex;
		
			for (int i = startIndex; i <= myExcelSheet.getLastRowNum(); i++) {
				row = myExcelSheet.getRow(i);
				
				ProductDataRaw raw = null;
				if (row.getCell(2) != null && row.getCell(2).getCellType() == XSSFCell.CELL_TYPE_STRING) {
					
					cellIndex = 0;
					vendorCode = row.getCell(cellIndex).getStringCellValue().trim();
					
					cellIndex = 2;
					productName = row.getCell(cellIndex).getStringCellValue().trim();
					
					cellIndex = 8;					
					if (row.getCell(cellIndex).getCellType() == XSSFCell.CELL_TYPE_NUMERIC) {
						quantity = String.valueOf(row.getCell(cellIndex).getNumericCellValue());						
					} else {
						quantity = "100";
					}		
					
					cellIndex = 9; 
					if (row.getCell(cellIndex).getCellType() == XSSFCell.CELL_TYPE_NUMERIC) {
						price = String.valueOf(row.getCell(cellIndex).getNumericCellValue());						
					} else {
						price = row.getCell(cellIndex).getStringCellValue();
					}
										
					cellIndex = 10; 
					if (row.getCell(cellIndex).getCellType() == XSSFCell.CELL_TYPE_NUMERIC) {
						supplierPrice = String.valueOf(row.getCell(cellIndex).getNumericCellValue());						
					} else {
						supplierPrice = row.getCell(cellIndex).getStringCellValue();
					}
					
					raw = new ProductDataRaw(productName, quantity, price, supplierPrice);
					raw.setSku(vendorCode);
					raw.setVendorCode(vendorCode);					
					outputData.add(raw);					
				} 				
				
			}			
			myExcelBook.close();
			//logger.debug("{}", outputData);	
			
		} catch (FileNotFoundException e) {
			logger.error("FileNotFoundException: ", e);
		} catch (IOException e) {
			logger.error("IOException:", e);			
		}
		logger.debug("loadFromExcelSledopyt: {}", outputData.size());
		return outputData;	
	}
	
	private void saveToDbSledopyt(Set<ProductDataRaw> productRaws) {
		
		for (ProductDataRaw productDataRaw : productRaws) {
			Product realProduct = wikiDao.findProductBySku(productDataRaw.getVendorCode());
			
			if (realProduct == null) {
				continue;
			}
			
			BigDecimal supplierPrice = PricerUtils.getPrice(productDataRaw.getSupplierPrice());
			BigDecimal price = PricerUtils.getPrice(productDataRaw.getPrice());			
						
			realProduct.setSupplierPrice(supplierPrice);
			realProduct.setPrice(price);
			
			logger.debug("update product: {}, {}, {}, {}", realProduct.getSku(), realProduct.getName(), price, supplierPrice);
						
			wikiDao.updatePriceAndQuantityProduct(realProduct);
			wikiDao.updateSupplierStockPrice(realProduct);			
		}
	}
	
	@SuppressWarnings("deprecation")
	private List<ProductDataRaw> loadFromExcelSititek() {
		HSSFWorkbook myExcelBook;
		List<ProductDataRaw> outputData = null;
		try {
			
			//myExcelBook = new HSSFWorkbook(new FileInputStream(LoaderConfig.FILE_PATH_PRICE_SITITEK_IN));
			
			myExcelBook = new HSSFWorkbook(new FileInputStream(LoaderConfig.DIR_PATH_PRICE_SITITEK_IN + LoaderConfig.FILE_NAME_PRICE_SITITEK_IN));
			HSSFSheet myExcelSheet = myExcelBook.getSheetAt(0);
			
			HSSFRow row;
			String quantity;
			String productName;
			String price;
			String supplierPrice;
			final int startIndex = 17;
			outputData = new ArrayList<ProductDataRaw>();			
		
			for (int i = startIndex; i <= myExcelSheet.getLastRowNum(); i++) {
				row = myExcelSheet.getRow(i);
				
				ProductDataRaw raw = null;
				if (row.getCell(0) != null && row.getCell(0).getCellType() == HSSFCell.CELL_TYPE_STRING) {				
					quantity = row.getCell(0).getStringCellValue();				
					productName = row.getCell(2).getStringCellValue();
					supplierPrice = row.getCell(9).getStringCellValue();
					
					try {
						price = row.getCell(10).getStringCellValue();	
					} catch (java.lang.IllegalStateException e) {
						price = String.valueOf(row.getCell(10).getNumericCellValue());						
					}					
					
					raw = new ProductDataRaw(productName, quantity, price, supplierPrice);
					outputData.add(raw);
				} 				
				//break;
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
	
	private List<Product> saveToDbSititek(List<ProductDataRaw> productRaws) {
		List<Product> results = new ArrayList<Product>();
		if (productRaws == null) {
			return results;
		}
		for (ProductDataRaw productDataRaw : productRaws) {
			Product product = createProductSititek(productDataRaw);
			if (product == null || product.getId() <= 0) {
				continue;
			}
			Product currentProduct = wikiDao.getProductById(product.getId());
			if (currentProduct == null || currentProduct.getId() <= 0) {
				continue;
			}						
			int quantity = 0;
			if (currentProduct.getMarket(CrmTypes.YANDEX_MARKET).isMarketSeller()) {
				quantity = currentProduct.getStockQuantity();
			} else {
				quantity = product.getQuantity() + currentProduct.getStockQuantity();
				if (product.getId() == 1064 && quantity == 0) {					
					quantity = 1;
				}
				
			}
			product.setQuantity(quantity);
			product.setMainSupplier(SupplierTypes.SITITEK);
			wikiDao.updatePriceAndQuantityProduct(product);
			wikiDao.updateSupplierStockPrice(product);	
			results.add(product);
		}
		return results;
	}
		
	private Product createProductSititek(ProductDataRaw raw) {			
		int id = PricerUtils.getId(raw.getProductName(), translator);
		int quantity = PricerUtils.getSititekQuantity(raw.getQuantity(), raw.getProductName());
		BigDecimal price;
		try {
			price = PricerUtils.getPrice(raw.getPrice());						
		} catch (java.lang.NumberFormatException e) {
			logger.error("PriceUtils.getPrice() price: <{}>, {}", raw.getPrice(), raw.getProductName());
			price = BigDecimal.ZERO;
		}		
		
		BigDecimal supplierPrice = PricerUtils.getPrice(raw.getSupplierPrice());				
		if (id == -1) {			
			Product colorProduct = translator.get(raw.getProductName());
			if (colorProduct == null) {
				return null;
			} 			
			Product mainProduct = wikiDao.getDbProductById(colorProduct.getLinkId());
			if (mainProduct == null) {
				return null;
			}
			quantity += mainProduct.getQuantity();
			Product product = new Product(colorProduct.getLinkId(), mainProduct.getName());			
			product.setQuantity(quantity);
			product.setPrice(mainProduct.getPrice());
			product.setSupplierPrice(mainProduct.getSupplierPrice());
			return product;
		} else {
			Product product = new Product(id, raw.getProductName());	
			product.setQuantity(quantity);
			product.setPrice(price);
			product.setSupplierPrice(supplierPrice);
			return product;
		}
	}
	
	
}
