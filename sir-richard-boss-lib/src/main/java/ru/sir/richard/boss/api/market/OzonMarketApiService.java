package ru.sir.richard.boss.api.market;

import java.net.URI;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import lombok.extern.slf4j.Slf4j;
import ru.sir.richard.boss.converter.OzonOrder4OrderConverter;
import ru.sir.richard.boss.converter.OzonProduct4ProductConverter;
import ru.sir.richard.boss.model.data.Order;
import ru.sir.richard.boss.model.data.OrderExternalCrm;
import ru.sir.richard.boss.model.data.Product;
import ru.sir.richard.boss.model.data.crm.OzonResult;
import ru.sir.richard.boss.model.dto.OzonOrderDto;
import ru.sir.richard.boss.model.dto.OzonOrdersDto;
import ru.sir.richard.boss.model.dto.OzonRequestPricesDto;
import ru.sir.richard.boss.model.dto.OzonRequestStocksDto;
import ru.sir.richard.boss.model.dto.OzonResponsePricesDto;
import ru.sir.richard.boss.model.dto.OzonResponseStocksDto;
import ru.sir.richard.boss.model.types.CrmStatuses;
import ru.sir.richard.boss.model.types.CrmTypes;
import ru.sir.richard.boss.model.types.OrderStatuses;
import ru.sir.richard.boss.model.utils.DateTimeUtils;
import ru.sir.richard.boss.model.utils.Pair;

/**
 * Сервис интеграции с OZON
 * @author alex4doorow
 *
 */
@Service
@Slf4j
//https://docs.ozon.ru/api/seller/#tag/Introduction
public class OzonMarketApiService {
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
	public static final String OZON_MARKET_STATUS_ACCEPTANCE_IN_PROGRESS = "acceptance_in_progress";
	public static final String OZON_MARKET_STATUS_AWAITING_APPROVE = "awaiting_approve";
	public static final String OZON_MARKET_STATUS_AWAITING_PACKAGING = "awaiting_packaging";
	public static final String OZON_MARKET_STATUS_AWAITING_DELIVER = "awaiting_deliver";
	public static final String OZON_MARKET_STATUS_ARBITRATION = "arbitration";
	public static final String OZON_MARKET_STATUS_CLIENT_ARBITRATION = "client_arbitration";
	public static final String OZON_MARKET_STATUS_DELIVERING = "delivering";
	public static final String OZON_MARKET_STATUS_DRIVER_PICKUP = "driver_pickup";
	public static final String OZON_MARKET_STATUS_DELIVERED = "delivered";
	public static final String OZON_MARKET_STATUS_CANCELED = "cancelled";
	public static final String OZON_MARKET_STATUS_NOT_ACCEPTED = "not_accepted";
	
	@Autowired
	private OzonOrder4OrderConverter ozonOrder4OrderConverter;
	
	@Autowired
	private OzonProduct4ProductConverter ozonProduct4ProductConverter;		
	
	@Autowired
	private Environment environment;
	
    private WebClient webClient;
	
	@PostConstruct
    private void init() {
		webClient = WebClient.builder()
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();        
    }
	
	public List<Order> getOrders(Pair<Date> period, String ozonStatus) {
		
		String sinceStart = DateTimeUtils.formatDate(period.getStart(), DateTimeUtils.DATE_FORMAT_OZON);
		String sinceEnd = DateTimeUtils.formatDate(period.getEnd(), DateTimeUtils.DATE_FORMAT_OZON);		
				
		final String url = environment.getProperty("ozon.market.url") + "/v3/posting/fbs/list";
		log.debug("ozon: {}", url);
		String inputJson = "{"
				+ "\"dir\": \"asc\",\r\n"
				+ "\"filter\": {\"delivery_method_id\": ["				
					+ environment.getProperty("ozon.market.cdek.delivery.courier.main") + ", " 
					+ environment.getProperty("ozon.market.cdek.delivery.courier.economy") + ", "
					+ environment.getProperty("ozon.market.cdek.delivery.pvz.main") + ", "
					+ environment.getProperty("ozon.market.cdek.delivery.pvz.economy") + ", "
					+ environment.getProperty("ozon.market.ozon.warehouse.pickup")
				+ "],\r\n"
				+ "\"order_id\": 0,\r\n"
				+ "\"provider_id\": [" + environment.getProperty("ozon.market.ozon.provider") + ", " + environment.getProperty("ozon.market.cdek.provider") + "],\r\n"
				+ "\"since\": \""+ sinceStart +"\",\"to\": \""+ sinceEnd + "\",\r\n"
				+ "\"status\": \"" + ozonStatus + "\",\r\n"
				+ "\"warehouse_id\": [" + environment.getProperty("ozon.market.cdek.warehouse") + "," + environment.getProperty("ozon.market.ozon.warehouse.pickup") +"]},\r\n"
				+ "\"limit\": 50,\r\n"
				+ "\"offset\": 0,\r\n"
				+ "\"with\": {\"analytics_data\": true,\"barcodes\": false,\"financial_data\": true}"
				+ "}";		

		OzonOrdersDto ozonOrdersDto;
		List<Order> result;
		try {
			ozonOrdersDto = webClient.post()
			        .uri(new URI(url))
			        .header("Host", environment.getProperty("ozon.market.host"))
			        .header("Client-Id", environment.getProperty("ozon.market.client.id"))
			        .header("Api-Key", environment.getProperty("ozon.market.client.key"))
			        .contentType(MediaType.APPLICATION_JSON)
			        .bodyValue(inputJson)
			        .retrieve()
			        .onStatus(
			        	    HttpStatus.NOT_FOUND::equals,
			        	    response -> response.bodyToMono(String.class).map(Exception::new))
			        .bodyToMono(OzonOrdersDto.class)
			        .retry(3)
			        .log()
			        .block();
			log.debug("ozonOrdersDto: {}", ozonOrdersDto);        
	        result = ozonOrder4OrderConverter.convertToEntities(ozonOrdersDto);
	        log.debug("orders: {}", result);
	        return result;
		} catch (Exception e) {
			log.error("getOrders: {}", e);			
		}		
		return null;		
	}
	
	public Order getOrder(String ozonOrderId) {

		final String url = environment.getProperty("ozon.market.url") + "/v3/posting/fbs/get";		
		String inputJson = "{\"posting_number\": \"" + ozonOrderId + "\",\"with\": {\"analytics_data\": true,\"barcodes\": false,\"financial_data\": false}}";
		
		Order result;
		OzonOrderDto ozonOrderDto;
		log.debug("ozon: {}", url);
		try {
			ozonOrderDto = webClient.post()
			        .uri(new URI(url))
			        .header("Host", environment.getProperty("ozon.market.host"))
			        .header("Client-Id", environment.getProperty("ozon.market.client.id"))
			        .header("Api-Key", environment.getProperty("ozon.market.client.key"))
			        .contentType(MediaType.APPLICATION_JSON)
			        .bodyValue(inputJson)
			        .retrieve()
			        .onStatus(
			        	    HttpStatus.NOT_FOUND::equals,
			        	    response -> response.bodyToMono(String.class).map(Exception::new))
			        .bodyToMono(OzonOrderDto.class)
			        .retry(3)
			        .log()
			        .block();
			log.debug("ozonOrderDto: {}", ozonOrderDto);        
	        result = ozonOrder4OrderConverter.convertToEntity(ozonOrderDto);
	        log.debug("order: {}", result);
		} catch (Exception e) {
			log.error("getOrder ozonOrderId: {}", ozonOrderId, e);
			result = new Order();
			result.setStatus(OrderStatuses.UNKNOWN);			
			result.setExternalCrms(Collections.singletonList(new OrderExternalCrm(CrmTypes.OZON, CrmStatuses.FAIL, 0L, ozonOrderId)));
		}
        return result;	
	}
	
	public OzonResult offerWarehouseStocks(boolean isOzonEnabled, List<Product> products) {

		OzonResult ozonResult = new OzonResult();
		OzonRequestStocksDto inputOzonStocksDto = ozonProduct4ProductConverter.convertToStocksDto(isOzonEnabled, products);
		final String url = environment.getProperty("ozon.market.url") + "/v2/products/stocks";	
		log.debug("ozon: {}", url);
		try {
			OzonResponseStocksDto resultResponse = webClient.post()
			        .uri(new URI(url))
			        .header("Host", environment.getProperty("ozon.market.host"))
			        .header("Client-Id", environment.getProperty("ozon.market.client.id"))
			        .header("Api-Key", environment.getProperty("ozon.market.client.key"))
			        .contentType(MediaType.APPLICATION_JSON)
			        .bodyValue(inputOzonStocksDto)
			        .retrieve()
			        .onStatus(HttpStatus.NOT_FOUND::equals, response -> response.bodyToMono(String.class).map(Exception::new))
			        .bodyToMono(OzonResponseStocksDto.class)
			        .retry(3)
			        .log()
			        .block(); 
			log.debug("result: {}", resultResponse);
			if (!resultResponse.getErrors().isEmpty()) {
				log.debug("errors: {}", resultResponse.getErrors());
			}
			ozonResult.setDirtyResponce(resultResponse);
    	    ozonResult.setResponseSuccess(true);
		} catch (Exception e) {			
			log.error("offerWarehouseStocks:", e);
			ozonResult.setResponseSuccess(false);
		}		
		return ozonResult;		
	}
	
	public OzonResult offerPrices(List<Product> products) {
		OzonResult ozonResult = new OzonResult();		
		if (products == null || products.isEmpty()) {
			return ozonResult;
		}
		final String url = environment.getProperty("ozon.market.url") + "/v1/product/import/prices";
		log.debug("ozon: {}", url);
		OzonRequestPricesDto inputOzonPricesDto = ozonProduct4ProductConverter.convertToPricesDto(products);		
		try {
			OzonResponsePricesDto resultResponse = webClient.post()
			        .uri(new URI(url))
			        .header("Host", environment.getProperty("ozon.market.host"))
			        .header("Client-Id", environment.getProperty("ozon.market.client.id"))
			        .header("Api-Key", environment.getProperty("ozon.market.client.key"))
			        .contentType(MediaType.APPLICATION_JSON)
			        .bodyValue(inputOzonPricesDto)
			        .retrieve()
			        .onStatus(
			        	    HttpStatus.NOT_FOUND::equals,
			        	    response -> response.bodyToMono(String.class).map(Exception::new))
			        .bodyToMono(OzonResponsePricesDto.class)
			        .retry(3)
			        .log()
			        .block(); 
			log.debug("result: {}", resultResponse);
			if (!resultResponse.getErrors().isEmpty()) {
				log.debug("errors: {}", resultResponse.getErrors());
			}
			ozonResult.setDirtyResponce(resultResponse);
    	    ozonResult.setResponseSuccess(true);
		} catch (Exception e) {			
			log.error("offerWarehouseProcks:", e);
			ozonResult.setResponseSuccess(false);
		}
		return ozonResult;		
	}
}