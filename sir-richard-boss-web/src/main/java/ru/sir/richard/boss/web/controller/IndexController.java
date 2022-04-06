package ru.sir.richard.boss.web.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import ru.sir.richard.boss.model.data.conditions.OrderConditions;

@Controller
public class IndexController extends AnyController {
	
	private final Logger logger = LoggerFactory.getLogger(IndexController.class);
	
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String index(Model model) {
		logger.debug("root()");
		return "redirect:/index";
	}	
		
	@RequestMapping(value = "/index", method = RequestMethod.GET)
	public String showIndexTest(Model model) {
		logger.debug("index()");
		model.addAttribute("orderConditionsByNoForm", new OrderConditions());
		return "index";
	}	
}
