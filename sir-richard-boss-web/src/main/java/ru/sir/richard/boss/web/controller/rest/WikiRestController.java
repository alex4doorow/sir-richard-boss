package ru.sir.richard.boss.web.controller.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import ru.sir.richard.boss.model.data.SupplierStockProduct;
import ru.sir.richard.boss.model.paging.Page;
import ru.sir.richard.boss.model.paging.PagingRequest;
import ru.sir.richard.boss.web.service.WikiRestService;

@RestController
//@RequestMapping("wiki-rest")
public class WikiRestController {
	
	private final Logger logger = LoggerFactory.getLogger(WikiRestController.class);
	
	@Autowired
	protected WikiRestService wikiRestService;
	
	//@PostMapping("/stock-products/suppliers/list")
	@RequestMapping(value = "/wiki-rest/stock-products/suppliers/list", method = RequestMethod.POST)
    public Page<SupplierStockProduct> stockProducts(@RequestBody PagingRequest pagingRequest) {
		
		logger.info("listByPeriod: {}", pagingRequest);
		
		return wikiRestService.getSupplierData(pagingRequest);
    	
		/*
    	Date initDate = DateTimeUtils.defaultFormatStringToDate("01.07.2022");
    	Pair<Date> period = new Pair<Date>(DateTimeUtils.firstDayOfMonth(initDate), DateTimeUtils.lastDayOfMonth(initDate)); 
    	return orderService.getOrdersByPeriod(pagingRequest, period);
    	*/
    }
	



}
