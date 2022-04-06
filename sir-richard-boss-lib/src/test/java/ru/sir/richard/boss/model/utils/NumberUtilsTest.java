package ru.sir.richard.boss.model.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NumberUtilsTest {
	
	private final Logger logger = LoggerFactory.getLogger(NumberUtilsTest.class);
	
	@Test
	public void testOne() {
		int newVisitors = 11;
		int uniqueVisitors = 89;
		
		BigDecimal result = BigDecimal.valueOf(newVisitors).divide(BigDecimal.valueOf(uniqueVisitors), 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP);
		logger.debug("testOne():{}", result);
		
	}
	
	@Test
	public void testTwo() {
		BigDecimal v = BigDecimal.valueOf(7675).divide(BigDecimal.valueOf(11209), 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP);
		String result = NumberUtils.formatRate(v);
		logger.debug("testTwo():{}", result);
		
	}
	
	@Test
	public void testThree() {
		BigDecimal money = new BigDecimal(345678124.56);		
		String moneyString = NumberUtils.formatRoubles(money);
		logger.debug("testThree():{}", moneyString);
		
	}

}
