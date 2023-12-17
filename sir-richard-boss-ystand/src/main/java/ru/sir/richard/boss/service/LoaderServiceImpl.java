package ru.sir.richard.boss.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import ru.sir.richard.boss.config.LoaderConfig;
import ru.sir.richard.boss.data.raw.CdekCityDataRaw;
import ru.sir.richard.boss.data.raw.CdekPvzDataRaw;

@Service("loaderService")
public class LoaderServiceImpl implements LoaderService {
	
	private final Logger logger = LoggerFactory.getLogger(LoaderServiceImpl.class);
	
	@Autowired  
	protected JdbcTemplate jdbcTemplate;
	
	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	@Override
	public void run() {
		logger.debug("run():{}", "start");
		
		clearCities();
		cityLoadFromExcel("City_RUS_20190328.xls");
		
		//clearPvzs();
		//pvzLoadFromExcel();
		logger.debug("run():{}", "finish");
		
	}
		

	private void cityLoadFromExcel(String fileName) {
		
		HSSFWorkbook myExcelBook;
		List<CdekCityDataRaw> outputData = null;
		try {
			
			myExcelBook = new HSSFWorkbook(new FileInputStream(LoaderConfig.FILE_PATH_CITY_CDEK_IN + fileName));
			HSSFSheet myExcelSheet = myExcelBook.getSheetAt(0);
			
			HSSFRow row;
			
			int id = 0;
			String name = "";
			String cityName = "";
			String regionName = "";
			int center = 0;
			double casheLimit = 0;

			final int startIndex = 1;
			outputData = new ArrayList<CdekCityDataRaw>();			
		
			for (int i = startIndex; i <= myExcelSheet.getLastRowNum(); i++) {
				
				id = 0;
				name = "";
				cityName = "";
				regionName = "";
				center = 0;
				casheLimit = 0;
				
				row = myExcelSheet.getRow(i);				
				CdekCityDataRaw raw = null;
				if (row.getCell(0) != null && row.getCell(0).getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {
					
					id = BigDecimal.valueOf(row.getCell(0).getNumericCellValue()).intValue();					
					try {
						
						name = row.getCell(1).getStringCellValue();
						cityName = row.getCell(2).getStringCellValue();
						regionName = row.getCell(3).getStringCellValue();
						
						
						if (row.getCell(4).getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {
							center = BigDecimal.valueOf(row.getCell(4).getNumericCellValue()).intValue();
						} else {
							center = 0;
						}
						if (row.getCell(5).getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {
							casheLimit = row.getCell(5).getNumericCellValue();	
						} else {
							casheLimit = 0;
						}
						
						
					} catch (Exception e) {
						center = 0;
						logger.error("{},{}",e.getMessage(), id);
					} 					
					
					
					//logger.debug("cityLoadFromExcel {},{}", id, name);	
					
					raw = new CdekCityDataRaw();
					raw.setId(id);
					raw.setName(name);
					raw.setCityName(cityName);
					raw.setRegionName(regionName);
					raw.setCenter(center);
					raw.setCasheLimit(casheLimit);
					
					outputData.add(raw);
					
				} 				
				//break;
			}			
			myExcelBook.close();
			logger.debug("{}", outputData);	
			
		} catch (FileNotFoundException e) {
			logger.error(e.getMessage());
		} catch (IOException e) {
			logger.error(e.getMessage());			
		}
		
		for (CdekCityDataRaw city : outputData) {
			addCity(city);
		}
		
		
		
	}
	
	@SuppressWarnings("deprecation")
	private void pvzLoadFromExcel() {
		
		XSSFWorkbook myExcelBook;
		List<CdekPvzDataRaw> outputData = null;
		OPCPackage pkg;
		try {
				
			pkg = OPCPackage.open(new File(LoaderConfig.FILE_PATH_PVZ_CDEK_IN));			
			myExcelBook = new XSSFWorkbook(pkg);
			XSSFSheet myExcelSheet = myExcelBook.getSheetAt(0);
			XSSFRow row;
		
			final int startIndex = 1;
			outputData = new ArrayList<CdekPvzDataRaw>();			
		
			for (int i = startIndex; i <= myExcelSheet.getLastRowNum(); i++) {
				row = myExcelSheet.getRow(i);
				
				CdekPvzDataRaw raw = null;
				if (row.getCell(0) != null && row.getCell(0).getCellType() == HSSFCell.CELL_TYPE_STRING) {
					
					// Город	Код	Адрес	Телефоны	График работы	Индекс

					
					String city = row.getCell(0).getStringCellValue();				
					String code = row.getCell(1).getStringCellValue();
					String address = row.getCell(2).getStringCellValue();
					String phones = row.getCell(3).getStringCellValue();
					String scheduleWork = row.getCell(4).getStringCellValue();
					String postCode = row.getCell(5).getStringCellValue();
										
					raw = new CdekPvzDataRaw();
					raw.setCity(city);
					raw.setCode(code);
					raw.setAddress(address);
					raw.setPhones(phones);
					raw.setScheduleWorks(scheduleWork);
					raw.setPostCode(postCode);
										
					
					outputData.add(raw);
				} 				
				//break;
			}			
			myExcelBook.close();
			//logger.debug("{}", outputData);	
			
		} catch (FileNotFoundException e) {
			logger.error(e.getMessage());
		} catch (IOException e) {
			logger.error(e.getMessage());			
		} catch (InvalidFormatException e) {
			logger.error(e.getMessage());			
		}	
		
		for (CdekPvzDataRaw pvz : outputData) {
			addPvz(pvz);
		}
	}
	
	private void clearCities() {
		/*
		final String sqlDeletePvzs = "DELETE FROM sr_test_cdek_city";
		this.jdbcTemplate.update(sqlDeletePvzs, new Object[] {});
		*/
	}
	
	private void clearPvzs() {
		final String sqlDeletePvzs = "DELETE FROM sr_wiki_cdek_pvz";
		this.jdbcTemplate.update(sqlDeletePvzs, new Object[] {});
	}
	
	private void addCity(CdekCityDataRaw cityRaw) {		
	  /*
		final String sqlInsertCity = "INSERT INTO sr_test_cdek_city"
				+ " (id, name, cityName, regionName, center, cache_limit)"
				+ " VALUES"
				+ " (?, ?, ?, ?, ?, ?)";
		this.jdbcTemplate.update(sqlInsertCity, new Object[] { 
				cityRaw.getId(), 
				cityRaw.getName(), 
				cityRaw.getCityName(), 
				cityRaw.getRegionName(), 
				cityRaw.getCenter(),
				cityRaw.getCasheLimit()});
				*/
	}
		
	private void addPvz(CdekPvzDataRaw pvzRaw) {		
		
		final String sqlInsertPvz = "INSERT INTO sr_wiki_cdek_pvz"
				+ " (code, city, address, phones, schedule_work, post_code)"
				+ " VALUES"
				+ " (?, ?, ?, ?, ?, ?)";
		this.jdbcTemplate.update(sqlInsertPvz, new Object[] { 
				pvzRaw.getCode(), 
				pvzRaw.getCity(), 
				pvzRaw.getAddress(), 
				pvzRaw.getPhones(), 
				pvzRaw.getScheduleWorks(), 
				pvzRaw.getPostCode()});
		//final int orderId = getLastInsert();
	}

}
