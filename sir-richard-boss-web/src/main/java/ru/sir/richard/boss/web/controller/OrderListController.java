package ru.sir.richard.boss.web.controller;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import ru.sir.richard.boss.crm.DeliveryService;
import ru.sir.richard.boss.model.data.Order;
import ru.sir.richard.boss.model.data.conditions.CustomerConditions;
import ru.sir.richard.boss.model.data.conditions.OrderConditions;
import ru.sir.richard.boss.model.types.OrderAmountTypes;
import ru.sir.richard.boss.model.types.OrderStatuses;
import ru.sir.richard.boss.model.types.ReportPeriodTypes;
import ru.sir.richard.boss.model.utils.DateTimeUtils;
import ru.sir.richard.boss.model.utils.NumberUtils;
import ru.sir.richard.boss.model.utils.Pair;
import ru.sir.richard.boss.model.utils.TextUtils;
import ru.sir.richard.boss.web.service.OrderService;
import ru.sir.richard.boss.web.validator.OrderConditionsFormValidator;

@Controller
public class OrderListController extends AnyController {

	private final Logger logger = LoggerFactory.getLogger(OrderListController.class);

	public final static int USER_ID = 1;

	@Autowired
	OrderConditionsFormValidator orderConditionsFormValidator;

	@Autowired
	private OrderService orderService;

	@Autowired
	private DeliveryService deliveryService;

	@InitBinder
	protected void initBinder(WebDataBinder binder, HttpServletRequest httpServletRequest) {

		binder.registerCustomEditor(Date.class,
				new CustomDateEditor(new SimpleDateFormat(DateTimeUtils.DATE_FORMAT_dd_MM_yyyy), true));

		binder.setValidator(orderConditionsFormValidator);
	}

	@RequestMapping(value = "/orders/import-crm", method = RequestMethod.GET)
	public String importCrm(Model model) {

		logger.debug("importCrm(): start");
		orderService.getCrmManager().setExecutorDate(DateTimeUtils.sysDate());
		orderService.getCrmManager().importRun();
		logger.debug("importCrm(): end");
		return "redirect:/orders";
	}

	// list page
	@RequestMapping(value = "/orders", method = RequestMethod.GET)
	public String showAllOrders(Model model) {

		logger.debug("showAllOrders()");
		OrderConditions orderConditions = wikiService.getConfig().loadOrderConditions(OrderListController.USER_ID);

		List<Order> orders = orderService.getOrderDao().listOrdersByConditions(orderConditions);
		Map<OrderAmountTypes, BigDecimal> totalAmounts = orderService.getOrderDao().calcTotalOrdersAmountsByConditions(orders,
				orderConditions.getPeriod());

		populateDefaultModel(model);
		model.addAttribute("orders", orders);
		model.addAttribute("totalAmounts", totalAmounts);
		model.addAttribute("reportPeriodType", ReportPeriodTypes.CURRENT_MONTH);
		model.addAttribute("listType", "orders");

		return "orders/list";
	}
	
	@RequestMapping(value = "/orders/trouble-load", method = RequestMethod.GET)
	public String showTroubleOrders(Model model) {

		logger.debug("showTroubleOrders()");

		List<Order> orders = orderService.getOrderDao().listTroubleOrders();
		model.addAttribute("orders", orders);
		model.addAttribute("reportPeriodType", ReportPeriodTypes.CURRENT_MONTH);
		model.addAttribute("listType", "trouble-load");

		return "orders/list";
	}

	@RequestMapping(value = "/orders/conditions/period/{period}", method = RequestMethod.GET)
	public String showOrdersByPeriod(@PathVariable("period") String period, Model model) {

		logger.debug("showOrdersByPeriod()");
		ReportPeriodTypes reportPeriodType = ReportPeriodTypes.getValueByCode(period);
		OrderConditions orderConditions = new OrderConditions(reportPeriodType);

		List<Order> orders = orderService.getOrderDao().listOrdersByConditions(orderConditions);
		Map<OrderAmountTypes, BigDecimal> totalAmounts = orderService.getOrderDao().calcTotalOrdersAmountsByConditions(orders,
				orderConditions.getPeriod());

		model.addAttribute("orders", orders);
		model.addAttribute("totalAmounts", totalAmounts);
		model.addAttribute("reportPeriodType", reportPeriodType);
		model.addAttribute("listType", "orders");
		return "orders/list";
	}

	@RequestMapping(value = "/orders/conditions/filter", method = RequestMethod.GET)
	public String showOrdersByConditions(Model model) {

		logger.debug("showOrdersByConditions()");

		OrderConditions orderConditionsForm = wikiService.getConfig().loadOrderConditions(OrderListController.USER_ID);

		model.addAttribute("orderConditionsForm", orderConditionsForm);
		model.addAttribute("reportPeriodType", ReportPeriodTypes.CURRENT_MONTH);
		model.addAttribute("reportPeriodTypes", ReportPeriodTypes.getListOrderValues());
		Map<Integer, String> months = DateTimeUtils.getMonths();
		model.addAttribute("reportPeriodMonths", months);

		populateDefaultModel(model);
		return "orders/orderconditionsform";
	}

	@RequestMapping(value = "/orders/conditions/filter/exec", method = RequestMethod.POST)
	public String execOrdersByConditions(@ModelAttribute("orderConditionsForm") OrderConditions orderConditionsForm,
			Model model, final RedirectAttributes redirectAttributes) {

		logger.debug("execOrdersByConditions():{}", orderConditionsForm);

		wikiService.getConfig().saveOrderConditions(OrderListController.USER_ID, orderConditionsForm);

		List<Order> orders = orderService.getOrderDao().listOrdersByConditions(orderConditionsForm);
		Map<OrderAmountTypes, BigDecimal> totalAmounts = orderService.getOrderDao().calcTotalOrdersAmountsByConditions(orders,
				orderConditionsForm.getPeriod());

		model.addAttribute("orders", orders);
		model.addAttribute("totalAmounts", totalAmounts);
		model.addAttribute("reportPeriodType", ReportPeriodTypes.CURRENT_MONTH);
		model.addAttribute("listType", "orders");
		return "orders/list";
	}

	@RequestMapping(value = "/orders/show/by-conditions/{dirtyConditions}", method = RequestMethod.GET)
	public String orderByOrderConditions(@PathVariable("dirtyConditions") String dirtyConditions, Model model, final RedirectAttributes redirectAttributes) {

		if (StringUtils.isEmpty(dirtyConditions)) {
			redirectAttributes.addFlashAttribute("css", "danger");
			redirectAttributes.addFlashAttribute("msg", "Запись не найдена!");
			return "redirect:/orders";
		}
		// 1) ищем по номеру заказа
		int orderSubNo = 0;
		int orderYear = DateTimeUtils.dateToShortYear(DateTimeUtils.sysDate());		
		logger.debug("orderByOrderConditions():{},{},{}", dirtyConditions, orderSubNo, orderYear);
		
		int id = 0;
		try {
			 id = orderService.getOrderDao().findIdByNo(Integer.valueOf(dirtyConditions.trim()), orderSubNo, orderYear);
	         
	    } catch (NumberFormatException nfe) {
	    	 id = 0;
	          
	    }
		OrderConditions orderConditions;
		CustomerConditions customerConditions;
		
		if (id <= 0) {
			// 2) ищем по трэккоду
			final String trackCode = dirtyConditions.trim();
			orderConditions = new OrderConditions();
			orderConditions.setPeriodExist(false);
			customerConditions = new CustomerConditions();
			orderConditions.setCustomerConditions(customerConditions);
			orderConditions.setTrackCode(trackCode);			
			List<Order> orders = orderService.getOrderDao().listOrdersByConditions(orderConditions);
			if (orders.size() > 0) {
				id = orders.get(0).getId();
			}			
		}
		
		if (id <= 0) {
			// 3) ищем по телефону физика
			final String dirtyPhoneNumber = dirtyConditions.trim();			
			final String phoneNumber = TextUtils.formatPhoneNumber(dirtyPhoneNumber);
			
			orderConditions = new OrderConditions();
			orderConditions.setPeriodExist(false);
			customerConditions = new CustomerConditions();
			customerConditions.setPersonPhoneNumber(phoneNumber);
			orderConditions.setCustomerConditions(customerConditions);
			List<Order> orders = orderService.getOrderDao().listOrdersByConditions(orderConditions);
			if (orders.size() > 0) {
				id = orders.get(0).getId();
			}			
		}
		
		if (id <= 0 && TextUtils.textIsEmail(dirtyConditions)) {
			// 4) ищем по email физика
			final String email = dirtyConditions.trim();
			
			orderConditions = new OrderConditions();
			orderConditions.setPeriodExist(false);
			customerConditions = new CustomerConditions();
			customerConditions.setPersonEmail(email);
			orderConditions.setCustomerConditions(customerConditions);
			List<Order> orders = orderService.getOrderDao().listOrdersByConditions(orderConditions);
			if (orders.size() > 0) {
				id = orders.get(0).getId();
			}
		}
		
		if (id <= 0 && TextUtils.textIsEmail(dirtyConditions)) {
			// 5) ищем по email юрика
			final String email = dirtyConditions.trim();				
			orderConditions = new OrderConditions();
			orderConditions.setPeriodExist(false);
			customerConditions = new CustomerConditions();
			customerConditions.setCompanyMainContactEmail(email);
			orderConditions.setCustomerConditions(customerConditions);
			List<Order> orders = orderService.getOrderDao().listOrdersByConditions(orderConditions);
			if (orders.size() > 0) {
				id = orders.get(0).getId();
			}			

		}
		if (id <= 0) {
			// 6) ищем по телефону юрика
			final String dirtyPhoneNumber = dirtyConditions.trim();			
			final String phoneNumber = TextUtils.formatPhoneNumber(dirtyPhoneNumber);			
			orderConditions = new OrderConditions();
			orderConditions.setPeriodExist(false);
			customerConditions = new CustomerConditions();
			customerConditions.setCompanyMainContactPhoneNumber(phoneNumber);
			orderConditions.setCustomerConditions(customerConditions);
			List<Order> orders = orderService.getOrderDao().listOrdersByConditions(orderConditions);
			if (orders.size() > 0) {
				id = orders.get(0).getId();
			}
		}
		
		if (id <= 0) {
			// 6) ищем по opencart no
			final String crmNo = dirtyConditions.trim();
			
			orderConditions = new OrderConditions();
			orderConditions.setPeriodExist(false);
			customerConditions = new CustomerConditions();
			orderConditions.setCustomerConditions(customerConditions);
			orderConditions.setCrmNo(crmNo);			
			List<Order> orders = orderService.getOrderDao().listOrdersByConditions(orderConditions);
			if (orders.size() > 0) {
				id = orders.get(0).getId();
			}
		}
		
		if (id <= 0) {			
			redirectAttributes.addFlashAttribute("css", "danger");
			redirectAttributes.addFlashAttribute("msg", "Запись не найдена!");
			return "redirect:/orders";
		}	
		return "redirect:/orders/" + id + "/orders";
	}

		
	@RequestMapping(value = "/orders/statuses/reload", method = RequestMethod.GET)
	public String ordersStatusesReload(Model model, final RedirectAttributes redirectAttributes) {
		logger.debug("ordersStatusesReload(): start");
		// --> orderListController
		
		String resultDelivery = deliveryService.ordersStatusesReload();
		String resultEmail = orderService.ordersSendFeedback(DateTimeUtils.sysDate());
		resultDelivery += "<br/>" + resultEmail;

		String resultExpired = orderService.bidExpiredSendMessages();
		resultDelivery += "<br/>" + resultExpired;

		redirectAttributes.addFlashAttribute("css", "success");
		redirectAttributes.addFlashAttribute("msg", resultDelivery);
		populateDefaultModel(model);
		logger.debug("ordersStatusesReload(): end");
				
		return "redirect:/orders";
	}
	
	@RequestMapping(value = "/orders/statuses/today", method = RequestMethod.GET)
	public String ordersStatusesToday(Model model, final RedirectAttributes redirectAttributes) {
		
		logger.debug("ordersStatusesToday(): start");
		deliveryService.ordersStatusesReload();
		
		//Date today = DateTimeUtils.beforeAnyDate(DateTimeUtils.sysDate(), 1);
		Date today = DateTimeUtils.sysDate();
		
		// посчитаем прибыль за сегодня		
		Pair<Date> dates = new Pair<Date>(today, today);
		List<Order> orders = orderService.getOrderDao().listYeildOrders(dates);
		Map<OrderAmountTypes, BigDecimal> totalAmounts = orderService.getOrderDao().calcTotalOrdersAmountsByConditions(orders,
				dates);

		// считаем все ордера, что были сегодня: отправлены, прибыли, получены
		Map<OrderStatuses, List<Order>> deliveryOrders = orderService.getOrderDao().getDeliveryOrders(today);
		String resultToday = String.format("За сегодня: (отправлено: %d, прибыло на пункты выдачи: %d, доставлено: %d, заказов: %d, прибыль: %s)", 
				deliveryOrders.get(OrderStatuses.DELIVERING).size(), 
				deliveryOrders.get(OrderStatuses.READY_GIVE_AWAY).size(), 
				deliveryOrders.get(OrderStatuses.DELIVERED).size(),
				orders.size(),
				NumberUtils.formatRoubles(totalAmounts.get(OrderAmountTypes.MARGIN)));
		
		String resultDelivering = ordersStatusesTodayByOrderStatus(OrderStatuses.DELIVERING, deliveryOrders);
		String resultReadyGiveAway = ordersStatusesTodayByOrderStatus(OrderStatuses.READY_GIVE_AWAY, deliveryOrders);
		String resultDelivered = ordersStatusesTodayByOrderStatus(OrderStatuses.DELIVERED, deliveryOrders);
						
		String result = "<strong>" + resultToday + "</strong><br>";
				
		result += resultDelivering;
		result += resultReadyGiveAway;
		result += resultDelivered;
		
		redirectAttributes.addFlashAttribute("css", "success");
		redirectAttributes.addFlashAttribute("msg", result);
		logger.debug("ordersStatusesToday(): end");
		return "redirect:/orders";		
	}
	
	private String ordersStatusesTodayByOrderStatus(OrderStatuses orderStatus, Map<OrderStatuses, List<Order>> deliveryOrders) {
		
		if (deliveryOrders.get(orderStatus).size() == 0) {
			return "";
		}
		
		String resultDelivering = orderStatus.getAnnotation() + ":<br>";
		List<Order> deliveringOrders = deliveryOrders.get(orderStatus);		
		for (Order currentStatusOrder : deliveringOrders) {			
			String debugInfoItem = "- " + currentStatusOrder.getNo() + ", " + currentStatusOrder.getCustomer().getViewShortName() + ", " + currentStatusOrder.getDelivery().getAddress().getAddress();
			resultDelivering = resultDelivering + debugInfoItem + "<br>";
		}		
		return resultDelivering;		
	}

	@ExceptionHandler(EmptyResultDataAccessException.class)
	public ModelAndView handleEmptyData(HttpServletRequest req, Exception ex) {
		logger.debug("handleEmptyData()");
		logger.error("Request:{}, error ", req.getRequestURL(), ex);
		ModelAndView model = new ModelAndView();
		model.setViewName("order/list");
		model.addObject("msg", "order condition not found");
		model.addObject("exception", "ex");
		return model;
	}

	@Override
	protected void populateDefaultModel(Model model) {
		super.populateDefaultModel(model);

		
	}
}
