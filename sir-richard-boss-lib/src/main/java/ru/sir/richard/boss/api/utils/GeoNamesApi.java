package ru.sir.richard.boss.api.utils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.PropertyResolver;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import ru.sir.richard.boss.model.utils.DateTimeUtils;

/*
// https://en.wikipedia.org/wiki/ISO_8601
http://www.geonames.org/export/web-services.html#timezone
указывать часовой пояс по адресу доставки. "Сейчас в норильске 4:37" https://ru.stackoverflow.com/questions/723350/%D0%A7%D0%B0%D1%81%D0%BE%D0%B2%D1%8B%D0%B5-%D0%BF%D0%BE%D1%8F%D1%81%D0%B0-java
https://grishaev.me/timezone/

Орск, Оренбург https://geocode-maps.yandex.ru/1.x/?apikey=*******&geocode=орск  
*/
public class GeoNamesApi {

	private final Logger logger = LoggerFactory.getLogger(GeoNamesApi.class);

	/**
	 * application.properties
	 */
	private final PropertyResolver environment;

	public GeoNamesApi(PropertyResolver environment) {
		this.environment = environment;
	}

	// https://geocode-maps.yandex.ru/1.x/?format=json&apikey=****&geocode=cityName
	public Map<String, String> getPositionByCity(String city) {

		Map<String, String> headers = new HashMap<>();
		headers.put("content-type", "application/json");

		Map<String, Object> queryStrings = new HashMap<>();
		queryStrings.put("format", "json");
		queryStrings.put("apikey", environment.getProperty("geocode.maps.yandex.key"));
		queryStrings.put("geocode", city);

		String sPos = null;
		try {
			HttpResponse<JsonNode> jsonResponseMapsYandex = Unirest
					.get(environment.getProperty("geocode.maps.yandex.url")).headers(headers).queryString(queryStrings)
					.asJson();

			if (jsonResponseMapsYandex.getStatus() != 200) {
				return null;
			}
			JSONObject jsonResponseMapsYandexBody;
			
			jsonResponseMapsYandexBody = jsonResponseMapsYandex.getBody().getObject()
						.getJSONObject("response").getJSONObject("GeoObjectCollection");
			JSONArray jsonResponseMapsYandexBodyArray = jsonResponseMapsYandexBody.getJSONArray("featureMember");
			for (int i = 0; i <= jsonResponseMapsYandexBodyArray.length() - 1; i++) {

					JSONObject objectPoint = jsonResponseMapsYandexBodyArray.getJSONObject(i).getJSONObject("GeoObject");
					sPos = objectPoint.getJSONObject("Point").getString("pos");
					JSONObject addressObjectPoint = objectPoint.getJSONObject("metaDataProperty")
							.getJSONObject("GeocoderMetaData").getJSONObject("Address");
					String countryCode = addressObjectPoint.getString("country_code");
					String addressText = addressObjectPoint.getString("formatted");

					logger.debug("objectPoint: {}, {}, {}", sPos, countryCode, addressText);
					if (countryCode.equals("RU") && StringUtils.isNotEmpty(sPos)) {
						break;
					}
			}

		} catch (UnirestException e) {
			logger.error("UnirestException:", e);
		} catch (JSONException e) {
			logger.error("JSONException", e);				
		}
		
		if (StringUtils.isEmpty(sPos)) {
			return null;
		}			
		String[] posArray = sPos.split(" ");
		Map<String, String> result = new HashMap<String, String>();
		result.put("lng", posArray[0]);
		result.put("lat", posArray[1]);		
		return result;		
	}

	// http://api.geonames.org/timezoneJSON?lat=51.212650&lng=58.618969&username=demo
	public GeoNamesBean getLocalTimeByCity(String city, Date currentDateTime) {

		Map<String, String> pos = getPositionByCity(city);
		if (pos == null) {
			return new GeoNamesBean();
		}

		Map<String, String> headers = new HashMap<>();
		headers.put("content-type", "application/json");

		Map<String, Object> queryStrings = new HashMap<>();
		queryStrings.put("lat", pos.get("lat"));
		queryStrings.put("lng", pos.get("lng"));
		queryStrings.put("username", environment.getProperty("geonames.user"));

		String timeZoneId = null;
		int gmtOffset = 0;
		try {
			HttpResponse<JsonNode> jsonResponseTimeZone = Unirest
					.get(environment.getProperty("geonames.url") + "/timezoneJSON").headers(headers)
					.queryString(queryStrings).asJson();

			if (jsonResponseTimeZone.getStatus() != 200) {
				return null;
			}
			JSONObject jsonResponseTimeZoneBody = jsonResponseTimeZone.getBody().getObject();
			timeZoneId = jsonResponseTimeZoneBody.getString("timezoneId");

			if (StringUtils.isEmpty(timeZoneId)) {
				return null;
			}
			gmtOffset = jsonResponseTimeZoneBody.getInt("gmtOffset");

		} catch (UnirestException e) {
			logger.error("UnirestException:", e);
		} catch (JSONException e) {
			logger.error("JSONException:", e);
		}
		String localDateTime = DateTimeUtils.defaultFormatDateTimeByTimeZone(currentDateTime, timeZoneId);
		String localTime = DateTimeUtils.defaultFormatTimeByTimeZone(currentDateTime, timeZoneId);
				
		return new GeoNamesBean(timeZoneId, localDateTime, localTime, gmtOffset);
	}

	public class GeoNamesBean {

		private String localDateTime;
		private String localTime;		
		private String timeZoneId; 
		private int gmtOffset;
		
		public GeoNamesBean() {
			this.timeZoneId = "";
			this.localDateTime = "";
			this.localTime = "";			
			this.gmtOffset = 3;			
		}

		public GeoNamesBean(String timeZoneId, String localDateTime, String localTime, int gmtOffset) {
			this.timeZoneId = timeZoneId;
			this.localDateTime = localDateTime;
			this.localTime = localTime;			
			this.gmtOffset = gmtOffset;
		}

		public String getTimeZoneId() {
			return timeZoneId;
		}

		public void setTimeZoneId(String timeZoneId) {
			this.timeZoneId = timeZoneId;
		}

		public String getLocalDateTime() {
			return localDateTime;
		}
		
		public String getLocalTime() {
			return localTime;			
		}
		public void setLocalTime(String localTime) {
			this.localTime = localTime;
		}

		public void setLocalDateTime(String localDateTime) {
			this.localDateTime = localDateTime;
		}

		public int getGmtOffset() {
			return gmtOffset;
		}

		public void setGmtOffset(int gmtOffset) {
			this.gmtOffset = gmtOffset;
		}
		
		public boolean isMoscowOffset() {
			return this.gmtOffset == 3;
		}
						
		public String textLocalTime() {
			
			if (!isMoscowOffset()) {
				return getLocalTime();
			} else {
				return "";
			}
		}
	}

}
