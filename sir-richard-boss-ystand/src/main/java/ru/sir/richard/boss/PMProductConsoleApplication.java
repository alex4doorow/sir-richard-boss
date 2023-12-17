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
import ru.sir.richard.boss.dao.WikiDao;
import ru.sir.richard.boss.service.ConverterService;

@SpringBootApplication
public class PMProductConsoleApplication implements CommandLineRunner {

	private final Logger logger = LoggerFactory.getLogger(PMProductConsoleApplication.class);
	
	@Autowired
	DataSource dataSource;
	
	@Autowired  
	private WikiDao wiki;
	
	@Bean		
	public DataSource getDataSource() {			
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("com.mysql.jdbc.Driver");        
        dataSource.setUrl(DbConfig.DB_PM_PRODUCTION_URL);
        return dataSource;	        
	}
	
	@Autowired
	private ConverterService converterService;
		
	public static void main(String[] args) throws Exception {	       
        SpringApplication app = new SpringApplication(PMProductConsoleApplication.class);
        app.run(args);
    }

	@Override
	public void run(String... args) throws Exception {
    	logger.debug("PMProductConsoleApplication start");
    	//wiki.init();
    	//converterService.createDescriptions();
    	logger.debug("PMProductConsoleApplication finish"); 
    	exit(0);	

	}


}
