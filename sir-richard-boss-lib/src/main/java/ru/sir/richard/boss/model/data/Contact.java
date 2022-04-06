package ru.sir.richard.boss.model.data;

import org.apache.commons.lang3.StringUtils;

import ru.sir.richard.boss.model.types.Countries;

public class Contact extends AnyId implements Person {
	
	private int personId; 
	private Countries country;
	private String phoneNumber;
	private String email;
	
	private String firstName;
	private String lastName;
	private String middleName;
	
	public Contact(Countries country) {
		super();
		this.country = country;
	}
	
	public Contact() {
		this(Countries.RUSSIA);
	}
	
	@Override
	public int getPersonId() {
		return personId;
	}
	
	public void setPersonId(int personId) {
		this.personId = personId;
	}

	@Override
	public Countries getCountry() {
		return country;
	}
	
	public void setCountry(Countries country) {
		this.country = country;
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
		return middleName;
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
		String result = StringUtils.defaultString(this.lastName) + " " + StringUtils.defaultString(this.firstName) + " " + StringUtils.defaultString(this.middleName);
		return result.trim();
	}
	
	@Override
	public Contact clone() throws CloneNotSupportedException  {
		Contact clone = (Contact) super.clone();		
		clone.personId = this.personId;
		clone.country = this.country;
		clone.phoneNumber = this.phoneNumber == null ? null : new String(this.phoneNumber);		
		clone.email = this.email == null ? null : new String(this.email);
		clone.firstName = this.firstName == null ? null : new String(this.firstName);
		clone.lastName = this.lastName == null ? null : new String(this.lastName);
		clone.middleName = this.middleName == null ? null : new String(this.middleName);
		return clone;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((country == null) ? 0 : country.hashCode());
		result = prime * result + ((email == null) ? 0 : email.hashCode());
		result = prime * result + ((firstName == null) ? 0 : firstName.hashCode());
		result = prime * result + ((lastName == null) ? 0 : lastName.hashCode());
		result = prime * result + ((middleName == null) ? 0 : middleName.hashCode());
		result = prime * result + ((phoneNumber == null) ? 0 : phoneNumber.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		Contact other = (Contact) obj;
		if (country != other.country)
			return false;
		if (email == null) {
			if (other.email != null)
				return false;
		} else if (!email.equals(other.email))
			return false;
		if (firstName == null) {
			if (other.firstName != null)
				return false;
		} else if (!firstName.equals(other.firstName))
			return false;
		if (lastName == null) {
			if (other.lastName != null)
				return false;
		} else if (!lastName.equals(other.lastName))
			return false;
		if (middleName == null) {
			if (other.middleName != null)
				return false;
		} else if (!middleName.equals(other.middleName))
			return false;
		if (phoneNumber == null) {
			if (other.phoneNumber != null)
				return false;
		} else if (!phoneNumber.equals(other.phoneNumber))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Contact [country=" + country + ", phoneNumber=" + phoneNumber + ", email=" + email + ", firstName="
				+ firstName + ", lastName=" + lastName + ", middleName=" + middleName + "]";
	}

}
