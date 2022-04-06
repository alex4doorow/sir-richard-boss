package ru.sir.richard.boss.model.calc;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.sir.richard.boss.model.data.Order;
import ru.sir.richard.boss.model.data.OrderAmounts;
import ru.sir.richard.boss.model.data.OrderItem;
import ru.sir.richard.boss.model.types.OrderAmountTypes;

public abstract class AnyOrderTotalAmountsCalculator {
		
	protected final Order order;
	protected OrderAmounts orderAmounts;
	
	public AnyOrderTotalAmountsCalculator(Order order) {
		this.order = order;		
		this.orderAmounts = new OrderAmounts(order);
	}
	
	public abstract OrderAmounts calc();
	
	protected Map<OrderAmountTypes, BigDecimal> calcSubTotals(List<OrderItem> orderItems) {
		Map<OrderAmountTypes, BigDecimal> result = new HashMap<OrderAmountTypes, BigDecimal>();
		
		BigDecimal bill = BigDecimal.ZERO;
		BigDecimal supplier = BigDecimal.ZERO;
		for (OrderItem item : orderItems) {
			if (item.getAmount() != null) {
				bill = bill.add(item.getAmount());
			}	
			if (item.getSupplierAmount() != null) {
				supplier = supplier.add(item.getSupplierAmount().multiply(BigDecimal.valueOf(item.getQuantity())));	
			}
		}
		result.put(OrderAmountTypes.BILL, bill);
		result.put(OrderAmountTypes.SUPPLIER, supplier);
		
		BigDecimal deliveryPrice = order.getDelivery().getPrice();	
		if (deliveryPrice == null) {
			deliveryPrice = BigDecimal.ZERO;
		}
		setUnionAmounts(bill, deliveryPrice);		
		return result;
		
	}
	
	private void setUnionAmounts(BigDecimal total, BigDecimal deliveryPrice) {
		orderAmounts.setValue(OrderAmountTypes.TOTAL, total);
		orderAmounts.setValue(OrderAmountTypes.DELIVERY, deliveryPrice);
	}

}
