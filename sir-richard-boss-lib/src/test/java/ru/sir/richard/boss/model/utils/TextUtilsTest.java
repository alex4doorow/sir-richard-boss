package ru.sir.richard.boss.model.utils;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

import ru.sir.richard.boss.model.types.CustomerTypes;
import ru.sir.richard.boss.utils.TextUtils;

public class TextUtilsTest {

	//private final Logger logger = LoggerFactory.getLogger(TextUtilsTest.class);
	
	@Test
	public void testEnumConvertValuesToSplitedString() {
		String result;
		
		/*
		result = CustomerTypes.convertValuesToSplitedString(null);
		assertEquals("", result);
		*/
		result = CustomerTypes.convertValuesToSplitedString(CustomerTypes.BUSINESSMAN);
		assertEquals("3", result);
		
		result = CustomerTypes.convertValuesToSplitedString(CustomerTypes.BUSINESSMAN, CustomerTypes.COMPANY);
		assertEquals("3,2", result);
		
		
		
	}
	
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
	public void testCutCityFromAddress() {
		String city;
		
		city = TextUtils.cutCityFromAddress("Владимир, пр-т Строителей, 15");
		assertEquals("Владимир", city);
		city = TextUtils.cutCityFromAddress("г.Челябинск, ул.Кирова 130/7, для доставки прошу смотреть по 2ГИС");
		assertEquals("Челябинск", city);				
		city = TextUtils.cutCityFromAddress("Санкт-Петербург, ул. Адмирала Трибуца, д.10");
		assertEquals("Санкт-Петербург", city);
		
	}
	
	@Test
	public void testCutStreetFromAddress() {
		String street;
		street = TextUtils.cutStreetFromAddress("Владимир, пр-т Строителей, 15");
		assertEquals("пр-т Строителей, 15", street);
		
		street = TextUtils.cutStreetFromAddress("Санкт-Петербург, ул. Адмирала Трибуца, д.10");
		assertEquals("ул. Адмирала Трибуца, д.10", street);

	}
	
	@Test
	public void testFormatPhoneNumber() {
		String s = "";
		String r = "";
		
		s = "+79057809641";
		r = TextUtils.formatPhoneNumber(s);
		assertEquals("(905) 780-96-41", r);
		
		s = "89177962825";
		r = TextUtils.formatPhoneNumber(s);
		assertEquals("(917) 796-28-25", r);
		
		s = "8(916)609-81-84";
		r = TextUtils.formatPhoneNumber(s);
		assertEquals("(916) 609-81-84", r);
		
		s = "89086650244";
		r = TextUtils.formatPhoneNumber(s);
		assertEquals("(908) 665-02-44", r);
		
		s = "9263361624";
		r = TextUtils.formatPhoneNumber(s);
		assertEquals("(926) 336-16-24", r);
		
		s = "8-910-450-86-80";
		r = TextUtils.formatPhoneNumber(s);
		assertEquals("(910) 450-86-80", r);
		
		s = "79057809641";
		r = TextUtils.formatPhoneNumber(s);
		assertEquals("(905) 780-96-41", r);		
		
		s ="989 612 28 82";
		r = TextUtils.formatPhoneNumber(s);
		assertEquals("(989) 612-28-82", r);		
	}
}
