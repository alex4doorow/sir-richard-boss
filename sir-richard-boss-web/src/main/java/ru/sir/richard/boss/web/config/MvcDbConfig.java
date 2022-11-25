package ru.sir.richard.boss.web.config;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

@Configuration
public class MvcDbConfig {
		
	private final Logger logger = LoggerFactory.getLogger(MvcDbConfig.class);
	
	@Autowired
	private Environment environment;
	
	@Autowired
	DataSource dataSource;
	
	@Bean
	public DataSource getDataSource() {			
		DriverManagerDataSource dataSource = null;
				
		logger.debug("jdbc.ds.pm.url: {}", environment.getProperty("jdbc.ds.pm.url"));
		logger.debug("jdbc.ds.pm.user: {}", environment.getProperty("jdbc.ds.pm.user"));
		logger.debug("jdbc.ds.pm.password: {}", environment.getProperty("jdbc.ds.pm.password"));
		logger.debug("application.production: {}", environment.getProperty("application.production"));	
		try {			
			dataSource = new DriverManagerDataSource();
	        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
	        dataSource.setUrl(environment.getProperty("jdbc.ds.pm.url"));
	        dataSource.setUsername(environment.getProperty("jdbc.ds.pm.user"));
	        dataSource.setPassword(environment.getProperty("jdbc.ds.pm.password"));
	        return dataSource;						
		} catch (Exception e2) {
			logger.error("Exception: {}", e2);
		}
		return null;
		
	}	
}
