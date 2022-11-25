package ru.sir.richard.boss;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@PropertySource(value="classpath:application-it.properties", encoding="UTF-8")
public class PropertySourceResolver {

    @Value("${example.firstProperty}") 
    private String firstProperty;
    
    @Value("${example.secondProperty}") 
    private String secondProperty;
    
    @Value("${jdbc.ds.pm.url}") 
    private String jdbcDsPmUrl;
    
    @Value("${jdbc.ds.pm.user}") 
    private String jdbcDsPmUser;
    
    @Value("${jdbc.ds.pm.password}") 
    private String jdbcDsPmPassword;
             
    public String getFirstProperty() {
        return firstProperty;
    }

    public String getSecondProperty() {
        return secondProperty;
    }

	public String getJdbcDsPmUrl() {
		return jdbcDsPmUrl;
	}

	public String getJdbcDsPmUser() {
		return jdbcDsPmUser;
	}

	public String getJdbcDsPmPassword() {
		return jdbcDsPmPassword;
	}
	
}
