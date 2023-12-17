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
import ru.sir.richard.boss.service.PricerService;

@SpringBootApplication
public class PricerConsoleApplication implements CommandLineRunner {
	
	private static String DB_URL = null;
	
	private final Logger logger = LoggerFactory.getLogger(PricerConsoleApplication.class);
			
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
    private PricerService pricerService;
	
	public static void main(String[] args) throws Exception {
		
		if (args.length < 2) {
    		System.out.println("Error count arguments!");
    		exit(0);
    	}    	
    	if (args[1].equalsIgnoreCase("sr")) {
    		DB_URL = DbConfig.DB_SR_PRODUCTION_URL;
    		
    	} else if (args[1].equalsIgnoreCase("pm")) {
    		DB_URL = DbConfig.DB_PM_PRODUCTION_URL;    		
    	} else {
    		System.out.println("Error db's argument!");
    		exit(0);
    	} 
    	   	
        SpringApplication app = new SpringApplication(PricerConsoleApplication.class);
        app.run(args);
    }

    @Override
    public void run(String... args) throws Exception {
    	//System.out.println(Charset.defaultCharset());
    	
    	logger.debug("PricerConsoleApplication start: {}, {}", args[0], args[1]); 
    	
    	if (args[0].equalsIgnoreCase("sititek")) {    		
        	pricerService.runSititek();    		
    	} else if (args[0].equalsIgnoreCase("sledopyt")) {
        	pricerService.runSledopyt();    		
    	} else if (args[0].equalsIgnoreCase("ecosniper")) {
        	pricerService.runEcosniper();    		
    	}      	   	
        exit(0);
    }

}
