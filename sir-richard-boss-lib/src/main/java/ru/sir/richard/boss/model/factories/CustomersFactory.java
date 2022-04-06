package ru.sir.richard.boss.model.factories;

import ru.sir.richard.boss.model.data.AnyCustomer;
import ru.sir.richard.boss.model.data.BusinessmanCustomer;
import ru.sir.richard.boss.model.data.CompanyCustomer;
import ru.sir.richard.boss.model.data.Customer;
import ru.sir.richard.boss.model.data.ForeignerCompanyCustomer;
import ru.sir.richard.boss.model.data.ForeignerCustomer;
import ru.sir.richard.boss.model.types.CustomerTypes;

public class CustomersFactory {
	
	public static AnyCustomer createCustomer(CustomerTypes customerType) {
		if (customerType == CustomerTypes.CUSTOMER) {
			return new Customer();
		} else if (customerType == CustomerTypes.BUSINESSMAN) {
			return new BusinessmanCustomer();
		} else if (customerType == CustomerTypes.COMPANY) {
			return new CompanyCustomer();
		} else if (customerType == CustomerTypes.FOREIGNER_COMPANY) {
			return new ForeignerCompanyCustomer(); 
		} else if (customerType == CustomerTypes.FOREIGNER_CUSTOMER) {
			return new ForeignerCustomer(); 
		} else if (customerType == CustomerTypes.UNKNOWN) {
			return new Customer();
		} else {
			return null;
		}
	}

	public static AnyCustomer createEmptyCustomer() {
		return createCustomer(CustomerTypes.UNKNOWN);
	}

}
