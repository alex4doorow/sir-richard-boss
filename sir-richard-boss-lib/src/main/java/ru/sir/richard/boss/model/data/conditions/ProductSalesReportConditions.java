package ru.sir.richard.boss.model.data.conditions;

import java.math.BigDecimal;
import java.util.Date;

public class ProductSalesReportConditions extends AnyReportConditions {
	
	private BigDecimal advertBudget;
			
	public ProductSalesReportConditions(Date periodStart, Date periodEnd) {
		super(periodStart, periodEnd);
		advertBudget = BigDecimal.ZERO;		
	}

	public BigDecimal getAdvertBudget() {
		return advertBudget;
	}

	public void setAdvertBudget(BigDecimal advertBudget) {
		this.advertBudget = advertBudget;
	}

}
