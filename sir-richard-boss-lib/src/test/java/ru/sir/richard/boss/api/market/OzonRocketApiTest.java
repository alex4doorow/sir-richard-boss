package ru.sir.richard.boss.api.market;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import ru.sir.richard.boss.AnyTestEnvironment;
import ru.sir.richard.boss.model.data.Address;
import ru.sir.richard.boss.model.data.Customer;
import ru.sir.richard.boss.model.data.Order;
import ru.sir.richard.boss.model.data.OrderItem;
import ru.sir.richard.boss.model.data.Product;
import ru.sir.richard.boss.model.data.crm.DeliveryServiceResult;
import ru.sir.richard.boss.model.types.CarrierStatuses;
import ru.sir.richard.boss.model.types.DeliveryTypes;
import ru.sir.richard.boss.model.types.OrderStatuses;
import ru.sir.richard.boss.model.types.OrderTypes;
import ru.sir.richard.boss.model.types.PaymentDeliveryTypes;
import ru.sir.richard.boss.model.types.PaymentTypes;
import ru.sir.richard.boss.model.utils.DateTimeUtils;

public class OzonRocketApiTest {
		
	private class TestEnvironment extends AnyTestEnvironment {

		@SuppressWarnings("serial")
		@Override
		protected Map<String, String> getEnvironmentMap() {
			return new HashMap<String, String>(){{
			    put("ozon.rocket.url", "https://xapi.ozon.ru/principal-integration-api");
			    put("ozon.rocket.client.id", "*");
			    put("ozon.rocket.client.key", *");
			    put("ozon.rocket.from.address", "");
			}};

		}	
		
	}		
}
