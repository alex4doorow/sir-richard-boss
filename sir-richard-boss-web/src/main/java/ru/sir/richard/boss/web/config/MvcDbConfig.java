package ru.sir.richard.boss.web.config;

import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jndi.JndiTemplate;

import javax.naming.NamingException;
import javax.sql.DataSource;

@Log4j2
@Configuration
@PropertySource(value="classpath:application.properties", encoding="UTF-8")
public class MvcDbConfig {

	@Autowired
	private Environment environment;

	@Autowired
	DataSource dataSource;

	@Bean
	public DataSource getDataSource() {
		if (StringUtils.isNotEmpty(environment.getProperty("jdbc.ds.pm.url"))) {
			// test config db settings
			DriverManagerDataSource dataSource;
			log.debug("jdbc.ds.pm.url: {}", environment.getProperty("jdbc.ds.pm.url"));
			log.debug("jdbc.ds.pm.user: {}", environment.getProperty("jdbc.ds.pm.user"));
			log.debug("jdbc.ds.pm.password: {}", environment.getProperty("jdbc.ds.pm.password"));
			log.debug("application.production: {}", environment.getProperty("application.production"));
			try {
				dataSource = new DriverManagerDataSource();
				dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
				dataSource.setUrl(environment.getProperty("jdbc.ds.pm.url"));
				dataSource.setUsername(environment.getProperty("jdbc.ds.pm.user"));
				dataSource.setPassword(environment.getProperty("jdbc.ds.pm.password"));
				return dataSource;
			} catch (Exception e2) {
				log.error("Exception: {}", e2);
			}
			return null;
		}
		// main jndi
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
