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
import ru.sir.richard.boss.service.YandexMarketService;

@SpringBootApplication
public class YandexMarketConsoleApplication implements CommandLineRunner {
	
	private static String DB_URL = DbConfig.DB_PM_PRODUCTION_URL;
	
	private final Logger logger = LoggerFactory.getLogger(YandexMarketConsoleApplication.class);
			
	@Autowired
	DataSource dataSource;
	
	@Bean		
	public DataSource getDataSource() {			
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("com.mysql.jdbc.Driver");        
        dataSource.setUrl(DB_URL);
        return dataSource;	        
	}
	
	
	@Autowired
    private YandexMarketService yandexMarketService;
	
	public static void main(String[] args) throws Exception {    	   	
        SpringApplication app = new SpringApplication(YandexMarketConsoleApplication.class);
        app.run(args);
    }


	@Override
	public void run(String... args) throws Exception {		
		logger.debug("YandexMarketConsoleApplication start");
		yandexMarketService.run();
        exit(0);
	}

}
