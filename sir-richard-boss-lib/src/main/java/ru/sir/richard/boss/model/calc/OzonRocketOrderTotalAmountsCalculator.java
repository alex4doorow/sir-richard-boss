package ru.sir.richard.boss.model.calc;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

import ru.sir.richard.boss.model.data.Order;
import ru.sir.richard.boss.model.data.OrderAmounts;
import ru.sir.richard.boss.model.types.OrderAmountTypes;
import ru.sir.richard.boss.model.types.PaymentTypes;

public class OzonRocketOrderTotalAmountsCalculator extends AnyOrderTotalAmountsCalculator {

	public OzonRocketOrderTotalAmountsCalculator(Order order) {
		super(order);
	}

	@Override
	public OrderAmounts calc() {
				
		BigDecimal deliveryCustomerPrice = order.getDelivery().getFactCustomerPrice();
		deliveryCustomerPrice = deliveryCustomerPrice == null ? BigDecimal.ZERO : deliveryCustomerPrice;

		BigDecimal deliverySellerPrice = order.getDelivery().getFactSellerPrice();				
		deliverySellerPrice = deliverySellerPrice == null ? BigDecimal.ZERO : deliverySellerPrice;

		BigDecimal deltaDeliveryPrice = deliveryCustomerPrice.subtract(deliverySellerPrice);
						
		Map<OrderAmountTypes, BigDecimal> subTotals = calcSubTotals(order.getItems());		
		
		BigDecimal bill = subTotals.get(OrderAmountTypes.BILL);		
		BigDecimal supplier = subTotals.get(OrderAmountTypes.SUPPLIER);		
		
		BigDecimal total = BigDecimal.ZERO.add(bill);		
		BigDecimal totalWithDelivery = bill.add(deliveryCustomerPrice);		
		
		BigDecimal margin = bill.subtract(supplier).add(deltaDeliveryPrice);
		margin = margin.subtract(margin.multiply(new BigDecimal("0.15")));
		margin = margin.setScale(2, RoundingMode.HALF_UP); 
		
		BigDecimal postpay = BigDecimal.ZERO;
		if (order.getPaymentType() == PaymentTypes.POSTPAY) {
			postpay = totalWithDelivery.subtract(deliverySellerPrice);
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
