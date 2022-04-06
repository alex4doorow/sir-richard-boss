package ru.sir.richard.boss.web.data;

import java.util.Date;

import ru.sir.richard.boss.model.data.conditions.AnyReportConditions;
import ru.sir.richard.boss.model.utils.DateTimeUtils;

public class FormSalesFunnelReport extends AnyReportConditions {
	
	public FormSalesFunnelReport(Date periodStart, Date periodEnd) {
		super(periodStart, periodEnd);		
	}
	
	public FormSalesFunnelReport() {
		super(DateTimeUtils.sysDate(), DateTimeUtils.sysDate());
	}

}
