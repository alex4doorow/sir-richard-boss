package ru.sir.richard.boss.model.data;

import org.apache.commons.lang3.StringUtils;

import ru.sir.richard.boss.model.types.Countries;
import ru.sir.richard.boss.model.types.CustomerTypes;

public class CompanyCustomer extends ForeignerCompanyCustomer {
	
	public CompanyCustomer() {
		super(Countries.RUSSIA);
	}

	@Override
	public CustomerTypes getType() {
		return CustomerTypes.COMPANY;
	}
	
	@Override
	public String toString() {
		return "CompanyCustomer [id=" + getId() 
				+ ", inn=" + StringUtils.defaultString(getInn()) 
				+ ", shortName=" + StringUtils.defaultString(getShortName()) 
				+ ", longName=" + StringUtils.defaultString(getShortName()) 
				+ "]";
	}
}
