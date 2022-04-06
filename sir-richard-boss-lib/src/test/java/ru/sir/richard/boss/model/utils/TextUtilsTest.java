package ru.sir.richard.boss.model.utils;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.sir.richard.boss.model.utils.TextUtils;

public class TextUtilsTest {

	private final Logger logger = LoggerFactory.getLogger(TextUtilsTest.class);
	
	@Test
	public void testOne() {
		String deliveryAddressText;
		//ll_cdek.ll_cdek_136
		deliveryAddressText = StringUtils.trim(StringUtils.substring(StringUtils.defaultString("<img src=\"https://pribormaster.ru/image/catalog/ll_cdek/logo.png\" / > Посылка склад-склад из Москва в Архангельск, ул. Адмиралтейская, 9 (3-4 раб. дня)"), 102));
		System.out.println("*" + deliveryAddressText + "*");
		
		//ll_cdek.ll_cdek_137
		deliveryAddressText = StringUtils.trim(StringUtils.substring(StringUtils.defaultString("<img src=\"https://pribormaster.ru/image/catalog/ll_cdek/logo.png\" / > Посылка склад-дверь из Москва в Благовещенская (6-9 раб. дней)"), 102));
		System.out.println("*" + deliveryAddressText + "*");
		
		//ll_cdek.ll_cdek_234
		deliveryAddressText = StringUtils.trim(StringUtils.substring(StringUtils.defaultString("<img src=\"https://pribormaster.ru/image/catalog/ll_cdek/logo.png\" / > Экономичная посылка склад-склад из Москва в Калининград, ул. Судостроительная, 17В (9-11 раб. дней)"), 114));
		System.out.println("*" + deliveryAddressText + "*");		
		
		//ll_cdek.ll_cdek_233
		deliveryAddressText = StringUtils.trim(StringUtils.substring(StringUtils.defaultString("<img src=\"https://pribormaster.ru/image/catalog/ll_cdek/logo.png\" / > Экономичная посылка склад-дверь из Москва в Красноярск (6-7 раб. дней)"), 114));
		System.out.println("*" + deliveryAddressText + "*");
	
	}
	
	
	
	@Test
	public void testNumberToPhrase() {
		
		logger.debug("testNumberToPhrase()");
		
		BigDecimal value;		
		value = BigDecimal.valueOf(1234567);		
		assertEquals("Один миллион двести тридцать четыре тысячи пятьсот шестьдесят семь", TextUtils.numberToPhrase(value));		
	}
	
	@Test
	public void testRoublesToPhrase() {
		
		logger.debug("testRoublesToPhrase()");
		
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
		city = TextUtils.cutCityFromAddress("Санкт-Петербург,ул. Адмирала Трибуца, д.10");
		assertEquals("Санкт-Петербург", city);
		
		
	}
	
	@Test
	public void testCutStreetFromAddress() {
		String street;
		street = TextUtils.cutStreetFromAddress("Владимир, пр-т Строителей, 15");
		assertEquals("пр-т Строителей, 15", street);
		
		street = TextUtils.cutStreetFromAddress("Санкт-Петербург, ул. Адмирала Трибуца, д.10");
		assertEquals("ул. Адмирала Трибуца, д.10", street);
		
		street = TextUtils.cutStreetFromAddress("Санкт-Петербург,ул. Адмирала Трибуца, д.10");
		assertEquals("ул. Адмирала Трибуца, д.10", street);
	}
	
	@Test
	public void testFormatPhoneNumber() {
		String s = "+79057809641";
		String r = TextUtils.formatPhoneNumber(s);
		assertEquals("(905) 780-96-41", r);
		
		s= "89177962825";
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
		
		
		
	}
	
	public void testString() {
		//sms: отправлено, статус: успешно | email: не отправлено |
		String str = "sms: отправлено, статус: успешно | email: не отправлено | ";
		str = str.substring(0, str.length() - 3);
		System.out.println(str);
		
		str = "sms: отправлено, статус: успешно | ";
		str = str.substring(0, str.length() - 3);
		System.out.println(str);
	
		/*
		str = "";		
		str = str.substring(0, str.length() - 3);
		System.out.println(str);
		*/
		str = "sms: отправлено, статус: успешно |";
		str = str.substring(0, str.length() - 2);
		System.out.println(str);
	}
	
	@Test
	public void testEscapeQuotes() {
		String so = "\"hope\"";
		String sn = TextUtils.escapingQuotes(so);
		System.out.println(so);
		System.out.println(sn);
	}

}
