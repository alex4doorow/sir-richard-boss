package ru.sir.richard.boss.api.postcalc;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.math.BigDecimal;

import org.junit.Test;

import ru.sir.richard.boss.model.data.Address;
import ru.sir.richard.boss.model.data.crm.DeliveryServiceResult;
import ru.sir.richard.boss.model.types.DeliveryTypes;
import ru.sir.richard.boss.model.types.PaymentTypes;
import ru.sir.richard.boss.model.utils.DateTimeUtils;

public class PostcalcApiTest {
	
	//@Test
	public void testPgtSeverny() throws IOException {
		
		DeliveryTypes type = DeliveryTypes.POST_I_CLASS;
		PaymentTypes paymentType = PaymentTypes.POSTPAY;
		Address to = new Address();
		to.setAddress("308519, Белгородская обл. → Белгородский р-н → Северный пгт, ул. Гагарина, д. 10");

		
		String iBase = paymentType == PaymentTypes.PREPAYMENT ? "p" : "f";		
		String parcelDataName = "";
		if (type == DeliveryTypes.POST_TYPICAL) {
			parcelDataName = "ЦеннаяПосылка";
	    } else if (type == DeliveryTypes.POST_EMS) {
	    	parcelDataName = "EMS";
	    } else if (type == DeliveryTypes.POST_I_CLASS) {
	    	parcelDataName = "Посылка1Класс";	
	    } else {
	    	parcelDataName = "";       
	    }	    
		
		PostcalcApi api = new PostcalcApi();
		DeliveryServiceResult result = api.postCalc(100, DateTimeUtils.sysDate(), BigDecimal.valueOf(500L), parcelDataName, to, iBase);
		
		
		assertEquals(new BigDecimal("758"), result.getPostpayAmount());
		assertEquals(new BigDecimal("258"), result.getDeliveryAmount());
		assertEquals(new BigDecimal("258"), result.getDeliverySellerSummary());
		assertEquals(new BigDecimal("258"), result.getDeliveryCustomerSummary());

		
	
		
	}

}
