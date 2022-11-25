package ru.sir.richard.boss.model.data.conditions;

import java.util.Date;

public abstract class AnyReportConditions extends AnyConditions {
	
	public AnyReportConditions() {
		super();
	}
	
	public AnyReportConditions(Date periodStart, Date periodEnd) {
		super(periodStart, periodEnd);
		
	}

	

}
