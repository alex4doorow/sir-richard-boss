package ru.sir.richard.boss.model.data.conditions;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ru.sir.richard.boss.model.data.Product;
import ru.sir.richard.boss.model.types.OrderTypes;
import ru.sir.richard.boss.model.types.ReportPeriodTypes;
import ru.sir.richard.boss.model.utils.DateTimeUtils;
import ru.sir.richard.boss.model.utils.TextUtils;

public class OrderConditions extends AnyConditions {
		
	private int customerId;
	private String no;
	private String crmNo;
	private CustomerConditions customerConditions;
	private String trackCode;
	private String deliveryAddress;
	private Product product;
	private boolean periodExist;
	private boolean trackCodeNotExist;
		
	public OrderConditions(ReportPeriodTypes reportPeriodType, Date inputDate) {
		super(reportPeriodType, inputDate);
		customerConditions = new CustomerConditions();

		this.product = Product.createEmpty();
		periodExist = true;
		trackCodeNotExist = false;

	}
	
	public OrderConditions(ReportPeriodTypes reportPeriodType) {
		this(reportPeriodType, DateTimeUtils.sysDate());		
	}
	
	public OrderConditions() {
		this(ReportPeriodTypes.CURRENT_MONTH, DateTimeUtils.sysDate());
	}

	public boolean isTrackCodeNotExist() {
		return trackCodeNotExist;
	}
	
	public boolean isTrackCodeExist() {
		return !trackCodeNotExist;
	}

	public void setTrackCodeNotExist(boolean trackCodeNotExist) {
		this.trackCodeNotExist = trackCodeNotExist;
	}

	public boolean isPeriodExist() {
		return periodExist;
	}

	public void setPeriodExist(boolean periodExist) {
		this.periodExist = periodExist;
	}

	public int getCustomerId() {
		return customerId;
	}

	public void setCustomerId(int customerId) {
		this.customerId = customerId;
	}

	public String getNo() {
		return no;
	}

	public void setNo(String no) {
		this.no = no;
	}	

	public String getCrmNo() {
		return crmNo;
	}

	public void setCrmNo(String crmNo) {
		this.crmNo = crmNo;
	}

	public CustomerConditions getCustomerConditions() {
		return customerConditions;
	}

	public void setCustomerConditions(CustomerConditions customerConditions) {
		this.customerConditions = customerConditions;
	}	

	

	

	


	public String getTrackCode() {
		return trackCode;
	}

	public void setTrackCode(String trackCode) {
		this.trackCode = trackCode;
	}	

	public String getDeliveryAddress() {
		return deliveryAddress;
	}

	public void setDeliveryAddress(String deliveryAddress) {
		this.deliveryAddress = deliveryAddress;
	}

		
	

	

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	
	
	public List<String> getViewOrderTypes() {
		List<String> results = new ArrayList<String>();
		for (OrderTypes status : this.getTypes()) {
			results.add(status.getAnnotation());
		}		
		return results;
	}
	
	
		
	
	
	public String getIdsTypes() {
		List<Integer> ids = new ArrayList<Integer>();
		for (OrderTypes type : this.getTypes()) {
			ids.add(type.getId());
		}	
		return TextUtils.convertIntegerListToDelimitedString(ids);
	}
	
	
	
	
	public void setViewOrderTypes(List<String> annotationStatuses) {
		
		Set<OrderTypes> statuses = new HashSet<OrderTypes>();
		for (String annotationStatus : annotationStatuses) {
			statuses.add(OrderTypes.getValueByAnnotation(annotationStatus));
		}
		this.setTypes(statuses);
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((crmNo == null) ? 0 : crmNo.hashCode());
		result = prime * result + ((customerConditions == null) ? 0 : customerConditions.hashCode());
		result = prime * result + customerId;
		result = prime * result + ((deliveryAddress == null) ? 0 : deliveryAddress.hashCode());
		result = prime * result + ((no == null) ? 0 : no.hashCode());
		result = prime * result + (periodExist ? 1231 : 1237);
		result = prime * result + ((product == null) ? 0 : product.hashCode());
		result = prime * result + ((trackCode == null) ? 0 : trackCode.hashCode());
		result = prime * result + (trackCodeNotExist ? 1231 : 1237);
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
		OrderConditions other = (OrderConditions) obj;
		if (crmNo == null) {
			if (other.crmNo != null)
				return false;
		} else if (!crmNo.equals(other.crmNo))
			return false;
		if (customerConditions == null) {
			if (other.customerConditions != null)
				return false;
		} else if (!customerConditions.equals(other.customerConditions))
			return false;
		if (customerId != other.customerId)
			return false;
		if (deliveryAddress == null) {
			if (other.deliveryAddress != null)
				return false;
		} else if (!deliveryAddress.equals(other.deliveryAddress))
			return false;
		if (no == null) {
			if (other.no != null)
				return false;
		} else if (!no.equals(other.no))
			return false;
		if (periodExist != other.periodExist)
			return false;
		if (product == null) {
			if (other.product != null)
				return false;
		} else if (!product.equals(other.product))
			return false;
		if (trackCode == null) {
			if (other.trackCode != null)
				return false;
		} else if (!trackCode.equals(other.trackCode))
			return false;
		if (trackCodeNotExist != other.trackCodeNotExist)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "OrderConditions [reportPeriodType=" + this.getReportPeriodType() + ", reportPeriodMonth= " + this.getReportPeriodMonth() + ", reportPeriodYear=" + this.getReportPeriodYear() 
				+ ", no=" + no + ", customerConditions=" + customerConditions + ", trackCode=" + trackCode
				+ ", period=" + this.getPeriod() + ", statuses=" + this.getStatuses() + "]";
	}

	
	
}
