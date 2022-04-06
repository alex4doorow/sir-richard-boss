package ru.sir.richard.boss.web.service;

import java.util.Date;

import ru.sir.richard.boss.crm.CrmManager;
import ru.sir.richard.boss.dao.CustomerDao;
import ru.sir.richard.boss.dao.OrderDao;
import ru.sir.richard.boss.model.data.Order;
import ru.sir.richard.boss.model.data.OrderAmounts;
import ru.sir.richard.boss.model.data.conditions.CustomerConditions;
import ru.sir.richard.boss.web.data.FormCustomer;

public interface OrderService {
		
	OrderDao getOrderDao();
	CustomerDao getCustomerDao();
	CrmManager getCrmManager();	
	
	int saveOrUpdate(Order order);		
	OrderAmounts calcTotalAmounts(Order order);	
	FormCustomer customerFindByConditions(CustomerConditions customerConditions);
	String bidExpiredSendMessages();	
	String ordersSendFeedback(Date dateStart);
	
}
