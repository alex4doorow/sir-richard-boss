package ru.sir.richard.boss.api.cdek;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.PropertySource;

import lombok.extern.slf4j.Slf4j;

import ru.sir.richard.boss.converter.CdekConverter;
import ru.sir.richard.boss.crm.DeliveryService;
import ru.sir.richard.boss.dao.OrderDao;
import ru.sir.richard.boss.dao.WikiDao;
import ru.sir.richard.boss.model.data.Address;
import ru.sir.richard.boss.model.data.Order;
import ru.sir.richard.boss.model.data.Product;
import ru.sir.richard.boss.model.data.ProductCategory;
import ru.sir.richard.boss.model.data.crm.DeliveryServiceResult;
import ru.sir.richard.boss.model.dto.CdekOrderDto;
import ru.sir.richard.boss.model.dto.CdekResponseOrderDto;
import ru.sir.richard.boss.model.types.DeliveryTypes;
import ru.sir.richard.boss.model.utils.DateTimeUtils;
import ru.sir.richard.boss.utils.WikiTestHelper;

import static org.junit.jupiter.api.Assertions.*;
import static org.hamcrest.MatcherAssert.assertThat;

@Slf4j
@SpringBootTest
@PropertySource(value="classpath:application-it.properties", encoding="UTF-8")
public class CdekApiServiceTest {

	@Autowired
    private CdekConverter cdekConverter;

	@Autowired
    private CdekApiService cdekApiService;

	@Autowired
	private WikiDao wikiDao;

	@Autowired
	private OrderDao orderDao;

	@Autowired
	private DeliveryService deliveryService;

	@Test
    public void testGetCitiesSimple() throws URISyntaxException {
    	List<Address> cities = cdekApiService.getCities("Киров");
    	cities.forEach((city) -> log.debug("testGetCdekCities: {}, {}, {}", city.getAddress(), city.getCarrierInfo().getCityId(), city.getCarrierInfo().getPostalSize()));        
    	assertTrue(cities.size() == 8, "Киров");
    }
    
	@Test
	public void testGetCitiesOtherWize() {	
		
		List<Address> cities = cdekApiService.getCities("Авдеевка");
		cities.forEach((city) -> log.debug("testGetCdekCities: {}", city.getAddress()));		
		int i = 0;
		for (Address city : cities) {
			if ("Авдеевка, Хвастовичский район, Калужская область, Россия".equals(city.getAddress())) {
				i++;
			}			
		}
		assertTrue(i == 1, "Авдеевка, Хвастовичский район, Калужская область, Россия");

		cities = cdekApiService.getCities("Калуга");
		cities.forEach((city) -> log.debug("testGetCdekCities: {}", city.getAddress()));
		i = 0;
		int j = 0;
		for (Address city : cities) {
			if ("Калуга, Калужская область, Россия".equals(city.getAddress())) {
				i++;
			}			
			if ("Калуган, Тегеран, Иран".equals(city.getAddress())) {
				j++;
			}
		}
		assertTrue(i == 1, "Калуга, Калужская область, Россия");
		assertFalse(j == 1, "Калуган, Тегеран, Иран");		
	}
	
	@Test
	public void testGetPVZs() {
		List<Address> pvzs = cdekApiService.getPvzs(415);
		pvzs.forEach((pvz) -> log.debug("testGetPVZs: {}, {}, {}, {}", pvz.getCarrierInfo().getPvzId(), 
				pvz.getCarrierInfo().getPvz(), 
				pvz.getCarrierInfo().getFullAddress(), 
				pvz.getCarrierInfo().getAddressComment()));
		
	}
	
	@Test
	public void testGetOrder() {
		Order order = cdekApiService.getOrderByTrackCode("1378911159", null);
		log.debug("order: {}", order);		
	}
	
	@Test
	public void testGetOrderStatuses() {
		List<Order> orders = new ArrayList<>();
		Order one = new Order();
		one.getDelivery().setTrackCode("1350203193");
		orders.add(one);
		
		Order two = new Order();
		two.getDelivery().setTrackCode("1350203193");
		orders.add(two);
		
		Order three = new Order();
		three.getDelivery().setTrackCode("1378911159");
		orders.add(three);
				
		List<Order> withStatusesOrders = cdekApiService.getStatuses(orders);
		withStatusesOrders.forEach((withStatusesOrder) -> log.debug("withStatusesOrder: {}, {}, {}", withStatusesOrder.getNo(), 
				withStatusesOrder.getDelivery().getTrackCode(), 
				withStatusesOrder.getDelivery().getCarrierStatus()));		
	}

	@Test
	public void testConvertOrderToJson_1() throws CloneNotSupportedException, IOException {

		// 1) company, pvz - pvz, prepaiment
		ProductCategory categoryOne = WikiTestHelper.createProductCategory(102, wikiDao);
		Product productOne = WikiTestHelper.createProduct(32, categoryOne, wikiDao);

		int sourceOrderId = 9835;
		Order sourceOrder = orderDao.findById(sourceOrderId);
		Order testing4CdekOrder = sourceOrder.clone();
		testing4CdekOrder.setId(0);
		testing4CdekOrder.setNo(orderDao.nextOrderNo());
		testing4CdekOrder.setProductCategory(categoryOne);
		testing4CdekOrder.getItems().get(0).setProduct(productOne);

		CdekOrderDto cdekOrder = cdekConverter.convertOrderToCdekOrderDto(testing4CdekOrder, deliveryService.calcTotalWeightG(testing4CdekOrder));
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		mapper.writeValue(new File("c:\\src\\sir-richard-boss\\--1-save\\cdek_order_1.json"), cdekOrder.getEntity());

		// 4) customer, pvz - courier, postpaiment
		// 5) customer, pvz - pvz, prepaiment
		// 6) customer, pvz - courier, postpaiment
	}

	@Test
	public void testConvertOrderToJson_2() throws CloneNotSupportedException, IOException {

		// 2) company, pvz - courier, prepaiment
		ProductCategory categoryOne = WikiTestHelper.createProductCategory(102, wikiDao);
		Product productOne = WikiTestHelper.createProduct(32, categoryOne, wikiDao);

		int sourceOrderId = 9835;
		Order sourceOrder = orderDao.findById(sourceOrderId);
		Order testing4CdekOrder = sourceOrder.clone();
		testing4CdekOrder.setId(0);
		testing4CdekOrder.setNo(orderDao.nextOrderNo());
		testing4CdekOrder.setProductCategory(categoryOne);
		testing4CdekOrder.getItems().get(0).setProduct(productOne);

		testing4CdekOrder.getDelivery().setDeliveryType(DeliveryTypes.CDEK_COURIER);
		testing4CdekOrder.getDelivery().getAddress().getCarrierInfo().setCityId(877);
		testing4CdekOrder.getDelivery().getAddress().getCarrierInfo().setStreet("Ленина");
		testing4CdekOrder.getDelivery().getAddress().getCarrierInfo().setHouse("10к3");
		testing4CdekOrder.getDelivery().getAddress().getCarrierInfo().setFlat("43");
		testing4CdekOrder.getDelivery().getAddress().setAddress("г. Братск, Ленина ул., дом 10к3, кв. 43");

		CdekOrderDto cdekOrder = cdekConverter.convertOrderToCdekOrderDto(testing4CdekOrder, deliveryService.calcTotalWeightG(testing4CdekOrder));
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		mapper.writeValue(new File("c:\\src\\sir-richard-boss\\--1-save\\cdek_order_2.json"), cdekOrder.getEntity());
	}

	@Test
	public void testConvertOrderToJson_3() throws CloneNotSupportedException, IOException {
		// 3) customer, pvz - pvz, postpaiment
		ProductCategory categoryOne = WikiTestHelper.createProductCategory(102, wikiDao);
		Product productOne = WikiTestHelper.createProduct(32, categoryOne, wikiDao);

		int sourceOrderId = 9611;
		Order sourceOrder = orderDao.findById(sourceOrderId);
		Order testing4CdekOrder = sourceOrder.clone();
		testing4CdekOrder.setId(0);
		testing4CdekOrder.setNo(orderDao.nextOrderNo());
		testing4CdekOrder.setProductCategory(categoryOne);
		testing4CdekOrder.getItems().get(0).setProduct(productOne);

		CdekOrderDto cdekOrder = cdekConverter.convertOrderToCdekOrderDto(testing4CdekOrder, deliveryService.calcTotalWeightG(testing4CdekOrder));
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		mapper.writeValue(new File("c:\\src\\sir-richard-boss\\--1-save\\cdek_order_3.json"), cdekOrder.getEntity());
	}

	@Test
	public void testAddOrder() throws CloneNotSupportedException {
		ProductCategory categoryOne = WikiTestHelper.createProductCategory(102, wikiDao) ;
		Product productOne = WikiTestHelper.createProduct(32, categoryOne, wikiDao);
		productOne.getStore().setHeight(0);
		productOne.getStore().setLength(0);
		productOne.getStore().setWeight(BigDecimal.valueOf(0));

		int sourceOrderId = 9835;
		Order sourceOrder = orderDao.findById(sourceOrderId);
		Order testing4CdekOrder = sourceOrder.clone();
		testing4CdekOrder.setId(0);
		testing4CdekOrder.setNo(orderDao.nextOrderNo());
		testing4CdekOrder.setProductCategory(categoryOne);
		testing4CdekOrder.getItems().get(0).setProduct(productOne);

		testing4CdekOrder.getDelivery().setDeliveryType(DeliveryTypes.CDEK_PVZ_TYPICAL);
		testing4CdekOrder.getDelivery().getAddress().getCarrierInfo().setCityId(877);
		testing4CdekOrder.getDelivery().getAddress().getCarrierInfo().setPvz("BRK61");
		testing4CdekOrder.getDelivery().getAddress().setAddress("г. Братск, ул. Мира, 37А");

		// company, pvz - pvz
		int testing4CdekOrderId = orderDao.addOrder(testing4CdekOrder);
		testing4CdekOrder = orderDao.findById(testing4CdekOrderId);
		CdekResponseOrderDto result = cdekApiService.addOrder(testing4CdekOrder,
				deliveryService.calcTotalWeightG(testing4CdekOrder),
				null);
		log.debug("result:{}", result);
		Order orderByUUID = cdekApiService.getOrderByUUIDTryingTen(result.getEntity().getUuid());
		log.info("orderByUUID: {}", orderByUUID);
		assertNotNull(orderByUUID.getDelivery().getTrackCode());
	}

	@Test
	public void testCalculate() throws Exception {
		ProductCategory categoryOne = WikiTestHelper.createProductCategory(102, wikiDao) ;
		Product productOne = WikiTestHelper.createProduct(32, categoryOne, wikiDao);

		// customer, tumen, pvz, postpay
		int sourceOrderId = 9850;
		Order sourceOrder = orderDao.findById(sourceOrderId);
		sourceOrder.setProductCategory(categoryOne);
		sourceOrder.getItems().get(0).setProduct(productOne);
		sourceOrder.getDelivery().setDeliveryType(DeliveryTypes.CDEK_PVZ_TYPICAL);
		sourceOrder.getDelivery().getAddress().getCarrierInfo().setCityId(252);

		DeliveryServiceResult calculatedData;
		calculatedData = cdekApiService.calculate(deliveryService.calcTotalWeightG(sourceOrder),
				DateTimeUtils.sysDate(),
				sourceOrder.getAmounts().getTotal(),
				136,
				sourceOrder.getDelivery().getAddress().getCarrierInfo().getCityId(),
				true,
				false);
		assertThat(new BigDecimal("390"), Matchers.comparesEqualTo(calculatedData.getDeliverySellerSummary()));
	}
}
