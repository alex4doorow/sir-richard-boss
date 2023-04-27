package ru.sir.richard.boss.model.utils.sender.sms;

import ru.sir.richard.boss.model.utils.sender.SendingResponseStatus;

public class SmsSendingResponseStatus extends SendingResponseStatus {

	public SmsSendingResponseStatus(boolean sent, boolean success, String recipient, String messageText,
			String statusMessage) {
		super(sent, success, recipient, messageText, statusMessage);
	}
	
	@Override
	protected String getSenderName() {
		return "sms";
	}
	
	public static SendingResponseStatus createEmpty() {		
		return new SmsSendingResponseStatus(false, false, "", "", "");
	}

}
