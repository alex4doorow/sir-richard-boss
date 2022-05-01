package ru.sir.richard.boss.model.utils;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TextUtilsTest {

	private final Logger logger = LoggerFactory.getLogger(TextUtilsTest.class);
	
	@Test
	public void testNumberToPhrase() {		
		BigDecimal value;		
		value = BigDecimal.valueOf(1234567);		
		assertEquals("Один миллион двести тридцать четыре тысячи пятьсот шестьдесят семь", TextUtils.numberToPhrase(value));		
	}
	
	@Test
	public void testRoublesToPhrase() {
	
		BigDecimal value;
				
		value = BigDecimal.valueOf(1.00);		
		assertEquals("Один рубль 00 копеек", TextUtils.roublesToPhrase(value));
		value = BigDecimal.valueOf(1.01);		
		assertEquals("Один рубль 01 копейка", TextUtils.roublesToPhrase(value));
		value = BigDecimal.valueOf(1.02);		
		assertEquals("Один рубль 02 копейки", TextUtils.roublesToPhrase(value));
		value = BigDecimal.valueOf(1.05);		
		assertEquals("Один рубль 05 копеек", TextUtils.roublesToPhrase(value));
		value = BigDecimal.valueOf(1.10);		
		assertEquals("Один рубль 10 копеек", TextUtils.roublesToPhrase(value));
		value = BigDecimal.valueOf(1.20);		
		assertEquals("Один рубль 20 копеек", TextUtils.roublesToPhrase(value));
		value = BigDecimal.valueOf(1.21);		
		assertEquals("Один рубль 21 копейка", TextUtils.roublesToPhrase(value));
		value = BigDecimal.valueOf(1.34);		
		assertEquals("Один рубль 34 копейки", TextUtils.roublesToPhrase(value));
		value = BigDecimal.valueOf(1.43);		
		assertEquals("Один рубль 43 копейки", TextUtils.roublesToPhrase(value));
		
		value = BigDecimal.valueOf(2.00);		
		assertEquals("Два рубля 00 копеек", TextUtils.roublesToPhrase(value));
		value = BigDecimal.valueOf(5.00);		
		assertEquals("Пять рублей 00 копеек", TextUtils.roublesToPhrase(value));
		value = BigDecimal.valueOf(11.00);		
		assertEquals("Одиннадцать рублей 00 копеек", TextUtils.roublesToPhrase(value));
		value = BigDecimal.valueOf(21.00);		
		assertEquals("Двадцать один рубль 00 копеек", TextUtils.roublesToPhrase(value));
		value = BigDecimal.valueOf(43.00);		
		assertEquals("Сорок три рубля 00 копеек", TextUtils.roublesToPhrase(value));
		value = BigDecimal.valueOf(149.00);		
		assertEquals("Сто сорок девять рублей 00 копеек", TextUtils.roublesToPhrase(value));		
		value = BigDecimal.valueOf(1000.00);		
		assertEquals("Одна тысяча рублей 00 копеек", TextUtils.roublesToPhrase(value));
		
		value = BigDecimal.valueOf(10000.00);		
		assertEquals("Десять тысяч рублей 00 копеек", TextUtils.roublesToPhrase(value));
		value = BigDecimal.valueOf(10001.00);		
		assertEquals("Десять тысяч один рубль 00 копеек", TextUtils.roublesToPhrase(value));
		value = BigDecimal.valueOf(10011.00);		
		assertEquals("Десять тысяч одиннадцать рублей 00 копеек", TextUtils.roublesToPhrase(value));
		
		value = BigDecimal.valueOf(10143.00);		
		assertEquals("Десять тысяч сто сорок три рубля 00 копеек", TextUtils.roublesToPhrase(value));
		value = BigDecimal.valueOf(10169.00);		
		assertEquals("Десять тысяч сто шестьдесят девять рублей 00 копеек", TextUtils.roublesToPhrase(value));
				
		value = BigDecimal.valueOf(5800.34);		
		assertEquals("Пять тысяч восемьсот рублей 34 копейки", TextUtils.roublesToPhrase(value));
		
		value = BigDecimal.valueOf(5801.00);		
		assertEquals("Пять тысяч восемьсот один рубль 00 копеек", TextUtils.roublesToPhrase(value));
		
		value = BigDecimal.valueOf(5801.43);		
		assertEquals("Пять тысяч восемьсот один рубль 43 копейки", TextUtils.roublesToPhrase(value));				
	}

	
	@Test
	public void testFormatPhoneNumber() {
}
