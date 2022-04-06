package ru.sir.richard.boss.dao;

import ru.sir.richard.boss.model.data.Address;
import ru.sir.richard.boss.model.data.AnyCustomer;
import ru.sir.richard.boss.model.data.Person;
import ru.sir.richard.boss.model.data.conditions.CustomerConditions;

public interface CustomerDao {
	
	AnyCustomer findById(int id);	
	AnyCustomer findByConditions(CustomerConditions customerConditions);
	
	String nextEmptyPhoneNumber();
	
	int addAddress(Address address);	
	void updateAddress(int addressId, Address address);
	
	Person findPerson(Person person);
	int addPerson(Person person);
	void updatePerson(int personId, Person person);
		
	int addCustomer(AnyCustomer customer);
	void updateCustomer(AnyCustomer customer);
	void eraseCustomer(int customerId);
	
	Address getAddress(int addressId);
	Person getPerson(int personId);

}
