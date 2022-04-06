package ru.sir.richard.boss.model.data;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;

import org.junit.Test;

import ru.sir.richard.boss.model.types.OrderStatuses;
import ru.sir.richard.boss.model.types.OrderTypes;
import ru.sir.richard.boss.model.types.PaymentTypes;

public class OrderTest {
	
	@Test
	public void calcBillAmount() {
		
		BigDecimal billAmount;
		BigDecimal marginAmount;
		BigDecimal postpayAmount;
						
		Order order = new Order();		
		order.getAmounts().setBill(BigDecimal.valueOf(100));
		order.getAmounts().setMargin(BigDecimal.valueOf(200));
		order.getAmounts().setPostpay(BigDecimal.valueOf(300));
		
		// постоплата, физик, заявка
		billAmount = BigDecimal.ZERO;
		marginAmount = BigDecimal.ZERO;
		postpayAmount = BigDecimal.ZERO;
		
		order.setOrderType(OrderTypes.ORDER);		
		order.setStatus(OrderStatuses.BID);
		order.setPaymentType(PaymentTypes.POSTPAY);
		
		if (order.isBillAmount()) {
			billAmount = order.getAmounts().getBill();
			marginAmount = order.getAmounts().getMargin();			
		}
		if (order.isPostpayAmount()) {
			postpayAmount = order.getAmounts().getPostpay();
		}	
		assertEquals(new BigDecimal("0"), billAmount); 
		assertEquals(new BigDecimal("0"), marginAmount);
		assertEquals(new BigDecimal("0"), postpayAmount);
		
		// постоплата, физик, подтвержден
		billAmount = BigDecimal.ZERO;
		marginAmount = BigDecimal.ZERO;
		postpayAmount = BigDecimal.ZERO;	
		order.setStatus(OrderStatuses.APPROVED);
		if (order.isBillAmount()) {
			billAmount = order.getAmounts().getBill();
			marginAmount = order.getAmounts().getMargin();			
		}
		if (order.isPostpayAmount()) {
			postpayAmount = order.getAmounts().getPostpay();
		}	
		assertEquals(new BigDecimal("100"), billAmount); 
		assertEquals(new BigDecimal("200"), marginAmount);
		assertEquals(new BigDecimal("300"), postpayAmount);
		
		// постоплата, физик, доставляется
		billAmount = BigDecimal.ZERO;
		marginAmount = BigDecimal.ZERO;
		postpayAmount = BigDecimal.ZERO;	
		order.setStatus(OrderStatuses.DELIVERING);
		if (order.isBillAmount()) {
			billAmount = order.getAmounts().getBill();
			marginAmount = order.getAmounts().getMargin();			
		}
		if (order.isPostpayAmount()) {
			postpayAmount = order.getAmounts().getPostpay();
		}	
		assertEquals(new BigDecimal("100"), billAmount); 
		assertEquals(new BigDecimal("200"), marginAmount);
		assertEquals(new BigDecimal("300"), postpayAmount);
		
		// постоплата, физик, ожидает получения
		billAmount = BigDecimal.ZERO;
		marginAmount = BigDecimal.ZERO;
		postpayAmount = BigDecimal.ZERO;
		order.setStatus(OrderStatuses.READY_GIVE_AWAY);
				
		if (order.isBillAmount()) {
			billAmount = order.getAmounts().getBill();
			marginAmount = order.getAmounts().getMargin();			
		}
		if (order.isPostpayAmount()) {
			postpayAmount = order.getAmounts().getPostpay();
		}	
		assertEquals(new BigDecimal("100"), billAmount); 
		assertEquals(new BigDecimal("200"), marginAmount);
		assertEquals(new BigDecimal("300"), postpayAmount);
				
		// постоплата, физик, доставлен
		billAmount = BigDecimal.ZERO;
		marginAmount = BigDecimal.ZERO;
		postpayAmount = BigDecimal.ZERO;	
		order.setStatus(OrderStatuses.DELIVERED);
		if (order.isBillAmount()) {
			billAmount = order.getAmounts().getBill();
			marginAmount = order.getAmounts().getMargin();			
		}
		if (order.isPostpayAmount()) {
			postpayAmount = order.getAmounts().getPostpay();
		}	
		assertEquals(new BigDecimal("100"), billAmount); 
		assertEquals(new BigDecimal("200"), marginAmount);
		assertEquals(new BigDecimal("300"), postpayAmount);
		
		// постоплата, физик, завершен
		billAmount = BigDecimal.ZERO;
		marginAmount = BigDecimal.ZERO;
		postpayAmount = BigDecimal.ZERO;
		order.setStatus(OrderStatuses.FINISHED);
		if (order.isBillAmount()) {
			billAmount = order.getAmounts().getBill();
			marginAmount = order.getAmounts().getMargin();			
		}
		if (order.isPostpayAmount()) {
			postpayAmount = order.getAmounts().getPostpay();
		}	
		assertEquals(new BigDecimal("100"), billAmount); 
		assertEquals(new BigDecimal("200"), marginAmount);
		assertEquals(new BigDecimal("0"), postpayAmount);

		// постоплата, физик, отказ от вручения
		billAmount = BigDecimal.ZERO;
		marginAmount = BigDecimal.ZERO;
		postpayAmount = BigDecimal.ZERO;
		order.setStatus(OrderStatuses.REDELIVERY);
		if (order.isBillAmount()) {
			billAmount = order.getAmounts().getBill();
			marginAmount = order.getAmounts().getMargin();			
		}
		if (order.isPostpayAmount()) {
			postpayAmount = order.getAmounts().getPostpay();
		}	
		assertEquals(new BigDecimal("0"), billAmount); 
		assertEquals(new BigDecimal("0"), marginAmount);
		assertEquals(new BigDecimal("300"), postpayAmount);
		
		// постоплата, физик, возврат получен
		billAmount = BigDecimal.ZERO;
		marginAmount = BigDecimal.ZERO;
		postpayAmount = BigDecimal.ZERO;
		order.setStatus(OrderStatuses.REDELIVERY_FINISHED);
		if (order.isBillAmount()) {
			billAmount = order.getAmounts().getBill();
			marginAmount = order.getAmounts().getMargin();			
		}
		if (order.isPostpayAmount()) {
			postpayAmount = order.getAmounts().getPostpay();
		}	
		assertEquals(new BigDecimal("0"), billAmount); 
		assertEquals(new BigDecimal("0"), marginAmount);
		assertEquals(new BigDecimal("0"), postpayAmount);
		
		// постоплата, физик, отменен
		billAmount = BigDecimal.ZERO;
		marginAmount = BigDecimal.ZERO;
		postpayAmount = BigDecimal.ZERO;
		order.setStatus(OrderStatuses.CANCELED);
		if (order.isBillAmount()) {
			billAmount = order.getAmounts().getBill();
			marginAmount = order.getAmounts().getMargin();			
		}
		if (order.isPostpayAmount()) {
			postpayAmount = order.getAmounts().getPostpay();
		}	
		assertEquals(new BigDecimal("0"), billAmount); 
		assertEquals(new BigDecimal("0"), marginAmount);
		assertEquals(new BigDecimal("0"), postpayAmount);
		
		// постоплата, юрик, заявка
		billAmount = BigDecimal.ZERO;
		marginAmount = BigDecimal.ZERO;
		postpayAmount = BigDecimal.ZERO;		
		
		order.setOrderType(OrderTypes.BILL);		
		order.setStatus(OrderStatuses.BID);
		order.setPaymentType(PaymentTypes.POSTPAY);
		if (order.isBillAmount()) {
			billAmount = order.getAmounts().getBill();
			marginAmount = order.getAmounts().getMargin();			
		}
		if (order.isPostpayAmount()) {
			postpayAmount = order.getAmounts().getPostpay();
		}	
		assertEquals(new BigDecimal("0"), billAmount); 
		assertEquals(new BigDecimal("0"), marginAmount);
		assertEquals(new BigDecimal("0"), postpayAmount);
	
		// постоплата, юрик, подтвержден
		billAmount = BigDecimal.ZERO;
		marginAmount = BigDecimal.ZERO;
		postpayAmount = BigDecimal.ZERO;	
		
		order.setOrderType(OrderTypes.BILL);		
		order.setStatus(OrderStatuses.APPROVED);
		order.setPaymentType(PaymentTypes.POSTPAY);
		if (order.isBillAmount()) {
			billAmount = order.getAmounts().getBill();
			marginAmount = order.getAmounts().getMargin();			
		}
		if (order.isPostpayAmount()) {
			postpayAmount = order.getAmounts().getPostpay();
		}	
		assertEquals(new BigDecimal("100"), billAmount); 
		assertEquals(new BigDecimal("200"), marginAmount);
		assertEquals(new BigDecimal("300"), postpayAmount);
		
		// постоплата, юрик, оплачено
		billAmount = BigDecimal.ZERO;
		marginAmount = BigDecimal.ZERO;
		postpayAmount = BigDecimal.ZERO;	
		
		order.setOrderType(OrderTypes.BILL);		
		order.setStatus(OrderStatuses.PAY_ON);
		order.setPaymentType(PaymentTypes.POSTPAY);
		if (order.isBillAmount()) {
			billAmount = order.getAmounts().getBill();
			marginAmount = order.getAmounts().getMargin();			
		}
		if (order.isPostpayAmount()) {
			postpayAmount = order.getAmounts().getPostpay();
		}	
		assertEquals(new BigDecimal("100"), billAmount); 
		assertEquals(new BigDecimal("200"), marginAmount);
		assertEquals(new BigDecimal("0"), postpayAmount);
		
		// постоплата, юрик, оплачено
		billAmount = BigDecimal.ZERO;
		marginAmount = BigDecimal.ZERO;
		postpayAmount = BigDecimal.ZERO;	
		
		order.setOrderType(OrderTypes.BILL);		
		order.setStatus(OrderStatuses.CANCELED);
		order.setPaymentType(PaymentTypes.POSTPAY);
		if (order.isBillAmount()) {
			billAmount = order.getAmounts().getBill();
			marginAmount = order.getAmounts().getMargin();			
		}
		if (order.isPostpayAmount()) {
			postpayAmount = order.getAmounts().getPostpay();
		}	
		assertEquals(new BigDecimal("0"), billAmount); 
		assertEquals(new BigDecimal("0"), marginAmount);
		assertEquals(new BigDecimal("0"), postpayAmount);
						
		// предоплата, юрик, подтвержден
		billAmount = BigDecimal.ZERO;
		marginAmount = BigDecimal.ZERO;
		postpayAmount = BigDecimal.ZERO;	
		
		order.setOrderType(OrderTypes.BILL);		
		order.setStatus(OrderStatuses.APPROVED);
		order.setPaymentType(PaymentTypes.PREPAYMENT);
		if (order.isBillAmount()) {
			billAmount = order.getAmounts().getBill();
			marginAmount = order.getAmounts().getMargin();			
		}
		if (order.isPostpayAmount()) {
			postpayAmount = order.getAmounts().getPostpay();
		}	
		assertEquals(new BigDecimal("0"), billAmount); 
		assertEquals(new BigDecimal("0"), marginAmount);
		assertEquals(new BigDecimal("0"), postpayAmount);
		
		// предоплата, юрик, ожидаем оплату
		billAmount = BigDecimal.ZERO;
		marginAmount = BigDecimal.ZERO;
		postpayAmount = BigDecimal.ZERO;	
		
		order.setOrderType(OrderTypes.BILL);		
		order.setStatus(OrderStatuses.PAY_WAITING);
		order.setPaymentType(PaymentTypes.PREPAYMENT);
		if (order.isBillAmount()) {
			billAmount = order.getAmounts().getBill();
			marginAmount = order.getAmounts().getMargin();			
		}
		if (order.isPostpayAmount()) {
			postpayAmount = order.getAmounts().getPostpay();
		}	
		assertEquals(new BigDecimal("0"), billAmount); 
		assertEquals(new BigDecimal("0"), marginAmount);
		assertEquals(new BigDecimal("0"), postpayAmount);
		
		// предоплата, юрик, оплата получена
		billAmount = BigDecimal.ZERO;
		marginAmount = BigDecimal.ZERO;
		postpayAmount = BigDecimal.ZERO;	
		
		order.setOrderType(OrderTypes.BILL);		
		order.setStatus(OrderStatuses.PAY_ON);
		order.setPaymentType(PaymentTypes.PREPAYMENT);
		if (order.isBillAmount()) {
			billAmount = order.getAmounts().getBill();
			marginAmount = order.getAmounts().getMargin();			
		}
		if (order.isPostpayAmount()) {
			postpayAmount = order.getAmounts().getPostpay();
		}	
		assertEquals(new BigDecimal("100"), billAmount); 
		assertEquals(new BigDecimal("200"), marginAmount);
		assertEquals(new BigDecimal("0"), postpayAmount);
		
		// предоплата, юрик, доставляется
		billAmount = BigDecimal.ZERO;
		marginAmount = BigDecimal.ZERO;
		postpayAmount = BigDecimal.ZERO;	
		
		order.setOrderType(OrderTypes.BILL);		
		order.setStatus(OrderStatuses.DELIVERING);
		order.setPaymentType(PaymentTypes.PREPAYMENT);
		if (order.isBillAmount()) {
			billAmount = order.getAmounts().getBill();
			marginAmount = order.getAmounts().getMargin();			
		}
		if (order.isPostpayAmount()) {
			postpayAmount = order.getAmounts().getPostpay();
		}	
		assertEquals(new BigDecimal("100"), billAmount); 
		assertEquals(new BigDecimal("200"), marginAmount);
		assertEquals(new BigDecimal("0"), postpayAmount);
		
		// предоплата, юрик, доставлен
		billAmount = BigDecimal.ZERO;
		marginAmount = BigDecimal.ZERO;
		postpayAmount = BigDecimal.ZERO;	
		
		order.setOrderType(OrderTypes.BILL);		
		order.setStatus(OrderStatuses.DELIVERED);
		order.setPaymentType(PaymentTypes.PREPAYMENT);
		if (order.isBillAmount()) {
			billAmount = order.getAmounts().getBill();
			marginAmount = order.getAmounts().getMargin();			
		}
		if (order.isPostpayAmount()) {
			postpayAmount = order.getAmounts().getPostpay();
		}	
		assertEquals(new BigDecimal("100"), billAmount); 
		assertEquals(new BigDecimal("200"), marginAmount);
		assertEquals(new BigDecimal("0"), postpayAmount);
		
		// предоплата, юрик, завершен
		billAmount = BigDecimal.ZERO;
		marginAmount = BigDecimal.ZERO;
		postpayAmount = BigDecimal.ZERO;	
		
		order.setOrderType(OrderTypes.BILL);		
		order.setStatus(OrderStatuses.FINISHED);
		order.setPaymentType(PaymentTypes.PREPAYMENT);
		if (order.isBillAmount()) {
			billAmount = order.getAmounts().getBill();
			marginAmount = order.getAmounts().getMargin();			
		}
		if (order.isPostpayAmount()) {
			postpayAmount = order.getAmounts().getPostpay();
		}	
		assertEquals(new BigDecimal("100"), billAmount); 
		assertEquals(new BigDecimal("200"), marginAmount);
		assertEquals(new BigDecimal("0"), postpayAmount);
		
		// предоплата, юрик, отменен
		billAmount = BigDecimal.ZERO;
		marginAmount = BigDecimal.ZERO;
		postpayAmount = BigDecimal.ZERO;	
		
		order.setOrderType(OrderTypes.BILL);		
		order.setStatus(OrderStatuses.CANCELED);
		order.setPaymentType(PaymentTypes.PREPAYMENT);
		if (order.isBillAmount()) {
			billAmount = order.getAmounts().getBill();
			marginAmount = order.getAmounts().getMargin();			
		}
		if (order.isPostpayAmount()) {
			postpayAmount = order.getAmounts().getPostpay();
		}	
		assertEquals(new BigDecimal("0"), billAmount); 
		assertEquals(new BigDecimal("0"), marginAmount);
		assertEquals(new BigDecimal("0"), postpayAmount);	
		
	}

}
