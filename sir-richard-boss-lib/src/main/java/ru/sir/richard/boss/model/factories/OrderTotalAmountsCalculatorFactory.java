package ru.sir.richard.boss.model.factories;

import ru.sir.richard.boss.model.calc.AnyOrderTotalAmountsCalculator;
import ru.sir.richard.boss.model.calc.BillTotalAmountsCalculator;
import ru.sir.richard.boss.model.calc.EmptyTotalAmountsCalculator;
import ru.sir.richard.boss.model.calc.OwnCourierOrderTotalAmountsCalculator;
import ru.sir.richard.boss.model.calc.OzonMarketOrderTotalAmountsCalculator;
import ru.sir.richard.boss.model.calc.OzonRocketOrderTotalAmountsCalculator;
import ru.sir.richard.boss.model.calc.PickupOrderTotalAmountsCalculator;
import ru.sir.richard.boss.model.calc.PostOrderTotalAmountsCalculator;
import ru.sir.richard.boss.model.calc.YandexGoOrderTotalAmountsCalculator;
import ru.sir.richard.boss.model.calc.YandexMarketOrderTotalAmountsCalculator;
import ru.sir.richard.boss.model.calc.YandexPayBillTotalAmountsCalculator;
import ru.sir.richard.boss.model.calc.СdekOrderTotalAmountsCalculator;
import ru.sir.richard.boss.model.data.Order;
import ru.sir.richard.boss.model.types.DeliveryTypes;
import ru.sir.richard.boss.model.types.OrderTypes;
import ru.sir.richard.boss.model.types.PaymentTypes;

public class OrderTotalAmountsCalculatorFactory {

	public static AnyOrderTotalAmountsCalculator createCalculator(Order order) {

		if (order.getOrderType() == OrderTypes.BILL || order.getOrderType() == OrderTypes.KP) {
			if (order.getPaymentType() == PaymentTypes.YANDEX_PAY) {
				return new YandexPayBillTotalAmountsCalculator(order);
			} else {
				return new BillTotalAmountsCalculator(order);
			}
					
		} else if (order.getOrderType() == OrderTypes.ORDER) {
			if (order.getDelivery().getDeliveryType() == DeliveryTypes.YANDEX_MARKET_FBS) {
				return new YandexMarketOrderTotalAmountsCalculator(order);
			} if (order.getDelivery().getDeliveryType() == DeliveryTypes.OZON_FBS) {
				return new OzonMarketOrderTotalAmountsCalculator(order);
			}  if (order.getDelivery().getDeliveryType() == DeliveryTypes.YANDEX_GO) {
				return new YandexGoOrderTotalAmountsCalculator(order);
			} else if (order.getDelivery().getDeliveryType() == DeliveryTypes.PICKUP) {
				return new PickupOrderTotalAmountsCalculator(order);
			} else if (order.getDelivery().getDeliveryType().isСdek()) {
				return new СdekOrderTotalAmountsCalculator(order);			
			} else if (order.getDelivery().getDeliveryType().isOzonRocket()) {
				return new OzonRocketOrderTotalAmountsCalculator(order);			
			} else if (order.getDelivery().getDeliveryType() == DeliveryTypes.DELLIN) {
				return new EmptyTotalAmountsCalculator(order);
			} else if (order.getDelivery().getDeliveryType().isCourier()) {
				return new OwnCourierOrderTotalAmountsCalculator(order);
			} else if (order.getDelivery().getDeliveryType().isPost()) {
				return new PostOrderTotalAmountsCalculator(order);			
			} else {
				return null;
			}
			
		} else {
			return new EmptyTotalAmountsCalculator(order);
		}
	}

}
