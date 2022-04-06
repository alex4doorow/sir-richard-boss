package ru.sir.richard.boss.api.cdek;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.text.ParseException;

import ru.sir.richard.boss.model.data.crm.DeliveryServiceResult;
import ru.sir.richard.boss.model.utils.DateTimeUtils;
import ru.sir.richard.boss.model.utils.NumberUtils;

public class CdekApiTest {
		
	//@Test
	public void testBatayskCourier() throws ParseException, Exception {
		
		CdekApi cdek = new CdekApi();
		
		int tariffId = 137; // курьер		
		int receiverCityId = 1072; // батайск
		DeliveryServiceResult result;
		
		result = cdek.calculate(BigDecimal.valueOf(1), DateTimeUtils.defaultFormatStringToDate("22.06.2019"), 
				new BigDecimal("4250"), tariffId, receiverCityId, true, false);
		
		assertEquals(new BigDecimal("350"), result.getDeliveryPrice());
		assertEquals(new BigDecimal("31.88"), result.getDeliveryInsurance());
		assertEquals("140,00", NumberUtils.defaultFormatNumber(result.getDeliveryPostpayFee()));
		
		assertEquals("530,00", NumberUtils.defaultFormatNumber(result.getDeliverySellerSummary()));
		assertEquals("390,00", NumberUtils.defaultFormatNumber(result.getDeliveryCustomerSummary()));
		
		assertEquals("4118,12", NumberUtils.formatNumber(result.getPostpayAmount(), "#0.00"));
				
		result = cdek.calculate(BigDecimal.valueOf(1), DateTimeUtils.defaultFormatStringToDate("22.06.2019"), 
				new BigDecimal("4250"), tariffId, receiverCityId, false, false);
		
		assertEquals(new BigDecimal("350"), result.getDeliveryPrice());
		assertEquals(new BigDecimal("31.88"), result.getDeliveryInsurance());
		assertEquals(new BigDecimal("0"), result.getDeliveryPostpayFee());
		assertEquals(new BigDecimal("382"), result.getDeliverySellerSummary());
		assertEquals("381,88", NumberUtils.formatNumber(result.getDeliveryCustomerSummary(), "#0.00"));
		assertEquals(new BigDecimal("0"), result.getPostpayAmount());
	
	}
	
	//@Test
	public void testMoscowPvz() throws ParseException, Exception {
		
		CdekApi cdek = new CdekApi();
		
		int tariffId = 136; // пункт выдачи		
		int receiverCityId = 44; // москва
		DeliveryServiceResult result;
		
		result = cdek.calculate(BigDecimal.valueOf(1), DateTimeUtils.defaultFormatStringToDate("22.06.2019"), 
				new BigDecimal("4250"), tariffId, receiverCityId, true, true);

		assertEquals(new BigDecimal("135"), result.getDeliveryPrice());
		assertEquals(new BigDecimal("31.88"), result.getDeliveryInsurance());
		assertEquals("140,00", NumberUtils.formatNumber(result.getDeliveryPostpayFee(), "#0.00"));
		assertEquals("300,00", NumberUtils.formatNumber(result.getDeliverySellerSummary(), "#0.00"));
		assertEquals(new BigDecimal("0"), result.getDeliveryCustomerSummary());		
		assertEquals("3943,12", NumberUtils.formatNumber(result.getPostpayAmount(), "#0.00"));

		
	}
	

}
