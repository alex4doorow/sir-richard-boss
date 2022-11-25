package ru.sir.richard.boss.web.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class LoginController {
	
	private final Logger logger = LoggerFactory.getLogger(LoginController.class);
	
	/*
	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public ModelAndView login(@RequestParam(value = "error",required = false) String error,	@RequestParam(value = "logout",	required = false) String logout) {
		
		logger.debug("login()");
		
		ModelAndView model = new ModelAndView();
		if (error != null) {
			logger.debug("login.error: {}", error);
			model.addObject("error", "Invalid Credentials provided.");
		}
		if (logout != null) {
			logger.debug("login: {}", "logout");
			model.addObject("message", "Logged out from JournalDEV successfully.");
		}

		model.setViewName("login");
		return model;
	}
	*/
		
	@RequestMapping(value = {"/index-logout"}, method = RequestMethod.GET)    
    public ResponseEntity<String> logout(@PathVariable String name) {
		logger.debug("index-logout()");
        return ResponseEntity.ok().build();
    }

}
