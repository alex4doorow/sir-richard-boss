package ru.sir.richard.boss.api.alarm;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

public class AlarmTest {

	private final Logger logger = LoggerFactory.getLogger(AlarmTest.class);
	
	//@Test
	public void testAlarmLocation0() throws UnirestException {	
		
		Map<String, String> headers = new HashMap<>();
		headers.put("content-type", "application/json;charset=utf-8");
		headers.put("authorization", "7C00000130783B1E");

//		55.817330, 37.796718

		JSONObject jsonObject = null;
		try {		     
			String jsonObjectString = "{ \"location\": {\"lat\": 55.817330, \"lng\": 37.796718}}";
			jsonObject = new JSONObject(jsonObjectString);		     
		     
		} catch (JSONException err){
		     logger.error("JSONException", err);
		}
		
		HttpResponse<JsonNode> jsonResponseGps = Unirest.post("https://pribormaster.ru/index.php?route=extension/module/yandex_market/alarm_location")
		//HttpResponse<JsonNode> jsonResponseGps = Unirest.post("https://pribormaster.ru/location")	
				.headers(headers)
				.body(jsonObject)
				.asJson();
		assertEquals(200, jsonResponseGps.getStatus());
				
		assertEquals("{\"location\":[{\"lng\":37.796718,\"lat\":55.81733}]}", jsonResponseGps.getBody().toString());
		
		
		
	}
	
	
	@Test
	public void testAlarmLocationAdd() throws UnirestException {	
		
		Map<String, String> headers = new HashMap<>();
		headers.put("content-type", "application/json;charset=utf-8");
		headers.put("authorization", "7C00000130783B1E");

//		55.817330, 37.796718

		JSONObject jsonObject = null;
		try {		     
			String jsonObjectString = "{ \"location\": {\"lat\": 55.817330, \"lng\": 37.796718}}";
			jsonObject = new JSONObject(jsonObjectString);		     
		     
		} catch (JSONException err){
		     logger.error("JSONException", err);
		}
		
		HttpResponse<JsonNode> jsonResponseGps = Unirest.post("http://pm.sir-richard.ru/index.php?route=extension/module/alarm/location/add")			
				.headers(headers)
				.body(jsonObject)
				.asJson();
		assertEquals(200, jsonResponseGps.getStatus());				
		assertEquals("{\"location\":[{\"lng\":37.796718,\"lat\":55.81733}]}", jsonResponseGps.getBody().toString());
	}
	
	@Test
	public void testAlarmLocationValues() throws UnirestException {	

//		55.817330, 37.796718

		
		HttpResponse<String> jsonResponseGps = Unirest.get("http://pm.sir-richard.ru/index.php?route=extension/module/alarm/location/values")
		//HttpResponse<JsonNode> jsonResponseGps = Unirest.post("https://pribormaster.ru/location")	
				//.headers(headers)
				//.body(jsonObject)
				.asString();
		assertEquals(200, jsonResponseGps.getStatus());
				
		//assertEquals("{\"location\":[{\"lng\":37.796718,\"lat\":55.81733}]}", jsonResponseGps.getBody().toString());
		
		
		
	}


}
