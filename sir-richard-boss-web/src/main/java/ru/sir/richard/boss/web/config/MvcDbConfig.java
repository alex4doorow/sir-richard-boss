package ru.sir.richard.boss.web.config;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.jndi.JndiTemplate;

import javax.naming.NamingException;
import javax.sql.DataSource;

@Log4j2
@Configuration
public class MvcDbConfig {

	@Autowired
	private Environment environment;
	
	@Autowired
	DataSource dataSource;
	
	@Bean
	public DataSource getDataSource() {
		JndiTemplate jndi = new JndiTemplate();
		log.info("DB connection JNDI: \"{}\"", environment.getProperty("jdbc.jndi"));
		try {
			dataSource = jndi.lookup(environment.getProperty("jdbc.jndi"), DataSource.class);
		} catch (NamingException e) {
			log.error("NamingException for " + environment.getProperty("jdbc.jndi"), e);
		}
		return dataSource;
	}
}
