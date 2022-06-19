package ru.sir.richard.boss.api.postcalc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.PropertyResolver;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.HttpRequest;

import ru.sir.richard.boss.api.AnyApi;
import ru.sir.richard.boss.model.data.Address;
import ru.sir.richard.boss.model.data.crm.DeliveryServiceResult;
import ru.sir.richard.boss.model.types.PaymentTypes;

public class PostcalcApi implements AnyApi {
	
	private final Logger logger = LoggerFactory.getLogger(PostcalcApi.class);
	
	/**
	 * application.properties
	 */	
	private final PropertyResolver environment;
	
	public PostcalcApi(PropertyResolver environment) {
		super();
		this.environment = environment;
	}
	
	public DeliveryServiceResult postCalc21(int weightOfG, Date calculateDate, BigDecimal totalAmount, String parcelDataName, Address to, 
			PaymentTypes paymentType) throws IOException {
				
		Map<String, String> headers = new HashMap<>();
		headers.put("User-Agent", "Mozilla/5.0");
		headers.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9");
		
		Map<String, Object> queryStrings = new HashMap<String, Object>();
		queryStrings.put("f", "107241");
		queryStrings.put("t", to.getPostCode());
		queryStrings.put("w", weightOfG);
		queryStrings.put("v", totalAmount.intValue());		
		queryStrings.put("key", "test");
		queryStrings.put("o", "plain");
		
		if (paymentType == PaymentTypes.PREPAYMENT) {
			queryStrings.put("ib", "p");
			queryStrings.put("pk", "50");
			queryStrings.put("sv", "sm");
		} else {
			queryStrings.put("ib", "f");
			queryStrings.put("pk", "50");
			queryStrings.put("sv", "sm,cod");
		}			
	
		try {
			HttpResponse<InputStream> insResponse = Unirest.get("http://api2.postcalc.ru/")
					.headers(headers)
					.queryString(queryStrings)
					.asBinary();				
			
			InputStream ins = insResponse.getBody();			
			//InputStream gins = new GZIPInputStream(insBody);
			
		    BufferedReader in2 = new BufferedReader(new InputStreamReader(ins));
		    String inputLine2;
		    StringBuffer response = new StringBuffer();
		    while ((inputLine2 = in2.readLine()) != null) {
		     	response.append(inputLine2);
		    }
		    in2.close();
		    ins.close();
		    //gins.close();
		    
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
		} catch (UnirestException e) {
			logger.error("UnirestException:", e);
		}  
		return null;

	}
	
	public DeliveryServiceResult postCalc(int weightOfG, Date calculateDate, BigDecimal totalAmount, String parcelDataName, Address to, String sv) throws IOException {
		
		if (StringUtils.isEmpty(to.getPostCode())) {
			return DeliveryServiceResult.createEmpty();
		}		
			
		String url = "http://api.postcalc.ru/?o=JSON";
		
	    URL obj = new URL(url);	    
	    HttpURLConnection con = (HttpURLConnection) obj.openConnection();
	    con.setRequestMethod("GET");
	    con.setRequestProperty("User-Agent", "Mozilla/5.0");	    
     
	    int responseCode = con.getResponseCode();
	    logger.debug("postCalc() responseCode:{}", responseCode);
	    logger.debug("postCalc() sending 'GET' request to URL:{}", url);
	    
	    InputStream  gis = new GZIPInputStream(con.getInputStream());	    
	    BufferedReader in2 = new BufferedReader(new InputStreamReader(gis));
	    String inputLine2;
	    StringBuffer response = new StringBuffer();
	    while ((inputLine2 = in2.readLine()) != null) {
	     	response.append(inputLine2);
	    }
	    in2.close();
	    gis.close();

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
