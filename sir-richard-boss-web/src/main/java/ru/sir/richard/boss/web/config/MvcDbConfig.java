package ru.sir.richard.boss.web.config;

import javax.naming.NamingException;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jndi.JndiTemplate;

import ru.sir.richard.boss.config.DbConfig;

@Configuration
public class MvcDbConfig {
		
	private final Logger logger = LoggerFactory.getLogger(MvcDbConfig.class);
	
	@Autowired
	DataSource dataSource;
	
	@Bean
	public DataSource getDataSource() {
				
		DataSource dataSource = null;
        JndiTemplate jndi = new JndiTemplate();
        logger.error("DB connection JNDI: \"{}\"", DbConfig.DB_JNDI);
        try {
            dataSource = jndi.lookup(DbConfig.DB_JNDI, DataSource.class);
        } catch (NamingException e) {
            logger.error("NamingException for " + DbConfig.DB_JNDI, e);
        }
        return dataSource;
	
		/*
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("com.mysql.jdbc.Driver");
        dataSource.setUrl(DB_PRODUCTION_URL);
        //dataSource.setUsername("p326995_test");
        //dataSource.setPassword("G5JyQGcx24");
        
        return dataSource;
       */
        
	}	
}
