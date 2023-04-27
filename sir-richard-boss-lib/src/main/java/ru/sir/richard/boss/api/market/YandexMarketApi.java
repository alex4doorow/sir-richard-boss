package ru.sir.richard.boss.api.market;

import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.print.PrintService;
import javax.print.PrintServiceLookup;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.printing.PDFPageable;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.core.env.PropertyResolver;

import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

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
@Slf4j
public class YandexMarketApi {

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
	public String offerPricesUpdatesV1(List<Product> products) throws JSONException {
				
		final String url = environment.getProperty("yandex.market.url") + environment.getProperty("yandex.market.warehouse.compaign.main") + "/offer-prices/updates.json";
		
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
		log.debug("offerPricesUpdates() inputJson:{}", inputJson);
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
        	    log.debug("offerPricesUpdates() jsonResponse:{}", myResponse.toString());
        	    
        	    String status = (String) myResponse.get("status");        	    
        	    if (status.equals("OK")) {  
        	    	log.debug("status: {}", status);
        	    	result = "status: " + status;
        	    	
        	    } else if (status.equals("ERROR")) {
        	    	JSONArray errors = (JSONArray) ((JSONObject) myResponse).get("errors");
        	    	        	    	
        	    	for (int i = 0; i < errors.length(); i++) {
        	    	    JSONObject error = errors.getJSONObject(i);
        	    	    String code = error.getString("code");
        	    	    String message = error.getString("message");
        	    	    
        	    	    log.error("status: {}, {}, {}", status, code, message);
            	    	result = "status: " + status + " [" + code + "] " + message;            	    	
        	    	} 
        	    }
            }

		} catch (Exception ex) {
			log.error("Exception: ", ex);
			result = "status: EXCEPTION " + ex.getMessage();
		} finally {
			//httpClient.close();
		}	
		return result;
	}
	
	// POST /campaigns/{campaignId}/offer-prices/updates
	public String offerPricesUpdatesV2(int warehouseCompainId, List<Product> products) throws JSONException {
				
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
			"      \"id\": \""+ product.getSku() + "\",\r\n" +
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
		log.debug("offerPricesUpdates() inputJson:{}", inputJson);
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
        	    log.debug("offerPricesUpdates() jsonResponse: {}", myResponse.toString());
        	    
        	    String status = (String) myResponse.get("status");        	    
        	    if (status.equals("OK")) {  
        	    	log.debug("status: {}", status);
        	    	result = "status: " + status;
        	    	
        	    } else if (status.equals("ERROR")) {
        	    	JSONArray errors = (JSONArray) ((JSONObject) myResponse).get("errors");
        	    	        	    	
        	    	for (int i = 0; i < errors.length(); i++) {
        	    	    JSONObject error = errors.getJSONObject(i);
        	    	    String code = error.getString("code");
        	    	    String message = error.getString("message");
        	    	    
        	    	    log.error("status: {}, {}, {}", status, code, message);
            	    	result = "status: " + status + " [" + code + "] " + message;            	    	
        	    	} 
        	    }
            }

		} catch (Exception ex) {
			log.error("Exception: ", ex);
			result = "status: EXCEPTION " + ex.getMessage();
		} finally {
			//httpClient.close();
		}	
		return result;
	}	
	
	public String offerPricesUpdatesByAllWarehouses(List<Product> products) throws JSONException {
		String result = "";
		result += "MAIN: " + offerPricesUpdatesV2(environment.getProperty("yandex.market.warehouse.compaign.main", Integer.class), products);
		result += ", EXPRESS: " + offerPricesUpdatesV2(environment.getProperty("yandex.market.warehouse.compaign.express", Integer.class), products);
		result += ", HUGE: " + offerPricesUpdatesV2(environment.getProperty("yandex.market.warehouse.compaign.huge", Integer.class), products);
		result += ", DBS: " + offerPricesUpdatesV2(environment.getProperty("yandex.market.warehouse.compaign.dbs", Integer.class), products);
		return result;
	}
	
	public Order order(Long ymOrderId, Order order) {
				
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
		    
		    if (responseCode == 404) {
		    	return null;
		    }		    
		    log.debug("offerMappingEntries() responseCode:{}", responseCode);
		    log.debug("offerMappingEntries() sending 'GET' request to URL:{}", url);
		    
		    BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"));
		    String inputLine;
		    StringBuffer response = new StringBuffer();
		    while ((inputLine = in.readLine()) != null) {
		     	response.append(inputLine);
		    }
		    in.close();
		    log.debug("response: {}", response);
		    		    
		    myResponse = new JSONObject(response.toString());
    	    log.debug("offerMappingEntries() jsonResponse:{}", myResponse.toString());
    	       	        
    	    String yandexMarketStatus = myResponse.getJSONObject("order").getString("status");
    	    String yandexMarketSubStatus = "";    	    
    	    try {
    	    	yandexMarketSubStatus = myResponse.getJSONObject("order").getString("substatus");    	    	
    	    } catch (org.json.JSONException ex) {
    	    	yandexMarketSubStatus = "";
    	    }    	        	     
    	    if (yandexMarketStatus.equalsIgnoreCase("PROCESSING") && yandexMarketSubStatus.equalsIgnoreCase("SHIPPED")) {
				result.setStatus(OrderStatuses.DELIVERING);
    	    } else if (yandexMarketStatus.equalsIgnoreCase("PROCESSING") && yandexMarketSubStatus.equalsIgnoreCase("COURIER_ARRIVED_TO_SENDER")) {
				result.setStatus(OrderStatuses.DELIVERING);
    	    } else if (yandexMarketStatus.equalsIgnoreCase("DELIVERY")) {
				result.setStatus(OrderStatuses.DELIVERING);
    	    } else if (yandexMarketStatus.equalsIgnoreCase("CANCELLED")) {
    	    	result.setStatus(OrderStatuses.CANCELED);    	    	
    	    } else if (yandexMarketStatus.equalsIgnoreCase("DELIVERED")) {
    	    	result.setStatus(OrderStatuses.DELIVERED);    	    	
    	    } else if (yandexMarketStatus.equalsIgnoreCase("PICKUP")) {
    	    	result.setStatus(OrderStatuses.READY_GIVE_AWAY);    	    	
    	    } else if (yandexMarketStatus.equalsIgnoreCase("PROCESSING")) {
    	    	result.setStatus(OrderStatuses.APPROVED);
    	    } else if (yandexMarketStatus.equalsIgnoreCase("UNPAID")) {
        	    result.setStatus(OrderStatuses.APPROVED);    	    	
        	} else {
        		result.setStatus(OrderStatuses.UNKNOWN);
        	} 
    	    log.debug("status: [{}, {}] -> {}", yandexMarketStatus, yandexMarketSubStatus, result.getStatus());
    	        	    
    	    JSONObject jsonDelivery = myResponse.getJSONObject("order").getJSONObject("delivery");
    	    
    	    String deliveryType = jsonDelivery.getString("type");
    	    String deliveryName = jsonDelivery.getString("serviceName");
    	    
    	    Region4JsonData resultRegionData = parsingRegionAddress(new Region4JsonData(jsonDelivery));
	        	    
    	    String addressText = resultRegionData.getAddress() + ", [" + deliveryType + ", " + deliveryName + "]";
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
			log.error("MalformedURLException:", e);
			return null;			
		} catch (Exception e) {
			log.error("Exception:", e);
			return null;
		}
		result.setId(ymOrderId.intValue());
		return result;		
	}
	
	// https://yandex.ru/dev/market/partner-marketplace-cd/doc/dg/reference/get-campaigns-id-orders-id-delivery-labels.html
	public void OrderLabels(int ymOrderId) {
		
		Map<String, String> headers = new HashMap<>();
		headers.put("Content-type", "application/json");			
		headers.put("Authorization", "OAuth oauth_token=\"" + environment.getProperty("yandex.market.oauth.token") + "\", oauth_client_id=\"" + environment.getProperty("yandex.market.oauth.client") + "\"");
		
		final String url = environment.getProperty("yandex.market.url") + environment.getProperty("yandex.market.warehouse.compaign.main") + "/orders/" + ymOrderId + "/delivery/labels.json";
		com.mashape.unirest.http.HttpResponse<InputStream> insResponse = null;
		try {			
			insResponse = Unirest.get(url)
					.headers(headers)
					.asBinary();
			
			InputStream in = insResponse.getBody();		
		    File downloadedFile = File.createTempFile("ym-label-" + ymOrderId, ".pdf");
		    FileOutputStream out = new FileOutputStream(downloadedFile);
		    
		    byte[] buffer = new byte[1024];
		    int len = in.read(buffer);
		    while (len != -1) {
		        out.write(buffer, 0, len);
		        len = in.read(buffer);
		        if (Thread.interrupted()) {
		            try {
		                throw new InterruptedException();
		            } catch (InterruptedException e) {
		            	log.error("InterruptedException:", e);
		            }
		        }
		    }
		    in.close();
		    out.close();
		    
		    log.info("downloadedFile: {}", downloadedFile.getAbsolutePath());
		    PDDocument document = PDDocument.load(new File(downloadedFile.getAbsolutePath()));
		    		    
		    PrintService myPrintService = findPrintService(environment.getProperty("printer.service.to.labels"));
	        if (myPrintService != null) {	        	
	            PrinterJob job = PrinterJob.getPrinterJob();
	            job.setPageable(new PDFPageable(document));
	            job.print();     	
	        } else {
	        	log.error("Printer " + environment.getProperty("printer.service.to.labels") + " is not found");
	        }	
		} catch (UnirestException e) {
			log.error("UnirestException:", e);
		} catch (IOException e) {
			log.error("IOException:", e);
		} catch (PrinterException e) {
			log.error("PrinterException:", e);
		}		
	}
		
	private PrintService findPrintService(String printerName) {
        PrintService[] printServices = PrintServiceLookup.lookupPrintServices(null, null);
        for (PrintService printService : printServices) {
            if (printService.getName().trim().equals(printerName)) {
                return printService;
            }
        }
        return null;
    }
	
	public Order order(Order realOrder) {
		
		Long ymOrderId = -1L;				
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
	
	public boolean boxes(int ymOrderId, Order order) throws JSONException {
				
		boolean resultRequest = false;
		
		int shipmentId = -1;
		String fulfilmentCode = "";	
		
		final int weight = 500;
		final int width = 20;
		final int height = 20;
		final int depth = 20;
		
		Order ymOrder = order(Long.valueOf(ymOrderId), order);
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

		log.debug("status() inputJson:{}", inputJson);
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
			    log.debug("status() jsonResponse:{}", myResponse.toString());
			    
			    String status = (String) myResponse.get("status");        	    
        	    if (status.equals("OK")) {  
        	    	log.debug("status: {}", status);
        	    	resultRequest = true;
        	    	
        	    } else if (status.equals("ERROR")) {
        	    	JSONObject parcelData = (JSONObject) myResponse.get("errors");
        	    	String code = (String) parcelData.get("code");
        	    	String message = (String) parcelData.get("message");        	    	
        	    	log.error("status: {}, {}, {}", status, code, message);
        	    }	    	    			    
			}
			
			} catch (Exception ex) {
				log.error("Exception: ", ex);
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
			return environment.getProperty("yandex.market.warehouse.compaign.main", Integer.class);
		} else if (order.getCustomer().getId() == 6611) {
			return environment.getProperty("yandex.market.warehouse.compaign.main", Integer.class); 
		} else if (order.getCustomer().getId() == 7204) {
			return environment.getProperty("yandex.market.warehouse.compaign.express", Integer.class); 
		} else if (order.getCustomer().getId() == 7277) {
			return environment.getProperty("yandex.market.warehouse.compaign.huge", Integer.class); 
		} else if (order.getCustomer().getId() == 7541) {
			return environment.getProperty("yandex.market.warehouse.compaign.dbs", Integer.class); 
		} else {
			return environment.getProperty("yandex.market.warehouse.compaign.main", Integer.class);
		} 
	}

	public String status(Order order) {
				
		int ymOrderId = -1;
		String status = "";
		String subStatus = "";		
		for (OrderExternalCrm orderExternalCrm : order.getExternalCrms()) {
			if (orderExternalCrm.getCrm() == CrmTypes.YANDEX_MARKET && orderExternalCrm.getParentId() > 0) {
				ymOrderId = orderExternalCrm.getParentId().intValue();
				break;
			}
		}
		
		String result = "";		
		if (ymOrderId <= 0) {
			return result;
		} 	
		
		if (order.getStatus() == OrderStatuses.APPROVED) {
					
			boolean resultRequestBoxes = false;
			try {
				resultRequestBoxes = boxes(ymOrderId, order);
			} catch (JSONException e) {
				log.error("JSONException: {}", e);
			}
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
			
		
		log.debug("status() inputJson:{}", inputJson);
		JSONObject inputJsonObj = null;
		try {
			inputJsonObj = new JSONObject(inputJson);
		} catch (JSONException e) {
			log.error("JSONException: {}", e);
		}
	
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
        	    log.debug("status() jsonResponse:{}", myResponse.toString());
            }

		} catch (Exception ex) {
			log.error("Exception: ", ex);
			result = "status: EXCEPTION " + ex.getMessage();
		} finally {
			//httpClient.close();
		}	
		return result;		
		
	}
	
	private Region4JsonData parsingRegionAddress(Region4JsonData regionData) throws JSONException {
		Region4JsonData result = null;
		
		JSONObject region = null;
		try {
			region = regionData.getRegion().getJSONObject("region");			
		} catch (org.json.JSONException ex) {
			region = null;
		}
		if (region != null) {			
			result = new Region4JsonData(region);
			if (StringUtils.isNoneEmpty(regionData.getAddress())) {
				result.setAddress(regionData.getAddress() + ", " + region.getString("name"));
			} else {
				result.setAddress(region.getString("name"));
			}
			result = parsingRegionAddress(result);			
		} else {			
			try {
				region = regionData.getRegion().getJSONObject("parent");
			} catch (org.json.JSONException ex) {
				region = null;
			}
			if (region == null) {
				return regionData;
			}
			result = new Region4JsonData(region);
			if (StringUtils.isNoneEmpty(regionData.getAddress())) {
				result.setAddress(regionData.getAddress() + ", " + region.getString("name"));
			} else {
				result.setAddress(region.getString("name"));
			}	
			if (region != null) {
				return parsingRegionAddress(result);
			} 
		}
		return result;
	}
	
	private class Region4JsonData {
		
		private JSONObject region = null;
		private String address = "";
		
		public Region4JsonData() {
			super();			
		}
		
		public Region4JsonData(JSONObject region) {
			this();
			this.region = region;
		}
		
		public JSONObject getRegion() {
			return region;
		}
		
		public String getAddress() {
			return address;
		}
		
		public void setAddress(String address) {
			this.address = address;
		}

		@Override
		public String toString() {
			return "Region4JsonData [region=" + region + ", address=" + address + "]";
		}		
	}	
}
