package ru.sir.richard.boss.model.utils;

import static org.junit.Assert.assertEquals;

import java.text.ParseException;
import java.util.Date;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DateTimeUtilsTest {
	
	private final Logger logger = LoggerFactory.getLogger(DateTimeUtilsTest.class);
	
	@Test
	public void testOne() {
		String s = DateTimeUtils.formatDate(new Date(), "MMMM");
		logger.debug("testOne():{}", s);		
	}
	
	@Test
	public void testTwo() throws ParseException {
		Date one;
		Date result;
		one = DateTimeUtils.defaultFormatStringToDate("18.03.2020");
		result = DateTimeUtils.afterAnyDateOnlyWork(one, 3);
		assertEquals(result, DateTimeUtils.defaultFormatStringToDate("23.03.2020"));
		
		one = DateTimeUtils.defaultFormatStringToDate("18.03.2020");
		result = DateTimeUtils.afterAnyDateOnlyWork(one, 4);
		assertEquals(result, DateTimeUtils.defaultFormatStringToDate("24.03.2020"));
		
		one = DateTimeUtils.defaultFormatStringToDate("16.03.2020");
		result = DateTimeUtils.afterAnyDateOnlyWork(one, 3);
		assertEquals(result, DateTimeUtils.defaultFormatStringToDate("19.03.2020"));
		
		one = DateTimeUtils.defaultFormatStringToDate("20.03.2020");
		result = DateTimeUtils.afterAnyDateOnlyWork(one, 3);
		assertEquals(result, DateTimeUtils.defaultFormatStringToDate("25.03.2020"));	
	}
	
	@Test
	public void testThree() throws ParseException {
		Date d1 = DateTimeUtils.sysDate();
		Date d2 = new Date();
		
		d2 = DateTimeUtils.truncateDate(d2);		
		d2.compareTo(d1);
		
		assertEquals(d1, d2);	
	}
	
	
	
}
