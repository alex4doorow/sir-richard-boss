package ru.sir.richard.boss.model.utils.sender.sms;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.PropertyResolver;

import ru.sir.richard.boss.api.epochta.v2.SmsApi;
import ru.sir.richard.boss.api.epochta.v2.SmsPhone;
import ru.sir.richard.boss.model.data.Order;
import ru.sir.richard.boss.model.types.OrderEmailStatuses;
import ru.sir.richard.boss.model.utils.TextUtils;
import ru.sir.richard.boss.model.utils.sender.AnySender;
import ru.sir.richard.boss.model.utils.sender.SenderTextGenerator;
import ru.sir.richard.boss.model.utils.sender.SendingResponseStatus;

public class SmsSender implements AnySender {
	
	private final Logger logger = LoggerFactory.getLogger(SmsSender.class);

	private final PropertyResolver environment;
	private final SenderTextGenerator textGenerator;
	
	public SmsSender(PropertyResolver environment) {
		super();
		this.textGenerator = new SmsSenderTextGenerator();
		this.environment = environment;
	}
	
	@Override
	public SendingResponseStatus sendOrder(Order order) {
		
		if (StringUtils.isEmpty(order.getDelivery().getTrackCode())) {			
			return SmsSendingResponseStatus.createEmpty();
		}				
		String smsText = textGenerator.createOrderMessage(order).getBody();
		if (StringUtils.isEmpty(smsText)) {
			return SmsSendingResponseStatus.createEmpty();
		}		
		if (StringUtils.isEmpty(order.getCustomer().getViewPhoneNumber())) {
			return SmsSendingResponseStatus.createEmpty();
		}		
		SmsApi apiSms = new SmsApi(environment);
	    String phone = "7" + TextUtils.phoneNumberDigit(order.getCustomer().getViewPhoneNumber());
	    
	    // TODO
	    //phone = "79161699099";
	    
	    logger.debug("sms text:{}", smsText);
		logger.debug("sms recepient:{}", phone);			    
	    List<SmsPhone> phones = new ArrayList<SmsPhone>();	    
		phones.add(new SmsPhone("id1", "", phone));
		String resultStatus = apiSms.sendSms("sir richard", smsText, phones);		
		SendingResponseStatus result;
		if (StringUtils.indexOf(resultStatus, "<RESPONSE><status>1</status>") >= 0) {
			// ?????? ?????????????? ????????????????????
			result = new SmsSendingResponseStatus(true, true, phone, smsText, resultStatus);
		} else {
			logger.error("sendOrder.resultStatus():{}", resultStatus);	
			if (StringUtils.indexOf(resultStatus, "<RESPONSE><status>-1</status>") >= 0) {
				logger.error("sendOrder.resultStatus():{}", "AUTH_FAILED -1 ???????????????????????? ?????????? ??/?????? ????????????");				
			} else if (StringUtils.indexOf(resultStatus, "<RESPONSE><status>-2</status>") >= 0) {
				logger.error("sendOrder.resultStatus():{}", "XML_ERROR -2 ???????????????????????? ????????????");			
			} else if (StringUtils.indexOf(resultStatus, "<RESPONSE><status>-3</status>") >= 0) {
				logger.error("sendOrder.resultStatus():{}", "NOT_ENOUGH_CREDITS -3 ???????????????????????? ?????????? ???? ????????????????");			
			} else if (StringUtils.indexOf(resultStatus, "<RESPONSE><status>-4</status>") >= 0) {
				logger.error("sendOrder.resultStatus():{}", "NO_RECIPIENTS -4 ?????? ???????????? ?????????????? ??????????????????????");			
			} else if (StringUtils.indexOf(resultStatus, "<RESPONSE><status>-5</status>") >= 0) {
				logger.error("sendOrder.resultStatus():{}", "INVALID_TEXT -5 ???????????????? ??????????");
			} else if (StringUtils.indexOf(resultStatus, "<RESPONSE><status>-7</status>") >= 0) {
				logger.error("sendOrder.resultStatus():{}", "BAD_SENDER_NAME -7 ???????????? ?? ?????????? ??????????????????????");
			} else {
				logger.error("sendOrder.resultStatus():{}", "UNKNOWN STATUS");
			}
			result = new SmsSendingResponseStatus(true, false, phone, smsText, resultStatus);
		}
		return result;		
	}
	
	@Override
	public SendingResponseStatus sendFeedback(Order order, OrderEmailStatuses orderEmailStatus) {
		return SmsSendingResponseStatus.createEmpty();
	}

	@Override
	public SendingResponseStatus sendManualOrder(Order order, String textMessage) {
		return SmsSendingResponseStatus.createEmpty();
	}
}
