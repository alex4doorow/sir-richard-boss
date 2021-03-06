package ru.sir.richard.boss.web.controller;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import net.sf.jasperreports.engine.JRException;
import ru.sir.richard.boss.crm.DeliveryService;
import ru.sir.richard.boss.model.data.Order;
import ru.sir.richard.boss.model.types.CrmTypes;
import ru.sir.richard.boss.model.utils.DateTimeUtils;
import ru.sir.richard.boss.web.service.OrderService;
import ru.sir.richard.boss.web.service.WikiService;

@Controller
public class ExportController {
	
	private final Logger logger = LoggerFactory.getLogger(ExportController.class);
	
	@Autowired
	private OrderService orderService;
	
	@Autowired
	protected WikiService wikiService;
	
	@Autowired
	protected DeliveryService deliveryService;
	
	@RequestMapping(value = "/orders/{id}/export/excel-cdek", method = RequestMethod.GET)
	@ResponseBody
	public void exportExcelCdek(@PathVariable("id") int orderId, HttpServletResponse response) throws JRException, IOException {
		
		logger.debug("exportExcelCdek():{}", "start");

		response.setContentType("application/vnd.ms-excel");
		response.setHeader("Content-Disposition", "attachment; filename=cdek-out.xls");

		final OutputStream outStream = response.getOutputStream();
		
		deliveryService.exportParcelOrdersToExcel(orderId, outStream, DateTimeUtils.sysDate(), CrmTypes.CDEK);
		
		/*
		orderService.getCrmManager().setExecutorDate(DateTimeUtils.sysDate());
		orderService.getCrmManager().exportCdekExcel(orderId, outStream);
		*/
		logger.debug("exportExcelCdek():{}", "finish");
	}	
	
	@RequestMapping(value = "/orders/{id}/export/api-cdek", method = RequestMethod.GET)
	public String exportApiCdek(@PathVariable("id") int orderId, Model model) throws JRException, IOException {
		
		logger.debug("exportApiCdek(): {}", "start");
		
		if (orderId == 0) {
			logger.debug("exportApiCdek(): {},{}", "finish", "empty");
			return "redirect:/orders";
		}
		
		Order order = orderService.getOrderDao().findById(orderId);		
		String trackCode = deliveryService.addCdekParcelOrder(order);
		
		logger.debug("exportApiCdek():{},{}", "finish", trackCode);
		return "redirect:/orders";
	}
	
	@RequestMapping(value = "/orders/{id}/export/api-ozon-rocket", method = RequestMethod.GET)
	public String exportApiOzonRocket(@PathVariable("id") int orderId, Model model) throws JRException, IOException {
		
		logger.debug("exportApiOzonRocket(): {}", "start");
		
		if (orderId == 0) {
			logger.debug("exportApiCdek():{},{}", "finish", "empty");
			return "redirect:/orders";
		}
		
		Order order = orderService.getOrderDao().findById(orderId);		
		String trackCode = deliveryService.addOzonRocketParcelOrder(order);
		
		logger.debug("exportApiOzonRocket(): {}, {}", "finish", trackCode);
		return "redirect:/orders";
	}
	
}
