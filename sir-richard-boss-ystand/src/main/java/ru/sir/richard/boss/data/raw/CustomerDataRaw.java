package ru.sir.richard.boss.data.raw;

import ru.sir.richard.boss.model.data.AnyCustomer;
import ru.sir.richard.boss.model.data.CompanyCustomer;
import ru.sir.richard.boss.model.data.Contact;
import ru.sir.richard.boss.model.data.Customer;
import ru.sir.richard.boss.model.factories.CustomersFactory;
import ru.sir.richard.boss.model.types.Countries;
import ru.sir.richard.boss.model.types.CustomerTypes;

public class CustomerDataRaw {

	public CustomerDataRaw(CustomerTypes customerType, Countries country) {
		
		super();
		this.customerType = customerType;
		this.country = country;
	}
	
	private CustomerTypes customerType;
	private Countries country;
	private String shortName;
	private String longName;
	private Contact mainContact;
	
	private String f;
	private String i;
	private String o;
	
	private String phone;
	private String email;
	private String address;

	public String getShortName() {
		return shortName;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	public String getLongName() {
		return longName;
	}

	public void setLongName(String longName) {
		this.longName = longName;
	}
	
	public Contact getMainContact() {
		return mainContact;
	}

	public void setMainContact(Contact mainContact) {
		this.mainContact = mainContact;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}	

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	
	

	public String getF() {
		return f;
	}

	public void setF(String f) {
		this.f = f;
	}

	public String getI() {
		return i;
	}

	public void setI(String i) {
		this.i = i;
	}

	public String getO() {
		return o;
	}

	public void setO(String o) {
		this.o = o;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}
		
	public CustomerTypes getCustomerType() {
		return customerType;
	}
	
	public Countries getCountry() {
		return country;
	}

	public AnyCustomer deepClone() {
		
		AnyCustomer result = CustomersFactory.createCustomer(this.customerType);
		result.setCountry(this.getCountry());
				
		if (this.customerType == CustomerTypes.CUSTOMER) {
			Customer customerResult = (Customer) result;
			customerResult.setFirstName(this.getI());
			customerResult.setMiddleName(this.getO());
			customerResult.setLastName(this.getF());
			customerResult.setPhoneNumber(this.phone);
			customerResult.setEmail(this.email);
		} else {
			CompanyCustomer customerResult = (CompanyCustomer) result;
			customerResult.setShortName(this.shortName); 
			customerResult.setLongName(this.longName);			
			customerResult.setMainContact(this.getMainContact());
		}	
		/*
		Address mainAddress = new Address(this.country, AddressTypes.MAIN);
		mainAddress.setAddress(this.address);
		result.setMainAddress(mainAddress);
		*/		
		return result;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((address == null) ? 0 : address.hashCode());
		result = prime * result + ((country == null) ? 0 : country.hashCode());
		result = prime * result + ((customerType == null) ? 0 : customerType.hashCode());
		result = prime * result + ((email == null) ? 0 : email.hashCode());
		result = prime * result + ((longName == null) ? 0 : longName.hashCode());
		result = prime * result + ((phone == null) ? 0 : phone.hashCode());
		result = prime * result + ((shortName == null) ? 0 : shortName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CustomerDataRaw other = (CustomerDataRaw) obj;
		if (address == null) {
			if (other.address != null)
				return false;
		} else if (!address.equals(other.address))
			return false;
		if (country != other.country)
			return false;
		if (customerType != other.customerType)
			return false;
		if (email == null) {
			if (other.email != null)
				return false;
		} else if (!email.equals(other.email))
			return false;
		if (longName == null) {
			if (other.longName != null)
				return false;
		} else if (!longName.equals(other.longName))
			return false;
		if (phone == null) {
			if (other.phone != null)
				return false;
		} else if (!phone.equals(other.phone))
			return false;
		if (shortName == null) {
			if (other.shortName != null)
				return false;
		} else if (!shortName.equals(other.shortName))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "CustomerDataRaw [customerType=" + customerType + ", country=" + country 
				//+ ", shortName=" + shortName
				//+ ", longName=" + longName 
				+ ", f=" + f + ", i=" + i + ", o=" + o 
				+ ", phone=" + phone + ", email=" + email + ", address=" + address + "]";
	}

}
