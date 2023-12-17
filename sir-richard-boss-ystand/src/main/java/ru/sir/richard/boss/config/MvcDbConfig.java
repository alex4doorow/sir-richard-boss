package ru.sir.richard.boss.config;

import javax.naming.NamingException;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jndi.JndiTemplate;

@Configuration
public class MvcDbConfig {
		
	private final Logger logger = LoggerFactory.getLogger(MvcDbConfig.class);
	
	//public final static String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	//public static final String DB_SR_PRODUCTION_URL = "jdbc:mysql://127.0.0.1:3306/p326995_mysirric?user=p326995_mysirric&password=ibXmNuB3eB&useUnicode=true&characterEncoding=UTF-8&useSSL=false";
	//public static final String DB_PM_PRODUCTION_URL = "jdbc:mysql://127.0.0.1:3306/p326995_pm?user=p326995_pm&password=VRxm7i42P3&useUnicode=true&characterEncoding=UTF-8&useSSL=false";
	public static final String DB_TEST_URL = "jdbc:mysql://127.0.0.1:3306/p326995_test?user=p326995_test&password=G5JyQGcx24&useUnicode=true&characterEncoding=UTF-8";
	
	//public static final String DB_JNDI = "java:comp/env/jdbc/sirRichardBoss";
	
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
	}
}
