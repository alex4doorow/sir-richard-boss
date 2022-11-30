package ru.sir.richard.boss.web.config;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.jmx.export.annotation.AnnotationMBeanExporter;
import org.springframework.jmx.support.RegistrationPolicy;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.handler.SimpleMappingExceptionResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;

import java.util.Locale;
import java.util.Properties;

@Log4j2
@Configuration
@EnableWebMvc
@EnableScheduling
@ComponentScan(basePackages = {"ru.sir.richard.boss.web", "ru.sir.richard.boss.dao", "ru.sir.richard.boss.crm", "ru.sir.richard.boss.api", "ru.sir.richard.boss.converter"})
@PropertySource(value="classpath:application.properties", encoding="UTF-8")
//@PropertySource(value="classpath:application-production.properties", encoding="UTF-8")
@PropertySource(value="classpath:application-test.properties", encoding="UTF-8")
public class MvcWebConfig implements WebMvcConfigurer {

	@Value("${application.name}")
	private String applicationName;
	
	@Value("${application.version}")
	private String applicationVersion;
	
	public String getApplicationName() {
		return applicationName;
	}

	public String getApplicationVersion() {
		return applicationVersion;
	}
		
	@Bean
	public InternalResourceViewResolver viewResolver() {
		InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
		viewResolver.setViewClass(JstlView.class);
		viewResolver.setPrefix("/WEB-INF/views/jsp/");
		viewResolver.setSuffix(".jsp");
		return viewResolver;
	}
	
	@Bean
	public ResourceBundleMessageSource messageSource() {
		ResourceBundleMessageSource rb = new ResourceBundleMessageSource();
		rb.setBasenames(new String[] { "messages/messages", "messages/validation" });
		return rb;
	}	
	
	@Bean
	public SessionLocaleResolver localeResolver() {
	    SessionLocaleResolver slr = new SessionLocaleResolver();
	    slr.setDefaultLocale(new Locale("ru_RU")); 
	    return slr;
	}
	
	@Bean
	public LocaleChangeInterceptor localeChangeInterceptor() {
	    LocaleChangeInterceptor lci = new LocaleChangeInterceptor();
	    lci.setParamName("lang");
	    return lci;
	}
	
	@Bean
	public AnnotationMBeanExporter annotationMBeanExporter() {
	    AnnotationMBeanExporter annotationMBeanExporter = new AnnotationMBeanExporter();
	    annotationMBeanExporter.addExcludedBean("dataSource");
	    annotationMBeanExporter.setRegistrationPolicy(RegistrationPolicy.IGNORE_EXISTING);
	    return annotationMBeanExporter;
	}
	
	@Bean
    public static PropertySourcesPlaceholderConfigurer propertyConfigInDev() {
        return new PropertySourcesPlaceholderConfigurer();
    }
	
	@Bean(name = "simpleMappingExceptionResolver")
	public SimpleMappingExceptionResolver createSimpleMappingExceptionResolver() {
		log.info("Creating SimpleMappingExceptionResolver");
		SimpleMappingExceptionResolver r = new SimpleMappingExceptionResolver();

		Properties mappings = new Properties();
		mappings.setProperty("RuntimeException", "runtimeException");
		
		r.setExceptionMappings(mappings); // None by default
		r.setExceptionAttribute("ex"); // Default is "exception"
		r.setWarnLogCategory("sir.richard.boss.ExceptionLogger"); // No default

		/*
		 * Normally Spring MVC has no default error view and this class is the
		 * only way to define one. A nice feature of Spring Boot is the ability
		 * to provide a very basic default error view (otherwise the application
		 * server typically returns a Java stack trace which is not acceptable
		 * in production). See Blog for more details.
		 * 
		 * To stick with the Spring Boot approach, DO NOT set this property of
		 * SimpleMappingExceptionResolver.
		 * 
		 * Here we are choosing to use SimpleMappingExceptionResolver since many
		 * Spring applications have used the approach since Spring V1. Normally
		 * we would specify the view as "error" to match Spring Boot, however so
		 * you can see what is happening, we are using a different page.
		 */
		r.setDefaultErrorView("error/exception2page");
		return r;
	}	
	
	@Override	
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/resources/**").addResourceLocations("/resources/");
	}	
		
	@Override
    public void addViewControllers(ViewControllerRegistry registry) {        
		registry.addViewController("/login");
		registry.addViewController("/session-expired");
        registry.addViewController("/invalid-session");		
		registry.addViewController("/news");        
        registry.addViewController("/anonymous");        
    }
}
