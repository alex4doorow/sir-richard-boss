package ru.sir.richard.boss.dao;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.text.ParseException;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.PropertySource;

import ru.sir.richard.boss.model.data.AnyCustomer;
import ru.sir.richard.boss.model.data.Customer;
import ru.sir.richard.boss.model.data.Order;
import ru.sir.richard.boss.model.data.OrderExternalCrm;
import ru.sir.richard.boss.model.data.Product;
import ru.sir.richard.boss.model.data.ProductCategory;
import ru.sir.richard.boss.model.data.conditions.OrderConditions;
import ru.sir.richard.boss.model.types.CrmStatuses;
import ru.sir.richard.boss.model.types.CrmTypes;
import ru.sir.richard.boss.model.types.CustomerTypes;
import ru.sir.richard.boss.model.types.OrderStatuses;
import ru.sir.richard.boss.model.types.StoreTypes;
import ru.sir.richard.boss.utils.DateTimeUtils;
import ru.sir.richard.boss.utils.Pair;

@SpringBootTest
@PropertySource(value="classpath:application-it.properties", encoding="UTF-8")
@Slf4j
public class OrderDaoTest {

	@Autowired
	private WikiDao wikiDao;
	
	@Autowired
	private CustomerDao customerDao;
	
	@Autowired
	private OrderDao orderDao;

	@Test
	public void testAddOrderTest() {		
		int id = orderDao.addOrderTest("");
		assertFalse(id == 0);		
	}	
	
	@Test
	public void testAddOrderCrmConnect() {
		int orderId = 10367;
		Order order = new Order();
		order.setExternalCrms(Collections.singletonList(new OrderExternalCrm(CrmTypes.OZON, CrmStatuses.SUCCESS, 5000234969L, "73940973-0045-2")));
		//orderDao.addCrmOrderImport(orderId, order);		
	}
	
	@Test
	public void testAddOrder() {
		
		ProductCategory categoryOne = wikiDao.getCategoryById(103);
		if(categoryOne == null) {			
			categoryOne = new ProductCategory(103, "Тестовая категория 103"); 
			wikiDao.getCategories().add(categoryOne);			
		} 		
		Product productOne = new Product(40, "Тестовый 40");
		productOne.setCategory(categoryOne);
		wikiDao.getProducts().add(productOne);
				
		AnyCustomer alex = new Customer();
		alex.setId(4386);		
		alex = customerDao.findById(alex.getId());
		assertTrue(alex.getId() == 4386);
				
		Order order = orderDao.findById(9776);
		assertTrue(order.getId() == 9776);
		
		int finderOrderId = orderDao.findIdByNo(order.getNo(), order.getSubNo(), 0);
		assertTrue(order.getId() == finderOrderId);
		
		order.setId(0);
		order.setOrderDate(DateTimeUtils.sysDate());
		order.setNo(orderDao.nextOrderNo());		
		assertFalse(order.getNo() <= 0);
		
		order.setSubNo(0);		
		order.setStore(StoreTypes.PM);
		
		order.setCustomer(alex);
		order.setProductCategory(categoryOne);
		
/*
		int orderId = orderDao.addOrder(order);
		assertFalse(orderId == 0);
*/
	}
	
	@Test
	public void testListOrdersByConditions() {
		OrderConditions orderConditions = new OrderConditions();
		orderConditions.setNo("12659");
		orderConditions.getCustomerConditions().setCustomerType(CustomerTypes.CUSTOMER);
		orderConditions.getCustomerConditions().setPersonLastName("Калашников");
		orderConditions.getCustomerConditions().setPersonPhoneNumber("(916) 169-90-99");
			
		List<Order> orders = orderDao.listOrdersByConditions(orderConditions);		
		orders.forEach(order -> log.debug("listOrdersByConditions: {}, {}, {}", order.getId(), order.getNo(), order.getCustomer().getViewShortName()));
	}
	
	@Test
	public void testListOrdersForFeedback() {
		
		List<Order> orders = orderDao.listOrdersForFeedback(DateTimeUtils.sysDate());
		orders.forEach(order -> log.debug("listOrdersForFeedback: {}, {}, {}", order.getId(), order.getNo(), order.getCustomer().getViewShortName()));
	}
	
	@Test
	public void testListYeildOrders() throws ParseException {
		List<Order> orders = orderDao.listYeildOrders(new Pair<Date>(DateTimeUtils.stringToDate("21.07.2022", DateTimeUtils.DATE_FORMAT_dd_MM_yyyy), 
				DateTimeUtils.stringToDate("22.07.2022", DateTimeUtils.DATE_FORMAT_dd_MM_yyyy)));
		
		orders.stream()
			.sorted((o1, o2) -> o1.getViewNo().compareTo(o2.getViewNo()))
			.forEach(order -> log.debug("listYeildOrders: {}, {}", order.getId(), order.getNo()));
			
		//orders.forEach(order -> logger.debug("listYeildOrders: {}, {}", order.getId(), order.getNo()));		
		//orders.sort((o1, o2) -> o1.getViewNo().compareTo(o2.getViewNo()));		
		
	}
	
	@Test
	public void testChangeFullStatusOrder_0() {
		
		ProductCategory categoryOne = wikiDao.getCategoryById(103);
		if (categoryOne == null) {			
			categoryOne = new ProductCategory(103, "Тестовая категория 103"); 
			wikiDao.getCategories().add(categoryOne);			
		}
		
		/*
		int orderId = 9776;
		Order order = orderDao.findById(orderId);		
		orderDao.changeFullStatusOrder(order);
		*/
	
	}
	
	@Test
	public void testChangeFullStatusOrder_1() {
		// (1 кейс) заказ оформлен вручную, у нас комплект, списываем товар комплекта
		
		/*
		T80 = 3
		T60 = 5
		
		|
		V
		
		T80 = 2
		T60 = 1		
		1+1 = 1
		1+2 = 0
		1+4 = 0
		*/
				
		
		ProductCategory categoryOne = wikiDao.getCategoryById(103);
		if (categoryOne == null) {			
			categoryOne = new ProductCategory(103, "Тестовая категория 103"); 
			wikiDao.getCategories().add(categoryOne);			
		}
		
		ProductCategory category201 = wikiDao.getCategoryById(201);
		if(category201 == null) {			
			category201 = new ProductCategory(201, "Тестовая категория 201"); 
			wikiDao.getCategories().add(category201);			
		} 	
		
		Product product985 = wikiDao.getDbProductById(985);
		product985.setCategory(category201);
		wikiDao.getProducts().add(product985);
		
		Product product986 = wikiDao.getDbProductById(986);
		product985.setCategory(category201);
		wikiDao.getProducts().add(product986);
		
		Product product1028 = wikiDao.getDbProductById(1028);
		product1028.setCategory(category201);
		wikiDao.getProducts().add(product1028);
		
		Product product1029 = wikiDao.getDbProductById(1029);
		product1029.setCategory(category201);
		wikiDao.getProducts().add(product1029);
		
		Product product1030 = wikiDao.getDbProductById(1030);
		product1030.setCategory(category201);
		wikiDao.getProducts().add(product1030);	
		
		int orderId = 9811;
		Order order = orderDao.findById(orderId);
		
		order.setId(0);
		order.setOrderDate(DateTimeUtils.sysDate());
		order.setNo(orderDao.nextOrderNo());	
		
		/*
		orderId = orderDao.addOrder(order);
		order = orderDao.findById(orderId);
		
		order.setStatus(OrderStatuses.APPROVED);
		orderDao.changeFullStatusOrder(order);
		*/		
				
	}
	
	@Test
	public void testChangeFullStatusOrder_2() {
		// (2 кейс) заказ оформлен вручную, у нас штучный товар, списываем поштучно
		
		ProductCategory categoryOne = wikiDao.getCategoryById(103);
		if (categoryOne == null) {			
			categoryOne = new ProductCategory(103, "Тестовая категория 103"); 
			wikiDao.getCategories().add(categoryOne);			
		}
		
		ProductCategory category201 = wikiDao.getCategoryById(201);
		if(category201 == null) {			
			category201 = new ProductCategory(201, "Тестовая категория 201"); 
			wikiDao.getCategories().add(category201);			
		} 	
		
		Product product985 = wikiDao.getDbProductById(985);
		product985.setCategory(category201);
		wikiDao.getProducts().add(product985);
		
		Product product986 = wikiDao.getDbProductById(986);
		product985.setCategory(category201);
		wikiDao.getProducts().add(product986);
		
		Product product1029 = wikiDao.getDbProductById(1029);
		product1029.setCategory(category201);
		wikiDao.getProducts().add(product1029);
		
		Product product1030 = wikiDao.getDbProductById(1030);
		product1030.setCategory(category201);
		wikiDao.getProducts().add(product1030);	
		
		int orderId = 9820;
		Order order = orderDao.findById(orderId);
		
		order.setId(0);
		order.setOrderDate(DateTimeUtils.sysDate());
		order.setNo(orderDao.nextOrderNo());	
		
		/*
		orderId = orderDao.addOrder(order);
		order = orderDao.findById(orderId);
		
		order.setStatus(OrderStatuses.APPROVED);
		orderDao.changeFullStatusOrder(order);	
		*/		
	}
	
	@Test
	public void testChangeFullStatusOrder_3() {
		// (3 кейс) заказ оформлен на я.маркете, у нас комплект, списываем товар комплекта		
		
		ProductCategory categoryOne = wikiDao.getCategoryById(103);
		if (categoryOne == null) {			
			categoryOne = new ProductCategory(103, "Тестовая категория 103"); 
			wikiDao.getCategories().add(categoryOne);			
		}
		
		ProductCategory category201 = wikiDao.getCategoryById(201);
		if(category201 == null) {			
			category201 = new ProductCategory(201, "Тестовая категория 201"); 
			wikiDao.getCategories().add(category201);			
		} 	
		
		Product product985 = wikiDao.getDbProductById(985);
		product985.setCategory(category201);
		wikiDao.getProducts().add(product985);
		
		Product product986 = wikiDao.getDbProductById(986);
		product985.setCategory(category201);
		wikiDao.getProducts().add(product986);
		
		Product product1029 = wikiDao.getDbProductById(1029);
		product1029.setCategory(category201);
		wikiDao.getProducts().add(product1029);
		
		Product product1030 = wikiDao.getDbProductById(1030);
		product1030.setCategory(category201);
		wikiDao.getProducts().add(product1030);		
		/*
		int orderId = 9814;
		Order order = orderDao.findById(orderId);
		order.setStatus(OrderStatuses.APPROVED);
		*/
				
	}
	
	@Test
	public void testChangeFullStatusOrder_4() {
		// (4 кейс) заказ оформлен на я.маркете, у нас штучный товар, списываем поштучно
		
		ProductCategory categoryOne = wikiDao.getCategoryById(103);
		if (categoryOne == null) {			
			categoryOne = new ProductCategory(103, "Тестовая категория 103"); 
			wikiDao.getCategories().add(categoryOne);			
		}
		
		ProductCategory category201 = wikiDao.getCategoryById(201);
		if(category201 == null) {			
			category201 = new ProductCategory(201, "Тестовая категория 201"); 
			wikiDao.getCategories().add(category201);			
		} 	
		
		Product product985 = wikiDao.getDbProductById(985);
		product985.setCategory(category201);
		wikiDao.getProducts().add(product985);
		
		Product product986 = wikiDao.getDbProductById(986);
		product985.setCategory(category201);
		wikiDao.getProducts().add(product986);
		
		Product product1029 = wikiDao.getDbProductById(1029);
		product1029.setCategory(category201);
		wikiDao.getProducts().add(product1029);
		
		Product product1030 = wikiDao.getDbProductById(1030);
		product1030.setCategory(category201);
		wikiDao.getProducts().add(product1030);		
		
		int orderId = 9822;
		Order order = orderDao.findById(orderId);		
		order.setStatus(OrderStatuses.APPROVED);
		
		//orderDao.changeFullStatusOrder(order);		
		
	}
}
