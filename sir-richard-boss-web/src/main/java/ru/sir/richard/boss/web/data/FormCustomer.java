package ru.sir.richard.boss.web.data;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import ru.sir.richard.boss.model.data.Address;
import ru.sir.richard.boss.model.data.AnyCustomer;
import ru.sir.richard.boss.model.data.AnyId;
import ru.sir.richard.boss.model.data.Contact;
import ru.sir.richard.boss.model.data.ForeignerCompanyCustomer;
import ru.sir.richard.boss.model.data.ForeignerCustomer;
import ru.sir.richard.boss.model.factories.CustomersFactory;
import ru.sir.richard.boss.model.types.AddressTypes;
import ru.sir.richard.boss.model.types.Countries;
import ru.sir.richard.boss.model.types.CustomerStatuses;
import ru.sir.richard.boss.model.types.CustomerTypes;

public class FormCustomer extends AnyId {
	
	private CustomerTypes customerType;
	
	// ANY_CUSTOMER	
	private Countries country;
	private CustomerStatuses status;
	private int personId;
	private Address mainAddress;
	
	// CUSTOMER	
	private String firstName;
	private String lastName;
	private String middleName;
	private String phoneNumber;
	private String email;
	
	// COMPANY
	private String inn;	
	private String shortName;
	private String longName;	
	private List<Contact> contacts;
	
	public FormCustomer() {
		super();
		contacts = new ArrayList<Contact>();
		contacts.add(new Contact(Countries.RUSSIA));
		clear();
		mainAddress = new Address(this.country, AddressTypes.MAIN);
	}
	
	@Override
	public void clear() {
		super.clear();
		customerType = CustomerTypes.CUSTOMER;
		country = Countries.RUSSIA;
		status = CustomerStatuses.PROCEED;
		personId = 0;		
	}

	public CustomerTypes getCustomerType() {
		return customerType;
	}

	public void setCustomerType(CustomerTypes customerType) {
		this.customerType = customerType;
	}	
	
	public Address getMainAddress() {		
		return this.mainAddress;		
	}
	
	public void setMainAddress(Address mainAddress) {
		this.mainAddress = mainAddress;
	}

	public Countries getCountry() {
		return country;
	}

	public void setCountry(Countries country) {
		this.country = country;
	}

	public CustomerStatuses getStatus() {
		return status;
	}

	public void setStatus(CustomerStatuses status) {
		this.status = status;
	}

	public int getPersonId() {
		return personId;
	}

	public void setPersonId(int personId) {
		this.personId = personId;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getMiddleName() {
		return middleName;
	}

	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getInn() {
		return inn;
	}

	public void setInn(String inn) {
		this.inn = inn;
	}

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
	
	public boolean isCompany() {
		if (getCustomerType() == CustomerTypes.CUSTOMER || getCustomerType() == CustomerTypes.FOREIGNER_CUSTOMER) {
			return false;	
		} else if (getCustomerType() == CustomerTypes.COMPANY || getCustomerType() == CustomerTypes.BUSINESSMAN || getCustomerType() == CustomerTypes.FOREIGNER_COMPANY) {			
			return true;
		} else {
			return false;	
		}
	}

	public List<Contact> getContacts() {
		return contacts;
	}

	public void setContacts(List<Contact> contacts) {
		this.contacts = contacts;
	}
	
	public Contact getMainContact() {		
		return contacts.get(0);
	}
	
	public void setMainContact(Contact contact) {
		contacts.remove(0);
		contacts.add(contact);
	}
	
	public String getViewLongName() {		
		if (getCustomerType() == CustomerTypes.CUSTOMER || getCustomerType() == CustomerTypes.FOREIGNER_CUSTOMER) {
			return StringUtils.trim(StringUtils.defaultString(this.lastName) + " " + StringUtils.defaultString(this.firstName) + " " + StringUtils.defaultString(this.middleName)) + ", " + this.getPhoneNumber();	
		} else if (getCustomerType() == CustomerTypes.COMPANY || getCustomerType() == CustomerTypes.BUSINESSMAN || getCustomerType() == CustomerTypes.FOREIGNER_COMPANY) {			
			String viewLongName = StringUtils.isNotEmpty(this.longName) ? longName : this.shortName;			
			String contact = getMainContact().getViewLongName() + " " + getMainContact().getEmail();
			viewLongName = viewLongName.trim() + ", " + contact;	
			if (StringUtils.isEmpty(this.inn)) {
				return viewLongName;
			} else {
				return "ИНН " + this.inn + " " + StringUtils.defaultString(viewLongName);
			}
		} else {
			return "";	
		}				
	}
	
	public String getViewShortName() {
		if (getCustomerType() == CustomerTypes.CUSTOMER || getCustomerType() == CustomerTypes.FOREIGNER_CUSTOMER) {			
			if (!StringUtils.isEmpty(this.firstName) && StringUtils.isEmpty(this.lastName)) {
				return this.firstName;
			}
			if (StringUtils.isEmpty(this.firstName) && !StringUtils.isEmpty(this.lastName)) {
				return this.lastName;
			}
			String ln = "",	fn = "", mn = "";
			if (StringUtils.isNotEmpty(this.lastName)) {
				ln = this.lastName; 
			}
			if (StringUtils.isNotEmpty(this.firstName)) {
				fn = this.firstName.substring(0, 1) + "."; 
			}
			if (StringUtils.isNotEmpty(this.middleName)) {
				mn = this.middleName.substring(0, 1) + "."; 
			}		
			return ln + " " + fn + " " + mn;
		} else if (getCustomerType() == CustomerTypes.COMPANY || getCustomerType() == CustomerTypes.BUSINESSMAN || getCustomerType() == CustomerTypes.FOREIGNER_COMPANY) {
			return StringUtils.defaultString(this.shortName);			
		} else {
			return "";	
		}		
	}
	
	public static FormCustomer createForm(AnyCustomer source) {
		FormCustomer result = new FormCustomer();
		result.setId(source.getId());
		result.setCustomerType(source.getType());
		result.setStatus(source.getStatus());
		result.setMainAddress(source.getMainAddress());
		result.setCountry(source.getCountry());
		
		if (source.isPerson()) {			
			ForeignerCustomer sourceCustomer = (ForeignerCustomer) source;
			result.setPersonId(sourceCustomer.getPersonId());
			result.setFirstName(sourceCustomer.getFirstName());
			result.setLastName(sourceCustomer.getLastName());
			result.setMiddleName(sourceCustomer.getMiddleName());
			result.setPhoneNumber(sourceCustomer.getPhoneNumber());
			result.setEmail(sourceCustomer.getEmail());	
			
		} else {
			ForeignerCompanyCustomer sourceCustomer = (ForeignerCompanyCustomer) source;
			result.setInn(sourceCustomer.getInn());
			result.setShortName(sourceCustomer.getShortName());
			result.setLongName(sourceCustomer.getLongName());
			result.setMainContact(sourceCustomer.getMainContact());
		} 					
		return result;		
	}
	
	public AnyCustomer createCustomer() {
		AnyCustomer result = CustomersFactory.createCustomer(this.getCustomerType());
		result.setId(this.getId());
		result.setStatus(this.getStatus());		
		if (result.isPerson()) {			
			ForeignerCustomer resultCustomer = (ForeignerCustomer) result;
			resultCustomer.setCountry(this.getCountry());
			resultCustomer.setPersonId(this.getPersonId());
			resultCustomer.setFirstName(this.getFirstName());
			resultCustomer.setLastName(this.getLastName());
			resultCustomer.setMiddleName(this.getMiddleName());
			resultCustomer.setPhoneNumber(this.getPhoneNumber());
			resultCustomer.setEmail(this.getEmail());			
		} else {
			ForeignerCompanyCustomer resultCustomer = (ForeignerCompanyCustomer) result;
			resultCustomer.setCountry(this.getCountry());
			resultCustomer.setInn(this.getInn());
			resultCustomer.setShortName(this.getShortName());
			resultCustomer.setLongName(this.getLongName());
			resultCustomer.setMainContact(this.getMainContact());
			
		} 
		result.setMainAddress(this.getMainAddress());
		return result;		
	}

}
