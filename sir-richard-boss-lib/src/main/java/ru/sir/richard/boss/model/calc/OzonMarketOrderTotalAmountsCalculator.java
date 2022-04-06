package ru.sir.richard.boss.model.calc;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

import ru.sir.richard.boss.model.data.Order;
import ru.sir.richard.boss.model.data.OrderAmounts;
import ru.sir.richard.boss.model.types.OrderAmountTypes;
import ru.sir.richard.boss.model.types.PaymentTypes;

public class OzonMarketOrderTotalAmountsCalculator extends AnyOrderTotalAmountsCalculator {
	
	
	public OzonMarketOrderTotalAmountsCalculator(Order order) {
		super(order);
	}

	@Override
	public OrderAmounts calc() {
		
		BigDecimal deliveryCustomerPrice = BigDecimal.ZERO;		
		Map<OrderAmountTypes, BigDecimal> subTotals = calcSubTotals(order.getItems());		
		
		BigDecimal bill = subTotals.get(OrderAmountTypes.BILL);		
		BigDecimal supplier = subTotals.get(OrderAmountTypes.SUPPLIER);		
		
		BigDecimal total = BigDecimal.ZERO.add(bill);		
		BigDecimal totalWithDelivery = bill.add(deliveryCustomerPrice);
		
		// октябрь 2021 9,6%
		// ноябрь 2021 10,25%		
		BigDecimal ozonFee = bill.multiply(new BigDecimal("0.10"));
					
		BigDecimal margin = bill.subtract(supplier).subtract(ozonFee);
		margin = margin.subtract(margin.multiply(new BigDecimal("0.15")));
		margin = margin.setScale(2, RoundingMode.HALF_UP); 
		
		BigDecimal postpay = BigDecimal.ZERO;
		if (order.getPaymentType() == PaymentTypes.POSTPAY) {
			postpay = totalWithDelivery;
		} 	
		orderAmounts.setValue(OrderAmountTypes.DELIVERY, deliveryCustomerPrice);
		orderAmounts.setValue(OrderAmountTypes.BILL, bill);
		orderAmounts.setValue(OrderAmountTypes.TOTAL_WITH_DELIVERY, totalWithDelivery);
		orderAmounts.setValue(OrderAmountTypes.TOTAL, total);
		orderAmounts.setValue(OrderAmountTypes.MARGIN, margin);
		orderAmounts.setValue(OrderAmountTypes.SUPPLIER, supplier);
		orderAmounts.setValue(OrderAmountTypes.POSTPAY, postpay);
				
		return orderAmounts;
	
	
	}
	

}
