package ru.sir.richard.boss.web.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

@Component
@Slf4j
public class ExceptionHandler implements HandlerExceptionResolver {

	@Override
    public ModelAndView resolveException(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
                                         Object o, Exception ex) {
        
		log.error("resolveException():{}, {}", httpServletRequest.getRequestURL(), ex);
		
	    String errorHeader = "Http Error Code: 500. Exception";
	    String errorMsg = "Http Error Code: 500. Exception";
	    
	    String stackTrace = "";
	    if (ex != null) {
	    	stackTrace = ex.getMessage() + ":" + "\r\n";       
	        for (java.lang.StackTraceElement e : ex.getStackTrace()) {
	        	String line = e.toString();        	
	        	if (line.contains("ru.sir.richard")) {
	        		line = "<b>" + line + "</b>";
	        	}
	        	stackTrace += "\t" + line + "\r\n";
	        }
	    }       
        
        ModelAndView errorPage = new ModelAndView("errors/exception2page", "exceptionMsg", "ExceptionHandler msg: " + ex.toString());
        errorPage.addObject("errorHeader", errorHeader);
	    errorPage.addObject("errorMsg", errorMsg);
	    errorPage.addObject("exception", ex);
	    errorPage.addObject("stackTrace", stackTrace);
	    errorPage.addObject("url", httpServletRequest.getRequestURL());
	    //errorPage.setViewName("errors/exception2page");
	    return errorPage;
        
        //return new ModelAndView("exception2page", "exceptionMsg", "ExceptionHandler msg: " + e.toString());
    }

}
