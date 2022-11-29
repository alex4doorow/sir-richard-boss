package ru.sir.richard.boss;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@PropertySource(value="classpath:application-it.properties", encoding="UTF-8")
public class PropertySourceResolver {

    @Value("${jdbc.ds.pm.url}") 
    private String jdbcDsPmUrl;
    
    @Value("${jdbc.ds.pm.user}") 
    private String jdbcDsPmUser;
    
    @Value("${jdbc.ds.pm.password}") 
    private String jdbcDsPmPassword;

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
