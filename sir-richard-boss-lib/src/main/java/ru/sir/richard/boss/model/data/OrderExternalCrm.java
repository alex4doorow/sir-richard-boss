package ru.sir.richard.boss.model.data;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import ru.sir.richard.boss.model.types.CrmStatuses;
import ru.sir.richard.boss.model.types.CrmTypes;

@Data
@NoArgsConstructor
public class OrderExternalCrm extends AnyId {

	private Order parent;
	private CrmTypes crm;
	private CrmStatuses status;
	private Long parentId;
	private String parentCode;

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
}
