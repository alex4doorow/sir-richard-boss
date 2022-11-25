package ru.sir.richard.boss.model.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;

public class DateTimeUtilsTest {
		
	@Test
	public void testMonth() throws ParseException {
		
		String s = DateTimeUtils.formatDate(new Date(), "MMMM");

		assertTrue(StringUtils.isNotEmpty(s));
		
		final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");		
		final java.util.Date currentDate = sdf.parse("2022-10-16");
		
		s = DateTimeUtils.formatDate(currentDate, "MMMM");
		assertEquals("октябрь", s);		
	}
	
	@Test
	public void testDate() throws ParseException {
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
	public void testSysDate() throws ParseException {
		Date d1 = DateTimeUtils.sysDate();
		Date d2 = new Date();
		
		d2 = DateTimeUtils.truncateDate(d2);		

		assertEquals(d1, d2);	
	}	
}
