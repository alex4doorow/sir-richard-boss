package ru.sir.richard.boss.api.market;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.PropertyResolver;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import ru.sir.richard.boss.AnyTestEnvironment;
import ru.sir.richard.boss.model.data.Product;

public class YandexMarketApiTest {
	
	private class TestEnvironment extends AnyTestEnvironment {

		@SuppressWarnings("serial")
		@Override
		protected Map<String, String> getEnvironmentMap() {
			return new HashMap<String, String>(){{
			    put("yandex.market.url", "https://api.partner.market.yandex.ru/v2/campaigns/");
			    put("yandex.market.warehouse.compaign.first", "2");
			    put("yandex.market.warehouse.compaign.second", "2");
			    put("yandex.market.warehouse.compaign.third", "2");
			    put("yandex.market.oauth.token", "****");
			    put("yandex.market.oauth.client", "***");		    
			}};
		}	
		
	}	

}
