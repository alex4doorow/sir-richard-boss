package ru.sir.richard.boss.utils.sender.sms;

import org.apache.commons.lang3.StringUtils;

import ru.sir.richard.boss.model.data.ForeignerCustomer;
import ru.sir.richard.boss.model.data.Order;
import ru.sir.richard.boss.model.types.DeliveryTypes;
import ru.sir.richard.boss.model.types.OrderEmailStatuses;
import ru.sir.richard.boss.model.types.OrderStatuses;

import ru.sir.richard.boss.model.types.PaymentTypes;
import ru.sir.richard.boss.utils.DateTimeUtils;
import ru.sir.richard.boss.utils.NumberUtils;
import ru.sir.richard.boss.utils.sender.Message;
import ru.sir.richard.boss.utils.sender.SenderTextGenerator;

public class SmsSenderTextGenerator extends SenderTextGenerator {

	public SmsSenderTextGenerator() {
		super();
	}
	
	@Override
	public Message createFeedbackMessage(Order order, OrderEmailStatuses orderEmailStatus) {
		return new Message();
	}	
	
	@Override
	public Message createOrderMessage(Order order) {
		Message message = new Message();
		if (StringUtils.isEmpty(order.getDelivery().getTrackCode())) {
			return message;
		}		
		String messageSubject = "Ваш заказ № " + order.getNo() + " от " + DateTimeUtils.defaultFormatDate(order.getOrderDate()) + " г. " + order.getStatus().getAnnotation();
		String messageBody = "";
		String messageFooter = "\r\n" +
				"Это сообщение сформировано автоматически. Отвечать на него не нужно.\r\n" +
				"-- \r\n" + 
				"С уважением, интернет-компания \"" + order.getStore().getAnnotation() + "\"\r\n" + 
				"https://" + order.getStore().getSite() + "\r\n" + 
				"+7 (499) 490-59-43\r\n" + 
				"+7 (916) 596-90-59\r\n" + 
				order.getStore().getEmail();
		if (order.getCustomer().isPerson() && order.getPaymentType() == PaymentTypes.POSTPAY) {
			// частное лицо и оплата при получении
			ForeignerCustomer customer = (ForeignerCustomer) order.getCustomer();					
			String amountText = NumberUtils.formatNumber(order.getAmounts().getTotalWithDelivery(), "###.##");
			if (order.getStatus().equals(OrderStatuses.DELIVERING) && (order.getDelivery().getDeliveryType().isCdek()) && StringUtils.isNotEmpty(order.getDelivery().getTrackCode())) {
				// статус "доставляется", указан трэккод и это сдэк
				messageBody = customer.getFirstName() + "! Заказ интернет-магазина " + order.getStore().getSite() + " " + order.getDelivery().getTrackCode() + " (" + order.getNo() + ") отправлен. Сумма к оплате " + amountText + " руб. Отследить заказ можно здесь https://www.cdek.ru/track.html?order_id=" + order.getDelivery().getTrackCode();
			} else if (order.getStatus().equals(OrderStatuses.DELIVERING) && order.getDelivery().getDeliveryType().isPost() && StringUtils.isNotEmpty(order.getDelivery().getTrackCode())) {
				// статус "доставляется", указан трэккод и это сдэк
				messageBody = customer.getFirstName() + "! Заказ интернет-магазина " + order.getStore().getSite() + " " + order.getDelivery().getTrackCode() + " (" + order.getNo() + ") отправлен. Сумма наложенного платежа " + amountText + " руб. Отследить заказ можно здесь www.pochta.ru.";	
			} else if (order.getStatus().equals(OrderStatuses.READY_GIVE_AWAY) && (order.getDelivery().getDeliveryType() == DeliveryTypes.PICKUP) && StringUtils.isNotEmpty(order.getDelivery().getTrackCode())) {
				// статус "прибыл", указан трэккод и это самовывоз
				//messageBody = customer.getFirstName() + "! Заказ интернет-магазина " + order.getStore().getSite() + " " + order.getDelivery().getTrackCode() + " (" + order.getNo() + ") готов к выдаче для самовывоза г.Москва, Щелковское Шоссе д.29, СДЭК. Сумма к оплате " + amountText +" руб. Отследить заказ можно здесь https://www.cdek.ru/track.html?order_id=" + order.getDelivery().getTrackCode();
			} else if (order.getStatus().equals(OrderStatuses.READY_GIVE_AWAY) && (order.getDelivery().getDeliveryType() == DeliveryTypes.CDEK_COURIER || order.getDelivery().getDeliveryType() == DeliveryTypes.CDEK_COURIER_ECONOMY) && StringUtils.isNotEmpty(order.getDelivery().getTrackCode())) {
				// статус "прибыл", указан трэккод и это курьер сдэка
				//messageBody = customer.getFirstName() + "! Заказ интернет-магазина " + order.getStore().getSite() + " " + order.getDelivery().getTrackCode() + " (" + order.getNo() + ") прибыл в место назначения. Курьер доставит по адресу " + order.getDelivery().getAddress().getAddress() + ". Сумма к оплате " + amountText + " руб. Отследить заказ можно здесь https://www.cdek.ru/track.html?order_id=" + order.getDelivery().getTrackCode();
			} else if (order.getStatus().equals(OrderStatuses.READY_GIVE_AWAY) && order.getDelivery().getDeliveryType().isCdek() && StringUtils.isNotEmpty(order.getDelivery().getTrackCode())) {
				// статус "прибыл", указан трэккод и это сдэк пвз
				//messageBody = customer.getFirstName() + "! Заказ интернет-магазина " + order.getStore().getSite() + " " + order.getDelivery().getTrackCode() + " (" + order.getNo() + ") прибыл на пункт выдачи " + order.getDelivery().getAddress().getAddress() + ". Сумма к оплате "+ amountText +" руб. Отследить заказ можно здесь https://www.cdek.ru/track.html?order_id=" + order.getDelivery().getTrackCode();
			} else if (order.getStatus().equals(OrderStatuses.READY_GIVE_AWAY) && order.getDelivery().getDeliveryType().isPost() && StringUtils.isNotEmpty(order.getDelivery().getTrackCode())) {
				// статус "прибыл", указан трэккод и это почта
				//messageBody = customer.getFirstName() + "! Заказ интернет-магазина " + order.getStore().getSite() + " " + order.getDelivery().getTrackCode() + " (" + order.getNo() + ") прибыл в почтовое отделение " + order.getDelivery().getAddress().getPostCode() + ". Сумма наложенного платежа "+ amountText +" руб. Отследить заказ можно здесь www.pochta.ru.";
			} else if (order.getStatus().equals(OrderStatuses.READY_GIVE_AWAY_TROUBLE) && (order.getDelivery().getDeliveryType() == DeliveryTypes.PICKUP) && StringUtils.isNotEmpty(order.getDelivery().getTrackCode())) {
				// статус "давно не забирают", указан трэккод и это самовывоз
				messageBody = customer.getFirstName() + "! Срок хранения заказа интернет-магазина " + order.getStore().getSite() + " " + order.getDelivery().getTrackCode() + " (" + order.getNo() + ") заканчивается. Убедительная просьба оперативно получить заказ. После истечения срока хранения будет осуществлен возврат. Заказ готов к выдаче для самовывоза г.Москва, Щелковское Шоссе д.29, СДЭК. Сумма к оплате " + amountText +" руб. Отследить заказ можно здесь https://www.cdek.ru/track.html?order_id=" + order.getDelivery().getTrackCode();
			} else if (order.getStatus().equals(OrderStatuses.READY_GIVE_AWAY_TROUBLE) && order.getDelivery().getDeliveryType().isCdek() && StringUtils.isNotEmpty(order.getDelivery().getTrackCode())) {
				// статус "давно не забирают", указан трэккод и это сдэк пвз
				messageBody = customer.getFirstName() + "! Срок хранения заказа интернет-магазина " + order.getStore().getSite() + " " + order.getDelivery().getTrackCode() + " (" + order.getNo() + ") заканчивается. Убедительная просьба оперативно получить заказ. После истечения срока хранения, посылка будет отправлена обратно. Сумма к оплате " + amountText + " руб. Отследить заказ можно здесь https://www.cdek.ru/track.html?order_id=" + order.getDelivery().getTrackCode();
			} else if (order.getStatus().equals(OrderStatuses.READY_GIVE_AWAY_TROUBLE) && order.getDelivery().getDeliveryType().isPost() && StringUtils.isNotEmpty(order.getDelivery().getTrackCode())) {
				// статус "давно не забирают", указан трэккод и это почта
				messageBody = customer.getFirstName() + "! Срок хранения заказа интернет-магазина " + order.getStore().getSite() + " " + order.getDelivery().getTrackCode() + " (" + order.getNo() + ") заканчивается. Убедительная просьба оперативно получить заказ. После истечения срока хранения, посылка будет отправлена обратно. Сумма к оплате " + amountText + " руб. Отследить заказ можно здесь www.pochta.ru.";				
			} 
		} else if (order.getCustomer().isPerson() && order.isPrepayment()) {
			ForeignerCustomer customer = (ForeignerCustomer) order.getCustomer();		
			if (order.getStatus().equals(OrderStatuses.DELIVERING) && order.getDelivery().getDeliveryType().isCdek() && StringUtils.isNotEmpty(order.getDelivery().getTrackCode())) {
				messageBody = customer.getFirstName() + "! Заказ интернет-магазина " + order.getStore().getSite() + " " + order.getDelivery().getTrackCode() + " (" + order.getNo() + ") отправлен. Отследить заказ можно здесь www.cdek.ru.";				
			} else if (order.getStatus().equals(OrderStatuses.DELIVERING) && order.getDelivery().getDeliveryType().isPost() && StringUtils.isNotEmpty(order.getDelivery().getTrackCode())) {
				// статус "доставляется", указан трэккод и это почта
				messageBody = customer.getFirstName() + "! Заказ интернет-магазина " + order.getStore().getSite() + " " + order.getDelivery().getTrackCode() + " (" + order.getNo() + ") отправлен. Отследить заказ можно здесь www.pochta.ru.";	
			}
		}
		//smsText = TextUtils.transliterate(smsText);
		message.setSubject(messageSubject);
		message.setBody(messageBody);
		message.setFooter(messageFooter);
		return message;
	}
	
	@Override
	public String createBillExpiredStatusMessage(Order order) {
		return "";
	}

	@Override
	public Message createManualOrderMessage(Order order, String textMessage) {		
		return null;
	}
	
}
