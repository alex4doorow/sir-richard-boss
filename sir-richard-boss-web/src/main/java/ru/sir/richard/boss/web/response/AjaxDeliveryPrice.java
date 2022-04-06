package ru.sir.richard.boss.web.response;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonView;

import ru.sir.richard.boss.model.types.DeliveryPrices;

public class AjaxDeliveryPrice {
	
	private DeliveryPrices deliveryPrice;
	
	@JsonView(Views.Public.class)
	String code;
	
	@JsonView(Views.Public.class)
	String annotation;
	
	@JsonView(Views.Public.class)
	BigDecimal price;
	
	public AjaxDeliveryPrice(DeliveryPrices deliveryPrice) {
		this.deliveryPrice = deliveryPrice;
		this.code = deliveryPrice.name();
		this.annotation = deliveryPrice.getAnnotation();
		this.price = deliveryPrice.getPrice();
	}
	
	public DeliveryPrices getDeliveryPrice() {
		return deliveryPrice;
	}
	
	public void setDeliveryPrice(DeliveryPrices deliveryPrice) {
		this.deliveryPrice = deliveryPrice;
	}
	
	public String getCode() {
		return code;
	}
	
	public void setCode(String code) {
		this.code = code;
	}
	public String getAnnotation() {
		return annotation;
	}
	public void setAnnotation(String annotation) {
		this.annotation = annotation;
	}
	public BigDecimal getPrice() {
		return price;
	}
	public void setPrice(BigDecimal price) {
		this.price = price;
	}	
	
}
