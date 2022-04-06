package ru.sir.richard.boss.model.data.conditions;

import ru.sir.richard.boss.model.types.CustomerTypes;

public class CustomerConditions extends AnyConditions {
		
	private CustomerTypes customerType;
	
	// CUSTOMER
	private String personPhoneNumber;
	private String personLastName;
	private String personEmail;
	
	// COMPANY
	private String companyInn;
	private String companyShortName;
	private String companyMainContactPhoneNumber;
	private String companyMainContactEmail;
	
	public CustomerConditions(CustomerTypes customerType) {
		super();
		this.customerType = customerType;
	}
	
	public CustomerConditions() {
		this(CustomerTypes.CUSTOMER);
	}

	public CustomerTypes getCustomerType() {
		return customerType;
	}
	
	public void setCustomerType(CustomerTypes customerType) {
		this.customerType = customerType;
	}

	public String getPersonPhoneNumber() {
		return personPhoneNumber;
	}

	public void setPersonPhoneNumber(String personPhoneNumber) {
		this.personPhoneNumber = personPhoneNumber;
	}

	public String getPersonLastName() {
		return personLastName;
	}

	public void setPersonLastName(String personLastName) {
		this.personLastName = personLastName;
	}

	public String getPersonEmail() {
		return personEmail;
	}

	public void setPersonEmail(String personEmail) {
		this.personEmail = personEmail;
	}

	public String getCompanyInn() {
		return companyInn;
	}

	public void setCompanyInn(String companyInn) {
		this.companyInn = companyInn;
	}

	public String getCompanyShortName() {
		return companyShortName;
	}

	public void setCompanyShortName(String companyShortName) {
		this.companyShortName = companyShortName;
	}

	public String getCompanyMainContactPhoneNumber() {
		return companyMainContactPhoneNumber;
	}

	public void setCompanyMainContactPhoneNumber(String companyMainContactPhoneNumber) {
		this.companyMainContactPhoneNumber = companyMainContactPhoneNumber;
	}

	public String getCompanyMainContactEmail() {
		return companyMainContactEmail;
	}

	public void setCompanyMainContactEmail(String companyMainContactEmail) {
		this.companyMainContactEmail = companyMainContactEmail;
	}	

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((companyInn == null) ? 0 : companyInn.hashCode());
		result = prime * result + ((companyMainContactEmail == null) ? 0 : companyMainContactEmail.hashCode());
		result = prime * result
				+ ((companyMainContactPhoneNumber == null) ? 0 : companyMainContactPhoneNumber.hashCode());
		result = prime * result + ((companyShortName == null) ? 0 : companyShortName.hashCode());
		result = prime * result + ((personEmail == null) ? 0 : personEmail.hashCode());
		result = prime * result + ((personLastName == null) ? 0 : personLastName.hashCode());
		result = prime * result + ((personPhoneNumber == null) ? 0 : personPhoneNumber.hashCode());
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
		CustomerConditions other = (CustomerConditions) obj;
		if (companyInn == null) {
			if (other.companyInn != null)
				return false;
		} else if (!companyInn.equals(other.companyInn))
			return false;
		if (companyMainContactEmail == null) {
			if (other.companyMainContactEmail != null)
				return false;
		} else if (!companyMainContactEmail.equals(other.companyMainContactEmail))
			return false;
		if (companyMainContactPhoneNumber == null) {
			if (other.companyMainContactPhoneNumber != null)
				return false;
		} else if (!companyMainContactPhoneNumber.equals(other.companyMainContactPhoneNumber))
			return false;
		if (companyShortName == null) {
			if (other.companyShortName != null)
				return false;
		} else if (!companyShortName.equals(other.companyShortName))
			return false;
		if (personEmail == null) {
			if (other.personEmail != null)
				return false;
		} else if (!personEmail.equals(other.personEmail))
			return false;
		if (personLastName == null) {
			if (other.personLastName != null)
				return false;
		} else if (!personLastName.equals(other.personLastName))
			return false;
		if (personPhoneNumber == null) {
			if (other.personPhoneNumber != null)
				return false;
		} else if (!personPhoneNumber.equals(other.personPhoneNumber))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "CustomerConditions [personPhoneNumber=" + personPhoneNumber + ", personLastName=" + personLastName + ", personEmail=" + personEmail 
				+ ", companyInn=" + companyInn + ", companyShortName=" + companyShortName + ", companyMainContactPhoneNumber=" + companyMainContactPhoneNumber
				+ "]";
	}

}
