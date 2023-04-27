package ru.sir.richard.boss.web.data;

import org.apache.commons.lang3.StringUtils;

import ru.sir.richard.boss.model.data.AnyCustomer;
import ru.sir.richard.boss.model.data.Contact;
import ru.sir.richard.boss.model.data.Order;
import ru.sir.richard.boss.model.data.OrderItem;
import ru.sir.richard.boss.model.types.DeliveryTypes;
import ru.sir.richard.boss.model.types.OrderAdvertTypes;
import ru.sir.richard.boss.model.types.OrderSourceTypes;
import ru.sir.richard.boss.model.types.OrderTypes;
import ru.sir.richard.boss.model.types.PaymentDeliveryTypes;
import ru.sir.richard.boss.model.types.PaymentTypes;
import ru.sir.richard.boss.model.utils.DateTimeUtils;
import ru.sir.richard.boss.model.utils.NumberUtils;

public class FormOrder extends Order {
	
	private FormCustomer formCustomer;
	private boolean sendMessage;
	private String textMessage;
	
	public FormOrder() {
		super();
		formCustomer = new FormCustomer();
		this.setOrderType(OrderTypes.ORDER);
		this.setSourceType(OrderSourceTypes.LID);
		this.setAdvertType(OrderAdvertTypes.ADVERT);		
		this.setPaymentType(PaymentTypes.POSTPAY);
		this.getDelivery().setDeliveryType(DeliveryTypes.CDEK_PVZ_TYPICAL);
		this.getDelivery().setPaymentDeliveryType(PaymentDeliveryTypes.CUSTOMER);		
		this.getDelivery().setRecipient(new Contact());		
		this.sendMessage = false;
		this.textMessage = "";
		this.getOffer().setCountDay(0);
		this.getOffer().setStartDate(DateTimeUtils.sysDate());
	}
	
	public boolean isSendMessage() {
		return sendMessage;
	}

	public void setSendMessage(boolean sendMessage) {
		this.sendMessage = sendMessage;
	}
	
	public String getTextMessage() {
		return textMessage;
	}

	public void setTextMessage(String textMessage) {
		this.textMessage = textMessage;
	}

	public boolean isCustomerEqualsRecipient() {
		return getDelivery().isCustomerEqualsRecipient();
	}
	
	public void setCustomerEqualsRecipient(boolean value) {
		if (value) {
			getDelivery().setRecipient(new Contact());	
		} else {
			//getDelivery().setRecipient(new Contact());
		}
	}
	
	public String getViewShortInfo() {
		String result = "";
		result = this.getNo() 
			+ " " + DateTimeUtils.defaultFormatDate(this.getOrderDate()) 
			+ " " + StringUtils.leftPad(NumberUtils.defaultFormatNumber(this.getAmounts().getTotalWithDelivery()), 10, ' ')
			+ " " + this.getOrderType().getAnnotation()
			+ " " + this.getStatus().getAnnotation() 
			+ " " + this.getViewItems();
		return result;
	}
	
	public String getViewItems() {
		if (this.getItems() == null || this.getItems().size() == 0) {
			return "";
		}		
		String result = "";
		for (OrderItem orderItem : this.getItems()) {
			String productName = orderItem.getProduct() == null ? "" : orderItem.getProduct().getName();
			String textItem = productName + ": " + orderItem.getQuantity() + " шт; ";
			result += textItem;
		}		
		return result.trim();		
	}

	public FormCustomer getFormCustomer() {
		return formCustomer;
	}

	public void setFormCustomer(FormCustomer formCustomer) {
		this.formCustomer = formCustomer;
	}

	public static FormOrder createForm(Order source) {
		FormOrder result = new FormOrder();		
		result.setId(source.getId());
		result.setNo(source.getNo());
		result.setSubNo(source.getSubNo());
		result.setOrderDate(source.getOrderDate());
		result.setOrderType(source.getOrderType());
		result.setProductCategory(source.getProductCategory());
		result.setSourceType(source.getSourceType());
		result.setAdvertType(source.getAdvertType());
		result.setPaymentType(source.getPaymentType());
		result.setStatus(source.getStatus());
		result.setDelivery(source.getDelivery());
		result.setStore(source.getStore());
		
		if (source.getDelivery().getRecipient() == null) {
			result.getDelivery().setRecipient(new Contact());
		} 
		result.setCustomer(source.getCustomer());		
		result.setFormCustomer(FormCustomer.createForm(source.getCustomer()));
		
		result.setStatuses(source.getStatuses());
		result.setItems(source.getItems());
		result.setAmounts(source.getAmounts());
		result.setAnnotation(source.getAnnotation());
		result.setAddedDate(source.getAddedDate());
		result.setModifiedDate(source.getModifiedDate());
		
		result.getOffer().setCountDay(source.getOffer().getCountDay());
		result.getOffer().setStartDate(source.getOffer().getStartDate());
				
		result.setComments(source.getComments());
		result.setExternalCrms(source.getExternalCrms());
		
		return result;
	}

	public void convertFormCustomer() {	
		AnyCustomer customer = this.getFormCustomer().createCustomer();
		this.setCustomer(customer);		
	}
}
