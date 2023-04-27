package ru.sir.richard.boss.web.controller;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.http.HttpServletResponse;

import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class ExportController {

	@Autowired
	private OrderService orderService;
	
	@Autowired
	protected WikiService wikiService;
	
	@Autowired
	protected DeliveryService deliveryService;
	
	@RequestMapping(value = "/orders/{id}/export/excel-cdek", method = RequestMethod.GET)
	@ResponseBody
	public void exportExcelCdek(@PathVariable("id") int orderId, HttpServletResponse response) throws IOException {
		response.setContentType("application/vnd.ms-excel");
		response.setHeader("Content-Disposition", "attachment; filename=cdek-out.xls");
		final OutputStream outStream = response.getOutputStream();
		deliveryService.exportParcelOrdersToExcel(orderId, outStream, DateTimeUtils.sysDate(), CrmTypes.CDEK);
	}	
	
	@RequestMapping(value = "/orders/{id}/export/api-cdek", method = RequestMethod.GET)
	public String exportApiCdek(@PathVariable("id") int orderId, Model model) throws JRException, IOException {
		
		log.debug("exportApiCdek(): {}", "start");
		
		if (orderId == 0) {
			log.debug("exportApiCdek(): {},{}", "finish", "empty");
			return "redirect:/orders";
		}
		
		Order order = orderService.getOrderDao().findById(orderId);		
		String trackCode = deliveryService.addCdekParcelOrder(order);
		
		log.debug("exportApiCdek():{},{}", "finish", trackCode);
		return "redirect:/orders";
	}
	
	@RequestMapping(value = "/orders/{id}/export/api-ozon-rocket", method = RequestMethod.GET)
	public String exportApiOzonRocket(@PathVariable("id") int orderId, Model model) throws JRException, IOException {

		return "redirect:/orders";
	}
	
}
