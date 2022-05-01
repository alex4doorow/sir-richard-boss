package ru.sir.richard.boss.dao;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import ru.sir.richard.boss.model.data.Product;
import ru.sir.richard.boss.model.data.conditions.OrderConditions;
import ru.sir.richard.boss.model.data.conditions.ProductConditions;
import ru.sir.richard.boss.model.data.conditions.ProductSalesReportConditions;
import ru.sir.richard.boss.model.types.CustomerTypes;
import ru.sir.richard.boss.model.types.DeliveryTypes;
import ru.sir.richard.boss.model.types.OrderAdvertTypes;
import ru.sir.richard.boss.model.types.OrderStatuses;
import ru.sir.richard.boss.model.types.OrderTypes;
import ru.sir.richard.boss.model.types.PaymentTypes;
import ru.sir.richard.boss.model.types.ReportPeriodTypes;
import ru.sir.richard.boss.model.types.SupplierTypes;
import ru.sir.richard.boss.model.utils.DateTimeUtils;
import ru.sir.richard.boss.model.utils.TextUtils;

@Repository
public class ConfigDaoImpl extends AnyDaoImpl implements ConfigDao {
	
	private final Logger logger = LoggerFactory.getLogger(ConfigDaoImpl.class);
	
	private Map<String, String> config;
	
	@Autowired
	private WikiDao wikiDao;
		
	@PostConstruct
	public void init() {
		logger.debug("ConfigDaoImpl.int()");
		this.config = instanceConfig();
	}
		
	@Override
	public Map<String, String> getConfig() {
		return this.config;
	}
	
	@Override
	public String getConfigValue(String key) {
		String result = config.get(key);
		return result == null ? "" : result.trim();
	}
	
	private Map<String, String> instanceConfig() {
		logger.debug("instanceConfig()");
		
		Map<String, String> result = new HashMap<String, String>();
		
		final String sqlSelectConfig = "SELECT * FROM sr_sys_config s ORDER BY s.code";
		
		this.jdbcTemplate.query(sqlSelectConfig,
				new RowMapper<String>() {
					@Override
					public String mapRow(ResultSet rs, int rowNum) throws SQLException {
						String key = rs.getString("CODE");
						String value = rs.getString("VALUE");
						result.put(key, value);
						return value;
					}
				});	
		return result;		
	}
	
	@Override
	public String getFormStringValueByKey(int userId, String formCode, String key, String defaultValue) {
		
		final String sqlSelectFormValue = "SELECT MAX(value) VALUE FROM sr_user_query uq"
				+ "  WHERE uq.user_id = ?"
				+ "    AND uq.form = ?"
				+ "    AND uq.code = ?";
		String value = this.jdbcTemplate.queryForObject(sqlSelectFormValue,
		        new Object[]{userId, formCode, key},
		        new RowMapper<String>() {
					@Override
		            public String mapRow(ResultSet rs, int rowNum) throws SQLException {
		            	return rs.getString("VALUE");	
		            }
		        });
		return value == null ? defaultValue : value;
	}
	
	@Override
	public String getDefaultFormStringValueByKey(int userId, String formCode, String key) {
		return getFormStringValueByKey(userId, formCode, key, "");
	}
	
	@Override
	public Date getFormDateValueByKey(int userId, String formCode, String key, Date defaultValue) {
		String value = getFormStringValueByKey(userId, formCode, key, null);
		try {
			return StringUtils.isEmpty(value) ? defaultValue : DateTimeUtils.defaultFormatStringToDate(value);
		} catch (ParseException e) {			
			logger.error("error getFormDateValueByKey():{},{},{},{}", e, formCode, key, value);
			return null;
		}		
	}
	
	@Override
	public int getFormIntegerValueByKey(int userId, String formCode, String key, int defaultValue) {
		String value = getFormStringValueByKey(userId, formCode, key, null);
		try {
			return StringUtils.isEmpty(value) ? defaultValue : Integer.parseInt(value);
		} catch (Exception e) {			
			logger.error("error getFormIntegerValueByKey():{},{},{},{}", e, formCode, key, value);
			return defaultValue;
		}		
	}
	
	@Override
	public boolean getFormBooleanValueByKey(int userId, String formCode, String key, boolean defaultValue) {
		Integer value = getFormIntegerValueByKey(userId, formCode, key, 0);
		if (value == 0) {
			return false;
		} else {
			return true;
		}
	}
	
	@Override
	public BigDecimal getFormBigDecimalValueByKey(int userId, String formCode, String key, BigDecimal defaultValue) {
		String value = getFormStringValueByKey(userId, formCode, key, null);
		try {
			return StringUtils.isEmpty(value) ? defaultValue : new BigDecimal(value);
		} catch (Exception e) {			
			logger.error("error getFormBigDecimalValueByKey():{},{},{},{}", e, formCode, key, value);
			return defaultValue;
		}	
		
	}
	
	@Override
	public void deleteFormValues(int userId, String formCode) {		
		final String sqlDeleteFormValues = "DELETE FROM sr_user_query WHERE user_id = ? AND form = ?";
		this.jdbcTemplate.update(sqlDeleteFormValues, new Object[] {userId, formCode});
	}
	
	@Override
	public void saveFormStringValue(int userId, String formCode, String key, String value) {
		
		final String sqlDeleteFormValue = "DELETE FROM sr_user_query WHERE user_id = ? AND form = ? AND code = ?";
		this.jdbcTemplate.update(sqlDeleteFormValue, new Object[] {userId, formCode, key});
		
		if (value == null) {
			return;
		}		
		final String sqlInsertFormValue = "INSERT INTO sr_user_query"
				+ " (user_id, form, code, value)"
				+ " VALUES"
				+ " (?, ?, ?, ?)";	
		this.jdbcTemplate.update(sqlInsertFormValue, new Object[] { 
				userId, formCode, key, value});		
	}
	
	@Override
	public void saveFormDateValue(int userId, String formCode, String key, Date value) {
		saveFormStringValue(userId, formCode, key, DateTimeUtils.defaultFormatDate(value));
	}
	
	@Override
	public void saveFormIntegerValue(int userId, String formCode, String key, int value) {
		saveFormStringValue(userId, formCode, key, Integer.toString(value));		
	}
	
	public void saveFormBooleanValue(int userId, String formCode, String key, boolean value) {
		String result = value ? "1" : "0";
		saveFormStringValue(userId, formCode, key, result);
	}
	
	@Override
	public void saveFormBigDecimalValue(int userId, String formCode, String key, BigDecimal value) {
		saveFormStringValue(userId, formCode, key, value.toPlainString());		
	}
	
	@Override
	public ProductConditions loadYmProductConditions(int userId) {
		ProductConditions productConditions = new ProductConditions();
		int yandexSellerExist = getFormIntegerValueByKey(userId, "ymProductConditionsForm", "yandexSellerExist", 1);
		productConditions.setYandexSellerExist(yandexSellerExist);
		
		int supplierStockExist = getFormIntegerValueByKey(userId, "ymProductConditionsForm", "supplierStockExist", 1);
		productConditions.setSupplierStockExist(supplierStockExist);
		
		String name = getFormStringValueByKey(userId, "ymProductConditionsForm", "name", "");
		productConditions.setName(name);
		
		String sku = getFormStringValueByKey(userId, "ymProductConditionsForm", "sku", "");
		productConditions.setSku(sku);
		
		String yandexSku = getFormStringValueByKey(userId, "ymProductConditionsForm", "yandexSku", "");
		productConditions.setYandexSku(yandexSku);
	
		Set<SupplierTypes> suppliers = SupplierTypes.getStatusesByArray(getFormStringValueByKey(userId, "ymProductConditionsForm", "suppliers", ""));
		productConditions.setSuppliers(suppliers);				
				
		return productConditions;
	}
	
	@Override
	public void saveYmProductConditions(int userId, ProductConditions conditions) {
						
		saveFormIntegerValue(userId, "ymProductConditionsForm", "yandexSellerExist", conditions.getYandexSellerExist());
		saveFormIntegerValue(userId, "ymProductConditionsForm", "supplierStockExist", conditions.getSupplierStockExist());
		
		saveFormStringValue(userId, "ymProductConditionsForm", "name", conditions.getName());
		saveFormStringValue(userId, "ymProductConditionsForm", "sku", conditions.getSku());
		saveFormStringValue(userId, "ymProductConditionsForm", "yandexSku", conditions.getYandexSku());
							
		String strSuppliers = SupplierTypes.getArrayByStatuses(conditions.getSuppliers());
		saveFormStringValue(userId, "ymProductConditionsForm", "suppliers", strSuppliers);
	}
	
	@Override
	public ProductConditions loadOzonProductConditions(int userId) {
		ProductConditions productConditions = new ProductConditions();
		int ozonSellerExist = getFormIntegerValueByKey(userId, "ozonProductConditionsForm", "ozonSellerExist", 1);
		productConditions.setOzonSellerExist(ozonSellerExist);
		
		int supplierStockExist = getFormIntegerValueByKey(userId, "ozonProductConditionsForm", "supplierStockExist", 1);
		productConditions.setSupplierStockExist(supplierStockExist);
		
		String name = getFormStringValueByKey(userId, "ozonProductConditionsForm", "name", "");
		productConditions.setName(name);
		
		String sku = getFormStringValueByKey(userId, "ozonProductConditionsForm", "sku", "");
		productConditions.setSku(sku);
		
		String ozonSku = getFormStringValueByKey(userId, "ozonProductConditionsForm", "ozonSku", "");
		productConditions.setOzonSku(ozonSku);
	
		Set<SupplierTypes> suppliers = SupplierTypes.getStatusesByArray(getFormStringValueByKey(userId, "ozonProductConditionsForm", "suppliers", ""));
		productConditions.setSuppliers(suppliers);				
				
		return productConditions;
	}
	
	@Override
	public void saveOzonProductConditions(int userId, ProductConditions conditions) {
						
		saveFormIntegerValue(userId, "ozonProductConditionsForm", "ozonSellerExist", conditions.getOzonSellerExist());
		saveFormIntegerValue(userId, "ozonProductConditionsForm", "supplierStockExist", conditions.getSupplierStockExist());
		
		saveFormStringValue(userId, "ozonProductConditionsForm", "name", conditions.getName());
		saveFormStringValue(userId, "ozonProductConditionsForm", "sku", conditions.getSku());
		saveFormStringValue(userId, "ozonProductConditionsForm", "ozonSku", conditions.getOzonSku());
							
		String strSuppliers = SupplierTypes.getArrayByStatuses(conditions.getSuppliers());
		saveFormStringValue(userId, "ozonProductConditionsForm", "suppliers", strSuppliers);
	}
	
	@Override
	public OrderConditions loadOrderConditions(int userId) {
		
		OrderConditions conditions = new OrderConditions();
		int reportPeriodTypeId = getFormIntegerValueByKey(userId, "orderConditionsForm", "reportPeriodType", ReportPeriodTypes.ANY_PERIOD.getId());
		conditions.setReportPeriodType(ReportPeriodTypes.getValueById(reportPeriodTypeId));
		
		conditions.setPeriodExist(getFormBooleanValueByKey(userId, "orderConditionsForm", "periodExist", true));
		conditions.setReportPeriodMonth(getFormIntegerValueByKey(userId, "orderConditionsForm", "reportPeriodMonth", DateTimeUtils.monthOfDate(new Date())));
		conditions.setReportPeriodYear(getFormIntegerValueByKey(userId, "orderConditionsForm", "reportPeriodYear", DateTimeUtils.yearOfDate(new Date())));
		
		conditions.setNo(getFormStringValueByKey(userId, "orderConditionsForm", "no", ""));
		conditions.setTrackCode(getDefaultFormStringValueByKey(userId, "orderConditionsForm", "trackCode"));
		conditions.setDeliveryAddress(getDefaultFormStringValueByKey(userId, "orderConditionsForm", "deliveryAddress"));
		
		int productId = getFormIntegerValueByKey(userId, "orderConditionsForm", "product", 0);
		if (productId > 0) {
			conditions.setProduct(wikiDao.getProductById(productId));
		} else {
			conditions.setProduct(Product.createEmpty());
		}
		
		conditions.getCustomerConditions().setPersonPhoneNumber(getDefaultFormStringValueByKey(userId, "orderConditionsForm", "customerConditions.person.phoneNumber"));
		conditions.getCustomerConditions().setPersonEmail(getDefaultFormStringValueByKey(userId, "orderConditionsForm", "customerConditions.person.email"));
		conditions.getCustomerConditions().setPersonLastName(getDefaultFormStringValueByKey(userId, "orderConditionsForm", "customerConditions.person.lastName"));
		conditions.getCustomerConditions().setCompanyInn(getDefaultFormStringValueByKey(userId, "orderConditionsForm", "customerConditions.company.inn"));
		conditions.getCustomerConditions().setCompanyShortName(getDefaultFormStringValueByKey(userId, "orderConditionsForm", "customerConditions.company.shortName"));
		
		Date periodStart = getFormDateValueByKey(userId, "orderConditionsForm", "period.start", DateTimeUtils.truncateDate(new Date()));
		Date periodEnd = getFormDateValueByKey(userId, "orderConditionsForm", "period.end", DateTimeUtils.truncateDate(new Date()));
				
		conditions.getPeriod().setStart(periodStart);
		conditions.getPeriod().setEnd(periodEnd);		

		Set<Object> dirtyStatuses = TextUtils.getStatusesByArray(getFormStringValueByKey(userId, "orderConditionsForm", "statuses", ""), OrderStatuses.class);
		conditions.setDirtyStatuses(dirtyStatuses);
				
		Set<OrderTypes> orderTypes = OrderTypes.getStatusesByArray(getFormStringValueByKey(userId, "orderConditionsForm", "orderTypes", ""));
		conditions.setTypes(orderTypes);

		Set<Object> dirtyDeliveryTypes = TextUtils.getStatusesByArray(getFormStringValueByKey(userId, "orderConditionsForm", "deliveryTypes", ""), DeliveryTypes.class);
		conditions.setDirtyDeliveryTypes(dirtyDeliveryTypes);
		
		Set<Object> dirtyCustomerTypes = TextUtils.getStatusesByArray(getFormStringValueByKey(userId, "orderConditionsForm", "customerTypes", ""), CustomerTypes.class);
		conditions.setDirtyCustomerTypes(dirtyCustomerTypes);
		
		Set<Object> dirtyPaymentTypes = TextUtils.getStatusesByArray(getFormStringValueByKey(userId, "orderConditionsForm", "paymentTypes", ""), PaymentTypes.class);
		conditions.setDirtyPaymentTypes(dirtyPaymentTypes);
		
		Set<Object> dirtyAdvertTypes = TextUtils.getStatusesByArray(getFormStringValueByKey(userId, "orderConditionsForm", "advertTypes", ""), OrderAdvertTypes.class);
		conditions.setDirtyAdvertTypes(dirtyAdvertTypes);
		
							
		return conditions;		
	}
	
	@Override
	public void saveOrderConditions(int userId, OrderConditions conditions) {
		
		saveFormIntegerValue(userId, "orderConditionsForm", "reportPeriodType", conditions.getReportPeriodType().getId());
		
		saveFormBooleanValue(userId, "orderConditionsForm", "periodExist", conditions.isPeriodExist());
		
		saveFormIntegerValue(userId, "orderConditionsForm", "reportPeriodMonth", conditions.getReportPeriodMonth());
		saveFormIntegerValue(userId, "orderConditionsForm", "reportPeriodYear", conditions.getReportPeriodYear());
		
		saveFormStringValue(userId, "orderConditionsForm", "no", conditions.getNo());
		saveFormStringValue(userId, "orderConditionsForm", "trackCode", conditions.getTrackCode());
		saveFormStringValue(userId, "orderConditionsForm", "deliveryAddress", conditions.getDeliveryAddress());
		saveFormIntegerValue(userId, "orderConditionsForm", "product", conditions.getProduct().getId());
				
		saveFormStringValue(userId, "orderConditionsForm", "customerConditions.person.phoneNumber", conditions.getCustomerConditions().getPersonPhoneNumber());
		saveFormStringValue(userId, "orderConditionsForm", "customerConditions.person.email", conditions.getCustomerConditions().getPersonEmail());
		saveFormStringValue(userId, "orderConditionsForm", "customerConditions.person.lastName", conditions.getCustomerConditions().getPersonLastName());
		
		saveFormStringValue(userId, "orderConditionsForm", "customerConditions.company.inn", conditions.getCustomerConditions().getCompanyInn());
		saveFormStringValue(userId, "orderConditionsForm", "customerConditions.company.shortName", conditions.getCustomerConditions().getCompanyShortName());
		
		saveFormDateValue(userId, "orderConditionsForm", "period.start", conditions.getPeriod().getStart());
		saveFormDateValue(userId, "orderConditionsForm", "period.end", conditions.getPeriod().getEnd());	
						
		String strStatuses = TextUtils.getArrayByStatuses(conditions.getDirtyStatuses(), OrderStatuses.class);
		saveFormStringValue(userId, "orderConditionsForm", "statuses", strStatuses);
		
		String strOrderTypes = OrderTypes.getArrayByStatuses(conditions.getTypes());
		saveFormStringValue(userId, "orderConditionsForm", "orderTypes", strOrderTypes);

		String strDeliveryTypes = TextUtils.getArrayByStatuses(conditions.getDirtyDeliveryTypes(), DeliveryTypes.class);
		saveFormStringValue(userId, "orderConditionsForm", "deliveryTypes", strDeliveryTypes);
		
		String strCustomerTypes = TextUtils.getArrayByStatuses(conditions.getDirtyCustomerTypes(), CustomerTypes.class);
		saveFormStringValue(userId, "orderConditionsForm", "customerTypes", strCustomerTypes);	
		
		String strPaymentTypes = TextUtils.getArrayByStatuses(conditions.getDirtyPaymentTypes(), PaymentTypes.class);
		saveFormStringValue(userId, "orderConditionsForm", "paymentTypes", strPaymentTypes);
		
		String strAdvertTypes = TextUtils.getArrayByStatuses(conditions.getDirtyAdvertTypes(), OrderAdvertTypes.class);
		saveFormStringValue(userId, "orderConditionsForm", "advertTypes", strAdvertTypes);	
			
	}
	
	@Override
	public ProductSalesReportConditions loadProductSalesByQueryReportConditions(int userId) {
		Date periodStart = getFormDateValueByKey(userId, "productSalesReportByQueryForm", "period.start", DateTimeUtils.sysDate());
		Date periodEnd = getFormDateValueByKey(userId, "productSalesReportByQueryForm", "period.end", DateTimeUtils.sysDate());
		
		ProductSalesReportConditions conditions = new ProductSalesReportConditions(periodStart, periodEnd);
		int reportPeriodTypeId = getFormIntegerValueByKey(userId, "productSalesReportByQueryForm", "reportPeriodType", ReportPeriodTypes.ANY_PERIOD.getId());
		conditions.setReportPeriodType(ReportPeriodTypes.getValueById(reportPeriodTypeId));
		
		Set<Object> dirtyDeliveryTypes = TextUtils.getStatusesByArray(getFormStringValueByKey(userId, "productSalesReportByQueryForm", "deliveryTypes", ""), DeliveryTypes.class);
		conditions.setDirtyDeliveryTypes(dirtyDeliveryTypes);
		
		Set<Object> dirtyCustomerTypes = TextUtils.getStatusesByArray(getFormStringValueByKey(userId, "productSalesReportByQueryForm", "customerTypes", ""), CustomerTypes.class);
		conditions.setDirtyCustomerTypes(dirtyCustomerTypes);
		
		Set<Object> dirtyPaymentTypes = TextUtils.getStatusesByArray(getFormStringValueByKey(userId, "productSalesReportByQueryForm", "paymentTypes", ""), PaymentTypes.class);
		conditions.setDirtyPaymentTypes(dirtyPaymentTypes);
		
		Set<Object> dirtyAdvertTypes = TextUtils.getStatusesByArray(getFormStringValueByKey(userId, "productSalesReportByQueryForm", "advertTypes", ""), OrderAdvertTypes.class);
		conditions.setDirtyAdvertTypes(dirtyAdvertTypes);
		
			
		return conditions;
	}
	
	@Override
	public void saveProductSalesByQueryReportConditions(int userId, ProductSalesReportConditions conditions) {
		
		saveFormDateValue(userId, "productSalesReportByQueryForm", "period.start", conditions.getPeriod().getStart());
		saveFormDateValue(userId, "productSalesReportByQueryForm", "period.end", conditions.getPeriod().getEnd());	
		
		saveFormIntegerValue(userId, "productSalesReportByQueryForm", "reportPeriodType", conditions.getReportPeriodType().getId());
		
		saveFormIntegerValue(userId, "productSalesReportByQueryForm", "reportPeriodMonth", conditions.getReportPeriodMonth());
		saveFormIntegerValue(userId, "productSalesReportByQueryForm", "reportPeriodYear", conditions.getReportPeriodYear());
		
		String strDeliveryTypes = TextUtils.getArrayByStatuses(conditions.getDirtyDeliveryTypes(), DeliveryTypes.class);
		saveFormStringValue(userId, "productSalesReportByQueryForm", "deliveryTypes", strDeliveryTypes);	
		
		String strCustomerTypes = TextUtils.getArrayByStatuses(conditions.getDirtyCustomerTypes(), CustomerTypes.class);
		saveFormStringValue(userId, "productSalesReportByQueryForm", "customerTypes", strCustomerTypes);	
		
		String strPaymentTypes = TextUtils.getArrayByStatuses(conditions.getDirtyPaymentTypes(), PaymentTypes.class);
		saveFormStringValue(userId, "productSalesReportByQueryForm", "paymentTypes", strPaymentTypes);
		
		String strAdvertTypes = TextUtils.getArrayByStatuses(conditions.getDirtyAdvertTypes(), OrderAdvertTypes.class);
		saveFormStringValue(userId, "productSalesReportByQueryForm", "advertTypes", strAdvertTypes);	
				
	}
}
