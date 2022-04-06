package ru.sir.richard.boss.model.calc;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

import ru.sir.richard.boss.model.data.Order;
import ru.sir.richard.boss.model.data.OrderAmounts;
import ru.sir.richard.boss.model.types.OrderAmountTypes;
import ru.sir.richard.boss.model.types.PaymentTypes;

public class YandexMarketOrderTotalAmountsCalculator extends AnyOrderTotalAmountsCalculator {

	public YandexMarketOrderTotalAmountsCalculator(Order order) {
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
		
		// расчет ноября через услуги в разрезе статей дал: 6,74 + 1,85 + 4 + 2.62 + 1,75 = 16,96% 
		// расчет ноября через процент суммарный услуг к суммарному доходу дал 14,02%
		// но я забыл вознаграждение за продвижение. это сумма 41752р. процент составил 7%
		// TODO сделать аналогичный пересчет за декабрь
		
		// 11.12.21 расчет по отчету продаж за ноябрь: доход 633893 руб, комиссии (в т.ч. и по отмененным): 72712 руб, комиссия: 11,47%
		BigDecimal ymFee = bill.multiply(new BigDecimal("0.1147"));
					
		BigDecimal margin = bill.subtract(supplier).subtract(ymFee);
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
