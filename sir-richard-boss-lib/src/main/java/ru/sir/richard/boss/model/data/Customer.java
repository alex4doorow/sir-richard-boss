package ru.sir.richard.boss.model.data;

import org.apache.commons.lang3.StringUtils;

import ru.sir.richard.boss.model.types.Countries;
import ru.sir.richard.boss.model.types.CustomerTypes;

/**
 * Покупатель - физическое лицо
 * @author alex4
 *
 */
public class Customer extends ForeignerCustomer implements Person {		

	public Customer() {
		super(Countries.RUSSIA);
	}
		
	@Override
	public CustomerTypes getType() {
		return CustomerTypes.CUSTOMER;
	}
	
	@Override
	public String toString() {
		return "Customer [id=" + getId() 
				+ ", firstName=" + StringUtils.defaultString(getFirstName())
				+ ", lastName=" + StringUtils.defaultString(getLastName())
				+ ", phoneNumber=" + StringUtils.defaultString(getPhoneNumber()) 
				+ ", email=" + StringUtils.defaultString(getEmail())
				+ ", mainAddress=" + getMainAddress() 				 
				+ "]";
	}
}
