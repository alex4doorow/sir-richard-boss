package ru.sir.richard.boss.api.alert;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.sir.richard.boss.api.AnyApi;

public class AlarmApi implements AnyApi {
	
	// состояния STATE_
	/**
	 * не поддается анализу
	 * 
	 */
	public static final int STATE_UNKNOWN = -1; 
	/**
	 * выключена
	 * 
	 */
	public static final int STATE_SLEEP = 0;   
	/**
	 * поставлена на охрану
	 * 
	 */
	public static final int STATE_ON = 1;    
	/**
	 * снята с охраны
	 * 
	 */
	public static final int STATE_OFF = 2;    
	/**
	 * сработка
	 * 
	 */
	public static final int STATE_ALARM = 3;   
	
	// действия по изменению состояния ACTION_
	/**
	 * поставить на охрану
	 * 
	 */
	public static final int ACTION_ON = 1;       
	/**
	 * снять с охраны 
	 * 
	 */
	public static final int ACTION_OFF = 2;  
	/**
	 * запрос текущего состояния
	 *  
	 */
	public static final int ACTION_HEART_BEAT = 3; 

	private final Logger logger = LoggerFactory.getLogger(AlarmApi.class);
		
	public String gpioActionExecute(String host, int action) {
		String responseString = "";
		
		if (StringUtils.isEmpty(host)) {
			return responseString;
		}
		try {
			responseString = gpioPrivateActionExecute(host, action);			
		} catch (IOException e) {
			logger.error("Ошибка выполнения запроса к сигнализации через WiFi: ", e);
		}
		return responseString;		   		
	}	
	
	public int gpioState(String host, int action) {
		
		int result = STATE_UNKNOWN;
				
		if (StringUtils.isEmpty(host)) {
			return result;
		}
		String responseString = "";
		try {
			responseString = gpioPrivateActionExecute(host, action);
		} catch (ConnectException e) {
			result = STATE_SLEEP;
			logger.error("WiFi Connection timed out: ", e);
		} catch (IOException e) {
			result = STATE_SLEEP;
			logger.error("Ошибка выполнения запроса к сигнализации через WiFi: ", e);
		}		
		String resultOfResponce = responseString;
	    resultOfResponce = StringUtils.remove(resultOfResponce, "<!DOCTYPE HTML><html>ALARM-CAR requestActionValue: ");
	    resultOfResponce = StringUtils.remove(resultOfResponce, "</html>");
	    resultOfResponce = resultOfResponce.trim();
	    
	    if (StringUtils.isEmpty(resultOfResponce)) {
	    	return STATE_SLEEP;
	    } else {
	    	result = Integer.valueOf(resultOfResponce);
	    }
		return result;		
	}
	
	private String gpioPrivateActionExecute(String host, int action) throws IOException {
		
		if (StringUtils.isEmpty(host)) {
			return "";
		}
		
		final String urlString = "http://" + host + "/alarm-car/" + action;		
		URL obj = null;
		HttpURLConnection con = null;
		StringBuffer response = null;
		
		obj = new URL(urlString);
		con = (HttpURLConnection) obj.openConnection();
		con.setRequestProperty("Content-Type", "text/html");
	    //con.setRequestProperty("Content-Language", "ru-RU");
	    //con.setRequestProperty("Accept-Language", "ru,en;q=0.9");
	    //con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/73.0.3683.86 Safari/537.36");
			    
	    int responseCode = con.getResponseCode();	    
	    BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"));
	    String inputLine;
	    response = new StringBuffer();
	    while ((inputLine = in.readLine()) != null) {
	     	response.append(inputLine);
	    }
	    in.close();
	    logger.debug("gpio code: {}, url: {}, response: {}", responseCode, urlString, response);	    
		return response.toString();	    		
	}	

}
