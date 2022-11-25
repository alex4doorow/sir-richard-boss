package ru.sir.richard.boss.web.controller;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import ru.sir.richard.boss.api.hh.HhApi;
import ru.sir.richard.boss.model.data.HhVacanciesPage;

@Controller
public class HhController {
	
	private final Logger logger = LoggerFactory.getLogger(HhController.class);
	
	@Autowired
	private Environment environment;
	
	@RequestMapping(value = "/vacancies", method = RequestMethod.GET)
	public String vacancies(Model model) {

		logger.debug("vacancies()");
		
		final String inputText = "java";		
		HhApi hh = new HhApi(environment);
		
		HhVacanciesPage vacancies = hh.createPage(inputText, -1);
		List<HhVacanciesPage> vacanciesPages = new ArrayList<HhVacanciesPage>();
		vacanciesPages.add(vacancies);
		
		if (vacancies.getPageCount() > 1) {
			for (int pageIndex = 1; pageIndex < vacancies.getPageCount(); pageIndex++) {
				vacancies = hh.createPage(inputText, pageIndex);
				vacanciesPages.add(vacancies);		
			}		
		}		
		model.addAttribute("vacanciesPages", vacanciesPages);		

		return "hh/list";
	}
}
