package ru.sir.richard.boss.model.data.conditions;

import java.math.BigDecimal;
import java.util.Date;

import ru.sir.richard.boss.model.types.ReportQueryNames;

public class ProductSalesReportConditions extends AnyReportConditions {
		
	private BigDecimal advertBudget;
	private ReportQueryNames queryName;
			
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

	public ReportQueryNames getQueryName() {
		return queryName;
	}

	public void setQueryName(ReportQueryNames queryName) {
		this.queryName = queryName;
	}	

}
