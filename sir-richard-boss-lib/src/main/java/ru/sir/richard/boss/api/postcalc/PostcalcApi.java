package ru.sir.richard.boss.api.postcalc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.zip.GZIPInputStream;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.sir.richard.boss.api.AnyApi;
import ru.sir.richard.boss.model.data.Address;
import ru.sir.richard.boss.model.data.crm.DeliveryServiceResult;

public class PostcalcApi implements AnyApi {
	
	private final Logger logger = LoggerFactory.getLogger(PostcalcApi.class);	
	
	public DeliveryServiceResult postCalc(int weightOfG, Date calculateDate, BigDecimal totalAmount, String parcelDataName, Address to, String iBase) throws IOException {
		
		if (StringUtils.isEmpty(to.getPostCode())) {
			return DeliveryServiceResult.createEmpty();
		}		
		
		// http://www.postcalc.ru/api/request-mandatory
		//http://api.postcalc.ru/?Output=json&From=107241&To=308519&Weight=100&Valuation=100&key=test
		
		//http://api2.postcalc.ru/?o=JSON&f=107241&t=308519&w=100&v=100&key=test&Charset=UTF-8
		
		String url = "http://api2.postcalc.ru/?o=JSON&f=107241&w=" + weightOfG + "&v=" + totalAmount.intValue() + "&ib=" + iBase + "&pr=0&pk=50&Round=0.01&vt=1&t=" + to.getPostCode() + "&cs=UTF-8&key=test";
		
		//String url = "http://test.postcalc.ru/web.php?Extend=0&Output=JSON&From=107241&Weight=" + weightOfG + "&Valuation=" + totalAmount.intValue() + "&Step=0&Date=" + DateTimeUtils.defaultFormatDate(calculateDate) + "&IBase=" + iBase + "&ProcessingFee=0&PackingFee=50&Round=0.01&VAT=1&To=" + to.getPostCode() + "&Charset=UTF-8";
	    URL obj = new URL(url);	    
	    HttpURLConnection con = (HttpURLConnection) obj.openConnection();
	    con.setRequestMethod("GET");
	    con.setRequestProperty("User-Agent", "Mozilla/5.0");	    
	    //con.setRequestProperty("Accept-Encoding", "gzip, deflate");	      
	    int responseCode = con.getResponseCode();
	    logger.debug("postCalc() responseCode:{}", responseCode);
	    logger.debug("postCalc() sending 'GET' request to URL:{}", url);
	    
	    //BufferedReader in = new BufferedReader((new InputStreamReader(con.getInputStream())));
	    
	    InputStream  gis = new GZIPInputStream(con.getInputStream());	    
	    BufferedReader in2 = new BufferedReader(new InputStreamReader(gis));
	    String inputLine2;
	    StringBuffer response = new StringBuffer();
	    while ((inputLine2 = in2.readLine()) != null) {
	     	response.append(inputLine2);
	    }
	    in2.close();
	    gis.close();

	   
	    
/*
	    
	    String inputLine;
	    StringBuffer response = new StringBuffer();
	    while ((inputLine = in.readLine()) != null) {
	     	response.append(inputLine);
	    }
	    in.close();
	    */

	    JSONObject myResponse = new JSONObject(response.toString());
	    logger.debug("postCalc() jsonResponse:{}", myResponse.toString());
	    
	    DeliveryServiceResult result = new DeliveryServiceResult();
	    	    
	    JSONObject parcelData = (JSONObject) myResponse.get("Отправления");
	    
	    if (StringUtils.isNotEmpty(parcelDataName)) {
	    	parcelData = (JSONObject) parcelData.get(parcelDataName);
	    } else {
	    	parcelData = null;
	    	
	    }
	    	    
	    if (parcelData != null) {
	    	BigDecimal postpayAmount = (new BigDecimal((String) parcelData.get("ОценкаПолная"))).setScale(0, RoundingMode.CEILING);	    		    	
	    	BigDecimal deliveryAmount = (new BigDecimal((String) parcelData.get("Доставка"))).setScale(0, RoundingMode.CEILING);
	    		    		    	
	    	result.setPostpayAmount(postpayAmount);
	    	result.setDeliveryAmount(deliveryAmount);
	    	result.setDeliverySellerSummary(deliveryAmount);
	    	result.setDeliveryCustomerSummary(deliveryAmount);
	    	
	    	result.setTermText((String) parcelData.get("СрокДоставки"));	    	
	    	JSONObject toResult = (JSONObject) myResponse.get("Куда");	    	 
	    	result.setTo(to.getPostCode() + ", " + (String) toResult.get("Адрес"));
	    	result.setParcelType((String) parcelData.get("Название"));
	    	result.setWeightText(weightOfG + " г.");	    	
	    	return result;
	    } else {
	    	return DeliveryServiceResult.createEmpty();
	    }
	}
}
