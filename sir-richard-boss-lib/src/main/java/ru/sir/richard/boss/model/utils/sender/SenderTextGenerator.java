package ru.sir.richard.boss.model.utils.sender;

import ru.sir.richard.boss.model.data.Order;
import ru.sir.richard.boss.model.types.OrderEmailStatuses;

public abstract class SenderTextGenerator {	
	
	public abstract String createBillExpiredStatusMessage(Order order);
	
	public abstract Message createManualOrderMessage(Order order, String textMessage);	
	public abstract Message createFeedbackMessage(Order order, OrderEmailStatuses orderEmailStatuses);
	public abstract Message createOrderMessage(Order order);
	
	 
}
