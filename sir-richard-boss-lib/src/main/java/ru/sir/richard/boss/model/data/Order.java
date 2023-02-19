package ru.sir.richard.boss.model.data;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.format.annotation.DateTimeFormat;

import ru.sir.richard.boss.model.factories.CustomersFactory;
import ru.sir.richard.boss.model.types.CrmTypes;
import ru.sir.richard.boss.model.types.CustomerTypes;
import ru.sir.richard.boss.model.types.OrderAdvertTypes;
import ru.sir.richard.boss.model.types.OrderEmailStatuses;
import ru.sir.richard.boss.model.types.OrderSourceTypes;
import ru.sir.richard.boss.model.types.OrderStatuses;
import ru.sir.richard.boss.model.types.OrderTypes;
import ru.sir.richard.boss.model.types.PaymentTypes;
import ru.sir.richard.boss.model.types.StoreTypes;
import ru.sir.richard.boss.utils.DateTimeUtils;

public class Order extends AnyId {
		
	private int no;
	private int subNo;
	
	@DateTimeFormat(pattern = "dd.MM.yyyy")
	private Date orderDate;
	private OrderTypes orderType;
	private ProductCategory productCategory;
	private OrderSourceTypes sourceType;
	private OrderAdvertTypes advertType;
	private PaymentTypes paymentType;
	private StoreTypes store;
	private OrderStatuses status;
	private OrderEmailStatuses emailStatus;
	private OrderDelivery delivery;	
	private List<OrderExternalCrm> externalCrms;		
	private AnyCustomer customer;		
	private List<OrderStatusItem> statuses;
	private List<OrderItem> items;	
	private OrderAmounts amounts; 	
	private String annotation;	
	private Date addedDate;
	private Date modifiedDate;	
	private Set<Comment> comments;
	private OrderOffer offer;
	
	public Order() {
		super();
		this.store = StoreTypes.PM;
		this.delivery = new OrderDelivery(this);
		this.externalCrms = new ArrayList<OrderExternalCrm>();
		this.statuses = new ArrayList<OrderStatusItem>();
		this.amounts = new OrderAmounts(this);
		this.comments = new HashSet<Comment>();
		this.items = new ArrayList<OrderItem>();
		this.offer = new OrderOffer(this);
	}
	
	public Order(CustomerTypes customerType) {
		this();
		this.customer = CustomersFactory.createCustomer(customerType);				
	}
	
	public int getNo() {
		return no;
	}	

	public void setNo(int no) {
		this.no = no;
	}	

	public int getSubNo() {
		return subNo;
	}

	public void setSubNo(int subNo) {
		this.subNo = subNo;
	}

	public Date getOrderDate() {
		return orderDate;
	}
	
	public void setOrderDate(Date orderDate) {
		this.orderDate = orderDate;
	}

	public String getAnnotation() {
		return annotation;
	}

	public void setAnnotation(String annotation) {
		this.annotation = annotation;
	}	
	
	public void setOrderType(OrderTypes orderType) {
		this.orderType = orderType;
	}

	public OrderTypes getOrderType() {
		return orderType;
	}

	public StoreTypes getStore() {
		return store;
	}

	public void setStore(StoreTypes store) {
		this.store = store;
	}

	public AnyCustomer getCustomer() {
		return customer;
	}

	public void setCustomer(AnyCustomer customer) {
		this.customer = customer;
	}
	
	public int getOrderYear() {
		return DateTimeUtils.dateToShortYear(this.orderDate);
	}
	
	public ProductCategory getProductCategory() {
		return productCategory;
	}

	public void setProductCategory(ProductCategory productCategory) {
		this.productCategory = productCategory;
	}

	public OrderSourceTypes getSourceType() {
		return sourceType;
	}

	public void setSourceType(OrderSourceTypes sourceType) {
		this.sourceType = sourceType;
	}

	public OrderAdvertTypes getAdvertType() {
		return advertType;
	}

	public void setAdvertType(OrderAdvertTypes advertType) {
		this.advertType = advertType;
	}

	public PaymentTypes getPaymentType() {
		return paymentType;
	}

	public void setPaymentType(PaymentTypes paymentType) {
		this.paymentType = paymentType;
	}
	
	public boolean isPrepayment() {
		if (paymentType == PaymentTypes.PREPAYMENT || paymentType == PaymentTypes.YANDEX_PAY) {
			return true;
		} else {
			return false;
		}
	}	

	public OrderStatuses getStatus() {
		return status;
	}

	public void setStatus(OrderStatuses status) {
		this.status = status;
	}
	
	public OrderEmailStatuses getEmailStatus() {
		return emailStatus;
	}

	public void setEmailStatus(OrderEmailStatuses emailStatus) {
		this.emailStatus = emailStatus;
	}

	public OrderDelivery getDelivery() {
		return delivery;
	}

	public void setDelivery(OrderDelivery delivery) {
		this.delivery = delivery;
	}

	public Date getAddedDate() {
		return addedDate;
	}

	public void setAddedDate(Date addedDate) {
		this.addedDate = addedDate;
	}

	public Date getModifiedDate() {
		return modifiedDate;
	}

	public void setModifiedDate(Date modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	public List<OrderStatusItem> getStatuses() {
		return statuses;
	}

	public void setStatuses(List<OrderStatusItem> statuses) {
		this.statuses = statuses;
	}

	public List<OrderItem> getItems() {
		return items;
	}

	public void setItems(List<OrderItem> items) {
		this.items = items;
	}

	public OrderAmounts getAmounts() {
		return amounts;
	}
	
	public void setAmounts(OrderAmounts amounts) {
		this.amounts = amounts;		
	}
	
	public Set<Comment> getComments() {
		return comments;
	}

	public void setComments(Set<Comment> comments) {
		this.comments = comments;
	}
	
	public List<OrderExternalCrm> getExternalCrms() {
		return externalCrms;
	}
	
	public void setExternalCrms(List<OrderExternalCrm> externalCrms) {
		this.externalCrms = externalCrms;
	}
	
	public OrderExternalCrm getExternalCrmByCode(CrmTypes crmTypes) {
		if (externalCrms == null || externalCrms.size() == 0 || crmTypes == CrmTypes.NONE) {
			return null;			
		}				
		for (OrderExternalCrm externalCrm : externalCrms) {
			if (crmTypes == externalCrm.getCrm()) {
				return externalCrm;
			}
		}		
		return null;
	}
	
	public CrmTypes getExternalCrm() {
		CrmTypes result;
		if (this.getExternalCrmByCode(CrmTypes.OZON) != null) {
			result = CrmTypes.OZON;			
		} else if (this.getExternalCrmByCode(CrmTypes.YANDEX_MARKET) != null) {
			result = CrmTypes.YANDEX_MARKET;
		}  else if (this.getExternalCrmByCode(CrmTypes.OPENCART) != null) {
			result = CrmTypes.OPENCART;
		} else {
			result = CrmTypes.NONE;
		}	
		return result;
		
	}

	public OrderOffer getOffer() {
		return offer;
	}

	public void addItem(OrderItem item) {
		item.setParent(this);
		getItems().add(item);
	}
	
	public void addStatusItem(OrderStatusItem item) {	
		int iNo = 1;
		for (OrderStatusItem orderStatusItem : getStatuses()) {
			if (orderStatusItem.getNo() > iNo) {
				iNo++;	
			}			
		}
		item.setNo(iNo);	
		item.setParent(this);
		getStatuses().add(item);
	}
	
	public OrderStatusItem getLastStatusItem() {
		if (getStatuses() == null || getStatuses().size() == 0) {
			return null;
		}
		return getStatuses().get(getStatuses().size() - 1);
	}
		
	public boolean isBillAmount() {
		if (this.getOrderType() == OrderTypes.ORDER) {			
			if (this.getStatus() == OrderStatuses.APPROVED) {
				return true;
			} else if (this.getStatus() == OrderStatuses.PAY_WAITING) {
				return true;
			} else if (this.getStatus() == OrderStatuses.PAY_ON) {
				return true;
			} else if (this.getStatus() == OrderStatuses.DELIVERING) {
				return true;
			} else if (this.getStatus() == OrderStatuses.READY_GIVE_AWAY) {
				return true;
			} else if (this.getStatus() == OrderStatuses.READY_GIVE_AWAY_TROUBLE) {
				return true;
			} else if (this.getStatus() == OrderStatuses.DELIVERED) {
				return true;
			} else if (this.getStatus() == OrderStatuses.FINISHED) {
				return true;
			} else if (this.getStatus() == OrderStatuses.DOC_NOT_EXIST) {
				return true;
			} 
		} else if (this.getOrderType() == OrderTypes.BILL) {
			if (this.isPrepayment()) {				
				if (this.getStatus() == OrderStatuses.APPROVED) {
					return false;
				} else if (this.getStatus() == OrderStatuses.PAY_WAITING) {
					return false;
				} else if (this.getStatus() == OrderStatuses.PAY_ON) {
					return true;
				} else if (this.getStatus() == OrderStatuses.DELIVERING) {
					return true;
				} else if (this.getStatus() == OrderStatuses.READY_GIVE_AWAY) {
					return true;
				} else if (this.getStatus() == OrderStatuses.READY_GIVE_AWAY_TROUBLE) {
					return true;
				} else if (this.getStatus() == OrderStatuses.DELIVERED) {
					return true;
				} else if (this.getStatus() == OrderStatuses.FINISHED) {
					return true;
				} else if (this.getStatus() == OrderStatuses.DOC_NOT_EXIST) {
					return true;
				} 				
			} else if (this.getPaymentType() == PaymentTypes.POSTPAY) {
				
				if (this.getStatus() == OrderStatuses.APPROVED) {
					return true;
				} else if (this.getStatus() == OrderStatuses.PAY_WAITING) {
					return true;
				} else if (this.getStatus() == OrderStatuses.PAY_ON) {
					return true;
				} else if (this.getStatus() == OrderStatuses.DELIVERING) {
					return true;
				} else if (this.getStatus() == OrderStatuses.READY_GIVE_AWAY) {
					return true;
				} else if (this.getStatus() == OrderStatuses.READY_GIVE_AWAY_TROUBLE) {
					return true;
				} else if (this.getStatus() == OrderStatuses.DELIVERED) {
					return true;
				} else if (this.getStatus() == OrderStatuses.FINISHED) {
					return true;
				} else if (this.getStatus() == OrderStatuses.DOC_NOT_EXIST) {
					return true;
				}
			}			
		}		
		return false;
	}
		
	public boolean isPostpayAmount() {
		
		if (this.getOrderType() == OrderTypes.ORDER) {
			if (this.getPaymentType() == PaymentTypes.POSTPAY) {				
				// заказ ФЛ с наложенным платежом
				if (this.getStatus() == OrderStatuses.APPROVED) {
					return true;
				} else if (this.getStatus() == OrderStatuses.DELIVERING) {
					return true;
				} else if (this.getStatus() == OrderStatuses.READY_GIVE_AWAY) {
					return true;
				} else if (this.getStatus() == OrderStatuses.READY_GIVE_AWAY_TROUBLE) {
					return true;
				} else if (this.getStatus() == OrderStatuses.DELIVERED) {
					return true;
				} else if (this.getStatus() == OrderStatuses.REDELIVERY) {
					return true;
				} 
			} else if (this.getPaymentType() == PaymentTypes.PAYMENT_COURIER) {
				return false;
			}
		} else if (this.getOrderType() == OrderTypes.BILL) {
			if (this.isPrepayment()) {
				return false;							
			} else if (this.getPaymentType() == PaymentTypes.POSTPAY) {
				if (this.getStatus() == OrderStatuses.APPROVED) {
					return true;
				} else if (this.getStatus() == OrderStatuses.DELIVERING) {
					return true;
				} else if (this.getStatus() == OrderStatuses.READY_GIVE_AWAY) {
					return true;
				}  else if (this.getStatus() == OrderStatuses.READY_GIVE_AWAY_TROUBLE) {
					return true;
				} else if (this.getStatus() == OrderStatuses.DELIVERED) {
					return true;
				} else if (this.getStatus() == OrderStatuses.PAY_WAITING) {
					return true;
				} 				
			}			
		}		
		return false;
	}	
	
	public boolean isOpenCart() {
		boolean result = false;
		if (getExternalCrms() != null && getExternalCrms().size() > 0) {
			for (OrderExternalCrm externalCrm : getExternalCrms()) {
				if (externalCrm.getCrm() == CrmTypes.OPENCART) {
					result = true;
					break;
				}
			}			
		}	
		return result;	
		
	}

	public String getViewNo() {
		String result = String.valueOf(this.no);
		if (getExternalCrms() != null && getExternalCrms().size() > 0) {			
			if (this.getAdvertType() == OrderAdvertTypes.OZON) {
				String ozonMarketNo = "";
				for (OrderExternalCrm externalCrm : getExternalCrms()) {										
					if (externalCrm.getCrm() == CrmTypes.OZON) {
						ozonMarketNo = String.valueOf(externalCrm.getParentCode());						
					}
				}
				result += " (" + ozonMarketNo + ")";				
				return result;	
				
			} else if (this.getAdvertType() == OrderAdvertTypes.YANDEX_MARKET) {
				String openCartNo = "";
				String yandexMarketNo = "";
				for (OrderExternalCrm externalCrm : getExternalCrms()) {
					if (externalCrm.getCrm() == CrmTypes.OPENCART) {
						openCartNo = String.valueOf(externalCrm.getParentId());						
					}					
					if (externalCrm.getCrm() == CrmTypes.YANDEX_MARKET) {
						yandexMarketNo = String.valueOf(externalCrm.getParentId());						
					}
				}
				result += " (" + yandexMarketNo + " / " + openCartNo + ")";				
				return result;
			}
			for (OrderExternalCrm externalCrm : getExternalCrms()) {
				if (externalCrm.getCrm() == CrmTypes.OPENCART) {
					result += " (" + externalCrm.getParentId() + ")";
					break;
				}
			}
			//Просмотр данных по заказу #10161 (197) от 01.03.2021 г.			
		}	
		if (subNo == 0) {			
			return result;
		} else {
			return result + '-' + String.valueOf(subNo);
		}
	}
	
	public String getViewMarketNo() {
		if (getExternalCrms() != null && getExternalCrms().size() > 0) {			
			if (this.getAdvertType() == OrderAdvertTypes.OZON) {
				for (OrderExternalCrm externalCrm : getExternalCrms()) {										
					if (externalCrm.getCrm() == CrmTypes.OZON) {
						return String.valueOf(externalCrm.getParentCode());						
					}
				}
			} else if (this.getAdvertType() == OrderAdvertTypes.YANDEX_MARKET) {								
				String openCartNo = "";
				String yandexMarketNo = "";
				for (OrderExternalCrm externalCrm : getExternalCrms()) {
					if (externalCrm.getCrm() == CrmTypes.OPENCART) {
						openCartNo = String.valueOf(externalCrm.getParentId());						
					} else if (externalCrm.getCrm() == CrmTypes.YANDEX_MARKET) {
						yandexMarketNo = String.valueOf(externalCrm.getParentId());						
					}
				}
				return yandexMarketNo + " / " + openCartNo;
			}					
		}	
		return "";
	}	
	
	public ViewOrderStatus getViewStatus() {
		return ViewOrderStatus.createViewOrderStatus(this);		
	}
	
	public String getBarcodeNumber() {					
		String s = DateTimeUtils.formatDate(this.getOrderDate(), "yyMMdd") + StringUtils.leftPad(String.valueOf(this.getNo()), 2, '0');
		return s;
	}
	
	public String getExpiredDate() {
		String result = "";
		if (this.getOffer().getCountDay() <= 0) {
			return result;
		}
		if ((this.getOrderType() == OrderTypes.BILL || this.getOrderType() == OrderTypes.KP) && this.getStatus() == OrderStatuses.BID) {			
			result = DateTimeUtils.defaultFormatDate(this.getOffer().getExpiredDate());			
		}
		return result;
	}
	
	public String getViewDateInfo() {
		//${order.orderType.annotation}, ${order.status.annotation}
		String result = this.getOrderType().getAnnotation() + ", " + this.getStatus().getAnnotation();
		String expiredDate = this.getExpiredDate();
		if (StringUtils.isEmpty(expiredDate)) {
			return result;
		} else {
			return result + ", " + expiredDate;
		}		
	}
	
	@Override
	public Order clone() throws CloneNotSupportedException  {
		Order clone = (Order) super.clone();		
		clone.no = this.no;
		clone.subNo = this.subNo;
		clone.orderDate = this.orderDate == null ? null : (Date) this.orderDate.clone();
		clone.orderType = this.orderType;		
		clone.productCategory = this.productCategory == null ? null : (ProductCategory) this.productCategory.clone();		
		clone.sourceType = this.sourceType;
		clone.advertType = this.advertType;
		clone.paymentType = this.paymentType;
		clone.store = this.store;
		clone.status = this.status;
		clone.emailStatus = this.emailStatus;
		clone.delivery = this.delivery == null ? null : (OrderDelivery) this.delivery.clone();
		clone.externalCrms = this.externalCrms == null ? null : new ArrayList<>(this.externalCrms);		
		clone.customer = this.customer == null ? null : this.customer.clone();		
		clone.statuses = this.statuses == null ? null : new ArrayList<>(this.statuses);
		clone.items = this.items == null ? null : new ArrayList<>(this.items);
		clone.amounts = this.amounts.clone();		
		clone.annotation = this.annotation == null ? null : new String(this.annotation);
		clone.addedDate = this.orderDate == null ? null : (Date) this.addedDate.clone();
		clone.modifiedDate = this.modifiedDate == null ? null : (Date) this.modifiedDate.clone();
		clone.comments = this.comments == null ? null : new HashSet<>(this.comments);
		return clone;
	}	
	
	@Override
	protected void clear() {
		super.clear();
		this.orderType = OrderTypes.ORDER;	
		this.productCategory = new ProductCategory();
		this.sourceType = OrderSourceTypes.UNKNOWN;
		this.advertType = OrderAdvertTypes.UNKNOWN;
		this.paymentType = PaymentTypes.POSTPAY;
		this.status = OrderStatuses.BID;
		this.emailStatus = OrderEmailStatuses.UNKNOWN;
		this.statuses = new ArrayList<OrderStatusItem>();
		this.items = new ArrayList<OrderItem>();
		this.delivery = new OrderDelivery(this);
		this.amounts = new OrderAmounts(this);
		this.productCategory = new ProductCategory();
		this.store = StoreTypes.PM;
	}	

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((addedDate == null) ? 0 : addedDate.hashCode());
		result = prime * result + ((advertType == null) ? 0 : advertType.hashCode());
		result = prime * result + ((amounts == null) ? 0 : amounts.hashCode());
		result = prime * result + ((annotation == null) ? 0 : annotation.hashCode());
		
		result = prime * result + ((customer == null) ? 0 : customer.hashCode());
		result = prime * result + ((delivery == null) ? 0 : delivery.hashCode());
		result = prime * result + ((items == null) ? 0 : items.hashCode());
		result = prime * result + ((modifiedDate == null) ? 0 : modifiedDate.hashCode());
		result = prime * result + no;
		result = prime * result + ((orderDate == null) ? 0 : orderDate.hashCode());
		result = prime * result + ((orderType == null) ? 0 : orderType.hashCode());
		result = prime * result + ((paymentType == null) ? 0 : paymentType.hashCode());
		result = prime * result + ((productCategory == null) ? 0 : productCategory.hashCode());
		result = prime * result + ((sourceType == null) ? 0 : sourceType.hashCode());
		result = prime * result + ((status == null) ? 0 : status.hashCode());
		result = prime * result + ((emailStatus == null) ? 0 : emailStatus.hashCode());
		result = prime * result + ((statuses == null) ? 0 : statuses.hashCode());		
		
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		Order other = (Order) obj;
		if (addedDate == null) {
			if (other.addedDate != null)
				return false;
		} else if (!addedDate.equals(other.addedDate))
			return false;
		if (advertType != other.advertType)
			return false;
		if (amounts == null) {
			if (other.amounts != null)
				return false;
		} else if (!amounts.equals(other.amounts))
			return false;
		if (annotation == null) {
			if (other.annotation != null)
				return false;
		} else if (!annotation.equals(other.annotation))
			return false;
		if (customer == null) {
			if (other.customer != null)
				return false;
		} else if (!customer.equals(other.customer))
			return false;
		if (delivery == null) {
			if (other.delivery != null)
				return false;
		} else if (!delivery.equals(other.delivery))
			return false;
		if (items == null) {
			if (other.items != null)
				return false;
		} else if (!items.equals(other.items))
			return false;
		if (modifiedDate == null) {
			if (other.modifiedDate != null)
				return false;
		} else if (!modifiedDate.equals(other.modifiedDate))
			return false;
		if (no != other.no)
			return false;
		if (orderDate == null) {
			if (other.orderDate != null)
				return false;
		} else if (!orderDate.equals(other.orderDate))
			return false;
		if (orderType != other.orderType)
			return false;
		if (paymentType != other.paymentType)
			return false;
		if (productCategory == null) {
			if (other.productCategory != null)
				return false;
		} else if (!productCategory.equals(other.productCategory))
			return false;
		if (sourceType != other.sourceType)
			return false;
		if (status != other.status)
			return false;
		if (emailStatus != other.emailStatus)
			return false;
		if (statuses == null) {
			if (other.statuses != null)
				return false;
		} else if (!statuses.equals(other.statuses))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Order [id=" + this.getId() + ", viewOrderNo=" + getViewNo() 
			+ ", orderDate=" + orderDate == null ? "" : DateTimeUtils.defaultFormatDate(orderDate) + ", "		
			+ ", productCategory=" + productCategory == null ? "" : productCategory		
			+ ", orderType=" + orderType + ", sourceType=" + sourceType + ", advertType=" + advertType
			+ ", paymentType=" + paymentType + ", status=" + status
			+ ", customer=" + customer == null ? "" : customer
			+ ", amounts=" + amounts == null ? "" : amounts	
			+ ", items=" + items == null ? "" : items
			+ ", comments=" + comments == null ? "" : comments
			+ ", statuses=" + statuses == null ? "" : statuses
			//+ ", annotation=" + annotation 
			+ ", delivery=" + delivery + "]";
	}	
}
