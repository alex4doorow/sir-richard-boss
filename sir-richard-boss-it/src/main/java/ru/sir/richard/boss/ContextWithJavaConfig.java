package ru.sir.richard.boss;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import lombok.extern.log4j.Log4j2;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import java.util.Properties;

@Configuration
@ComponentScan(basePackages = {"ru.sir.richard.boss"})
@PropertySource(value="classpath:application-it.properties", encoding="UTF-8")
@Log4j2
public class ContextWithJavaConfig {

	@Autowired
	private Environment environment;
	
	@Autowired 
	private PropertySourceResolver propertySourceResolver;
	
	@Bean
	public DataSource dataSource() {
		
		log.debug("jdbc.ds.pm.url: {}", propertySourceResolver.getJdbcDsPmUrl());
		log.debug("jdbc.ds.pm.user: {}", propertySourceResolver.getJdbcDsPmUser());
		log.debug("jdbc.ds.pm.password: {}", propertySourceResolver.getJdbcDsPmPassword());
				
		DriverManagerDataSource dataSource = null;
		try {
			dataSource = new DriverManagerDataSource();
	        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
	        dataSource.setUrl(propertySourceResolver.getJdbcDsPmUrl());
	        dataSource.setUsername(propertySourceResolver.getJdbcDsPmUser());
	        dataSource.setPassword(propertySourceResolver.getJdbcDsPmPassword());
	        return dataSource;
		} catch (Exception e2) {
			log.error("Exception:", e2);
		}
		return dataSource;        
	}

	@Bean
	public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
		final LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
		em.setDataSource(dataSource());
		em.setPackagesToScan("ru.sir.richard.boss.model.entity");

		final HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
		em.setJpaVendorAdapter(vendorAdapter);
		em.setJpaProperties(additionalProperties());
		return em;
	}

	final Properties additionalProperties() {
		final Properties hibernateProperties = new Properties();
		hibernateProperties.setProperty("hibernate.hbm2ddl.auto", environment.getProperty("hibernate.hbm2ddl.auto"));
		hibernateProperties.setProperty("hibernate.dialect", environment.getProperty("hibernate.dialect"));
		return hibernateProperties;
	}
}
