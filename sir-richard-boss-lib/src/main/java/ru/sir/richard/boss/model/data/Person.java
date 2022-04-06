package ru.sir.richard.boss.model.data;

import ru.sir.richard.boss.model.types.Countries;

public interface Person {
	
	int getPersonId();
	Countries getCountry();
	String getFirstName();
	String getLastName();	
	String getMiddleName();
	String getPhoneNumber();
	String getEmail();
	String getViewLongName();	

}
