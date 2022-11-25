package ru.sir.richard.boss.crm;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.text.ParseException;
import java.util.Date;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

import ru.sir.richard.boss.model.utils.DateTimeUtils;

@SpringBootTest
@PropertySource(value="classpath:application-it.properties", encoding="UTF-8")
public class CrmManagerTest {

	@Autowired
	CrmManager crmManager;
	
	@Autowired
	OzonExecutor ozonExecutor;	
	
	@Autowired
	private Environment environment;
	
	@Test
	public void testOzonExecutorRun() throws ParseException {
		
		String ozonName = environment.getProperty("ozon.market.ozon.default.customer.firstName");
		assertEquals("Озон", ozonName);
		
		Date executorDate = DateTimeUtils.defaultFormatStringToDate("20.11.2022");
		ozonExecutor.setExecutorDate(executorDate);
		//ozonExecutor.run();	
	}
			
	@Test
	public void testOzonOrderFinished() throws ParseException {
		
		Date executorDate;
		Date recalculatorDate;
		Date deliveredOderDate;
		
		executorDate = DateTimeUtils.stringToDate("27.10.2022", DateTimeUtils.DATE_FORMAT_dd_MM_yyyy);		
		recalculatorDate = ozonExecutor.findRecalculatedDate(executorDate);		
		assertEquals("25.10.2022", DateTimeUtils.defaultFormatDate(recalculatorDate));
		
		executorDate = DateTimeUtils.stringToDate("02.11.2022", DateTimeUtils.DATE_FORMAT_dd_MM_yyyy);		
		recalculatorDate = ozonExecutor.findRecalculatedDate(executorDate);		
		assertEquals("25.10.2022", DateTimeUtils.defaultFormatDate(recalculatorDate));
		
		executorDate = DateTimeUtils.stringToDate("16.10.2022", DateTimeUtils.DATE_FORMAT_dd_MM_yyyy);		
		recalculatorDate = ozonExecutor.findRecalculatedDate(executorDate);		
		assertEquals("15.10.2022", DateTimeUtils.defaultFormatDate(recalculatorDate));
		
		executorDate = DateTimeUtils.stringToDate("14.10.2022", DateTimeUtils.DATE_FORMAT_dd_MM_yyyy);		
		recalculatorDate = ozonExecutor.findRecalculatedDate(executorDate);		
		assertEquals("25.09.2022", DateTimeUtils.defaultFormatDate(recalculatorDate));
		
		executorDate = DateTimeUtils.stringToDate("01.10.2022", DateTimeUtils.DATE_FORMAT_dd_MM_yyyy);		
		recalculatorDate = ozonExecutor.findRecalculatedDate(executorDate);		
		assertEquals("25.09.2022", DateTimeUtils.defaultFormatDate(recalculatorDate));
		
		recalculatorDate = DateTimeUtils.stringToDate("25.10.2022", DateTimeUtils.DATE_FORMAT_dd_MM_yyyy);
		deliveredOderDate = ozonExecutor.findDeliveredOrdersDate(recalculatorDate);
		assertEquals("15.10.2022", DateTimeUtils.defaultFormatDate(deliveredOderDate));
		
		recalculatorDate = DateTimeUtils.stringToDate("15.10.2022", DateTimeUtils.DATE_FORMAT_dd_MM_yyyy);
		deliveredOderDate = ozonExecutor.findDeliveredOrdersDate(recalculatorDate);
		assertEquals("30.09.2022", DateTimeUtils.defaultFormatDate(deliveredOderDate));	
	}
	
	/*
	@Test
	public void testActualizationPostpay() throws ParseException {
		
		Date executorDate = DateTimeUtils.stringToDate("25.10.2022", DateTimeUtils.DATE_FORMAT_dd_MM_yyyy);		
		String result = ozonExecutor.actualizationPostpay(executorDate);
		
		logger.debug("testActualizationPostpay: {}", result);
	}
	*/

}
