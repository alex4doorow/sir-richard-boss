package ru.sir.richard.boss.model.calc;

import java.math.BigDecimal;
import java.util.Map;

import ru.sir.richard.boss.model.data.Order;
import ru.sir.richard.boss.model.data.OrderAmounts;
import ru.sir.richard.boss.model.types.OrderAmountTypes;

public class OwnCourierOrderTotalAmountsCalculator extends AnyOrderTotalAmountsCalculator {

	public OwnCourierOrderTotalAmountsCalculator(Order order) {
		super(order);
	}

	@Override
	public OrderAmounts calc() {
		BigDecimal deliveryPrice = order.getDelivery().getCustomerPrice();
		
		Map<OrderAmountTypes, BigDecimal> subTotals = calcSubTotals(order.getItems());
		
		BigDecimal bill = subTotals.get(OrderAmountTypes.BILL).add(deliveryPrice);		
		BigDecimal supplier = subTotals.get(OrderAmountTypes.SUPPLIER);
		BigDecimal total = BigDecimal.ZERO.add(bill);	
		
		BigDecimal margin = bill.subtract(supplier);				
		BigDecimal postpay = BigDecimal.ZERO;
			
		orderAmounts.setValue(OrderAmountTypes.BILL, bill);
		orderAmounts.setValue(OrderAmountTypes.TOTAL_WITH_DELIVERY, total);
		orderAmounts.setValue(OrderAmountTypes.MARGIN, margin);
		orderAmounts.setValue(OrderAmountTypes.SUPPLIER, supplier);
		orderAmounts.setValue(OrderAmountTypes.POSTPAY, postpay);
				
		return orderAmounts;
	}

}
