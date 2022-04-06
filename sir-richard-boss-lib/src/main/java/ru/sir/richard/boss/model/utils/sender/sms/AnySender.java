package ru.sir.richard.boss.model.utils.sender.sms;

import ru.sir.richard.boss.model.data.Order;
import ru.sir.richard.boss.model.utils.sender.SendingResponseStatus;


public interface AnySender {
	
	public SendingResponseStatus send(Order order);

}
