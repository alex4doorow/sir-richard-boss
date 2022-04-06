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

	//private final Logger logger = LoggerFactory.getLogger(OzonRocketApiTest.class);
	
	//@Test
	public void testGetToken() throws UnirestException, JSONException {		
		OzonRocketApi ozonRocketApi = new OzonRocketApi();
		String token = ozonRocketApi.getToken();
		assertNotNull(token);
	}

	//@Test
	public void testGetFromPlaceId() throws UnirestException, JSONException {
		OzonRocketApi ozonRocketApi = new OzonRocketApi();		
		Long fromPlaceId = ozonRocketApi.getFromPlaceId();
		assertNotNull(fromPlaceId);
		assertEquals(String.valueOf(19292478517000L), String.valueOf(fromPlaceId));	        	
	}
	
	//@Test
	public void testGetDeliveryVariant() throws UnirestException, JSONException {
		OzonRocketApi ozonRocketApi = new OzonRocketApi();
		
		Address deliveryVariant = ozonRocketApi.getDeliveryVariant(19489680423000L);				
		assertNotNull(deliveryVariant);		
	}
	
	//@Test
	public void testCalculate() throws UnirestException, JSONException {		
		
		OzonRocketApi ozonRocketApi = new OzonRocketApi();
		Long deliveryVariantId;
		
		deliveryVariantId = ozonRocketApi.getDeliveryVariantId("Нижний Новгород", "PickPoint", 500, BigDecimal.valueOf(3000), false, false, false, false);				
		assertNotNull(deliveryVariantId);
		
		DeliveryServiceResult deliveryServiceResult;
		
		deliveryServiceResult = ozonRocketApi.calculate(2000, DateTimeUtils.sysDate(), BigDecimal.valueOf(5900), 
				deliveryVariantId,
				false,			
				false);
		assertNotNull(deliveryServiceResult);
		//assertEquals(BigDecimal.valueOf(119), deliveryServiceResult.getDeliveryPrice());
		//assertEquals("4 дн.", deliveryServiceResult.getTermText());
		
		deliveryVariantId = ozonRocketApi.getDeliveryVariantId("Псков", "PickPoint", 500, BigDecimal.valueOf(3000), false, false, false, false);
		assertNotNull(deliveryVariantId);
		
		deliveryServiceResult = ozonRocketApi.calculate(2000, DateTimeUtils.sysDate(), BigDecimal.valueOf(5900), 
				deliveryVariantId,
				false,			
				false);
		assertNotNull(deliveryServiceResult);
		//assertEquals(BigDecimal.valueOf(119), deliveryServiceResult.getDeliveryPrice());
		//assertEquals("4 дн.", deliveryServiceResult.getTermText());		
		
	}
	
	//@Test
	public void testCalculateByAddress() throws UnirestException, JSONException {
		
		OzonRocketApi ozonRocketApi = new OzonRocketApi();
						
		DeliveryServiceResult deliveryServiceResult = ozonRocketApi.calculateByAddress(2000, DateTimeUtils.sysDate(), BigDecimal.valueOf(5900),
				"Псков, Ленина 15",
				false,			
				false);
		assertNotNull(deliveryServiceResult);
		assertEquals(BigDecimal.valueOf(240), deliveryServiceResult.getDeliveryPrice());
		//assertEquals("4 дн.", deliveryServiceResult.getTermText());	
		
	}
	
	//@Test
	public void testAddOrder() {
		
		OzonRocketApi ozonRocketApi = new OzonRocketApi();
		
		Customer customer = new Customer();
		
		customer.setFirstName("Иван");
		customer.setLastName("Сусанин");		
		customer.setEmail("sir-richard@sir-richard.ru");
		customer.setPhoneNumber("(916) 59-69-059");
		
		Order order = new Order();
		order.setCustomer(customer);
		
		order.setNo(11932);
		order.setOrderDate(DateTimeUtils.sysDate());		

		// физик, постоплата, пвз, доставку платит клиент
		order.setOrderType(OrderTypes.ORDER);
		order.setStatus(OrderStatuses.APPROVED);
		order.setPaymentType(PaymentTypes.POSTPAY);
		order.getDelivery().setPaymentDeliveryType(PaymentDeliveryTypes.CUSTOMER);
		order.getDelivery().setDeliveryType(DeliveryTypes.OZON_ROCKET_PICKPOINT);			
		order.getAmounts().setPostpay(new BigDecimal("4350"));
		order.getAmounts().setTotalWithDelivery(new BigDecimal("4350"));
		order.getDelivery().setPrice(new BigDecimal("100"));
				
		order.getDelivery().getAddress().setAddress("Псков, Инженерная 16");
		order.getDelivery().getAddress().getCarrierInfo().setDeliveryVariantId(1011000000002626L);
					
		
		Product productOne = new Product();
		productOne.setSku("UST-SAPSAN");
		productOne.setModel("UST-SAPSAN");
		productOne.setName("Отпугиватель птиц Сапсан-3");
				
		OrderItem orderItemOne = new OrderItem();
		orderItemOne.setProduct(productOne);
		orderItemOne.setQuantity(1);
		orderItemOne.setDiscountRate(new BigDecimal("0.00"));
		orderItemOne.setPrice(BigDecimal.valueOf(4250));
		orderItemOne.setSupplierAmount(BigDecimal.valueOf(2600));
		orderItemOne.setAmount(BigDecimal.valueOf(4250));						
		order.getItems().add(orderItemOne);		
		
		String trackCode = ozonRocketApi.addOrder(order);
		assertNotNull(trackCode);
	}
	
	//@Test
	public void testOrderStatus() {
		
		OzonRocketApi ozonRocketApi = new OzonRocketApi();
		
		Order order = new Order();
		order.setNo(11932);
		
		CarrierStatuses status = ozonRocketApi.getStatus(order);
		assertNotEquals(CarrierStatuses.UNKNOWN.getId(), status.getId());
			
		
		
	}
	
	//@Test
	public void testThree() throws UnirestException, JSONException {
		
		Map<String, String> headers = new HashMap<>();
		headers.put("content-type", "application/x-www-form-urlencoded");
		
		Map<String, Object> fields = new HashMap<>();
		fields.put("grant_type", "client_credentials");
		fields.put("client_id", "Principal_22855192632000_9c0295ab-bcd2-4237-b946-39322a531c9e");
		fields.put("client_secret", "Cp6H9PnItPJUshZiEIguPULEzHHa08+k+RU+wLM1UpI=");
		
		HttpResponse<JsonNode> jsonResponse = Unirest.post("https://xapi.ozon.ru/principal-auth-api/connect/token")
				.headers(headers)
				.fields(fields)
				.asJson();	
		
		assertEquals(200, jsonResponse.getStatus());		
		JSONObject myObj = jsonResponse.getBody().getObject();			
    	String accessToken = (String) myObj.get("access_token");
    	
    	headers = new HashMap<>();
    	headers.put("content-type", "application/json");
    	headers.put("authorization", "Bearer " + accessToken);
    	
    	fields = new HashMap<>();
		fields.put("deliveryType", "PickPoint");
		fields.put("client_id", "Principal_22855192632000_9c0295ab-bcd2-4237-b946-39322a531c9e");
		fields.put("client_secret", "Cp6H9PnItPJUshZiEIguPULEzHHa08+k+RU+wLM1UpI=");
		
		String bodyJson = "{\"deliveryType\": \"PickPoint\", \"filter\": {\"isCashAvailable\": true, \"isPaymentCardAvailable\": true, \"isAnyPaymentAvailable\": true}, \"address\": \"Москва, Байкальская 18к1\", \"radius\": 1}";
				
    	HttpResponse<JsonNode> jsonResponseDeliveryVariantsByAddress = Unirest.post("https://xapi.ozon.ru/principal-integration-api/v1/delivery/variants/byaddress")
				.headers(headers)
				.body(bodyJson)
				.asJson();    	
    	assertEquals(200, jsonResponseDeliveryVariantsByAddress.getStatus());
	}

}
