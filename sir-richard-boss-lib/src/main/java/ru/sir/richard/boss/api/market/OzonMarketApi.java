package ru.sir.richard.boss.api.market;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.PropertyResolver;

import ru.sir.richard.boss.model.data.Address;
import ru.sir.richard.boss.model.data.Customer;
import ru.sir.richard.boss.model.data.Order;
import ru.sir.richard.boss.model.data.OrderExternalCrm;
import ru.sir.richard.boss.model.data.OrderItem;
import ru.sir.richard.boss.model.data.OrderStatusItem;
import ru.sir.richard.boss.model.data.Product;
import ru.sir.richard.boss.model.data.crm.OzonResult;
import ru.sir.richard.boss.model.data.crm.OzonResultBean;
import ru.sir.richard.boss.model.types.AddressTypes;
import ru.sir.richard.boss.model.types.Countries;
import ru.sir.richard.boss.model.types.CrmTypes;
import ru.sir.richard.boss.model.types.DeliveryTypes;
import ru.sir.richard.boss.model.types.OrderAdvertTypes;
import ru.sir.richard.boss.model.types.OrderSourceTypes;
import ru.sir.richard.boss.model.types.OrderStatuses;
import ru.sir.richard.boss.model.types.OrderTypes;
import ru.sir.richard.boss.model.types.PaymentDeliveryTypes;
import ru.sir.richard.boss.model.types.PaymentTypes;
import ru.sir.richard.boss.model.types.StoreTypes;
import ru.sir.richard.boss.model.utils.DateTimeUtils;
import ru.sir.richard.boss.model.utils.NumberUtils;
import ru.sir.richard.boss.model.utils.Pair;
import ru.sir.richard.boss.model.utils.TextUtils;

// https://docs.ozon.ru/api/seller/#tag/Introduction
public class OzonMarketApi {
	
	private final Logger logger = LoggerFactory.getLogger(OzonMarketApi.class);
	
	/**
	 * application.properties
	 */	
	private final PropertyResolver environment;
	
	public OzonMarketApi(PropertyResolver environment) {
		super();
		this.environment = environment;
	}

	private HttpEntityEnclosingRequestBase postSetHeader(HttpEntityEnclosingRequestBase post) {
		post.setHeader("Host", environment.getProperty("ozon.market.host"));
		post.setHeader("Client-Id", environment.getProperty("ozon.market.client.id"));
		post.setHeader("Api-Key", environment.getProperty("ozon.market.client.key"));
		post.setHeader("Content-type", "application/json");	
		return post;
	}
	
	// https://api-seller.ozon.ru/v3/posting/fbs/get
	public Order getOrder(String ozonOrderId) {
	
		final String url = environment.getProperty("ozon.market.url") + "/v3/posting/fbs/get";		
		
		String inputJson = 
		"{" +
		    "\"posting_number\": \"" + ozonOrderId + "\"," +
		    "\"with\": {" +
		        "\"analytics_data\": true," +
		        "\"barcodes\": true," +
		        "\"financial_data\": true" +
		    "}" +
		"}";
		
		JSONObject inputJsonObj = new JSONObject(inputJson);		
		CloseableHttpClient httpClient = HttpClientBuilder.create().build();
		JSONObject myResponse = null;
		
		Order order = null;
		try {

			HttpEntityEnclosingRequestBase post = new HttpPost(url);
			StringEntity postingString = new StringEntity(inputJsonObj.toString(), "UTF-8");
						
			post.setEntity(postingString);			
			post = postSetHeader(post);

			HttpResponse response = httpClient.execute(post);			
			if (response != null) {
                InputStream inS = response.getEntity().getContent(); //Get the data in the entity
                
                BufferedReader in = new BufferedReader(new InputStreamReader(inS, "utf-8"));
                String inputLine;
        	    StringBuffer responseB = new StringBuffer();
        	    while ((inputLine = in.readLine()) != null) {
        	     	responseB.append(inputLine);
        	    }
        	    in.close();                
        	    
        	    myResponse = new JSONObject(responseB.toString());
        	    logger.debug("getOrder() jsonResponse:{}", myResponse.toString());
        	           	         	    
        	    JSONObject myResponseResults = myResponse.getJSONObject("result");        	            	    	
        	    order = createOrderByOzonPosting1(myResponseResults, false);
            }

		} catch (Exception ex) {
			logger.error("Exception: ", ex);
		} finally {
			try {
				httpClient.close();
			} catch (IOException e) {
				logger.error("httpClient.close(): EXCEPTION ", e);

			}
		}	
		return order;
	}
	
	
	private Order createOrderByOzonPosting1(JSONObject postingObject, boolean isCreate) {
		
		logger.debug(postingObject.getString("posting_number"));
		
		Order order = new Order();
		order.setId(postingObject.getInt("order_id"));
		order.setOrderDate(ozonStringToDate(postingObject.getString("in_process_at")));			
		
		order.setSourceType(OrderSourceTypes.LID);                
        order.setStore(StoreTypes.PM);
        order.setAdvertType(OrderAdvertTypes.OZON);      
						
		order.setAnnotation("");				
		/*
		acceptance_in_progress — идёт приёмка,
		awaiting_approve — ожидает подтверждения,
		awaiting_packaging — ожидает упаковки,
		awaiting_deliver — ожидает отгрузки,
		arbitration — арбитраж,
		client_arbitration — клиентский арбитраж доставки,
		delivering — доставляется,
		driver_pickup — у водителя,
		delivered — доставлено,
		cancelled — отменено,
		not_accepted — не принят на сортировочном центре.
		*/		
		order.setStatus(OrderStatuses.BID);
		if (!isCreate) {
			if (postingObject.getString("status").equalsIgnoreCase("awaiting_approve")) {
				order.setStatus(OrderStatuses.BID);			
			} else if (postingObject.getString("status").equalsIgnoreCase("awaiting_packaging")) {
				order.setStatus(OrderStatuses.BID);			
			} else if (postingObject.getString("status").equalsIgnoreCase("awaiting_deliver")) {
				order.setStatus(OrderStatuses.APPROVED);			
			} else if (postingObject.getString("status").equalsIgnoreCase("delivering")) {
				order.setStatus(OrderStatuses.DELIVERING);		
			} else if (postingObject.getString("status").equalsIgnoreCase("delivered")) {
				order.setStatus(OrderStatuses.DELIVERED);			
			} else if (postingObject.getString("status").equalsIgnoreCase("cancelled")) {
				order.setStatus(OrderStatuses.CANCELED);			
			} else {
				order.setStatus(OrderStatuses.UNKNOWN);
			}  
			OrderStatusItem orderStatusItem = new OrderStatusItem(order);
			orderStatusItem.setAddedDate(new Date());			
			orderStatusItem.setStatus(order.getStatus());
			orderStatusItem.setCrmStatus(postingObject.getString("status"));
			orderStatusItem.setCrmSubStatus("");
			order.addStatusItem(orderStatusItem);
		}
		
		String deliveryAddressText = "";
		Customer customer = new Customer();
		customer.setCountry(Countries.RUSSIA);		
		Address deliveryAddress = new Address(Countries.RUSSIA, AddressTypes.MAIN);
		if (postingObject.getJSONObject("delivery_method").getInt("tpl_provider_id") == environment.getProperty("ozon.market.ozon.provider", Long.class)) {			
			deliveryAddressText = "МО, г. Балашиха, 1-е Мая, д.25";			
			customer.setFirstName("Озон");
			customer.setLastName("Маркетов");			
			customer.setPhoneNumber("(800) 234-70-00");				
			customer.setEmail("help@ozon.ru");			
			Address customerAddress = customer.getMainAddress();
			
			JSONObject analiticsData = postingObject.getJSONObject("analytics_data");
			if (analiticsData != null) {
				deliveryAddressText = analiticsData.getString("region") + ", " + analiticsData.getString("city");	
			}
			customerAddress.setAddress(deliveryAddress.getAddress());				
		} else {
			JSONObject customerObject = postingObject.getJSONObject("customer");
			deliveryAddressText = StringUtils.trim(StringUtils.defaultString(customerObject.getJSONObject("address").getString("address_tail")));		        
		    order.getDelivery().getAddress().getCarrierInfo().setCityContext(customerObject.getJSONObject("address").getString("city"));
		    order.getDelivery().getAddress().getCarrierInfo().setRegion(customerObject.getJSONObject("address").getString("region"));
		    order.getDelivery().getAddress().getCarrierInfo().setPvz(customerObject.getJSONObject("address").getString("provider_pvz_code"));
		         					
			customer.setFirstName(customerObject.getString("name"));			
			customer.setPhoneNumber(TextUtils.formatPhoneNumber(customerObject.getString("phone")));				
			customer.setEmail(customerObject.getString("customer_email"));
			
			Address customerAddress = customer.getMainAddress();
			customerAddress.setAddress(deliveryAddress.getAddress());			
		}                  
        order.setOrderType(OrderTypes.ORDER);
    	order.setPaymentType(PaymentTypes.POSTPAY);
    	
    	// posting.getJSONObject("delivery_method").getString("name");
    	if (postingObject.getJSONObject("delivery_method").getLong("id") == environment.getProperty("ozon.market.cdek.delivery.courier.main", Long.class)) { 
    		order.getDelivery().setDeliveryType(DeliveryTypes.CDEK_COURIER);	
    	} else if (postingObject.getJSONObject("delivery_method").getLong("id") == environment.getProperty("ozon.market.cdek.delivery.courier.economy", Long.class)) { 
    		order.getDelivery().setDeliveryType(DeliveryTypes.CDEK_COURIER_ECONOMY);	
    	} else if (postingObject.getJSONObject("delivery_method").getLong("id") == environment.getProperty("ozon.market.cdek.delivery.pvz.main", Long.class)) { 
    		order.getDelivery().setDeliveryType(DeliveryTypes.CDEK_PVZ_TYPICAL);	
    	} else if (postingObject.getJSONObject("delivery_method").getLong("id") == environment.getProperty("ozon.market.cdek.delivery.pvz.economy", Long.class)) { 
    		order.getDelivery().setDeliveryType(DeliveryTypes.CDEK_PVZ_ECONOMY);	
    	} else if (postingObject.getJSONObject("delivery_method").getLong("id") == environment.getProperty("ozon.market.ozon.warehouse.pickup", Long.class)) { 
    		order.getDelivery().setDeliveryType(DeliveryTypes.OZON_FBS);	
    	}
    	   	
        deliveryAddress.setAddress(deliveryAddressText);
        order.getDelivery().setAddress(deliveryAddress);
        
        order.getDelivery().getAddress().getCarrierInfo().getCourierInfo().setDeliveryDate(ozonStringToDate(postingObject.getString("shipment_date")));
        
        order.getDelivery().setPaymentDeliveryType(PaymentDeliveryTypes.CUSTOMER);		                
        order.getDelivery().setTrackCode(postingObject.getString("tracking_number"));
        
        //order.setProductCategory(wikiDao.getCategoryById(0));            										
        order.setCustomer(customer);   
        
		OrderExternalCrm crmOzon = new OrderExternalCrm(order);
		crmOzon.setCrm(CrmTypes.OZON);
		crmOzon.setId(postingObject.getInt("order_id"));
		crmOzon.setParentCode(postingObject.getString("posting_number"));		
		crmOzon.setParentId(postingObject.getInt("order_id"));
       	order.getExternalCrms().add(crmOzon);       	
        order = setPmCrmOrderItems(postingObject, order);
   		
		return order;
	}	
	
	// POST https://api-seller.ozon.ru/v3/posting/fbs/list
	public List<Order> getOrders(Pair<Date> period, String ozonStatus) {
				
		List<Order> orders = new ArrayList<Order>();
		final String url = environment.getProperty("ozon.market.url") + "/v3/posting/fbs/list";	
				
		//dateQuery = DateTimeUtils.formatDate(calculateDate, "yyyy-MM-dd") + "T" + DateTimeUtils.formatDate(calculateDate, "HH:mm:ss");
		
		/*
		acceptance_in_progress — идёт приёмка,
		awaiting_approve — ожидает подтверждения,
		awaiting_packaging — ожидает упаковки,
		awaiting_deliver — ожидает отгрузки,
		arbitration — арбитраж,
		client_arbitration — клиентский арбитраж доставки,
		delivering — доставляется,
		driver_pickup — у водителя,
		delivered — доставлено,
		cancelled — отменено,
		not_accepted — не принят на сортировочном центре.
		*/
		
		String inputJson = 
		"{" +
		    "\"dir\": \"asc\"," +
		    "\"filter\": {" +
		        "\"delivery_method_id\": [" +
		    
					environment.getProperty("ozon.market.cdek.delivery.courier.main") + ", " + 
					environment.getProperty("ozon.market.cdek.delivery.courier.economy") + ", " +
					environment.getProperty("ozon.market.cdek.delivery.pvz.main") + ", " +
					environment.getProperty("ozon.market.cdek.delivery.pvz.economy") + ", " +
					environment.getProperty("ozon.market.ozon.warehouse.pickup") + ", " + 		        	
		        	
		        "]," +
		        "\"order_id\": 0," +
		        "\"provider_id\": [" + environment.getProperty("ozon.market.ozon.provider") + "," + environment.getProperty("ozon.market.cdek.provider") + "]," +
		        "\"since\": \"" + ozonDateToString(period.getStart()) + "\"," +
		        "\"to\": \"" + ozonDateToString(period.getEnd()) + "\"," +
		        "\"status\": \"" + ozonStatus + "\"," +
		        "\"warehouse_id\": [" + 
		        	environment.getProperty("ozon.market.cdek.warehouse") + "," +
		        	environment.getProperty("ozon.market.ozon.warehouse.pickup") +
		        	"]" +
		    "}," +
		    "\"limit\": 50," +
		    "\"offset\": 0," +
		    "\"with\": {" +
		        "\"analytics_data\": true," +
		        "\"barcodes\": true," +
		        "\"financial_data\": true" +
		    "}" +
		"}";
		
		JSONObject inputJsonObj = new JSONObject(inputJson);		
		CloseableHttpClient httpClient = HttpClientBuilder.create().build();
		JSONObject myResponse = null;
		try {

			HttpEntityEnclosingRequestBase post = new HttpPost(url);
			StringEntity postingString = new StringEntity(inputJsonObj.toString(), "UTF-8");
						
			post.setEntity(postingString);			
			post = postSetHeader(post);

			HttpResponse response = httpClient.execute(post);			
			if (response != null) {
                InputStream inS = response.getEntity().getContent(); //Get the data in the entity
                
                BufferedReader in = new BufferedReader(new InputStreamReader(inS, "utf-8"));                
        	    String inputLine;
        	    StringBuffer responseB = new StringBuffer();
        	    while ((inputLine = in.readLine()) != null) {
        	     	responseB.append(inputLine);
        	    }
        	    in.close();                
        	    
        	    myResponse = new JSONObject(responseB.toString());
        	    logger.debug("getOrders() jsonResponse:{}", myResponse.toString());
        	           	            	    
        	    //JSONArray myResponseResults = myResponse.getJSONArray("result");
        	    
        	    if (myResponse.getJSONObject("result").get("postings") == null) {
        	    	logger.debug("getOrders() jsonResponse:{}", myResponse.toString());
        	    	return orders;
        	    	
        	    }
        	    
        	    JSONArray myResponseResults = myResponse.getJSONObject("result").getJSONArray("postings");        	    
        	    for (int i = 0; i < myResponseResults.length(); i++) {        	    	
        	    	Order order = createOrderByOzonPosting1(myResponseResults.getJSONObject(i), true);
        	    	orders.add(order);        	    	
        	    }
            }

		} catch (Exception ex) {
			logger.error("Exception: ", ex);
		} finally {
			try {
				httpClient.close();
			} catch (IOException e) {
				logger.error("httpClient.close(): EXCEPTION ", e);

			}
		}			
		
		return orders;
	} 
	
	private Date ozonStringToDate(String sDate) {
		Date result = null;
		try {
			result = DateTimeUtils.stringToDate(sDate.replace('T', ' '), "yyyy-MM-dd hh:mm:SSS");
		} catch (JSONException e) {
			logger.error("JSONException", e);
		} catch (ParseException e) {
			logger.error("ParseException", e);

		}
		return result;
	}
	
	private String ozonDateToString(Date date) {
		
		return DateTimeUtils.formatDate(date, "yyyy-MM-dd") + 'T' + DateTimeUtils.formatDate(date,"hh:mm:SS") + 'Z';		
	}

	private Order setPmCrmOrderItems(JSONObject posting, Order crmOrder) {
		
		List<OrderItem> orderItems = new ArrayList<OrderItem>();
		JSONArray productsObjects = posting.getJSONArray("products");
		
		for (int i = 0; i < productsObjects.length(); i++) {
			
			OrderItem orderItem = new OrderItem();
			productsObjects.getJSONObject(i).getString("price");
			

			orderItem.setNo(i);
			
			Product product = new Product();
			product.setId(productsObjects.getJSONObject(i).getInt("sku"));
			product.setSku(productsObjects.getJSONObject(i).getString("offer_id"));
			product.setName(productsObjects.getJSONObject(i).getString("name"));
						
			orderItem.setProduct(product);
			//orderItem.setSupplierAmount(product.getSupplierPrice());			
			orderItem.setQuantity(productsObjects.getJSONObject(i).getInt("quantity"));
			
			double dPrice = productsObjects.getJSONObject(i).getDouble("price");
			orderItem.setPrice(BigDecimal.valueOf(dPrice));
			//orderItem.setAmount(rs.getBigDecimal("TOTAL"));						
			//crmOrder.setProductCategory(orderItem.getProduct().getCategory());			
			orderItems.add(orderItem);
	    	
	    }
		crmOrder.setItems(orderItems);
		return crmOrder;
	} 
	
	
	// POST https://api-seller.ozon.ru/v1/product/import/stocks	
	public OzonResult offerStocks(List<Product> products) {
		
		
		if (products == null || products.size() == 0) {
			return new OzonResult();
		}

		final String url = environment.getProperty("ozon.market.url") + "/v1/product/import/stocks";	

		OzonResult ozonResult = new OzonResult();
		String inputJson = 
				
				"{" +
					    "\"stocks\": [";
		
		for (Product product : products) {
			
			if (StringUtils.isEmpty(product.getMarket(CrmTypes.OZON).getMarketSku())) {
				continue;
			}			
			if (!product.getMarket(CrmTypes.OZON).isMarketSeller()) {
				continue;
			}

			String item = "\r\n" +					
					
						        "{\r\n" +
						            "\"offer_id\": \"" + product.getSku() + "\"," + "\r\n" +
						            "\"product_id\": " +  product.getMarket(CrmTypes.OZON).getMarketSku() + "," + "\r\n" +
						            "\"stock\": "+  product.getQuantity() + "\r\n" +
						        "}\r\n";
					
			inputJson += item + ",";			
			
		}
		
		inputJson = StringUtils.substring(inputJson, 0, inputJson.length() - 1); 
		inputJson += "\r\n  ]\r\n" + 
				"}";
				
		logger.debug("offerStoks() inputJson:{}", inputJson);        
		JSONObject inputJsonObj = new JSONObject(inputJson);
	
		CloseableHttpClient httpClient = HttpClientBuilder.create().build();
		JSONObject myResponse = null;
		try {

			HttpEntityEnclosingRequestBase post = new HttpPost(url);
			StringEntity postingString = new StringEntity(inputJsonObj.toString(), "UTF-8");
						
			post.setEntity(postingString);
			
			post = postSetHeader(post);

			HttpResponse response = httpClient.execute(post);			
			if (response != null) {
                InputStream inS = response.getEntity().getContent(); //Get the data in the entity
                
                BufferedReader in = new BufferedReader(new InputStreamReader(inS, "utf-8"));
        	    String inputLine;
        	    StringBuffer responseB = new StringBuffer();
        	    while ((inputLine = in.readLine()) != null) {
        	     	responseB.append(inputLine);
        	    }
        	    in.close();                
        	    
        	    myResponse = new JSONObject(responseB.toString());
        	    logger.debug("offerStocks() jsonResponse:{}", myResponse.toString());
        	    
        	    ozonResult.setDirtyResponce(myResponse.toString());

        	    JSONArray myResponseResults = null;
        	    try {
        	    	myResponseResults = myResponse.getJSONArray("result");        	    	
        	    } catch (org.json.JSONException  e) {
        	    	logger.error("exception result: ", e);
        	    }
        	    
        	    if (myResponseResults == null) {
        	    	 ozonResult.setResponseSuccess(false);
        	    	 return ozonResult;		
        	    } 
        	    ozonResult.setResponseSuccess(true);
        	    for (int i = 0; i < myResponseResults.length(); i++) {
        	    	OzonResultBean resultBean = new OzonResultBean();
        	    	resultBean.setProductId(String.valueOf(myResponseResults.getJSONObject(i).getInt("product_id")));        	    	
        	    	resultBean.setOfferId(myResponseResults.getJSONObject(i).getString("offer_id"));        	    	
        	    	resultBean.setUpdated(myResponseResults.getJSONObject(i).getBoolean("updated"));
        	    	
        	    	resultBean.setErrors(myResponseResults.getJSONObject(i).getJSONArray("errors").toString());
        	    	ozonResult.getResponse().add(resultBean);
        	    	
        	    	boolean isBeanError = false;
        	    	if (!resultBean.isUpdated()) {        	    		
        	    		JSONArray myResponseResultsErrors = myResponseResults.getJSONObject(i).getJSONArray("errors");
        	    		
        	    		for (int ii = 0; ii < myResponseResultsErrors.length(); ii++) {
        	    			String code = myResponseResultsErrors.getJSONObject(ii).getString("code");
        	    			String message = myResponseResultsErrors.getJSONObject(ii).getString("message");    	    			
        	    			
        	    			if (!isBeanError) {
        	    				
        	    				if (StringUtils.equalsIgnoreCase(code, "SKU_STOCK_NOT_CHANGE")) {
            	    				isBeanError = false;
            	    			} else {
            	    				isBeanError = true;
            	    				resultBean.setErrors("[" + code + "] " + message);
            	    				break;
            	    			}
        	    			}
        	    		}
        	    		
        	    		if (ozonResult.isResponseSuccess() && isBeanError) {
            	    		ozonResult.setResponseSuccess(false);
            	    	}
        	    		
        	    	}
        	    	
        	    	
        	    }
            }

		} catch (Exception ex) {
			logger.error("Exception: ", ex);
			//result = "status: EXCEPTION " + ex.getMessage();
		} finally {
			try {
				httpClient.close();
			} catch (IOException e) {
				logger.error("httpClient.close(): EXCEPTION ", e);

			}
		}	
		return ozonResult;		
	}	
	
	// https://api-seller.ozon.ru/v2/products/stocks
	public OzonResult offerWarehouseStocks(List<Product> products) {
		
		if (products == null || products.size() == 0) {
			return new OzonResult();
		}

		final String url = environment.getProperty("ozon.market.url") + "/v2/products/stocks";	

		OzonResult ozonResult = new OzonResult();
		String inputJson = 
				
				"{" +
					    "\"stocks\": [";
		
		int ozonProducts = 0;
		for (Product product : products) {
			
			if (StringUtils.isEmpty(product.getMarket(CrmTypes.OZON).getMarketSku())) {
				continue;
			}
			if (!product.getMarket(CrmTypes.OZON).isMarketSeller()) {
				continue;
			}
			
			int cdekQuantity = 0;
			int ozonPickupQuantity = 0;
			int ozonExpressQuantity = 0;
			int CDEK_QUANTITY_GREATER = 10;
			if (product.getQuantity() > CDEK_QUANTITY_GREATER) {
				cdekQuantity = 2;
				ozonExpressQuantity = 2;
				ozonPickupQuantity = product.getQuantity() - cdekQuantity - ozonExpressQuantity;
			} else {
				ozonPickupQuantity = product.getQuantity();				
			}
			ozonPickupQuantity = ozonPickupQuantity < 0 ? 0 : ozonPickupQuantity;  

			String item = "\r\n" +
						        "{\r\n" +
						            "\"offer_id\": \"" + product.getSku() + "\"," + "\r\n" +
						            "\"product_id\": " +  product.getMarket(CrmTypes.OZON).getMarketSku() + "," + "\r\n" +
						            "\"stock\": "+  cdekQuantity + "," + "\r\n" +
						            "\"warehouse_id\": "+ environment.getProperty("ozon.market.cdek.warehouse") + "\r\n" +
						        "},\r\n" +
							        "{\r\n" +
						            "\"offer_id\": \"" + product.getSku() + "\"," + "\r\n" +
						            "\"product_id\": " +  product.getMarket(CrmTypes.OZON).getMarketSku() + "," + "\r\n" +
						            "\"stock\": "+  ozonPickupQuantity + "," + "\r\n" +
						            "\"warehouse_id\": "+  environment.getProperty("ozon.market.ozon.warehouse.pickup") + "\r\n" +
						        "},\r\n" +
							        "{\r\n" +
						            "\"offer_id\": \"" + product.getSku() + "\"," + "\r\n" +
						            "\"product_id\": " +  product.getMarket(CrmTypes.OZON).getMarketSku() + "," + "\r\n" +
						            "\"stock\": "+  ozonExpressQuantity + "," + "\r\n" +
						            "\"warehouse_id\": "+  environment.getProperty("ozon.market.ozon.warehouse.express") + "\r\n" +
						        "}";
					
			inputJson += item + ",";
			ozonProducts++;			
		}		
		if (ozonProducts == 0) {
			return ozonResult;
		}
		
		inputJson = StringUtils.substring(inputJson, 0, inputJson.length() - 1); 
		inputJson += "\r\n  ]\r\n" + 
				"}";
				
		logger.debug("offerWarehouseStocks() inputJson:{}", inputJson);        
		JSONObject inputJsonObj = new JSONObject(inputJson);
	
		CloseableHttpClient httpClient = HttpClientBuilder.create().build();
		JSONObject myResponse = null;
		try {

			HttpEntityEnclosingRequestBase post = new HttpPost(url);
			StringEntity postingString = new StringEntity(inputJsonObj.toString(), "UTF-8");
						
			post.setEntity(postingString);
			
			post = postSetHeader(post);

			HttpResponse response = httpClient.execute(post);			
			if (response != null) {
                InputStream inS = response.getEntity().getContent(); //Get the data in the entity
                
                BufferedReader in = new BufferedReader(new InputStreamReader(inS, "utf-8"));
        	    String inputLine;
        	    StringBuffer responseB = new StringBuffer();
        	    while ((inputLine = in.readLine()) != null) {
        	     	responseB.append(inputLine);
        	    }
        	    in.close();                
        	    
        	    myResponse = new JSONObject(responseB.toString());
        	    logger.debug("offerStocks() jsonResponse:{}", myResponse.toString());   
        	    ozonResult.setDirtyResponce(myResponse.toString());
        	    ozonResult.setResponseSuccess(true);
            }

		} catch (Exception ex) {
			logger.error("Exception: ", ex);
			//result = "status: EXCEPTION " + ex.getMessage();
		} finally {
			try {
				httpClient.close();
			} catch (IOException e) {
				logger.error("httpClient.close(): EXCEPTION ", e);

			}
		}	
		return ozonResult;		
	}		
	
	
	// POST https://api-seller.ozon.ru/v1/product/import/prices
	public String offerPrices(List<Product> products) {
		
		if (products == null || products.size() == 0) {
			return "";
		}
				
		final String url = environment.getProperty("ozon.market.url") + "/v1/product/import/prices"; 
		
		String result = "";
		
		String inputJson =  				
				
				"{" +
					    "\"prices\": [";
		
		int ozonProducts = 0;
		for (Product product : products) {
			
			if (StringUtils.isEmpty(product.getMarket(CrmTypes.OZON).getMarketSku())) {
				continue;
			}
			if (!product.getMarket(CrmTypes.OZON).isMarketSeller()) {
				continue;
			}
			
			String stringOldPrice = "0";			
			String stringPrice;
			if (product.getMarket(CrmTypes.OZON).getSpecialPrice() == null || product.getMarket(CrmTypes.OZON).getSpecialPrice().equals(BigDecimal.ZERO)) {
				if (product.getPriceWithoutDiscount().compareTo(product.getPrice()) > 0) {
					stringPrice = NumberUtils.localeFormatNumber(product.getPriceWithDiscount(), new Locale("en", "UK"), '.', "#########.00");
					stringOldPrice = NumberUtils.localeFormatNumber(product.getPriceWithoutDiscount(), new Locale("en", "UK"), '.', "#########.00");
					
				} else {
					stringPrice = NumberUtils.localeFormatNumber(product.getPrice(), new Locale("en", "UK"), '.', "#########.00");
				}
			} else {
				stringPrice = NumberUtils.localeFormatNumber(product.getMarket(CrmTypes.OZON).getSpecialPrice(), new Locale("en", "UK"), '.', "#########.00");
			}
			String item = "\r\n" +
						        "{\r\n" +
						            "\"offer_id\": \"" + product.getSku() + "\"," + "\r\n" +						        
						            "\"old_price\": \""+ stringOldPrice + "\"," + "\r\n" +
//						            "\"premium_price\": \"0\"," + "\r\n" +
						            "\"price\": \""+ stringPrice + "\"," + "\r\n" +
						            "\"product_id\": " +  product.getMarket(CrmTypes.OZON).getMarketSku() + "\r\n" +
						        "}";
					
			inputJson += item + ",";
			ozonProducts++;
			
		}
		if (ozonProducts == 0) {
			return result;
		}
		
		inputJson = StringUtils.substring(inputJson, 0, inputJson.length() - 1); 
		inputJson += "\r\n  ]\r\n" + 
				"}";			
		
		
		logger.debug("offerPrices() inputJson:{}", inputJson);        
		JSONObject inputJsonObj = new JSONObject(inputJson);
	
		CloseableHttpClient httpClient = HttpClientBuilder.create().build();
		JSONObject myResponse = null;
		try {

			HttpEntityEnclosingRequestBase post = new HttpPost(url);
			StringEntity postingString = new StringEntity(inputJsonObj.toString(), "UTF-8");
			post.setEntity(postingString);			
			post = postSetHeader(post);

			HttpResponse response = httpClient.execute(post);			
			if (response != null) {
                InputStream inS = response.getEntity().getContent(); //Get the data in the entity
                
                BufferedReader in = new BufferedReader(new InputStreamReader(inS, "utf-8"));
        	    String inputLine;
        	    StringBuffer responseB = new StringBuffer();
        	    while ((inputLine = in.readLine()) != null) {
        	     	responseB.append(inputLine);
        	    }
        	    in.close();                
        	    
        	    myResponse = new JSONObject(responseB.toString());
        	    logger.debug("offerPricesUpdates() jsonResponse:{}", myResponse.toString());        	    

            }

		} catch (Exception ex) {
			logger.error("Exception: ", ex);
			result = "status: EXCEPTION " + ex.getMessage();
		} finally {
			//httpClient.close();
		}	
		return result;
	}

}
