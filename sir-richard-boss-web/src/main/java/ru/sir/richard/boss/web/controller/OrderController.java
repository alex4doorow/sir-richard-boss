package ru.sir.richard.boss.web.controller;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.core.env.Environment;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import ru.sir.richard.boss.model.data.AnyCustomer;
import ru.sir.richard.boss.model.data.Order;
import ru.sir.richard.boss.model.data.OrderAmounts;
import ru.sir.richard.boss.model.data.OrderItem;
import ru.sir.richard.boss.model.factories.CustomersFactory;
import ru.sir.richard.boss.model.types.CustomerTypes;
import ru.sir.richard.boss.model.types.OrderStatuses;
import ru.sir.richard.boss.model.types.OrderTypes;
import ru.sir.richard.boss.model.utils.DateTimeUtils;
import ru.sir.richard.boss.model.utils.NumberUtils;
import ru.sir.richard.boss.model.utils.sender.MessageManager;
import ru.sir.richard.boss.model.utils.sender.MessageSendingStatus;
import ru.sir.richard.boss.model.utils.sender.email.EmailSenderTextGenerator;
import ru.sir.richard.boss.web.data.FormOrder;
import ru.sir.richard.boss.web.service.OrderService;
import ru.sir.richard.boss.web.validator.OrderFormValidator;

@Controller
public class OrderController extends AnyController {

	private final Logger logger = LoggerFactory.getLogger(OrderController.class);
	
	@Autowired
	private Environment environment;

	@Autowired
	OrderFormValidator orderFormValidator;
		
	@Autowired
	private OrderService orderService;
	
	public OrderController() {
		super();	
	}
		
	@InitBinder
	protected void initBinder(WebDataBinder binder, HttpServletRequest httpServletRequest) {	
		
		binder.registerCustomEditor(Date.class, new CustomDateEditor(new SimpleDateFormat(DateTimeUtils.DATE_FORMAT_dd_MM_yyyy), true));
		/*
		binder.registerCustomEditor(BigDecimal.class,
				new CustomNumberEditor(BigDecimal.class, new DecimalFormat(NumberUtils.NUMBER_FORMAT_MONEY), true));
		*/
		binder.registerCustomEditor(BigDecimal.class, 
				new BigDecimalEditor(BigDecimal.class, new DecimalFormat(NumberUtils.NUMBER_FORMAT_MONEY), true));
		binder.setValidator(orderFormValidator);
		
		/*
		 * SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
		 * dateFormat.setLenient(false); webDataBinder.registerCustomEditor(Date.class,
		 * new CustomDateEditor(dateFormat, true));
		 */
		
		// Probably you only want to init this when form is submitted
	    if (!"POST".equalsIgnoreCase(httpServletRequest.getMethod())) {
	        return;
	    }

	    // Filter out all request when we have nothing to do
	    Object nonCastedTarget = binder.getTarget();
	    if (nonCastedTarget == null || !(nonCastedTarget instanceof FormOrder)) {
	        return;
	    }

	    FormOrder order = (FormOrder) nonCastedTarget;
	    //logger.debug("target:order", order);	    	    
	    
	    CustomerTypes customerType = order.getFormCustomer().getCustomerType();		
    	AnyCustomer customer = CustomersFactory.createCustomer(customerType);
	    order.setCustomer(customer);
	}	
	
	// show order
	@RequestMapping(value = "/orders/{id}/{listType}", method = RequestMethod.GET)
	public String showOrder(@PathVariable("id") int id, @PathVariable("listType") String listType, Model model) {
		logger.debug("showOrder():{}", id);
		Order order = orderService.getOrderDao().findById(id);		
		if (order == null) {
			model.addAttribute("css", "danger");
			model.addAttribute("msg", "Order not found");
		}
		FormOrder formOrder = FormOrder.createForm(order);
		model.addAttribute("order", formOrder);
		model.addAttribute("listType", listType);
		
		return "orders/show";
	}
	
	// show add order form
	@RequestMapping(value = "/orders/add/{listType}", method = RequestMethod.GET)
	public String showAddOrderForm(Model model, @PathVariable("listType") String listType) {

		logger.debug("showAddOrderForm()");
		FormOrder order = new FormOrder();
		order.setNo(orderService.getOrderDao().nextOrderNo());
		order.setOrderDate(DateTimeUtils.sysDate());
		order.setProductCategory(wikiService.getWiki().getCategoryById(0));		
		order.getItems().add(new OrderItem(order));	
				
		model.addAttribute("orderForm", order);
		model.addAttribute("listType", listType);
				
		populateDefaultModel(model);
		return "orders/orderform";
	}	

	// show update form
	@RequestMapping(value = "/orders/{id}/update/{listType}", method = RequestMethod.GET)
	public String showUpdateOrderForm(@PathVariable("id") int id, @PathVariable("listType") String listType, Model model) {

		logger.debug("showUpdateOrderForm():{},{}", id, listType);

		Order order = orderService.getOrderDao().findById(id);
		FormOrder formOrder = FormOrder.createForm(order);		
		if (formOrder.getItems().size() == 0) {
			order.getItems().add(new OrderItem(order));
		}

		model.addAttribute("orderForm", formOrder);
		model.addAttribute("order", formOrder);
		model.addAttribute("listType", listType);
				
		populateDefaultModel(model);
		model.addAttribute("cdekDefaultCity", order.getDelivery().getAddress().getCity());
		return "orders/orderform";
	}

	// save new or update order
	@RequestMapping(value = "/orders/{id}/save/{listType}", method = RequestMethod.POST)
	public String saveOrUpdateOrder(@PathVariable("id") int id, @PathVariable("listType") String listType, @ModelAttribute("orderForm") @Validated FormOrder formOrder, 
			BindingResult bindingResult, Model model, final RedirectAttributes redirectAttributes) {

		logger.debug("saveOrUpdateOrder():{},{},{},{},{}", formOrder.getNo(), DateTimeUtils.defaultFormatDate(formOrder.getOrderDate()), 
				formOrder.getOrderType(), formOrder.getSourceType(), formOrder.getProductCategory());
	
		if (bindingResult.hasErrors()) {
			logger.debug("bindingResult:{}", bindingResult.getAllErrors());
			populateDefaultModel(model);
			return "orders/orderform";
		} else {
			List<OrderItem> orderItems = removeTrashFromOrderItems(formOrder.getItems());
			formOrder.setItems(orderItems);
			
			OrderAmounts recalcOrderAmounts = orderService.calcTotalAmounts(formOrder);	
			formOrder.setAmounts(recalcOrderAmounts);
			
			formOrder.convertFormCustomer();
						
			orderService.saveOrUpdate(formOrder);
			redirectAttributes.addFlashAttribute("css", "success");
			if (formOrder.isNew()) {
				redirectAttributes.addFlashAttribute("msg", "Запись добавлена!");
			} else {
				String msg = String.format("Запись изменена: #%s от %s г, %s", 
						formOrder.getNo(), 
						DateTimeUtils.defaultFormatDate(formOrder.getOrderDate()),
						formOrder.getCustomer().getViewShortName());
				redirectAttributes.addFlashAttribute("msg", msg);
			}
			if (listType.equals("trouble-load")) {
				return "redirect:/orders/trouble-load";				
			}
			return "redirect:/orders";			
		}
	}	
	
	// show update form
	@RequestMapping(value = "/orders/{id}/clone", method = RequestMethod.GET)
	public String showCloneOrderForm(@PathVariable("id") int id, Model model) {

			logger.debug("showCloneOrderForm():{}", id);

			Order order = null;
			try {
				order = orderService.getOrderDao().findById(id).clone();				
			} catch (CloneNotSupportedException e) {
				e.printStackTrace();
			}
			order.setId(0);		
			FormOrder formOrder = FormOrder.createForm(order);
			formOrder.setNo(orderService.getOrderDao().nextOrderNo());
			formOrder.setOrderDate(DateTimeUtils.sysDate());
			formOrder.getDelivery().setTrackCode("");
			
			formOrder.getCustomer().setId(0);
			formOrder.getCustomer().getMainAddress().setId(0);
			formOrder.getFormCustomer().setId(0);
			formOrder.getFormCustomer().setPersonId(0);
			formOrder.getFormCustomer().getMainAddress().setId(0);
								
			model.addAttribute("orderForm", formOrder);
			model.addAttribute("order", formOrder);
			model.addAttribute("listType", "orders");
			populateDefaultModel(model);
			
			return "orders/orderform";	
	}

	@RequestMapping(value = "/orders/{id}/change-status/{listType}", method = RequestMethod.GET)
	public String changeStatusOrderForm(@PathVariable("id") int id, @PathVariable("listType") String listType, Model model) {

		logger.debug("changeStatusOrderForm():{}", id);

		Order order = orderService.getOrderDao().findById(id);
		FormOrder formOrder = FormOrder.createForm(order);		
		
		model.addAttribute("orderForm", formOrder);
		model.addAttribute("order", formOrder);
		model.addAttribute("listType", listType);
		
		populateDefaultModel(model);

		return "orders/orderstatusform";
	}	
	
	@RequestMapping(value = "/orders/{id}/change-status/save/{listType}", method = RequestMethod.POST)
	public String saveOrderChangeStatus(@PathVariable("id") int id, @PathVariable("listType") String listType, 
			@ModelAttribute("orderForm") @Validated FormOrder formOrder,
			BindingResult bindingResult, Model model, final RedirectAttributes redirectAttributes) {

		logger.debug("saveOrderChangeStatusOrder():{}", formOrder);		
		if (bindingResult.hasErrors()) {
			logger.debug("bindingResult:{}", bindingResult.getAllErrors());
			Order beforeOrder = orderService.getOrderDao().findById(id);
			FormOrder beforeFormOrder = FormOrder.createForm(beforeOrder);
			model.addAttribute("order", beforeFormOrder);
			populateDefaultModel(model);
			return "orders/orderstatusform";
		} else {	
			String msg;			
			Order oldOrder = orderService.getOrderDao().findById(id);			
			formOrder.setExternalCrms(oldOrder.getExternalCrms());
			formOrder.setCustomer(oldOrder.getCustomer());
			
			orderService.getOrderDao().changeFullStatusOrder(formOrder);
			
			Order newOrder = orderService.getOrderDao().findById(id);
			MessageManager messageManager = new MessageManager(environment);	
			MessageSendingStatus responceStatus = messageManager.sendOrderMessage(newOrder, formOrder.isSendMessage());
			redirectAttributes.addFlashAttribute("css", "success");			
			msg = String.format("Статус заказа изменен: #%s от %s г, %s. Было: \"%s\", стало: \"%s\". Сообщение %s", 
					formOrder.getNo(), 
					DateTimeUtils.defaultFormatDate(formOrder.getOrderDate()),
					formOrder.getCustomer().getViewShortName(),
					oldOrder.getStatus().getAnnotation(),
					formOrder.getStatus().getAnnotation(),
					responceStatus.getViewStatus());
			redirectAttributes.addFlashAttribute("msg", msg);
			if (listType.equals("trouble-load")) {
				return "redirect:/orders/trouble-load";				
			}
			return "redirect:/orders";
		}
	}
		
	@RequestMapping(value = "/orders/{id}/bill-expired-status/{listType}", method = RequestMethod.GET)
	public String changeBillExpiredStatusForm(@PathVariable("id") int id, @PathVariable("listType") String listType, Model model) {
		logger.debug("changeBillExpiredStatusForm():{}", id);
		Order order = orderService.getOrderDao().findById(id);
		if ((order.getOrderType() == OrderTypes.BILL || order.getOrderType() == OrderTypes.KP) && order.getStatus() == OrderStatuses.BID) {
			FormOrder formOrder = FormOrder.createForm(order);
			EmailSenderTextGenerator textGenerator = new EmailSenderTextGenerator();
			String textMessage = textGenerator.createBillExpiredStatusMessage(order);
			formOrder.setTextMessage(textMessage);
			model.addAttribute("orderForm", formOrder);
			model.addAttribute("order", formOrder);
			model.addAttribute("listType", listType);
			populateDefaultModel(model);
			return "orders/billexpiredstatusform";
		} else {
			return "redirect:/orders";			
		}
	}	
	
	@RequestMapping(value = "/orders/{id}/bill-expired-status/save/{listType}", method = RequestMethod.POST)
	public String saveBillExpiredStatus(@PathVariable("id") int id, @PathVariable("listType") String listType, 
			@ModelAttribute("orderForm") @Validated FormOrder formOrder,
			BindingResult bindingResult, Model model, final RedirectAttributes redirectAttributes) {

		logger.debug("saveBillExpiredStatus():{}", formOrder);		
		if (bindingResult.hasErrors()) {
			logger.debug("bindingResult:{}", bindingResult.getAllErrors());
			Order beforeOrder = orderService.getOrderDao().findById(id);
			FormOrder beforeFormOrder = FormOrder.createForm(beforeOrder);
			model.addAttribute("order", beforeFormOrder);
			populateDefaultModel(model);
			return "orders/billexpiredstatusform";
		} else {	
			String msg;			
			Order oldOrder = orderService.getOrderDao().findById(id);
			orderService.getOrderDao().changeBillExpiredStatusOrder(formOrder);

			MessageManager messageManager = new MessageManager(environment);	
			MessageSendingStatus responceStatus = messageManager.sendOrderManualMessage(oldOrder, formOrder.getTextMessage(), formOrder.isSendMessage());
			redirectAttributes.addFlashAttribute("css", "success");
			
			msg = String.format("Запрос на актуальность данных отправлен: #%s от %s г, %s. Сообщение %s", 
					formOrder.getNo(), 
					DateTimeUtils.defaultFormatDate(formOrder.getOrderDate()),
					formOrder.getCustomer().getViewShortName(),
					responceStatus.getViewStatus());
					
			redirectAttributes.addFlashAttribute("msg", msg);
			if (listType.equals("trouble-load")) {
				return "redirect:/orders/trouble-load";				
			}
			return "redirect:/orders";
		}
	}

	// delete order
	@RequestMapping(value = "/orders/{id}/delete", method = RequestMethod.GET)
	public String deleteOrder(@PathVariable("id") int id, Model model) {
		logger.debug("deleteOrder():{}", id);
		orderService.getOrderDao().deleteOrder(id);
		return "redirect:/orders";
	}
	
	@RequestMapping(value = "/orders/{id}/erase", method = RequestMethod.GET)
	public String eraseOrder(@PathVariable("id") int id, Model model) {		
		logger.debug("eraseOrder():{}", id);
		orderService.getOrderDao().eraseOrder(id, false);
		return "redirect:/orders";
	}


	@ExceptionHandler(EmptyResultDataAccessException.class)
	public ModelAndView handleEmptyData(HttpServletRequest req, Exception ex) {
		logger.debug("handleEmptyData()");
		logger.error("Request:{}, error:{}", req.getRequestURL(), ex);
		ModelAndView model = new ModelAndView();
		model.setViewName("order/show");
		model.addObject("msg", "order not found");
		model.addObject("exception", "ex");
		return model;
	}
	
	@Override
	protected void populateDefaultModel(Model model) {
		super.populateDefaultModel(model);

		model.addAttribute("cdekDefaultCountry", environment.getProperty("cdek.from.country"));
		model.addAttribute("cdekDefaultCity", environment.getProperty("cdek.from.city"));
		model.addAttribute("cdekCityFrom", environment.getProperty("cdek.from.city"));
	}
	
	private List<OrderItem> removeTrashFromOrderItems(List<OrderItem> orderItems) {
		List<OrderItem> result = new ArrayList<OrderItem>();		
		int no = 1;
		for (OrderItem orderItem : orderItems) {
			if (orderItem != null && orderItem.isActual()) {
				orderItem.setNo(no);
				result.add(orderItem);
				no++;
			}			
		}
		return result;
	}
}
