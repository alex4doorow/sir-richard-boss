package ru.sir.richard.boss.model.data;

import org.junit.jupiter.api.Test;

import ru.sir.richard.boss.model.factories.CustomersFactory;
import ru.sir.richard.boss.model.types.AddressTypes;
import ru.sir.richard.boss.model.types.Countries;
import ru.sir.richard.boss.model.types.CustomerTypes;

public class CloneableTest {
	
	@Test
	public void testOne() throws CloneNotSupportedException {
		
		Address addressOne = new Address(Countries.RUSSIA, AddressTypes.MAIN);
		addressOne.setId(1);
		addressOne.setAddress("г.Псков, ***");
		
		CarrierInfo cdekInfo = new CarrierInfo();
		cdekInfo.setCityContext("");
		cdekInfo.setCityId(123);
		cdekInfo.setPvz("MSK17");				
		addressOne.setCarrierInfo(cdekInfo);
				
		Address addressTwo = addressOne.clone();					
		addressTwo.setId(2);
		addressTwo.setAddressType(AddressTypes.MAIN);
		addressTwo.setAddress("Якутск, ***");
		addressTwo.getCarrierInfo().setPvz("CH345");
		
		AnyCustomer customerOne = CustomersFactory.createCustomer(CustomerTypes.CUSTOMER);
		customerOne.setMainAddress(addressOne);
		
		Order one = new Order();
		one.setCustomer(customerOne);
		one.setId(1);
		one.setNo(6001);
		
		Order two = one.clone();		
		two.setId(2);
		two.setNo(6002);
		two.getCustomer().getMainAddress().setAddress("г.Москва, ***");
				
	}

}
