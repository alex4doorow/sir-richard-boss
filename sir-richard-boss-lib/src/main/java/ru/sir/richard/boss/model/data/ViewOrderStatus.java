package ru.sir.richard.boss.model.data;

import ru.sir.richard.boss.model.types.DeliveryTypes;
import ru.sir.richard.boss.model.types.OrderStatuses;
import ru.sir.richard.boss.model.types.OrderTypes;
import ru.sir.richard.boss.model.types.PaymentTypes;

public class ViewOrderStatus {
		
	public static String VIEW_STATUS_NONE = "";
	public static String VIEW_STATUS_SUCCESS = "success";
	public static String VIEW_STATUS_WARNING = "warning";
	public static String VIEW_STATUS_DANGER = "danger";
	public static String VIEW_STATUS_INFO = "info";
	public static String VIEW_STATUS_SECONDARY = "secondary";
	public static String VIEW_STATUS_LIGHT = "light";	
	public static String VIEW_STATUS_DARK = "dark";
		
	private String union;
	private String orderId;
	private String orderNo;
	private String orderDate;
		
	public ViewOrderStatus() {
		union = VIEW_STATUS_LIGHT;
		orderId = VIEW_STATUS_LIGHT;
		orderNo = VIEW_STATUS_LIGHT;
		orderDate = VIEW_STATUS_LIGHT;
	}

	public String getUnion() {
		return union;
	}

	public void setUnion(String union) {
		this.union = union;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.union = VIEW_STATUS_NONE;
		this.orderId = orderId;
	}

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.union = VIEW_STATUS_NONE;
		this.orderNo = orderNo;
	}

	public String getOrderDate() {
		return orderDate;
	}

	public void setOrderDate(String orderDate) {
		this.union = VIEW_STATUS_NONE;
		this.orderDate = orderDate;
	}	
	
	public static ViewOrderStatus createViewOrderStatus(Order order) {
				
		ViewOrderStatus viewOrderStatus = new ViewOrderStatus();
		if (order.getOrderType() == OrderTypes.KP || order.getOrderType() == OrderTypes.CONSULTATION) {
			if (order.getStatus() == OrderStatuses.CANCELED) {
				viewOrderStatus.setUnion(ViewOrderStatus.VIEW_STATUS_DANGER);
				return viewOrderStatus;
			} else {
				viewOrderStatus.setUnion(ViewOrderStatus.VIEW_STATUS_INFO);			
				return viewOrderStatus; // голубой			
			}
		} else if (order.getOrderType() == OrderTypes.ORDER) {
			if (order.getStatus() == OrderStatuses.BID) {
				viewOrderStatus.setUnion(ViewOrderStatus.VIEW_STATUS_LIGHT);			
				return viewOrderStatus; 
			} else if (order.getStatus() == OrderStatuses.APPROVED) {
				viewOrderStatus.setUnion(ViewOrderStatus.VIEW_STATUS_LIGHT);
				if (order.getDelivery().getDeliveryType() == DeliveryTypes.YANDEX_MARKET_FBS) {
					viewOrderStatus.setOrderId(ViewOrderStatus.VIEW_STATUS_WARNING);					
				} else if (order.getDelivery().getDeliveryType() == DeliveryTypes.OZON_FBS) {
					viewOrderStatus.setOrderId(ViewOrderStatus.VIEW_STATUS_WARNING);					
				} else if (order.getDelivery().getDeliveryType().isСdek()) {
					viewOrderStatus.setOrderId(ViewOrderStatus.VIEW_STATUS_WARNING);					
				} else if (order.getDelivery().getDeliveryType().isOzonRocket()) {
					viewOrderStatus.setOrderId(ViewOrderStatus.VIEW_STATUS_WARNING);					
				} else if (order.getDelivery().getDeliveryType().isPost()) {
					viewOrderStatus.setOrderId(ViewOrderStatus.VIEW_STATUS_WARNING);					
				} else if (order.getDelivery().getDeliveryType().isCourier()) {
					viewOrderStatus.setUnion(ViewOrderStatus.VIEW_STATUS_WARNING);
					return viewOrderStatus;
				} else if (order.getDelivery().getDeliveryType() == DeliveryTypes.YANDEX_GO) {
					viewOrderStatus.setUnion(ViewOrderStatus.VIEW_STATUS_WARNING);
					return viewOrderStatus;
				}
				return viewOrderStatus;				
			} else if (order.getStatus() == OrderStatuses.DOC_NOT_EXIST) {
				viewOrderStatus.setUnion(ViewOrderStatus.VIEW_STATUS_LIGHT);	
				viewOrderStatus.setOrderId(ViewOrderStatus.VIEW_STATUS_DARK);
				viewOrderStatus.setOrderNo(ViewOrderStatus.VIEW_STATUS_DARK);
				viewOrderStatus.setOrderDate(ViewOrderStatus.VIEW_STATUS_DARK);		
				return viewOrderStatus;
			} else if (order.getStatus() == OrderStatuses.FINISHED) {
				viewOrderStatus.setUnion(ViewOrderStatus.VIEW_STATUS_SUCCESS);	
				return viewOrderStatus; // зеленый
			} else if (order.getStatus() == OrderStatuses.REDELIVERY) {
				viewOrderStatus.setUnion(ViewOrderStatus.VIEW_STATUS_DANGER);	
				return viewOrderStatus; // красный
			} else if (order.getStatus() == OrderStatuses.CANCELED) {
				viewOrderStatus.setUnion(ViewOrderStatus.VIEW_STATUS_DANGER);	
				return viewOrderStatus; // красный
			} else if (order.getStatus() == OrderStatuses.LOST) {
				viewOrderStatus.setUnion(ViewOrderStatus.VIEW_STATUS_DANGER);	
				return viewOrderStatus; // красный
			} else if (order.getStatus() == OrderStatuses.REDELIVERY_FINISHED) {
				viewOrderStatus.setUnion(ViewOrderStatus.VIEW_STATUS_DANGER);	
				return viewOrderStatus; // красный
			} else if (order.getStatus() == OrderStatuses.DELIVERING) {
				viewOrderStatus.setUnion(ViewOrderStatus.VIEW_STATUS_LIGHT);
				if (order.getDelivery().getDeliveryType().isСdek()) {
					viewOrderStatus.setOrderId(ViewOrderStatus.VIEW_STATUS_SUCCESS);
					return viewOrderStatus;
				} else if (order.getDelivery().getDeliveryType().isOzonRocket()) {
					viewOrderStatus.setOrderId(ViewOrderStatus.VIEW_STATUS_SUCCESS);
					return viewOrderStatus;
				} else if (order.getDelivery().getDeliveryType().isPost()) {
					viewOrderStatus.setOrderId(ViewOrderStatus.VIEW_STATUS_SUCCESS);
					return viewOrderStatus;
				} else if (order.getDelivery().getDeliveryType().isCourier()) {
					viewOrderStatus.setUnion(ViewOrderStatus.VIEW_STATUS_WARNING);
					return viewOrderStatus;
				} else if (order.getDelivery().getDeliveryType() == DeliveryTypes.YANDEX_GO) {
					viewOrderStatus.setUnion(ViewOrderStatus.VIEW_STATUS_WARNING);
					return viewOrderStatus;
				} else if (order.getDelivery().getDeliveryType() == DeliveryTypes.YANDEX_MARKET_FBS) {
					viewOrderStatus.setOrderId(ViewOrderStatus.VIEW_STATUS_SUCCESS);
					return viewOrderStatus;
				} else if (order.getDelivery().getDeliveryType() == DeliveryTypes.OZON_FBS) {
					viewOrderStatus.setOrderId(ViewOrderStatus.VIEW_STATUS_SUCCESS);
					return viewOrderStatus;
				}
			} else if (order.getStatus() == OrderStatuses.READY_GIVE_AWAY || order.getStatus() == OrderStatuses.READY_GIVE_AWAY_TROUBLE) {
				viewOrderStatus.setUnion(ViewOrderStatus.VIEW_STATUS_LIGHT);
				if (order.getDelivery().getDeliveryType().isСdek()) {
					viewOrderStatus.setOrderId(ViewOrderStatus.VIEW_STATUS_SUCCESS);
					viewOrderStatus.setOrderNo(ViewOrderStatus.VIEW_STATUS_DANGER);
					viewOrderStatus.setOrderDate(ViewOrderStatus.VIEW_STATUS_DANGER);
					return viewOrderStatus;
				} else if (order.getDelivery().getDeliveryType().isOzonRocket()) {
					viewOrderStatus.setOrderId(ViewOrderStatus.VIEW_STATUS_SUCCESS);
					viewOrderStatus.setOrderNo(ViewOrderStatus.VIEW_STATUS_DANGER);
					viewOrderStatus.setOrderDate(ViewOrderStatus.VIEW_STATUS_DANGER);
					return viewOrderStatus;
				} else if (order.getDelivery().getDeliveryType().isPost()) {
					viewOrderStatus.setOrderId(ViewOrderStatus.VIEW_STATUS_SUCCESS);
					viewOrderStatus.setOrderNo(ViewOrderStatus.VIEW_STATUS_DANGER);
					viewOrderStatus.setOrderDate(ViewOrderStatus.VIEW_STATUS_DANGER);					
					return viewOrderStatus;
				} else if (order.getDelivery().getDeliveryType() == DeliveryTypes.YANDEX_MARKET_FBS) {
					viewOrderStatus.setOrderId(ViewOrderStatus.VIEW_STATUS_SUCCESS);
					viewOrderStatus.setOrderNo(ViewOrderStatus.VIEW_STATUS_DANGER);
					viewOrderStatus.setOrderDate(ViewOrderStatus.VIEW_STATUS_DANGER);
					return viewOrderStatus;
				} else if (order.getDelivery().getDeliveryType() == DeliveryTypes.OZON_FBS) {
					viewOrderStatus.setOrderId(ViewOrderStatus.VIEW_STATUS_SUCCESS);
					viewOrderStatus.setOrderNo(ViewOrderStatus.VIEW_STATUS_DANGER);
					viewOrderStatus.setOrderDate(ViewOrderStatus.VIEW_STATUS_DANGER);
					return viewOrderStatus;
				}
			} else if (order.getStatus() == OrderStatuses.DELIVERED) {
				if (order.getDelivery().getDeliveryType().isPost() || order.getDelivery().getDeliveryType().isСdek() || order.getDelivery().getDeliveryType().isOzonRocket()) {
					viewOrderStatus.setOrderId(ViewOrderStatus.VIEW_STATUS_SUCCESS);
					viewOrderStatus.setOrderNo(ViewOrderStatus.VIEW_STATUS_SUCCESS);
					viewOrderStatus.setOrderDate(ViewOrderStatus.VIEW_STATUS_SUCCESS);
					return viewOrderStatus;
				} else if (order.getDelivery().getDeliveryType().isCourier()) {
					viewOrderStatus.setUnion(ViewOrderStatus.VIEW_STATUS_SUCCESS);
					return viewOrderStatus;
				} else if (order.getDelivery().getDeliveryType() == DeliveryTypes.YANDEX_MARKET_FBS) {
					viewOrderStatus.setOrderId(ViewOrderStatus.VIEW_STATUS_SUCCESS);
					viewOrderStatus.setOrderNo(ViewOrderStatus.VIEW_STATUS_SUCCESS);
					viewOrderStatus.setOrderDate(ViewOrderStatus.VIEW_STATUS_SUCCESS);
					return viewOrderStatus;
				} else if (order.getDelivery().getDeliveryType() == DeliveryTypes.OZON_FBS) {
					viewOrderStatus.setOrderId(ViewOrderStatus.VIEW_STATUS_SUCCESS);
					viewOrderStatus.setOrderNo(ViewOrderStatus.VIEW_STATUS_SUCCESS);
					viewOrderStatus.setOrderDate(ViewOrderStatus.VIEW_STATUS_SUCCESS);
					return viewOrderStatus;
				}						
			}
		} else if (order.getOrderType() == OrderTypes.BILL) {			
			if (order.getStatus() == OrderStatuses.BID) {
				viewOrderStatus.setUnion(ViewOrderStatus.VIEW_STATUS_INFO);
				return viewOrderStatus;
			} else if (order.getStatus() == OrderStatuses.APPROVED) {
				viewOrderStatus.setUnion(ViewOrderStatus.VIEW_STATUS_LIGHT);
				if (order.isPrepayment()) {
					viewOrderStatus.setOrderNo(ViewOrderStatus.VIEW_STATUS_DANGER);
				} else {
					viewOrderStatus.setOrderId(ViewOrderStatus.VIEW_STATUS_WARNING);
					viewOrderStatus.setOrderNo(ViewOrderStatus.VIEW_STATUS_DANGER);					
				}				
				return viewOrderStatus;
			} else if (order.getStatus() == OrderStatuses.PAY_WAITING) {
				viewOrderStatus.setUnion(ViewOrderStatus.VIEW_STATUS_LIGHT);
				viewOrderStatus.setOrderNo(ViewOrderStatus.VIEW_STATUS_DANGER);
				return viewOrderStatus;
			} else if (order.getStatus() == OrderStatuses.PAY_ON) {
				viewOrderStatus.setUnion(ViewOrderStatus.VIEW_STATUS_LIGHT);
				if (order.isPrepayment()) {
					viewOrderStatus.setOrderId(ViewOrderStatus.VIEW_STATUS_WARNING);
					viewOrderStatus.setOrderNo(ViewOrderStatus.VIEW_STATUS_SUCCESS);
				} else {
					viewOrderStatus.setOrderId(ViewOrderStatus.VIEW_STATUS_SUCCESS);
					viewOrderStatus.setOrderNo(ViewOrderStatus.VIEW_STATUS_SUCCESS);
					viewOrderStatus.setOrderDate(ViewOrderStatus.VIEW_STATUS_SUCCESS);							
				}
				return viewOrderStatus;
			} else if (order.getStatus() == OrderStatuses.DOC_NOT_EXIST) {
				viewOrderStatus.setUnion(ViewOrderStatus.VIEW_STATUS_LIGHT);	
				viewOrderStatus.setOrderId(ViewOrderStatus.VIEW_STATUS_DARK);
				viewOrderStatus.setOrderNo(ViewOrderStatus.VIEW_STATUS_DARK);
				viewOrderStatus.setOrderDate(ViewOrderStatus.VIEW_STATUS_DARK);		
				return viewOrderStatus;
			} else if (order.getStatus() == OrderStatuses.FINISHED) {
				viewOrderStatus.setUnion(ViewOrderStatus.VIEW_STATUS_SUCCESS);
				return viewOrderStatus;
			} else if (order.getStatus() == OrderStatuses.CANCELED) {
				viewOrderStatus.setUnion(ViewOrderStatus.VIEW_STATUS_DANGER);
				return viewOrderStatus;
			} else if (order.getStatus() == OrderStatuses.DELIVERING) {				
				if (order.isPrepayment()) {					
					viewOrderStatus.setUnion(ViewOrderStatus.VIEW_STATUS_LIGHT);
					viewOrderStatus.setOrderId(ViewOrderStatus.VIEW_STATUS_SUCCESS);
					viewOrderStatus.setOrderNo(ViewOrderStatus.VIEW_STATUS_SUCCESS);
					viewOrderStatus.setOrderDate(ViewOrderStatus.VIEW_STATUS_DANGER);
					return viewOrderStatus;
				} else if (order.getPaymentType() == PaymentTypes.POSTPAY) {
					viewOrderStatus.setUnion(ViewOrderStatus.VIEW_STATUS_LIGHT);
					viewOrderStatus.setOrderId(ViewOrderStatus.VIEW_STATUS_SUCCESS);
					viewOrderStatus.setOrderNo(ViewOrderStatus.VIEW_STATUS_DANGER);
					viewOrderStatus.setOrderDate(ViewOrderStatus.VIEW_STATUS_DANGER);
					return viewOrderStatus;
				}
			} else if (order.getStatus() == OrderStatuses.READY_GIVE_AWAY || order.getStatus() == OrderStatuses.READY_GIVE_AWAY_TROUBLE) {
				if (order.isPrepayment()) {					
					viewOrderStatus.setUnion(ViewOrderStatus.VIEW_STATUS_LIGHT);
					viewOrderStatus.setOrderId(ViewOrderStatus.VIEW_STATUS_SUCCESS);
					viewOrderStatus.setOrderNo(ViewOrderStatus.VIEW_STATUS_SUCCESS);
					viewOrderStatus.setOrderDate(ViewOrderStatus.VIEW_STATUS_SUCCESS);
					return viewOrderStatus;
				} else if (order.getPaymentType() == PaymentTypes.POSTPAY) {
					viewOrderStatus.setUnion(ViewOrderStatus.VIEW_STATUS_LIGHT);
					viewOrderStatus.setOrderId(ViewOrderStatus.VIEW_STATUS_SUCCESS);
					viewOrderStatus.setOrderNo(ViewOrderStatus.VIEW_STATUS_DANGER);
					viewOrderStatus.setOrderDate(ViewOrderStatus.VIEW_STATUS_SUCCESS);
					return viewOrderStatus;
				}
				
			} else if (order.getStatus() == OrderStatuses.DELIVERED) {
				if (order.isPrepayment()) {					
					viewOrderStatus.setUnion(ViewOrderStatus.VIEW_STATUS_LIGHT);
					viewOrderStatus.setOrderId(ViewOrderStatus.VIEW_STATUS_SUCCESS);
					viewOrderStatus.setOrderNo(ViewOrderStatus.VIEW_STATUS_SUCCESS);
					viewOrderStatus.setOrderDate(ViewOrderStatus.VIEW_STATUS_SUCCESS);
					return viewOrderStatus;
				} else if (order.getPaymentType() == PaymentTypes.POSTPAY) {
					viewOrderStatus.setUnion(ViewOrderStatus.VIEW_STATUS_LIGHT);
					viewOrderStatus.setOrderId(ViewOrderStatus.VIEW_STATUS_DANGER);
					viewOrderStatus.setOrderNo(ViewOrderStatus.VIEW_STATUS_SUCCESS);
					return viewOrderStatus;
				}				
			}
		
		} else if (order.getOrderType() == OrderTypes.REFUND) {
			viewOrderStatus.setUnion(ViewOrderStatus.VIEW_STATUS_SECONDARY);
			return viewOrderStatus;
			
		} else if (order.getOrderType() == OrderTypes.REPAIR || order.getOrderType() == OrderTypes.CHANGE) {
			viewOrderStatus.setUnion(ViewOrderStatus.VIEW_STATUS_SECONDARY);
			return viewOrderStatus;
			
		} else if (order.getOrderType() == OrderTypes.GIFT) {
			viewOrderStatus.setUnion(ViewOrderStatus.VIEW_STATUS_SECONDARY);
			return viewOrderStatus;
			
		} else if (order.getOrderType() == OrderTypes.TEST_DIVE) {
			viewOrderStatus.setUnion(ViewOrderStatus.VIEW_STATUS_SECONDARY);
			return viewOrderStatus;
		}
		return viewOrderStatus;	
		
		
	}

}
