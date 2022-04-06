package ru.sir.richard.boss.api.epochta.v2;

public class SmsPhone {
	public String idMessage;
	public String varaibles;
	public String phone;

	public SmsPhone(String idMessage, String variables, String phone) {
		this.phone = phone;
		this.varaibles = variables;
		this.idMessage = idMessage;
	}

	public String getIdMessage() {
		return this.idMessage;
	}

	public String getVariable() {
		return this.varaibles;
	}

	public String getPhone() {
		return this.phone;
	}

}
