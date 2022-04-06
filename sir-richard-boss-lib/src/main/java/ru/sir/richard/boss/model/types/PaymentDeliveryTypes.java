package ru.sir.richard.boss.model.types;

public enum PaymentDeliveryTypes {
		
	CUSTOMER(1, "покупатель"),
	SELLER(2, "продавец");

	private int id;
	private String annotation;

	PaymentDeliveryTypes(int id, String annotation) {
		this.id = id;
		this.annotation = annotation;
	}

	public String getAnnotation() {
		return annotation;
	}

	public int getId() {
		return id;
	}

	public static PaymentDeliveryTypes getValueById(int value) {
		if (value == 1) {
			return PaymentDeliveryTypes.CUSTOMER;
		} else if (value == 2) { 
			return PaymentDeliveryTypes.SELLER;
		} else {
			return PaymentDeliveryTypes.CUSTOMER;
		}
	}
}
