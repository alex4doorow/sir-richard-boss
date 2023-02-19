package ru.sir.richard.boss.web.controller;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import ru.sir.richard.boss.model.data.conditions.OrderConditions;

@Controller
@Slf4j
public class IndexController extends AnyController {

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String index(Model model) {
		log.debug("root()");
		return "redirect:/main/index";
	}	
		
	@RequestMapping(value = "/index", method = RequestMethod.GET)
	public String showIndexTest(Model model) {
		log.debug("index()");
		model.addAttribute("orderConditionsByNoForm", new OrderConditions());
		return "main/index";
	}
	
	@RequestMapping(value = "/main/index", method = RequestMethod.GET)
	public String showIndex4(Model model) {
		log.debug("index()");
		return "main/index";
	}

}
