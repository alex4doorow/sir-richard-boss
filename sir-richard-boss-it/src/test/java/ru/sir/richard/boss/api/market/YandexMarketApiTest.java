package ru.sir.richard.boss.api.market;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import lombok.extern.slf4j.Slf4j;
import ru.sir.richard.boss.model.data.Customer;
import ru.sir.richard.boss.model.data.Order;
import ru.sir.richard.boss.model.data.OrderExternalCrm;
import ru.sir.richard.boss.model.types.CrmTypes;

@SpringBootTest
@PropertySource(value="classpath:application-it.properties", encoding="UTF-8")
@Slf4j
public class YandexMarketApiTest {

	@Autowired
	private Environment environment;

	@Test
	public void testPribormasterCartFBS() throws UnirestException {
		
		String jsonObjectString;
		Map<String, String> headers = new HashMap<>();
		headers.put("content-type", environment.getProperty("yandex.market.content-type"));
		headers.put("authorization", environment.getProperty("yandex.market.authorization.fbs"));
		JSONObject jsonObject = null;
		   
		jsonObjectString = "{\r\n"
				+ "  \"cart\": {\r\n"
				+ "    \"businessId\": 893360,\r\n"
				+ "    \"currency\": \"RUR\",\r\n"
				+ "    \"buyer\": {\r\n"
				+ "      \"type\": \"PERSON\"\r\n"
				+ "    },\r\n"
				+ "    \"items\": [\r\n"
				+ "      {\r\n"
				+ "        \"feedId\": 935744,\r\n"
				+ "        \"offerId\": \"LS-987BF\",\r\n"
				+ "        \"feedCategoryId\": \"1072752874\",\r\n"
				+ "        \"offerName\": \"ЭкоСнайпер LS-987BF\",\r\n"
				+ "        \"subsidy\": 0,\r\n"
				+ "        \"count\": 1,\r\n"
				+ "        \"fulfilmentShopId\": 1017201,\r\n"
				+ "        \"sku\": \"1772222425\",\r\n"
				+ "        \"warehouseId\": 55269,\r\n"
				+ "        \"partnerWarehouseId\": \"46902712-00b8-4b38-ac5c-a86e02b42b75\"\r\n"
				+ "      }\r\n"
				+ "    ],\r\n"
				+ "    \"delivery\": {\r\n"
				+ "      \"region\": {\r\n"
				+ "        \"id\": 213,\r\n"
				+ "        \"name\": \"Москва\",\r\n"
				+ "        \"type\": \"CITY\",\r\n"
				+ "        \"parent\": {\r\n"
				+ "          \"id\": 1,\r\n"
				+ "          \"name\": \"Москва и Московская область\",\r\n"
				+ "          \"type\": \"SUBJECT_FEDERATION\",\r\n"
				+ "          \"parent\": {\r\n"
				+ "            \"id\": 3,\r\n"
				+ "            \"name\": \"Центральный федеральный округ\",\r\n"
				+ "            \"type\": \"COUNTRY_DISTRICT\",\r\n"
				+ "            \"parent\": {\r\n"
				+ "              \"id\": 225,\r\n"
				+ "              \"name\": \"Россия\",\r\n"
				+ "              \"type\": \"COUNTRY\"\r\n"
				+ "            }\r\n"
				+ "          }\r\n"
				+ "        }\r\n"
				+ "      }\r\n"
				+ "    }\r\n"
				+ "  }\r\n"
				+ "}";
		jsonObject = new JSONObject(jsonObjectString);
		
		HttpResponse<JsonNode> jsonResponseCart = Unirest.post("https://pribormaster.ru/index.php?route=extension/module/yandex_market/cart")
				.headers(headers)
				.body(jsonObject)
				.asJson();
		assertEquals(200, jsonResponseCart.getStatus());
		assertEquals("{\"cart\":{\"items\":[{\"feedId\":935744,\"count\":2,\"offerId\":\"LS-987BF\"}]}}", jsonResponseCart.getBody().toString());

		jsonObjectString = "{ \"cart\": { \"businessId\": 893360, \"currency\": \"RUR\", \"items\": [ { \"feedId\": 935744, \"offerId\": \"SUN-BATTERY-SC-09\", \"offerName\": \"Аккумулятор SITITEK Sun-Battery SC-09, черный\", \"subsidy\": 0, \"count\": 1, \"fulfilmentShopId\": 1017201, \"warehouseId\": 55269, \"partnerWarehouseId\": \"46902712-00b8-4b38-ac5c-a86e02b42b75\" } ], \"delivery\": { \"region\": { \"id\": 213, \"name\": \"Москва\", \"type\": \"CITY\", \"parent\": { \"id\": 1, \"name\": \"Москва и Московская область\", \"type\": \"SUBJECT_FEDERATION\", \"parent\": { \"id\": 3, \"name\": \"Центральный федеральный округ\", \"type\": \"COUNTRY_DISTRICT\", \"parent\": { \"id\": 225, \"name\": \"Россия\", \"type\": \"COUNTRY\" } } } } } } }";
		jsonObject = new JSONObject(jsonObjectString);		     
		
		jsonResponseCart = Unirest.post("https://pribormaster.ru/index.php?route=extension/module/yandex_market/cart")
				.headers(headers)
				.body(jsonObject)
				.asJson();
		assertEquals(200, jsonResponseCart.getStatus());
		
			     
		jsonObjectString = "{ \"cart\": { \"businessId\": 893360, \"currency\": \"RUR\", \"items\": [ { \"feedId\": 935744, \"offerId\": \"LS-989\", \"offerName\": \"Ультразвуковой отпугиватель ЭкоСнайпер LS-989 (230 кв.м.)\", \"subsidy\": 0, \"count\": 1, \"fulfilmentShopId\": 1017201, \"warehouseId\": 55269, \"partnerWarehouseId\": \"46902712-00b8-4b38-ac5c-a86e02b42b75\" } ], \"delivery\": { \"region\": { \"id\": 213, \"name\": \"Москва\", \"type\": \"CITY\", \"parent\": { \"id\": 1, \"name\": \"Москва и Московская область\", \"type\": \"SUBJECT_FEDERATION\", \"parent\": { \"id\": 3, \"name\": \"Центральный федеральный округ\", \"type\": \"COUNTRY_DISTRICT\", \"parent\": { \"id\": 225, \"name\": \"Россия\", \"type\": \"COUNTRY\" } } } } } } }";
		jsonObject = new JSONObject(jsonObjectString);		     
		
		jsonResponseCart = Unirest.post("https://pribormaster.ru/index.php?route=extension/module/yandex_market/cart")
				.headers(headers)
				.body(jsonObject)
				.asJson();
		assertEquals(200, jsonResponseCart.getStatus());
						     
		jsonObjectString = "{ \"cart\": { \"businessId\": 893360, \"currency\": \"RUR\", \"items\": [ { \"feedId\": 935744, \"offerId\": \"WK-0108\", \"feedCategoryId\": \"1072752874\", \"offerName\": \"Звуковой прибор от птиц WK-0108\", \"subsidy\": 0, \"count\": 1, \"fulfilmentShopId\": 1017201, \"sku\": \"101153969631\", \"warehouseId\": 55269, \"partnerWarehouseId\": \"46902712-00b8-4b38-ac5c-a86e02b42b75\" } ], \"delivery\": { \"region\": { \"id\": 213, \"name\": \"Москва\", \"type\": \"CITY\", \"parent\": { \"id\": 1, \"name\": \"Москва и Московская область\", \"type\": \"SUBJECT_FEDERATION\", \"parent\": { \"id\": 3, \"name\": \"Центральный федеральный округ\", \"type\": \"COUNTRY_DISTRICT\", \"parent\": { \"id\": 225, \"name\": \"Россия\", \"type\": \"COUNTRY\" } } } } } } }";
		jsonObject = new JSONObject(jsonObjectString);		     
		
		jsonResponseCart = Unirest.post("https://pribormaster.ru/index.php?route=extension/module/yandex_market/cart")
				.headers(headers)
				.body(jsonObject)
				.asJson();
		assertEquals(200, jsonResponseCart.getStatus());
		//assertEquals("{\"cart\":{\"items\":[{\"feedId\":935744,\"count\":2,\"offerId\":\"WK-0108\"}]}}", jsonResponseCart.getBody().toString());
	}
	
	@Test
	public void testPribormasterCartDBS() throws UnirestException {
		
		String jsonObjectString;
		Map<String, String> headers = new HashMap<>();
		
		headers.put("content-type", environment.getProperty("yandex.market.content-type"));
		headers.put("authorization", environment.getProperty("yandex.market.authorization.dbs"));
		JSONObject jsonObject = null;
		     
		jsonObjectString = "{ \"cart\": { \"businessId\": 893360, \"currency\": \"RUR\", \"items\": [ { \"feedId\": 11320053, \"offerId\": \"LS-997MR\", \"feedCategoryId\": \"1757570077\", \"offerName\": \"Вибрационный отпугиватель кротов LS-997MR\", \"subsidy\": 0, \"count\": 1, \"params\": \"\", \"fulfilmentShopId\": 14847327, \"sku\": \"673058380\", \"warehouseId\": 309258, \"partnerWarehouseId\": \"a75876b1-066e-4fe0-a216-e8ffe64c31df\" } ], \"delivery\": { \"region\": { \"id\": 213, \"name\": \"Москва\", \"type\": \"CITY\", \"parent\": { \"id\": 1, \"name\": \"Москва и Московская область\", \"type\": \"SUBJECT_FEDERATION\", \"parent\": { \"id\": 3, \"name\": \"Центральный федеральный округ\", \"type\": \"COUNTRY_DISTRICT\", \"parent\": { \"id\": 225, \"name\": \"Россия\", \"type\": \"COUNTRY\" } } } } } } }";
		jsonObject = new JSONObject(jsonObjectString);		     
		
		HttpResponse<JsonNode> jsonResponseCart = Unirest.post("https://pribormaster.ru/index.php?route=extension/module/yandex_market/cart")
				.headers(headers)
				.body(jsonObject)
				.asJson();
		assertEquals(200, jsonResponseCart.getStatus());
		log.debug("****/*:{}", jsonResponseCart.getBody().toString());
		
		jsonObject = null;
     
		jsonObjectString = "{ \"cart\": { \"businessId\": 893360, \"currency\": \"RUR\", \"items\": [ { \"feedId\": 11320053, \"offerId\": \"LS-997MR\", \"feedCategoryId\": \"1757570077\", \"offerName\": \"Вибрационный отпугиватель кротов LS-997MR\", \"subsidy\": 0, \"count\": 1, \"params\": \"\", \"fulfilmentShopId\": 14847327, \"sku\": \"673058380\", \"warehouseId\": 309258, \"partnerWarehouseId\": \"a75876b1-066e-4fe0-a216-e8ffe64c31df\" } ], \"delivery\": { \"region\": { \"id\": 213, \"name\": \"Хабаровск\", \"type\": \"CITY\", \"parent\": { \"id\": 1, \"name\": \"Москва и Московская область\", \"type\": \"SUBJECT_FEDERATION\", \"parent\": { \"id\": 3, \"name\": \"Центральный федеральный округ\", \"type\": \"COUNTRY_DISTRICT\", \"parent\": { \"id\": 225, \"name\": \"Россия\", \"type\": \"COUNTRY\" } } } } } } }";
		jsonObject = new JSONObject(jsonObjectString);		     

		jsonResponseCart = Unirest.post("https://pribormaster.ru/index.php?route=extension/module/yandex_market/cart")
				.headers(headers)
				.body(jsonObject)
				.asJson();
		assertEquals(200, jsonResponseCart.getStatus());
		log.debug("****/**:{}", jsonResponseCart.getBody().toString());
			     
		jsonObjectString = "{ \"cart\": { \"businessId\": 893360, \"currency\": \"RUR\", \"items\": [ { \"feedId\": 11320053, \"offerId\": \"LS-997MR\", \"feedCategoryId\": \"1757570077\", \"offerName\": \"Вибрационный отпугиватель кротов LS-997MR\", \"subsidy\": 0, \"count\": 1, \"params\": \"\", \"fulfilmentShopId\": 14847327, \"sku\": \"673058380\", \"warehouseId\": 309258, \"partnerWarehouseId\": \"a75876b1-066e-4fe0-a216-e8ffe64c31df\" } ], \"delivery\": { \"region\": { \"id\": 213, \"name\": \"Северобайкальск\", \"type\": \"CITY\", \"parent\": { \"id\": 1, \"name\": \"Москва и Московская область\", \"type\": \"SUBJECT_FEDERATION\", \"parent\": { \"id\": 3, \"name\": \"Центральный федеральный округ\", \"type\": \"COUNTRY_DISTRICT\", \"parent\": { \"id\": 225, \"name\": \"Россия\", \"type\": \"COUNTRY\" } } } } } } }";
		jsonObject = new JSONObject(jsonObjectString);		     

		jsonResponseCart = Unirest.post("https://pribormaster.ru/index.php?route=extension/module/yandex_market/cart")
				.headers(headers)
				.body(jsonObject)
				.asJson();
		assertEquals(200, jsonResponseCart.getStatus());
		log.debug("****/***:{}", jsonResponseCart.getBody().toString());				
	}
	
	@Test
	public void testOrderAddress() {
		
		int ymOrderId;
		Order ymOrder;
		String addressText;
				 
		Customer customerFBSExpress = new Customer();			
		customerFBSExpress.setId(7204); // express FBS
		ymOrderId = 153208837;
		Order orderFromFBSExpress = new Order();
		orderFromFBSExpress.setCustomer(customerFBSExpress);
		OrderExternalCrm crmYandexMarket = new OrderExternalCrm(orderFromFBSExpress);
		crmYandexMarket.setCrm(CrmTypes.YANDEX_MARKET);      
		orderFromFBSExpress.getExternalCrms().add(crmYandexMarket);
		orderFromFBSExpress.getExternalCrmByCode(CrmTypes.YANDEX_MARKET).setParentId(Long.valueOf(ymOrderId));
		
		YandexMarketApi yandexMarketApi = new YandexMarketApi(this.environment);		
		ymOrder = yandexMarketApi.order(Long.valueOf(ymOrderId), orderFromFBSExpress);
		addressText = ymOrder.getDelivery().getAddress().getAddress();
		assertTrue(ymOrder != null);
		log.info(addressText);
		assertEquals("Москва, Москва и Московская область, Центральный федеральный округ, Россия, [DELIVERY, Доставка]", addressText);
						
		Customer customerFBS = new Customer();			
		customerFBS.setId(6611); // FBS
		ymOrderId = 152956455;
		Order orderFromFBS = new Order();
		crmYandexMarket = new OrderExternalCrm(orderFromFBS);
		crmYandexMarket.setCrm(CrmTypes.YANDEX_MARKET);      
		orderFromFBS.getExternalCrms().add(crmYandexMarket);
		orderFromFBS.getExternalCrmByCode(CrmTypes.YANDEX_MARKET).setParentId(Long.valueOf(ymOrderId));
		
		yandexMarketApi = new YandexMarketApi(this.environment);		
		ymOrder = yandexMarketApi.order(Long.valueOf(ymOrderId), orderFromFBS);
		assertTrue(ymOrder != null);		
		addressText = ymOrder.getDelivery().getAddress().getAddress();
		log.info(addressText);
		assertEquals("Шахты, Городской округ Шахты, Ростовская область, Южный федеральный округ, Россия, [DELIVERY, Доставка]", addressText);
		
		//dbs 7541
	}
}
