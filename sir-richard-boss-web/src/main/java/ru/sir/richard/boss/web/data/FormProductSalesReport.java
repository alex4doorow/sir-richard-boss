package ru.sir.richard.boss.web.data;

import java.util.Date;

import ru.sir.richard.boss.model.data.conditions.ProductSalesReportConditions;
import ru.sir.richard.boss.model.utils.DateTimeUtils;

public class FormProductSalesReport extends ProductSalesReportConditions {

	public FormProductSalesReport(Date periodStart, Date periodEnd) {
		super(periodStart, periodEnd);		
	}
	
	public FormProductSalesReport() {
		super(DateTimeUtils.sysDate(), DateTimeUtils.sysDate());
	}

}
