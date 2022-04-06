package ru.sir.richard.boss.model.data;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import ru.sir.richard.boss.model.types.OrderStatuses;

public class OrderStatusItem extends AnyId {
	
	private int no;
	private Order parent;
	private OrderStatuses status;
	private String crmStatus;
	private String crmSubStatus;
	private String comment;
	private Date addedDate;
	
	public OrderStatusItem() {
		super();	
		this.parent = null;
		this.no = 1;
		this.status = OrderStatuses.BID;
	}

	public OrderStatusItem(Order parent) {
		this();
		this.parent = parent;		
	}	

	public int getNo() {
		return no;
	}

	public void setNo(int no) {
		this.no = no;
	}

	public Order getParent() {
		return parent;
	}

	public void setParent(Order parent) {
		this.parent = parent;
	}

	public OrderStatuses getStatus() {
		return status;
	}

	public void setStatus(OrderStatuses status) {
		this.status = status;
	}

	public Date getAddedDate() {
		return addedDate;
	}

	public void setAddedDate(Date addedDate) {
		this.addedDate = addedDate;
	}
	
	public String getCrmStatus() {
		return crmStatus;
	}

	public void setCrmStatus(String crmStatus) {
		this.crmStatus = crmStatus;
	}

	public String getCrmSubStatus() {
		return crmSubStatus;
	}

	public void setCrmSubStatus(String crmSubStatus) {
		this.crmSubStatus = crmSubStatus;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	@Override
	public OrderStatusItem clone() throws CloneNotSupportedException  {
		OrderStatusItem clone = (OrderStatusItem) super.clone();
		clone.no = this.no;
		clone.parent = this.parent;
		clone.status = this.status;
		clone.addedDate = this.addedDate == null ? null : (Date) this.addedDate.clone();
		return clone;
	}	
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((addedDate == null) ? 0 : addedDate.hashCode());
		result = prime * result + no;
		result = prime * result + ((parent == null) ? 0 : parent.hashCode());
		result = prime * result + ((status == null) ? 0 : status.hashCode());
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
		OrderStatusItem other = (OrderStatusItem) obj;
		if (addedDate == null) {
			if (other.addedDate != null)
				return false;
		} else if (!addedDate.equals(other.addedDate))
			return false;
		if (no != other.no)
			return false;
		if (parent == null) {
			if (other.parent != null)
				return false;
		} else if (!parent.equals(other.parent))
			return false;
		if (status != other.status)
			return false;
		return true;
	}

	@Override
	public String toString() {
		String crmStatusInfo = "";
		if (StringUtils.isNotEmpty(crmStatus)) {
			crmStatusInfo = "[crmStatus=" + crmStatus + ", crmSubStatus=" + crmSubStatus + "]";
		}	
		return "OrderStatusItem [id=" + this.getId() + ", no=" + no + ", status=" + status + ", addedDate=" + addedDate + crmStatusInfo + "]";
	}

}
