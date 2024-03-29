package ru.sir.richard.boss.utils.sender;

import java.util.ArrayList;
import java.util.List;

import org.springframework.core.env.PropertyResolver;

import ru.sir.richard.boss.model.data.Order;
import ru.sir.richard.boss.model.types.OrderAdvertTypes;
import ru.sir.richard.boss.model.types.OrderEmailStatuses;
import ru.sir.richard.boss.utils.sender.email.EmailSender;

public class MessageManager {
	
	private List<AnySender> senders = new ArrayList<AnySender>();
	
	public MessageManager(PropertyResolver environment) {
		//senders.add(new SmsSender(environment));
		senders.add(new EmailSender(environment));
	}
	
	public MessageSendingStatus sendOrderMessage(Order order, boolean isSendMessage) {
		MessageSendingStatus result = MessageSendingStatus.createEmpty();
		if (!isSendMessage) {
			return result;
		}		
		if (isNotNecessarySendMessage(order)) {
			return result;
		}
		for (AnySender sender : senders) {
			SendingResponseStatus sendingResult = sender.sendOrder(order);
			result.addSendingResult(sendingResult);
		}
		return result;
		
	}
	
	public MessageSendingStatus sendOrderFeedback(Order order, OrderEmailStatuses orderEmailStatuses) {
		MessageSendingStatus result = MessageSendingStatus.createEmpty();
		
		if (isNotNecessarySendMessage(order)) {
			return result;
		}
		for (AnySender sender : senders) {
			SendingResponseStatus sendingResult = sender.sendFeedback(order, orderEmailStatuses);
			result.addSendingResult(sendingResult);
		}
		return result;		
	}
	
	public MessageSendingStatus sendOrderManualMessage(Order order, String textMessage, boolean isSendMessage) {
		MessageSendingStatus result = MessageSendingStatus.createEmpty();
		if (!isSendMessage) {
			return result;
		}	
		if (isNotNecessarySendMessage(order)) {
			return result;
		}		
		for (AnySender sender : senders) {
			SendingResponseStatus sendingResult = sender.sendManualOrder(order, textMessage);
			result.addSendingResult(sendingResult);
		}
		return result;
		
	}
	
	private boolean isNotNecessarySendMessage(Order order) {		
		if (order.getAdvertType() == OrderAdvertTypes.YANDEX_MARKET) {
			return true;
		} else if (order.getAdvertType() == OrderAdvertTypes.OZON) {
			return true;
		}
		return false;
	}
	
	

}
