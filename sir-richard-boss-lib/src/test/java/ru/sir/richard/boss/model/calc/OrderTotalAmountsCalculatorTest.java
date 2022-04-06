package ru.sir.richard.boss.model.calc;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;

import org.junit.Test;

import ru.sir.richard.boss.model.data.Order;
import ru.sir.richard.boss.model.data.OrderAmounts;
import ru.sir.richard.boss.model.data.OrderItem;
import ru.sir.richard.boss.model.data.Product;
import ru.sir.richard.boss.model.factories.OrderTotalAmountsCalculatorFactory;
import ru.sir.richard.boss.model.types.DeliveryTypes;
import ru.sir.richard.boss.model.types.OrderStatuses;
import ru.sir.richard.boss.model.types.OrderTypes;
import ru.sir.richard.boss.model.types.PaymentDeliveryTypes;
import ru.sir.richard.boss.model.types.PaymentTypes;

public class OrderTotalAmountsCalculatorTest {
	
/*
	тесты
	расчет стоимости, дохода, прибыли, наложки
	1. физик, постоплата, самовывоз
	2. 
	   физик, постоплата, пвз, доставляется, платит продавец, скидка 3%
	   физик, постоплата, пвз, получен
	   физик, постоплата, пвз, завершен
	3. физик, наличными, курьер
*/
	
	@Test
	public void testCdekCourier() {
				
		AnyOrderTotalAmountsCalculator calculator;
		OrderAmounts amounts;
				
		Order order = new Order();
		
		// физик, постоплата, пвз, доставку платит клиент, скидка 0%
		order.setOrderType(OrderTypes.ORDER);
		order.setStatus(OrderStatuses.APPROVED);
		order.getDelivery().setDeliveryType(DeliveryTypes.CDEK_PVZ_TYPICAL);
		order.getDelivery().setPaymentDeliveryType(PaymentDeliveryTypes.CUSTOMER);	
		order.getAmounts().setPostpay(new BigDecimal("0.0"));
		
		order.getDelivery().setFactCustomerPrice(BigDecimal.valueOf(394)); // сколько берем за доставку с клиента
		order.getDelivery().setFactSellerPrice(BigDecimal.valueOf(521)); // сколько платим за услуги сдэка мы
		
		Product productOne = new Product();
		productOne.setModel("Sapsan-3");
		productOne.setName("Sapsan-3");
				
		OrderItem orderItemOne = new OrderItem();
		orderItemOne.setProduct(productOne);
		orderItemOne.setQuantity(1);
		orderItemOne.setDiscountRate(new BigDecimal("0.00"));
		orderItemOne.setPrice(BigDecimal.valueOf(4250));
		orderItemOne.setSupplierAmount(BigDecimal.valueOf(2600));
		orderItemOne.setAmount(BigDecimal.valueOf(4250));						
		order.getItems().add(orderItemOne);		
		
		calculator = OrderTotalAmountsCalculatorFactory.createCalculator(order);
		amounts = calculator.calc();				
		assertEquals(new BigDecimal("4250"), amounts.getBill()); // доход
		assertEquals(new BigDecimal("2600"), amounts.getSupplier()); // закуплено
		assertEquals(new BigDecimal("4644"), amounts.getTotalWithDelivery()); // к оплате 4644
		assertEquals(new BigDecimal("1294.55"), amounts.getMargin()); // прибыль = 1292		
		assertEquals(new BigDecimal("4123"), amounts.getPostpay()); // наложенный платеж 4121,83 
		assertEquals(new BigDecimal("394"), amounts.getDelivery()); // стоимость доставки
		
		// физик, постоплата, пвз, доставку платит продавец, скидка 0%
		order.getDelivery().setPaymentDeliveryType(PaymentDeliveryTypes.SELLER);
		order.getDelivery().setFactCustomerPrice(BigDecimal.valueOf(0)); // сколько берем за доставку с клиента
		order.getDelivery().setFactSellerPrice(BigDecimal.valueOf(521)); // сколько платим за услуги сдэка мы
		
		calculator = OrderTotalAmountsCalculatorFactory.createCalculator(order);
		amounts = calculator.calc();				
		assertEquals(new BigDecimal("4250"), amounts.getBill()); // доход
		assertEquals(new BigDecimal("2600"), amounts.getSupplier()); // закуплено
		assertEquals(new BigDecimal("4250"), amounts.getTotalWithDelivery()); // к оплате 4250
		assertEquals(new BigDecimal("959.65"), amounts.getMargin()); // прибыль = 959		
		assertEquals(new BigDecimal("3729"), amounts.getPostpay()); // наложенный платеж 3729 
		assertEquals(new BigDecimal("0"), amounts.getDelivery()); // стоимость доставки	
		
	}
	
	@Test
	public void testCdekPvz() {
				
		AnyOrderTotalAmountsCalculator calculator;
		OrderAmounts amounts;
				
		Order order = new Order();
		
		// физик, постоплата, пвз, доставку платит клиент, скидка 0%
		order.setOrderType(OrderTypes.ORDER);
		order.setStatus(OrderStatuses.APPROVED);
		order.getDelivery().setDeliveryType(DeliveryTypes.CDEK_PVZ_TYPICAL);
		order.getDelivery().setPaymentDeliveryType(PaymentDeliveryTypes.CUSTOMER);	
		order.getAmounts().setPostpay(new BigDecimal("0.0"));
		
		order.getDelivery().setFactCustomerPrice(BigDecimal.valueOf(394)); // сколько берем за доставку с клиента
		order.getDelivery().setFactSellerPrice(BigDecimal.valueOf(521)); // сколько платим за услуги сдэка мы
		
		Product productOne = new Product();
		productOne.setModel("Sapsan-3");
		productOne.setName("Sapsan-3");
				
		OrderItem orderItemOne = new OrderItem();
		orderItemOne.setProduct(productOne);
		orderItemOne.setQuantity(1);
		orderItemOne.setDiscountRate(new BigDecimal("0.00"));
		orderItemOne.setPrice(BigDecimal.valueOf(4250));
		orderItemOne.setSupplierAmount(BigDecimal.valueOf(2600));
		orderItemOne.setAmount(BigDecimal.valueOf(4250));						
		order.getItems().add(orderItemOne);		
		
		calculator = OrderTotalAmountsCalculatorFactory.createCalculator(order);
		amounts = calculator.calc();				
		assertEquals(new BigDecimal("4250"), amounts.getBill()); // доход
		assertEquals(new BigDecimal("2600"), amounts.getSupplier()); // закуплено
		assertEquals(new BigDecimal("4644"), amounts.getTotalWithDelivery()); // к оплате 4644
		assertEquals(new BigDecimal("1294.55"), amounts.getMargin()); // прибыль = 1292		
		assertEquals(new BigDecimal("4123"), amounts.getPostpay()); // наложенный платеж 4121,83 
		assertEquals(new BigDecimal("394"), amounts.getDelivery()); // стоимость доставки
		
		// физик, постоплата, пвз, доставку платит продавец, скидка 0%
		order.getDelivery().setPaymentDeliveryType(PaymentDeliveryTypes.SELLER);
		order.getDelivery().setFactCustomerPrice(BigDecimal.valueOf(0)); // сколько берем за доставку с клиента
		order.getDelivery().setFactSellerPrice(BigDecimal.valueOf(521)); // сколько платим за услуги сдэка мы
		
		calculator = OrderTotalAmountsCalculatorFactory.createCalculator(order);
		amounts = calculator.calc();				
		assertEquals(new BigDecimal("4250"), amounts.getBill()); // доход
		assertEquals(new BigDecimal("2600"), amounts.getSupplier()); // закуплено
		assertEquals(new BigDecimal("4250"), amounts.getTotalWithDelivery()); // к оплате 4250
		assertEquals(new BigDecimal("959.65"), amounts.getMargin()); // прибыль = 959		
		assertEquals(new BigDecimal("3729"), amounts.getPostpay()); // наложенный платеж 3729 
		assertEquals(new BigDecimal("0"), amounts.getDelivery()); // стоимость доставки
				
	}
	
	@Test
	public void testCdekPickup() {
				
		AnyOrderTotalAmountsCalculator calculator;
		OrderAmounts amounts;
				
		Order order = new Order();
		
		// физик, постоплата, самовывоз, доставку платит клиент, скидка 0%
		order.setOrderType(OrderTypes.ORDER);
		order.setStatus(OrderStatuses.APPROVED);
		order.getDelivery().setDeliveryType(DeliveryTypes.PICKUP);
		order.getDelivery().setPaymentDeliveryType(PaymentDeliveryTypes.CUSTOMER);	
		order.getAmounts().setPostpay(new BigDecimal("0.0"));
		
		order.getDelivery().setFactCustomerPrice(BigDecimal.valueOf(170)); // сколько берем за доставку с клиента
		order.getDelivery().setFactSellerPrice(BigDecimal.valueOf(0)); // сколько платим за услуги сдэка мы
		
		Product productOne = new Product();
		productOne.setModel("Sapsan-3");
		productOne.setName("Sapsan-3");
				
		OrderItem orderItemOne = new OrderItem();
		orderItemOne.setProduct(productOne);
		orderItemOne.setQuantity(1);
		orderItemOne.setDiscountRate(new BigDecimal("0.00"));
		orderItemOne.setPrice(BigDecimal.valueOf(4250));
		orderItemOne.setSupplierAmount(BigDecimal.valueOf(2600));
		orderItemOne.setAmount(BigDecimal.valueOf(4250));						
		order.getItems().add(orderItemOne);		
		
		calculator = OrderTotalAmountsCalculatorFactory.createCalculator(order);
		amounts = calculator.calc();				
		assertEquals(new BigDecimal("4250"), amounts.getBill()); // доход
		assertEquals(new BigDecimal("2600"), amounts.getSupplier()); // закуплено
		assertEquals(new BigDecimal("4420"), amounts.getTotalWithDelivery()); // к оплате 4644
		assertEquals(new BigDecimal("1298.29"), amounts.getMargin()); // прибыль = 1292		
		assertEquals(new BigDecimal("4127.40"), amounts.getPostpay()); // наложенный платеж 4121,83 
		assertEquals(new BigDecimal("170"), amounts.getDelivery()); // стоимость доставки
		
		// физик, постоплата, самовывоз, доставку платит продавец, скидка 0%
		order.setOrderType(OrderTypes.ORDER);
		order.setStatus(OrderStatuses.APPROVED);
		order.getDelivery().setDeliveryType(DeliveryTypes.PICKUP);
		order.getDelivery().setPaymentDeliveryType(PaymentDeliveryTypes.SELLER);	
		order.getAmounts().setPostpay(new BigDecimal("0.0"));
		
		order.getDelivery().setFactCustomerPrice(BigDecimal.valueOf(0)); // сколько берем за доставку с клиента
		order.getDelivery().setFactSellerPrice(BigDecimal.valueOf(0)); // сколько платим за услуги сдэка мы
						
		calculator = OrderTotalAmountsCalculatorFactory.createCalculator(order);
		amounts = calculator.calc();				
		assertEquals(new BigDecimal("4250"), amounts.getBill()); // доход
		assertEquals(new BigDecimal("2600"), amounts.getSupplier()); // закуплено
		assertEquals(new BigDecimal("4250"), amounts.getTotalWithDelivery()); // к оплате 4644
		assertEquals(new BigDecimal("1158.13"), amounts.getMargin()); // прибыль = 1292		
		assertEquals(new BigDecimal("3962.50"), amounts.getPostpay()); // наложенный платеж 4121,83 
		assertEquals(new BigDecimal("0"), amounts.getDelivery()); // стоимость доставки
				
	}
	
	
	@Test
	public void testPrepaymentCdekPvz() {
				
		AnyOrderTotalAmountsCalculator calculator;
		OrderAmounts amounts;				
		Order order = new Order();
		
		// юрик, предоплата, пвз, доставку платит клиент, скидка 0%, частичная постоплата = 2000.01
		order.setOrderType(OrderTypes.BILL);
		order.setStatus(OrderStatuses.APPROVED);
		order.setPaymentType(PaymentTypes.PREPAYMENT);
		
		order.getDelivery().setDeliveryType(DeliveryTypes.CDEK_PVZ_TYPICAL);
		order.getDelivery().setPaymentDeliveryType(PaymentDeliveryTypes.CUSTOMER);	
		order.getAmounts().setPostpay(new BigDecimal("2000.01"));
		
		order.getDelivery().setFactCustomerPrice(BigDecimal.valueOf(1300)); // сколько берем за доставку с клиента
		order.getDelivery().setFactSellerPrice(BigDecimal.valueOf(1300)); // сколько платим за услуги сдэка мы
		
		Product productOne = new Product();
		productOne.setModel("Sapsan-3");
		productOne.setName("Sapsan-3");
				
		OrderItem orderItemOne = new OrderItem();
		orderItemOne.setProduct(productOne);
		orderItemOne.setQuantity(1);
		orderItemOne.setDiscountRate(new BigDecimal("0.00"));
		orderItemOne.setPrice(BigDecimal.valueOf(4250));
		orderItemOne.setSupplierAmount(BigDecimal.valueOf(2600));
		orderItemOne.setAmount(BigDecimal.valueOf(4250));						
		order.getItems().add(orderItemOne);		
		
		calculator = OrderTotalAmountsCalculatorFactory.createCalculator(order);
		amounts = calculator.calc();				
		assertEquals(new BigDecimal("4250"), amounts.getBill()); // доход
		assertEquals(new BigDecimal("2600"), amounts.getSupplier()); // закуплено
		assertEquals(new BigDecimal("5550"), amounts.getTotalWithDelivery()); // к оплате 
		assertEquals(new BigDecimal("1402.50"), amounts.getMargin()); // прибыль = 1402,5		
		assertEquals(new BigDecimal("2000.01"), amounts.getPostpay()); // наложенный платеж 2000,01
		assertEquals(new BigDecimal("1300"), amounts.getDelivery()); // стоимость доставки
		
		// юрик, предоплата, пвз, доставку платит продавец, скидка 0%
		order.setOrderType(OrderTypes.BILL);
		order.setStatus(OrderStatuses.APPROVED);
		order.setPaymentType(PaymentTypes.PREPAYMENT);
		
		order.getDelivery().setDeliveryType(DeliveryTypes.CDEK_PVZ_TYPICAL);
		order.getDelivery().setPaymentDeliveryType(PaymentDeliveryTypes.SELLER);	
		order.getAmounts().setPostpay(new BigDecimal("2000.01"));
		
		order.getDelivery().setFactCustomerPrice(BigDecimal.valueOf(0)); // сколько берем за доставку с клиента
		order.getDelivery().setFactSellerPrice(BigDecimal.valueOf(250)); // сколько платим за услуги сдэка мы
				
		calculator = OrderTotalAmountsCalculatorFactory.createCalculator(order);
		amounts = calculator.calc();				
		assertEquals(new BigDecimal("4250"), amounts.getBill()); // доход
		assertEquals(new BigDecimal("2600"), amounts.getSupplier()); // закуплено
		assertEquals(new BigDecimal("4250"), amounts.getTotalWithDelivery()); // к оплате 4644
		assertEquals(new BigDecimal("1190.00"), amounts.getMargin()); // прибыль = 1190		
		assertEquals(new BigDecimal("2000.01"), amounts.getPostpay()); // наложенный платеж 2000,01 
		assertEquals(new BigDecimal("0"), amounts.getDelivery()); // стоимость доставки
	}

}
