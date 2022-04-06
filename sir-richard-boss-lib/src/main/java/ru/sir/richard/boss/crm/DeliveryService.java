package ru.sir.richard.boss.crm;

import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import ru.sir.richard.boss.model.data.Address;
import ru.sir.richard.boss.model.data.Order;
import ru.sir.richard.boss.model.data.crm.DeliveryServiceResult;
import ru.sir.richard.boss.model.types.CrmTypes;
import ru.sir.richard.boss.model.types.DeliveryTypes;

public interface DeliveryService {
	
	String addCdekParcelOrder(Order order);
	String addOzonRocketParcelOrder(Order order);	
	
	DeliveryServiceResult calc(Order order, BigDecimal totalAmount, DeliveryTypes deliveryType, Address to);
	List<Address> getCdekCities(String city);
	List<Address> getCdekPvzs(int cityId);
	List<Address> getCdekPvz(int cityId, String pvzCode);
	
	String ordersStatusesReload();
	//String ordersSendFeedback(Date dateStart);
	
	String scheduledOrdersStatusesReload();
	
	void exportParcelOrdersToExcel(int orderId, OutputStream outStream, Date executorDate, CrmTypes crmType);

}
