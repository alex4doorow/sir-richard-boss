package ru.sir.richard.boss.web.config;

import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jndi.JndiTemplate;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.naming.NamingException;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.Properties;

@Log4j2
@Configuration
@PropertySource(value="classpath:application.properties", encoding="UTF-8")
@EntityScan("ru.sir.richard.boss.web.entity")
@EnableJpaRepositories(basePackages = "ru.sir.richard.boss.repository")
public class MvcDbConfig {

	@Autowired
	private Environment environment;

	@Bean
	public DataSource dataSource() {
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
				log.error("Exception:", e2);
			}
			return null;
		}
		// main jndi
		if (StringUtils.isNotEmpty(environment.getProperty("jdbc.jndi"))) {
			JndiTemplate jndi = new JndiTemplate();
			log.info("DB connection JNDI: \"{}\"", environment.getProperty("jdbc.jndi"));
			DataSource dataSource = null;
			try {
				dataSource = jndi.lookup(environment.getProperty("jdbc.jndi"), DataSource.class);
			} catch (NamingException e) {
				log.error("NamingException for " + environment.getProperty("jdbc.jndi"), e);
			}
			return dataSource;
		} else {
			throw new RuntimeException("dataSource is not configured!");
		}
	}

	@Bean
	public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
		final LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
		em.setDataSource(dataSource());
		em.setPackagesToScan("ru.sir.richard.boss.model.entity"); // TODO entity scan didn't work

		final HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
		em.setJpaVendorAdapter(vendorAdapter);
		em.setJpaProperties(additionalProperties());
		return em;
	}

	@Bean
	public PlatformTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
		JpaTransactionManager transactionManager = new JpaTransactionManager();
		transactionManager.setEntityManagerFactory(entityManagerFactory);
		return transactionManager;
	}

	final Properties additionalProperties() {
		final Properties hibernateProperties = new Properties();
		hibernateProperties.setProperty("hibernate.hbm2ddl.auto", environment.getProperty("hibernate.hbm2ddl.auto"));
		hibernateProperties.setProperty("hibernate.dialect", environment.getProperty("hibernate.dialect"));
		return hibernateProperties;
	}
}
