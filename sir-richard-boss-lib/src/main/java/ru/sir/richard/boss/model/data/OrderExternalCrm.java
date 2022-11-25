package ru.sir.richard.boss.model.data;

import org.apache.commons.lang3.StringUtils;

import ru.sir.richard.boss.model.types.CrmStatuses;
import ru.sir.richard.boss.model.types.CrmTypes;

public class OrderExternalCrm extends AnyId {

	private Order parent;
	private CrmTypes crm;
	private CrmStatuses status;
	private Long parentId;
	private String parentCode;
		
	public OrderExternalCrm() {
		super();
		this.crm = CrmTypes.NONE;
		this.status = CrmStatuses.NONE;
		this.parentId = 0L;
		this.parentCode = "";
	}
	
	public OrderExternalCrm(CrmTypes crm, CrmStatuses status, Long parentId, String parentCode) {
		this();
		this.crm = crm;
		this.status = status;
		this.parentId = parentId;
		this.parentCode = parentCode;
	}
	
	public OrderExternalCrm(Order parent) {
		this();
		this.parent = parent;
	}
	
	public Order getParent() {
		return parent;
	}

	public void setParent(Order parent) {
		this.parent = parent;
	}

	public CrmTypes getCrm() {
		return crm;
	}

	public void setCrm(CrmTypes crm) {
		this.crm = crm;
	}

	public CrmStatuses getStatus() {
		return status;
	}

	public void setStatus(CrmStatuses status) {
		this.status = status;
	}
	
	public Long getParentId() {
		return parentId;
	}

	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}
		
	public String getParentCode() {
		return parentCode;
	}

	public void setParentCode(String parentCode) {
		this.parentCode = parentCode;
	}

	@Override
	public OrderExternalCrm clone() throws CloneNotSupportedException  {
		OrderExternalCrm clone = (OrderExternalCrm) super.clone();
		clone.parent = this.parent;
		clone.crm = this.crm;
		clone.status = this.status;
		clone.parentId = this.parentId;
		clone.parentCode = this.parentCode;
		return clone;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((crm == null) ? 0 : crm.hashCode());
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
		OrderExternalCrm other = (OrderExternalCrm) obj;
		if (crm != other.crm)
			return false;
		if (parent == null) {
			if (other.parent != null)
				return false;
		} else if (!parent.equals(other.parent))
			return false;
		if (parentId != other.parentId)
			return false;
		if (status != other.status)
			return false;
		return true;
	}

	@Override
	public String toString() {
		if (StringUtils.isEmpty(parentCode)) {
			return "OrderExternalCrm [crm=" + crm + ", status=" + status + ", parentId=" + parentId	+ "]";	
		} else {
			return "OrderExternalCrm [crm=" + crm + ", status=" + status + ", parentId=" + parentId + ", parentCode=" + parentCode + "]";			
		}
		
	}
}
