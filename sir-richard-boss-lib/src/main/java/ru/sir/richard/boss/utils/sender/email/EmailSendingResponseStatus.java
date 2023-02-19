package ru.sir.richard.boss.utils.sender.email;

import ru.sir.richard.boss.utils.sender.SendingResponseStatus;

public class EmailSendingResponseStatus extends SendingResponseStatus {

	public EmailSendingResponseStatus(boolean sent, boolean success, String recipient, String messageText,
			String statusMessage) {
		super(sent, success, recipient, messageText, statusMessage);
	}
	
	@Override
	protected String getSenderName() {
		return "email";
	}
	
	public static SendingResponseStatus createEmpty() {		
		return new EmailSendingResponseStatus(false, false, "", "", "");
	}

}
