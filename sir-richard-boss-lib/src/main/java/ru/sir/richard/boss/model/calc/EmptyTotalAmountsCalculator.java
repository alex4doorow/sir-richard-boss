package ru.sir.richard.boss.model.calc;

import java.math.BigDecimal;

import ru.sir.richard.boss.model.data.Order;
import ru.sir.richard.boss.model.data.OrderAmounts;
import ru.sir.richard.boss.model.types.OrderAmountTypes;

public class EmptyTotalAmountsCalculator extends AnyOrderTotalAmountsCalculator {
	
	public EmptyTotalAmountsCalculator(Order order) {
		super(order);
	}

	@Override
	public OrderAmounts calc() {
		
		orderAmounts.setValue(OrderAmountTypes.BILL, BigDecimal.ZERO);
		orderAmounts.setValue(OrderAmountTypes.TOTAL_WITH_DELIVERY, BigDecimal.ZERO);
		orderAmounts.setValue(OrderAmountTypes.MARGIN, BigDecimal.ZERO);
		orderAmounts.setValue(OrderAmountTypes.SUPPLIER, BigDecimal.ZERO);
		orderAmounts.setValue(OrderAmountTypes.POSTPAY, BigDecimal.ZERO);				
		return orderAmounts;
	}

}
