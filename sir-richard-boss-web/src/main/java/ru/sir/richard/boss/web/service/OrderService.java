package ru.sir.richard.boss.web.service;

import java.util.Date;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.sir.richard.boss.crm.CrmManager;
import ru.sir.richard.boss.dao.CustomerDao;
import ru.sir.richard.boss.dao.OrderDao;
import ru.sir.richard.boss.model.calc.AnyOrderTotalAmountsCalculator;
import ru.sir.richard.boss.model.data.AnyCustomer;
import ru.sir.richard.boss.model.data.Order;
import ru.sir.richard.boss.model.data.OrderAmounts;
import ru.sir.richard.boss.model.data.conditions.CustomerConditions;
import ru.sir.richard.boss.model.factories.OrderTotalAmountsCalculatorFactory;
import ru.sir.richard.boss.model.types.OrderEmailStatuses;
import ru.sir.richard.boss.model.utils.DateTimeUtils;
import ru.sir.richard.boss.model.utils.sender.MessageManager;
import ru.sir.richard.boss.model.utils.sender.email.EmailSenderTextGenerator;
import ru.sir.richard.boss.web.data.FormCustomer;

@Service
@Slf4j
public class OrderService {

	@Autowired
	private Environment environment;
		
	@Autowired
	private OrderDao orderDao;
	
	@Autowired
	private CrmManager crmManager;
		
	@Autowired
	private CustomerDao customerDao;
	
	public OrderDao getOrderDao() {
		return orderDao;
	}
	
	public CustomerDao getCustomerDao() {
		return customerDao;
	}
	
	public CrmManager getCrmManager() {
		return crmManager;
	}

	@Transactional
	public int saveOrUpdate(Order order) {
		log.debug("saveOrUpdate():{}", order);
		int orderId;
		if (order.isNew()) {			
			orderId = orderDao.addOrder(order);
		} else {
			calcTotalAmounts(order);			
			orderDao.updateOrder(order);
			orderId = order.getId();
		}	
		return orderId;
	}
		
	public OrderAmounts calcTotalAmounts(Order order) {

		AnyOrderTotalAmountsCalculator calculator = OrderTotalAmountsCalculatorFactory.createCalculator(order);
		return calculator.calc();
	}
	
	public FormCustomer customerFindByConditions(CustomerConditions customerConditions) {
		AnyCustomer customer = customerDao.findByConditions(customerConditions);	
		if (customer == null) {
			return null;
		} else {
			return FormCustomer.createForm(customer);
		}
	}
		
	public String ordersSendFeedback(Date dateStart) {
		List<Order> ordersForFeedback = orderDao.listOrdersForFeedback(dateStart);
		
		log.debug("");
		String debugInfoItem = "";
		String debugInfo = "";
		int deliveredCount = 0;

		MessageManager messageManager = new MessageManager(environment);
		for (Order orderForFeedback : ordersForFeedback) {
						
			Order currentOrder = orderDao.findById(orderForFeedback.getId());
			
			messageManager.sendOrderFeedback(currentOrder, OrderEmailStatuses.FEEDBACK);			
			orderDao.changeEmailStatusOrder(orderForFeedback.getId(), OrderEmailStatuses.FEEDBACK);
											
			debugInfoItem = "- запрос на отзыв: " + currentOrder.getNo() + ", " + currentOrder.getCustomer().getViewShortName() + ", " + currentOrder.getDelivery().getAddress().getAddress();
			debugInfo = debugInfo + debugInfoItem + "<br>";
			log.debug("запрос на отзыв: {}, {}, {}", currentOrder.getNo(), currentOrder.getCustomer().getViewShortName(), currentOrder.getDelivery().getAddress().getAddress());
			deliveredCount++;
		}
		
		int changedCount = deliveredCount;
		String result = String.format("Всего отправлено запросов на отзыв: %d", changedCount);
		if (changedCount > 0) {
			result = result + ":<br>" + debugInfo;
		}		
		return result;
	}
	
	public String bidExpiredSendMessages() {
		Date dateStart = new Date();
		
		List<Order> expiredBids = orderDao.listBidExpired(dateStart);
		
		log.debug("");
		String debugInfoItem = "";
		String debugInfo = "";
		int expiredCount = 0;
		for (Order bid : expiredBids) {
			
			Order currentOrder = orderDao.findById(bid.getId());
			orderDao.changeBillExpiredStatusOrder(currentOrder);
			
			EmailSenderTextGenerator textGenerator = new EmailSenderTextGenerator();
			String textMessage = textGenerator.createBillExpiredStatusMessage(currentOrder);
			if (StringUtils.isEmpty(textMessage)) {
				continue;
			}
			
			MessageManager messageManager = new MessageManager(environment);		
			messageManager.sendOrderManualMessage(currentOrder, textMessage, true);
								
			debugInfoItem = "- актуальность, " + currentOrder.getOrderType().getAnnotation()+ ": " + currentOrder.getNo() + ", " + currentOrder.getCustomer().getViewShortName() + ", срок: " + DateTimeUtils.defaultFormatDate(currentOrder.getOffer().getExpiredDate());
			debugInfo = debugInfo + debugInfoItem + "<br>";
			log.debug("актуальность данных: {}, {}, {}", currentOrder.getNo(), currentOrder.getCustomer().getViewShortName(), DateTimeUtils.defaultFormatDate(currentOrder.getOffer().getExpiredDate()));
			expiredCount++;
		}
		
		int changedCount = expiredCount;
		String result = String.format("Всего отправлено запросов на актуализацию: %d", changedCount);
		if (changedCount > 0) {
			result = result + ":<br>" + debugInfo;
		}	
		return result;
	}
	
}
