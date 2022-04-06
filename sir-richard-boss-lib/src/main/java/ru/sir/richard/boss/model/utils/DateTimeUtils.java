package ru.sir.richard.boss.model.utils;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.TextStyle;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import ru.sir.richard.boss.model.types.ReportPeriodTypes;

import java.time.LocalDate;
import java.time.Month;
import java.time.ZoneId;

public class DateTimeUtils {
		
	public static String DATE_FORMAT_dd_MM_yyyy = "dd.MM.yyyy";
	public static String DATE_FORMAT_HH_mm = "HH:mm";

	public static String formatDate(Date date, String dateFormatString) {
		if (date == null) {
			return "";			
		}				
		try {
			SimpleDateFormat formatter = new SimpleDateFormat(dateFormatString);
			String format = formatter.format(date);
			return format;
		} catch (Exception ex) {
			return "error: [date=" + date + ", dateFormatString=" + dateFormatString + "]";
		}		
	}
	
	public static String defaultFormatDate(Date date) {
		return formatDate(date, DATE_FORMAT_dd_MM_yyyy);
	}

	public static int dateToShortYear(Date date) {		
		if (date == null) {
			return 0;
		}
		SimpleDateFormat formatter = new SimpleDateFormat("yy");
		String format = formatter.format(date);
		return Integer.parseInt(format);
	}

	public static int dateToLongYear(Date date) {		
		if (date == null) {
			return 0;
		}
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy");
		String format = formatter.format(date);
		return Integer.parseInt(format);
	}
	
	public static java.util.Date stringToDate(String inputString, String dateFormatString) throws ParseException {
		if (StringUtils.isEmpty(inputString)) {
			return null;
		}
		SimpleDateFormat formatter = new SimpleDateFormat(dateFormatString);		
		return formatter.parse(inputString);	
	}
	
	public static java.util.Date defaultFormatStringToDate(String inputString) throws ParseException {
		return stringToDate(inputString, DATE_FORMAT_dd_MM_yyyy);
	}
	
	public static java.util.Date timestampToDate(java.sql.Timestamp inputTimestamp) {
		
		java.util.Date modifiedDateTime;
    	Timestamp modifiedTimestamp = inputTimestamp;
    	if (modifiedTimestamp != null) {
    		modifiedDateTime = new java.util.Date(modifiedTimestamp.getTime());
    	} else {
    		modifiedDateTime = null;
    	}
    	return modifiedDateTime;		
	}
	
	public static java.sql.Timestamp dateToTimestamp(java.util.Date inputDate) {
		if (inputDate == null) {
			return null;
		}		
		Calendar cal = Calendar.getInstance();
		cal.setTime(inputDate);
		cal.set(Calendar.MILLISECOND, 0);		
		return new java.sql.Timestamp(cal.getTimeInMillis());		
	}
	
	public static java.util.Date truncateDate(java.util.Date inputDate) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(inputDate);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);		
	    return calendar.getTime();
	}
	
	public static java.util.Date beforeAnyDate(java.util.Date inputDate, int incrementDay) {
		return afterAnyDate(inputDate, -incrementDay);
	}
	
	public static java.util.Date afterAnyDate(java.util.Date inputDate, int incrementDay) {
		Calendar calendar = Calendar.getInstance();		
		calendar.setTime(inputDate);
		calendar.add(Calendar.DAY_OF_YEAR, incrementDay);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);		
	    return calendar.getTime();
	}
	
	public static java.util.Date afterAnyDateOnlyWork(java.util.Date inputDate, int incrementDay) {
		
		java.util.Date result = inputDate;
		
		int i = 0;
		
		while (i < incrementDay) {
			result = afterAnyDate(result, 1);
			int dayOfWeek = Integer.valueOf(formatDate(result, "u"));
			if (dayOfWeek == 6 || dayOfWeek == 7) {
				continue;
			}
			i++;			
		}
	    return result;
	}
	
	public static java.util.Date beforeDate(java.util.Date inputDate) {
		
		Calendar calendar = Calendar.getInstance();		
		calendar.setTime(inputDate);
		calendar.add(Calendar.DAY_OF_YEAR, -1);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);		
	    return calendar.getTime();
	}
	
	public static java.util.Date firstDayOfMonth(java.util.Date inputDate) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(inputDate);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);		
	    calendar.set(Calendar.DAY_OF_MONTH, 1);
	    return calendar.getTime();
	}
	
	public static java.util.Date lastDayOfMonth(java.util.Date inputDate) {		
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(inputDate);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		
		calendar.add(Calendar.MONTH, 1);
	    calendar.add(Calendar.DAY_OF_MONTH, -1);
	    return calendar.getTime();
	}
	
	public static java.util.Date firstDayOfQuarter(java.util.Date inputDate) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(inputDate);
		int month = calendar.get(Calendar.MONTH) + 1;
		int firstQuarterMonth;
		if (month == 1 || month == 2 || month == 3) {
			firstQuarterMonth = 1;
		} else if (month == 4 || month == 5 || month == 6) {
			firstQuarterMonth = 4;
		} else if (month == 7 || month == 8 || month == 9) {
			firstQuarterMonth = 7;
		} else if (month == 10 || month == 11 || month == 12) {
			firstQuarterMonth = 10;
		} else {
			firstQuarterMonth = -1;
		}
		
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);		
	    calendar.set(Calendar.DAY_OF_MONTH, 1);
	    calendar.set(Calendar.MONTH, firstQuarterMonth - 1);
	    return calendar.getTime();
	}
	
	public static java.util.Date lastDayOfQuarter(java.util.Date inputDate) {		
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(inputDate);
		int month = calendar.get(Calendar.MONTH) + 1;
		int lastQuarterMonth;
		if (month == 1 || month == 2 || month == 3) {
			lastQuarterMonth = 3;
		} else if (month == 4 || month == 5 || month == 6) {
			lastQuarterMonth = 6;
		} else if (month == 7 || month == 8 || month == 9) {
			lastQuarterMonth = 9;
		} else if (month == 10 || month == 11 || month == 12) {
			lastQuarterMonth = 12;
		} else {
			lastQuarterMonth = -1;
		}		
		
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		calendar.set(Calendar.DAY_OF_MONTH, 1);		
		calendar.set(Calendar.MONTH, lastQuarterMonth - 1);
	    return calendar.getTime();
	}
	
	public static java.util.Date firstDayOfHalfYear(java.util.Date inputDate) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(inputDate);
		int month = calendar.get(Calendar.MONTH) + 1;
		int firstHalfYearMonth;
		if (month == 1 || month == 2 || month == 3 || month == 4 || month == 5 || month == 6) {
			firstHalfYearMonth = 1;
		} else {
			firstHalfYearMonth = 7;
		}		
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);		
	    calendar.set(Calendar.DAY_OF_MONTH, 1);
	    calendar.set(Calendar.MONTH, firstHalfYearMonth - 1);
	    return calendar.getTime();
	}
	
	public static java.util.Date lastDayOfHalfYear(java.util.Date inputDate) {		
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(inputDate);
		int month = calendar.get(Calendar.MONTH) + 1;
		int lastHalfYearMonth;
		if (month == 1 || month == 2 || month == 3 || month == 4 || month == 5 || month == 6) {
			lastHalfYearMonth = 6;
		} else {
			lastHalfYearMonth = 12;
		}				
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		calendar.set(Calendar.DAY_OF_MONTH, 1);		
		calendar.set(Calendar.MONTH, lastHalfYearMonth - 1);
	    return calendar.getTime();
	}
	
	public static java.util.Date firstDayOfYear(java.util.Date inputDate) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(inputDate);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);	
		calendar.set(Calendar.MONTH, 0);
	    calendar.set(Calendar.DAY_OF_MONTH, 1);
	    return calendar.getTime();
	}
	
	public static java.util.Date lastDayOfYear(java.util.Date inputDate) {		
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(inputDate);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.MONTH, 11);	    
	    return calendar.getTime();
	}
	
	public static java.util.Date beforeAnyDayOfDate(java.util.Date inputDate, int value) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(inputDate);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);		
		calendar.add(Calendar.DATE, -value);
	    return calendar.getTime();
	}	
	
	public static int monthOfDate(java.util.Date inputDate) {
		Date date = new Date();
		LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		int month = localDate.getMonthValue();
		return month;		
	}
	
	public static int quarterOfDate(java.util.Date inputDate) {
		Date date = new Date();
		LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		int month = localDate.getMonthValue();
		if (month == 1 || month == 2 || month == 3) {
			return 1;
		} if (month == 4 || month == 5 || month == 6) {
			return 2;
		} if (month == 7 || month == 8 || month == 9) {
			return 3;
		} if (month == 10 || month == 11 || month == 12) {
			return 4;
		} else {
			return -1;
		}
	}
	
	public static int halfYearOfDate(java.util.Date inputDate) {
		Date date = new Date();
		LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		int month = localDate.getMonthValue();
		if (month == 1 || month == 2 || month == 3 || month == 4 || month == 5 || month == 6) {
			return 1;
		} else {
			return 2;
		}
	}
	
	public static int yearOfDate(java.util.Date inputDate) {
		Date date = new Date();
		LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		int year = localDate.getYear();
		return year;		
	}
	
	public static int firstMonthOfQuarter(int quarter) {
		if (quarter == 1) {
			return 1;
		} else if (quarter == 2) {
			return 4;
		} if (quarter == 3) {
			return 7;
		} if (quarter == 4) {
			return 10;
		} else {
			return 1;	
		}
	}
	
	public static int firstMonthOfHalfYear(int halfYear) {
		if (halfYear == 1) {
			return 1;
		} else {
			return 2;
		}		
	}	
	
	public static java.util.Date calendarPeriodStart(int month, int year) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.MONTH, month - 1);
		calendar.set(Calendar.YEAR, year);
		java.util.Date start = calendar.getTime();
		return firstDayOfMonth(start);		
	} 
	
	public static Pair<java.util.Date> getPeriodByMonthYear(int periodType, int month, int quarter, int halfYear, int year) {
		
		java.util.Date start;
		java.util.Date end;
		int calcMonth;
		if (periodType == ReportPeriodTypes.ANY_MONTH.getId() || periodType == ReportPeriodTypes.CURRENT_MONTH.getId()) {			
			calcMonth = month;
			start = calendarPeriodStart(calcMonth, year);	
			end = lastDayOfMonth(start);			
		} else if (periodType == ReportPeriodTypes.ANY_QUARTER.getId() || periodType == ReportPeriodTypes.CURRENT_QUARTER.getId()) {
			calcMonth = firstMonthOfQuarter(quarter);	
			start = calendarPeriodStart(calcMonth, year);
			end = lastDayOfQuarter(start);				
		} else if (periodType == ReportPeriodTypes.ANY_HALF_YEAR.getId() || periodType == ReportPeriodTypes.CURRENT_QUARTER.getId()) {
			calcMonth = firstMonthOfHalfYear(halfYear);
			start = calendarPeriodStart(calcMonth, year);
			end = lastDayOfHalfYear(start);				
		} else if (periodType == ReportPeriodTypes.ANY_YEAR.getId() || periodType == ReportPeriodTypes.CURRENT_YEAR.getId()) {
			calcMonth = 1;
			start = calendarPeriodStart(calcMonth, year);
			end = lastDayOfYear(start);
		} else {
			start = new java.util.Date();
			end = new java.util.Date();
		}
		return new Pair<java.util.Date>(start, end); 
	}
	
	public static String getTextPeriodByReportPeriodType(int periodType, int month, int quarter, int halfYear, int year) {
		if (periodType == ReportPeriodTypes.ANY_MONTH.getId()) {
			return getMonths().get(month) + " " + year;
		} else if (periodType == ReportPeriodTypes.ANY_QUARTER.getId()) {
			return String.valueOf(quarter) + "-й квартал " + year;
		} else if (periodType == ReportPeriodTypes.ANY_HALF_YEAR.getId()) {
			return String.valueOf(halfYear) + "-е полугодие " + year;
		} else if (periodType == ReportPeriodTypes.ANY_YEAR.getId()) {
			return String.valueOf(year);
		} else {
			return "";
		}
	}
	
	public static Map<Integer, String> getMonths() {		
		Map<Integer, String> months = new HashMap<Integer, String>();
		for (int i = 1; i <= 12; i++) {
			Month monthNumber = Month.of(i);
			Locale locale = Locale.forLanguageTag("ru");
			String monthText = monthNumber.getDisplayName(TextStyle.FULL_STANDALONE, locale);
			months.put(i, monthText);
		}		
		return months;
	}
	
	public static Map<Integer, String> getQuarters() {		
		Map<Integer, String> quarters = new HashMap<Integer, String>();
		for (int i = 1; i <= 4; i++) {
			String quarterText = String.valueOf(i) + "-й квартал";			
			quarters.put(i, quarterText);
		}		
		return quarters;
	}
	
	public static Map<Integer, String> getHalfYears() {		
		Map<Integer, String> halfYears = new HashMap<Integer, String>();
		for (int i = 1; i <= 2; i++) {
			String halfYearText = String.valueOf(i) + "-е полугодие";			
			halfYears.put(i, halfYearText);
		}		
		return halfYears;
	}

	public static Date sysDate() {		
		return truncateDate(new Date());
	}

}
