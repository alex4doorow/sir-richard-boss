package ru.sir.richard.boss.model.data;

import org.apache.commons.lang3.StringUtils;

import ru.sir.richard.boss.model.types.Countries;
import ru.sir.richard.boss.model.types.CustomerTypes;

public class ForeignerCustomer extends AnyCustomer implements Person {		
	
	private int personId;
	private String firstName;
	private String lastName;
	private String middleName;
	private String phoneNumber; 
	private String email; 
		
	public ForeignerCustomer(Countries country) {
		super();
		this.setCountry(country);
	}
	
	public ForeignerCustomer() {
		this(Countries.UNKNOWN);
	}
	
	@Override
	public int getPersonId() {
		return personId;
	}
		
	public void setPersonId(int personId) {
		this.personId = personId;
	}
	
	@Override
	public boolean isPerson() {
		return true;
	}

	@Override
	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	@Override
	public String getLastName() {
		return lastName;
	}
	
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}	
	
	@Override
	public String getMiddleName() {
		return this.middleName;
	}	
	
	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}
	
	@Override
	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	@Override
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Override
	public String getViewLongName() {
		String result = StringUtils.defaultString(this.lastName) + " " + StringUtils.defaultString(this.firstName)
				+ " " + StringUtils.defaultString(this.middleName);
		return result.trim();
	}
	
	@Override
	public String getViewLongNameWithContactInfo() {
		return getViewLongName() + ", " + getPhoneNumber();
	}
	
	@Override	
	public String getViewShortName() {
		if (StringUtils.isNotEmpty(this.firstName) && StringUtils.isEmpty(this.lastName)) {
			return this.firstName.trim();
		}
		if (StringUtils.isEmpty(this.firstName) && StringUtils.isNotEmpty(this.lastName)) {
			return this.lastName.trim();
		}
		if (StringUtils.isNotEmpty(this.firstName) && StringUtils.isNotEmpty(this.lastName) && StringUtils.isEmpty(this.middleName)) {
			return (this.lastName + " " + this.firstName).trim();
		}
		String ln = "",	fn = "", mn = "";
		if (!StringUtils.isEmpty(this.lastName)) {
			ln = this.lastName.trim(); 
		}
		if (!StringUtils.isEmpty(this.firstName)) {
			fn = this.firstName.trim().substring(0, 1) + "."; 
		}
		if (!StringUtils.isEmpty(this.middleName)) {
			mn = this.middleName.trim().substring(0, 1) + "."; 
		}		
		return (ln + " " + fn + " " + mn).trim();
	}
	
	@Override
	public String getViewPhoneNumber() {
		return this.getPhoneNumber();
	}
	
	@Override
	public CustomerTypes getType() {
		return CustomerTypes.FOREIGNER_CUSTOMER;
	}
	
	@Override
	public ForeignerCustomer clone() throws CloneNotSupportedException  {
		ForeignerCustomer clone = (ForeignerCustomer) super.clone();						
		clone.personId = this.personId;				
		clone.firstName = this.firstName == null ? null : new String(this.firstName);
		clone.lastName = this.lastName == null ? null : new String(this.lastName);
		clone.middleName = this.middleName == null ? null : new String(this.middleName);
		clone.phoneNumber = this.phoneNumber == null ? null : new String(this.phoneNumber);		
		clone.email = this.email == null ? null : new String(this.email);
		return clone;
	}
	
	@Override
	public String toString() {
		return "ForeignerCustomer [id=" + getId() 
		+ ", firstName=" + StringUtils.defaultString(getFirstName())
		+ ", lastName=" + StringUtils.defaultString(getLastName())
		+ ", phoneNumber=" + StringUtils.defaultString(getPhoneNumber()) 
		+ ", email=" + StringUtils.defaultString(getEmail())
		+ ", mainAddress=" + getMainAddress()
		+ ", country=" + getCountry() 
		+ "]";
	}
}
