package ru.sir.richard.boss.api.market;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;
import ru.sir.richard.boss.converter.OzonOrder4OrderConverter;
import ru.sir.richard.boss.converter.OzonProduct4ProductConverter;
import ru.sir.richard.boss.model.data.Order;
import ru.sir.richard.boss.model.data.Product;
import ru.sir.richard.boss.model.data.crm.OzonResult;
import ru.sir.richard.boss.model.dto.OzonOrderDto;
import ru.sir.richard.boss.model.dto.OzonRequestPricesDto;
import ru.sir.richard.boss.model.dto.OzonRequestStockDto;
import ru.sir.richard.boss.model.dto.OzonRequestStocksDto;
import ru.sir.richard.boss.model.types.CrmStatuses;
import ru.sir.richard.boss.model.types.CrmTypes;
import ru.sir.richard.boss.model.types.OrderStatuses;
import ru.sir.richard.boss.model.utils.DateTimeUtils;
import ru.sir.richard.boss.model.utils.Pair;

@Slf4j
@SpringBootTest
@PropertySource(value="classpath:application-it.properties", encoding="UTF-8")
public class OzomMarketApiServiceTest {
	
	@Autowired
	private Environment environment;
	
	@Autowired
	private OzonMarketApiService ozonMarketApiService;

	@Autowired
	private OzonOrder4OrderConverter ozonOrder4OrderConverter;
	
	@Autowired
	private OzonProduct4ProductConverter ozonProduct4ProductConverter;
	
	@Test
	public void testOzonOrderToOrderConvertUsingMapper() throws ParseException, JsonMappingException, JsonProcessingException {
		
        String jsonOutString = "{\"result\": {\n" +
                "        \"posting_number\": \"17797402-0235-1\",\n" +
                "        \"order_id\": 5000234969,\n" +
                "        \"order_number\": \"17797402-0235\",\n" +
                "        \"status\": \"delivered\",\n" +
                "        \"delivery_method\": {\n" +
                "            \"id\": 22294396015000,\n" +
                "            \"name\": \"Ozon Rocket самостоятельно, Москва\",\n" +
                "            \"warehouse_id\": 22294396015000,\n" +
                "            \"warehouse\": \"OZON логистика\",\n" +
                "            \"tpl_provider_id\": 24,\n" +
                "            \"tpl_provider\": \"Ozon Rocket\"\n" +
                "        },\n" +
                "        \"tracking_number\": \"\",\n" +
                "        \"tpl_integration_type\": \"ozon\",\n" +
                "        \"in_process_at\": \"2021-10-08T09:15:06Z\",\n" +
                "        \"shipment_date\": \"2021-10-11T10:00:00Z\",\n" +
                "        \"delivering_date\": \"2021-10-11T10:19:26Z\",\n" +
                "        \"provider_status\": \"\",\n" +
                "        \"delivery_price\": \"\",\n" +
                "        \"cancellation\": {\n" +
                "            \"cancel_reason_id\": 0,\n" +
                "            \"cancel_reason\": \"\",\n" +
                "            \"cancellation_type\": \"\",\n" +
                "            \"cancelled_after_ship\": false,\n" +
                "            \"affect_cancellation_rating\": false,\n" +
                "            \"cancellation_initiator\": \"\"\n" +
                "        },\n" +
                "        \"customer\": null,\n" +
                "        \"addressee\": null,\n" +
                "        \"products\": [\n" +
                "            {\n" +
                "                \"price\": \"3790.0000\",\n" +
                "                \"offer_id\": \"BAMBOO-2\",\n" +
                "                \"name\": \"Столик подставка для ноутбука SITITEK Bamboo 2\",\n" +
                "                \"sku\": 315593714,\n" +
                "                \"quantity\": 1,\n" +
                "                \"mandatory_mark\": [],\n" +
                "                \"dimensions\": {\n" +
                "                    \"height\": \"100.00\",\n" +
                "                    \"length\": \"600.00\",\n" +
                "                    \"weight\": \"2900\",\n" +
                "                    \"width\": \"400.00\"\n" +
                "                },\n" +
                "                \"currency_code\": \"RUB\"\n" +
                "            }\n" +
                "        ],\n" +
                "        \"barcodes\": {\n" +
                "            \"upper_barcode\": \"%101%563209110\",\n" +
                "            \"lower_barcode\": \"22301321305000\"\n" +
                "        },\n" +
                "        \"analytics_data\": {\n" +
                "            \"region\": \"Свердловская Область\",\n" +
                "            \"city\": \"Полевской\",\n" +
                "            \"delivery_type\": \"PVZ\",\n" +
                "            \"is_premium\": true,\n" +
                "            \"payment_type_group_name\": \"Карты оплаты\",\n" +
                "            \"warehouse_id\": 22294396015000,\n" +
                "            \"warehouse\": \"OZON логистика\",\n" +
                "            \"tpl_provider_id\": 24,\n" +
                "            \"tpl_provider\": \"Ozon Rocket\",\n" +
                "            \"delivery_date_begin\": \"2021-10-14T12:01:00Z\",\n" +
                "            \"delivery_date_end\": \"2021-10-14T16:00:00Z\",\n" +
                "            \"is_legal\": false\n" +
                "        },\n" +
                "        \"financial_data\": {\n" +
                "            \"products\": [\n" +
                "                {\n" +
                "                    \"commission_amount\": 0,\n" +
                "                    \"commission_percent\": 15,\n" +
                "                    \"payout\": 3221.5,\n" +
                "                    \"product_id\": 315593714,\n" +
                "                    \"old_price\": 3790,\n" +
                "                    \"price\": 3790,\n" +
                "                    \"total_discount_value\": 0,\n" +
                "                    \"total_discount_percent\": 0,\n" +
                "                    \"actions\": [],\n" +
                "                    \"picking\": null,\n" +
                "                    \"quantity\": 1,\n" +
                "                    \"client_price\": \"\",\n" +
                "                    \"item_services\": {\n" +
                "                        \"marketplace_service_item_fulfillment\": 0,\n" +
                "                        \"marketplace_service_item_pickup\": 0,\n" +
                "                        \"marketplace_service_item_dropoff_pvz\": 0,\n" +
                "                        \"marketplace_service_item_dropoff_sc\": 0,\n" +
                "                        \"marketplace_service_item_dropoff_ff\": 0,\n" +
                "                        \"marketplace_service_item_direct_flow_trans\": -91.2,\n" +
                "                        \"marketplace_service_item_return_flow_trans\": 0,\n" +
                "                        \"marketplace_service_item_deliv_to_customer\": 0,\n" +
                "                        \"marketplace_service_item_return_not_deliv_to_customer\": 0,\n" +
                "                        \"marketplace_service_item_return_part_goods_customer\": 0,\n" +
                "                        \"marketplace_service_item_return_after_deliv_to_customer\": 0\n" +
                "                    },\n" +
                "                    \"currency_code\": \"RUB\"\n" +
                "                }\n" +
                "            ],\n" +
                "            \"posting_services\": {\n" +
                "                \"marketplace_service_item_fulfillment\": 0,\n" +
                "                \"marketplace_service_item_pickup\": 0,\n" +
                "                \"marketplace_service_item_dropoff_pvz\": 0,\n" +
                "                \"marketplace_service_item_dropoff_sc\": 0,\n" +
                "                \"marketplace_service_item_dropoff_ff\": 0,\n" +
                "                \"marketplace_service_item_direct_flow_trans\": 0,\n" +
                "                \"marketplace_service_item_return_flow_trans\": 0,\n" +
                "                \"marketplace_service_item_deliv_to_customer\": -166.76,\n" +
                "                \"marketplace_service_item_return_not_deliv_to_customer\": 0,\n" +
                "                \"marketplace_service_item_return_part_goods_customer\": 0,\n" +
                "                \"marketplace_service_item_return_after_deliv_to_customer\": 0\n" +
                "            },\n" +
                "            \"cluster_from\": \"Московская область (Недействительный)\",\n" +
                "            \"cluster_to\": \"Екатеринбург\"\n" +
                "        },\n" +
                "        \"additional_data\": [],\n" +
                "        \"is_express\": false,\n" +
                "        \"requirements\": {\n" +
                "            \"products_requiring_gtd\": [],\n" +
                "            \"products_requiring_country\": [],\n" +
                "            \"products_requiring_mandatory_mark\": [],\n" +
                "            \"products_requiring_rnpt\": []\n" +
                "        },\n" +
                "        \"product_exemplars\": null,\n" +
                "        \"courier\": null,\n" +
                "        \"parent_posting_number\": \"\",\n" +
                "        \"related_postings\": null,\n" +
                "        \"available_actions\": []\n" +
                "    }\n" +
                "}";
        //https://www.baeldung.com/java-modelmapper        
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        OzonOrderDto ozonOrder = mapper.readValue(jsonOutString, OzonOrderDto.class);
        log.info("ozonOrder: {}", ozonOrder);
        
        assertEquals("17797402-0235-1", ozonOrder.getResult().getPostingNumber());
        assertEquals(5000234969L, ozonOrder.getResult().getOrderId());        
        assertEquals(24, ozonOrder.getResult().getDeliveryMethod().getTplProviderId());
        assertEquals("delivered", ozonOrder.getResult().getStatus());
        assertEquals(1, ozonOrder.getResult().getProducts().length);
                        
        Order order = ozonOrder4OrderConverter.convertToEntity(ozonOrder);
        log.info("order: {}, {}, {}, {}", order.getId(), order.getDelivery().getAddress().getAddress(), order.getStatus(), order.getItems());
        
        assertEquals("17797402-0235-1", order.getExternalCrmByCode(CrmTypes.OZON).getParentCode());
        assertEquals(5000234969L, order.getExternalCrmByCode(CrmTypes.OZON).getParentId());
        assertEquals("Свердловская Область, Полевской", order.getDelivery().getAddress().getAddress());
        assertEquals(OrderStatuses.DELIVERED, order.getStatus());
        assertEquals(1, order.getItems().size());
        assertEquals(environment.getProperty("ozon.market.ozon.default.customer.phoneNumber"), order.getCustomer().getViewPhoneNumber());
    }
	
	@Test
	public void testOzonOrderToOrderConvertUsingWebClient() {
		String ozonOrderId = "17797402-0235-1";
		Order order = ozonMarketApiService.getOrder(ozonOrderId);
		assertFalse(order == null);
		assertEquals(ozonOrderId, order.getExternalCrmByCode(CrmTypes.OZON).getParentCode());
        assertEquals(388353822, order.getExternalCrmByCode(CrmTypes.OZON).getParentId());
        assertEquals("Свердловская Область, Полевской", order.getDelivery().getAddress().getAddress());
        assertEquals(OrderStatuses.DELIVERED, order.getStatus());
        assertEquals(1, order.getItems().size());
        assertEquals(environment.getProperty("ozon.market.ozon.default.customer.phoneNumber"), order.getCustomer().getViewPhoneNumber());
        
        ozonOrderId = "03149662-0230-1";
        order = ozonMarketApiService.getOrder(ozonOrderId);
        assertEquals(ozonOrderId, order.getExternalCrmByCode(CrmTypes.OZON).getParentCode());        
	}
	
	@Test
	public void testOzonOrderToOrderConvertUsingWebClientNotFound() {
		String ozonOrderId = "17797402-0235-2";
		Order order = ozonMarketApiService.getOrder(ozonOrderId);
		assertFalse(order == null);
		assertEquals(OrderStatuses.UNKNOWN, order.getStatus());
		assertEquals(CrmStatuses.FAIL, order.getExternalCrmByCode(CrmTypes.OZON).getStatus());
	}	
	
	@Test
	public void testOzonOrdersToOrdersConvertUsingWebClient() throws ParseException {
		Date executorDate = DateTimeUtils.defaultFormatStringToDate("20.11.2022");
		Pair<Date> period = new Pair<Date>(DateTimeUtils.beforeAnyDate(executorDate, 1),
				DateTimeUtils.afterAnyDate(executorDate, 1));

		List<Order> orders;
		orders = ozonMarketApiService.getOrders(period, OzonMarketApiService.OZON_MARKET_STATUS_DELIVERING);
		log.info("orders: {}", orders);
		
		orders = ozonMarketApiService.getOrders(period, OzonMarketApiService.OZON_MARKET_STATUS_AWAITING_APPROVE);
		log.info("orders: {}", orders);

		orders = ozonMarketApiService.getOrders(period, OzonMarketApiService.OZON_MARKET_STATUS_AWAITING_DELIVER);
		log.info("orders: {}", orders);
	}
	
	@Test
	public void testProductStoksToOzonStocksDto() throws ParseException, JsonProcessingException {
		Product product;
		List<Product> products;
		OzonRequestStocksDto ozonStocksDto;

		product = new Product(32, "CARKU PRO-60");
		product.setName("ПУСКО-ЗАРЯДНОЕ УСТРОЙСТВО \"CARKU PRO-60\"");
		product.setSku("CARKU-PRO-60");
		product.setQuantity(12);
		product.getMarket(CrmTypes.OZON).setMarketSku("323061675");
		product.getMarket(CrmTypes.OZON).setMarketSeller(true);		
		products = Collections.singletonList(product);		
		ozonStocksDto = ozonProduct4ProductConverter.convertToStocksDto(true, products);
		log.debug("ozonStocksDto: {}", ozonStocksDto);
		assertFalse(ozonStocksDto == null);		
		assertEquals(3, ozonStocksDto.getOzonRequestStockDtos().size());	
		for (OzonRequestStockDto item: ozonStocksDto.getOzonRequestStockDtos()) {
			if (item.getWarehouseId() == Long.valueOf(environment.getProperty("ozon.market.cdek.warehouse"))) {
				assertEquals(2, item.getStock());				
			} else if (item.getWarehouseId() == Long.valueOf(environment.getProperty("ozon.market.ozon.warehouse.express"))) {
				assertEquals(2, item.getStock());				
			} else if (item.getWarehouseId() == Long.valueOf(environment.getProperty("ozon.market.ozon.warehouse.pickup"))) {
				assertEquals(8, item.getStock());				
			}			
		}		
		product = new Product(32, "CARKU PRO-60");
		product.setName("ПУСКО-ЗАРЯДНОЕ УСТРОЙСТВО \"CARKU PRO-60\"");
		product.setSku("CARKU-PRO-60");
		product.setQuantity(2);
		product.getMarket(CrmTypes.OZON).setMarketSku("323061675");
		product.getMarket(CrmTypes.OZON).setMarketSeller(true);		
		products = Collections.singletonList(product);		
		ozonStocksDto = ozonProduct4ProductConverter.convertToStocksDto(true, products);
		log.debug("ozonStocksDto: {}", ozonStocksDto);
		assertFalse(ozonStocksDto == null);
		assertEquals(3, ozonStocksDto.getOzonRequestStockDtos().size());	
		for (OzonRequestStockDto item: ozonStocksDto.getOzonRequestStockDtos()) {
			if (item.getWarehouseId() == Long.valueOf(environment.getProperty("ozon.market.cdek.warehouse"))) {
				assertEquals(0, item.getStock());				
			} else if (item.getWarehouseId() == Long.valueOf(environment.getProperty("ozon.market.ozon.warehouse.express"))) {
				assertEquals(0, item.getStock());				
			} else if (item.getWarehouseId() == Long.valueOf(environment.getProperty("ozon.market.ozon.warehouse.pickup"))) {
				assertEquals(2, item.getStock());				
			}			
		}	
	}
	
	@Test
	public void testProductStoksToOzonStocksWebClient() {
		Product product = new Product(32, "CARKU PRO-60");
		product.setName("ПУСКО-ЗАРЯДНОЕ УСТРОЙСТВО \"CARKU PRO-60\"");
		product.setSku("CARKU-PRO-60");
		product.setQuantity(1);
		product.getMarket(CrmTypes.OZON).setMarketSku("323061675");
		product.getMarket(CrmTypes.OZON).setMarketSeller(true);		
		List<Product> products = Collections.singletonList(product);	
		OzonResult ozonResult = ozonMarketApiService.offerWarehouseStocks(true, products);
		log.debug("ozon result: {}", ozonResult);
	}
	
	@Test
	public void testProductPricesToOzonPricesDto() throws ParseException, JsonProcessingException {
		Product product = new Product(32, "CARKU PRO-60");
		product.setName("ПУСКО-ЗАРЯДНОЕ УСТРОЙСТВО \"CARKU PRO-60\"");
		product.setSku("CARKU-PRO-60");
		product.setQuantity(1);
		product.setPriceWithoutDiscount(BigDecimal.valueOf(16990));		
		product.setPriceWithDiscount(BigDecimal.valueOf(13990));
		product.getMarket(CrmTypes.OZON).setMarketSku("323061675");
		product.getMarket(CrmTypes.OZON).setMarketSeller(true);		
		List<Product> products = Collections.singletonList(product);	
		
		OzonRequestPricesDto ozonRequestPricesDto = ozonProduct4ProductConverter.convertToPricesDto(products);
		log.debug("ozonRequestPricesDto: {}", ozonRequestPricesDto);
		assertFalse(ozonRequestPricesDto == null);
	}
	
	@Test
	public void testProductPricesToOzonPricesWebClient() {
		Product product = new Product(32, "CARKU PRO-60");
		product.setName("ПУСКО-ЗАРЯДНОЕ УСТРОЙСТВО \"CARKU PRO-60\"");
		product.setSku("CARKU-PRO-60");
		product.setPriceWithoutDiscount(BigDecimal.valueOf(16990));		
		product.setPriceWithDiscount(BigDecimal.valueOf(13990));
		product.getMarket(CrmTypes.OZON).setMarketSku("323061675");
		product.getMarket(CrmTypes.OZON).setMarketSeller(true);		
		List<Product> products = Collections.singletonList(product);	
		OzonResult ozonResult = ozonMarketApiService.offerPrices(products);
		log.debug("ozon result: {}", ozonResult);
	}
}
