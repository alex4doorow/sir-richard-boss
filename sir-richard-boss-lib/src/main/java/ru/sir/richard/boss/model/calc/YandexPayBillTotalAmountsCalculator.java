package ru.sir.richard.boss.model.calc;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

import ru.sir.richard.boss.model.data.Order;
import ru.sir.richard.boss.model.data.OrderAmounts;
import ru.sir.richard.boss.model.types.OrderAmountTypes;

public class YandexPayBillTotalAmountsCalculator extends AnyOrderTotalAmountsCalculator {

	public YandexPayBillTotalAmountsCalculator(Order order) {
		super(order);
	}

	@Override
	public OrderAmounts calc() {
					
		BigDecimal deliveryCustomerPrice = order.getDelivery().getFactCustomerPrice();
		BigDecimal deliverySellerPrice = order.getDelivery().getFactSellerPrice();	
		BigDecimal deltaDeliveryPrice = deliveryCustomerPrice.subtract(deliverySellerPrice);
		
		Map<OrderAmountTypes, BigDecimal> subTotals = calcSubTotals(order.getItems());		
		
		BigDecimal bill = subTotals.get(OrderAmountTypes.BILL);		
		BigDecimal supplier = subTotals.get(OrderAmountTypes.SUPPLIER);		
		
		orderAmounts.setValue(OrderAmountTypes.DELIVERY, deliveryCustomerPrice);
		
		BigDecimal total = BigDecimal.ZERO.add(bill);		
		BigDecimal totalWithDelivery = bill.add(deliveryCustomerPrice);		
		BigDecimal yookassaFee = total.multiply(new BigDecimal("3.5")).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
				
		BigDecimal margin = bill.add(deltaDeliveryPrice).subtract(supplier).subtract(yookassaFee);
		margin = margin.subtract(margin.multiply(new BigDecimal("0.15")));
		margin = margin.setScale(2, RoundingMode.HALF_UP); 
		
		BigDecimal postpay = order.getAmounts().getValue(OrderAmountTypes.POSTPAY);			
		orderAmounts.setValue(OrderAmountTypes.BILL, bill);
		orderAmounts.setValue(OrderAmountTypes.TOTAL_WITH_DELIVERY, totalWithDelivery);
		orderAmounts.setValue(OrderAmountTypes.TOTAL, total);
		orderAmounts.setValue(OrderAmountTypes.MARGIN, margin);
		orderAmounts.setValue(OrderAmountTypes.SUPPLIER, supplier);
		orderAmounts.setValue(OrderAmountTypes.POSTPAY, postpay);
				
		return orderAmounts;
	}

}
