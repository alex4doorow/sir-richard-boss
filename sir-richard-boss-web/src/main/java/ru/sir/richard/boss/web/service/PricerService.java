package ru.sir.richard.boss.web.service;

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
import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import ru.sir.richard.boss.dao.WikiDao;
import ru.sir.richard.boss.model.data.Product;
import ru.sir.richard.boss.model.data.raw.ProductDataRaw;
import ru.sir.richard.boss.model.types.CrmTypes;
import ru.sir.richard.boss.model.types.SupplierTypes;
import ru.sir.richard.boss.model.utils.PricerUtils;

@Service
@Slf4j
public class PricerService {
	
	private static final int BUFFER_SIZE = 4096;
	private Map<String, Product> translator;
	
	@Autowired
	private WikiDao wikiDao;
	
	@Autowired
	private Environment environment;
			
	public void runSititek() {
		
		log.info("run(): {}, {}", SupplierTypes.SITITEK.getAnnotation(), "start");
	
		try {
			downloadFile();
		} catch (IOException e) {
			log.error("IOException:", e);
			return;
		}
		translator = PricerUtils.createSititekTranslatorData();
		List<ProductDataRaw> raws = loadFromExcelSititek();
		saveToDbSititek(raws);

		log.info("run(): {}, {}", SupplierTypes.SITITEK.getAnnotation(), "finish");
		
	}
	
	private List<ProductDataRaw> loadFromExcelSititek() {
		HSSFWorkbook myExcelBook;
		List<ProductDataRaw> outputData = null;
		try {

			myExcelBook = new HSSFWorkbook(new FileInputStream(environment.getProperty("pricer.sititek.dir.out") + environment.getProperty("pricer.sititek.file.name.in")));
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
				if (row.getCell(0) != null && row.getCell(0).getCellType() == CellType.STRING) {				
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
			}			
			myExcelBook.close();
		} catch (FileNotFoundException e) {
			log.error("FileNotFoundException:", e);
		} catch (IOException e) {
			log.error("IOException:", e);
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
		int supplierQuantity = quantity;
		BigDecimal price;
		try {
			price = PricerUtils.getPrice(raw.getPrice());						
		} catch (java.lang.NumberFormatException e) {
			log.error("PriceUtils.getPrice() price: <{}>, {}", raw.getPrice(), raw.getProductName());
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
			product.setSupplierQuantity(supplierQuantity);
			product.setPrice(mainProduct.getPrice());
			product.setSupplierPrice(mainProduct.getSupplierPrice());
			return product;
		} else {
			Product product = new Product(id, raw.getProductName());	
			product.setQuantity(quantity);
			product.setSupplierQuantity(supplierQuantity);
			product.setPrice(price);
			product.setSupplierPrice(supplierPrice);
			return product;
		}
	}
	
	/**
     * Downloads a file from a URL
     * @throws IOException
     */
    private void downloadFile() throws IOException {

    	final String pricerFolderOut = environment.getProperty("pricer.sititek.dir.out");
    	final String pricerFileName = environment.getProperty("pricer.sititek.file.name.in");
    	final String pricerDownloadUrl = environment.getProperty("pricer.sititek.url") + pricerFileName;

        URL url = new URL(pricerDownloadUrl);
        HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
        httpConn.setRequestMethod("GET");        
        String loginPassword = environment.getProperty("pricer.sititek.login.password");
        String base64LoginPassword = Base64.getEncoder().encodeToString(loginPassword.getBytes());
        httpConn.setRequestProperty("authorization", "Basic " + base64LoginPassword);        
        httpConn.setRequestProperty("Content-type", "application/vnd.ms-excel");
        httpConn.setRequestProperty("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9"); 
        httpConn.setRequestProperty("accept-encoding", "gzip, deflate, br");
        httpConn.setRequestProperty("accept-language", "ru,en;q=0.9");
        
        int responseCode = httpConn.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            String fileName = "";
            String disposition = httpConn.getHeaderField("Content-Disposition");
            String contentType = httpConn.getContentType();
            int contentLength = httpConn.getContentLength();
 
            if (disposition != null) {
                int index = disposition.indexOf("filename=");
                if (index > 0) {
                    fileName = disposition.substring(index + 10, disposition.length() - 1);
                }
            } else {
                fileName = pricerFileName;
            }
 
            log.info("Content-Type = " + contentType);
            log.info("Content-Disposition = " + disposition);
            log.info("Content-Length = " + contentLength);
            log.info("fileName = " + fileName);
 
            InputStream inputStream = httpConn.getInputStream();
            String saveFilePath = pricerFolderOut + File.separator + fileName;
             
            FileOutputStream outputStream = new FileOutputStream(saveFilePath);
 
            int bytesRead = -1;
            byte[] buffer = new byte[BUFFER_SIZE];
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            } 
            outputStream.close();
            inputStream.close(); 
            log.info("File downloaded");
        } else {
        	log.info("No file to download. Server replied HTTP code: " + responseCode);
        }
        httpConn.disconnect();
    }	
}
