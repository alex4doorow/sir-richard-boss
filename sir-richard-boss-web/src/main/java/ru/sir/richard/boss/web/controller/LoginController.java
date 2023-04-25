package ru.sir.richard.boss.web.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class LoginController {

	@RequestMapping(value = {"/index-logout"}, method = RequestMethod.GET)    
    public ResponseEntity<String> logout(@PathVariable String name) {
		log.debug("index-logout()");
        return ResponseEntity.ok().build();
    }
}
