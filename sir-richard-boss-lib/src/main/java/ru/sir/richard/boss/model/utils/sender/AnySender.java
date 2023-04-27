package ru.sir.richard.boss.model.utils.sender;

import ru.sir.richard.boss.model.data.Order;
import ru.sir.richard.boss.model.types.OrderEmailStatuses;

public interface AnySender {
	
	SendingResponseStatus sendOrder(Order order);
	SendingResponseStatus sendManualOrder(Order order, String textMessage);
	
	SendingResponseStatus sendFeedback(Order order, OrderEmailStatuses orderEmailStatuses);

}
