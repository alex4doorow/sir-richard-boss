package ru.sir.richard.boss.model.data;

import java.util.Date;

import lombok.Data;
import ru.sir.richard.boss.model.types.OrderStatuses;
import ru.sir.richard.boss.model.types.OrderTypes;
import ru.sir.richard.boss.model.utils.DateTimeUtils;

/**
 * Контрольный срок исполнения заявки или КП
 * @author alex4doorow
 *
 */
@Data
public class OrderOffer {
	
	private int countDay;
	private Date startDate;
	
	private Order parent;
	
	public OrderOffer(Order parent) {
		super();	
		this.parent = parent;
	}

	public Date getExpiredDate() {
		if (parent == null) {
			return DateTimeUtils.sysDate();
		}
		if ((parent.getOrderType() == OrderTypes.BILL || parent.getOrderType() == OrderTypes.KP) && parent.getStatus() == OrderStatuses.BID) {
			return DateTimeUtils.afterAnyDateOnlyWork(this.startDate, this.countDay);
		} else {
			return parent.getOrderDate() == null ? DateTimeUtils.sysDate() : parent.getOrderDate();
		}
	}
}
