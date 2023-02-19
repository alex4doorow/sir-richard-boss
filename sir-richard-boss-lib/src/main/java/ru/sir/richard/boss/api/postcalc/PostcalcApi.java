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

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.PropertyResolver;

import ru.sir.richard.boss.api.AnyApi;
import ru.sir.richard.boss.model.data.Address;
import ru.sir.richard.boss.model.data.crm.DeliveryServiceResult;
import ru.sir.richard.boss.model.types.DeliveryTypes;
import ru.sir.richard.boss.model.types.PaymentTypes;

@Slf4j
public class PostcalcApi implements AnyApi {

	private PropertyResolver environment;	

	public PostcalcApi(PropertyResolver environment) {
		super();
		this.environment = environment;
	}
	
	public static String getParcelDataName(DeliveryTypes type) {		
		String parcelDataName;
		if (type == DeliveryTypes.POST_TYPICAL) {
			parcelDataName = "ЦеннаяПосылка";
	    } else if (type == DeliveryTypes.POST_EMS) {
	    	parcelDataName = "EMS";
	    } else if (type == DeliveryTypes.POST_I_CLASS) {
	    	parcelDataName = "Посылка1Класс";	
	    } else {
	    	parcelDataName = "";       
	    }
		return parcelDataName;
	}
	
	public static String getSv(PaymentTypes paymentType) {
		
		if (paymentType == PaymentTypes.PREPAYMENT || paymentType == PaymentTypes.YANDEX_PAY) {
			return "&ib=p&pk=50&sv=sm,ng";
		} else {
			return "&ib=f&pk=50&sv=sm,cod,ng";			
		}
	}
	
	public DeliveryServiceResult postCalc(int weightOfG, Date calculateDate, BigDecimal totalAmount, String parcelDataName, Address to, String sv) throws IOException {
		
		if (StringUtils.isEmpty(to.getPostCode())) {
			return DeliveryServiceResult.createEmpty();
		}		

		String url = environment.getProperty("postcalc.url").trim() + "/?o=JSON&f=" + environment.getProperty("postcalc.from.index").trim() + "&w=" + weightOfG + "&v=" + totalAmount.intValue() + sv + "&pr=0&pk=50&Round=0.01&vt=1&t=" + to.getPostCode() + "&cs=UTF-8&key=test";
		
	    URL obj = new URL(url);	    
	    HttpURLConnection con = (HttpURLConnection) obj.openConnection();
	    con.setRequestMethod("GET");
	    con.setRequestProperty("User-Agent", "Mozilla/5.0");	    
     
	    int responseCode = con.getResponseCode();
	    log.debug("postCalc() responseCode:{}", responseCode);
	    log.debug("postCalc() sending 'GET' request to URL:{}", url);
	    
	    InputStream  gis = new GZIPInputStream(con.getInputStream());	    
	    BufferedReader in2 = new BufferedReader(new InputStreamReader(gis));
	    String inputLine2;
	    StringBuffer response = new StringBuffer();
	    while ((inputLine2 = in2.readLine()) != null) {
	     	response.append(inputLine2);
	    }
	    in2.close();
	    gis.close();

	    // 16.10.2022 19:00:55:012 [main] DEBUG r.s.r.boss.api.postcalc.PostcalcApi - postCalc() jsonResponse:{"Status":"ERROR_TESTKEY_LIMIT_50","_server":{"HTTP_HOST":"api.postcalc.ru","REMOTE_ADDR":"46.138.94.104","HTTP_USER_AGENT":"Mozilla/5.0","HTTP_ACCEPT_ENCODING":"","SERVER_ADDR":"185.159.82.61"},"Errors":{"ERROR_TESTKEY_LIMIT_50":"Достигнут предел запросов для тестового ключа: 50 в сутки с одного адреса IP. [IP: '46.138.94.104']"},"Message":"Limit for test key reached: 50 requests in one day from one IP address. [IP: '46.138.94.104']","_timing":{"ExtRequest":"0.0","LocalCalc":"0.0","Start":"2022-10-16  18:54:55.6273 MSK","Total":"4.1","End":"2022-10-16  18:54:55.6313 MSK"},"NumReqToday":52,"API":"2.1","_vars":{"Valuation":0,"Parcels":"","VAT":1,"Corp":1,"Box":"s","From":"107241","Weight":300,"Date":"now","Services":"","Partible":0,"Country":"RU","To":"164170","Key":"test","IBase":"p"},"_request":{"pr":"0","sv":"sm,ng","f":"107241","o":"JSON","cs":"UTF-8","t":"164170","v":"1000","w":"300","ib":"p","Round":"0.01","pk":"50","vt":"1","key":"test"}}
	    JSONObject myResponse = null;
	    DeliveryServiceResult result = new DeliveryServiceResult();
	    try {
	    	myResponse = new JSONObject(response.toString());
		    log.debug("postCalc() jsonResponse:{}", myResponse.toString());
		    	   	
	    	String status = (String) myResponse.get("Status");
	    	if (StringUtils.equals(status, "BAD_TO_INDEX")) {
	    		result = DeliveryServiceResult.createEmpty();
	    		result.setTermText("неизвестно");	    	
		    	result.setTo("Индекс получателя введен неверно");
		    	result.setParcelType(status);
		    	return result;
	    	} else if (StringUtils.equals(status, "ERROR_TESTKEY_LIMIT_50")) {
	    		result = DeliveryServiceResult.createEmpty();
	    		result.setTermText("Достигнут предел запросов для тестового ключа: 50 в сутки с одного адреса IP");	    	
		    	result.setTo("Превышено число попыток");
		    	result.setParcelType(status);
		    	return result;

	    	} 
	    	
	    } catch (Exception e) {
	    	log.error("Status: ", e);
	    } 	    
	    JSONObject parcelData = null;
	    try {
	    	parcelData = (JSONObject) myResponse.get("Отправления");
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
	    	
	    } catch (JSONException e) {
			log.error("JSONException:", e);
		}    
	    return DeliveryServiceResult.createEmpty();
	}
}
