package ru.sir.richard.boss.utils;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GenericTest  {
	
	private final Logger logger = LoggerFactory.getLogger(GenericTest.class);
	
		
	
	public static void main(String[] args) {
		
		GenericTest test = new GenericTest();	
		
		
		AnyTypes<TestSupplierType> testSupplierType = new TestSupplierType();		
		Set<TestSupplierType> tests1 = testSupplierType.getStatusesByArray("1,2,3");
		test.logger.info("{}", tests1);
		
		AnyTypes<TestOrderType> testOrderType = new TestOrderType();
		Set<TestOrderType> tests2 = testOrderType.getStatusesByArray("1,2,3");
		test.logger.info("{}", tests2);
	} 

}
