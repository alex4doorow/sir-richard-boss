package ru.sir.richard.boss.converter.excel;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.sir.richard.boss.model.data.CompanyCustomer;
import ru.sir.richard.boss.model.data.OrderItem;
import ru.sir.richard.boss.model.data.Product;
import ru.sir.richard.boss.utils.ConverterUtils;

public class ConverterUtilsTest {
	

	private final Logger logger = LoggerFactory.getLogger(ConverterUtilsTest.class);
	
	//@Test
	public void testExtractPhoneNumberOfCompanyName() {
		
		String text;
		
		text = "Никита Сергеевич 8(846)302-05-05";		
		assertEquals("8(846)302-05-05", ConverterUtils.extractPhoneNumberOfText(text));
		
		text = "Федотов Владимир 8-(49241)-93-270";		
		assertEquals("8-(49241)-93-270", ConverterUtils.extractPhoneNumberOfText(text));
		
		text = "Можжухина Элла Юрьевна";		
		assertEquals("", ConverterUtils.extractPhoneNumberOfText(text));
		
		text = "Иван";		
		assertEquals("", ConverterUtils.extractPhoneNumberOfText(text));
		
		
		
	}
	 
	
	
	//@Test
	public void testSeparateCustomerCompanyName() {
		
		String text;
		String phoneNumber;
		String email;
		CompanyCustomer company;
		
		text = "ООО \"РЕСУРС-ТЕХНОПАРК\", Иван";
		phoneNumber = "(950) 905 56 00";
		email = "";
		company = customerCompanyNameSeparate2CompanyNameAndContact(text, phoneNumber, email);
		assertEquals("ООО \"РЕСУРС-ТЕХНОПАРК\"", company.getShortName());
		assertEquals("Иван", company.getMainContact().getFirstName());
		assertEquals("", company.getMainContact().getMiddleName());
		assertEquals("", company.getMainContact().getLastName());
		assertEquals("(950) 905 56 00", company.getMainContact().getPhoneNumber());
		assertEquals("", company.getMainContact().getEmail());
						
		text = "ООО\" Компания ИН СЕТ\", Никита Сергеевич 8(846)302-05-05, 89991722392";
		phoneNumber = "(999) 172-23-92";
		email = "ns.inset-s@bk.ru";
		company = customerCompanyNameSeparate2CompanyNameAndContact(text, phoneNumber, email);
		assertEquals( "ООО\" Компания ИН СЕТ\"", company.getShortName());
		assertEquals("Никита", company.getMainContact().getFirstName());
		assertEquals("", company.getMainContact().getMiddleName());
		assertEquals("Сергеевич", company.getMainContact().getLastName());
		assertEquals("8(846)302-05-05", company.getMainContact().getPhoneNumber());
		assertEquals("ns.inset-s@bk.ru", company.getMainContact().getEmail());
						
		text = "ОБЩЕСТВО С ОГРАНИЧЕННОЙ ОТВЕТСТВЕННОСТЬЮ \"Агропромышленная группа \"Молочный продукт\", Можжухина Элла Юрьевна";		
		phoneNumber = "(910) 623-05-39";
		email = "mojuhinaey@ra-group.ru";
		company = customerCompanyNameSeparate2CompanyNameAndContact(text, phoneNumber, email);
		assertEquals( "ОБЩЕСТВО С ОГРАНИЧЕННОЙ ОТВЕТСТВЕННОСТЬЮ \"Агропромышленная группа \"Молочный продукт\"", company.getShortName());
		assertEquals("Элла", company.getMainContact().getFirstName());
		assertEquals("Юрьевна", company.getMainContact().getMiddleName());
		assertEquals("Можжухина", company.getMainContact().getLastName());
		assertEquals("(910) 623-05-39", company.getMainContact().getPhoneNumber());
		assertEquals("mojuhinaey@ra-group.ru", company.getMainContact().getEmail());
		
		text = "ООО «Красное Эхо», Владимир Федотов 8-(49241)-93-270";
		phoneNumber = "";
		email = "vladimir.fedotov@red-echo.ru";
		company = customerCompanyNameSeparate2CompanyNameAndContact(text, phoneNumber, email);
		assertEquals( "ООО «Красное Эхо»", company.getShortName());
		assertEquals("Владимир", company.getMainContact().getFirstName());
		assertEquals("", company.getMainContact().getMiddleName());
		assertEquals("Федотов", company.getMainContact().getLastName());
		assertEquals("8-(49241)-93-270", company.getMainContact().getPhoneNumber());
		assertEquals("vladimir.fedotov@red-echo.ru", company.getMainContact().getEmail());
		
		text = "ООО \"СДЭК-Регион\", Дегтярева Светлана Валерьевна, получатель Мастюгина Елена (495) 665-58-99 доб. 4417  сот. (916) 295-17-00";
		phoneNumber = "(914) 690-68-89";
		email = "dieghtiarieva74@mail.ru";
		company = customerCompanyNameSeparate2CompanyNameAndContact(text, phoneNumber, email);
		assertEquals( "ООО \"СДЭК-Регион\"", company.getShortName());
		assertEquals("Светлана", company.getMainContact().getFirstName());
		assertEquals("Валерьевна", company.getMainContact().getMiddleName());
		assertEquals("Дегтярева", company.getMainContact().getLastName());
		assertEquals("(916) 295-17-00", company.getMainContact().getPhoneNumber());
		assertEquals("dieghtiarieva74@mail.ru", company.getMainContact().getEmail());
		
				
		//text = "БинБанк, ИП Черноусов, получатель Важин Сергей Александрович (960) 765-01-06";
		
		
	}
	
	private CompanyCustomer customerCompanyNameSeparate2CompanyNameAndContact(final String inputText, 
			String inputPhoneNumber, String inputEmail) {
		return ConverterUtils.customerCompanyNameSeparate2CompanyNameAndContact(inputText, 
				inputPhoneNumber, inputEmail);		
	}
	
	@Test
	public void testSeparateItems() {		
		logger.debug("testSeparateItems()");
		
		String text;
		int quantity;
		BigDecimal price;
		List<OrderItem> exceptItems;
		OrderItem oneExceptItem, twoExceptItem, threeExceptItem;
		
		List<OrderItem> actualItems;
				
		text = "iSocket 707"; quantity = 1; price = BigDecimal.valueOf(4790L);
		exceptItems = new ArrayList<OrderItem>();
		oneExceptItem = new OrderItem();
		oneExceptItem.setProduct(new Product(0, "iSocket 707"));
		oneExceptItem.setPrice(price);
		oneExceptItem.setQuantity(quantity);
		exceptItems.add(oneExceptItem);
		
		actualItems = productTextSeparate2OrderItems(text, quantity, price);		
		assertEquals(1, actualItems.size());
		assertEquals(exceptItems, actualItems);
		assertEquals("iSocket 707", actualItems.get(0).getProduct().getName());
		assertEquals(1, actualItems.get(0).getQuantity());
						
		text = "ГРАД А-550УЗ+ГРОМ ПРОФИ М 2шт"; quantity = 1; price = BigDecimal.valueOf(0);
		actualItems = productTextSeparate2OrderItems(text, quantity, price);
		assertEquals(2, actualItems.size());
		
		exceptItems = new ArrayList<OrderItem>();
		oneExceptItem = new OrderItem();
		oneExceptItem.setProduct(new Product(0, "ГРАД А-550УЗ"));		
		oneExceptItem.setQuantity(1);
		exceptItems.add(oneExceptItem);
		
		twoExceptItem = new OrderItem();
		twoExceptItem.setProduct(new Product(0, "ГРОМ ПРОФИ М"));		
		twoExceptItem.setQuantity(2);
		exceptItems.add(twoExceptItem);
		
		assertEquals(exceptItems, actualItems);
				
		text = "Отпугиватель змей ЭкоСнайпер LS-107 + батарейки";
		actualItems = productTextSeparate2OrderItems(text, quantity, price);
		assertEquals(2, actualItems.size());
		
		exceptItems = new ArrayList<OrderItem>();
		oneExceptItem = new OrderItem();
		oneExceptItem.setProduct(new Product(0, "Отпугиватель змей ЭкоСнайпер LS-107"));		
		oneExceptItem.setQuantity(1);
		exceptItems.add(oneExceptItem);
		
		twoExceptItem = new OrderItem();
		twoExceptItem.setProduct(new Product(0, "LR20"));		
		twoExceptItem.setQuantity(1);
		exceptItems.add(twoExceptItem);
		
		assertEquals(exceptItems, actualItems);
		
		text = "Набор запасной для отпугивателя комаров “ThermaCell” (большой) 1 шт + Чехол для противомоскитного прибора “ThermaCell” (оливковый) 1 шт";
		actualItems = productTextSeparate2OrderItems(text, quantity, price);		
		assertEquals(2, actualItems.size());		
		
		text = "Отпугиватель собак стационарный \"Weitech WK-0051\" 1 шт + батарейки 4 шт";
		actualItems = productTextSeparate2OrderItems(text, quantity, price);
		assertEquals(2, actualItems.size());
		exceptItems = new ArrayList<OrderItem>();
		oneExceptItem = new OrderItem();
		oneExceptItem.setProduct(new Product(0, "Отпугиватель собак стационарный \"Weitech WK-0051\""));		
		oneExceptItem.setQuantity(1);
		exceptItems.add(oneExceptItem);
		
		twoExceptItem = new OrderItem();
		twoExceptItem.setProduct(new Product(0, "LR20"));		
		twoExceptItem.setQuantity(4);
		exceptItems.add(twoExceptItem);
		
		assertEquals(exceptItems, actualItems);
		
		text = "Уничтожитель комаров универсальный \"Москито MV-01\" 1шт + Аттрактант-приманка \"SITITEK\" для уничтожителей комаров 2 шт + Дверная магнитная сетка ЭкоСнайпер Magic Mesh Buzz Off от комаров 1шт";
		actualItems = productTextSeparate2OrderItems(text, quantity, price);
		assertEquals(3, actualItems.size());
		exceptItems = new ArrayList<OrderItem>();
		oneExceptItem = new OrderItem();
		oneExceptItem.setProduct(new Product(0, "Уничтожитель комаров универсальный \"Москито MV-01\""));		
		oneExceptItem.setQuantity(1);
		exceptItems.add(oneExceptItem);
		
		twoExceptItem = new OrderItem();
		twoExceptItem.setProduct(new Product(0, "Аттрактант-приманка \"SITITEK\" для уничтожителей комаров"));		
		twoExceptItem.setQuantity(2);
		exceptItems.add(twoExceptItem);
		
		threeExceptItem = new OrderItem();
		threeExceptItem.setProduct(new Product(0, "Дверная магнитная сетка ЭкоСнайпер Magic Mesh Buzz Off от комаров"));		
		threeExceptItem.setQuantity(1);
		exceptItems.add(threeExceptItem);
		
		assertEquals(exceptItems, actualItems);
		
		//ГРАД А-1000 ПРО
		//LR20
		
	}
	
	private List<OrderItem> productTextSeparate2OrderItems(final String inputText, int quantity, BigDecimal price) {
		List<OrderItem> results = ConverterUtils.productTextSeparate2OrderItems(inputText, quantity, price);
		logger.debug("results:{}", results);
		return results;
	}
	
	

}
