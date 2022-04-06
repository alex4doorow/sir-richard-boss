package ru.sir.richard.boss.dao;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import ru.sir.richard.boss.model.data.Order;
import ru.sir.richard.boss.model.data.OrderItem;
import ru.sir.richard.boss.model.data.OrderStatusItem;
import ru.sir.richard.boss.model.data.conditions.ConditionResult;
import ru.sir.richard.boss.model.data.conditions.OrderConditions;
import ru.sir.richard.boss.model.types.OrderAmountTypes;
import ru.sir.richard.boss.model.types.OrderEmailStatuses;
import ru.sir.richard.boss.model.types.OrderStatuses;
import ru.sir.richard.boss.model.utils.Pair;

public interface OrderDao {
		
	List<Order> listOrders();
	List<Order> listTroubleOrders();
		
	List<Order> listOrdersByConditions(OrderConditions orderCondition);
	List<Order> listOrdersForFeedback(Date dateStart);
	List<Order> listBidExpired(Date dateStart);
	List<Order> listYeildOrders(Pair<Date> dates);
	
	ConditionResult createSQLQueryListOrdersByConditions(OrderConditions orderConditions);
	
	Map<OrderAmountTypes, BigDecimal> calcTotalOrdersAmountsByConditions(OrderConditions orderConditions);
	Map<OrderAmountTypes, BigDecimal> calcTotalOrdersAmountsByConditions(List<Order> orders, Pair<Date> period);
	
	Order findById(int orderId);
	List<OrderItem> getItemsByOrder(Order order);
	int findIdByNo(int orderNo, int orderSubNo, int orderYear);
	
	int addOrder(Order order);
	void addCrmOrderImport(int orderId, Order crmOrder);
	
	void updateOrder(Order order);	
	void deleteOrder(int orderId);	
	void eraseOrder(int orderId, boolean isDeleteCustomer);
	
	void changeFullStatusOrder(Order order);
	void changeBillExpiredStatusOrder(Order order);
	void changeEmailStatusOrder(int orderId, OrderEmailStatuses status);
	void changeStatusOrder(int orderId, OrderStatuses status, String annotation, String trackCode, OrderStatusItem newOrderStatusValue);
	
	Map<OrderStatuses, List<Order>> getDeliveryOrders(Date date);
		
	int nextOrderNo();	
	boolean checkNotUniqueOrderNo(int orderId, int orderNo, int orderYear);

}
