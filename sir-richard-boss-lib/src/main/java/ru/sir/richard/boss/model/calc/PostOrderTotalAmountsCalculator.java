package ru.sir.richard.boss.model.calc;

import java.math.BigDecimal;
import java.util.Map;

import ru.sir.richard.boss.model.data.Order;
import ru.sir.richard.boss.model.data.OrderAmounts;
import ru.sir.richard.boss.model.types.OrderAmountTypes;
import ru.sir.richard.boss.model.types.PaymentTypes;

public class PostOrderTotalAmountsCalculator extends AnyOrderTotalAmountsCalculator {

	public PostOrderTotalAmountsCalculator(Order order) {
		super(order);
	}

	@Override
	public OrderAmounts calc() {
		
		Map<OrderAmountTypes, BigDecimal> subTotals = calcSubTotals(order.getItems());
		
		BigDecimal bill = subTotals.get(OrderAmountTypes.BILL);		
		BigDecimal supplier = subTotals.get(OrderAmountTypes.SUPPLIER);		
		BigDecimal margin = bill.subtract(supplier);
		
		BigDecimal postpay = BigDecimal.ZERO;
		BigDecimal total = BigDecimal.ZERO;
		if (order.getPaymentType() == PaymentTypes.POSTPAY) {
			total = order.getAmounts().getValue(OrderAmountTypes.POSTPAY);
			postpay = order.getAmounts().getValue(OrderAmountTypes.POSTPAY);			
		} else if (order.isPrepayment()) {
			BigDecimal deliveryPrice = order.getDelivery().getCustomerPrice();
			total = bill.add(deliveryPrice);
			postpay = order.getAmounts().getValue(OrderAmountTypes.POSTPAY);
		}				
		orderAmounts.setValue(OrderAmountTypes.BILL, bill);
		orderAmounts.setValue(OrderAmountTypes.TOTAL_WITH_DELIVERY, total);
		orderAmounts.setValue(OrderAmountTypes.MARGIN, margin);
		orderAmounts.setValue(OrderAmountTypes.SUPPLIER, supplier);
		orderAmounts.setValue(OrderAmountTypes.POSTPAY, postpay);
				
		return orderAmounts;
	}

}
