package ru.sir.richard.boss.model.data;

import java.text.ParseException;
import java.util.Date;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

import ru.sir.richard.boss.model.utils.DateTimeUtils;
import ru.sir.richard.boss.model.utils.Pair;

@EqualsAndHashCode
@ToString
public class CourierInfo extends Pair<Date> {
		
	private Date deliveryDate;	

	public CourierInfo() {
		super();
	}
			
	public CourierInfo(Date start, Date end) {
		super(start, end);
	}	

	public Date getDeliveryDate() {
		return deliveryDate;
	}
	
	public void setDeliveryDate(Date deliveryDate) {
		this.deliveryDate = deliveryDate;
	}
		
	public String getStartTime() {
		if (getStart() != null) {
			return DateTimeUtils.formatDate(getStart(), DateTimeUtils.DATE_FORMAT_HH_mm);
		} else {
			return null;
		}
	}

	public void setStartTime(String startTime) {
		
		if (StringUtils.isEmpty(startTime)) {
			setStart(null);
		}
		if ("  :  ".equals(startTime)) {			
			setStart(null);
		}
		try {
			this.setStart(DateTimeUtils.stringToDate(startTime, DateTimeUtils.DATE_FORMAT_HH_mm));
		} catch (ParseException e) {
			e.printStackTrace();
		} 
	}

	public String getEndTime() {
		if (getEnd() != null) {
			return DateTimeUtils.formatDate(getEnd(), DateTimeUtils.DATE_FORMAT_HH_mm);
		} else {
			return null;
		}
	}

	public void setEndTime(String endTime) {
		if (StringUtils.isEmpty(endTime)) {
			setEnd(null);
		}
		try {
			this.setEnd(DateTimeUtils.stringToDate(endTime, DateTimeUtils.DATE_FORMAT_HH_mm));
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	public String timeInterval() {
		if (this.getStart() == null && this.getEnd() == null) {
			return "";
		}
		String startTime = StringUtils.defaultString(DateTimeUtils.formatDate(this.getStart(), 
				DateTimeUtils.DATE_FORMAT_HH_mm));
		String endTime = StringUtils.defaultString(DateTimeUtils.formatDate(this.getEnd(), 
				DateTimeUtils.DATE_FORMAT_HH_mm));
		return startTime + " - " + endTime;
	}	
	
	@Override
	public CourierInfo clone() throws CloneNotSupportedException  {
		CourierInfo clone = new CourierInfo();
		clone.setStart(this.getStart() == null ? null : (Date) this.getStart().clone()); 
		clone.setEnd(this.getEnd() == null ? null : (Date) this.getEnd().clone());
		clone.deliveryDate = this.deliveryDate == null ? null : (Date) deliveryDate.clone();				
		return clone;
	}
}
