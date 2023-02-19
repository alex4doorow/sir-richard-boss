package ru.sir.richard.boss.web.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;
import ru.sir.richard.boss.api.cdek.CdekApiService;
import ru.sir.richard.boss.crm.DeliveryService;
import ru.sir.richard.boss.model.data.Address;
import ru.sir.richard.boss.model.data.Order;
import ru.sir.richard.boss.model.data.OrderAmounts;
import ru.sir.richard.boss.model.data.OrderItem;
import ru.sir.richard.boss.model.data.Product;
import ru.sir.richard.boss.model.data.SupplierStockProduct;
import ru.sir.richard.boss.model.data.conditions.CustomerConditions;
import ru.sir.richard.boss.model.data.conditions.OrderConditions;
import ru.sir.richard.boss.model.data.conditions.ProductConditions;
import ru.sir.richard.boss.model.data.crm.DeliveryServiceResult;
import ru.sir.richard.boss.model.paging.Page;
import ru.sir.richard.boss.model.paging.PagingRequest;
import ru.sir.richard.boss.model.types.DeliveryPrices;
import ru.sir.richard.boss.model.types.DeliveryTypes;
import ru.sir.richard.boss.model.types.ReportPeriodTypes;
import ru.sir.richard.boss.utils.DateTimeUtils;
import ru.sir.richard.boss.utils.NumberUtils;
import ru.sir.richard.boss.utils.Pair;
import ru.sir.richard.boss.web.data.FormCustomer;
import ru.sir.richard.boss.web.response.AjaxDeliveryPrice;
import ru.sir.richard.boss.web.response.OrderConditionsAjaxResponseBody;
import ru.sir.richard.boss.web.response.OrderFormAjaxResponseBody;
import ru.sir.richard.boss.web.response.ProductFormAjaxResponceBody;
import ru.sir.richard.boss.web.service.OrderService;
import ru.sir.richard.boss.web.service.WikiRestService;
import ru.sir.richard.boss.web.service.WikiService;

@RestController
@Slf4j
public class AjaxController {

	@Autowired
	private WikiService wikiService;
	
	@Autowired
	protected WikiRestService wikiRestService;
	
	@Autowired
	private OrderService orderService;
	
	@Autowired
	private DeliveryService deliveryService;
	
	@Autowired
	private CdekApiService cdekApiService;

	@ResponseBody
	@RequestMapping(value = "/ajax/wiki/stock-products/post2/suppliers/list", produces = "application/json", method = RequestMethod.POST)
    public Page<SupplierStockProduct> stockProductsPost(@RequestBody PagingRequest pagingRequest) {
		
		log.info("stockProductsPost: {}", pagingRequest);
		
		return wikiRestService.getSupplierData(pagingRequest);
    }
		
	@ResponseBody
	@RequestMapping(value = "/ajax/wiki/search/find-products-by-context", produces = "application/json", method = RequestMethod.POST)
	public OrderFormAjaxResponseBody findProductsByContextAjax(@RequestBody String contextString) {
		log.debug("findProductsByContextAjax():{}", contextString);
		
		OrderFormAjaxResponseBody result = new OrderFormAjaxResponseBody();
		result.setMsg("result msg");
	
		if (isValidContextString(contextString)) {
			List<Product> products = null;
			if (StringUtils.contains(contextString, " : ")) {
				
				String[] contextStringValues = StringUtils.split(contextString, " : ");
				String contextSku = contextStringValues[0];
				
				Product singleProduct = wikiService.getWiki().findProductBySku(contextSku);				
				products = new ArrayList<Product>();
				products.add(singleProduct);		
				
			} else {
				products = wikiService.getWiki().findProductsByName(contextString);				
			}
			if (products != null && products.size() > 0) {
				result.setCode("200");
				result.setMsg("");
				result.getResult().setProducts(products);
			} else {
				result.setCode("204");
				result.setMsg("No any product!");
			}
		} else {
			result.setCode("400");
			result.setMsg("Search criteria is empty!");
		}
		return result;
	}
	
	@ResponseBody
	@RequestMapping(value = "/ajax/customer/search/next-phone-number", produces = "application/json", method = RequestMethod.POST)
	public OrderFormAjaxResponseBody nextPhoneNumber(@RequestBody String contextString) {
		OrderFormAjaxResponseBody result = new OrderFormAjaxResponseBody();
		String phoneNumber = orderService.getCustomerDao().nextEmptyPhoneNumber();
		log.debug("nextPhoneNumber():{}", phoneNumber);		
		result.setMsg(phoneNumber);		
		result.setCode("200");
		return result;
	}	
	
	@ResponseBody
	@RequestMapping(value = "/ajax/wiki/search/find-price-by-delivery-types", produces = "application/json", method = RequestMethod.POST)
	public OrderFormAjaxResponseBody findDeliveryPriceByDeliveryTypes(@RequestBody String contextString) {
		log.debug("findDeliveryPriceByDeliveryTypes():{}", contextString);
		
		OrderFormAjaxResponseBody result = new OrderFormAjaxResponseBody();
		result.setMsg("result msg");
	
		List<AjaxDeliveryPrice> resultPrices = new ArrayList<AjaxDeliveryPrice>();
		if (isValidContextString(contextString)) {
			DeliveryTypes deliveryType = DeliveryTypes.valueOf(StringUtils.replace(contextString.trim(),"\"", ""));
			List<DeliveryPrices> prices = DeliveryPrices.getValuesByDeliveryType(deliveryType);			
			
			for (DeliveryPrices price : prices) {
				resultPrices.add(new AjaxDeliveryPrice(price));				
			}
			result.getResult().setDeliveryPrices(resultPrices);
			result.setCode("200");
			result.setMsg("");

		} else {
			result.setCode("400");
			result.setMsg("Search criteria is empty!");
		}
		return result;
	}
	
	@ResponseBody
	@RequestMapping(value = "/ajax/cusomer/search/find-customer-by-conditions", produces = "application/json", method = RequestMethod.POST)
	public OrderFormAjaxResponseBody findCustomerByConditionsAjax(@RequestBody CustomerConditions customerConditions) {
		log.debug("findCustomerByConditionsAjax():{}", customerConditions);		
		OrderFormAjaxResponseBody result = new OrderFormAjaxResponseBody();
		result.setMsg("result msg");		
			FormCustomer formCustomer = orderService.customerFindByConditions(customerConditions);
			
			if (formCustomer != null) {
				result.setCode("200");
				result.setMsg("");
				result.getResult().setFormCustomer(formCustomer);
				// список заказов конкретного покупателя				
				OrderConditions orderConditions = new OrderConditions();
				orderConditions.setCustomerId(formCustomer.getId());
				List<Order> customerOrders = orderService.getOrderDao().listOrdersByConditions(orderConditions);
				
				for (Order customerOrder : customerOrders) {
					List<OrderItem> orderItems = orderService.getOrderDao().getItemsByOrder(customerOrder);
					customerOrder.setItems(orderItems);
				}
				result.createOrders(customerOrders);
			} else {
				result.setCode("204");
				result.setMsg("No any customer!");
			}		
		return result;
	}
	
	@ResponseBody
	@RequestMapping(value = "/ajax/orders/conditions/filter/get-periods-by-type", produces = "application/json", method = RequestMethod.POST)
	public OrderConditionsAjaxResponseBody findOrdersConditionPeriodByTypeAjax(@RequestBody String contextString) {
		
		OrderConditionsAjaxResponseBody result = new OrderConditionsAjaxResponseBody();		
		result.setMsg("result msg");
		if (isValidContextString(contextString)) {
			ReportPeriodTypes reportPeriodType = ReportPeriodTypes.valueOf(contextString);			
			OrderConditions orderConditions = new OrderConditions(reportPeriodType);
			result.getResult().setReportPeriodType(reportPeriodType);
			result.getResult().setReportPeriodMonth(orderConditions.getReportPeriodMonth());
			result.getResult().setReportPeriodQuarter(orderConditions.getReportPeriodQuarter());
			result.getResult().setReportPeriodHalfYear(orderConditions.getReportPeriodHalfYear());
			result.getResult().setReportPeriodYear(orderConditions.getReportPeriodYear());
			result.getResult().setPeriod(orderConditions.getPeriod());	
			result.setCode("200");
			result.setMsg("");
		} else {
			result.setCode("400");
			result.setMsg("Search criteria is empty!");
		}		
		return result;		
	}
		
	@ResponseBody
	@RequestMapping(value = "/ajax/orders/conditions/filter/get-periods-by-month-year", produces = "application/json", method = RequestMethod.POST)
	public OrderConditionsAjaxResponseBody findOrdersConditionPeriodByMonthYearAjax(@RequestBody OrderConditions reportPeriodContainer) {
		
		log.debug("findOrdersConditionPeriodByMonthYearAjax():{}", reportPeriodContainer);		
		OrderConditionsAjaxResponseBody result = new OrderConditionsAjaxResponseBody();		
		result.setMsg("result msg");
		
		//int periodType, int month, int quarter, int halfYear, int year
		Pair<Date> period = DateTimeUtils.getPeriodByMonthYear(reportPeriodContainer.getReportPeriodType().getId(),
				reportPeriodContainer.getReportPeriodMonth(), 
				reportPeriodContainer.getReportPeriodQuarter(),
				reportPeriodContainer.getReportPeriodHalfYear(),
				reportPeriodContainer.getReportPeriodYear());
				
		
		//OrderConditions orderConditions = new OrderConditions(reportPeriodContainer.getReportPeriodType());
		//orderConditions.setPeriod(period);
		
		result.getResult().setReportPeriodType(reportPeriodContainer.getReportPeriodType());
		result.getResult().setReportPeriodMonth(Integer.valueOf(reportPeriodContainer.getReportPeriodMonth()));
		result.getResult().setReportPeriodQuarter(Integer.valueOf(reportPeriodContainer.getReportPeriodQuarter()));
		result.getResult().setReportPeriodHalfYear(Integer.valueOf(reportPeriodContainer.getReportPeriodHalfYear()));
		result.getResult().setReportPeriodYear(Integer.valueOf(reportPeriodContainer.getReportPeriodYear()));
		result.getResult().setPeriod(period);				
				
		result.setCode("200");
		result.setMsg("");		
		return result;	
		
	}
	
	@ResponseBody
	@RequestMapping(value = "/ajax/orders/marketplace-info", produces = "application/json", method = RequestMethod.POST)
	public OrderFormAjaxResponseBody findOrderMarketplaceInfoAjax(@RequestBody Order orderContainer) {
		//logger.debug("findOrderMarketplaceInfoAjax(): {}", orderContainer.getId());
		
		Order order = orderService.getOrderDao().findById(orderContainer.getId());
		OrderFormAjaxResponseBody result = new OrderFormAjaxResponseBody();
		result.setCode("200");
		result.setMsg(order.getViewMarketNo());					
		return result;
	}

	@ResponseBody
	@RequestMapping(value = "/ajax/orders/calc/total-amounts", produces = "application/json", method = RequestMethod.POST)
	public OrderFormAjaxResponseBody calcOrderItemsTotalAmountsAjax(@RequestBody Order orderContainer) {
		log.debug("calcOrderItemsTotalAmountsAjax():{}, {}", orderContainer.getOrderType(), orderContainer.getDelivery().getDeliveryType());
		
		OrderAmounts orderAmounts = orderService.calcTotalAmounts(orderContainer);		
		OrderFormAjaxResponseBody result = new OrderFormAjaxResponseBody();
		result.getResult().setAmounts(orderAmounts);
		result.setCode("200");
		result.setMsg("");
		return result;
	}
	
	@ResponseBody
	@RequestMapping(value = "/ajax/orders/calc/parcel-delivery-cdek-cities", produces = "application/json", method = RequestMethod.POST)
	public OrderFormAjaxResponseBody calcOrderItemsParcelDeliveryCdekCities(@RequestBody String contextString) {
		log.debug("calcOrderItemsParcelDeliveryCdekCitiesAjax():{}, {}", contextString);

		if (StringUtils.isEmpty(contextString) || contextString.equals("\"\"")) {
			List<Address> cities = new ArrayList<Address>();
			
			OrderFormAjaxResponseBody result = new OrderFormAjaxResponseBody();
			result.getResult().getDeliveryServiceResult().setAddresses(cities);
			result.setCode("400");
			result.setMsg("Search criteria is empty!");
			return result;
		}
		String actualContextString = "";
		if (contextString.indexOf("\"") == 0 && contextString.lastIndexOf("\"") == contextString.length() - 1) {
			actualContextString = contextString.substring(1, contextString.length() - 1);
			actualContextString = actualContextString.substring(0, actualContextString.length());
		}					
		List<Address> cities = cdekApiService.getCities(actualContextString);		
		OrderFormAjaxResponseBody result = new OrderFormAjaxResponseBody();
		result.getResult().getDeliveryServiceResult().setAddresses(cities);
		result.setCode("200");
		result.setMsg("");
		return result;
	}
	
	@ResponseBody
	@RequestMapping(value = "/ajax/orders/calc/parcel-delivery-cdek-pvzs", produces = "application/json", method = RequestMethod.POST)
	public OrderFormAjaxResponseBody calcOrderItemsParcelDeliveryCdekPvzs(@RequestBody Integer cityId) {
		log.debug("calcOrderItemsParcelDeliveryCdekPvzsAjax(): {}", cityId);
		
		List<Address> pvzs = cdekApiService.getPvzs(cityId);
		OrderFormAjaxResponseBody result = new OrderFormAjaxResponseBody();
		result.getResult().getDeliveryServiceResult().setAddresses(pvzs);
		result.setCode("200");
		result.setMsg("");
		return result;
	}
		
	@ResponseBody
	@RequestMapping(value = "/ajax/orders/calc/parcel-delivery-amounts", produces = "application/json", method = RequestMethod.POST)
	public OrderFormAjaxResponseBody calcOrderItemsParcelDeliveryAmountsAjax(@RequestBody Order orderContainer) {
		log.debug("calcOrderItemsParcelDeliveryAmountsAjax():{}, {}", orderContainer.getOrderType(), orderContainer.getDelivery().getDeliveryType());
		
		OrderAmounts orderAmounts = orderService.calcTotalAmounts(orderContainer);				
					
		DeliveryServiceResult deliveryServiceResult = deliveryService.calc(orderContainer, 
				orderAmounts.getTotal(), 
				orderContainer.getDelivery().getDeliveryType(), 
				orderContainer.getDelivery().getAddress());
				
		OrderFormAjaxResponseBody result = new OrderFormAjaxResponseBody();
		result.getResult().setDeliveryServiceResult(deliveryServiceResult);
		result.setCode("200");
		result.setMsg("");
		return result;
	}
	
	@ResponseBody
	@RequestMapping(value = "/ajax/product/create-meta", produces = "application/json", method = RequestMethod.POST)
	public ProductFormAjaxResponceBody productCreateMeta(@RequestBody int productId) {

		log.debug("productCreateMeta(): {}", productId);		
		Product product = wikiService.getWiki().createProductDescriptionMeta(productId);
		
		ProductFormAjaxResponceBody result = new ProductFormAjaxResponceBody();
		result.getResult().setStore(product.getStore());
			
		return result;
	}
	
	@ResponseBody
	@RequestMapping(value = "/ajax/product/priceText", produces = "application/json", method = RequestMethod.POST)
	public ProductFormAjaxResponceBody productCreatePriceText(@RequestBody ProductConditions productConditions) {

		log.debug("productCreatePriceText(): {}, {}", productConditions.getPriceWithoutDiscountText(), productConditions.getPriceWithDiscountText());		
		
		String testOutput = "";
		BigDecimal priceWithoutDiscount = BigDecimal.ZERO;
		
		String sWithoutD = productConditions.getPriceWithoutDiscountText() == null ? "0" : productConditions.getPriceWithoutDiscountText().replace(",00", "").replace(" ", "").trim();
		String sWithD = productConditions.getPriceWithDiscountText() == null ? "0" : productConditions.getPriceWithDiscountText().replace(",00", "").replace(" ", "").trim();
		
		if (StringUtils.isNotEmpty(sWithoutD)) {
			priceWithoutDiscount = new BigDecimal(sWithoutD);	
		}
		BigDecimal priceWithDiscount = BigDecimal.ZERO;
		if (StringUtils.isNotEmpty(sWithD)) {
			priceWithDiscount = new BigDecimal(sWithD);	
		}
		
		if (priceWithDiscount.compareTo(BigDecimal.ZERO) == 0) {			
			String priceText = NumberUtils.formatRoubles(priceWithoutDiscount);			
			testOutput = "<b>&nbsp;<span>" + priceText + " </span></b>";			
		} else {
			String priceWithoutDiscountText2 = NumberUtils.formatRoubles(priceWithoutDiscount);
			String priceWithDiscountText2 = NumberUtils.formatRoubles(priceWithDiscount);
			testOutput = "<b>&nbsp;<span style=\"text-decoration: line-through\">" + priceWithoutDiscountText2 + "</span>&nbsp;<span>" + priceWithDiscountText2 + "</span></b>";
		}
		
		ProductFormAjaxResponceBody result = new ProductFormAjaxResponceBody();
		result.getResult().setPriceText(testOutput);
			
		return result;
	}	
		
	private boolean isValidContextString(String contextString) {
		if (contextString == null) {
			return false;
		}
		if ((StringUtils.isEmpty(contextString))) {			
			return false;
		}
		return true;
	}	
	
}
