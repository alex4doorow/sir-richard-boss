package ru.sir.richard.boss.dao;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

import ru.sir.richard.boss.model.data.conditions.OrderConditions;
import ru.sir.richard.boss.model.data.conditions.ProductConditions;
import ru.sir.richard.boss.model.data.conditions.ProductSalesReportConditions;

public interface ConfigDao {
		
	Map<String, String> getConfig();
	String getConfigValue(String key);
		
	String getDefaultFormStringValueByKey(int userId, String formCode, String key);
	String getFormStringValueByKey(int userId, String formCode, String key, String defaultValue);
	Date getFormDateValueByKey(int userId, String formCode, String key, Date defaultValue);
	int getFormIntegerValueByKey(int userId, String formCode, String key, int defaultValue);
	boolean getFormBooleanValueByKey(int userId, String formCode, String key, boolean defaultValue);
	BigDecimal getFormBigDecimalValueByKey(int userId, String formCode, String key, BigDecimal defaultValue);
	
	void deleteFormValues(int userId, String formCode);
	void saveFormStringValue(int userId, String formCode, String key, String value);
	void saveFormDateValue(int userId, String formCode, String key, Date value);
	void saveFormIntegerValue(int userId, String formCode, String key, int value);
	void saveFormBooleanValue(int userId, String formCode, String key, boolean value);
	void saveFormBigDecimalValue(int userId, String formCode, String key, BigDecimal value);
		
	ProductConditions loadYmProductConditions(int userId);
	void saveYmProductConditions(int userId, ProductConditions productConditionForm);
	
	ProductConditions loadOzonProductConditions(int userId);
	void saveOzonProductConditions(int userId, ProductConditions productConditionForm);
	
	OrderConditions loadOrderConditions(int userId);
	void saveOrderConditions(int userId, OrderConditions conditions);
	
	ProductSalesReportConditions loadProductSalesByQueryReportConditions(int userId);
	void saveProductSalesByQueryReportConditions(int userId, ProductSalesReportConditions conditions);
	
	

}
