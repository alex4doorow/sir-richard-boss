package ru.sir.richard.boss.web.controller;
// https://docs.spring.io/spring/docs/3.0.0.M3/reference/html/ch17s07.html

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.*;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.util.JRLoader;
import ru.sir.richard.boss.dao.ReportDao;
import ru.sir.richard.boss.model.data.Order;
import ru.sir.richard.boss.model.data.conditions.AnyReportConditions;
import ru.sir.richard.boss.model.data.conditions.ProductSalesReportConditions;
import ru.sir.richard.boss.model.data.report.ProductSalesReportBean;
import ru.sir.richard.boss.model.data.report.SalesFunnelReportBean;
import ru.sir.richard.boss.model.types.ReportPeriodTypes;
import ru.sir.richard.boss.model.types.ReportQueryNames;
import ru.sir.richard.boss.model.utils.DateTimeUtils;
import ru.sir.richard.boss.web.data.FormProductSalesReport;
import ru.sir.richard.boss.web.data.FormSalesFunnelReport;
import ru.sir.richard.boss.web.service.OrderService;
import ru.sir.richard.boss.web.validator.ProductSalesReportConditionsFormValidator;

@Controller
public class ReportController extends AnyController {

	@Autowired
	ServletContext servletContext; 
	
	@Autowired
	private OrderService orderService;
	
	@Autowired
	private ReportDao reportManager;
	
	@Autowired
	ProductSalesReportConditionsFormValidator productSalesReportConditionsFormValidator;

	@RequestMapping(value = "/orders/{id}/report/bill", method = RequestMethod.GET)
	@ResponseBody
	public void getReportOrderBill(@PathVariable("id") int id, HttpServletResponse response) throws JRException, IOException {
		createPdfOrderReport(id, 1, response, "/resources/jasperreports/bill-master", "v2",
				"order-bill-" + StringUtils.leftPad(String.valueOf(id), 4, "0") + ".pdf", true);
	}

	@RequestMapping(value = "/orders/{id}/report/sdek-bill", method = RequestMethod.GET)
	@ResponseBody
	public void getReportSdekOrderBill(@PathVariable("id") int id, HttpServletResponse response) throws JRException, IOException {
		createPdfOrderReport(id, 2, response, "/resources/jasperreports/bill-sdek-master", "v4",
				"order-bill-sdek-" + StringUtils.leftPad(String.valueOf(id), 4, "0") + ".pdf", true);
	}

	@RequestMapping(value = "/orders/{id}/report/post-russia-address-ticket", method = RequestMethod.GET)
	@ResponseBody
	public void getReportPostRussiaAddressTicket(@PathVariable("id") int id, HttpServletResponse response) throws JRException, IOException {
		createPdfOrderReport(id, 1, response, "/resources/jasperreports/post-address-ticket", "v2",
				"post-russia-address-ticket-" + StringUtils.leftPad(String.valueOf(id), 4, "0") + ".pdf", false);
	}
	
	@RequestMapping(value = "/orders/{id}/report/post-russia-address-ticket-postpay", method = RequestMethod.GET)
	@ResponseBody
	public void getReportPostRussiaAddressTicketWithPostpay(@PathVariable("id") int id, HttpServletResponse response) throws JRException, IOException {
		createPdfOrderReport(id, 1, response, "/resources/jasperreports/post-address-ticket-np", "v2",
				"post-russia-address-ticket-np-" + StringUtils.leftPad(String.valueOf(id), 4, "0") + ".pdf", false);
	}

	@RequestMapping(value = "/orders/{id}/report/post-russia-postpay", method = RequestMethod.GET)
	@ResponseBody
	public void getReportPostRussiaPostpay(@PathVariable("id") int id, HttpServletResponse response) throws JRException, IOException {
		createPdfOrderReport(id, 1, response, "/resources/jasperreports/post-postpay", "v3",
				"post-russia-postpay-" + StringUtils.leftPad(String.valueOf(id), 4, "0") + ".pdf", false);
		
	}
	
	@RequestMapping(value = "/orders/{id}/report/garant-ticket-all", method = RequestMethod.GET)
	@ResponseBody
	public void getReportGarantTicketAll(@PathVariable("id") int id, HttpServletResponse response) throws JRException, IOException {
		createPdfOrderReport(id, 1, response, "/resources/jasperreports/garant-ticket-all-master", "v1",
				"garant-ticket-all-" + StringUtils.leftPad(String.valueOf(id), 4, "0") + ".pdf", false);		
	}
	
	@RequestMapping(value = "/orders/{id}/report/garant-ticket-sititek", method = RequestMethod.GET)
	@ResponseBody
	public void getReportGarantTicketSititek(@PathVariable("id") int id, HttpServletResponse response) throws JRException, IOException {
		createPdfOrderReport(id, 1, response, "/resources/jasperreports/garant-ticket-sititek-master", "v1",
				"garant-ticket-sititek-" + StringUtils.leftPad(String.valueOf(id), 4, "0") + ".pdf", false);		
	}
	
	@RequestMapping(value = "/orders/{id}/report/kkm", method = RequestMethod.GET)
	@ResponseBody
	public void getReportKKM(@PathVariable("id") int id, HttpServletResponse response) throws JRException, IOException {
		createPdfOrderReport(id, 1, response, "/resources/jasperreports/kkm-master", "v1",
				"kkm-" + StringUtils.leftPad(String.valueOf(id), 4, "0") + ".pdf", false);		
	}
	
	@RequestMapping(value = "/orders/{id}/report/pko", method = RequestMethod.GET)
	@ResponseBody
	public void getReportPKO(@PathVariable("id") int id, HttpServletResponse response) throws JRException, IOException {
		createPdfOrderReport(id, 1, response, "/resources/jasperreports/bill-pko-master", "v1",
				"pko-" + StringUtils.leftPad(String.valueOf(id), 4, "0") + ".pdf", false);		
	}
	
	@RequestMapping(value = "/orders/{id}/report/return-form", method = RequestMethod.GET)
	@ResponseBody
	public void getReportReturnForm(@PathVariable("id") int id, HttpServletResponse response) throws JRException, IOException {
		createPdfOrderReport(id, 1, response, "/resources/jasperreports/return-form-master", "v1",
				"return-form-" + StringUtils.leftPad(String.valueOf(id), 4, "0") + ".pdf", false);		
	}
	
	// list page
	@RequestMapping(value = "/reports/product-sales", method = RequestMethod.GET)
	public String reportProductSales(Model model) {
		int userId = OrderListController.USER_ID;
		Date periodStart = wikiService.getConfig().getFormDateValueByKey(userId, "productSalesReportForm", "period.start", DateTimeUtils.sysDate());
		Date periodEnd = wikiService.getConfig().getFormDateValueByKey(userId, "productSalesReportForm", "period.end", DateTimeUtils.sysDate());
		BigDecimal advertBudget  = wikiService.getConfig().getFormBigDecimalValueByKey(userId, "productSalesReportForm", "advertBudget", BigDecimal.ZERO);
		FormProductSalesReport reportForm = new FormProductSalesReport(periodStart, periodEnd);
		reportForm.setAdvertBudget(advertBudget);
		model.addAttribute("reportForm", reportForm);
		return "reports/reportproductsalesform";
	}
	
	@RequestMapping(value = "/reports/product-sales/filter/exec", method = RequestMethod.POST)
	public void execReportProductSales(@ModelAttribute("reportForm") FormProductSalesReport reportForm,
			Model model, final RedirectAttributes redirectAttributes, HttpServletResponse response) throws JRException, IOException {
		int userId = OrderListController.USER_ID;
		wikiService.getConfig().saveFormDateValue(userId, "productSalesReportForm", "period.start", reportForm.getPeriodStart());
		wikiService.getConfig().saveFormDateValue(userId, "productSalesReportForm", "period.end", reportForm.getPeriodEnd());
		wikiService.getConfig().saveFormBigDecimalValue(userId, "productSalesReportForm", "advertBudget", reportForm.getAdvertBudget());
		
		Map<String, Object> parameters = new HashMap<>();
		parameters.putAll(wikiService.getConfigData());
		parameters.put("PERIOD_START", reportForm.getPeriod().getStart());
		parameters.put("PERIOD_END", reportForm.getPeriod().getEnd());
		parameters.put("ADVERT_BUDGET", reportForm.getAdvertBudget());
		parameters.put("REPORT_QUERY_NAME", "Данные за период");
												
		List<ProductSalesReportBean> reportBeans = reportManager.productSales(reportForm.getPeriod());
		createPdfReport(reportBeans, parameters, response, "/resources/jasperreports/report-product-sales-master-v3.jasper",
				"report-product-sales.pdf");
	}
	
	
	@RequestMapping(value = "/reports/product-sales-by-query-name", method = RequestMethod.GET)
	public String reportProductSalesByQueryName(Model model) {
			int userId = OrderListController.USER_ID;
			
			Date periodStart = wikiService.getConfig().getFormDateValueByKey(userId, "productSalesReportByQueryForm", "period.start", DateTimeUtils.sysDate());
			Date periodEnd = wikiService.getConfig().getFormDateValueByKey(userId, "productSalesReportByQueryForm", "period.end", DateTimeUtils.sysDate());
			
			int reportQueryNameId = wikiService.getConfig().getFormIntegerValueByKey(userId, "productSalesReportByQueryForm", "reportQueryName", ReportQueryNames.CDEK_POSTPAYMENT.getId());
			
			FormProductSalesReport reportForm = new FormProductSalesReport(periodStart, periodEnd);
			reportForm.setQueryName(ReportQueryNames.getValueById(reportQueryNameId));
			
			model.addAttribute("reportForm", reportForm);
			model.addAttribute("reportQueryNames", ReportQueryNames.values());
			populateDefaultModel(model);

			return "reports/reportproductsalesformbyqueryname";
	}
	
	@RequestMapping(value = "/reports/product-sales-by-query-name/filter/exec", method = RequestMethod.POST)
	public void execReportProductSalesByQueryName(@ModelAttribute("reportForm") FormProductSalesReport reportForm,
			Model model, final RedirectAttributes redirectAttributes, HttpServletResponse response) throws JRException, IOException {

		int userId = OrderListController.USER_ID;
		wikiService.getConfig().saveFormDateValue(userId, "productSalesReportByQueryForm", "period.start", reportForm.getPeriod().getStart());
		wikiService.getConfig().saveFormDateValue(userId, "productSalesReportByQueryForm", "period.end", reportForm.getPeriod().getEnd());	
		
		wikiService.getConfig().saveFormIntegerValue(userId, "productSalesReportByQueryForm", "reportQueryName", reportForm.getQueryName().getId());
		
		Map<String, Object> parameters = new HashMap<>();
		parameters.putAll(wikiService.getConfigData());
		parameters.put("PERIOD_START", reportForm.getPeriod().getStart());
		parameters.put("PERIOD_END", reportForm.getPeriod().getEnd());
		parameters.put("ADVERT_BUDGET", BigDecimal.ZERO);
		parameters.put("REPORT_QUERY_NAME", reportForm.getQueryName().getAnnotation());
												
		List<ProductSalesReportBean> reportBeans = reportManager.productSalesByQueryName(reportForm);
		createPdfReport(reportBeans, parameters, response, "/resources/jasperreports/report-product-sales-master-v4.jasper",
				"report-product-sales-by-query.pdf");
	}	
	
	// list page
	@RequestMapping(value = "/reports/product-sales-by-query", method = RequestMethod.GET)
	public String reportProductSalesByQuery(Model model) {
			ProductSalesReportConditions reportForm = wikiService.getConfig().loadProductSalesByQueryReportConditions(OrderListController.USER_ID);
			model.addAttribute("reportForm", reportForm);
			model.addAttribute("reportPeriodType", reportForm.getReportPeriodType());
			model.addAttribute("reportPeriodTypes", ReportPeriodTypes.getListOrderValues());
			Map<Integer, String> months = DateTimeUtils.getMonths();
			model.addAttribute("reportPeriodMonths", months);
			populateDefaultModel(model);
			return "reports/reportproductsalesformbyquery";
	}
	
	@RequestMapping(value = "/reports/product-sales-by-query/filter/exec", method = RequestMethod.POST)
	public void execReportProductSalesByQuery(@ModelAttribute("reportForm") FormProductSalesReport reportForm,
			Model model, final RedirectAttributes redirectAttributes, HttpServletResponse response) throws JRException, IOException {
		wikiService.getConfig().saveProductSalesByQueryReportConditions(OrderListController.USER_ID, reportForm);
		Map<String, Object> parameters = new HashMap<>(wikiService.getConfigData());
		parameters.put("PERIOD_START", reportForm.getPeriod().getStart());
		parameters.put("PERIOD_END", reportForm.getPeriod().getEnd());
		parameters.put("ADVERT_BUDGET", BigDecimal.ZERO);
		parameters.put("REPORT_QUERY_NAME", "Данные по запросу");
										
		List<ProductSalesReportBean> reportBeans = reportManager.productSalesByQuery(reportForm);
		createPdfReport(reportBeans, parameters, response, "/resources/jasperreports/report-product-sales-master-v4.jasper",
				"report-product-sales-by-query.pdf");
	}
	
	@RequestMapping(value = "/reports/sales-funnel", method = RequestMethod.GET)
	public String reportSalesFunnel(Model model) {

		int userId = 1;
		Date periodStart = wikiService.getConfig().getFormDateValueByKey(userId, "salesFunnelReportForm", "period.start", DateTimeUtils.sysDate());
		Date periodEnd = wikiService.getConfig().getFormDateValueByKey(userId, "salesFunnelReportForm", "period.end", DateTimeUtils.sysDate());
		
		model.addAttribute("reportPeriodTypes", ReportPeriodTypes.getReportValues());				
		model.addAttribute("reportPeriodMonths", DateTimeUtils.getMonths());
		model.addAttribute("reportPeriodQuarters", DateTimeUtils.getQuarters());
		model.addAttribute("reportPeriodHalfYears", DateTimeUtils.getHalfYears());		
		
		FormSalesFunnelReport reportForm = new FormSalesFunnelReport(periodStart, periodEnd);
				
		reportForm.setReportPeriodType(ReportPeriodTypes.getValueById(wikiService.getConfig().getFormIntegerValueByKey(userId, "salesFunnelReportForm", "reportPeriodType", 
				ReportPeriodTypes.ANY_MONTH.getId())));
		reportForm.setReportPeriodMonth(wikiService.getConfig().getFormIntegerValueByKey(userId, "salesFunnelReportForm", "reportPeriodMonth", 
				DateTimeUtils.monthOfDate(DateTimeUtils.sysDate())));
		reportForm.setReportPeriodQuarter(wikiService.getConfig().getFormIntegerValueByKey(userId, "salesFunnelReportForm", "reportPeriodQuarter", 
				DateTimeUtils.quarterOfDate(DateTimeUtils.sysDate())));
		reportForm.setReportPeriodHalfYear(wikiService.getConfig().getFormIntegerValueByKey(userId, "salesFunnelReportForm", "reportPeriodHalfYear", 
				DateTimeUtils.halfYearOfDate(DateTimeUtils.sysDate())));
		reportForm.setReportPeriodYear(wikiService.getConfig().getFormIntegerValueByKey(userId, "salesFunnelReportForm", "reportPeriodYear", 
				DateTimeUtils.yearOfDate(DateTimeUtils.sysDate())));
		model.addAttribute("reportForm", reportForm);			

		return "reports/reportsalesfunnelform";
	}
	
	@RequestMapping(value = "/reports/sales-funnel/filter/exec", method = RequestMethod.POST)
	public void execReportSalesFunnel(@ModelAttribute("reportForm") FormSalesFunnelReport reportForm,
			Model model, final RedirectAttributes redirectAttributes, HttpServletResponse response) throws JRException, IOException {
		int userId = 1;
		wikiService.getConfig().saveFormIntegerValue(userId, "salesFunnelReportForm", "reportPeriodType", 
				reportForm.getReportPeriodType().getId());
		wikiService.getConfig().saveFormIntegerValue(userId, "salesFunnelReportForm", "reportPeriodMonth", 
				reportForm.getReportPeriodMonth());
		wikiService.getConfig().saveFormIntegerValue(userId, "salesFunnelReportForm", "reportPeriodQuarter", 
				reportForm.getReportPeriodQuarter());
		wikiService.getConfig().saveFormIntegerValue(userId, "salesFunnelReportForm", "reportPeriodHalfYear", 
				reportForm.getReportPeriodHalfYear());
		wikiService.getConfig().saveFormIntegerValue(userId, "salesFunnelReportForm", "reportPeriodYear", 
				reportForm.getReportPeriodYear());
		wikiService.getConfig().saveFormDateValue(userId, "salesFunnelReportForm", "period.start", 
				reportForm.getPeriodStart());
		wikiService.getConfig().saveFormDateValue(userId, "salesFunnelReportForm", "period.end", 
				reportForm.getPeriodEnd());

		Map<String, Object> parameters = new HashMap<>(wikiService.getConfigData());
		parameters.put("PERIOD_START", reportForm.getPeriod().getStart());
		parameters.put("PERIOD_END", reportForm.getPeriod().getEnd());		
		parameters.put("PERIOD_TYPE", reportForm.getReportPeriodType().getId());
		parameters.put("PERIOD_MONTH", reportForm.getReportPeriodMonth());
		parameters.put("PERIOD_QUARTER", reportForm.getReportPeriodQuarter());
		parameters.put("PERIOD_HALF_YEAR", reportForm.getReportPeriodQuarter());
		parameters.put("PERIOD_YEAR", reportForm.getReportPeriodYear());

		List<SalesFunnelReportBean> reportBeans = reportManager.salesFunnel(reportForm.getPeriod());
		createPdfReport(reportBeans, parameters, response, "/resources/jasperreports/report-sales-funnel-v5.jasper",
				"report-sales-funnel.pdf");				
	}
	
	private void createPdfReport(Collection<?> beanCollection, Map<String, Object> parameters, HttpServletResponse response, String templateName, String fileName)
			throws JRException, IOException {
		
		InputStream jasperMasterStream = servletContext.getResourceAsStream(templateName); 		

		JasperReport jasperReport = (JasperReport) JRLoader.loadObject(jasperMasterStream);
		JRBeanCollectionDataSource beanColDataSource = new JRBeanCollectionDataSource(beanCollection);
		JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, beanColDataSource);

		response.setContentType("application/x-pdf");
		response.setHeader("Content-disposition", "inline; filename=" + fileName);

		final OutputStream outStream = response.getOutputStream();
		JasperExportManager.exportReportToPdfStream(jasperPrint, outStream);
	}

	private void createPdfOrderReport(int orderId, int count, HttpServletResponse response, String inputTemplateName,
									  String inputTemplateSuffix, String fileName, boolean isPreffix) throws JRException, IOException {

		Order one = orderService.getOrderDao().findById(orderId);
		List<Order> orders = Collections.singletonList(one);
		
		String templateName;
		if (isPreffix) {			
			templateName = inputTemplateName + "-" + one.getStore().getPreffix() + "-" + inputTemplateSuffix + ".jasper";			
		} else {			
			templateName = inputTemplateName + "-" + inputTemplateSuffix + ".jasper";	
		}
		Map<String, Object> parameters = new HashMap<>(wikiService.getConfigData());
		InputStream jasperMasterStream = servletContext.getResourceAsStream(templateName);
		JasperReport jasperReport = (JasperReport) JRLoader.loadObject(jasperMasterStream);
		JRBeanCollectionDataSource beanColDataSource = new JRBeanCollectionDataSource(orders);

		JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, beanColDataSource);
		response.setContentType("application/x-pdf");
		response.setHeader("Content-disposition", "inline; filename=" + fileName);

		final OutputStream outStream = response.getOutputStream();
		JasperExportManager.exportReportToPdfStream(jasperPrint, outStream);
	}
	
	@Override
	protected void populateDefaultModel(Model model) {
		super.populateDefaultModel(model);
			
	}
}
