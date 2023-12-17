package ru.sir.richard.boss;

import static java.lang.System.exit;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import ru.sir.richard.boss.config.DbConfig;
import ru.sir.richard.boss.service.ConverterService;

@SpringBootApplication
public class PMConverterConsoleApplication implements CommandLineRunner {
	
	//public static final String DB_PM_TEST_URL = "jdbc:mysql://127.0.0.1:3306/p326995_pm_test2?user=p326995_pm_test2&password=zc3rkf4nsT&useUnicode=true&characterEncoding=UTF-8";
	
	private final Logger logger = LoggerFactory.getLogger(PMConverterConsoleApplication.class);
	
	@Autowired
	private ConverterService converterService;
	

	@Bean		
	public DataSource getDataSource() {			
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("com.mysql.jdbc.Driver");        
        dataSource.setUrl(DbConfig.DB_PM_PRODUCTION_URL);
        return dataSource;	        
	}
	
	public static void main(String[] args) throws Exception {
	       
        SpringApplication app = new SpringApplication(PMConverterConsoleApplication.class);
        app.run(args);
    }

	@Override
	public void run(String... args) throws Exception {
    	logger.debug("PMConverterConsoleApplication start"); 
    	converterService.run();    	
        exit(0);		
	}

}
