package ru.sir.richard.boss.utils.sender.email;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.PropertyResolver;

import ru.sir.richard.boss.model.data.Order;
import ru.sir.richard.boss.model.types.OrderEmailStatuses;
import ru.sir.richard.boss.utils.sender.AnySender;
import ru.sir.richard.boss.utils.sender.Message;
import ru.sir.richard.boss.utils.sender.SenderTextGenerator;
import ru.sir.richard.boss.utils.sender.SendingResponseStatus;

public class EmailSender implements AnySender {
	
	private final Logger logger = LoggerFactory.getLogger(EmailSender.class);
	
	private final PropertyResolver environment;
	private final SenderTextGenerator textGenerator;
	
	public EmailSender(PropertyResolver environment) {
		super();
		this.textGenerator = new EmailSenderTextGenerator();
		this.environment = environment;
	}

	@Override
	public SendingResponseStatus sendOrder(Order order) {

		Message message = textGenerator.createOrderMessage(order);
		return sendOrderMessage(order, message);		
	}
	
	@Override
	public SendingResponseStatus sendManualOrder(Order order, String textMessage) {	
		
		Message message = textGenerator.createManualOrderMessage(order, textMessage);
		return sendOrderMessage(order, message);	
		
	}
	
	@Override
	public SendingResponseStatus sendFeedback(Order order, OrderEmailStatuses orderEmailStatuses) {
		Message message = textGenerator.createFeedbackMessage(order, orderEmailStatuses);
		return sendOrderMessage(order, message);
		
	}
	
	private SendingResponseStatus sendOrderMessage(Order order, Message message) {
		String emailText = message.getBody();
		if (StringUtils.isEmpty(emailText)) {
			return EmailSendingResponseStatus.createEmpty();
		}	
		
		String toEmail = order.getCustomer().getEmail();
		if (StringUtils.isEmpty(toEmail)) {
			return EmailSendingResponseStatus.createEmpty();
		}
		
		String subject = message.getSubject();		
		logger.debug("email text:{}", emailText);
		logger.debug("email recepient:{}", toEmail);
		SendingResponseStatus result;
		boolean emailResult = EmailUtils.sendEmail(environment, order.getStore(), toEmail, subject, emailText + message.getFooter());
		if (emailResult) {
			// email успешно отправлен
			result = new EmailSendingResponseStatus(true, true, toEmail, emailText, "success");
		} else {
			result = new EmailSendingResponseStatus(true, false, toEmail, emailText, "fail");
		}
		return result;
	}
}
