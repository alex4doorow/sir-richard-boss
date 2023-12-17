package ru.sir.richard.boss.utils;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONObject;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestPricerLoad {
	
	private final Logger logger = LoggerFactory.getLogger(TestPricerLoad.class);
	
	//@Test
	public void testOne() {
		
		
		System.out.println("1");
		
		String url = "https://sititek.ru/dealer/Price.xls";
		URL obj;
		JSONObject myResponse = null;
		try {
			obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
				
			
		    con.setRequestProperty("Content-type", "application/vnd.ms-excel");			
			con.setRequestProperty("Authorization", "user=\"dealer\", password=\"fLR7MRrI\"");
			
		    		  
		    int responseCode = con.getResponseCode();
		    String fileName = con.getRequestProperty("fileName");
		   
		   
		    logger.debug("offerMappingEntries() responseCode:{}", responseCode);
		    logger.debug("offerMappingEntries() sending 'GET' request to URL:{}", url);
		    
		    
			
		} catch (MalformedURLException e) {
			logger.error("MalformedURLException: ", e);
		} catch (Exception e) {
			logger.error("Exception: ", e);
		}
		
	}

}
