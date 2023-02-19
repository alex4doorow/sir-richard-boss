package ru.sir.richard.boss.utils.sender;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

public class MessageSendingStatus {
	
	final private List<SendingResponseStatus> sendingResponceStatuses = new ArrayList<SendingResponseStatus>();
	
	public MessageSendingStatus() {
		
	}	
	
	public void addSendingResult(SendingResponseStatus sendingResponseStatus) {
		sendingResponceStatuses.add(sendingResponseStatus);		
	}
	
	public String getViewStatus() {
		// sms: отправлено, статус: успешно email: не отправлено
		// sms: отправлено, статус: успешно; email: не отправлено |
		
		String result = "";
		for (SendingResponseStatus sendingResponseStatus : sendingResponceStatuses) {
			result += " " + sendingResponseStatus.getViewStatus() + "; ";
		}
		if (StringUtils.isEmpty(result) || result.trim().equals(";  ;")) {
			return "не отправлялось";
		}
		result = result.substring(0, result.length() - 2).trim();
		return result;		
	} 
	
	public static MessageSendingStatus createEmpty() {		
		return new MessageSendingStatus();
	}
}
