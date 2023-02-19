package ru.sir.richard.boss.model.data.conditions;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.format.annotation.DateTimeFormat;

import ru.sir.richard.boss.model.types.CustomerTypes;
import ru.sir.richard.boss.model.types.DeliveryTypes;
import ru.sir.richard.boss.model.types.OrderAdvertTypes;
import ru.sir.richard.boss.model.types.OrderStatuses;
import ru.sir.richard.boss.model.types.OrderTypes;
import ru.sir.richard.boss.model.types.PaymentTypes;
import ru.sir.richard.boss.model.types.ReportPeriodTypes;
import ru.sir.richard.boss.utils.DateTimeUtils;
import ru.sir.richard.boss.utils.Pair;
import ru.sir.richard.boss.utils.TextUtils;

public abstract class AnyConditions {	
	
	private Pair<Date> period;
	
	private ReportPeriodTypes reportPeriodType;
	private int reportPeriodMonth;
	private int reportPeriodQuarter;
	private int reportPeriodHalfYear;
	private int reportPeriodYear;
	
	private Set<DeliveryTypes> deliveryTypes;	
	private Set<CustomerTypes> customerTypes;	
	private Set<PaymentTypes> paymentTypes;
	private Set<OrderAdvertTypes> advertTypes;
		
	private Set<OrderStatuses> statuses;
	private Set<OrderTypes> types;
	
	public AnyConditions() {
		deliveryTypes = new HashSet<DeliveryTypes>();
		customerTypes = new HashSet<CustomerTypes>();
		paymentTypes = new HashSet<PaymentTypes>();
		advertTypes = new HashSet<OrderAdvertTypes>();
		
		statuses = new HashSet<OrderStatuses>();
		types = new HashSet<OrderTypes>();
		this.reportPeriodType = ReportPeriodTypes.ANY_MONTH;
		this.period = new Pair<>(DateTimeUtils.sysDate(), DateTimeUtils.sysDate());		
	}
	
	public AnyConditions(Date periodStart, Date periodEnd) {
		this();
		this.period = new Pair<>(periodStart, periodEnd);
	}
	
	public AnyConditions(ReportPeriodTypes reportPeriodType, Date inputDate) {
		this();
		this.reportPeriodType = reportPeriodType;		
		setPeriodByType(this.reportPeriodType, DateTimeUtils.sysDate());
	
	}
	
	@DateTimeFormat(pattern = "dd.MM.yyyy")
	public Date getPeriodStart() {
		return period.getStart();
	}
	
	@DateTimeFormat(pattern = "dd.MM.yyyy")
	public void setPeriodStart(Date periodStart) {
		period.setStart(periodStart);
	}
	
	@DateTimeFormat(pattern = "dd.MM.yyyy")
	public Date getPeriodEnd() {
		return period.getEnd();
	}
		
	@DateTimeFormat(pattern = "dd.MM.yyyy")
	public void setPeriodEnd(Date periodEnd) {
		period.setEnd(periodEnd);
	}
	
	public Pair<Date> findPeriodByType(ReportPeriodTypes type, Date date) {
		Date start;
		Date end;
				
		if (type == ReportPeriodTypes.CURRENT_MONTH) {
			start = DateTimeUtils.firstDayOfMonth(date); 
			end = DateTimeUtils.lastDayOfMonth(start);		
		} else if (type == ReportPeriodTypes.PRIOR_MONTH) {
			Date firstDateOfMonth = DateTimeUtils.firstDayOfMonth(date);			
			start = DateTimeUtils.firstDayOfMonth(DateTimeUtils.beforeAnyDayOfDate(firstDateOfMonth, 1));			 
			end = DateTimeUtils.lastDayOfMonth(start);		
		} else if (type == ReportPeriodTypes.CURRENT_QUARTER) {
			start = DateTimeUtils.firstDayOfQuarter(date); 
			end = DateTimeUtils.lastDayOfQuarter(start);		
		} else if (type == ReportPeriodTypes.CURRENT_YEAR) {
			start = DateTimeUtils.firstDayOfYear(date); 
			end = DateTimeUtils.lastDayOfYear(start);		
		} else if (type == ReportPeriodTypes.LAST_7_DAYS) {			 
			end = DateTimeUtils.truncateDate(date);
			start = DateTimeUtils.beforeAnyDayOfDate(end, 7);			
		} else if (type == ReportPeriodTypes.LAST_30_DAYS) {			 
			end = DateTimeUtils.truncateDate(date);
			start = DateTimeUtils.beforeAnyDayOfDate(end, 30);			
		} else if (type == ReportPeriodTypes.LAST_90_DAYS) {			 
			end = DateTimeUtils.truncateDate(date);
			start = DateTimeUtils.beforeAnyDayOfDate(end, 90);			
		} else if (type == ReportPeriodTypes.CURRENT_HALF_YEAR) {			 
			start = DateTimeUtils.firstDayOfHalfYear(date); 
			end = DateTimeUtils.lastDayOfHalfYear(start);				
		} else {
			start = DateTimeUtils.truncateDate(new Date());
			end = DateTimeUtils.truncateDate(new Date());
		}
		return new Pair<Date>(start, end);		
	}	
	
	public void setPeriodByType(ReportPeriodTypes type, Date inputDate) {
		Pair<Date> period = findPeriodByType(type, inputDate);
		setPeriod(period);
		
		this.reportPeriodMonth = DateTimeUtils.monthOfDate(period.getStart());
		this.reportPeriodQuarter = DateTimeUtils.quarterOfDate(period.getStart());
		this.reportPeriodHalfYear = DateTimeUtils.halfYearOfDate(period.getStart());		
		this.reportPeriodYear = DateTimeUtils.yearOfDate(period.getStart());		
	}	
	
	public ReportPeriodTypes getReportPeriodType() {
		return reportPeriodType;
	}

	public void setReportPeriodType(ReportPeriodTypes reportPeriodType) {
		this.reportPeriodType = reportPeriodType;
	}

	public int getReportPeriodMonth() {
		return reportPeriodMonth;
	}

	public void setReportPeriodMonth(int reportPeriodMonth) {
		this.reportPeriodMonth = reportPeriodMonth;
	}

	public int getReportPeriodQuarter() {
		return reportPeriodQuarter;
	}

	public void setReportPeriodQuarter(int reportPeriodQuarter) {
		this.reportPeriodQuarter = reportPeriodQuarter;
	}

	public int getReportPeriodHalfYear() {
		return reportPeriodHalfYear;
	}

	public void setReportPeriodHalfYear(int reportPeriodHalfYear) {
		this.reportPeriodHalfYear = reportPeriodHalfYear;
	}

	public int getReportPeriodYear() {
		return reportPeriodYear;
	}

	public void setReportPeriodYear(int reportPeriodYear) {
		this.reportPeriodYear = reportPeriodYear;
	}

	public Pair<Date> getPeriod() {
		return period;
	}

	public void setPeriod(Pair<Date> period) {
		this.period = period;
	}
		
	public Set<OrderStatuses> getStatuses() {
		return statuses;
	}

	public void setStatuses(Set<OrderStatuses> statuses) {
		this.statuses = statuses;
	}
	
	public void setDirtyStatuses(Set<Object> dirtyResults) {
		
		Set<OrderStatuses> results = new HashSet<OrderStatuses>();
		for (Object dirtyResult : dirtyResults) {
			OrderStatuses result = (OrderStatuses) dirtyResult;
			results.add(result);
		}
		this.statuses = results;
	}
	
	public Set<Object> getDirtyStatuses() {
		
		Set<Object> dirtyResults = new HashSet<Object>();	
		for (OrderStatuses result : getStatuses()) {
			Object dirtyResult = (Object) result;
			dirtyResults.add(dirtyResult);
		}
		return dirtyResults;
	}


	public Set<OrderTypes> getTypes() {
		return types;
	}

	public void setTypes(Set<OrderTypes> types) {
		this.types = types;
	}

	public Set<CustomerTypes> getCustomerTypes() {
		return customerTypes;
	}

	public void setCustomerTypes(Set<CustomerTypes> customerTypes) {
		this.customerTypes = customerTypes;
	}		
	
	public Set<PaymentTypes> getPaymentTypes() {
		return paymentTypes;
	}

	public void setPaymentTypes(Set<PaymentTypes> paymentTypes) {
		this.paymentTypes = paymentTypes;
	} 	
	
	public Set<OrderAdvertTypes> getAdvertTypes() {
		return advertTypes;
	}

	public void setAdvertTypes(Set<OrderAdvertTypes> advertTypes) {
		this.advertTypes = advertTypes;
	}

	public Set<DeliveryTypes> getDeliveryTypes() {
		return deliveryTypes;
	}

	public void setDeliveryTypes(Set<DeliveryTypes> deliveryTypes) {
		this.deliveryTypes = deliveryTypes;
	}
	
	public void setDirtyDeliveryTypes(Set<Object> dirtyResults) {
		
		Set<DeliveryTypes> results = new HashSet<DeliveryTypes>();
		for (Object dirtyResult : dirtyResults) {
			DeliveryTypes result = (DeliveryTypes) dirtyResult;
			results.add(result);
		}
		this.deliveryTypes = results;
	}
	
	public Set<Object> getDirtyDeliveryTypes() {
		
		Set<Object> dirtyResults = new HashSet<Object>();	
		for (DeliveryTypes result : getDeliveryTypes()) {
			Object dirtyResult = (Object) result;
			dirtyResults.add(dirtyResult);
		}
		return dirtyResults;
	}
	
	public void setDirtyCustomerTypes(Set<Object> dirtyResults) {
		
		Set<CustomerTypes> results = new HashSet<CustomerTypes>();
		for (Object dirtyResult : dirtyResults) {
			CustomerTypes result = (CustomerTypes) dirtyResult;
			results.add(result);
		}
		this.customerTypes = results;
	}
	
	public Set<Object> getDirtyCustomerTypes() {
		
		Set<Object> dirtyResults = new HashSet<Object>();	
		for (CustomerTypes result : getCustomerTypes()) {
			Object dirtyResult = (Object) result;
			dirtyResults.add(dirtyResult);
		}
		return dirtyResults;
	}
	
	public void setDirtyPaymentTypes(Set<Object> dirtyResults) {
		
		Set<PaymentTypes> results = new HashSet<PaymentTypes>();
		for (Object dirtyResult : dirtyResults) {
			PaymentTypes result = (PaymentTypes) dirtyResult;
			results.add(result);
		}
		this.paymentTypes = results;
	}
	
	public Set<Object> getDirtyPaymentTypes() {
		
		Set<Object> dirtyResults = new HashSet<Object>();	
		for (PaymentTypes result : getPaymentTypes()) {
			Object dirtyResult = (Object) result;
			dirtyResults.add(dirtyResult);
		}
		return dirtyResults;
	}
	
	public void setDirtyAdvertTypes(Set<Object> dirtyResults) {
		
		Set<OrderAdvertTypes> results = new HashSet<OrderAdvertTypes>();
		for (Object dirtyResult : dirtyResults) {
			OrderAdvertTypes result = (OrderAdvertTypes) dirtyResult;
			results.add(result);
		}
		this.advertTypes = results;
	}
	
	public Set<Object> getDirtyAdvertTypes() {
		
		Set<Object> dirtyResults = new HashSet<Object>();	
		for (OrderAdvertTypes result : getAdvertTypes()) {
			Object dirtyResult = (Object) result;
			dirtyResults.add(dirtyResult);
		}
		return dirtyResults;
	}		
	
	public List<String> getViewDeliveryTypes() {
		List<String> results = new ArrayList<String>();
		for (DeliveryTypes value : this.deliveryTypes) {
			results.add(value.getAnnotation());
		}		
		return results;
	}
	
	public void setViewDeliveryTypes(List<String> annotationStatuses) {		
		Set<DeliveryTypes> statuses = new HashSet<DeliveryTypes>();
		for (String annotationStatus : annotationStatuses) {
			statuses.add(DeliveryTypes.getValueByAnnotation(annotationStatus));
		}
		this.deliveryTypes = statuses;		
	}	
	
	public List<String> getViewCustomerTypes() {
		List<String> results = new ArrayList<String>();
		for (CustomerTypes value : this.customerTypes) {
			results.add(value.getLongName());
		}		
		return results;
	}
	
	public void setViewCustomerTypes(List<String> annotationValues) {
		
		Set<CustomerTypes> values = new HashSet<CustomerTypes>();
		for (String annotationStatus : annotationValues) {
			values.add(CustomerTypes.getValueByAnnotation(annotationStatus));
		}
		this.customerTypes = values;		
	}		
	
	public List<String> getViewPaymentTypes() {
		List<String> results = new ArrayList<String>();
		for (PaymentTypes value : this.paymentTypes) {
			results.add(value.getAnnotation());
		}		
		return results;
	}
	
	public void setViewPaymentTypes(List<String> annotationValues) {
		
		Set<PaymentTypes> values = new HashSet<PaymentTypes>();
		for (String annotationStatus : annotationValues) {
			values.add(PaymentTypes.getValueByAnnotation(annotationStatus));
		}
		this.paymentTypes = values;
		
	}
	
	public List<String> getViewAdvertTypes() {
		List<String> results = new ArrayList<String>();
		for (OrderAdvertTypes value : this.advertTypes) {
			results.add(value.getAnnotation());
		}		
		return results;
	}
	
	public void setViewAdvertTypes(List<String> annotationValues) {
		
		Set<OrderAdvertTypes> values = new HashSet<OrderAdvertTypes>();
		for (String annotationStatus : annotationValues) {
			values.add(OrderAdvertTypes.getValueByAnnotation(annotationStatus));
		}
		this.advertTypes = values;		
	}		
	
	public List<String> getViewStatuses() {
		List<String> results = new ArrayList<String>();
		for (OrderStatuses status : this.getStatuses()) {
			results.add(status.getAnnotation());
		}		
		return results;
	}
	
	public String getIdsStatuses() {
		List<Integer> ids = new ArrayList<Integer>();
		for (OrderStatuses status : this.getStatuses()) {
			ids.add(status.getId());
		}	
		return TextUtils.convertIntegerListToDelimitedString(ids);
	}
	
	public void setViewStatuses(List<String> annotationStatuses) {
		
		Set<OrderStatuses> statuses = new HashSet<OrderStatuses>();
		for (String annotationStatus : annotationStatuses) {
			statuses.add(OrderStatuses.getValueByAnnotation(annotationStatus));
		}
		this.setStatuses(statuses);
	}
	
	public String getIdsDeliveryTypes() {
		List<Integer> ids = new ArrayList<Integer>();
		for (DeliveryTypes deliveryType : this.deliveryTypes) {
			ids.add(deliveryType.getId());
		}	
		return TextUtils.convertIntegerListToDelimitedString(ids);
	}
	
	public String getIdsCustomerTypes() {
		List<Integer> ids = new ArrayList<Integer>();
		for (CustomerTypes customerType : this.customerTypes) {
			ids.add(customerType.getId());
		}	
		return TextUtils.convertIntegerListToDelimitedString(ids);
	}
	
	public String getIdsPaymentTypes() {
		List<Integer> ids = new ArrayList<Integer>();
		for (PaymentTypes paymentType : this.paymentTypes) {
			ids.add(paymentType.getId());
		}	
		return TextUtils.convertIntegerListToDelimitedString(ids);
	}
	
	public String getIdsAdvertTypes() {
		List<Integer> ids = new ArrayList<Integer>();
		for (OrderAdvertTypes advertType : this.advertTypes) {
			ids.add(advertType.getId());
		}	
		return TextUtils.convertIntegerListToDelimitedString(ids);
	}
	


}
