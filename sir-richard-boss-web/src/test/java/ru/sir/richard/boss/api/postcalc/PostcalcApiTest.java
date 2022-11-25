package ru.sir.richard.boss.api.postcalc;

import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.PropertyResolver;

import ru.sir.richard.boss.model.data.Address;

@ExtendWith(MockitoExtension.class)
public class PostcalcApiTest {
	
	@Mock
	private PropertyResolver propertyResolverMock;
		
	@BeforeEach
	public void beforeEach() {
		/*
		Mockito.when(propertyResolverMock.getProperty("postcalc.url")).thenReturn("http://api.postcalc.ru");
		Mockito.when(propertyResolverMock.getProperty("postcalc.from.index")).thenReturn("107241");
		*/	
	}
	
	@Test
	public void testPrepayment() throws IOException {
		
		Address to = new Address();
		to.setAddress("164170, Архангельская обл., Мирный г. ул. Циргвава, д. 3, кв. 75");		
		PostcalcApi api = new PostcalcApi(propertyResolverMock);
		
		/*
		DeliveryServiceResult result = api.postCalc(300, DateTimeUtils.sysDate(), BigDecimal.valueOf(1000L), 
				PostcalcApi.getParcelDataName(DeliveryTypes.POST_I_CLASS), to, 
				PostcalcApi.getSv(PaymentTypes.PREPAYMENT));		
		assertEquals(new BigDecimal("516"), result.getDeliveryAmount());
				
		result = api.postCalc(300, DateTimeUtils.sysDate(), BigDecimal.valueOf(1000L), 
				PostcalcApi.getParcelDataName(DeliveryTypes.POST_I_CLASS), to, 
				PostcalcApi.getSv(PaymentTypes.POSTPAY));		
		assertEquals(new BigDecimal("534"), result.getDeliveryAmount());
		*/
		
	}
	
	@Test
	public void testPgtSeverny() throws IOException {

		Address to = new Address();
		to.setAddress("308519, Белгородская обл. → Белгородский р-н → Северный пгт, ул. Гагарина, д. 10");    		
		PostcalcApi api = new PostcalcApi(propertyResolverMock);
		
		/*
		DeliveryServiceResult result = api.postCalc(100, DateTimeUtils.sysDate(), BigDecimal.valueOf(500L), 
				PostcalcApi.getParcelDataName(DeliveryTypes.POST_I_CLASS), to, 
				PostcalcApi.getSv(PaymentTypes.POSTPAY));
				
		assertEquals(new BigDecimal("793"), result.getPostpayAmount());
		assertEquals(new BigDecimal("293"), result.getDeliveryAmount());
		assertEquals(new BigDecimal("293"), result.getDeliverySellerSummary());
		assertEquals(new BigDecimal("293"), result.getDeliveryCustomerSummary());
		*/		
	}
}
