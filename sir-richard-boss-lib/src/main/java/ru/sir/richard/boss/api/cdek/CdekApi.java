package ru.sir.richard.boss.api.cdek;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.PropertyResolver;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import ru.sir.richard.boss.api.AnyApi;
import ru.sir.richard.boss.model.data.CarrierInfo;
import ru.sir.richard.boss.model.data.Order;
import ru.sir.richard.boss.model.data.OrderExternalCrm;
import ru.sir.richard.boss.model.data.crm.CdekOrderBean;
import ru.sir.richard.boss.model.data.crm.CdekOrderItemBean;
import ru.sir.richard.boss.model.data.crm.DeliveryServiceResult;
import ru.sir.richard.boss.model.types.CarrierStatuses;
import ru.sir.richard.boss.model.types.CrmStatuses;
import ru.sir.richard.boss.model.types.CrmTypes;
import ru.sir.richard.boss.model.types.DeliveryTypes;
import ru.sir.richard.boss.model.types.OrderAdvertTypes;
import ru.sir.richard.boss.model.utils.DateTimeUtils;
import ru.sir.richard.boss.model.utils.NumberUtils;
import ru.sir.richard.boss.model.utils.TextUtils;
import ru.sir.richard.boss.model.utils.XmlUtils;

public class CdekApi implements AnyApi {
	
	/**
	 * application.properties
	 */	
	private final PropertyResolver environment;
	
	private final Logger logger = LoggerFactory.getLogger(CdekApi.class);
	
	public CdekApi(PropertyResolver environment) {
		super();
		this.environment = environment;
	}
	
	public String addOrder(CdekOrderBean cdekOrderBean, int tariffId, int weightOfG) {
		
		String trackCode = "";
		
		Map<String, String> auth = getAuth(DateTimeUtils.sysDate(), true);
		String url = environment.getProperty("cdek.url");	
				
		BigDecimal deliveryAmount = cdekOrderBean.getDeliveryPay();				
		String recipientName = TextUtils.repaceToHTMLTag(cdekOrderBean.getRecipient());
		String recipientCompany = TextUtils.repaceToHTMLTag(cdekOrderBean.getRecipient()); // ???????? ???? ???????????????????? ?? xml 1.5
		String comment = TextUtils.repaceToHTMLTag(cdekOrderBean.getDeliveryAnnotation());
		
		String recipientAddressString = "";
		if (cdekOrderBean.getDeliveryType().is??dekPvz()) {
			recipientAddressString = "        <address pvzCode=\"" + cdekOrderBean.getPvz() + "\"/>\r\n";
		} else {
			recipientAddressString = "        <address flat=\"" + cdekOrderBean.getFlat() + "\" house=\"" + cdekOrderBean.getHouse() + "\" street=\"" + cdekOrderBean.getStreet() + "\"/>\r\n";
		}
		
		String itemsString = "";	
		for (CdekOrderItemBean itemBean : cdekOrderBean.getItems()) {
			
			BigDecimal productPrice = itemBean.getProductPrice();
			BigDecimal productPay = itemBean.getProductPay();
						
			itemsString += "            <item amount=\"" + itemBean.getProductQuantity() + "\" comment=\"" + TextUtils.repaceToHTMLTag(itemBean.getProductName()) + "\" cost=\"" + cdekFormatNumber(productPrice) + "\"\r\n" + 
					"                payment=\"" + cdekFormatNumber(productPay) + "\"\r\n" + 
					"                warekey=\"" + itemBean.getProductSku() + "\" weight=\"0.01\"/>\r\n";
		}			
		
		String inputXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n" + 
				"<deliveryrequest account=\"" + auth.get("login") + "\"\r\n" + 
				"    date=\"" + auth.get("dateQuery") + "\" number=\"0\" ordercount=\"1\" secure=\"" + auth.get("secure") + "\">\r\n" + 
				"    <order comment=\"" + comment + "\" deliveryrecipientcost=\"" + cdekFormatNumber(deliveryAmount) + "\"\r\n" + 
				
				"        number=\"" + cdekOrderBean.getNo() + "\" phone=\"" + cdekOrderBean.getRecipientPhone() + "\" reccitycode=\"" + cdekOrderBean.getCityId() + "\"\r\n" + 
				"        recipientEmail=\"" + cdekOrderBean.getRecipientEmail() + "\" recipientCompany=\"" + recipientCompany + "\" recipientName=\"" + recipientName + "\"\r\n" + 
				"        sellername=\"" + cdekOrderBean.getOrder().getStore().getSite() + "\" sendcitycode=\"44\" tarifftypecode=\"" + tariffId + "\">\r\n" +				
				recipientAddressString +				 
				"        <package description=\"????????????????????????\" barCode=\"1\" number=\"1\" sizea=\"10\"\r\n" +				
				"            sizeb=\"10\" sizec=\"10\" weight=\"" + weightOfG + "\">\r\n" +
				
				itemsString +
				
				"        </package>\r\n" + 
				"        <AddService ServiceCode=\"37\"/>\r\n" +				
				"    </order>\r\n" + 
				"</deliveryrequest>";				
				
		logger.debug("test input.xml:{}", inputXml);			
		
		HttpClient httpClient = HttpClientBuilder.create().build();
		
		try {
			HttpPost post = new HttpPost(url);	
			List<NameValuePair> postParameters = new ArrayList<NameValuePair>();
		    postParameters.add(new BasicNameValuePair("xml_request", inputXml));		    
		    post.setEntity(new UrlEncodedFormEntity(postParameters, "utf-8"));
			HttpResponse response = httpClient.execute(post);
			BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "utf-8"));

			StringBuffer result = new StringBuffer();
		    String line = "";
		    while ((line = rd.readLine()) != null) {
		        result.append(line);
		    }
		    logger.debug("xml result:{}", result.toString());
		      		    
		    if (response != null) {
				String xmlSource = new String(result);
			    Document doc = XmlUtils.stringToDom(xmlSource);
			    			    
			    doc.getDocumentElement().normalize();
				NodeList nList = doc.getElementsByTagName("Order");
				for (int i = 0; i < nList.getLength(); i++) {

					Node nNode = nList.item(i);
					if (nNode.getNodeType() == Node.ELEMENT_NODE) {
						Element eElement = (Element) nNode;							
						//Order modifiedOrder = new Order();												
						logger.debug("addOrder() order:{}", eElement.getAttribute("DispatchNumber"));
						
						trackCode = eElement.getAttribute("DispatchNumber");
						if (StringUtils.isNoneEmpty(trackCode)) {
							break;
						}
					}	
				}
			}

		} catch (Exception ex) {
			logger.error("Exception: ", ex);
		} finally {
			//httpClient.close();
		}
		return trackCode;
	}	
	
	public List<Order> getStatuses(List<Order> orders) throws SAXException, ParserConfigurationException {
		
		Map<String, String> auth = getAuth(DateTimeUtils.sysDate(), true);
		String url = "https://integration.cdek.ru/status_report_h.php";		
		String stringDispatchNumbers = "";
		for (Order order : orders) {
			if (StringUtils.isNotEmpty(order.getDelivery().getTrackCode())) {
				stringDispatchNumbers += "	<Order DispatchNumber=\"" + order.getDelivery().getTrackCode() + "\"/>";
			}
		}		
		String inputXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
				+ "<StatusReport Account=\"" + auth.get("login") + "\" Date=\"" + auth.get("dateQuery") + "\" Secure=\"" + auth.get("secure") + "\" ShowHistory=\"0\">"
				+ stringDispatchNumbers
				+ "</StatusReport>";
		logger.debug("test input.xml:{}", inputXml);			
		
		HttpClient httpClient = HttpClientBuilder.create().build();
		List<Order> modifiedOrders = new ArrayList<Order>();
		try {
			HttpPost post = new HttpPost(url);			
			List<NameValuePair> postParameters = new ArrayList<NameValuePair>();			
		    postParameters.add(new BasicNameValuePair("xml_request", inputXml));		    

		    post.setEntity(new UrlEncodedFormEntity(postParameters, "utf-8"));
			HttpResponse response = httpClient.execute(post);
			BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "utf-8"));

			StringBuffer result = new StringBuffer();
		    String line = "";
		    while ((line = rd.readLine()) != null) {
		        result.append(line);
		    }
		    logger.debug("xml result:{}", result.toString());			
			if (response != null) {
				String xmlSource = new String(result);
			    Document doc = XmlUtils.stringToDom(xmlSource);
			    			    
			    doc.getDocumentElement().normalize();
				NodeList nList = doc.getElementsByTagName("Order");
				for (int i = 0; i < nList.getLength(); i++) {

					Node nNode = nList.item(i);
					if (nNode.getNodeType() == Node.ELEMENT_NODE) {
						Element eElement = (Element) nNode;							
						Order modifiedOrder = new Order();												
						logger.debug("getOrders() order:{}", eElement.getAttribute("Number"));
						
						//modifiedOrder.setNo(parseElementByAttributeName(eElement, "Number"));
						
						modifiedOrder.getDelivery().setTrackCode(eElement.getAttribute("DispatchNumber"));

						//"CM_43694_25787"
						int dispatchNumber = parseElementByAttributeName(eElement, "DispatchNumber");
						modifiedOrder = getCrmConnectData(modifiedOrder, orders, dispatchNumber, eElement.getAttribute("Number"));
						
						Element status = (Element) eElement.getElementsByTagName("Status").item(0);
						int iSdekStatus = parseElementByAttributeName(status, "Code");
						
						logger.debug("getOrders() order.cdekStatus:{}", iSdekStatus);
						CarrierStatuses sdekStatus = CarrierStatuses.getValueById(iSdekStatus);						
						modifiedOrder.getDelivery().setCarrierStatus(sdekStatus);											
						modifiedOrders.add(modifiedOrder);									
					}	
				}
			}

		} catch (Exception ex) {
			logger.error("Exception: ", ex);
		} finally {
			//httpClient.close();
		}			
		return modifiedOrders;		
	}	
	
	private Order getCrmConnectData(Order modifiedOrder, List<Order> orders, int dispatchNumber, String orderNumber) {
						
		Order result = modifiedOrder;
		if (dispatchNumber <= 0) {
			return result;
		}
		result.getDelivery().setTrackCode(String.valueOf(dispatchNumber));
		
		Order fullDataOrder = null;
		for (Order order : orders) {
			if (StringUtils.equals(order.getDelivery().getTrackCode(), String.valueOf(dispatchNumber))) {
				fullDataOrder = order;
				break;
			}
		}	
		if (fullDataOrder == null) {
			return result;
		}
		result.setId(fullDataOrder.getId());
		result.setNo(fullDataOrder.getNo());
		result.setAdvertType(fullDataOrder.getAdvertType());		
		if (fullDataOrder.getAdvertType() == OrderAdvertTypes.CDEK_MARKET) {
			OrderExternalCrm orderExternalCrm = new OrderExternalCrm(result);
			orderExternalCrm.setCrm(CrmTypes.CDEK);
			orderExternalCrm.setParentId(dispatchNumber);
			orderExternalCrm.setParentCode(orderNumber);
			orderExternalCrm.setStatus(CrmStatuses.SUCCESS);
			result.getExternalCrms().add(orderExternalCrm);			
		}	
		
		return result;
	}
	
	private int parseElementByAttributeName(Element eElement, String attributeName) {
		int result = 0;
		try {
			result = Integer.valueOf(eElement.getAttribute(attributeName).trim());
		} catch (NumberFormatException ex) {
			logger.error("NumberFormatException: ", ex);
		}		
		return result;
	}
	
	public static int getCdekTariffId(DeliveryTypes deliveryType) {
		int tariffId;
		if (deliveryType == DeliveryTypes.CDEK_COURIER) {
			tariffId = 137;
		} else if (deliveryType == DeliveryTypes.CDEK_COURIER_ECONOMY) {
			tariffId = 233;			
	    } else if (deliveryType == DeliveryTypes.CDEK_PVZ_TYPICAL) {
	    	tariffId = 136;
	    } else if (deliveryType == DeliveryTypes.PICKUP) {
	    	tariffId = 136;
	    } else if (deliveryType == DeliveryTypes.CDEK_PVZ_ECONOMY) {
	    	tariffId = 234;	
	    } else {
	    	tariffId = 0;       
	    }
		return tariffId;
	}

	/**
	 * ?????????? ????????????????, ?????????????????? ???????????????? ???? ???????????????????? ???????????? ?? ???????????????????????? ?????????????????? ???????????????? "???????????????????? ????????????"
	 * @param weightOfKg
	 * @param calculateDate
	 * @param totalAmount
	 * @param tariffId
	 * @param receiverCityId
	 * @param isPostpay ???????????????????? (????/??????)
	 * @param isPaySeller ?????? ???????????? ???? ???????????????? (????????????????/????????????????????)
	 * @return
	 * @throws Exception
	 */
	public DeliveryServiceResult calculate(BigDecimal weightOfKg, Date calculateDate, BigDecimal totalAmount, 
			int tariffId, int receiverCityId, boolean isPostpay, boolean isPaySeller) throws Exception {
		
		if (tariffId == 0) {
			return DeliveryServiceResult.createEmpty();
		}		
		Map<String, String> auth = getAuth(calculateDate, false);
		final String url = "http://api.cdek.ru/calculator/calculate_price_by_json.php";				
		String inputJsonService = "\"services\": [{\"id\": 2, \"param\": 1}, {\"id\": 37}]";
		String inputJson = "{\"version\":\"1.0\", \"authLogin\":\"" + auth.get("login") + "\", \"secure\":\"" + auth.get("secure") 
				+ "\", \"dateExecute\":\"" + DateTimeUtils.formatDate(calculateDate, "yyyy-MM-dd") 
				+ "\", \"senderCityId\":\"44\", \"receiverCityId\":\"" + receiverCityId 
				+ "\", \"tariffId\":\""+ tariffId + "\", \"goods\": [{\"weight\":\"" + weightOfKg.toPlainString() 
				+ "\", \"length\":\"10\", \"width\":\"10\", \"height\":\"10\"}]," 
				+ inputJsonService + "}";  
			
		JSONObject inputJsonObj = new JSONObject(inputJson);
						
		BigDecimal deliveryPrice = BigDecimal.ZERO;
		BigDecimal deliveryFullPrice = BigDecimal.ZERO;
		
		BigDecimal deliverySellerSummary = BigDecimal.ZERO;
		BigDecimal deliveryCustomerSummary = BigDecimal.ZERO;
		
		BigDecimal deliveryInsurance = BigDecimal.ZERO;
		BigDecimal deliveryPostpayFee = BigDecimal.ZERO;
		BigDecimal postpayAmount = BigDecimal.ZERO;
		int deliveryPeriodMin = 0;
		int deliveryPeriodMax = 0;
		String errorText = "";
		
		CloseableHttpClient httpClient = HttpClientBuilder.create().build();
		JSONObject myResponse = null;
		try {

			HttpPost post = new HttpPost(url);
			StringEntity postingString = new StringEntity(inputJsonObj.toString());
			post.setEntity(postingString);
			post.setHeader("Content-type", "application/json");
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
        	    logger.debug("cdekCalc() jsonResponse:{}", myResponse.toString());
        	    
              	JSONObject parcelData = (JSONObject) myResponse.get("result");
           	    deliveryPeriodMin = (Integer) parcelData.get("deliveryPeriodMin");
           	    deliveryPeriodMax = (Integer) parcelData.get("deliveryPeriodMax");
           	    deliveryPrice = (new BigDecimal((String) parcelData.get("price"))).setScale(0, RoundingMode.HALF_UP);
           	    deliveryInsurance = totalAmount.multiply(new BigDecimal("0.75")).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
           	    
           	    deliveryCustomerSummary = (new BigDecimal((String) parcelData.get("price"))).setScale(2, RoundingMode.HALF_UP);
        	    //deliveryCustomerSummary = deliveryCustomerSummary.add(totalAmount.multiply(BigDecimal.ONE.divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP))).round(new MathContext(0, RoundingMode.HALF_UP)).setScale(0, RoundingMode.CEILING);
        	    deliveryFullPrice = deliveryCustomerSummary;
        	    
           	    // ?????????????????? = (0.75 * totalAmount) / 100
           	    // ???????????????? ???? ?????????????? = (totalAmount + ?????????????????? ????????????????) * 0,03 
           	    // ???????????????? = price + ?????????????????? + ???????????????? ???? ??????????????
           	    
           	    if (isPostpay) {
           	    	BigDecimal oPostpayAmount = BigDecimal.ZERO;
           	    	for (int ii = 0; ii < 10; ii++) {           	    		
           	    		BigDecimal nDeliverySellerSummary = deliveryPrice.add(deliveryInsurance).add(deliveryPostpayFee);
           	    		BigDecimal nPostpayAmount = totalAmount.add(nDeliverySellerSummary);
           	    		BigDecimal delta = nPostpayAmount.subtract(oPostpayAmount);
           	    		if (delta.abs().compareTo(BigDecimal.ONE) < 0) {
           	    			deliverySellerSummary = nDeliverySellerSummary;
           	    			postpayAmount = nPostpayAmount;
           	    			break;
           	    		} else {
           	    			deliveryPostpayFee = (totalAmount.add(nDeliverySellerSummary)).multiply(new BigDecimal("3")).divide(BigDecimal.valueOf(100));
           	    			oPostpayAmount = nPostpayAmount;
           	    		}
           	    	}
           	    	deliverySellerSummary = deliverySellerSummary.round(new MathContext(2, RoundingMode.HALF_UP));
           	    	deliveryPostpayFee = deliveryPostpayFee.round(new MathContext(2, RoundingMode.HALF_UP));
           	    	deliveryCustomerSummary = deliverySellerSummary.subtract(deliveryPostpayFee);
          	    	if (isPaySeller) {           	    		
          	    		postpayAmount = totalAmount.subtract(deliveryPrice).subtract(deliveryInsurance).subtract(deliveryPostpayFee);
           	    		deliveryCustomerSummary = BigDecimal.ZERO; 
           	    		deliveryFullPrice = deliverySellerSummary;
           	    	} else {
           	    		postpayAmount = totalAmount.add(deliveryCustomerSummary).subtract(deliveryPrice).subtract(deliveryInsurance).subtract(deliveryPostpayFee);
           	    		deliveryFullPrice = deliveryCustomerSummary;
           	    	}    
           	    } else {
           	    	deliveryCustomerSummary = deliveryPrice.add(deliveryInsurance);
           	    	deliverySellerSummary = deliveryPrice.add(deliveryInsurance).round(new MathContext(0, RoundingMode.HALF_UP)).setScale(0, RoundingMode.CEILING);
           	    	deliveryFullPrice = deliveryCustomerSummary;
           	    }
            }

		} catch (Exception ex) {
			
			if (myResponse != null) {				
				JSONArray parcelDataError = (JSONArray) myResponse.get("error");
		    	errorText = "";
		    	for (int i = 0; i < parcelDataError.length(); i++) {
		    		JSONObject iObj = parcelDataError.getJSONObject(i);
		    		String iObj2 = (String) iObj.get("text");
		    		errorText += iObj2 + " "; 
		    	}
		    	errorText = errorText.trim(); 
		    } else {
		    	logger.error("Exception: ", ex);
		    }
		} finally {
			httpClient.close();
		}		
		DeliveryServiceResult result = new DeliveryServiceResult();
		result.setDeliveryAmount(deliveryCustomerSummary);
								
		result.setDeliveryPrice(deliveryPrice);		
		result.setDeliveryInsurance(deliveryInsurance);				
		result.setDeliveryPostpayFee(deliveryPostpayFee);
		
		result.setDeliveryFullPrice(deliveryFullPrice);
		result.setDeliverySellerSummary(deliverySellerSummary);
		result.setDeliveryCustomerSummary(deliveryCustomerSummary);
		
		result.setPostpayAmount(postpayAmount);
						
		if (deliveryPeriodMin == deliveryPeriodMax) {
			result.setTermText(String.valueOf(deliveryPeriodMax) + " ????.");	
		} else {
			result.setTermText(deliveryPeriodMin + "-" + deliveryPeriodMax + " ????.");
		}
		result.setParcelType("tariffId: " + tariffId); // TODO
		result.setTo("receiverCityId: " + receiverCityId); // TODO
		result.setWeightText(weightOfKg.toPlainString() + " ????.");
		result.setErrorText(errorText);
		return result;
	}
	
	public JSONObject getCities(String cityContext) throws IOException, SAXException, ParserConfigurationException {
		
		logger.debug("getCities():{}", cityContext);
		
		final String url = "http://api.cdek.ru/city/getListByTerm/json.php?q=" + cityContext + "&name_startsWith=" + cityContext;
		String errorText = "";
					
		CloseableHttpClient httpClient = HttpClientBuilder.create().build();
		JSONObject myResponse = null;
		try {

			HttpPost post = new HttpPost(url);			
			post.setHeader("Content-type", "application/json");
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
        	    logger.debug("cdekCalc() jsonResponse:{}", myResponse.toString());
        	    
            }

		} catch (Exception ex) {
			
			if (myResponse != null) {				
				JSONArray parcelDataError = (JSONArray) myResponse.get("error");
		    	errorText = "";
		    	for (int i = 0; i < parcelDataError.length(); i++) {
		    		JSONObject iObj = parcelDataError.getJSONObject(i);
		    		String iObj2 = (String) iObj.get("text");
		    		errorText += iObj2 + " "; 
		    	}
		    	errorText = errorText.trim(); 
		    } else {
		    	logger.error("Exception: ", ex);
		    }
		} finally {
			httpClient.close();
		}		
		return myResponse;
	}
	
	public List<CarrierInfo> getPvzs(int cityId) throws IOException, SAXException, ParserConfigurationException {		
		logger.debug("getPvzList():{}", cityId);
				
		String urlString = "http://integration.cdek.ru/pvzlist/v1/xml?cityid=" + cityId + "&lang=rus&type=\"PVZ\"";		
		URL obj = new URL(urlString);	    
	    HttpURLConnection con = (HttpURLConnection) obj.openConnection();
	    
	    con.setRequestProperty("Content-Type", "text/xml;charset=UTF-8");
	    //con.setRequestProperty("Content-Language", "ru-RU");
	    //con.setRequestProperty("Accept-Language", "ru,en;q=0.9");
	    //con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/73.0.3683.86 Safari/537.36");
			    
	    int responseCode = con.getResponseCode();
	    logger.debug("getPvzList() responseCode:{}", responseCode);
	    logger.debug("getPvzList() sending 'GET' request to URL:{}", urlString);
	    
	    BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"));
	    String inputLine;
	    StringBuffer response = new StringBuffer();
	    while ((inputLine = in.readLine()) != null) {
	     	response.append(inputLine);
	    }
	    in.close();
	    String xmlSource = new String(response);
	    Document doc = XmlUtils.stringToDom(xmlSource);
	    //logger.debug("getPvzList() result:{}", doc.getFirstChild().getNodeName());
	    List<CarrierInfo> pvzs = new ArrayList<CarrierInfo>();
		try {
			doc.getDocumentElement().normalize();
			NodeList nList = doc.getElementsByTagName("Pvz");
			for (int i = 0; i < nList.getLength(); i++) {

				Node nNode = nList.item(i);
				//logger.debug("getPvzList() current element:{}", nNode.getNodeName());
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nNode;
					logger.debug("getPvzList() pvz:{}", eElement.getAttribute("Code"));
									
					CarrierInfo cdekInfo = new CarrierInfo();					
					cdekInfo.setPvz(eElement.getAttribute("Code"));

					cdekInfo.setRegion(new String(eElement.getAttribute("RegionName")));
					cdekInfo.setCityContext(new String(eElement.getAttribute("City")));
					cdekInfo.setShortAddress(new String(eElement.getAttribute("Address")));
					cdekInfo.setFullAddress(new String(eElement.getAttribute("FullAddress")));
					cdekInfo.setAddressComment(new String(eElement.getAttribute("AddressComment")));					
					cdekInfo.setPhone(new String(eElement.getAttribute("Phone")));
					cdekInfo.setEmail(new String(eElement.getAttribute("Email")));
					cdekInfo.setNote(new String(eElement.getAttribute("Note")));
					cdekInfo.setPvzType(new String(eElement.getAttribute("Type")));
					cdekInfo.setHaveCash(new String(eElement.getAttribute("HaveCash")));
					cdekInfo.setAllowedCod(new String(eElement.getAttribute("AllowedCod")));					
					cdekInfo.setNearestStation(new String(eElement.getAttribute("NearestStation")));
					cdekInfo.setMetroStation(new String(eElement.getAttribute("MetroStation")));
					
					if (eElement.getElementsByTagName("OfficeImage").getLength() > 0) {
						Element officialImageElement = (Element) eElement.getElementsByTagName("OfficeImage").item(0);	
						cdekInfo.setUrl(new String(officialImageElement.getAttribute("url")));
					}
					if (eElement.getElementsByTagName("WeightLimit").getLength() > 0) {
						Element weightLimiteElement = (Element) eElement.getElementsByTagName("WeightLimit").item(0);
						int weightMax = Integer.valueOf(new String(weightLimiteElement.getAttribute("WeightMax")));
						if (weightMax < 30) {
							cdekInfo.setWeightMax("?????? <= " + weightMax  + " ????.");
						}
					}		
					
					if (StringUtils.isNotEmpty(cdekInfo.getPvzType()) && cdekInfo.getPvzType().toUpperCase().contains("POSTAMAT")) {
						continue;
					}					
					pvzs.add(cdekInfo);					
				}
			}
		} catch (Exception e) {
			logger.error("Exception: ", e);
		}
		return pvzs;
	}	
		
	public CarrierInfo getPvz(int cityId, String pvzCode) throws IOException, SAXException, ParserConfigurationException {
		List<CarrierInfo> pvzs = getPvzs(cityId);
		if (pvzs == null || pvzs.size() == 0) {
			return null;
		}
		CarrierInfo result = null;
		for (CarrierInfo cdekInfo : pvzs) {
			if (StringUtils.equals(pvzCode, cdekInfo.getPvz())) {
				return cdekInfo;
			}
		}		
		return result;
	}
	
	private Map<String, String> getAuth(Date calculateDate, boolean isMd5) {
		Map<String, String> result = new HashMap<String, String>();
		String secure = null;
		String dateQuery = null;
		if (isMd5) {
			dateQuery = DateTimeUtils.formatDate(calculateDate, "yyyy-MM-dd") + "T" + DateTimeUtils.formatDate(calculateDate, "HH:mm:ss");
			secure = DigestUtils.md5Hex(dateQuery + "&" + environment.getProperty("cdek.auth.secure"));
			
		} else {
			secure = environment.getProperty("cdek.auth.secure");
			dateQuery = "";
		}
		result.put("login", environment.getProperty("cdek.auth.login"));
		result.put("secure", secure);
		result.put("dateQuery", dateQuery);
	
		return result;
	}
	
	private String cdekFormatNumber(BigDecimal value) {
		Locale locale = new Locale("en", "UK");
		return NumberUtils.localeFormatNumber(value, locale, '.', ',', "####.#");
	}
	
}
