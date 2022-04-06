package ru.sir.richard.boss.web.response;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonView;

import ru.sir.richard.boss.model.types.ReportPeriodTypes;
import ru.sir.richard.boss.model.utils.DateTimeUtils;
import ru.sir.richard.boss.model.utils.Pair;

public class OrderConditionsAjaxResponseBody extends AnyAjaxResponseBody {
	
	@JsonView(Views.Public.class)
	ResultOrderConditionsAjaxResponseBody result;
	
	public OrderConditionsAjaxResponseBody() {
		super();
		this.result = new ResultOrderConditionsAjaxResponseBody();
	}
		
	public ResultOrderConditionsAjaxResponseBody getResult() {
		return result;
	}
	
	public void setResult(ResultOrderConditionsAjaxResponseBody result) {
		this.result = result;
	}
	
	public class ResultOrderConditionsAjaxResponseBody {
		
		@JsonView(Views.Public.class)
		ReportPeriodTypes reportPeriodType;
		
		@JsonView(Views.Public.class)
		Pair<Date> period;
		
		@JsonView(Views.Public.class)
		Integer reportPeriodMonth;
		
		@JsonView(Views.Public.class)
		Integer reportPeriodQuarter;
		
		@JsonView(Views.Public.class)
		Integer reportPeriodHalfYear;
		
		@JsonView(Views.Public.class)
		Integer reportPeriodYear;
		
		@JsonView(Views.Public.class)
		String viewPeriodStart;
		
		@JsonView(Views.Public.class)
		String viewPeriodEnd;
					
		public ReportPeriodTypes getReportPeriodType() {
			return reportPeriodType;
		}

		public void setReportPeriodType(ReportPeriodTypes reportPeriodType) {
			this.reportPeriodType = reportPeriodType;
		}

		public Pair<Date> getPeriod() {
			return period;
		}		

		public String getViewPeriodStart() {
			return viewPeriodStart;
		}

		public void setViewPeriodStart(String viewPeriodStart) {
			this.viewPeriodStart = viewPeriodStart;
		}

		public String getViewPeriodEnd() {
			return viewPeriodEnd;
		}

		public void setViewPeriodEnd(String viewPeriodEnd) {
			this.viewPeriodEnd = viewPeriodEnd;
		}

		public void setPeriod(Pair<Date> period) {
			this.period = period;
			this.viewPeriodStart = DateTimeUtils.defaultFormatDate(period.getStart());
			this.viewPeriodEnd = DateTimeUtils.defaultFormatDate(period.getEnd());			
		}

		public Integer getReportPeriodMonth() {
			return reportPeriodMonth;
		}

		public void setReportPeriodMonth(Integer reportPeriodMonth) {
			this.reportPeriodMonth = reportPeriodMonth;
		}

		public Integer getReportPeriodQuarter() {
			return reportPeriodQuarter;
		}

		public void setReportPeriodQuarter(Integer reportPeriodQuarter) {
			this.reportPeriodQuarter = reportPeriodQuarter;
		}

		public Integer getReportPeriodHalfYear() {
			return reportPeriodHalfYear;
		}

		public void setReportPeriodHalfYear(Integer reportPeriodHalfYear) {
			this.reportPeriodHalfYear = reportPeriodHalfYear;
		}

		public Integer getReportPeriodYear() {
			return reportPeriodYear;
		}

		public void setReportPeriodYear(Integer reportPeriodYear) {
			this.reportPeriodYear = reportPeriodYear;
		}		
	}

}
