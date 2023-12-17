package ru.sir.richard.boss.data.raw;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import ru.sir.richard.boss.model.data.Address;
import ru.sir.richard.boss.model.data.AnyCustomer;
import ru.sir.richard.boss.model.data.Comment;
import ru.sir.richard.boss.model.data.Order;
import ru.sir.richard.boss.model.data.OrderItem;
import ru.sir.richard.boss.model.types.AddressTypes;
import ru.sir.richard.boss.model.types.CommentTypes;
import ru.sir.richard.boss.model.types.Countries;
import ru.sir.richard.boss.model.types.CustomerTypes;
import ru.sir.richard.boss.model.types.DeliveryTypes;
import ru.sir.richard.boss.model.types.OrderAdvertTypes;
import ru.sir.richard.boss.model.types.OrderSourceTypes;
import ru.sir.richard.boss.model.types.OrderStatuses;
import ru.sir.richard.boss.model.types.OrderTypes;
import ru.sir.richard.boss.model.types.PaymentTypes;
import ru.sir.richard.boss.model.utils.DateTimeUtils;

public class OrderDataRaw {
		
	private int orderNo;
	private int orderSubNo;
		
	private Date orderDate;
	private int quantity;
	private double price;
	private ProductDataRaw product;
	private CustomerDataRaw customer;
	
	private double deliveryPrice;
	private String deliveryTrackCode;
	private String deliveryComment;
		
	private double billAmount;
	private double supplierAmount;
	private double mergeAmount;
	private double postpaySDEKAmount;
	private double postpayPostAmount;
	private double postpayCompanyAmount;
	private double totalAmount;
	
	private OrderTypes orderType;
	private OrderSourceTypes sourceType;
	private OrderAdvertTypes advertType;
	private OrderStatuses status;
	private DeliveryTypes deliveryType;
	private PaymentTypes paymentType;
		
	private List<OrderItem> orderItems = new ArrayList<OrderItem>(); 
	private Set<Comment> converterComments; 
			
	public OrderDataRaw(CustomerTypes customerType, Countries country) {
		super();
		product = new ProductDataRaw();
		customer = new CustomerDataRaw(customerType, country);		
	}

	public int getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(int orderNo) {
		this.orderNo = orderNo;
	}
	
	public int getOrderSubNo() {
		return orderSubNo;
	}
	
	public String getViewOrderNo() {
		if (orderSubNo == 0) {
			return String.valueOf(orderNo);
		} else {
			return String.valueOf(orderNo) + "-" + String.valueOf(orderSubNo);	
		}
	}

	public void setOrderSubNo(int orderSubNo) {
		this.orderSubNo = orderSubNo;
	}

	public Date getOrderDate() {
		return orderDate;
	}

	public void setOrderDate(Date orderDate) {
		this.orderDate = orderDate;
	}
	
	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}
	
	public DeliveryTypes getDeliveryType() {
		return deliveryType;
	}

	public void setDeliveryType(DeliveryTypes deliveryType) {
		this.deliveryType = deliveryType;
	}

	public double getDeliveryPrice() {
		return deliveryPrice;
	}

	public void setDeliveryPrice(double deliveryPrice) {
		this.deliveryPrice = deliveryPrice;
	}
	
	public String getDeliveryTrackCode() {
		return deliveryTrackCode;
	}

	public void setDeliveryTrackCode(String deliveryTrackCode) {
		this.deliveryTrackCode = deliveryTrackCode;
	}
	

	public String getDeliveryComment() {
		return deliveryComment;
	}

	public void setDeliveryComment(String deliveryComment) {
		this.deliveryComment = deliveryComment;
	}

	public ProductDataRaw getProduct() {
		return product;
	}

	public CustomerDataRaw getCustomer() {
		return customer;
	}	
	
	public double getBillAmount() {
		return billAmount;
	}

	public void setBillAmount(double billAmount) {
		this.billAmount = billAmount;
	}

	public double getSupplierAmount() {
		return supplierAmount;
	}

	public void setSupplierAmount(double supplierAmount) {
		this.supplierAmount = supplierAmount;
	}

	public double getMergeAmount() {
		return mergeAmount;
	}

	public void setMergeAmount(double mergeAmount) {
		this.mergeAmount = mergeAmount;
	}	

	public double getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(double totalAmount) {
		this.totalAmount = totalAmount;
	}

	public double getPostpaySDEKAmount() {
		return postpaySDEKAmount;
	}

	public void setPostpaySDEKAmount(double postpaySDEKAmount) {
		this.postpaySDEKAmount = postpaySDEKAmount;
	}

	public double getPostpayPostAmount() {
		return postpayPostAmount;
	}

	public void setPostpayPostAmount(double postpayPostAmount) {
		this.postpayPostAmount = postpayPostAmount;
	}

	public double getPostpayCompanyAmount() {
		return postpayCompanyAmount;
	}

	public void setPostpayCompanyAmount(double postpayCompanyAmount) {
		this.postpayCompanyAmount = postpayCompanyAmount;
	}
	
	public double getPostpayAmount() {
		return postpayCompanyAmount + postpayPostAmount + postpaySDEKAmount;
	}

	public OrderStatuses getStatus() {
		return status;
	}

	public void setStatus(OrderStatuses status) {
		this.status = status;
	}		

	public OrderAdvertTypes getAdvertType() {
		return advertType;
	}

	public void setAdvertType(OrderAdvertTypes advertType) {
		this.advertType = advertType;
	}

	public OrderTypes getOrderType() {
		return orderType;
	}

	public void setOrderType(OrderTypes orderType) {
		this.orderType = orderType;
	}

	public OrderSourceTypes getSourceType() {
		return sourceType;
	}

	public void setSourceType(OrderSourceTypes sourceType) {
		this.sourceType = sourceType;
	}		

	public PaymentTypes getPaymentType() {
		return paymentType;
	}

	public void setPaymentType(PaymentTypes paymentType) {
		this.paymentType = paymentType;
	}

	public List<OrderItem> getOrderItems() {
		return orderItems;
	}

	public void setOrderItems(List<OrderItem> orderItems) {
		this.orderItems = orderItems;
	}	

	public Set<Comment> getConverterComments() {
		return converterComments;
	}

	public void setConverterComments(Set<Comment> converterComments) {
		this.converterComments = converterComments;
	}
	
	public String getConverterComment(CommentTypes commentType, String key) {
		if (StringUtils.isEmpty(key)) {
			return null;
		}
		for (Comment comment : getConverterComments()) {
			if (comment.getCommentType() == commentType && comment.getKey().equalsIgnoreCase((key))) {
				return comment.getValue();
			}				
		}
		return null;
	}	

	public Order deepClone() {	
		/*
		i:0, Order [id=0, no=859, orderDate=08.01.2015, 
		productCategory=ProductCategory [id=0, name=, group=null], 
				orderType=ORDER, sourceType=LID, advertType=ADVERT, paymentType=PREPAYMENT, status=FINISHED, 
				
				customer=Customer [id=0, firstName=Андрей, lastName=Андрей,phoneNumber=(903) 172-38-61, email=, 
					mainAddress=Address [id=0, addressType=MAIN, country=RUSSIA, address=null]], 
				
				amounts=OrderAmount [amounts={POSTPAY=0, TOTAL=0, TOTAL_WITH_DELIVERY=0, SUPPLIER=0, MARGIN=0, BILL=0}], 
				
				items=[OrderItem [id=0, no=1, product=[Product [id=0, name=iSocket 707]], 
				price=5990.0, quantity=1, discountRate=0, supplierAmount=0, amount=0]], 
				
				   comments=null, 
				statuses=[],	
				delivery=, ]]
		
		*/
		
		AnyCustomer customer = this.getCustomer().deepClone();
				
		Order result = new Order();
		result.setCustomer(customer);
		result.setNo(orderNo);
		result.setSubNo(orderSubNo);		
		result.setOrderDate(orderDate);
		
		result.setOrderType(this.orderType);
		result.setSourceType(this.sourceType);		
		result.setAdvertType(this.advertType);
		result.setPaymentType(this.paymentType);		
		result.setStatus(this.status);
		
		// items
		int no = 1;
		for (OrderItem orderItem : this.getOrderItems()) {			
			orderItem.setNo(no);			
			result.addItem(orderItem);
			no++;
		}
		
		// comments
		result.setComments(this.getConverterComments());
		
		// delivery
		result.getDelivery().setDeliveryType(this.deliveryType);
		
		//result.getDelivery().setAnnotation(this.getDeliveryType() + " " + this.getCustomer().getAddress());
		result.getDelivery().setPrice(BigDecimal.valueOf(deliveryPrice));
		result.getDelivery().setTrackCode(deliveryTrackCode);
		result.getDelivery().setAnnotation(deliveryComment);
		
		Address deliveryAddress = new Address(customer.getCountry(), AddressTypes.MAIN);
		deliveryAddress.setAddress(this.getCustomer().getAddress());
		result.getDelivery().setAddress(deliveryAddress);
		result.getDelivery().getCourierInfo().setDeliveryDate(orderDate);
		
		result.getAmounts().setTotalWithDelivery(BigDecimal.valueOf(this.getTotalAmount()));
		result.getAmounts().setTotal(BigDecimal.valueOf(this.getTotalAmount()));
		result.getAmounts().setBill(BigDecimal.valueOf(this.getBillAmount()));
		result.getAmounts().setSupplier(BigDecimal.valueOf(this.getSupplierAmount()));
		result.getAmounts().setMargin(BigDecimal.valueOf(this.getMergeAmount()));
		result.getAmounts().setPostpay(BigDecimal.valueOf(this.getPostpayAmount()));		
		return result;	
		
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((customer == null) ? 0 : customer.hashCode());
		long temp;
		temp = Double.doubleToLongBits(deliveryPrice);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((deliveryTrackCode == null) ? 0 : deliveryTrackCode.hashCode());
		
		result = prime * result + ((deliveryType == null) ? 0 : deliveryType.hashCode());
		result = prime * result + ((orderDate == null) ? 0 : orderDate.hashCode());
		result = prime * result + orderNo;
		temp = Double.doubleToLongBits(price);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((product == null) ? 0 : product.hashCode());
		result = prime * result + quantity;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		OrderDataRaw other = (OrderDataRaw) obj;
		if (customer == null) {
			if (other.customer != null)
				return false;
		} else if (!customer.equals(other.customer))
			return false;
		if (Double.doubleToLongBits(deliveryPrice) != Double.doubleToLongBits(other.deliveryPrice))
			return false;
		if (deliveryTrackCode == null) {
			if (other.deliveryTrackCode != null)
				return false;
		} else if (!deliveryTrackCode.equals(other.deliveryTrackCode))
			return false;
		if (deliveryType == null) {
			if (other.deliveryType != null)
				return false;
		} else if (!deliveryType.equals(other.deliveryType))
			return false;
		if (orderDate == null) {
			if (other.orderDate != null)
				return false;
		} else if (!orderDate.equals(other.orderDate))
			return false;
		if (orderNo != other.orderNo)
			return false;
		if (Double.doubleToLongBits(price) != Double.doubleToLongBits(other.price))
			return false;
		if (product == null) {
			if (other.product != null)
				return false;
		} else if (!product.equals(other.product))
			return false;
		if (quantity != other.quantity)
			return false;
		return true;
	}

	@Override
	public String toString() {
		
		return "OrderDataRaw [viewOrderNo=" + getViewOrderNo() + ", orderDate=" + DateTimeUtils.formatDate(orderDate, DateTimeUtils.DATE_FORMAT_dd_MM_yyyy)
				+ ", orderType=" + orderType + ", status=" + status 
				+ ", quantity=" + quantity + ", price="	+ price  
				+ ", product=" + product + ", customer=" + customer + ", deliveryType=" + deliveryType
				+ ", deliveryPrice=" + deliveryPrice + ", deliveryTrackCode=" + deliveryTrackCode + "\b\r"
				+ "{"
				+ "totalAmount=" + totalAmount + ", billAmount=" +billAmount 
				+ ", supplierAmount=" + supplierAmount + ", mergeAmount=" + mergeAmount 
				+ ", postpaySDEKAmount=" + postpaySDEKAmount + ", postpayPostAmount=" + postpayPostAmount + ", postpayCompanyAmount=" + postpayCompanyAmount 
				+ "}"
				+ "converterComments=" + converterComments
				+ "]";
	}

}
