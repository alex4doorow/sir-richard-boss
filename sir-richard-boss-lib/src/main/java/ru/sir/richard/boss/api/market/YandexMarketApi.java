package ru.sir.richard.boss.api.market;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.PropertyResolver;

import ru.sir.richard.boss.model.data.Order;
import ru.sir.richard.boss.model.data.OrderDeliveryShipment;
import ru.sir.richard.boss.model.data.OrderExternalCrm;
import ru.sir.richard.boss.model.data.Product;
import ru.sir.richard.boss.model.types.CrmTypes;
import ru.sir.richard.boss.model.types.OrderStatuses;
import ru.sir.richard.boss.model.utils.NumberUtils;

/**
 * doc https://yandex.ru/dev/market/partner-marketplace-cd/doc/dg/reference/put-campaigns-id-orders-id-status.html 
 * @author alex4doorow
 *
 */
public class YandexMarketApi {
	
	private final Logger logger = LoggerFactory.getLogger(YandexMarketApi.class);
	
	/**
	 * application.properties
	 */	
	private final PropertyResolver environment;
		
	private YandexMarketApi() {
		super();
		this.environment = null;
	}

	public YandexMarketApi(PropertyResolver environment) {
		super();
		this.environment = environment;		
	}
	
	public YandexMarketApi(String staticUrlApi, 
			int firstWarehouseCompaignId, int secondWarehouseCompaignId, int thirdWarehouseCompaignId,
			String oauthToken, String oauthClientId) {		
		this();	
	}
	
	private HttpEntityEnclosingRequestBase postSetHeader(HttpEntityEnclosingRequestBase post) {
		post.setHeader("Content-type", "application/json");			
		post.setHeader("Authorization", "OAuth oauth_token=\"" + environment.getProperty("yandex.market.oauth.token") + "\", oauth_client_id=\"" + environment.getProperty("yandex.market.oauth.client") + "\"");
		return post;
	}
	
	// POST /campaigns/{campaignId}/offer-prices/updates
	public String offerPricesUpdatesV1(List<Product> products) {
				
		final String url = environment.getProperty("yandex.market.url") + environment.getProperty("yandex.market.warehouse.compaign.first") + "/offer-prices/updates.json";
		
		String result = "";
		String inputJson = "{\"offers\": \r\n" + 
				"  [";
		for (Product product : products) {
			
			String stringPrice;
			if (product.getMarket(CrmTypes.YANDEX_MARKET).getSpecialPrice() == null || product.getMarket(CrmTypes.YANDEX_MARKET).getSpecialPrice().equals(BigDecimal.ZERO)) {
				stringPrice = NumberUtils.localeFormatNumber(product.getPrice(), new Locale("en", "UK"), '.', "#########.00");
			} else {
				stringPrice = NumberUtils.localeFormatNumber(product.getMarket(CrmTypes.YANDEX_MARKET).getSpecialPrice(), new Locale("en", "UK"), '.', "#########.00");
			}
			String item = 
			"    \r\n" + 					
			"    {\r\n" + 
			"      \"marketSku\": "+ product.getMarket(CrmTypes.YANDEX_MARKET).getMarketSku() + ",\r\n" + 
			"      \"price\": \r\n" + 
			"      {\r\n" + 
			"        \"currencyId\": \"RUR\",\r\n" + 
			"        \"value\": " + stringPrice + "\r\n" + 
			"      }\r\n" + 
			"    }";
			
			inputJson += item + ",";
		}
		inputJson = StringUtils.substring(inputJson, 0, inputJson.length() - 1); 
		inputJson += "\r\n  ]\r\n" + 
				"}";
		logger.debug("offerPricesUpdates() inputJson:{}", inputJson);        
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
        	    
        	    String status = (String) myResponse.get("status");        	    
        	    if (status.equals("OK")) {  
        	    	logger.debug("status: {}", status);
        	    	result = "status: " + status;
        	    	
        	    } else if (status.equals("ERROR")) {
        	    	JSONArray errors = (JSONArray) ((JSONObject) myResponse).get("errors");
        	    	        	    	
        	    	for (int i = 0; i < errors.length(); i++) {
        	    	    JSONObject error = errors.getJSONObject(i);
        	    	    String code = error.getString("code");
        	    	    String message = error.getString("message");
        	    	    
        	    	    logger.error("status: {}, {}, {}", status, code, message);
            	    	result = "status: " + status + " [" + code + "] " + message;            	    	
        	    	} 
        	    }
            }

		} catch (Exception ex) {
			logger.error("Exception: ", ex);
			result = "status: EXCEPTION " + ex.getMessage();
		} finally {
			//httpClient.close();
		}	
		return result;
	}
	
	// POST /campaigns/{campaignId}/offer-prices/updates
	public String offerPricesUpdatesV2(int warehouseCompainId, List<Product> products) {
				
		final String url = environment.getProperty("yandex.market.url") + warehouseCompainId + "/offer-prices/updates.json";
		
		String result = "";
		String inputJson = "{\"offers\": \r\n" + 
				"  [";
		for (Product product : products) {
			
			String stringPrice;
			if (product.getMarket(CrmTypes.YANDEX_MARKET).getSpecialPrice() == null || product.getMarket(CrmTypes.YANDEX_MARKET).getSpecialPrice().equals(BigDecimal.ZERO)) {
				stringPrice = NumberUtils.localeFormatNumber(product.getPrice(), new Locale("en", "UK"), '.', "#########.00");
			} else {
				stringPrice = NumberUtils.localeFormatNumber(product.getMarket(CrmTypes.YANDEX_MARKET).getSpecialPrice(), new Locale("en", "UK"), '.', "#########.00");
			}
			String item = 
			"    \r\n" +			
			"    {\r\n" + 
			"      \"id\": "+ product.getSku() + ",\r\n" +
			"      \"price\": \r\n" + 
			"      {\r\n" + 
			"        \"currencyId\": \"RUR\",\r\n" + 
			"        \"value\": " + stringPrice + "\r\n" + 
			"      }\r\n" + 
			"    }";
			
			inputJson += item + ",";
		}
		inputJson = StringUtils.substring(inputJson, 0, inputJson.length() - 1); 
		inputJson += "\r\n  ]\r\n" + 
				"}";
		logger.debug("offerPricesUpdates() inputJson:{}", inputJson);        
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
        	    logger.debug("offerPricesUpdates() jsonResponse: {}", myResponse.toString());        	    
        	    
        	    String status = (String) myResponse.get("status");        	    
        	    if (status.equals("OK")) {  
        	    	logger.debug("status: {}", status);
        	    	result = "status: " + status;
        	    	
        	    } else if (status.equals("ERROR")) {
        	    	JSONArray errors = (JSONArray) ((JSONObject) myResponse).get("errors");
        	    	        	    	
        	    	for (int i = 0; i < errors.length(); i++) {
        	    	    JSONObject error = errors.getJSONObject(i);
        	    	    String code = error.getString("code");
        	    	    String message = error.getString("message");
        	    	    
        	    	    logger.error("status: {}, {}, {}", status, code, message);
            	    	result = "status: " + status + " [" + code + "] " + message;            	    	
        	    	} 
        	    }
            }

		} catch (Exception ex) {
			logger.error("Exception: ", ex);
			result = "status: EXCEPTION " + ex.getMessage();
		} finally {
			//httpClient.close();
		}	
		return result;
	}	
	
	public String offerPricesUpdatesByAllWarehouses(List<Product> products) {
		String result = "";
		result += "FIRST: " + offerPricesUpdatesV2(environment.getProperty("yandex.market.warehouse.compaign.first", Integer.class), products);
		result += ", SECOND: " + offerPricesUpdatesV2(environment.getProperty("yandex.market.warehouse.compaign.second", Integer.class), products);
		result += ", THIRD: " + offerPricesUpdatesV2(environment.getProperty("yandex.market.warehouse.compaign.third", Integer.class), products);		
		return result;
	}
	
	public Order order(int ymOrderId, Order order) {
		
		// GET /campaigns/{campaignId}/orders/{ymOrderId}
		
		int shipmentId = -1;
		Order result = new Order();
		final String url = environment.getProperty("yandex.market.url") + getWarehouseCompainId(order) + "/orders/" + ymOrderId + ".json";
		
		URL obj;
		JSONObject myResponse = null;
		try {
			obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			
		    con.setRequestProperty("Content-type", "application/json");			
			con.setRequestProperty("Authorization", "OAuth oauth_token=\"" + environment.getProperty("yandex.market.oauth.token") + "\", oauth_client_id=\"" + environment.getProperty("yandex.market.oauth.client") + "\"");
		    		  
		    int responseCode = con.getResponseCode();
		    logger.debug("offerMappingEntries() responseCode:{}", responseCode);
		    logger.debug("offerMappingEntries() sending 'GET' request to URL:{}", url);
		    
		    BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"));
		    String inputLine;
		    StringBuffer response = new StringBuffer();
		    while ((inputLine = in.readLine()) != null) {
		     	response.append(inputLine);
		    }
		    in.close();
		    logger.debug("response: {}", response);
		    		    
		    myResponse = new JSONObject(response.toString());
    	    logger.debug("offerMappingEntries() jsonResponse:{}", myResponse.toString());        	    
    	       	        
    	    JSONObject jsonDelivery = myResponse.getJSONObject("order").getJSONObject("delivery");
    	    
    	    String deliveryType = jsonDelivery.getString("type");
    	    String deliveryName = jsonDelivery.getString("serviceName");
    	    
    	    String regionName = jsonDelivery.getJSONObject("region").getString("name");
    	    String parentRegionName = jsonDelivery.getJSONObject("region").getJSONObject("parent").getString("name");
    	    String parentParentRegionName = jsonDelivery.getJSONObject("region").getJSONObject("parent").getJSONObject("parent").getString("name");
    	    String addressText = regionName + ", " + parentRegionName + ", " + parentParentRegionName + ", [" + deliveryType + ", " + deliveryName + "]";
    	       	        	    
    	   	result.getDelivery().getAddress().setAddress(addressText.trim());    	    	
    	    
    	    JSONArray jsonShipments = myResponse.getJSONObject("order").getJSONObject("delivery").getJSONArray("shipments");    	    
    	    if (jsonShipments.length() > 0) {
    	    	JSONObject o = (JSONObject) jsonShipments.get(0); 
    	    	shipmentId = (int) o.get("id");
    	    	
    	    	OrderDeliveryShipment shipment = new OrderDeliveryShipment(shipmentId);
    	    	String fulfilmentCode = String.valueOf(ymOrderId) + "-" + 1;
    	    
    	    	shipment.setFulfilmentCode(fulfilmentCode);
    	    	
    	    	List<OrderDeliveryShipment> shipments = new ArrayList<OrderDeliveryShipment>();
    	    	shipments.add(shipment);
    	    	result.getDelivery().setShipments(shipments);
    	    }  	    
			
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;		
	}
	
	public Order order(Order realOrder) {
		
		int ymOrderId = -1;				
		for (OrderExternalCrm orderExternalCrm : realOrder.getExternalCrms()) {
			if (orderExternalCrm.getCrm() == CrmTypes.YANDEX_MARKET && orderExternalCrm.getParentId() > 0) {
				ymOrderId = orderExternalCrm.getParentId();
				break;
			}
		}		
		if (ymOrderId > 0) {
			return order(ymOrderId, realOrder);
		}	
		return null;
	}
	
	public boolean boxes(int ymOrderId, Order order) {
				
		boolean resultRequest = false;
		
		int shipmentId = -1;
		String fulfilmentCode = "";	
		
		final int weight = 500;
		final int width = 20;
		final int height = 20;
		final int depth = 20;
		
		Order ymOrder = order(ymOrderId, order);
		if (ymOrder.getDelivery().getShipments().size() > 0) {
			shipmentId = ymOrder.getDelivery().getShipments().get(0).getId();
			fulfilmentCode = ymOrder.getDelivery().getShipments().get(0).getFulfilmentCode();			
		} 
		final String url = environment.getProperty("yandex.market.url") + getWarehouseCompainId(order) + "/orders/" + ymOrderId + "/delivery/shipments/" + shipmentId + "/boxes.json";
		
		String inputJson = "" +
				"{\r\n" +
				"\"boxes\":\r\n" + 
				"[\r\n" +
				  "{\r\n" +
				
				    "\"fulfilmentId\": \"" + fulfilmentCode + "\"," +
				    "\"weight\": " + weight + "," +
				    "\"width\": " + width + "," +
				    "\"height\": " + height + "," +
				    "\"depth\": " + depth + "" + 
				  "},\r\n" +
				  
				"]\r\n" +
				"}\r\n";				

		logger.debug("status() inputJson:{}", inputJson);        
		JSONObject inputJsonObj = new JSONObject(inputJson);

		CloseableHttpClient httpClient = HttpClientBuilder.create().build();
		JSONObject myResponse = null;
		try {

			HttpEntityEnclosingRequestBase put = new HttpPut(url);

			StringEntity postingString = new StringEntity(inputJsonObj.toString(), "UTF-8");
			put.setEntity(postingString);

			put = postSetHeader(put);
			HttpResponse response = httpClient.execute(put);			
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
			    logger.debug("status() jsonResponse:{}", myResponse.toString()); 
			    
			    String status = (String) myResponse.get("status");        	    
        	    if (status.equals("OK")) {  
        	    	logger.debug("status: {}", status);
        	    	resultRequest = true;
        	    	
        	    } else if (status.equals("ERROR")) {
        	    	JSONObject parcelData = (JSONObject) myResponse.get("errors");
        	    	String code = (String) parcelData.get("code");
        	    	String message = (String) parcelData.get("message");        	    	
        	    	logger.error("status: {}, {}, {}", status, code, message);      	    	
        	    }	    	    			    
			}
			
			} catch (Exception ex) {
				logger.error("Exception: ", ex);
			} finally {
				//httpClient.close();
			}	
			return resultRequest;		
	}	
	
	public boolean getFirstWarehouse(Order order) {
		if (order == null || order.getCustomer() == null || order.getCustomer().getId() <= 0) {
			return true;
		} else if (order.getCustomer().getId() == 6611) {
			return true; 
		} else if (order.getCustomer().getId() == 7204) {
			return false; 
		} else if (order.getCustomer().getId() == 7277) {
			return false; 
		} else {
			return true;
		}
	}	
	
	private int getWarehouseCompainId(Order order) {
		if (order == null || order.getCustomer() == null || order.getCustomer().getId() <= 0) {
			return environment.getProperty("yandex.market.warehouse.compaign.first", Integer.class);
		} else if (order.getCustomer().getId() == 6611) {
			return environment.getProperty("yandex.market.warehouse.compaign.first", Integer.class); 
		} else if (order.getCustomer().getId() == 7204) {
			return environment.getProperty("yandex.market.warehouse.compaign.second", Integer.class); 
		} else if (order.getCustomer().getId() == 7277) {
			return environment.getProperty("yandex.market.warehouse.compaign.third", Integer.class); 
		} else {
			return environment.getProperty("yandex.market.warehouse.compaign.first", Integer.class);
		} 
	}

	public String status(Order order) {
				
		int ymOrderId = -1;
		String status = "";
		String subStatus = "";		
		for (OrderExternalCrm orderExternalCrm : order.getExternalCrms()) {
			if (orderExternalCrm.getCrm() == CrmTypes.YANDEX_MARKET && orderExternalCrm.getParentId() > 0) {
				ymOrderId = orderExternalCrm.getParentId();
				break;
			}
		}
		
		String result = "";		
		if (ymOrderId <= 0) {
			return result;
		} 	
		
		if (order.getStatus() == OrderStatuses.APPROVED) {
					
			boolean resultRequestBoxes = boxes(ymOrderId, order);
			if (!resultRequestBoxes) {
				return result;
			}
			
			status = "PROCESSING";
			subStatus = "READY_TO_SHIP";
			 
		} else if (order.getStatus() == OrderStatuses.DELIVERING) {
			status = "PROCESSING";
			subStatus = "SHIPPED";	
			
		} else if (order.getStatus() == OrderStatuses.CANCELED) {
			status = "CANCELLED";
			return result; // мы отменяем когда маркет уже отменил
		} else {
			return result;
		}
				
		final String url = environment.getProperty("yandex.market.url") + getWarehouseCompainId(order) + "/orders/" + ymOrderId + "/status.json";			
		String inputJson = "" +				
							"{\r\n" +
							"\"order\":\r\n" + 
							"{\r\n" + 
							"  \"status\": \"" + status + "\",\r\n" + 
							"  \"substatus\": \""+ subStatus + "\"\r\n" + 
							"}\r\n" + 
							"}\r\n";
			
		
		logger.debug("status() inputJson:{}", inputJson);        
		JSONObject inputJsonObj = new JSONObject(inputJson);
	
		CloseableHttpClient httpClient = HttpClientBuilder.create().build();
		JSONObject myResponse = null;
		try {

			HttpEntityEnclosingRequestBase put = new HttpPut(url);
			
			StringEntity postingString = new StringEntity(inputJsonObj.toString(), "UTF-8");
			put.setEntity(postingString);
			
			put = postSetHeader(put);
			HttpResponse response = httpClient.execute(put);			
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
        	    logger.debug("status() jsonResponse:{}", myResponse.toString()); 
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
