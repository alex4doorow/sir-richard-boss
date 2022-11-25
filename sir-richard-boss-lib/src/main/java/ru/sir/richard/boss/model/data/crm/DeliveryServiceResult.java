package ru.sir.richard.boss.model.data.crm;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import ru.sir.richard.boss.model.data.Address;

public class DeliveryServiceResult {
	
	private BigDecimal postpayAmount;
	private BigDecimal deliveryAmount;
		
	private BigDecimal deliveryPrice;	
	private BigDecimal deliveryInsurance;
	private BigDecimal deliveryPostpayFee;
	
	private BigDecimal deliveryFullPrice; // стоимость доставки (прайс + все комиссии)
	private BigDecimal deliveryCustomerSummary; // сколько берем с клиента за доставку
	private BigDecimal deliverySellerSummary; // сколько берет c нас сдэк за доставку и инкассацию
	
	private Integer deliveryPeriodMin = 0;
	private Integer deliveryPeriodMax = 0;
			
	private String termText;
	private String to;
	private String parcelType;
	private String weightText;
	private String localTimeText;
	private String errorText;
	
	private List<Address> addresses;
	
	public DeliveryServiceResult() {
		postpayAmount = BigDecimal.ZERO;
		deliveryAmount = BigDecimal.ZERO;
		
		deliveryPrice = BigDecimal.ZERO;
		deliveryInsurance = BigDecimal.ZERO;
		deliveryPostpayFee = BigDecimal.ZERO;
		
		deliveryFullPrice = BigDecimal.ZERO;
		deliveryCustomerSummary = BigDecimal.ZERO;
		deliverySellerSummary = BigDecimal.ZERO;
		
		this.localTimeText = "";
			
		addresses = new ArrayList<Address>();
	}

	public Integer getDeliveryPeriodMin() {
		return deliveryPeriodMin;
	}

	public void setDeliveryPeriodMin(Integer deliveryPeriodMin) {
		this.deliveryPeriodMin = deliveryPeriodMin;
	}

	public Integer getDeliveryPeriodMax() {
		return deliveryPeriodMax;
	}

	public void setDeliveryPeriodMax(Integer deliveryPeriodMax) {
		this.deliveryPeriodMax = deliveryPeriodMax;
	}

	public BigDecimal getPostpayAmount() {
		return postpayAmount;
	}

	public void setPostpayAmount(BigDecimal postpayAmount) {
		this.postpayAmount = postpayAmount;
	}

	public BigDecimal getDeliveryAmount() {
		return deliveryAmount;
	}

	public void setDeliveryAmount(BigDecimal deliveryAmount) {
		this.deliveryAmount = deliveryAmount;
	}
	
	public BigDecimal getDeliveryPrice() {
		return deliveryPrice;
	}

	public void setDeliveryPrice(BigDecimal deliveryPrice) {
		this.deliveryPrice = deliveryPrice;
	}

	public BigDecimal getDeliveryInsurance() {
		return deliveryInsurance;
	}

	public void setDeliveryInsurance(BigDecimal deliveryInsurance) {
		this.deliveryInsurance = deliveryInsurance;
	}

	public BigDecimal getDeliveryPostpayFee() {
		return deliveryPostpayFee;
	}

	public void setDeliveryPostpayFee(BigDecimal deliveryPostpayFee) {
		this.deliveryPostpayFee = deliveryPostpayFee;
	}

	public BigDecimal getDeliveryCustomerSummary() {
		return deliveryCustomerSummary;
	}

	public void setDeliveryCustomerSummary(BigDecimal deliveryCustomerSummary) {
		this.deliveryCustomerSummary = deliveryCustomerSummary;
	}

	public BigDecimal getDeliveryFullPrice() {
		return deliveryFullPrice;
	}

	public void setDeliveryFullPrice(BigDecimal deliveryFullPrice) {
		this.deliveryFullPrice = deliveryFullPrice;
	}

	public BigDecimal getDeliverySellerSummary() {
		return deliverySellerSummary;
	}

	public void setDeliverySellerSummary(BigDecimal deliverySellerSummary) {
		this.deliverySellerSummary = deliverySellerSummary;
	}

	public String getTermText() {
		return termText;
	}

	public void setTermText(String termText) {
		this.termText = termText;
	}
	
	public String getLocalTimeText() {
		return localTimeText;
	}

	public void setLocalTimeText(String localTimeText) {
		this.localTimeText = localTimeText;
	}

	public String getErrorText() {
		return errorText;
	}

	public void setErrorText(String errorText) {
		this.errorText = errorText;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}	
	
	public String getParcelType() {
		return parcelType;
	}

	public void setParcelType(String parcelType) {
		this.parcelType = parcelType;
	}

	public String getWeightText() {
		return weightText;
	}

	public void setWeightText(String weightText) {
		this.weightText = weightText;
	}

	public String getInfo() {
		String result;
		if (StringUtils.isEmpty(errorText)) {
			result = "доставляем: " + this.to + ", " + "срок: " + this.termText + ", " + "вес: " + this.weightText;
			if (StringUtils.isNoneEmpty(this.localTimeText)) {
				result += " сейчас в месте назначения: " + this.localTimeText;				
			}
			
		} else {
			result = errorText;
		}				
		return result;
	}
	
	public List<Address> getAddresses() {
		return addresses;
	}

	public void setAddresses(List<Address> addresses) {
		this.addresses = addresses;
	}

	public static DeliveryServiceResult createEmpty() {
		return new DeliveryServiceResult();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((deliveryAmount == null) ? 0 : deliveryAmount.hashCode());
		result = prime * result + ((postpayAmount == null) ? 0 : postpayAmount.hashCode());
		result = prime * result + ((termText == null) ? 0 : termText.hashCode());
		result = prime * result + ((to == null) ? 0 : to.hashCode());
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
		DeliveryServiceResult other = (DeliveryServiceResult) obj;
		if (deliveryAmount == null) {
			if (other.deliveryAmount != null)
				return false;
		} else if (!deliveryAmount.equals(other.deliveryAmount))
			return false;
		if (postpayAmount == null) {
			if (other.postpayAmount != null)
				return false;
		} else if (!postpayAmount.equals(other.postpayAmount))
			return false;
		if (termText == null) {
			if (other.termText != null)
				return false;
		} else if (!termText.equals(other.termText))
			return false;
		if (to == null) {
			if (other.to != null)
				return false;
		} else if (!to.equals(other.to))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "DeliveryServiceResult [postpayAmount=" + postpayAmount + ", deliveryAmount=" + deliveryAmount
				+ ", termText=" + termText + ", to=" + to + "]";
	}	

}
