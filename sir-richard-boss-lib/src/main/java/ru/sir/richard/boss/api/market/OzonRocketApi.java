package ru.sir.richard.boss.api.market;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.PropertyResolver;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import ru.sir.richard.boss.model.data.Address;
import ru.sir.richard.boss.model.data.ForeignerCompanyCustomer;
import ru.sir.richard.boss.model.data.Order;
import ru.sir.richard.boss.model.data.crm.DeliveryServiceResult;
import ru.sir.richard.boss.model.types.CarrierStatuses;
import ru.sir.richard.boss.model.types.CrmTypes;
import ru.sir.richard.boss.model.types.DeliveryTypes;
import ru.sir.richard.boss.model.types.PaymentTypes;
import ru.sir.richard.boss.model.utils.TextUtils;

public class OzonRocketApi {

	private final Logger logger = LoggerFactory.getLogger(OzonRocketApi.class);
	
	/**
	 * application.properties
	 */	
	private final PropertyResolver environment;
	
	public OzonRocketApi(PropertyResolver environment) {
		super();
		this.environment = environment;
	}

	public String getToken() {

		Map<String, String> headers = new HashMap<>();
		headers.put("content-type", "application/x-www-form-urlencoded");

		Map<String, Object> fields = new HashMap<>();
		fields.put("grant_type", "client_credentials");
		fields.put("client_id", environment.getProperty("ozon.rocket.client.id"));
		fields.put("client_secret", environment.getProperty("ozon.rocket.client.key"));

		try {
			HttpResponse<JsonNode> jsonResponseConnectToken = Unirest.post("https://xapi.ozon.ru/principal-auth-api/connect/token")
					.headers(headers)
					.fields(fields)
					.asJson();
			if (jsonResponseConnectToken.getStatus() == 200) {
				JSONObject accessToken = jsonResponseConnectToken.getBody().getObject();
				return (String) accessToken.get("access_token");
			}
			if (jsonResponseConnectToken.getStatus() == 401) {
				logger.error("Server Ozon Rocket Api autorized with error. Status: {}", jsonResponseConnectToken.getStatus());
			}
		} catch (UnirestException e) {
			logger.error("UnirestException:", e);
		}
		return null;
	}

	public Map<String, String> getHeaders() {
		String accessToken = getToken();
		if (accessToken == null) {
			return null;
		}
		Map<String, String> headers = new HashMap<>();
		headers.put("content-type", "application/json");
		headers.put("authorization", "Bearer " + accessToken);
		return headers;
	}

	public Long getFromPlaceId() {
		try {

			HttpResponse<JsonNode> jsonResponseDeliveryFromPlaces = Unirest.get(environment.getProperty("ozon.rocket.url") + "/v1/delivery/from_places")
					.headers(getHeaders())
					.asJson();
			if (jsonResponseDeliveryFromPlaces.getStatus() != 200) {
				return null;
			}

			JSONObject jsonResponseDeliveryFromPlacesBody = jsonResponseDeliveryFromPlaces.getBody().getObject();
			JSONArray jsonResponseDeliveryFromPlacesBodyArray = jsonResponseDeliveryFromPlacesBody.getJSONArray("places");

			Long result = null;
			String ourDeliveryPoint = new String(environment.getProperty("ozon.rocket.from.address.ru").getBytes("ISO-8859-1"), "UTF-8");
			for (int i = 0; i <= jsonResponseDeliveryFromPlacesBodyArray.length() - 1; i++) {
				JSONObject resultFromPlace = jsonResponseDeliveryFromPlacesBodyArray.getJSONObject(i);
				logger.debug("resultFromPlace: {}, {}, {}", resultFromPlace.getLong("id"), resultFromPlace.getString("name"), resultFromPlace.getString("address"));
				
				if (StringUtils.equals(ourDeliveryPoint, resultFromPlace.getString("address"))) {
					result = resultFromPlace.getLong("id");
					break;
				}
			}
			if (result == null && jsonResponseDeliveryFromPlacesBodyArray.length() > 0) {
				result = jsonResponseDeliveryFromPlacesBodyArray.getJSONObject(0).getLong("id");
			}
			return result;
		} catch (UnirestException e) {
			logger.error("UnirestException:", e);
		} catch (UnsupportedEncodingException e) {
			logger.error("UnsupportedEncodingException:", e);
		}  
		return null;
	}

	/**
	 * 
	 * @param deliveryType Способ доставки: "Courier" — доставка курьером,
	 *                     "PickPoint" — самовывоз, "Postamat" — постамат.
	 * @return
	 */
	private String getObjectTypeNameByDeliveryType(String deliveryType) {

		if (StringUtils.equalsIgnoreCase(deliveryType, "PickPoint")) {
			return "Самовывоз";
		} else if (StringUtils.equalsIgnoreCase(deliveryType, "Postamat")) {
			return "Постамат";
		}
		if (StringUtils.equalsIgnoreCase(deliveryType, "Courier")) {
			return "Курьер";
		} else {
			return null;
		}
	}

	/**
	 * Получить первый попавшийся способ доставки по городу получателя определенного
	 * типа для предварительного расчета стоимости доставки
	 * 
	 * @param cityName
	 * @param deliveryType Способ доставки: "Courier" — доставка курьером, "PickPoint" — самовывоз, "Postamat" — постамат.
	 * @param weightOfG
	 * @param isPostpay постоплата (да/нет)
	 * @param isCash если постоплата, то оплата наличными (да/нет)
	 * @param isCard если постоплата, то оплата картой (да/нет) 
	 * @return Long
	 */
	public Long getDeliveryVariantId(String cityName, String deliveryType, int weightOfG, BigDecimal totalAmount,
			boolean isLegalEntity, 
			boolean isPostpay, 
			boolean isCash, 
			boolean isCard) {

		Map<String, Object> queryStrings = new HashMap<>();
		queryStrings.put("cityName", cityName);
		queryStrings.put("payloadIncludes.includeWorkingHours", "false");
		queryStrings.put("payloadIncludes.includePostalCode", "false");
		queryStrings.put("pagination.size", "100");

		try {
			HttpResponse<JsonNode> jsonResponseVariants = Unirest.get(environment.getProperty("ozon.rocket.url") + "/v1/delivery/variants")
					.headers(getHeaders())
					.queryString(queryStrings)
					.asJson();
			
			if (jsonResponseVariants.getStatus() != 200) {
				return null;
			}

			JSONObject jsonResponseVariantsBody = jsonResponseVariants.getBody().getObject();
			int totalCount = jsonResponseVariantsBody.getInt("totalCount");
			if (totalCount == 0) {
				// по городу не нашли ничего
				return null;
			}

			String nextPageToken;
			try {
				nextPageToken = jsonResponseVariantsBody.getString("nextPageToken");
			} catch (Exception e) {
				nextPageToken = null;
			}

			JSONArray jsonResponseVariantsBodyArray = jsonResponseVariantsBody.getJSONArray("data");

			Long result = null;
			for (int i = 0; i <= jsonResponseVariantsBodyArray.length() - 1; i++) {
				JSONObject objectVariant = jsonResponseVariantsBodyArray.getJSONObject(i);
				logger.debug("variant: {}, {}, {}", objectVariant.getLong("id"), objectVariant.getString("objectTypeName"), objectVariant.getString("name"));

				String deliveryObjectTypeName = getObjectTypeNameByDeliveryType(deliveryType);

				if (deliveryObjectTypeName.equalsIgnoreCase(objectVariant.getString("objectTypeName"))
						&& objectVariant.getBoolean("enabled") 
						//&& !objectVariant.getBoolean("isRestrictionAccess")
						&& "Active".equalsIgnoreCase(objectVariant.getString("stateName"))) {
										
					if (isLegalEntity && objectVariant.getBoolean("legalEntityNotAvailable")) {
						// фильтр на юрлиц
						continue;	
					}					
					if (isPostpay && isCash && objectVariant.getBoolean("isCashForbidden")) {
						// запрет приема наличными
						continue;
					}					
					if (isPostpay && isCard && !objectVariant.getBoolean("cardPaymentAvailable")) {
						// запрет оплаты картой
						continue;
					}
					if (weightOfG > 0) {
						// указан вес посылки
						int maxWeight = objectVariant.getInt("maxWeight");
						if (maxWeight > 0 && weightOfG > maxWeight) {
							continue;
						}
					}

					result = objectVariant.getLong("id");
					break;
				}


			}
			if (result == null && StringUtils.isNotEmpty(nextPageToken)) {
				// recursion
				/*
				 * jsonResponseVariants = Unirest.get(STATIC_URL_API + "/v1/delivery/variants")
				 * .headers(getHeaders()) .queryString(queryStrings) .asJson();
				 */
			}
			return result;

		} catch (UnirestException e) {
			logger.error("UnirestException:", e);
		}

		return null;
	}
	
	public Address getDeliveryVariant(Long deliveryVariantId) {
		
		try {
	
			HttpResponse<JsonNode> jsonResponseVariantsByids = Unirest.post(environment.getProperty("ozon.rocket.url") + "/v1/delivery/variants/byids")
					.headers(getHeaders())
					.body("{\"ids\": [" + deliveryVariantId + "]}")
					.asJson();			
			if (jsonResponseVariantsByids.getStatus() != 200) {
				return null;
			}

			JSONObject jsonResponseVariantsByidsBody = jsonResponseVariantsByids.getBody().getObject();
			JSONArray jsonResponseVariantsByidsBodyArray = jsonResponseVariantsByidsBody.getJSONArray("data");

			Address result = null;
			if (jsonResponseVariantsByidsBodyArray.length() > 0) {
				JSONObject objectVariant = jsonResponseVariantsByidsBodyArray.getJSONObject(0);
				logger.debug("variant: {}, {}, {}", objectVariant.getLong("id"), objectVariant.getString("objectTypeName"), objectVariant.getString("name"));
				
				result = new Address();
				try {
					result.setAddress(objectVariant.getString("address"));
				} catch (Exception ex) {
					logger.error("Exception address:", ex);
				}
				try {
					result.setAnnotation(objectVariant.getString("description"));
				} catch (Exception ex) {
					logger.error("Exception description: {}", deliveryVariantId);
				}				
				try {
					result.getCarrierInfo().setCityContext(objectVariant.getString("settlement"));
				} catch (Exception ex) {
					logger.error("Exception settlment: {}", deliveryVariantId);
				}
				try {
					result.getCarrierInfo().setRegion(objectVariant.getString("region"));
				} catch (Exception ex) {
					logger.error("Exception region:", ex);
				}
				try {
					result.getCarrierInfo().setStreet(objectVariant.getString("streets"));
				} catch (Exception ex) {
					logger.error("Exception streets:", ex);
				}
				try {
					result.getCarrierInfo().setNote(objectVariant.getString("howToGet"));
				} catch (Exception ex) {
					logger.error("Exception howToGet:", ex);
				}
				try {
					result.getCarrierInfo().setPhone(objectVariant.getString("phone"));
				} catch (Exception ex) {
					logger.error("Exception phone:", ex);
				}
				result.getCarrierInfo().setCityId(objectVariant.getInt("cityId"));				
				result.getCarrierInfo().setWeightMax(String.valueOf(objectVariant.getLong("maxWeight")));
				return result;
			}
			
		} catch (UnirestException e) {
			logger.error("UnirestException:", e);
		}
		return null;
	}

	/**
	 * Предварительный расчет стоимости и сроков доставки
	 * 
	 * @param weightOfG
	 * @param calculateDate
	 * @param totalAmount
	 * @param isLegalEntity     юридическое лицо (да/нет),
	 * @param deliveryVariantId способ доставки,
	 * @param isPostpay постоплата (да/нет) 
	 * @param isPaySeller       кто платит за доставку (продавец/покупатель)
	 * @return
	 */
	public DeliveryServiceResult calculate(int weightOfG, Date calculateDate, BigDecimal totalAmount,
			Long deliveryVariantId, 
			boolean isPostpay, 
			boolean isPaySeller) {

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

		Map<String, Object> queryDeliveryCalculateStrings = new HashMap<>();
		queryDeliveryCalculateStrings.put("deliveryVariantId", String.valueOf(deliveryVariantId));
		queryDeliveryCalculateStrings.put("weight", weightOfG);
		queryDeliveryCalculateStrings.put("fromPlaceId", getFromPlaceId());

		try {
			HttpResponse<JsonNode> jsonResponseDeliveryCalculate = Unirest.get(environment.getProperty("ozon.rocket.url") + "/v1/delivery/calculate")
					.headers(getHeaders())
					.queryString(queryDeliveryCalculateStrings)
					.asJson();
			
			if (jsonResponseDeliveryCalculate.getStatus() != 200) {
				return null;
			}

			double amount = jsonResponseDeliveryCalculate.getBody().getObject().getDouble("amount");
			logger.debug("amount: {}", BigDecimal.valueOf(amount));

			deliveryPrice = (new BigDecimal(String.valueOf(amount)).setScale(0, RoundingMode.HALF_UP));

			Map<String, Object> queryDeliveryTimeStrings = new HashMap<>();
			queryDeliveryTimeStrings.put("deliveryVariantId", String.valueOf(deliveryVariantId));
			queryDeliveryTimeStrings.put("fromPlaceId", getFromPlaceId());
			HttpResponse<JsonNode> jsonResponseDeliveryTime = Unirest.get(environment.getProperty("ozon.rocket.url") + "/v1/delivery/time")
					.headers(getHeaders()).queryString(queryDeliveryTimeStrings).asJson();
			deliveryPeriodMin = jsonResponseDeliveryTime.getBody().getObject().getInt("days");
			deliveryPeriodMax = deliveryPeriodMin;

			logger.debug("days: {}", deliveryPeriodMax);
		} catch (UnirestException e) {
			logger.error("UnirestException:", e);
		}
		
		deliveryCustomerSummary = deliveryPrice;
		deliveryFullPrice = deliveryCustomerSummary;

		if (isPostpay) {
			
			deliveryPostpayFee = (totalAmount.add(deliveryPrice)).multiply(new BigDecimal("2.5")).divide(BigDecimal.valueOf(100));
			deliveryPostpayFee = deliveryPostpayFee.round(new MathContext(2, RoundingMode.HALF_UP));
   	    	deliveryCustomerSummary = deliveryCustomerSummary.add(deliveryPostpayFee);   	    	
   	    	deliverySellerSummary = deliveryCustomerSummary;
   	    				
   	    	if (isPaySeller) {           	    		
  	    		postpayAmount = totalAmount;
   	    		deliveryCustomerSummary = BigDecimal.ZERO; 
   	    		deliveryFullPrice = deliverySellerSummary;
   	    	} else {
   	    		postpayAmount = totalAmount.add(deliveryCustomerSummary);
   	    		deliveryFullPrice = deliveryCustomerSummary;
   	    	}
  	    	
		} else {
			deliveryCustomerSummary = deliveryPrice;
			deliverySellerSummary = deliveryPrice.round(new MathContext(0, RoundingMode.HALF_UP)).setScale(0, RoundingMode.CEILING);
			deliveryFullPrice = deliveryCustomerSummary;
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

		result.setTermText(String.valueOf(deliveryPeriodMax) + " дн.");
		result.setWeightText(weightOfG + " г.");
		result.setErrorText(errorText);
		return result;

	}
	
	public DeliveryServiceResult calculateByAddress(int weightOfG, Date calculateDate, BigDecimal totalAmount,
			String address, 
			boolean isPostpay, 
			boolean isPaySeller) {		
		
		BigDecimal deliveryPrice = BigDecimal.ZERO;
		BigDecimal deliveryFullPrice = BigDecimal.ZERO;

		BigDecimal deliverySellerSummary = BigDecimal.ZERO;
		BigDecimal deliveryCustomerSummary = BigDecimal.ZERO;
		
		BigDecimal deliveryPostpayFee = BigDecimal.ZERO;
		BigDecimal postpayAmount = BigDecimal.ZERO;
		int deliveryPeriodMin = 0;
		int deliveryPeriodMax = 0;
		String errorText = "";
		
		JSONObject resultCalculateInformation = null;
		try {
			HttpResponse<JsonNode> jsonResponseCalculateInformation = Unirest.post(environment.getProperty("ozon.rocket.url") + "/v1/delivery/calculate/information")
					.headers(getHeaders())
					.body("{\"fromPlaceId\":" + getFromPlaceId() + ", \"destinationAddress\": \"" + address + "\", \"packages\": [{\"count\": 1,\"dimensions\": {\"weight\": 10,\"length\": 10,\"height\": 10,\"width\": 10}, \"price\": 10, \"estimatedPrice\": 10}]}")
					.asJson();			
			if (jsonResponseCalculateInformation.getStatus() != 200) {
				return null;
			}
			JSONObject jsonResponseCalculateInformationBody = jsonResponseCalculateInformation.getBody().getObject();
			JSONArray jsonResponseCalculateInformationArray = jsonResponseCalculateInformationBody.getJSONArray("deliveryInfos");	

			if (jsonResponseCalculateInformationArray.length() == 0) {
				return null;
			}			
			for (int i = 0; i <= jsonResponseCalculateInformationArray.length() - 1; i++) {
				JSONObject resultCalculateInformationItem = jsonResponseCalculateInformationArray.getJSONObject(i);
				logger.debug("resultCalculateInformation: {}, {}, {}", resultCalculateInformationItem.getString("deliveryType"), resultCalculateInformationItem.getDouble("price"), resultCalculateInformationItem.getInt("deliveryTermInDays"));
				if (DeliveryTypes.OZON_ROCKET_COURIER.getCode().equalsIgnoreCase(resultCalculateInformationItem.getString("deliveryType"))) {
					resultCalculateInformation = resultCalculateInformationItem;
					break;
				}
			}			
		} catch (UnirestException e) {
			logger.error("UnirestException:", e);
		}
		if (resultCalculateInformation == null) {
			return null;
		}			

		deliveryPrice = (new BigDecimal(String.valueOf(resultCalculateInformation.getDouble("price"))).setScale(0, RoundingMode.HALF_UP));
		
		deliveryPeriodMin = resultCalculateInformation.getInt("deliveryTermInDays");
		deliveryPeriodMax = deliveryPeriodMin;
		
		deliveryCustomerSummary = deliveryPrice;
		deliveryFullPrice = deliveryCustomerSummary;

		if (isPostpay) {
			
			deliveryPostpayFee = (totalAmount.add(deliveryPrice)).multiply(new BigDecimal("2.5")).divide(BigDecimal.valueOf(100));
			deliveryPostpayFee = deliveryPostpayFee.round(new MathContext(2, RoundingMode.HALF_UP));
   	    	deliveryCustomerSummary = deliveryCustomerSummary.add(deliveryPostpayFee);   	    	
   	    	deliverySellerSummary = deliveryCustomerSummary;
   	    				
   	    	if (isPaySeller) {           	    		
  	    		postpayAmount = totalAmount;
   	    		deliveryCustomerSummary = BigDecimal.ZERO; 
   	    		deliveryFullPrice = deliverySellerSummary;
   	    	} else {
   	    		postpayAmount = totalAmount.add(deliveryCustomerSummary);
   	    		deliveryFullPrice = deliveryCustomerSummary;
   	    	}
  	    	
		} else {
			deliveryCustomerSummary = deliveryPrice;
			deliverySellerSummary = deliveryPrice.round(new MathContext(0, RoundingMode.HALF_UP)).setScale(0, RoundingMode.CEILING);
			deliveryFullPrice = deliveryCustomerSummary;
		}
		
		DeliveryServiceResult result = new DeliveryServiceResult();
		result.setDeliveryAmount(deliveryCustomerSummary);
								
		result.setDeliveryPrice(deliveryPrice);		
		result.setDeliveryInsurance(BigDecimal.ZERO);				
		result.setDeliveryPostpayFee(deliveryPostpayFee);
		
		result.setDeliveryFullPrice(deliveryFullPrice);
		result.setDeliverySellerSummary(deliverySellerSummary);
		result.setDeliveryCustomerSummary(deliveryCustomerSummary);
		
		result.setPostpayAmount(postpayAmount);

		result.setTermText(String.valueOf(deliveryPeriodMax) + " дн.");
		result.setWeightText(weightOfG + " г.");
		result.setErrorText(errorText);
		return result;		
		
	}
	
	public String addOrder(Order order) {
		
		String result = null;
		
		String legalName = "";
		String name = "";
		String typeCustomer = "";
		boolean returnOfShippingDocuments = false;
				
		String phoneNumber = "+7" + TextUtils.phoneNumberDigit(order.getCustomer().getViewPhoneNumber().trim());
		if (order.getCustomer().isPerson()) {
			name = order.getCustomer().getViewShortName();
			typeCustomer = "NaturalPerson";			
			if (order.getDelivery().getRecipient() == null) {
				name = order.getCustomer().getViewLongName();		
			} else {
				name = order.getDelivery().getRecipient().getViewLongName();
				if (StringUtils.isNotEmpty(order.getDelivery().getRecipient().getPhoneNumber())) {
					phoneNumber = "+7" + order.getDelivery().getRecipient().getPhoneNumber().trim();
				}
			}			
		} else {				
			typeCustomer = "LegalPerson";
			ForeignerCompanyCustomer company = (ForeignerCompanyCustomer) order.getCustomer();			
			legalName = TextUtils.escapingQuotes(company.getShortName());
			//returnOfShippingDocuments = false;
		}		
		
		String typePayment = "";
		double prepareAmount = 0;
		double recipientPaymentAmount = 0;
		double deliveryPrice = 0;
		if (order.getPaymentType() == PaymentTypes.POSTPAY) {
			typePayment = "Postpay";
			recipientPaymentAmount = order.getAmounts().getTotalWithDelivery().doubleValue();
			deliveryPrice = order.getDelivery().getPrice().doubleValue();
		} else {
			typePayment = "FullPrepayment";	
			prepareAmount = order.getAmounts().getTotal().doubleValue();
		}		
		Long fromPlaceId = getFromPlaceId();
		
		String bodyRequest = "{\"orderNumber\": \"" + order.getNo() + "\",\r\n" + 
				"\"buyer\": {\r\n" + 
				"\"name\": \"" + name + "\",\r\n" + 
				"\"type\": \"" + typeCustomer + "\",\r\n" + 
				"\"legalName\": \"" + legalName + "\",\r\n" + 
				"\"email\": \"" + order.getCustomer().getEmail() + "\",\r\n" + 
				"\"phone\": \"" + phoneNumber + "\"\r\n" + 
				"},\r\n" + 
				"\"recipient\": {\r\n" + 
				"\"name\": \"" + name + "\",\r\n" + 
				"\"type\": \"" + typeCustomer + "\",\r\n" + 
				"\"legalName\": \"" + legalName + "\",\r\n" + 
				"\"email\": \"" + order.getCustomer().getEmail() + "\",\r\n" + 
				"\"phone\": \"" + phoneNumber + "\"\r\n" + 
				"},\r\n" + 
				"\"firstMileTransfer\": {\r\n" + 
				"\"type\": \"" + "DropOff" + "\",\r\n" + 
				"\"fromPlaceId\": \"" + fromPlaceId + "\"\r\n" + 
				"},\r\n" + 
				"\"payment\": {\r\n" + 
				"\"type\": \"" + typePayment + "\",\r\n" + 
				"\"prepaymentAmount\": "+ prepareAmount +",\r\n" + 
				"\"recipientPaymentAmount\": "+ recipientPaymentAmount + ",\r\n" + 
				"\"deliveryPrice\": " + deliveryPrice + ",\r\n" + 
				"\"deliveryVat\": {\r\n" + 
				"\"rate\": 0,\r\n" + 
				"\"sum\": 0\r\n" + 
				"}\r\n" + 
				"},\r\n" + 
				"\"deliveryInformation\": {\r\n" + 
				"\"deliveryVariantId\": \"" + order.getDelivery().getAddress().getCarrierInfo().getDeliveryVariantId() + "\",\r\n" + 
				"\"address\": \"" + order.getDelivery().getAddress().getAddress() + "\",\r\n" + 
				"\"deliveryType\": \""+ order.getDelivery().getDeliveryType().getCode() + "\",\r\n" + 
				"\"searchRadiusKm\": 50\r\n" + 
				"},\r\n" + 
				"\"packages\": [\r\n" + 
				"{\r\n" + 
				"\"packageNumber\": \"" + order.getNo() +  "\",\r\n" + 
				"\"dimensions\": {\r\n" + 
				"\"weight\": 20,\r\n" + 
				"\"length\": 20,\r\n" + 
				"\"height\": 20,\r\n" + 
				"\"width\": 300\r\n" + 
				"}\r\n" +
				"}\r\n" + 
				"],\r\n" + 
				"\"orderLines\": [\r\n";
				
		String bodyItems = "";
		for (int i = 0; i <= order.getItems().size() - 1; i++) {
			
			double sellingPrice = 0;
			double estimatedPrice = 0;
			
			sellingPrice = order.getItems().get(i).getPrice().doubleValue();
			estimatedPrice = order.getItems().get(i).getPrice().doubleValue();
			
			String productName;
			if (StringUtils.isNoneEmpty(order.getItems().get(i).getProduct().getDeliveryName())) {
				productName = order.getItems().get(i).getProduct().getDeliveryName();
			} else {
				productName = order.getItems().get(i).getProduct().getName(); 
			}		
			String bodyItem = "{\r\n" + 
							"\"articleNumber\": \""+ TextUtils.escapingQuotes(order.getItems().get(i).getProduct().getSku()) +"\",\r\n" + 
							"\"name\": \""+ TextUtils.escapingQuotes(productName) + "\",\r\n" + 
							"\"sellingPrice\": " + sellingPrice + ",\r\n" +							
							"\"estimatedPrice\": " + estimatedPrice + ",\r\n" +
							"\"quantity\": " + order.getItems().get(i).getQuantity() + ",\r\n" + 
							"\"vat\": {\r\n" + 
							"\"rate\": 0,\r\n" + 
							"\"sum\": 0\r\n" + 
							"},\r\n" + 
							"\"attributes\": {\r\n" + 
							"\"isDangerous\": false\r\n" + 
							"},\r\n" + 
							"\"resideInPackages\": [\"" + order.getNo() + "\"]\r\n" +
							"}";
			bodyItems += bodyItem;
			
			if (i < order.getItems().size() - 1) {
				bodyItems += ",";
			}
					
		}				
		bodyRequest += bodyItems;
								
		bodyRequest += "],\r\n" +
				
				"\"comment\": \"\",\r\n" + 
				"\"allowPartialDelivery\": true,\r\n" + 
				"\"allowUncovering\": true,\r\n" + 
				"\"orderAttributes\": {\r\n" + 
				"\"contractorShortName\": \"\",\r\n" + 
				"\"returnOfShippingDocuments\": " + returnOfShippingDocuments + "\r\n" + 
				"}\r\n" + 
				"}";		
			
		try {
			HttpResponse<JsonNode> jsonResponseOrder = Unirest.post(environment.getProperty("ozon.rocket.url") + "/v1/order")
					.headers(getHeaders())
					.body(bodyRequest)
					.asJson();			
			if (jsonResponseOrder.getStatus() != 200) {
				logger.error("jsonResponseOrder.error: {}", jsonResponseOrder.getBody().toString());
				return null;
			}
			JSONObject jsonResponseOrderBody = jsonResponseOrder.getBody().getObject();
			logger.debug("order: {}, {}", jsonResponseOrderBody.getLong("id"), jsonResponseOrderBody.getString("logisticOrderNumber"));
			
			result = jsonResponseOrderBody.getString("logisticOrderNumber");
					
		} catch (UnirestException e) {
			logger.error("UnirestException:", e);
		}
		
		return result;
		
	}
	
	public CarrierStatuses getStatus(Order order) {
		
		CarrierStatuses result = CarrierStatuses.UNKNOWN;
		
		Map<String, Object> queryStrings = new HashMap<>();
		queryStrings.put("orderNumber", order.getNo());
		try {
			HttpResponse<JsonNode> jsonResponseStatusByOrderNumber = Unirest.get(environment.getProperty("ozon.rocket.url") + "/v1/tracking/byordernumber")
					.headers(getHeaders())
					.queryString(queryStrings)
					.asJson();
			if (jsonResponseStatusByOrderNumber.getStatus() != 200) {
				return null;
			}
			JSONObject jsonResponseStatusByOrderNumberBody = jsonResponseStatusByOrderNumber.getBody().getObject();
			String orderStatusCode;
			try {
				orderStatusCode = jsonResponseStatusByOrderNumberBody.getString("orderStatusCode");				
			} catch (Exception e) {
				orderStatusCode = "";
				
			}			
			result = CarrierStatuses.getValueByCode(orderStatusCode, CrmTypes.OZON);
			
		} catch (UnirestException e) {
			logger.error("UnirestException:", e);
		}
		return result;
	}
	
	public List<Order> getStatuses(List<Order> orders) {
		List<Order> modifiedOrders = new ArrayList<Order>();
		for (Order order : orders) {
			if (StringUtils.isNotEmpty(order.getDelivery().getTrackCode())) {
				
				CarrierStatuses carrierStatus = getStatus(order);
				
				Order modifiedOrder = new Order();
				modifiedOrder.setNo(order.getNo());						
				modifiedOrder.setStatus(carrierStatus.getOrderStatus());				
				modifiedOrder.getDelivery().setTrackCode(order.getDelivery().getTrackCode());
				modifiedOrder.getDelivery().setCarrierStatus(carrierStatus);	
				modifiedOrders.add(modifiedOrder);						

			}
		}				
		return modifiedOrders;		
	}

}
