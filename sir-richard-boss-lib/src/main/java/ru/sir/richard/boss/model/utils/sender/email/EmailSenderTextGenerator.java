package ru.sir.richard.boss.model.utils.sender.email;

import org.apache.commons.lang3.StringUtils;

import ru.sir.richard.boss.model.data.ForeignerCompanyCustomer;
import ru.sir.richard.boss.model.data.ForeignerCustomer;
import ru.sir.richard.boss.model.data.Order;
import ru.sir.richard.boss.model.data.OrderItem;
import ru.sir.richard.boss.model.types.DeliveryTypes;
import ru.sir.richard.boss.model.types.OrderEmailStatuses;
import ru.sir.richard.boss.model.types.OrderStatuses;
import ru.sir.richard.boss.model.types.OrderTypes;
import ru.sir.richard.boss.model.types.PaymentTypes;
import ru.sir.richard.boss.model.utils.DateTimeUtils;
import ru.sir.richard.boss.model.utils.NumberUtils;
import ru.sir.richard.boss.model.utils.sender.Message;
import ru.sir.richard.boss.model.utils.sender.SenderTextGenerator;

public class EmailSenderTextGenerator extends SenderTextGenerator {

	public EmailSenderTextGenerator() {
		super();
	}	
		
	@Override
	public Message createFeedbackMessage(Order order, OrderEmailStatuses orderEmailStatuses) {
		
		if (!order.getCustomer().isPerson() || StringUtils.isEmpty(order.getCustomer().getEmail()) || order.getItems().size() == 0) {
			Message message = new Message();		
			String messageSubject = "empty"; 
			String messageBody = "";
			
			message.setSubject(messageSubject);
			message.setBody(messageBody);
			message.setFooter("");
			return message;
		}
		
		ForeignerCustomer customer = (ForeignerCustomer) order.getCustomer();
		
		String messageItems = "";
		for (OrderItem orderItem : order.getItems()) {
			
			if (orderItem.getQuantity() > 1) {
				messageItems += orderItem.getProduct().getName() + ": " + orderItem.getQuantity() + " шт.";
				
			} else {
				messageItems += orderItem.getProduct().getName();
			}			
			
			break;
		}
		
		Message message = new Message();
		String messageSubject = customer.getFirstName() + ", оставьте отзыв на товары из Вашего заказа на \""+ order.getStore().getAnnotation() + "\""; 
		String messageBody = "";

		String priborMasterUrl = "***";
		priborMasterUrl = "***";
		
		
		if (orderEmailStatuses.equals(OrderEmailStatuses.FEEDBACK)) {			
			messageBody = "Добрый день, " + customer.getFirstName() + "\r\n"
					+ "\r\n"
					+ "Благодарим Вас за покупку в интернет-магазине \"" + order.getStore().getAnnotation() + "\".\r\n"
					+ "Несколько дней назад, Вы приобрели: " + messageItems + ". Спасибо за ваше доверие.\r\n"
					+ "\r\n"
					+ "Во время покупок, при выборе, легче ориентироваться на отзывы, не так ли?\r\n"
					+ "Расскажите, пожалуйста, Вы приобрели именно то, что хотели? Довольны ли Вы результатом?\r\n"
					+ "\r\n"
					+ "Оставьте отзыв. Наверняка, Вам есть, что сказать о недавней покупке. Расскажите о своих впечатлениях и помогите другим людям сделать правильный выбор.\r\n"
					+ "Черкните пару слов в ответном письме. Или перейдя на сайт, оставьте отзыв на странице с заказанным Вами товаром.\r\n"
					+ "\r\n"
					// + "А хотите получить " + bonusSumma + " рублей на свой телефон?\r\n"
					
					+ "Хотите оставить отзыв на нашу компанию в \"Яндекс Маркет\"?\r\n"
					+ "Для этого потребуется перейти на сайт \"Яндекс.Справочник\" и написать отзыв о нашей компании \"ПРИБОРМАСТЕР\":\r\n"
					+ priborMasterUrl + ".\r\n";
					//+ "\r\n"
					//+ "После публикации отзыва, пришлите нам уведомление, указав номер и оператора своего сотового телефона, - мы в течении суток, в знак благодарности, сделаем пополнение баланса, на сумму " + bonusSumma + "р.\r\n";  
		}
	
		String messageFooter = "\r\n"				
				+ "-- \r\n" 
				+ "С уважением, интернет-компания \"ПРИБОРМАСТЕР\"\r\n"
				+ "ИП Федоров Алексей Анатольевич\r\n"
				+ "ИНН 771872248140\r\n"
				+ "https://" + order.getStore().getSite() + "\r\n" 
				+ "+7 (499) 490-59-43\r\n" 
				+ "+7 (916) 596-90-59\r\n"				
				+ order.getStore().getEmail();
		
		message.setSubject(messageSubject);
		message.setBody(messageBody);
		message.setFooter(messageFooter);
		return message;
	}
		
	@Override
	public Message createOrderMessage(Order order) {
		
		Message message = new Message();		
		String messageSubject = "Ваш заказ № " + order.getViewNo() + " от " + DateTimeUtils.defaultFormatDate(order.getOrderDate()) + " г. " + order.getStatus().getAnnotation();
		String messageBody = "";
		String messageFooter = "\r\n"
				+ "Это сообщение сформировано автоматически. Отвечать на него не нужно.\r\n"
				+ "-- \r\n" 
				+ "С уважением, интернет-компания \"" + order.getStore().getAnnotation() + "\"\r\n"
				+ "ИП Федоров Алексей Анатольевич\r\n"
				+ "ИНН 771872248140\r\n"
				+ "107241, г. Москва, ул. Байкальская 18-1-82\r\n"
				+ "https://" + order.getStore().getSite() + "\r\n" 
				+ "+7 (499) 490-59-43\r\n" 
				+ "+7 (916) 596-90-59\r\n"				
				+ order.getStore().getEmail();
				
		String amountText = "";
		if (order.getCustomer().isPerson() && order.getStatus().equals(OrderStatuses.APPROVED)) {
			// информация об подтверждении заказа физлицу
			ForeignerCustomer customer = (ForeignerCustomer) order.getCustomer();
			
			// статус "подтвержден"	
			String messageItems = "";
			for (OrderItem orderItem : order.getItems()) {
				amountText = NumberUtils.formatNumber(orderItem.getAmount(), "###.##");
				messageItems += orderItem.getProduct().getName() + ": " + orderItem.getQuantity() + " шт." + " цена: " + amountText + " руб.\r\n";
			}
			messageItems += "\r\n";
			messageItems += "Адрес получения: " + order.getDelivery().getAddress().getAddress() + "\r\n";
			messageItems += "Способ доставки: " + order.getDelivery().getDeliveryType().getAnnotation() + "\r\n";
			messageItems += "\r\n";
			amountText = NumberUtils.formatNumber(order.getAmounts().getTotalWithDelivery(), "###.##");
			messageItems += "Общая стоимость заказа с учетом доставки: " + amountText + " руб.\r\n";
			if (order.isPrepayment()) {
				messageItems += "Статус оплаты: ожидаем оплату.\r\n";
			} else {
				messageItems += "Статус оплаты: оплата при получении.\r\n";
			}			
			if (order.getPaymentType() == PaymentTypes.PAYMENT_COURIER) {
				messageBody = "Добрый день, " + customer.getFirstName() + "\r\n"
						+ "\r\n"
						+ "Заказ интернет-магазина www." + order.getStore().getSite() + " № " + order.getViewNo() + " от " + DateTimeUtils.defaultFormatDate(order.getOrderDate()) + " подтвержден.\r\n"
						+ "В настоящее время, мы проверяем заказанное Вами оборудование, формируем сопроводительные документы и готовим заказ для доставки:\r\n"
						+ messageItems
						+ "\r\n"
						+ "За час до доставки, курьер с Вами свяжется по контактному номеру телефона: " + customer.getPhoneNumber() + " и предупредит о своем визите.\r\n"
						+ "Согласованное время доставки: " + DateTimeUtils.formatDate(order.getDelivery().getCourierInfo().getDeliveryDate(), "dd.MM.yyyy, EEE") + ", " + order.getDelivery().getCourierInfo().timeInterval() + "\r\n";
			} else {
				messageBody = "Добрый день, " + customer.getFirstName() + "\r\n"
						+ "\r\n"
						+ "Заказ интернет-магазина www." + order.getStore().getSite() + " № " + order.getViewNo() + " от " + DateTimeUtils.defaultFormatDate(order.getOrderDate()) + " для " + customer.getViewLongName() + " подтвержден.\r\n"
						+ "В настоящее время, мы проверяем заказанное Вами оборудование, формируем сопроводительные документы и упаковываем посылку с вашим заказом:\r\n"
						+ messageItems
						+ "\r\n"
						+ "Как только заказ будет отправлен, Вы получите уведомление об отправке на электронную почту и SMS на контактный номер телефона: " + customer.getPhoneNumber() + ".\r\n";	
			}		
	
			message.setSubject(messageSubject);
			message.setBody(messageBody);
			message.setFooter(messageFooter);
			return message;
		}  

		if (order.getCustomer().isCompany()) {	
			// юрлица или ип
			ForeignerCompanyCustomer customer = (ForeignerCompanyCustomer) order.getCustomer();			
			if (order.getStatus() == OrderStatuses.PAY_ON) {
				// статус "оплата получена"
				messageBody = "Добрый день!\r\n"
						+ "\r\n"
						+ "Оплата по счету " + order.getNo() + " для компании " + customer.getViewShortName() + " получена. Спасибо.\r\n"
						+ "Как только оборудование будет проверено, подготовлены сопроводительные документы, мы пришлем уведомление об отгрузке.\r\n";
			} else if (order.getStatus() == OrderStatuses.DELIVERING) {				
				if (order.getDelivery().getDeliveryType().isСdek() && StringUtils.isNotEmpty(order.getDelivery().getTrackCode())) {
					// статус "доставляется", указан трэккод и это сдэк
					messageBody = "Добрый день!\r\n"
							+ "\r\n"
							+ "Заказ для компании " + customer.getViewShortName() + " отправлен.\r\n"
							+ "\r\n"
							+ "Трэккод для отслеживания: " + order.getDelivery().getTrackCode() + "\r\n"
							+ "Отследить груз можно здесь: https://www.cdek.ru/track.html?order_id=" + order.getDelivery().getTrackCode() + "\r\n"
							+ "Адрес получения: " + order.getDelivery().getAddress().getAddress() + "\r\n";
				} else if (order.getDelivery().getDeliveryType().isOzonRocket() && StringUtils.isNotEmpty(order.getDelivery().getTrackCode())) {
					// статус "доставляется", указан трэккод и это ozon rocket
					messageBody = "Добрый день!\r\n"
							+ "\r\n"
							+ "Заказ для компании " + customer.getViewShortName() + " отправлен.\r\n"
							+ "\r\n"
							+ "Трэккод для отслеживания: " + order.getDelivery().getTrackCode() + "\r\n"
							+ "Отследить груз можно здесь: https://rocket.ozon.ru/tracking/?SearchId=" + order.getDelivery().getTrackCode() + "\r\n"
							+ "Адрес получения: " + order.getDelivery().getAddress().getAddress() + "\r\n";
				} else if (order.getDelivery().getDeliveryType() == DeliveryTypes.DELLIN && StringUtils.isNotEmpty(order.getDelivery().getTrackCode())) {
					// статус "прибыл", указан трэккод и это Деловые Линии
					messageBody = "Добрый день!\r\n"
							+ "\r\n"
							+ "Заказ для компании " + customer.getViewShortName() + " доставляется.\r\n"
							+ "\r\n"
							+ "Трэккод для отслеживания: " + order.getDelivery().getTrackCode() + "\r\n"
							+ "Отследить груз можно здесь: https://www.dellin.ru/tracker/?mode=search&rwID=" + order.getDelivery().getTrackCode() + "\r\n"
							+ "Адрес получения ТК \"Деловые Линии\": " + order.getDelivery().getAddress().getAddress() + "\r\n";
				}
			} else if (order.getStatus() == OrderStatuses.READY_GIVE_AWAY) {				
				if (order.getDelivery().getDeliveryType().isСdek() && StringUtils.isNotEmpty(order.getDelivery().getTrackCode())) {
					// статус "прибыл", указан трэккод и это сдэк
					messageBody = "Добрый день!\r\n"
							+ "\r\n"
							+ "Заказ для компании " + customer.getViewShortName() + " прибыл.\r\n"
							+ "\r\n"
							+ "Трэккод для отслеживания: " + order.getDelivery().getTrackCode() + "\r\n"
							+ "Отследить груз можно здесь: https://www.cdek.ru/track.html?order_id=" + order.getDelivery().getTrackCode() + "\r\n"
							+ "Адрес получения: " + order.getDelivery().getAddress().getAddress() + "\r\n";
				} else if (order.getDelivery().getDeliveryType().isOzonRocket() && StringUtils.isNotEmpty(order.getDelivery().getTrackCode())) {
					// статус "прибыл", указан трэккод и это ozon rocket
					messageBody = "Добрый день!\r\n"
							+ "\r\n"
							+ "Заказ для компании " + customer.getViewShortName() + " прибыл.\r\n"
							+ "\r\n"
							+ "Трэккод для отслеживания: " + order.getDelivery().getTrackCode() + "\r\n"
							+ "Отследить груз можно здесь: https://rocket.ozon.ru/tracking/?SearchId=" + order.getDelivery().getTrackCode() + "\r\n"
							+ "Адрес получения: " + order.getDelivery().getAddress().getAddress() + "\r\n";
				} else if (order.getDelivery().getDeliveryType() == DeliveryTypes.DELLIN && StringUtils.isNotEmpty(order.getDelivery().getTrackCode())) {
					// статус "прибыл", указан трэккод и это сдэк
					messageBody = "Добрый день!\r\n"
							+ "\r\n"
							+ "Заказ для компании " + customer.getViewShortName() + " прибыл.\r\n"
							+ "\r\n"
							+ "Трэккод для отслеживания: " + order.getDelivery().getTrackCode() + "\r\n"
							+ "Отследить груз можно здесь: https://www.dellin.ru/tracker/?mode=search&rwID=" + order.getDelivery().getTrackCode() + "\r\n"
							+ "Адрес получения Терминал ТК \"Деловые Линии\": " + order.getDelivery().getAddress().getAddress() + "\r\n";
				}
			} else if (order.getStatus() == OrderStatuses.DOC_NOT_EXIST) {
				messageBody = "Добрый день!\r\n"
						+ "\r\n"	
						+ "Пока так и не получили от вас подписанный \"ТОРГ-12\" на оборудование:"
						+ "\r\n"
						+ "ИП Федоров Алексей Анатольевич ИНН 771872248140\r\n"
						+ "Накладная № " + order.getNo() + " от "+ DateTimeUtils.defaultFormatDate(order.getOrderDate()) + "\r\n"
						+ "для ИНН " + customer.getInn() + " " + customer.getViewShortName() + "\r\n"
						+ "\r\n"
						+"Оригинал ожидаем по адресу:\r\n"
						+ "107241, г. Москва, ул.Байкальская д.18 к.1 кв.82, Федоров Алексей Анатольевич\r\n"
						+ "\r\n"
						+ "Пришлите, пожалуйста, скан ТОРГ-12 подписанный с вашей стороны.\r\n"
						+ "\r\n"
						+ "Если нужны подлинники, то повторно отправим оригиналы. Сообщите по какому адресу\r\n"
						+ "\r\n"
						+ "Ждем подписанный \"ТОРГ-12\".\r\n"
						+ "Спасибо!\r\n";
				messageFooter = "\r\n" +
						"-- \r\n" 
						+ "С уважением,\r\n"
						+ "ИП Федоров Алексей Анатольевич\r\n"
						+ "ИНН 771872248140"
						+ "107241, г. Москва, ул. Байкальская 18-1-82\r\n"
						+ "https://" + order.getStore().getSite() + "\r\n" 
						+ "+7 (499) 490-59-43\r\n"
						+ "+7 (916) 596-90-59\r\n" 
						+ order.getStore().getEmail();
			} else {
				// ....
			}
		} else if (order.getCustomer().isPerson()) {
			ForeignerCustomer customer = (ForeignerCustomer) order.getCustomer();	
			amountText = "";
			
			if (order.getStatus() == OrderStatuses.PAY_ON && order.getPaymentType() == PaymentTypes.PREPAYMENT) {
				// статус "оплата получена" по счету
				messageBody = "Добрый день, " + customer.getFirstName() + "!\r\n"
						+ "\r\n"
						+ "Оплата по счету № " + order.getNo() + " получена. Спасибо.\r\n"
						+ "Как только оборудование будет проверено, подготовлены сопроводительные документы, мы пришлем уведомление об отгрузке.\r\n";
			} else if (order.getStatus() == OrderStatuses.PAY_ON && order.getPaymentType() == PaymentTypes.YANDEX_PAY) {
				// статус "оплата получена" по карте
				messageBody = "Добрый день, " + customer.getFirstName() + "!\r\n"
						+ "\r\n"
						+ "Оплата заказа № " + order.getViewNo() + " выполнена. Спасибо.\r\n"
						+ "После отправления заказа, мы пришлем уведомление об отгрузке и трэккод для отслеживания.\r\n";
			} else if (order.getPaymentType() == PaymentTypes.POSTPAY || order.getPaymentType() == PaymentTypes.PAYMENT_COURIER) {				
				// частное лицо и оплата при получении				
				amountText = NumberUtils.formatNumber(order.getAmounts().getTotalWithDelivery(), "###.##");
				if (order.getStatus() == OrderStatuses.DELIVERING && (order.getDelivery().getDeliveryType().isСdek()) && StringUtils.isNotEmpty(order.getDelivery().getTrackCode())) {
					// статус "доставляется", указан трэккод и это сдэк
					messageBody = "Добрый день, " + customer.getFirstName() + "!\r\n"
							+ "\r\n" 
							+ "Заказ интернет-магазина www."+ order.getStore().getSite() + " " + order.getDelivery().getTrackCode() + " (" + order.getNo() + ") отправлен.\r\n"
						    + "Адрес доставки: " + order.getDelivery().getAddress().getAddress() + ".\r\n"
							+" Сумма к оплате: " + amountText + " руб.\r\n"
							+" Отследить посылку можно здесь: https://www.cdek.ru/track.html?order_id=" + order.getDelivery().getTrackCode() + "\r\n";
				} else if (order.getStatus() == OrderStatuses.DELIVERING && (order.getDelivery().getDeliveryType().isOzonRocket()) && StringUtils.isNotEmpty(order.getDelivery().getTrackCode())) {
					// статус "доставляется", указан трэккод и это ozon rocket
					messageBody = "Добрый день, " + customer.getFirstName() + "!\r\n"
							+ "\r\n" 
							+ "Заказ интернет-магазина www."+ order.getStore().getSite() + " " + order.getDelivery().getTrackCode() + " (" + order.getNo() + ") отправлен.\r\n"
						    + "Адрес доставки: " + order.getDelivery().getAddress().getAddress() + ".\r\n"
							+" Сумма к оплате: " + amountText + " руб.\r\n"
							+" Отследить посылку можно здесь: https://rocket.ozon.ru/tracking/?SearchId=" + order.getDelivery().getTrackCode() + "\r\n";
				} else if (order.getStatus() == OrderStatuses.DELIVERING && (order.getDelivery().getDeliveryType().isPost()) && StringUtils.isNotEmpty(order.getDelivery().getTrackCode())) {
					// статус "доставляется", указан трэккод и это сдэк
					messageBody = "Добрый день, " + customer.getFirstName() + "!\r\n"
							+ "\r\n"
							+ "Заказ интернет-магазина www." + order.getStore().getSite() + " " + order.getDelivery().getTrackCode() + " (" + order.getNo() + ") отправлен.\r\n"
							+ "Сумма наложенного платежа: " + amountText + " руб.\r\n"
							+ "Отследить посылку можно здесь: https://www.pochta.ru/tracking#" + order.getDelivery().getTrackCode() + "\r\n";	
				} else if (order.getStatus() == OrderStatuses.READY_GIVE_AWAY && (order.getDelivery().getDeliveryType() == DeliveryTypes.CDEK_COURIER || order.getDelivery().getDeliveryType() == DeliveryTypes.CDEK_COURIER_ECONOMY) && StringUtils.isNotEmpty(order.getDelivery().getTrackCode())) {
					// статус "прибыл", указан трэккод и это курьер сдэк
					messageBody = "Добрый день, " + customer.getFirstName() + "!\r\n"
							+ "\r\n"
							+ "Заказ интернет-магазина www." + order.getStore().getSite() + " " + order.getDelivery().getTrackCode() + " (" + order.getNo() + ") прибыл в место назначения.\r\n"
							+ "Курьер доставит по адресу: " + order.getDelivery().getAddress().getAddress() + ".\r\n"
							+ "Сумма к оплате: " + amountText + " руб.\r\n"
							+ "Отследить посылку можно здесь: https://www.cdek.ru/track.html?order_id=" + order.getDelivery().getTrackCode() + "\r\n";
				
				} else if (order.getStatus() == OrderStatuses.READY_GIVE_AWAY && (order.getDelivery().getDeliveryType() == DeliveryTypes.PICKUP) && StringUtils.isNotEmpty(order.getDelivery().getTrackCode())) {
					// статус "прибыл", указан трэккод и это самовывоз
					messageBody = "Добрый день, " + customer.getFirstName() + "!\r\n"
							+ "\r\n"
							+ "Заказ интернет-магазина www." + order.getStore().getSite() + " " + order.getDelivery().getTrackCode() + " (" + order.getNo() + ") готов к выдаче для самовывоза:\r\n"
							+ "г.Москва, Щелковское Шоссе д.29, СДЭК.\r\n"
							+ "Сумма к оплате " + amountText +" руб.\r\n"
							+ "Отследить посылку можно здесь: https://www.cdek.ru/track.html?order_id=" + order.getDelivery().getTrackCode() + "\r\n";
				} else if (order.getStatus() == OrderStatuses.READY_GIVE_AWAY && (order.getDelivery().getDeliveryType().isСdek()) && StringUtils.isNotEmpty(order.getDelivery().getTrackCode())) {
					// статус "прибыл", указан трэккод и это сдэк пвз
					messageBody = "Добрый день, " + customer.getFirstName() + "!\r\n"
							+ "\r\n"
							+ "Заказ интернет-магазина www." + order.getStore().getSite() + " " + order.getDelivery().getTrackCode() + " (" + order.getNo() + ") прибыл на пункт выдачи:\r\n"
							+ order.getDelivery().getAddress().getAddress() + ".\r\n"
							+ "Сумма к оплате: "+ amountText + " руб.\r\n"
							+ "Отследить посылку можно здесь: https://www.cdek.ru/track.html?order_id=" + order.getDelivery().getTrackCode() + "\r\n";
					
				} else if (order.getStatus() == OrderStatuses.READY_GIVE_AWAY && (order.getDelivery().getDeliveryType().isOzonRocket()) && StringUtils.isNotEmpty(order.getDelivery().getTrackCode())) {
					// статус "прибыл", указан трэккод и это ozon rocket
					messageBody = "Добрый день, " + customer.getFirstName() + "!\r\n"
							+ "\r\n"
							+ "Заказ интернет-магазина www." + order.getStore().getSite() + " " + order.getDelivery().getTrackCode() + " (" + order.getNo() + ") прибыл на пункт выдачи:\r\n"
							+ order.getDelivery().getAddress().getAddress() + ".\r\n"
							+ "Сумма к оплате: "+ amountText + " руб.\r\n"
							+ "Отследить посылку можно здесь: https://rocket.ozon.ru/tracking/?SearchId=" + order.getDelivery().getTrackCode() + "\r\n";
					
				} else if (order.getStatus() == OrderStatuses.READY_GIVE_AWAY && (order.getDelivery().getDeliveryType().isPost()) && StringUtils.isNotEmpty(order.getDelivery().getTrackCode())) {
					// статус "прибыл", указан трэккод и это почта
					messageBody = "Добрый день, " + customer.getFirstName() + "!\r\n"
							+ "\r\n"
							+ "Заказ интернет-магазина www." + order.getStore().getSite() + " " + order.getDelivery().getTrackCode() + " (" + order.getNo() + ") прибыл в почтовое отделение:\r\n" 
							+ order.getDelivery().getAddress().getPostCode() + ".\r\n"
							+ "Сумма наложенного платежа: "+ amountText +" руб.\r\n"
							+ "Отследить посылку можно здесь: https://www.pochta.ru/tracking#" + order.getDelivery().getTrackCode() + "\r\n";
				} else if (order.getStatus() == OrderStatuses.READY_GIVE_AWAY_TROUBLE && (order.getDelivery().getDeliveryType().isСdek()) && StringUtils.isNotEmpty(order.getDelivery().getTrackCode())) {
					// статус "долго не забирают" и это сдэк
					messageBody = "Добрый день, " + customer.getFirstName() + "\r\n"
							+ "\r\n"
							+ "Заказ интернет-магазина www." + order.getStore().getSite() + " № " + order.getNo() + " от " + DateTimeUtils.defaultFormatDate(order.getOrderDate()) + " для получателя: " + customer.getViewLongName() + " ожидает в месте вручения.\r\n"
							+ "Адрес получения: " + order.getDelivery().getAddress().getAddress() + "\r\n"
							+ "\r\n"
							+ "Срок хранения посылки истекает через несколько дней. Убедительная просьба оперативно получить заказ. После истечения срока хранения, посылка будет отправлена обратно.\r\n"
							+ "Трэккод вашей посылки: " + order.getDelivery().getTrackCode() + "\r\n"
							+ "Отследить посылку можно здесь: https://www.cdek.ru/track.html?order_id=" + order.getDelivery().getTrackCode() + "\r\n";
					
				} else if (order.getStatus() == OrderStatuses.READY_GIVE_AWAY_TROUBLE && (order.getDelivery().getDeliveryType().isOzonRocket()) && StringUtils.isNotEmpty(order.getDelivery().getTrackCode())) {
					// статус "долго не забирают" и это ozon rocket
					messageBody = "Добрый день, " + customer.getFirstName() + "\r\n"
							+ "\r\n"
							+ "Заказ интернет-магазина www." + order.getStore().getSite() + " № " + order.getNo() + " от " + DateTimeUtils.defaultFormatDate(order.getOrderDate()) + " для получателя: " + customer.getViewLongName() + " ожидает в месте вручения.\r\n"
							+ "Адрес получения: " + order.getDelivery().getAddress().getAddress() + "\r\n"
							+ "\r\n"
							+ "Срок хранения посылки истекает через несколько дней. Убедительная просьба оперативно получить заказ. После истечения срока хранения, посылка будет отправлена обратно.\r\n"
							+ "Трэккод вашей посылки: " + order.getDelivery().getTrackCode() + "\r\n"
							+ "Отследить посылку можно здесь: https://rocket.ozon.ru/tracking/?SearchId=" + order.getDelivery().getTrackCode() + "\r\n";
					
				} else if (order.getStatus() == OrderStatuses.READY_GIVE_AWAY_TROUBLE && (order.getDelivery().getDeliveryType().isPost()) && StringUtils.isNotEmpty(order.getDelivery().getTrackCode())) {
					// статус "долго не забирают" и это почта
					messageBody = "Добрый день, " + customer.getFirstName() + "\r\n"
							+ "\r\n"
							+ "Заказ интернет-магазина www." + order.getStore().getSite() + " № " + order.getNo() + " от " + DateTimeUtils.defaultFormatDate(order.getOrderDate()) + " для " + customer.getViewLongName() + " ожидает в почтовом отделении: " + order.getDelivery().getAddress().getPostCode() + ".\r\n"
							+ "Адрес получателя: " + order.getDelivery().getAddress().getAddress() + "\r\n"
							+ "\r\n"
							+ "Срок хранения посылки истекает через несколько дней. Убедительная просьба оперативно получить заказ. После истечения срока хранения, посылка будет отправлена обратно.\r\n"
							+ "Трэккод вашей посылки: " + order.getDelivery().getTrackCode() + "\r\n"
							+ "Отследить посылку можно здесь: https://www.pochta.ru/tracking#" + order.getDelivery().getTrackCode() + "\r\n";
					
				} else if (order.getStatus() == OrderStatuses.CANCELED) {
					// статус "отменен"
					messageBody = "Добрый день, " + customer.getFirstName() + "\r\n"
							+ "\r\n"
							+ "Заказ интернет-магазина www." + order.getStore().getSite() + " № " + order.getNo() + " от " + DateTimeUtils.defaultFormatDate(order.getOrderDate()) + " для " + customer.getViewLongName() + " отменен.\r\n"
							+ "В случае несогласия с причиной отмены вашего заказа, или при наличии вопросов, обратитесь, пожалуйста, к администрации сайта.\r\n"
							+ "Контактные данные указаны ниже, в подписи данного письма.\r\n"
							+ "\r\n"
							+ "Сожалеем, что не смогли вам помочь.\r\n";
					
				} else if (order.getStatus() == OrderStatuses.REDELIVERY && (order.getDelivery().getDeliveryType().isСdek()) && StringUtils.isNotEmpty(order.getDelivery().getTrackCode())) {
					// статус "отказ от вручения"
					messageBody = "Добрый день, " + customer.getFirstName() + "\r\n"
							+ "\r\n"
							+ "Заказ интернет-магазина www." + order.getStore().getSite() + " № " + order.getNo() + " от " + DateTimeUtils.defaultFormatDate(order.getOrderDate()) + " для " + customer.getViewLongName() + " отменен.\r\n"
							+ "Причина возврата вашего заказа: \"отказ от вручения, истек срок хранения\".\r\n"
							+ "Трэккод вашей посылки: " + order.getDelivery().getTrackCode() + "\r\n"
							+ "Отследить груз можно здесь: https://www.cdek.ru/track.html?order_id=" + order.getDelivery().getTrackCode() + "\r\n"
							+ "\r\n"
							+ "В случае не согласия с причиной отмены вашего заказа, или при наличии каких-либо вопросов, обратитесь, пожалуйста, к администрации сайта.\r\n"
							+ "Контактные данные указаны ниже, в подписи данного письма.\r\n"
							+ "\r\n"
							+ "Сожалеем, что не смогли вам помочь.\r\n";					
				} 
				
			} else if (order.getPaymentType() == PaymentTypes.PREPAYMENT) {
				// предоплата физика					
				if (order.getStatus() == OrderStatuses.DELIVERING && (order.getDelivery().getDeliveryType().isСdek()) && StringUtils.isNotEmpty(order.getDelivery().getTrackCode())) {					
					messageBody = "Добрый день, " + customer.getFirstName() + "!\r\n"
								+ "\r\n"
								+ "Заказ интернет-магазина www." + order.getStore().getSite() + " " + order.getDelivery().getTrackCode() + " (" + order.getNo() + ") отправлен.\r\n"
							    + "Адрес доставки: " + order.getDelivery().getAddress().getAddress() + ".\r\n"
								+ "Отследить заказ можно здесь: www.cdek.ru." + "\r\n";				
				} else if (order.getStatus() == OrderStatuses.DELIVERING && (order.getDelivery().getDeliveryType().isOzonRocket()) && StringUtils.isNotEmpty(order.getDelivery().getTrackCode())) {					
					messageBody = "Добрый день, " + customer.getFirstName() + "!\r\n"
							+ "\r\n"
							+ "Заказ интернет-магазина www." + order.getStore().getSite() + " " + order.getDelivery().getTrackCode() + " (" + order.getNo() + ") отправлен.\r\n"
						    + "Адрес доставки: " + order.getDelivery().getAddress().getAddress() + ".\r\n"
							+ "Отследить заказ можно здесь: https://rocket.ozon.ru/tracking/?SearchId=" + order.getDelivery().getTrackCode() +"\r\n";				
			    } else if (order.getStatus() == OrderStatuses.DELIVERING && (order.getDelivery().getDeliveryType().isPost()) && StringUtils.isNotEmpty(order.getDelivery().getTrackCode())) {
						// статус "доставляется", указан трэккод и это почта
					messageBody = "Добрый день, " + customer.getFirstName() + "!\r\n"
								+ "\r\n"
								+ "Заказ интернет-магазина www." + order.getStore().getSite() + " " + order.getDelivery().getTrackCode() + " (" + order.getNo() + ") отправлен.\r\n"
								+ "Отследить посылку можно здесь: https://www.pochta.ru/tracking#" + order.getDelivery().getTrackCode() + "\r\n";
				} else if (order.getStatus() == OrderStatuses.READY_GIVE_AWAY && (order.getDelivery().getDeliveryType() == DeliveryTypes.CDEK_COURIER || order.getDelivery().getDeliveryType() == DeliveryTypes.CDEK_COURIER_ECONOMY) && StringUtils.isNotEmpty(order.getDelivery().getTrackCode())) {
					// статус "прибыл", указан трэккод и это курьер сдэка
					messageBody = "Добрый день, " + customer.getFirstName() + "!\r\n"
							+ "\r\n"
							+ "Заказ интернет-магазина www." + order.getStore().getSite() + " " + order.getDelivery().getTrackCode() + " (" + order.getNo() + ") прибыл в место назначения.\r\n"
							+ "Курьер доставит по адресу: " + order.getDelivery().getAddress().getAddress() + ".\r\n"
							+ "Отследить заказ можно здесь: https://www.cdek.ru/track.html?order_id=" + order.getDelivery().getTrackCode() + "\r\n";
				
				} else if (order.getStatus() == OrderStatuses.READY_GIVE_AWAY && (order.getDelivery().getDeliveryType() == DeliveryTypes.OZON_ROCKET_COURIER) && StringUtils.isNotEmpty(order.getDelivery().getTrackCode())) {
					// статус "прибыл", указан трэккод и это курьер ozon rocket
					messageBody = "Добрый день, " + customer.getFirstName() + "!\r\n"
							+ "\r\n"
							+ "Заказ интернет-магазина www." + order.getStore().getSite() + " " + order.getDelivery().getTrackCode() + " (" + order.getNo() + ") прибыл в место назначения.\r\n"
							+ "Курьер доставит по адресу: " + order.getDelivery().getAddress().getAddress() + ".\r\n"
							+ "Отследить заказ можно здесь: https://rocket.ozon.ru/tracking/?SearchId=" + order.getDelivery().getTrackCode() + "\r\n";
				
				} else if (order.getStatus() == OrderStatuses.READY_GIVE_AWAY && (order.getDelivery().getDeliveryType() == DeliveryTypes.PICKUP) && StringUtils.isNotEmpty(order.getDelivery().getTrackCode())) {
					// статус "прибыл", указан трэккод и это самовывоз
					messageBody = "Добрый день, " + customer.getFirstName() + "!\r\n"
							+ "\r\n"
							+ "Заказ интернет-магазина www." + order.getStore().getSite() + " " + order.getDelivery().getTrackCode() + " (" + order.getNo() + ") готов к выдаче для самовывоза:\r\n"
							+ "г.Москва, Щелковское Шоссе д.29, СДЭК.\r\n"
							+ "Отследить заказ можно здесь: https://www.cdek.ru/track.html?order_id=" + order.getDelivery().getTrackCode() + "\r\n";
				} else if (order.getStatus() == OrderStatuses.READY_GIVE_AWAY && (order.getDelivery().getDeliveryType().isСdek()) && StringUtils.isNotEmpty(order.getDelivery().getTrackCode())) {
					// статус "прибыл", указан трэккод и это сдэк пвз
					messageBody = "Добрый день, " + customer.getFirstName() + "!\r\n"
							+ "\r\n"
							+ "Заказ интернет-магазина www." + order.getStore().getSite() + " " + order.getDelivery().getTrackCode() + " (" + order.getNo() + ") прибыл на пункт выдачи:\r\n"
							+ order.getDelivery().getAddress().getAddress() + ".\r\n"
							+ "Отследить заказ можно здесь: https://www.cdek.ru/track.html?order_id=" + order.getDelivery().getTrackCode() + "\r\n";
					
				} else if (order.getStatus() == OrderStatuses.READY_GIVE_AWAY && (order.getDelivery().getDeliveryType().isPost()) && StringUtils.isNotEmpty(order.getDelivery().getTrackCode())) {
					// статус "прибыл", указан трэккод и это почта
					messageBody = "Добрый день, " + customer.getFirstName() + "!\r\n"
							+ "\r\n"
							+ "Заказ интернет-магазина www." + order.getStore().getSite() + " " + order.getDelivery().getTrackCode() + " (" + order.getNo() + ") прибыл в почтовое отделение:\r\n" 
							+ order.getDelivery().getAddress().getPostCode() + ".\r\n"
							+ "Отследить посылку можно здесь: https://www.pochta.ru/tracking#" + order.getDelivery().getTrackCode() + "\r\n";
				} 				
			}
		} 		
		message.setSubject(messageSubject);
		message.setBody(messageBody);
		message.setFooter(messageFooter);
		return message;
	}
	
	@Override
	public String createBillExpiredStatusMessage(Order order) {
		String result = "";
		if (order.getCustomer().isCompany() && order.getOrderType() == OrderTypes.BILL) {
			
			/*
			Добрый день,

			Для компании ООО "РОМАШКА" был выставлен счет № 1111 от 01.01.2015 г.
			Срок действия счета: 3 дня. Он заканчился 15.03.2020 г.

			Оплата пока не поступила. Товар снимаем с резерва.
			Подтвердите, пожалуйста, актуальность вашего заказа, и сообщите, - когда планируете выполнить оплату?

			Если ваш заказ все еще актуален, то пришлите, пожалуйста, подтверждение в ответ на это письмо. И укажите планируемые сроки оплаты.
			Спасибо!
			*/
			
			ForeignerCompanyCustomer company = (ForeignerCompanyCustomer) order.getCustomer();
			String contactName;
			if (StringUtils.trimToEmpty(company.getMainContact().getFirstName()).equalsIgnoreCase("Н")) {
				contactName = "";
			} else {
				contactName = company.getMainContact().getFirstName() + " " + StringUtils.trimToEmpty(company.getMainContact().getMiddleName());
			}			
			result = "Добрый день, " + contactName + "\r\n"
					+ "\r\n"
					+ "Для компании " + order.getCustomer().getViewShortName() + " был выставлен счет № " + order.getNo() + " от " + DateTimeUtils.defaultFormatDate(order.getOrderDate()) + " г.\r\n"					
					+ "Срок действия счета: " + order.getOffer().getCountDay() + " дня. Действует до " + DateTimeUtils.defaultFormatDate(order.getOffer().getExpiredDate()) + ".\r\n"
					+ "\r\n"
					+ "Оплата пока не поступила. Товар снимаем с резерва.\r\n" 
					+ "Подтвердите, пожалуйста, актуальность вашей заявки, и сообщите, - когда планируете выполнить оплату?\r\n"
					+ "\r\n"
					+ "Если ваш заказ все еще в силе, то пришлите, пожалуйста, подтверждение в ответ на это письмо. И укажите планируемые сроки оплаты.\r\n";

		} else if (order.getCustomer().isCompany() && order.getOrderType() == OrderTypes.KP) {
/*		
			Запрос на актуальность коммерческого предложения от 01.01.2015 г.

			Добрый день,

			Для компании ООО "РОМАШКА" было отправлено коммерческое предложение от 01.01.2015 г.
			Срок действия: 30 дней. Он закончился 10.10.2020 г.

			Если ваш запрос все еще актуален, то пришлите, пожалуйста, подтверждение в ответ на это письмо. И укажите планируемые сроки закупки запрашиваемого оборудования.
*/
			
			ForeignerCompanyCustomer company = (ForeignerCompanyCustomer) order.getCustomer();	
			String contactName;
			if (StringUtils.trimToEmpty(company.getMainContact().getFirstName()).equalsIgnoreCase("Н")) {
				contactName = "";
			} else {
				contactName = company.getMainContact().getFirstName() + " " + StringUtils.trimToEmpty(company.getMainContact().getMiddleName());
			}			
			result = "Добрый день, " + contactName + "\r\n"
					+ "\r\n"
					+ "Для компании " + order.getCustomer().getViewShortName() + " было отправлено коммерческое предложение от " + DateTimeUtils.defaultFormatDate(order.getOrderDate()) + " г.\r\n"
					+ "\r\n" 
					+ "Если ваш запрос все еще актуален, то пришлите, пожалуйста, подтверждение в ответ на это письмо. И укажите планируемые сроки закупки запрашиваемого оборудования.\r\n";
		}		
		return result;
	}

	@Override
	public Message createManualOrderMessage(Order order, String textMessage) {

		Message message = new Message();		
		String messageSubject = "";
		String messageBody = textMessage;
		String messageFooter = "\r\n"
				+ "-- \r\n" 
				+ "С уважением, интернет-компания \"" + order.getStore().getAnnotation() + "\"\r\n"
				+ "ИП Федоров Алексей Анатольевич\r\n"
				+ "ИНН 771872248140\r\n"
				+ "107241, г. Москва, ул. Байкальская 18-1-82\r\n"
				+ "https://" + order.getStore().getSite() + "\r\n" 
				+ "+7 (499) 490-59-43\r\n" 
				+ "+7 (916) 596-90-59\r\n"				
				+ order.getStore().getEmail();

		if (order.getCustomer().isCompany()) {	
			// юрлица или ип
			//ForeignerCompanyCustomer customer = (ForeignerCompanyCustomer) order.getCustomer();			
			if (order.getOrderType() == OrderTypes.BILL) {
				messageSubject = "Запрос на актуальность счета № " + order.getNo() + " от " + DateTimeUtils.defaultFormatDate(order.getOrderDate()) + " г. ";
				
			} else if (order.getCustomer().isCompany() && order.getOrderType() == OrderTypes.KP) {
				messageSubject = "Запрос на актуальность коммерческого предложения от " + DateTimeUtils.defaultFormatDate(order.getOrderDate()) + " г. ";
			}
		} 		
		message.setSubject(messageSubject);
		message.setBody(messageBody);
		message.setFooter(messageFooter);
		return message;
	}
}
