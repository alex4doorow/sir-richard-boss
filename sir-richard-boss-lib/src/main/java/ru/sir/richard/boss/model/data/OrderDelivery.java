package ru.sir.richard.boss.model.data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import ru.sir.richard.boss.model.types.AddressTypes;
import ru.sir.richard.boss.model.types.CarrierStatuses;
import ru.sir.richard.boss.model.types.Countries;
import ru.sir.richard.boss.model.types.DeliveryPrices;
import ru.sir.richard.boss.model.types.DeliveryTypes;
import ru.sir.richard.boss.model.types.OrderStatuses;
import ru.sir.richard.boss.model.types.PaymentDeliveryTypes;
import ru.sir.richard.boss.model.utils.DateTimeUtils;

public class OrderDelivery extends AnyId {
	
	private Order parent;
	
	private BigDecimal price; // значение, которое ввел оператор
	private BigDecimal factCustomerPrice; // сколько платит покупатель
	private BigDecimal factSellerPrice; // сколько платит продавец
	
	private DeliveryTypes deliveryType;
	private DeliveryPrices deliveryPrice;
	private PaymentDeliveryTypes paymentDeliveryType;
	private Address address;
	private Person recipient;
	private CarrierStatuses carrierStatus;

	private String annotation;
	private String trackCode;
	
	private List<OrderDeliveryShipment> shipments; // грузовые места
	
	public OrderDelivery() {
		this.parent = null;
		//this.courierInfo = new CourierInfo();
		deliveryType = DeliveryTypes.CDEK_PVZ_TYPICAL;
		deliveryPrice = DeliveryPrices.UNKNOWN;
		paymentDeliveryType = PaymentDeliveryTypes.CUSTOMER;
		this.address = new Address(Countries.RUSSIA, AddressTypes.MAIN);
		this.carrierStatus = CarrierStatuses.CDEK_CREATE;
		
		this.price = BigDecimal.ZERO;
		this.factSellerPrice = BigDecimal.ZERO;
		this.factCustomerPrice = BigDecimal.ZERO;
		
		this.shipments = new ArrayList<OrderDeliveryShipment>();
	}
	
	public OrderDelivery(Order parent) {
		this();
		this.parent = parent;	
	}

	public BigDecimal getPrice() {
		return price == null ? BigDecimal.ZERO : price;
	}
	
	public BigDecimal getCustomerPrice() {
		if (paymentDeliveryType == PaymentDeliveryTypes.CUSTOMER) {
			return getPrice();
		} else {
			return BigDecimal.ZERO;
		}
	}
	
	public BigDecimal getSellerPrice() {
		if (paymentDeliveryType == PaymentDeliveryTypes.SELLER) {
			return getPrice();
		} else {
			return BigDecimal.ZERO;
		}
	}
	
	public BigDecimal getFactCustomerPrice() {
		return this.factCustomerPrice;
	}

	public void setFactCustomerPrice(BigDecimal factSellerPrice) {
		this.factCustomerPrice = factSellerPrice;
	}

	public BigDecimal getFactSellerPrice() {
		return this.factSellerPrice;
	}

	public void setFactSellerPrice(BigDecimal factSellerPrice) {
		this.factSellerPrice = factSellerPrice;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public DeliveryTypes getDeliveryType() {
		return deliveryType;
	}

	public void setDeliveryType(DeliveryTypes deliveryType) {
		this.deliveryType = deliveryType;
	}
	
	public DeliveryPrices getDeliveryPrice() {
		return deliveryPrice;
	}

	public void setDeliveryPrice(DeliveryPrices deliveryPrice) {
		this.deliveryPrice = deliveryPrice;
	}

	public PaymentDeliveryTypes getPaymentDeliveryType() {
		return paymentDeliveryType;
	}

	public void setPaymentDeliveryType(PaymentDeliveryTypes paymentDeliveryType) {
		this.paymentDeliveryType = paymentDeliveryType;
	}

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

	public Person getRecipient() {
		return recipient;
	}

	public void setRecipient(Person recipient) {
		this.recipient = recipient;
	}

	public String getAnnotation() {
		return annotation;
	}

	public void setAnnotation(String annotation) {
		this.annotation = annotation;
	}

	public String getTrackCode() {
		return trackCode;
	}

	public void setTrackCode(String trackCode) {
		this.trackCode = trackCode;
	}	
	
	public CourierInfo getCourierInfo() {
		return address.getCarrierInfo().getCourierInfo();
	}
	
	public CarrierStatuses getCarrierStatus() {
		return carrierStatus;
	}

	public void setCarrierStatus(CarrierStatuses carrierStatus) {
		this.carrierStatus = carrierStatus;
	}	

	public List<OrderDeliveryShipment> getShipments() {
		return shipments;
	}

	public void setShipments(List<OrderDeliveryShipment> shipments) {
		this.shipments = shipments;
	}

	public String getViewDeliveryInfo() {
		if (this.getAddress() == null || this.getAddress().getAddress() == null) {
			return "";
		}
		String result = this.getAddress().getAddress().replace("\"", "");
		if (getDeliveryType().isCourier() 
				&& (parent.getStatus() == OrderStatuses.BID || parent.getStatus() == OrderStatuses.APPROVED || parent.getStatus() == OrderStatuses.PAY_WAITING || parent.getStatus() == OrderStatuses.PAY_ON || parent.getStatus() == OrderStatuses.DELIVERING)) {
			result += ", доставляем: " + DateTimeUtils.formatDate(this.getAddress().getCarrierInfo().getCourierInfo().getDeliveryDate(), "dd.MM.yyyy, EEE") + " " + this.getAddress().getCarrierInfo().getCourierInfo().timeInterval();
		} else if (getDeliveryType() == DeliveryTypes.YANDEX_MARKET_FBS 
				&& (parent.getStatus() == OrderStatuses.BID || parent.getStatus() == OrderStatuses.APPROVED || parent.getStatus() == OrderStatuses.DELIVERING)) {
			result += ", отгружаем: " + DateTimeUtils.formatDate(this.getAddress().getCarrierInfo().getCourierInfo().getDeliveryDate(), "dd.MM.yyyy, EEE") + " " + this.getAddress().getCarrierInfo().getCourierInfo().timeInterval();
		} else if (getDeliveryType() == DeliveryTypes.OZON_FBS 
				&& (parent.getStatus() == OrderStatuses.BID || parent.getStatus() == OrderStatuses.APPROVED || parent.getStatus() == OrderStatuses.DELIVERING)) {
			result += ", отгружаем: " + DateTimeUtils.formatDate(this.getAddress().getCarrierInfo().getCourierInfo().getDeliveryDate(), "dd.MM.yyyy, EEE") + " " + this.getAddress().getCarrierInfo().getCourierInfo().timeInterval();
		}
		return result;
	}
	
	public boolean isCustomerEqualsRecipient() {
		return (recipient == null || recipient.getPersonId() <= 0);
	}
	
	public boolean isCustomerNotEqualsRecipient() {
		return !isCustomerEqualsRecipient();
	}
		
	@Override
	protected void clear() {
		this.deliveryType = DeliveryTypes.UNKNOWN;
	}	
	
	@Override
	public OrderDelivery clone() throws CloneNotSupportedException  {
		OrderDelivery clone = (OrderDelivery) super.clone();
		clone.parent = this.parent;
		
		clone.price = this.price == null ? null : new BigDecimal(this.price.toString());
		clone.factSellerPrice = this.factSellerPrice == null ? null : new BigDecimal(this.factSellerPrice.toString());
		
		clone.deliveryType = this.deliveryType;
		clone.deliveryPrice = this.deliveryPrice;
		clone.paymentDeliveryType = this.paymentDeliveryType;
		clone.address = this.address == null ? null : this.address.clone();
		clone.carrierStatus = this.carrierStatus;		
		clone.annotation = this.annotation == null ? null : new String(this.annotation);
		clone.trackCode = this.trackCode == null ? null : new String(this.trackCode);
		clone.recipient = this.recipient == null ? null : this.recipient;
		return clone;
	}	

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((address == null) ? 0 : address.hashCode());
		result = prime * result + ((annotation == null) ? 0 : annotation.hashCode());
		result = prime * result + ((deliveryType == null) ? 0 : deliveryType.hashCode());
		result = prime * result + ((parent == null) ? 0 : parent.hashCode());
		result = prime * result + ((price == null) ? 0 : price.hashCode());
		result = prime * result + ((trackCode == null) ? 0 : trackCode.hashCode());
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
		OrderDelivery other = (OrderDelivery) obj;
		if (address == null) {
			if (other.address != null)
				return false;
		} else if (!address.equals(other.address))
			return false;
		if (annotation == null) {
			if (other.annotation != null)
				return false;
			
		} else if (!annotation.equals(other.annotation))
			return false;		
		if (deliveryType != other.deliveryType)
			return false;
		if (parent == null) {
			if (other.parent != null)
				return false;
		} else if (!parent.equals(other.parent))
			return false;
		if (price == null) {
			if (other.price != null)
				return false;
		} else if (!price.equals(other.price))
			return false;
		if (trackCode == null) {
			if (other.trackCode != null)
				return false;
		} else if (!trackCode.equals(other.trackCode))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "OrderDelivery [deliveryType=" + deliveryType + ", price=" + price + ", factCustomerPrice=" + factCustomerPrice + ", factSellerPrice=" + factSellerPrice 
				+ ", address=" + address 
				//+ ", courierInfo=CourierInfo [" + courierInfo.getDeliveryDate() + "]"
				+ ", annotation=" + annotation 
				+ ", trackCode=" + trackCode + "]";
				
	}
}
