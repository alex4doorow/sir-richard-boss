package ru.sir.richard.boss.dao;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import ru.sir.richard.boss.api.market.OzonMarketApi;
import ru.sir.richard.boss.api.market.YandexMarketApi;
import ru.sir.richard.boss.model.data.Address;
import ru.sir.richard.boss.model.data.AnyCustomer;
import ru.sir.richard.boss.model.data.Comment;
import ru.sir.richard.boss.model.data.ForeignerCompanyCustomer;
import ru.sir.richard.boss.model.data.ForeignerCustomer;
import ru.sir.richard.boss.model.data.Order;
import ru.sir.richard.boss.model.data.OrderDelivery;
import ru.sir.richard.boss.model.data.OrderExternalCrm;
import ru.sir.richard.boss.model.data.OrderItem;
import ru.sir.richard.boss.model.data.OrderStatusItem;
import ru.sir.richard.boss.model.data.Person;
import ru.sir.richard.boss.model.data.conditions.ConditionResult;
import ru.sir.richard.boss.model.data.conditions.CustomerConditions;
import ru.sir.richard.boss.model.data.conditions.OrderConditions;
import ru.sir.richard.boss.model.factories.CustomersFactory;
import ru.sir.richard.boss.model.types.AddressTypes;
import ru.sir.richard.boss.model.types.CommentTypes;
import ru.sir.richard.boss.model.types.Countries;
import ru.sir.richard.boss.model.types.CrmStatuses;
import ru.sir.richard.boss.model.types.CrmTypes;
import ru.sir.richard.boss.model.types.CustomerTypes;
import ru.sir.richard.boss.model.types.DeliveryTypes;
import ru.sir.richard.boss.model.types.OrderAdvertTypes;
import ru.sir.richard.boss.model.types.OrderAmountTypes;
import ru.sir.richard.boss.model.types.OrderEmailStatuses;
import ru.sir.richard.boss.model.types.OrderSourceTypes;
import ru.sir.richard.boss.model.types.OrderStatuses;
import ru.sir.richard.boss.model.types.OrderTypes;
import ru.sir.richard.boss.model.types.PaymentDeliveryTypes;
import ru.sir.richard.boss.model.types.PaymentTypes;
import ru.sir.richard.boss.model.types.ReportPeriodTypes;
import ru.sir.richard.boss.model.types.StoreTypes;
import ru.sir.richard.boss.model.utils.DateTimeUtils;
import ru.sir.richard.boss.model.utils.Pair;
import ru.sir.richard.boss.model.utils.TextUtils;

@Repository
public class OrderDaoImpl extends AnyDaoImpl implements OrderDao {
		
	private final Logger logger = LoggerFactory.getLogger(OrderDaoImpl.class);
	
	@Autowired
	private Environment environment;
	
	@Autowired
	private CustomerDao customerDao;
	
	@Autowired
	private WikiDao wikiDao;
	
	@Override
	public List<Order> listOrdersByConditions(OrderConditions orderConditions) {
		logger.debug("listOrdersByConditions():{}", orderConditions);
		
		ConditionResult conditionResult = createSQLQueryListOrdersByConditions(orderConditions);
		final String sqlSelectListOrders = conditionResult.getConditionText();
		
		Object[] conditionPeriod;
		if (conditionResult.isPeriodExist()) {
			conditionPeriod = new Object[]{
					orderConditions.getPeriod().getStart(),
					orderConditions.getPeriod().getEnd()
					};
		} else {
			conditionPeriod = new Object[]{};
		}

		List<Order> orders = this.jdbcTemplate.query(sqlSelectListOrders,
				conditionPeriod,				
				new RowMapper<Order>() {
					@Override
		            public Order mapRow(ResultSet rs, int rowNum) throws SQLException {
		                Order order = new Order();			                
		                order.setId(rs.getInt("ID"));
		                order.setOrderType(OrderTypes.getValueById(rs.getInt("ORDER_TYPE")));
		                order.setPaymentType(PaymentTypes.getValueById(rs.getInt("PAYMENT_TYPE")));
		                order.setSourceType(OrderSourceTypes.getValueById(rs.getInt("SOURCE_TYPE")));
		                order.setAdvertType(OrderAdvertTypes.getValueById(rs.getInt("ADVERT_TYPE")));
		                
		                if (rs.getInt("STORE_ID") == 2) {
		            		order.setStore(StoreTypes.PM);
		            	} else {
		            		order.setStore(StoreTypes.SR);
		            	}
		                
		                order.setNo(rs.getInt("ORDER_NO"));
		                order.setSubNo(rs.getInt("ORDER_SUB_NO"));
		                order.setOrderDate(rs.getTimestamp("ORDER_DATE"));
		                
		                order.setAnnotation(rs.getString("ANNOTATION"));
		                Countries country = Countries.getValueByCode(rs.getString("COUNTRY_ISO_CODE_2"));
		                Address deliveryAddress = new Address(country, AddressTypes.getValueById(rs.getInt("ADDRESS_TYPE")));
		                deliveryAddress.setAddress(rs.getString("ADDRESS"));
		                order.getDelivery().setAddress(deliveryAddress);
		                order.getDelivery().setDeliveryType(DeliveryTypes.getValueById(rs.getInt("DELIVERY_TYPE")));
		                order.getDelivery().setPaymentDeliveryType(PaymentDeliveryTypes.getValueById(rs.getInt("PAYMENT_DELIVERY_TYPE")));		                
		                order.getDelivery().setTrackCode(rs.getString("TRACK_CODE"));	                
		                order.getDelivery().setPrice(rs.getBigDecimal("DELIVERY_PRICE"));
		                
		                order.getDelivery().getAddress().getCarrierInfo().getCourierInfo().setDeliveryDate(rs.getDate("DATE_DELIVERY"));		                
						order.getDelivery().getAddress().getCarrierInfo().getCourierInfo().setStart(DateTimeUtils.timestampToDate(rs.getTimestamp("DELIVERY_TIME_IN")));
						order.getDelivery().getAddress().getCarrierInfo().getCourierInfo().setEnd(DateTimeUtils.timestampToDate(rs.getTimestamp("DELIVERY_TIME_OUT")));
		                
		                order.setProductCategory(wikiDao.getCategoryById(rs.getInt("CATEGORY_PRODUCT_ID")));
		                order.getAmounts().setValue(OrderAmountTypes.TOTAL, rs.getBigDecimal("AMOUNT_TOTAL"));
		                order.getAmounts().setValue(OrderAmountTypes.TOTAL_WITH_DELIVERY, rs.getBigDecimal("AMOUNT_TOTAL_WITH_DELIVERY"));
		                order.getAmounts().setValue(OrderAmountTypes.BILL, rs.getBigDecimal("AMOUNT_BILL"));
		                order.getAmounts().setValue(OrderAmountTypes.SUPPLIER, rs.getBigDecimal("AMOUNT_SUPPLIER"));
		                order.getAmounts().setValue(OrderAmountTypes.MARGIN, rs.getBigDecimal("AMOUNT_MARGIN"));
		                order.getAmounts().setValue(OrderAmountTypes.POSTPAY, rs.getBigDecimal("AMOUNT_POSTPAY"));
		                
		                order.setStatus(OrderStatuses.getValueById(rs.getInt("STATUS")));
		                order.setEmailStatus(OrderEmailStatuses.getValueById(rs.getInt("STATUS_EMAIL")));
		                
		                order.getOffer().setCountDay(rs.getInt("OFFER_COUNT_DAY"));
		                order.getOffer().setStartDate(rs.getTimestamp("OFFER_DATE_START"));
		                		                
		                CustomerTypes customerType = CustomerTypes.getValueById(rs.getInt("CUSTOMER_TYPE"));
		                AnyCustomer customer = CustomersFactory.createCustomer(customerType);
						customer.setId(rs.getInt("ID"));
						customer.setCountry(country);
						if (customer.isPerson()) {
							ForeignerCustomer personCustomer = (ForeignerCustomer) customer;
							personCustomer.setPersonId(rs.getInt("PERSON_ID"));
							personCustomer.setFirstName(rs.getString("SHORT_NAME"));
							personCustomer.setLastName(rs.getString("LONG_NAME"));
							personCustomer.setPhoneNumber(rs.getString("PHONE_NUMBER"));							
						} else {
							ForeignerCompanyCustomer companyCustomer = (ForeignerCompanyCustomer) customer;
							companyCustomer.setShortName(rs.getString("SHORT_NAME").toUpperCase());
							companyCustomer.setLongName(rs.getString("LONG_NAME").toUpperCase());
							companyCustomer.setInn(rs.getString("INN"));							
							companyCustomer.getMainContact().setPhoneNumber(rs.getString("PHONE_NUMBER"));
							companyCustomer.getMainContact().setEmail(rs.getString("EMAIL"));							
						}		                
		                order.setCustomer(customer);
		                
		                return order;
		            }
		        });
		return orders;			
	}
	
	@Override
	public List<Order> listOrdersForFeedback(Date dateStart) {
		final int COUNT_DAY_TIMEOUT = 10;
		
		logger.debug("listOrdersForFeedback():{}", dateStart);

		final String sqlSelectListOrders = "SELECT * FROM sr_v_order o "
				+ "  WHERE (o.customer_type in (1)) AND "
				+ "        (status in (8, 10)) AND "
				+ "        (status_email = 0) AND "
				+ "        ((email is not null) and trim(email) <> '')"		
		        + " ORDER BY order_no";		
		logger.debug("createSQLQueryListOrdersForFeedback: {}", sqlSelectListOrders);
 
		Date dateBeforeStart = null;		
		dateBeforeStart = DateTimeUtils.beforeAnyDate(dateStart, COUNT_DAY_TIMEOUT);		
		Object[] conditionPeriod;		
		conditionPeriod = new Object[]{
					};	
		List<Order> beforeOrders = this.jdbcTemplate.query(sqlSelectListOrders,
				conditionPeriod,				
				new RowMapper<Order>() {
					@Override
		            public Order mapRow(ResultSet rs, int rowNum) throws SQLException {
		                Order order = new Order();			                
		                order.setId(rs.getInt("ID"));
		                order.setOrderType(OrderTypes.getValueById(rs.getInt("ORDER_TYPE")));
		                order.setPaymentType(PaymentTypes.getValueById(rs.getInt("PAYMENT_TYPE")));
		                order.setSourceType(OrderSourceTypes.getValueById(rs.getInt("SOURCE_TYPE")));
		                order.setAdvertType(OrderAdvertTypes.getValueById(rs.getInt("ADVERT_TYPE")));		                		                
		                order.setNo(rs.getInt("ORDER_NO"));
		                order.setSubNo(rs.getInt("ORDER_SUB_NO"));
		                order.setOrderDate(rs.getTimestamp("ORDER_DATE"));		                
		                order.setAnnotation(rs.getString("ANNOTATION"));		                
		                order.setStatus(OrderStatuses.getValueById(rs.getInt("STATUS")));
		                order.setEmailStatus(OrderEmailStatuses.getValueById(rs.getInt("STATUS_EMAIL")));		                
		                CustomerTypes customerType = CustomerTypes.getValueById(rs.getInt("CUSTOMER_TYPE"));
		                AnyCustomer customer = CustomersFactory.createCustomer(customerType);
						customer.setId(rs.getInt("ID"));					
						ForeignerCustomer personCustomer = (ForeignerCustomer) customer;
						personCustomer.setPersonId(rs.getInt("PERSON_ID"));
						personCustomer.setFirstName(rs.getString("SHORT_NAME"));
						personCustomer.setLastName(rs.getString("LONG_NAME"));
						personCustomer.setPhoneNumber(rs.getString("PHONE_NUMBER"));
						personCustomer.setEmail(rs.getString("EMAIL"));							                
		                order.setCustomer(customer);		                
		                return order;
		            }
		        });
		
		List<Order> orders = new ArrayList<Order>();
		for (Order order : beforeOrders) {
			if (StringUtils.isEmpty(order.getCustomer().getEmail())) {
				continue;
			}
			
			final String sqlSelectCountOrderCustomer = "SELECT COUNT(*) COUNT_ID FROM sr_v_order "
					+ " where customer_id = ?"
					+ "   and status in (10, 8)";
			int countOrderCustomer = this.jdbcTemplate.queryForObject(sqlSelectCountOrderCustomer,
			        new Object[]{order.getCustomer().getId()},
			        new RowMapper<Integer>() {
						@Override
			            public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
			            	return rs.getInt("COUNT_ID");	
			            }
			        });
			
			if (countOrderCustomer > 1) {
				// проверка на повторное обращение к клиенту за отзывом
				continue;
			}			

			final String sqlSelectMinOrderDate = "SELECT MIN(date_added) MIN_DATE_ADDED FROM sr_order_status"
					+ "  WHERE order_id = ? AND status IN (8, 10)";
			java.sql.Date minDateAdded = this.jdbcTemplate.queryForObject(sqlSelectMinOrderDate,
			        new Object[]{order.getId()},
			        new RowMapper<java.sql.Date>() {
						@Override
			            public java.sql.Date mapRow(ResultSet rs, int rowNum) throws SQLException {
			            	return rs.getDate("MIN_DATE_ADDED");	
			            }
			        });
			
			if (DateTimeUtils.truncateDate(minDateAdded).compareTo(dateBeforeStart) < 0) {
				// заказ получен клиентом десять дней назад - отправим емейл на запрос
				orders.add(order);
			}			
		}			
		return orders;			
	}	
	
	@Override
	public List<Order> listBidExpired(Date dateStart) {
		logger.debug("listBidExpired():{}", dateStart);
		
		final String sqlSelectListOrders = "SELECT * FROM sr_v_order o "
				+ "  WHERE (o.order_type in (2, 3)) AND "
				+ "        (o.status in (1)) AND "
				+ "        (o.offer_count_day > 0)"		
		        + " ORDER BY order_no";		
		logger.debug("createSQLQueryListOrdersForBidExpired: {}", sqlSelectListOrders);
			
		
		Object[] conditionPeriod;		
		conditionPeriod = new Object[]{
					};	
		List<Order> beforeOrders = this.jdbcTemplate.query(sqlSelectListOrders,
				conditionPeriod,				
				new RowMapper<Order>() {
					@Override
		            public Order mapRow(ResultSet rs, int rowNum) throws SQLException {
		                Order order = new Order();			                
		                order.setId(rs.getInt("ID"));
		                order.setOrderType(OrderTypes.getValueById(rs.getInt("ORDER_TYPE")));
		                order.setPaymentType(PaymentTypes.getValueById(rs.getInt("PAYMENT_TYPE")));
		                order.setSourceType(OrderSourceTypes.getValueById(rs.getInt("SOURCE_TYPE")));
		                order.setAdvertType(OrderAdvertTypes.getValueById(rs.getInt("ADVERT_TYPE")));		                		                
		                order.setNo(rs.getInt("ORDER_NO"));
		                order.setSubNo(rs.getInt("ORDER_SUB_NO"));
		                order.setOrderDate(rs.getTimestamp("ORDER_DATE"));		                
		                order.setAnnotation(rs.getString("ANNOTATION"));		                
		                order.setStatus(OrderStatuses.getValueById(rs.getInt("STATUS")));
		                order.setEmailStatus(OrderEmailStatuses.getValueById(rs.getInt("STATUS_EMAIL")));
		                
		                order.getOffer().setCountDay(rs.getInt("OFFER_COUNT_DAY"));
		                order.getOffer().setStartDate(rs.getTimestamp("OFFER_DATE_START"));
		            
		                AnyCustomer customer = customerDao.findById(rs.getInt("CUSTOMER_ID"));
		                order.setCustomer(customer);		                	                
		                return order;
		            }
		        });
		
		List<Order> orders = new ArrayList<Order>();
		for (Order order : beforeOrders) {
			if (StringUtils.isEmpty(order.getCustomer().getEmail())) {
				continue;
			}
			//int countDay = order.getOffer().getCountDay();
			Date expiredDay = order.getOffer().getExpiredDate();
			
			if (DateTimeUtils.truncateDate(expiredDay).compareTo(DateTimeUtils.truncateDate(dateStart)) < 0) {
				// счет протух
				orders.add(order);
			}
		}			
		return orders;
	}	
	
	/**
	 * заказы за сегодня
	 * @param dateStart
	 * @return
	 */
	@Override
	public List<Order> listYeildOrders(Pair<Date> dates) {
				
		logger.debug("listYeildOrders():{}", dates.getStart());
		
		final String sqlSelectListOrders = "SELECT * FROM sr_v_order o "
				+ "  WHERE (o.order_type in (1, 2)) AND "
				+ "        (o.status in (2, 3, 4, 5, 7, 12, 10, 8)) AND "
				+ "        (o.order_date between ? and ?)"		
		        + " ORDER BY order_no";		
		logger.debug("createSQLQuerylistYeildOrders: {}", sqlSelectListOrders);
					
		Object[] conditionPeriod;		
		conditionPeriod = new Object[]{
				dates.getStart(), 
				dates.getEnd()
					};	
		List<Order> beforeOrders = this.jdbcTemplate.query(sqlSelectListOrders,
				conditionPeriod,				
				new RowMapper<Order>() {
					@Override
		            public Order mapRow(ResultSet rs, int rowNum) throws SQLException {
		                Order order = new Order();			                
		                order.setId(rs.getInt("ID"));
		                order.setOrderType(OrderTypes.getValueById(rs.getInt("ORDER_TYPE")));
		                order.setPaymentType(PaymentTypes.getValueById(rs.getInt("PAYMENT_TYPE")));
		                order.setSourceType(OrderSourceTypes.getValueById(rs.getInt("SOURCE_TYPE")));
		                order.setAdvertType(OrderAdvertTypes.getValueById(rs.getInt("ADVERT_TYPE")));		                		                
		                order.setNo(rs.getInt("ORDER_NO"));
		                order.setSubNo(rs.getInt("ORDER_SUB_NO"));
		                order.setOrderDate(rs.getTimestamp("ORDER_DATE"));
		                order.setStatus(OrderStatuses.getValueById(rs.getInt("STATUS")));
		                
		                order.getAmounts().setValue(OrderAmountTypes.TOTAL, rs.getBigDecimal("AMOUNT_TOTAL"));
		                order.getAmounts().setValue(OrderAmountTypes.TOTAL_WITH_DELIVERY, rs.getBigDecimal("AMOUNT_TOTAL_WITH_DELIVERY"));
		                order.getAmounts().setValue(OrderAmountTypes.BILL, rs.getBigDecimal("AMOUNT_BILL"));
		                order.getAmounts().setValue(OrderAmountTypes.SUPPLIER, rs.getBigDecimal("AMOUNT_SUPPLIER"));
		                order.getAmounts().setValue(OrderAmountTypes.MARGIN, rs.getBigDecimal("AMOUNT_MARGIN"));
		                order.getAmounts().setValue(OrderAmountTypes.POSTPAY, rs.getBigDecimal("AMOUNT_POSTPAY"));
		                		                          
		                return order;
		            }
		        });
			
		return beforeOrders;
	}	

	@Override
	public Map<OrderAmountTypes, BigDecimal> calcTotalOrdersAmountsByConditions(OrderConditions orderConditions) {
		
		List<Order> orders = listOrdersByConditions(orderConditions);
		Map<OrderAmountTypes, BigDecimal> result = calcTotalOrdersAmountsByConditions(orders, orderConditions.getPeriod());		
		return result;
	}
	
	@Override
	public Map<OrderAmountTypes, BigDecimal> calcTotalOrdersAmountsByConditions(List<Order> orders, Pair<Date> period) {
		Map<OrderAmountTypes, BigDecimal> results = new HashMap<OrderAmountTypes, BigDecimal>();		

		BigDecimal billAmount = BigDecimal.ZERO;
		BigDecimal supplierAmount = BigDecimal.ZERO;
		BigDecimal marginAmount = BigDecimal.ZERO;
		BigDecimal marginWithoutAdvertAmount = BigDecimal.ZERO;
		//BigDecimal postpayAmount = BigDecimal.ZERO;
		BigDecimal approvedConversion = BigDecimal.ZERO;
		BigDecimal bidConversion = BigDecimal.ZERO;
		int realOrdersCount = 0;
		for (Order order : orders) {			
			if (order.isBillAmount()) {
				realOrdersCount++;				
				billAmount = billAmount.add(order.getAmounts().getBill());
				supplierAmount = supplierAmount.add(order.getAmounts().getSupplier());
				marginAmount = marginAmount.add(order.getAmounts().getMargin());
			}			
		}
		
		BigDecimal advertAmount = wikiDao.ejectTotalAmountsByConditions(OrderAmountTypes.ADVERT_BUDGET, period);
		BigDecimal clickCount = wikiDao.ejectTotalAmountsByConditions(OrderAmountTypes.COUNT_VISITS, period);
		
		if (clickCount == null || clickCount.equals(BigDecimal.ZERO)) {
			approvedConversion = BigDecimal.ZERO;
			bidConversion = BigDecimal.ZERO;
			
		} else {
			approvedConversion = BigDecimal.valueOf(realOrdersCount).divide(clickCount, 4, RoundingMode.HALF_UP);
			bidConversion = BigDecimal.valueOf(orders.size()).divide(clickCount, 4, RoundingMode.HALF_UP);
		}		
		marginWithoutAdvertAmount = marginAmount.subtract(advertAmount);
		results.put(OrderAmountTypes.BILL, billAmount);
		results.put(OrderAmountTypes.SUPPLIER, supplierAmount);
		results.put(OrderAmountTypes.MARGIN, marginWithoutAdvertAmount);
		Map<OrderAmountTypes, BigDecimal> postpayAmounts = calcTotalOrdersPostpayAmountByConditions(); 
		
		results.put(OrderAmountTypes.POSTPAY, postpayAmounts.get(OrderAmountTypes.POSTPAY));
		results.put(OrderAmountTypes.POSTPAY_SDEK, postpayAmounts.get(OrderAmountTypes.POSTPAY_SDEK));
		results.put(OrderAmountTypes.POSTPAY_POST, postpayAmounts.get(OrderAmountTypes.POSTPAY_POST));
		results.put(OrderAmountTypes.POSTPAY_COMPANY, postpayAmounts.get(OrderAmountTypes.POSTPAY_COMPANY));
		results.put(OrderAmountTypes.POSTPAY_YANDEX_MARKET, postpayAmounts.get(OrderAmountTypes.POSTPAY_YANDEX_MARKET));
		results.put(OrderAmountTypes.POSTPAY_OZON_MARKET, postpayAmounts.get(OrderAmountTypes.POSTPAY_OZON_MARKET));
		results.put(OrderAmountTypes.POSTPAY_OZON_ROCKET, postpayAmounts.get(OrderAmountTypes.POSTPAY_OZON_ROCKET));
				
		results.put(OrderAmountTypes.ADVERT_BUDGET, advertAmount);
		results.put(OrderAmountTypes.COUNT_REAL_ORDERS, BigDecimal.valueOf(realOrdersCount));
		results.put(OrderAmountTypes.CONVERSION_APPROVED, approvedConversion);
		results.put(OrderAmountTypes.CONVERSION_BID, bidConversion);				
		return results;
	}
	
	private  Map<OrderAmountTypes, BigDecimal> calcTotalOrdersPostpayAmountByConditions() {
	
		final String sqlSelectMinOrderDate = "SELECT MIN(order_date) MIN_ORDER_DATE FROM sr_order"
				+ "  WHERE amount_postpay > 0 "
				+ "    AND status NOT IN (0, 1, 21, 22, 2, 4, 8, 13, 15, 16, 11)";
		java.sql.Date minOrderDate = this.jdbcTemplate.queryForObject(sqlSelectMinOrderDate,
		        new Object[]{},
		        new RowMapper<java.sql.Date>() {
					@Override
		            public java.sql.Date mapRow(ResultSet rs, int rowNum) throws SQLException {
		            	return rs.getDate("MIN_ORDER_DATE");	
		            }
		        });
		final Map<OrderAmountTypes, BigDecimal> postpayAmounts = new HashMap<OrderAmountTypes, BigDecimal>();	
		BigDecimal postpayAmount = BigDecimal.ZERO;
		BigDecimal sDekPostpayAmount = BigDecimal.ZERO;
		BigDecimal postPostpayAmount = BigDecimal.ZERO;		
		BigDecimal companyPostpayAmount = BigDecimal.ZERO;
		BigDecimal yandexMarketPostpayAmount = BigDecimal.ZERO;
		BigDecimal ozonMarketPostpayAmount = BigDecimal.ZERO;
		BigDecimal ozonRocketPostpayAmount = BigDecimal.ZERO;		
		
		Pair<Date> postpayPeriod;
		try {
			// select * from sr_order where amount_postpay > 0 and status not in (1, 4, 0, 8, 13, 15, 21)
			postpayPeriod = new Pair<Date>(new Date(minOrderDate.getTime()),
					//DateTimeUtils.defaultFormatStringToDate("15.04.2018"), 
					DateTimeUtils.lastDayOfMonth(DateTimeUtils.sysDate()));
		} catch (Exception e) {	
			postpayPeriod = new Pair<Date>();
			logger.error("Exception:", e);
		}

		OrderConditions orderPostpayConditions = new OrderConditions();
		orderPostpayConditions.setPeriod(postpayPeriod);		
		List<Order> orders = listOrdersByConditions(orderPostpayConditions);		
		for (Order order : orders) {
			if (order.isPostpayAmount()) {
				
				postpayAmount = postpayAmount.add(order.getAmounts().getPostpay());
				
				if (order.getAdvertType() == OrderAdvertTypes.YANDEX_MARKET) {
					yandexMarketPostpayAmount = yandexMarketPostpayAmount.add(order.getAmounts().getPostpay());					
				} else if (order.getAdvertType() == OrderAdvertTypes.OZON) {
					ozonMarketPostpayAmount = ozonMarketPostpayAmount.add(order.getAmounts().getPostpay());					
				} else if (order.getCustomer().isPerson() && (order.getDelivery().getDeliveryType().isOzonRocket())) {
					ozonRocketPostpayAmount = ozonRocketPostpayAmount.add(order.getAmounts().getPostpay());					
				} else if (order.getCustomer().isPerson() && (order.getDelivery().getDeliveryType().isСdek() || order.getDelivery().getDeliveryType() == DeliveryTypes.PICKUP)) {
					sDekPostpayAmount = sDekPostpayAmount.add(order.getAmounts().getPostpay());
				} else if (order.getCustomer().isPerson() && order.getDelivery().getDeliveryType().isPost()) {
					postPostpayAmount = postPostpayAmount.add(order.getAmounts().getPostpay());
				} else if (order.getCustomer().isCompany()) {
					companyPostpayAmount = companyPostpayAmount.add(order.getAmounts().getPostpay());					
				} else {
					sDekPostpayAmount = sDekPostpayAmount.add(order.getAmounts().getPostpay());					
				}
				logger.debug("postpay: {}, {}, {}, [sdek:{}, post:{}, company:{}, ym:{}, ozon.market:{}, ozon.rocket:{}]", order.getNo(), order.getCustomer().getViewShortName(), order.getAmounts().getPostpay(),
						sDekPostpayAmount, postPostpayAmount, companyPostpayAmount, 
						yandexMarketPostpayAmount, ozonMarketPostpayAmount, ozonRocketPostpayAmount);									
			}
		}
		postpayAmounts.put(OrderAmountTypes.POSTPAY, postpayAmount);
		postpayAmounts.put(OrderAmountTypes.POSTPAY_SDEK, sDekPostpayAmount);
		postpayAmounts.put(OrderAmountTypes.POSTPAY_POST, postPostpayAmount);
		postpayAmounts.put(OrderAmountTypes.POSTPAY_COMPANY, companyPostpayAmount);
		postpayAmounts.put(OrderAmountTypes.POSTPAY_YANDEX_MARKET, yandexMarketPostpayAmount);
		postpayAmounts.put(OrderAmountTypes.POSTPAY_OZON_MARKET, ozonMarketPostpayAmount);
		postpayAmounts.put(OrderAmountTypes.POSTPAY_OZON_ROCKET, ozonRocketPostpayAmount);
				
		return postpayAmounts;
	}

	@Override
	public List<Order> listOrders() {		
		OrderConditions orderConditions = new OrderConditions();
		orderConditions.setPeriodByType(ReportPeriodTypes.CURRENT_MONTH, DateTimeUtils.sysDate());		
		return listOrdersByConditions(orderConditions);
	}
	
	public List<Order> listTroubleOrders() {
		
		final int COUNT_DAY_TROUBLE_READY_GIVE_AWAY = 5;
		final int COUNT_DAY_TROUBLE_DELIVERING = 20;
		final int COUNT_DAY_TROUBLE_POST_DELIVERED = 3;
		final int COUNT_DAY_TROUBLE_CDEK_DELIVERED = 25;
		final int COUNT_DAY_TROUBLE_REDELIVERY = 8;
		
		List<Order> resultOrders = new ArrayList<Order>();
		
		// заказ, постоплата, прибыл, сдэк
		final String sqlSelectListOrders1 = "SELECT id from sr_v_order"
						+ " WHERE order_type = 1 "
						+ "   AND payment_type = 2 "
						+ "   AND status = 7 "
						+ "   AND delivery_type in (101, 102, 103, 104)"
						+ " ORDER BY id";
		resultOrders = listTroubleOrdersByQuery(resultOrders, sqlSelectListOrders1, COUNT_DAY_TROUBLE_READY_GIVE_AWAY, true);
		
		// заказ, постоплата, прибыл, почта
		final String sqlSelectListOrders11 = "SELECT id from sr_v_order"
				+ " WHERE order_type = 1 "
				+ "   AND payment_type = 2 "
				+ "   AND status = 7 "
				+ "   AND delivery_type in (401, 402, 405)"
				+ " ORDER BY id";
		resultOrders = listTroubleOrdersByQuery(resultOrders, sqlSelectListOrders11, COUNT_DAY_TROUBLE_READY_GIVE_AWAY, true);
		
		// заказ, постоплата, доставляется, почта
		final String sqlSelectListOrders21 = "SELECT id from sr_v_order"
						+ " WHERE order_type = 1 "
						+ "   AND payment_type = 2 "
						+ "   AND status = 5 "
						+ "   AND delivery_type in (401, 402, 405)"
						+ " ORDER BY id";
		resultOrders = listTroubleOrdersByQuery(resultOrders, sqlSelectListOrders21, COUNT_DAY_TROUBLE_DELIVERING, true);
		
		// заказ, постоплата, доставляется, сдэк, москва
		final String sqlSelectListOrdersMoscow22 = "SELECT id from sr_v_order"
						+ " WHERE order_type = 1 "
						+ "   AND payment_type = 2 "
						+ "   AND status = 5 "
						+ "   AND delivery_type in (101, 102, 103, 104)"
						+ "   AND city like \"москва\""
						+ " ORDER BY id";
		resultOrders = listTroubleOrdersByQuery(resultOrders, sqlSelectListOrdersMoscow22, COUNT_DAY_TROUBLE_READY_GIVE_AWAY, true);
				
		// заказ, постоплата, отказ от вручения, сдэк, москва
		final String sqlSelectListOrdersMoscow23 = "SELECT id from sr_v_order"
						+ " WHERE order_type = 1 "
						+ "   AND payment_type = 2 "
						+ "   AND status = 9 "
						+ "   AND delivery_type in (101, 102, 103, 104)"
						+ "   AND city like \"%москва%\""
						+ " ORDER BY id";
		resultOrders = listTroubleOrdersByQuery(resultOrders, sqlSelectListOrdersMoscow23, COUNT_DAY_TROUBLE_REDELIVERY, true);
								
		// заказ, постоплата, получены, почта
		final String sqlSelectListOrders3 = "SELECT id from sr_v_order"
						+ " WHERE order_type = 1 "
						+ "   AND payment_type = 2 "
						+ "   AND status = 10 "
						+ "   AND delivery_type in (401, 402, 405)"
						+ " ORDER BY id";
		resultOrders = listTroubleOrdersByQuery(resultOrders, sqlSelectListOrders3, COUNT_DAY_TROUBLE_POST_DELIVERED, true);

		// заказ, постоплата, получены, сдэк
		final String sqlSelectListOrders4 = "SELECT id from sr_v_order"
						+ " WHERE order_type = 1 "
						+ "   AND payment_type = 2 "
						+ "   AND status = 10 "
						+ "   AND delivery_type in (101, 102, 103, 104)"
						+ " ORDER BY id";
		resultOrders = listTroubleOrdersByQuery(resultOrders, sqlSelectListOrders4, COUNT_DAY_TROUBLE_CDEK_DELIVERED, true);
		
		// заказ, постоплата, заканчивается срок хранения, сдэк
		final String sqlSelectListOrders5 = "SELECT id from sr_v_order"
						+ " WHERE order_type = 1 "
						+ "   AND payment_type = 2 "
						+ "   AND status = 12 "
						+ "   AND delivery_type in (101, 102, 103, 104)"
						+ " ORDER BY id";
		resultOrders = listTroubleOrdersByQuery(resultOrders, sqlSelectListOrders5, -1, false);
		
		// заказ, постоплата, заканчивается срок хранения, почта
		final String sqlSelectListOrders51 = "SELECT id from sr_v_order"
				+ " WHERE order_type = 1 "
				+ "   AND payment_type = 2 "
				+ "   AND status = 12 "
				+ "   AND delivery_type in (401, 402, 405)"
				+ " ORDER BY id";
		resultOrders = listTroubleOrdersByQuery(resultOrders, sqlSelectListOrders51, -1, false);		
		
		// заказ, постоплата, заканчивается срок хранения, самовывоз
		final String sqlSelectListOrders52 = "SELECT id from sr_v_order"
				+ " WHERE order_type = 1 "
				+ "   AND payment_type = 2 "
				+ "   AND status in (7, 12) "
				+ "   AND delivery_type in (403)"
				+ " ORDER BY id";		
		resultOrders = listTroubleOrdersByQuery(resultOrders, sqlSelectListOrders52, COUNT_DAY_TROUBLE_READY_GIVE_AWAY, false);
				

		
		// КП и счета, заявки - протухает срок
		final String sqlSelectListOrders6 = "SELECT id from sr_v_order"
				+ " WHERE order_type in (2,3)"
				+ "   AND offer_count_day > 0"
				+ "   AND status in (1) "
				+ " ORDER BY id";
		List<Order> tmpOrders = new ArrayList<Order>();
		tmpOrders = listTroubleOrdersByQuery(tmpOrders, sqlSelectListOrders6, -1, false);		
		for (Order tmpOrder : tmpOrders) {			
			if (tmpOrder.getOffer().getExpiredDate().compareTo(DateTimeUtils.sysDate()) <= 0) {
				resultOrders.add(tmpOrder);
			}
		}		
		Collections.sort(resultOrders, new SortOrderByNo());		
		return resultOrders;	
	}
	
	private class SortOrderByNo implements Comparator<Order> { 
	    // Used for sorting in ascending order of 
	    // roll number 
	    public int compare(Order a, Order b) { 
	        return a.getNo() - b.getNo(); 
	    } 
	} 
	
	private List<Order> listTroubleOrdersByQuery(List<Order> resultOrders, final String sqlSelectListOrders, final int COUNT_DAY_TROUBLE, boolean isPeriod) {
		
		List<Order> orders = this.jdbcTemplate.query(sqlSelectListOrders,		
				new RowMapper<Order>() {
					@Override
		            public Order mapRow(ResultSet rs, int rowNum) throws SQLException {						
						int orderId = rs.getInt("ID");
						Order order = new Order();
						order.setId(orderId);	
						return order;
		            }
		        });	
		for (Order order : orders) {			
			OrderStatusItem currentStatus = getCurrentStatus(order.getId());
			if (currentStatus != null) {
				if (isPeriod) {
					Date firstTroubleDate = DateTimeUtils.afterAnyDate(DateTimeUtils.truncateDate(currentStatus.getAddedDate()), COUNT_DAY_TROUBLE);
					if (firstTroubleDate.before(DateTimeUtils.truncateDate(DateTimeUtils.sysDate()))) {
						Order resultOrder = findById(order.getId());
						resultOrders.add(resultOrder);			
					}					
				} else {
					Order resultOrder = findById(order.getId());
					resultOrders.add(resultOrder);
				}	
			}
		}
		return resultOrders;		
	}
	
	private OrderStatusItem getCurrentStatus(int orderId) {
		
		final String sqlSelectOrderStatusItems = "SELECT *"
		        + "  FROM sr_order_status os"
		        + "  WHERE os.order_id = ? AND id = (select MAX(id) FROM sr_order_status WHERE order_id = ?)"
		        + "  ORDER BY id";			
		List<OrderStatusItem> orderStatusItems = this.jdbcTemplate.query(sqlSelectOrderStatusItems,
				new Object[]{orderId, orderId},
		        new RowMapper<OrderStatusItem>() {
					@Override
		            public OrderStatusItem mapRow(ResultSet rs, int rowNum) throws SQLException {						
						OrderStatusItem item = new OrderStatusItem();
						item.setId(rs.getInt("ID"));
						item.setStatus(OrderStatuses.getValueById(rs.getInt("STATUS")));
						item.setAddedDate(DateTimeUtils.timestampToDate(rs.getTimestamp("DATE_ADDED")));						
		                return item;
		            }		            
		        });
		
		if (orderStatusItems == null || orderStatusItems.size() == 0) {
			return null;
		}
		return orderStatusItems.get(0);		
	}
	
	@Override
	public ConditionResult createSQLQueryListOrdersByConditions(OrderConditions orderConditions) {

		ConditionResult conditionResult = new ConditionResult();
		
		String result = "SELECT * FROM sr_v_order o WHERE ";		
		boolean isPeriodCondition = orderConditions.isPeriodExist();
		boolean isDiscretCondition = false;
		if (StringUtils.isNotEmpty(orderConditions.getCustomerConditions().getPersonPhoneNumber())) {			
			result += "(phone_number like \"%" + orderConditions.getCustomerConditions().getPersonPhoneNumber() + "%\")";
			isPeriodCondition = false;
			isDiscretCondition = true;
		} 
		if (StringUtils.isNotEmpty(orderConditions.getCustomerConditions().getPersonEmail())) {
			if (isDiscretCondition) {
				result += " AND ";
			}
			result += "(email like \"" + orderConditions.getCustomerConditions().getPersonEmail() + "%\")";
			isPeriodCondition = false;
			isDiscretCondition = true;
		}
		if (StringUtils.isNotEmpty(orderConditions.getCustomerConditions().getPersonLastName())) {
			if (isDiscretCondition) {
				result += " AND ";
			}
			result += "(UPPER(person_last_name) like UPPER(\"%" + orderConditions.getCustomerConditions().getPersonLastName().trim() + "%\"))";
			isDiscretCondition = true;
		} 
		if (StringUtils.isNotEmpty(orderConditions.getTrackCode())) {
			if (isDiscretCondition) {
				result += " AND ";
			}
			result += "(track_code = \"" + orderConditions.getTrackCode() + "\")";
			isPeriodCondition = false;
			isDiscretCondition = true;
		}
		
		if (StringUtils.isNotEmpty(orderConditions.getCrmNo())) {
			if (isDiscretCondition) {
				result += " AND ";
			}
			result += "((id in (select order_id from sr_order_crm_connect where parent_crm_id = \"" + orderConditions.getCrmNo() + "\")) OR (id in (select order_id from sr_order_crm_connect where parent_crm_code = \"" + orderConditions.getCrmNo() + "\")))";
			isPeriodCondition = false;
			isDiscretCondition = true;
		}
		
		if (StringUtils.isNotEmpty(orderConditions.getDeliveryAddress())) {
			if (isDiscretCondition) {
				result += " AND ";
			}
			result += "(UPPER(address) like UPPER(\"%" + orderConditions.getDeliveryAddress().trim() + "%\"))";
			isDiscretCondition = true;
		}	
		if (orderConditions.getCustomerId() > 0) {
			if (isDiscretCondition) {
				result += " AND ";
			}
			result += "(customer_id = " + orderConditions.getCustomerId() + ")";
			isPeriodCondition = false;
			isDiscretCondition = true;
		}		
		if (StringUtils.isNotEmpty(orderConditions.getCustomerConditions().getCompanyInn())) {
			if (isDiscretCondition) {
				result += " AND ";
			}
			result += "(inn = \"" + orderConditions.getCustomerConditions().getCompanyInn().trim() + "\")";
			isDiscretCondition = true;
		}
		if (StringUtils.isNotEmpty(orderConditions.getCustomerConditions().getCompanyMainContactEmail())) {
			if (isDiscretCondition) {
				result += " AND ";
			}
			result += "(email like \"" + orderConditions.getCustomerConditions().getCompanyMainContactEmail() + "%\")";
			isPeriodCondition = false;
			isDiscretCondition = true;
		}
		if (StringUtils.isNotEmpty(orderConditions.getCustomerConditions().getCompanyMainContactPhoneNumber())) {
			if (isDiscretCondition) {
				result += " AND ";
			}
			result += "(phone_number like \"%" + orderConditions.getCustomerConditions().getCompanyMainContactPhoneNumber() + "%\")";
			isPeriodCondition = false;
			isDiscretCondition = true;
		}		
		if (StringUtils.isNotEmpty(orderConditions.getNo())) {
			if (isDiscretCondition) {
				result += " AND ";
			}
			result += "(order_no = \"" + orderConditions.getNo() + "\")";
			isPeriodCondition = false;
			isDiscretCondition = true;
		} 		
		if (StringUtils.isNotEmpty(orderConditions.getCustomerConditions().getCompanyShortName())) {
			if (isDiscretCondition) {
				result += " AND ";
			}
			result += "(UPPER(short_name) like UPPER(\"%" + TextUtils.escapingQuotes(orderConditions.getCustomerConditions().getCompanyShortName().trim()) + "%\"))";
			isDiscretCondition = true;
		} 
		if (StringUtils.isNotEmpty(orderConditions.getIdsTypes())) {			
			if (isDiscretCondition) {
				result += " AND ";
			}
			result += "(order_type in (" + orderConditions.getIdsTypes() + "))";
			isDiscretCondition = true;
		}
		if (StringUtils.isNotEmpty(orderConditions.getIdsDeliveryTypes())) {			
			if (isDiscretCondition) {
				result += " AND ";
			}
			result += "(delivery_type in (" + orderConditions.getIdsDeliveryTypes() + "))";
			isDiscretCondition = true;
		}
		if (StringUtils.isNotEmpty(orderConditions.getIdsCustomerTypes())) {			
			if (isDiscretCondition) {
				result += " AND ";
			}
			result += "(customer_type in (" + orderConditions.getIdsCustomerTypes() + "))";
			isDiscretCondition = true;
		}
		if (StringUtils.isNotEmpty(orderConditions.getIdsPaymentTypes())) {			
			if (isDiscretCondition) {
				result += " AND ";
			}
			result += "(payment_type in (" + orderConditions.getIdsPaymentTypes() + "))";
			isDiscretCondition = true;
		}
		if (StringUtils.isNotEmpty(orderConditions.getIdsAdvertTypes())) {			
			if (isDiscretCondition) {
				result += " AND ";
			}
			result += "(advert_type in (" + orderConditions.getIdsAdvertTypes() + "))";
			isDiscretCondition = true;
		}
		
		if (orderConditions.getProduct() != null && !orderConditions.getProduct().isNew()) {
			if (isDiscretCondition) {
				result += " AND ";
			}
			String additionalSelect = "select oi.order_id from sr_order_item oi, oc_product p"
					+ " where oi.product_id = p.product_id"
					+ " and p.product_id = " + orderConditions.getProduct().getId();
			result += "(id in (" + additionalSelect + "))";
			isDiscretCondition = true;
		}		
		if (orderConditions.isTrackCodeNotExist()) {
			if (isDiscretCondition) {
				result += " AND ";
			}
			result += "(track_code = '' OR track_code is NULL)";
			isDiscretCondition = true;
		}
		if (orderConditions.getStatuses().size() > 0) {
			if (isDiscretCondition) {
				result += " AND ";
			}
			result += "(status in (" + orderConditions.getIdsStatuses() + "))";
			isDiscretCondition = true;
		}		
		if (result.trim().equals("SELECT * FROM sr_v_order o WHERE")) {
			result += "(order_date BETWEEN ? AND ?)";
			isPeriodCondition = true;			
		} else {
			if (isPeriodCondition) {
				// за период
				if (isDiscretCondition) {
					result += " AND ";
				}			
				result += "(order_date BETWEEN ? AND ?)";
			}	
		}
		result += " ORDER BY order_no DESC";
		conditionResult.setPeriodExist(isPeriodCondition);
		conditionResult.setConditionText(result);		
		logger.debug("createSQLQueryListOrdersByConditions: {}", result);
		return conditionResult;			
	}
	
	@Override
	public Order findById(int orderId) {
		final String sqlSelectOrder = "SELECT * FROM sr_v_order WHERE id = ?";
		Order order = this.jdbcTemplate.queryForObject(sqlSelectOrder,
		        new Object[]{orderId},
		        new RowMapper<Order>() {
					@Override
		            public Order mapRow(ResultSet rs, int rowNum) throws SQLException {
		            	Order order = new Order();
		            	order.setId(rs.getInt("ID"));	
		            	order.setOrderType(OrderTypes.getValueById(rs.getInt("ORDER_TYPE")));
		            	order.setNo(rs.getInt("ORDER_NO"));
		            	order.setSubNo(rs.getInt("ORDER_SUB_NO"));		            	
		            	order.setOrderDate(rs.getDate("ORDER_DATE"));
		            	if (rs.getInt("STORE_ID") == 2) {
		            		order.setStore(StoreTypes.PM);
		            	} else {
		            		order.setStore(StoreTypes.SR);
		            	}
		            	
		            	AnyCustomer customer = customerDao.findById(rs.getInt("CUSTOMER_ID"));
		                order.setCustomer(customer);
		                
		                order.setSourceType(OrderSourceTypes.getValueById(rs.getInt("SOURCE_TYPE")));
		                order.setAdvertType(OrderAdvertTypes.getValueById(rs.getInt("ADVERT_TYPE")));
		                order.setPaymentType(PaymentTypes.getValueById(rs.getInt("PAYMENT_TYPE")));		                
		                order.setProductCategory(wikiDao.getCategoryById(rs.getInt("CATEGORY_PRODUCT_ID")));
                
		                order.getAmounts().setValue(OrderAmountTypes.TOTAL, rs.getBigDecimal("AMOUNT_TOTAL"));
		                order.getAmounts().setValue(OrderAmountTypes.TOTAL_WITH_DELIVERY, rs.getBigDecimal("AMOUNT_TOTAL_WITH_DELIVERY"));
		                order.getAmounts().setValue(OrderAmountTypes.BILL, rs.getBigDecimal("AMOUNT_BILL"));
		                order.getAmounts().setValue(OrderAmountTypes.SUPPLIER, rs.getBigDecimal("AMOUNT_SUPPLIER"));
		                order.getAmounts().setValue(OrderAmountTypes.MARGIN, rs.getBigDecimal("AMOUNT_MARGIN"));
		                order.getAmounts().setValue(OrderAmountTypes.POSTPAY, rs.getBigDecimal("AMOUNT_POSTPAY"));		
						
		                order.setAnnotation(rs.getString("ANNOTATION"));
						
		                order.setStatus(OrderStatuses.getValueById(rs.getInt("STATUS")));
		                order.setEmailStatus(OrderEmailStatuses.getValueById(rs.getInt("STATUS_EMAIL")));
		                order.setAddedDate(DateTimeUtils.timestampToDate(rs.getTimestamp("DATE_ADDED")));
		                order.setModifiedDate(DateTimeUtils.timestampToDate(rs.getTimestamp("DATE_MODIFIED")));
		                
		                order.getOffer().setCountDay(rs.getInt("OFFER_COUNT_DAY"));
		                order.getOffer().setStartDate(rs.getTimestamp("OFFER_DATE_START"));
		                 					
		                List<OrderStatusItem> statuses = getStatusesByOrder(order);
		                order.setStatuses(statuses);
		                		                
		                order.setItems(getItemsByOrder(order));		                
		                order.setDelivery(getDeliveryByOrder(order));		                
		                order.setComments(getCommentsByOrder(order.getId()));
		                order.setExternalCrms(getExternalCrmsByOrder(order));
		                
		                return order;
		            }
		        });
		return order;	
	}	
	
	@Override
	public int findIdByNo(int orderNo, int orderSubNo, int orderYear) {
			final String sqlSelectOrder = "SELECT MAX(id) max_id FROM sr_order WHERE order_no = ?"
					+ " AND order_sub_no = ?";
			Integer orderId = this.jdbcTemplate.queryForObject(sqlSelectOrder,
			        new Object[]{orderNo, orderSubNo},
			        new RowMapper<Integer>() {
			            public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
			            	return rs.getInt("MAX_ID");		      
			            }
			        });
			return orderId == null ? 0 : orderId.intValue(); 
	}

	@Override
	public int addOrder(Order order) {
			logger.debug("addOrder():{}", order);
			
			int customerId = 0;
			AnyCustomer checkCustomer = null;
			if (order.getCustomer().getId() > 0) {
				customerId = order.getCustomer().getId();
				checkCustomer = order.getCustomer();
			} else if (customerId == 0 && order.getCustomer().isPerson()) {
				ForeignerCustomer personCustomer = (ForeignerCustomer) order.getCustomer();				
				CustomerConditions customerConditions = new CustomerConditions(order.getCustomer().getType());
				customerConditions.setPersonPhoneNumber(personCustomer.getPhoneNumber());				
				checkCustomer = customerDao.findByConditions(customerConditions);				
			} else if (customerId == 0 && (order.getCustomer().isCompany())) {				
				ForeignerCompanyCustomer companyCustomer = (ForeignerCompanyCustomer) order.getCustomer();				
				CustomerConditions customerConditions = new CustomerConditions(order.getCustomer().getType());
				customerConditions.setCustomerType(order.getCustomer().getType());
				customerConditions.setCompanyInn(companyCustomer.getInn());
				customerConditions.setCompanyMainContactPhoneNumber(companyCustomer.getMainContact().getPhoneNumber());
				customerConditions.setCompanyMainContactEmail(companyCustomer.getMainContact().getEmail());
				checkCustomer = customerDao.findByConditions(customerConditions);
			}  
			if (checkCustomer == null) {
				// новый клиент
				customerId = customerDao.addCustomer(order.getCustomer());
			} else {				
				if (checkCustomer.getId() > 0 && order.getCustomer().isNew()) {
					// customer is exist! -> перепривязка к существующему и обновление данных	
					customerId = checkCustomer.getId();
					order.getCustomer().setId(customerId);
					
					if (order.getCustomer().isPerson()) {
						order.getCustomer().setPersonId(checkCustomer.getPersonId());
					} else if (order.getCustomer().isCompany()) {
						ForeignerCompanyCustomer companyCustomer = (ForeignerCompanyCustomer) order.getCustomer();
						ForeignerCompanyCustomer checkCompanyCustomer = (ForeignerCompanyCustomer) checkCustomer;
						
						companyCustomer.getMainContact().setId(checkCompanyCustomer.getMainContact().getId());
						companyCustomer.getMainContact().setPersonId(checkCompanyCustomer.getMainContact().getPersonId());
					}					
					customerDao.updateCustomer(order.getCustomer());				
				} else if (checkCustomer.getId() > 0 && order.getCustomer().getId() > 0 && checkCustomer.getId() == order.getCustomer().getId()) {
					// правка текущего клиента
					customerId = order.getCustomer().getId();
					customerDao.updateCustomer(order.getCustomer());
				} else if (checkCustomer.getId() > 0 && order.getCustomer().getId() > 0 && checkCustomer.getId() != order.getCustomer().getId()) {
					// customer is exist! -> перепривязка к существующему и обновление данных	
					customerId = checkCustomer.getId();
					order.getCustomer().setId(customerId);
					customerDao.updateCustomer(order.getCustomer());
				}
			}			
			final String sqlInsertOrder = "INSERT INTO sr_order"
					+ " (order_type, order_no, order_sub_no, order_year, order_date, "
					+ "  source_type, advert_type, payment_type, store_id,"
					+ "  category_product_id, customer_id,"
					+ "  amount_total_with_delivery, amount_total, amount_bill, amount_supplier, amount_margin, amount_postpay, "
					+ "  annotation, status, offer_count_day, offer_date_start)"
					+ " VALUES"
					+ " (?, ?, ?, ?, ?, "
					+ "  ?, ?, ?, ?, "
					+ "  ?, ?, "
					+ "  ?, ?, ?, ?, ?, ?, "
					+ "  ?, ?, "
					+ "  ?, ?)";	
			this.jdbcTemplate.update(sqlInsertOrder, new Object[] { 
					order.getOrderType().getId(), order.getNo(), order.getSubNo(), DateTimeUtils.dateToShortYear(order.getOrderDate()), order.getOrderDate(),
					order.getSourceType().getId(), order.getAdvertType().getId(), order.getPaymentType().getId(), order.getStore().getId(), 
					order.getProductCategory().getId(), customerId, 
					order.getAmounts().getTotalWithDelivery(), order.getAmounts().getTotal(), order.getAmounts().getBill(), order.getAmounts().getSupplier(), order.getAmounts().getMargin(), order.getAmounts().getPostpay(),
					order.getAnnotation(), OrderStatuses.BID.getId(),
					order.getOffer().getCountDay(), new Date()});
			final int orderId = getLastInsert();
			
			order.setId(orderId);			
			addOrUpdateOrderItems(order, order.getItems(), true);
			addOrUpdateOrderComments(orderId, order.getComments());

			int deliveryAddressId = 0;
			if (order.getDelivery().getDeliveryType() != DeliveryTypes.PICKUP) {
				deliveryAddressId = customerDao.addAddress(order.getDelivery().getAddress());				
			}
			
			int recipientId = 0;
			if (order.getDelivery().getRecipient() != null && StringUtils.isNotEmpty(order.getDelivery().getRecipient().getFirstName())) {
				Person newRecipient = order.getDelivery().getRecipient();
				
				Person currentRecipient = customerDao.findPerson(newRecipient);
				
				if (currentRecipient.getPersonId() > 0) {
					customerDao.updatePerson(currentRecipient.getPersonId(), newRecipient);
					recipientId = currentRecipient.getPersonId();
				} else {
					recipientId = customerDao.addPerson(newRecipient);	
				}
			}
			
			final String sqlInsertOrderDelivery = "INSERT INTO sr_order_delivery"
					+ "  (order_id, delivery_type, payment_delivery_type, "
					+ "   address_id, date_delivery, time_in, time_out, "
					+ "   price, seller_price, customer_price, annotation, track_code, recipient_id)"
					+ "  VALUES"
					+ "  (?, ?, ?, "
					+ "   ?, ?, ?, ?, "
					+ "   ?, ?, ?, ?, ?, ?)";			
			this.jdbcTemplate.update(sqlInsertOrderDelivery, new Object[] { 
					orderId, 
					order.getDelivery().getDeliveryType().getId(),
					order.getDelivery().getPaymentDeliveryType().getId(),					
					deliveryAddressId,
					order.getDelivery().getAddress().getCarrierInfo().getCourierInfo().getDeliveryDate(), 
					order.getDelivery().getAddress().getCarrierInfo().getCourierInfo().getStart(),
					order.getDelivery().getAddress().getCarrierInfo().getCourierInfo().getEnd(),
					order.getDelivery().getPrice(),
					order.getDelivery().getFactSellerPrice(),
					order.getDelivery().getFactCustomerPrice(),					
					order.getDelivery().getAnnotation(),
					order.getDelivery().getTrackCode(),
					recipientId});

			final String sqlInsertOrderStatus = "INSERT INTO sr_order_status"
					+ "  (order_id, status, crm_status, crm_sub_status, date_added)"
					+ "  VALUES"
					+ "  (?, ?, ?, ?, ?)";	
			if (order.getStatuses().size() > 0) {
				for (OrderStatusItem orderStatusItem : order.getStatuses()) {
					Date addedDate = orderStatusItem.getAddedDate() != null ? orderStatusItem.getAddedDate() : new Date();
					
					this.jdbcTemplate.update(sqlInsertOrderStatus, new Object[] { 
							orderId, 
							orderStatusItem.getStatus().getId(),
							orderStatusItem.getCrmStatus(),
							orderStatusItem.getCrmSubStatus(),
							addedDate});
				}
			} else {
				this.jdbcTemplate.update(sqlInsertOrderStatus, new Object[] { 
						orderId, 
						OrderStatuses.BID.getId(),
						"", 
						"",
						new Date()});	
			}
			return orderId;
	}

	@Override
	public void updateOrder(Order order) {
		logger.debug("updateOrder():{}", order);
		
		AnyCustomer checkCustomer = null;
		
		if (order.getCustomer().isPerson()) {
			
			ForeignerCustomer personCustomer = (ForeignerCustomer) order.getCustomer();				
			CustomerConditions customerConditions = new CustomerConditions(order.getCustomer().getType());
			customerConditions.setPersonPhoneNumber(personCustomer.getPhoneNumber());				
			checkCustomer = customerDao.findByConditions(customerConditions);				
		} else {
			
			ForeignerCompanyCustomer companyCustomer = (ForeignerCompanyCustomer) order.getCustomer();			
			CustomerConditions customerConditions = new CustomerConditions(companyCustomer.getType());
			customerConditions.setCompanyInn(companyCustomer.getInn());
			customerConditions.setCompanyMainContactPhoneNumber(companyCustomer.getMainContact().getPhoneNumber());
			customerConditions.setCompanyMainContactEmail(companyCustomer.getMainContact().getEmail());
			checkCustomer = customerDao.findByConditions(customerConditions);
		}  
		
		int customerId = 0;
		if (checkCustomer == null) {	
			// правка текущего - поменял номер телефона или ключевые реквизиты у организации
			customerDao.updateCustomer(order.getCustomer());
		}
		if (checkCustomer != null) {			
			if (order.getCustomer().getId() > 0 && checkCustomer.getId() == order.getCustomer().getId()) {
				// правка текущего клиента
				customerId = checkCustomer.getId();
				customerDao.updateCustomer(order.getCustomer());
			} else if (order.getCustomer().getId() > 0 && checkCustomer.getId() != order.getCustomer().getId()) {
				// customer is exist! -> перепривязка к существующему и обновление данных	
				customerId = checkCustomer.getId();
				order.getCustomer().setId(customerId);
				order.getCustomer().setPersonId(checkCustomer.getPersonId());
				customerDao.updateCustomer(order.getCustomer());
			}
		}		
		
		addOrUpdateOrderItems(order, order.getItems(), false);
				
		final String sqlUpdateOrder = "UPDATE sr_order"
				+ "  SET customer_id = ?,"
				+ "      order_type = ?,"
				+ "      order_no = ?,"
				+ "      order_sub_no = ?,"
				+ "      order_year = ?,"
				+ "      order_date = ?,"
				+ "		 source_type = ?,"
				+ "		 advert_type = ?,"
				+ "      payment_type = ?,"
				+ "      store_id = ?,"
				+ "      category_product_id = ?,"
				+ "		 amount_total_with_delivery = ?,"				
				+ "      amount_total = ?,"
				+ "		 amount_bill = ?,"
				+ "      amount_supplier = ?,"
				+ "      amount_margin = ?,"
				+ "      amount_postpay = ?,"
				+ "      date_modified = ?,"
				+ "      offer_count_day = ?,"
				+ "      annotation = ?"
				+ "  WHERE id = ?";
		
		this.jdbcTemplate.update(sqlUpdateOrder, new Object[] { 
				order.getCustomer().getId(),
				order.getOrderType().getId(),
				order.getNo(), 
				order.getSubNo(), 
				DateTimeUtils.dateToShortYear(order.getOrderDate()),
				order.getOrderDate(),
				order.getSourceType().getId(), 
				order.getAdvertType().getId(),
				order.getPaymentType().getId(),
				order.getStore().getId(),
				order.getProductCategory().getId(), 
				order.getAmounts().getTotalWithDelivery(),
				order.getAmounts().getTotal(),
				order.getAmounts().getBill(),
				order.getAmounts().getSupplier(),
				order.getAmounts().getMargin(),
				order.getAmounts().getPostpay(),
				new Date(),
				order.getOffer().getCountDay(),
				order.getAnnotation(), 
				order.getId()});
		
		
		if (order.getDelivery().getDeliveryType() == DeliveryTypes.PICKUP) {
			// добавляем адрес доставки
			int deliveryAddressId = 0;
			order.getDelivery().getAddress().setId(deliveryAddressId);
		} else if (order.getDelivery().getDeliveryType() != DeliveryTypes.PICKUP && order.getDelivery().getAddress().getId() == 0 && StringUtils.isNotEmpty(order.getDelivery().getAddress().getAddress())) {
			// добавляем адрес доставки
			int deliveryAddressId = customerDao.addAddress(order.getDelivery().getAddress());
			order.getDelivery().getAddress().setId(deliveryAddressId);
		} else if (order.getDelivery().getAddress().getId() > 0) {
			customerDao.updateAddress(order.getDelivery().getAddress().getId(), order.getDelivery().getAddress());
		}
		
		int recipientId = 0;
		if (order.getDelivery().getRecipient() != null && StringUtils.isNotEmpty(order.getDelivery().getRecipient().getFirstName())) {
			Person newRecipient = order.getDelivery().getRecipient();			
			Person currentRecipient = customerDao.findPerson(newRecipient);
			
			if (currentRecipient.getPersonId() > 0) {
				customerDao.updatePerson(currentRecipient.getPersonId(), newRecipient);
				recipientId = currentRecipient.getPersonId();
			} else {
				recipientId = customerDao.addPerson(newRecipient);	
			}
		}
		
		final String sqlUpdateOrderDelivery = "UPDATE sr_order_delivery"
				+ "  SET delivery_type = ?, "
				+ "      payment_delivery_type = ?,"
				+ "      address_id = ?,"
				+ "      date_delivery = ?,"
				+ "      time_in = ?,"
				+ "      time_out = ?,"
				+ "		 price = ?,"
				+ "		 seller_price = ?,"
				+ "		 customer_price = ?,"
				+ "		 annotation = ?,"
				+ "      track_code = ?,"
				+ "      recipient_id = ?"
				+ "  WHERE order_id = ?";
		
		this.jdbcTemplate.update(sqlUpdateOrderDelivery, new Object[] { 
				order.getDelivery().getDeliveryType().getId(),
				order.getDelivery().getPaymentDeliveryType().getId(),
				order.getDelivery().getAddress().getId(),
				order.getDelivery().getAddress().getCarrierInfo().getCourierInfo().getDeliveryDate(),				
				DateTimeUtils.dateToTimestamp(order.getDelivery().getAddress().getCarrierInfo().getCourierInfo().getStart()),
				DateTimeUtils.dateToTimestamp(order.getDelivery().getAddress().getCarrierInfo().getCourierInfo().getEnd()),
				order.getDelivery().getPrice(),
				order.getDelivery().getFactSellerPrice(),	
				order.getDelivery().getFactCustomerPrice(),
				order.getDelivery().getAnnotation(),
				order.getDelivery().getTrackCode(),		
				recipientId,
				order.getId()});
	}
	
	private OrderStatuses findCurrentStatusOrderByOrderId(int orderId) {
		final String sqlSelectOrder = "SELECT status FROM sr_v_order WHERE id = ?";
		Integer orderStatusId = this.jdbcTemplate.queryForObject(sqlSelectOrder,
		        new Object[]{orderId},
		        new RowMapper<Integer>() {
					@Override
		            public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
		            	return rs.getInt("STATUS");
		            }
		        });
		return OrderStatuses.getValueById(orderStatusId);
	}	
	
	@Override
	public void changeFullStatusOrder(Order order) {
					
		OrderStatuses currentOrderStatus = findCurrentStatusOrderByOrderId(order.getId());
				
		final String sqlUpdateOrder = "UPDATE sr_order"
				+ "  SET order_type = ?,"
				+ "      source_type = ?,"
				+ "      payment_type = ?,"
				+ "      category_product_id = ?,"
				+ "      status = ?,"				
				+ "      date_modified = ?,"
				+ "      annotation = ?"
				+ "  WHERE id = ?";
		
		this.jdbcTemplate.update(sqlUpdateOrder, new Object[] { 
				order.getOrderType().getId(),
				order.getSourceType().getId(),
				order.getPaymentType().getId(),
				order.getProductCategory().getId(),				
				order.getStatus().getId(),	
				new Date(),
				order.getAnnotation(),
				order.getId()});	
		
		final String sqlUpdateOrderDelivery = "UPDATE sr_order_delivery"
				+ "  SET track_code = ?"
				+ "  WHERE order_id = ?";
		
		this.jdbcTemplate.update(sqlUpdateOrderDelivery, new Object[] { 				
				order.getDelivery().getTrackCode(),				
				order.getId()});
		
		if (currentOrderStatus != order.getStatus()) {
			changeCrmStatus(order);
			
			final String sqlInsertOrderStatus = "INSERT INTO sr_order_status"
					+ "  (order_id, status, date_added)"
					+ "  VALUES"
					+ "  (?, ?, ?)";			
			this.jdbcTemplate.update(sqlInsertOrderStatus, new Object[] { 
					order.getId(), 
					order.getStatus().getId(),
					new Date()});
			
			operateSubstactProductQuantityOrder(order);
			
			if (order.getStatus() == OrderStatuses.BID && currentOrderStatus == OrderStatuses.CANCELED) {
				if (order.getOrderType() == OrderTypes.BILL) {
					
					final String sqlUpdateOrderOffer = "UPDATE sr_order"
							+ "  SET offer_date_start = ?"
							+ "  WHERE id = ?";					
					this.jdbcTemplate.update(sqlUpdateOrderOffer, new Object[] { 				
							DateTimeUtils.sysDate(),
							order.getId()});	
				}				
			}			
		}		
	}
	
	private void changeCrmStatus(Order order) {
		
		final String sqlSelectCountCrmOrder = "SELECT count(*) COUNT_ID FROM sr_order_crm_connect WHERE order_id = ? AND crm_id = ? AND crm_status = 1";
		Integer countOpencart = this.jdbcTemplate.queryForObject(sqlSelectCountCrmOrder,
		        new Object[]{
		        		order.getId(),
		        		CrmTypes.OPENCART.getId()
		        		},
		        new RowMapper<Integer>() {
					@Override
		            public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
		            	return rs.getInt("COUNT_ID");
		            }
		        });
		
		if (countOpencart == 0) {
			return;
		}
		
		Integer countYandexMarket = this.jdbcTemplate.queryForObject(sqlSelectCountCrmOrder,
		        new Object[]{
		        		order.getId(),
		        		CrmTypes.YANDEX_MARKET.getId()
		        		},
		        new RowMapper<Integer>() {
					@Override
		            public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
		            	return rs.getInt("COUNT_ID");
		            }
		        });
		Integer countOzonMarket = this.jdbcTemplate.queryForObject(sqlSelectCountCrmOrder,
		        new Object[]{
		        		order.getId(),
		        		CrmTypes.OZON.getId()
		        		},
		        new RowMapper<Integer>() {
					@Override
		            public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
		            	return rs.getInt("COUNT_ID");
		            }
		        });
		
		int crmOrderStatusId = 17;
		String crmComment = "";
		if (order.getStatus() == OrderStatuses.APPROVED) {
			
			crmOrderStatusId = 19;
			crmComment = order.getStatus().getAnnotation();
						
			List<OrderItem> orderItems = getItemsByOrder(order);
			order.setItems(orderItems);		
			
			if (countYandexMarket > 0) {
				
				YandexMarketApi yandexMarketApi = new YandexMarketApi(this.environment);
				
				// поставим в адрес доставки данные из ЯМа 
				Order ymOrder = yandexMarketApi.order(order);
				String addressText = ymOrder.getDelivery().getAddress().getAddress();
				
				order.getDelivery().getAddress().setAddress(addressText);			
				customerDao.updateAddress(order.getDelivery().getAddress().getId(), order.getDelivery().getAddress());
				
				if (orderItems.size() == 1 && orderItems.get(0).getQuantity() == 1) {								
					// делаем грузовые места и смену статусов только для одного товара и одного места
					yandexMarketApi.status(order);	
				}				
			}
			
			if (countOzonMarket > 0) {				
				OzonMarketApi ozonMarketApi = new OzonMarketApi(this.environment);				
				// поставим в адрес доставки данные из OZON 
				OrderExternalCrm ozonCrm = order.getExternalCrmByCode(CrmTypes.OZON);
				Order ozonOrder = ozonMarketApi.getOrder(ozonCrm.getParentCode());
				String addressText = ozonOrder.getDelivery().getAddress().getAddress();
				
				order.getDelivery().getAddress().setAddress(addressText);			
				customerDao.updateAddress(order.getDelivery().getAddress().getId(), order.getDelivery().getAddress());
			}	
			
			
		} else if (order.getStatus() == OrderStatuses.DELIVERING) {
			crmOrderStatusId = 3;
			if (StringUtils.isEmpty(order.getDelivery().getTrackCode())) {
				crmComment = "на доставке";
			} else {
				crmComment = "трэккод: " + order.getDelivery().getTrackCode();	
			}
			
			if (countYandexMarket > 0 
					&& order.getDelivery().getAddress().getCarrierInfo().getCourierInfo().getDeliveryDate() != null 
					&& DateTimeUtils.truncateDate(order.getDelivery().getAddress().getCarrierInfo().getCourierInfo().getDeliveryDate()).compareTo(DateTimeUtils.sysDate()) == 0
					) {				
				// если запись яма есть, и "сегодня" равно планируемой дате отгрузки, то меняем статус в яме на status = "PROCESSING", subStatus = "SHIPPED"				
				YandexMarketApi yandexMarketApi = new YandexMarketApi(this.environment);
				yandexMarketApi.status(order);	
			} else {
				logger.error("Yandex.Market status not changed: sysDate != deliveryDate! sysDate:{}, deliveryDate:{}", DateTimeUtils.sysDate(), order.getDelivery().getAddress().getCarrierInfo().getCourierInfo().getDeliveryDate());
			}
					
			
		} else if (order.getStatus() == OrderStatuses.READY_GIVE_AWAY || order.getStatus() == OrderStatuses.READY_GIVE_AWAY_TROUBLE) {
			crmOrderStatusId = 20;
			if (StringUtils.isEmpty(order.getDelivery().getTrackCode())) {
				crmComment = "прибыл";
			} else {
				crmComment = "трэккод: " + order.getDelivery().getTrackCode();	
			}	
		} else if (order.getStatus() == OrderStatuses.DELIVERED) {
			crmOrderStatusId = 18;			
		} else if (order.getStatus() == OrderStatuses.CANCELED) {
			crmOrderStatusId = 16;			
		} else if (order.getStatus() == OrderStatuses.PAY_WAITING) {
			crmOrderStatusId = 2;			
		} else if (order.getStatus() == OrderStatuses.PAY_ON) {
			crmOrderStatusId = 3; // отправлен, чтобы не было повторной загрузки из срм 			
		} else if (order.getStatus() == OrderStatuses.FINISHED) {
			crmOrderStatusId = 5;			
		} else if (order.getStatus() == OrderStatuses.REDELIVERY) {
			crmOrderStatusId = 12;			
		} else if (order.getStatus() == OrderStatuses.REDELIVERY_FINISHED) {
			crmOrderStatusId = 11;			
		}
		
		final String sqlSelectCrmOrder = "SELECT * FROM sr_order_crm_connect WHERE order_id = ? AND crm_id = ? AND crm_status = 1";
		int crmOrderId = this.jdbcTemplate.queryForObject(sqlSelectCrmOrder,
		        new Object[]{
		        		order.getId(),
		        		1},
		        new RowMapper<Integer>() {
					@Override
		            public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
		            	return rs.getInt("PARENT_CRM_ID");
		            }
		        });
		if (crmOrderId <= 0) {
			return;
		}
		
		final String sqlUpdateCrmOrderStatus = "UPDATE oc_order"
				+ "  SET order_status_id = ?"
				+ "  WHERE order_id = ?";
		
		this.jdbcTemplate.update(sqlUpdateCrmOrderStatus, new Object[] { 				
				crmOrderStatusId,
				crmOrderId});	
		
		
		final String sqlInsertCrmOrderStatusHistory = "INSERT INTO oc_order_history"
				+ "  (order_id, order_status_id, notify, comment, date_added)"
				+ "  VALUES"
				+ "  (?, ?, 1, ?, ?)";			
		this.jdbcTemplate.update(sqlInsertCrmOrderStatusHistory, new Object[] { 
				crmOrderId, 
				crmOrderStatusId,
				crmComment,
				new Date()});
		
	}
	
	public void changeBillExpiredStatusOrder(Order order) {
		
		if (order.getStatus() == OrderStatuses.BID && (order.getOrderType() == OrderTypes.BILL || order.getOrderType() == OrderTypes.KP)) {
			
			final String sqlUpdateOrder = "UPDATE sr_order"
					+ "  SET status = ?,"				
					+ "      date_modified = ?"
					+ "  WHERE id = ?";
			
			this.jdbcTemplate.update(sqlUpdateOrder, new Object[] { 
					OrderStatuses.CANCELED.getId(),	
					new Date(),
					order.getId()});	
						
		}
	}
	
	/**
	 * Операция по расходу товаров с полок нашего склада при подтверждении заказа
	 * @param order
	 */
	private void operateSubstactProductQuantityOrder(Order order) {
		// 1 опенкарт прибормастер (пользовательские и яндекс маркет)
			// при создании - только обновить остаток продукта
			// при подтверждении - снять с нашего склада
		// 2 опенкарт сэр ричард
			// при создании снять с oc_product
			// при подтверждении - снять с нашего склада
		// 3 ручные
			// при создании ничего
			// при подтверждении снять с oc_product, снять с нашего склада
		// 4 озон
			// при создании -  ничего
			// при подтверждении - снять с нашего склада
		
		if (order.getStatus() != OrderStatuses.APPROVED) {
			return;
		}

		/*
		final String sqlUpdateSupplierStockProduct = "UPDATE sr_stock s"
				+ "  SET quantity = ?"
				+ "  WHERE s.id = ?";
		*/
		List<OrderItem> orderItems = getItemsByOrder(order);
		
		boolean isCrmLoad = false;		
		if (order.getExternalCrms() != null && order.getExternalCrms().size() > 0) {
			isCrmLoad = true;
		}		
		
		boolean isProduct = false;
		boolean isStock = false;
		boolean isSynchronize = false;
		if (isCrmLoad && order.getStore() == StoreTypes.SR) {
			isProduct = false;
			isStock = true;
			isSynchronize = false;
		} else if (isCrmLoad && order.getAdvertType() == OrderAdvertTypes.OZON) {
			isProduct = true;
			isStock = true;
			isSynchronize = false;
		} else if (!isCrmLoad) {
			isProduct = true;
			isStock = true;
			isSynchronize = false;
		} else {
			isProduct = false;
			isStock = true;
			isSynchronize = false;			
		}		
		
		for (OrderItem orderItem : orderItems) {
			
			
			wikiDao.updateDeltaQuantityProduct2(orderItem.getProduct(), orderItem.getQuantity(), isProduct, isStock, isSynchronize);
			
			/*
			Product product = orderItem.getProduct();
			int deltaQuantity = orderItem.getQuantity();
			SupplierStockProduct supplierStockProduct = wikiDao.supplierStockProductFindByProductId(product.getId());
			if (supplierStockProduct == null) {
				continue;
			}			
			int quantity = supplierStockProduct.getProduct().getCompositeStockQuantity();			
			
			// снять товар с фронта для резерва под яндекс.маркет
			if (order.getAdvertType() != OrderAdvertTypes.YANDEX_MARKET) {
				// яндекс маркет сметает с полок сразу после акцепта, это для всех остальных				
				if ((product.getQuantity() - deltaQuantity) >= 0) {
					wikiDao.updateDeltaQuantityProduct(supplierStockProduct.getProduct().getId(), deltaQuantity);
					orderItem.getProduct().setQuantity(product.getQuantity() - deltaQuantity);					
				} else {
					wikiDao.updateDeltaQuantityProduct(supplierStockProduct.getProduct().getId(), orderItem.getProduct().getQuantity());
					product.setQuantity(0);
				}	
			}	
			
			if ((quantity - deltaQuantity) >= 0) {
				
				if (supplierStockProduct.getProduct().isComposite()) {
					// это комплект
					for (Product slave : supplierStockProduct.getProduct().getKitComponents()) {	
						SupplierStockProduct supplierStockSlaveProduct = wikiDao.supplierStockProductFindByProductId(slave.getId());
						if (supplierStockSlaveProduct == null) {
							break;
						}
						int slaveQuantity = supplierStockSlaveProduct.getProduct().getStockQuantity();
						int deltaSlaveQuantity = slave.getSlaveQuantity() * deltaQuantity;
						
						slaveQuantity = slaveQuantity - deltaSlaveQuantity;						
						this.jdbcTemplate.update(sqlUpdateSupplierStockProduct, new Object[] { 				
								slaveQuantity,
								supplierStockSlaveProduct.getId()});
						
						
						if (!order.isOpenCart()) {
							wikiDao.updateDeltaQuantityProduct(slave.getId(), deltaSlaveQuantity);
						}
												
						Product wikiProduct = wikiDao.getProductById(slave.getId());
						wikiProduct.setStockQuantity(slaveQuantity);
					}
				} else {
					// это штучный товар
					quantity = quantity - deltaQuantity;
					this.jdbcTemplate.update(sqlUpdateSupplierStockProduct, new Object[] { 				
							quantity,
							supplierStockProduct.getId()});
					
					Product wikiProduct = wikiDao.getProductById(product.getId());
					wikiProduct.setStockQuantity(quantity);					
				}
				
			} else {
				continue;
			}
			*/
		}
	}
	
	@Override
	public void changeEmailStatusOrder(int orderId, OrderEmailStatuses status) {
		
		final String sqlUpdateOrder = "UPDATE sr_order"
				+ "  SET status_email = ?"				
				+ "  WHERE id = ?";
		
		this.jdbcTemplate.update(sqlUpdateOrder, new Object[] { 				
				status.getId(),
				orderId});		
		
	}
	
	
	@Override
	public void changeStatusOrder(int orderId, OrderStatuses status, String annotation, String trackCode, OrderStatusItem newOrderStatusValue) {
		
		OrderStatuses currentOrderStatus = findCurrentStatusOrderByOrderId(orderId);
				
		final String sqlUpdateOrder = "UPDATE sr_order"
				+ "  SET status = ?,"				
				+ "      date_modified = ?,"
				+ "      annotation = ?"
				+ "  WHERE id = ?";
		
		this.jdbcTemplate.update(sqlUpdateOrder, new Object[] { 				
				status.getId(),	
				new Date(),
				annotation,
				orderId});		
		
		final String sqlUpdateOrderDelivery = "UPDATE sr_order_delivery"
				+ "  SET track_code = ?"
				+ "  WHERE order_id = ?";
		
		this.jdbcTemplate.update(sqlUpdateOrderDelivery, new Object[] { 				
				trackCode,				
				orderId});
		
		if (currentOrderStatus != status) {
			if (newOrderStatusValue == null) {
				final String sqlInsertOrderStatus = "INSERT INTO sr_order_status"
						+ "  (order_id, status, date_added)"
						+ "  VALUES"
						+ "  (?, ?, ?)";			
				this.jdbcTemplate.update(sqlInsertOrderStatus, new Object[] { 
						orderId, 
						status.getId(),
						new Date()});
				
			} else {
				final String sqlInsertOrderStatus = "INSERT INTO sr_order_status"
						+ "  (order_id, status, date_added, crm_status, crm_sub_status)"
						+ "  VALUES"
						+ "  (?, ?, ?, ?, ?)";			
				this.jdbcTemplate.update(sqlInsertOrderStatus, new Object[] { 
						orderId, 
						status.getId(),
						new Date(),
						newOrderStatusValue.getCrmStatus(),
						newOrderStatusValue.getCrmSubStatus()});
			}
		}		
	}
	
	@Override
	public void addCrmOrderImport(int orderId, Order crmOrder) {

		final String sqlInsertCrmStatus = "INSERT INTO sr_order_crm_connect"
				+ " (order_id, crm_id, parent_crm_id, parent_crm_code, crm_status)"
				+ " VALUES"
				+ " (?, ?, ?, ?, ?)";	
		for (OrderExternalCrm externalCrm : crmOrder.getExternalCrms()) {			
			this.jdbcTemplate.update(sqlInsertCrmStatus, new Object[] { 
					orderId, 
					externalCrm.getCrm().getId(), 
					externalCrm.getParentId(),
					StringUtils.isEmpty(externalCrm.getParentCode()) ? null : externalCrm.getParentCode(),  
					CrmStatuses.SUCCESS.getId()});			
			
		}		
		
	}
	
	@Override
	public int nextOrderNo() {
		//int orderYear = DateTimeUtils.dateToShortYear(DateTimeUtils.sysDate());
		final String sqlSelectMaxNoOrder = "SELECT MAX(order_no) max_order_no FROM sr_order o";
		Integer maxOrderNo = this.jdbcTemplate.queryForObject(sqlSelectMaxNoOrder,
		        new Object[]{},
		        new RowMapper<Integer>() {
					@Override
		            public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
		            	return Integer.valueOf(rs.getInt("max_order_no"));	
		            }
		        });
		return maxOrderNo + 1;
	}
	
	private void addOrUpdateOrderItems(Order order, List<OrderItem> orderItems, boolean isInsert) {
		
		boolean isCrmLoad = false;		
		if (isInsert && order.getExternalCrms() != null && order.getExternalCrms().size() > 0) {
			isCrmLoad = true;
		}		
		
		boolean isProduct = false;
		boolean isStock = false;
		boolean isSynchronize = false;
		if (order.getStore() == StoreTypes.PM && isCrmLoad) {
			isSynchronize = true;
		}
		if (order.getStore() == StoreTypes.SR && isCrmLoad) {
			isProduct = true;
		}
		
		final String sqlDeleteOrderItems = "DELETE FROM sr_order_item WHERE order_id = ?";
		this.jdbcTemplate.update(sqlDeleteOrderItems, new Object[] {order.getId()});
		
		final String sqlInsertOrderItem = "INSERT INTO sr_order_item"
				+ "(no, order_id, product_id, price, quantity, discount_rate, amount, amount_supplier)"
				+ "VALUES"
				+ "(?, ?, ?, ?, ?, ?, ?, ?)";		
		
		int no = 1;
		for (OrderItem orderItem : orderItems) {
			BigDecimal discountRate = orderItem.getDiscountRate() == null ? BigDecimal.ZERO : orderItem.getDiscountRate();  
			this.jdbcTemplate.update(sqlInsertOrderItem, new Object[] {
					no,
					order.getId(),
					orderItem.getProduct().getId(),
					orderItem.getPrice(),
					orderItem.getQuantity(),
					discountRate,
					orderItem.getAmount(),
					orderItem.getSupplierAmount()});
			no++;
			wikiDao.updateDeltaQuantityProduct2(orderItem.getProduct(), orderItem.getQuantity(), isProduct, isStock, isSynchronize);
		}		
	}
	
	private void addOrUpdateOrderComments(int orderId, Set<Comment> orderComments) {
		
		final String sqlDeleteOrderComments = "DELETE FROM sr_order_comment WHERE order_id = ?";
		this.jdbcTemplate.update(sqlDeleteOrderComments, new Object[] { 
				orderId});
		
		final String sqlInsertOrderComment = "INSERT INTO sr_order_comment"
				+ "(order_id, comment_type, code, value)"
				+ "VALUES"
				+ "(?, ?, ?, ?)";		
		
		for (Comment orderComment : orderComments) {
			
			String value = StringUtils.truncate(orderComment.getValue(), 255);
			
			this.jdbcTemplate.update(sqlInsertOrderComment, new Object[] {						
					orderId,
					orderComment.getCommentType().getId(),
					orderComment.getKey(),
					value});
		}			
	}

	@Transactional
	@Override
	public void deleteOrder(int orderId) {
		logger.debug("deleteOrder():{}", orderId);
		/*
		Order oldOrder = findById(orderId);		
		changeStatusOrder(orderId, OrderStatuses.DELETED, oldOrder.getAnnotation(), oldOrder.getDelivery().getTrackCode());
		*/
	}
	
	@Transactional
	@Override
	public void eraseOrder(int orderId, boolean isDeleteCustomer) {
		logger.debug("eraseOrder():{}", orderId);
		
		final String sqlDeleteOrder = "DELETE FROM sr_order WHERE id = ?";
		final String sqlDeleteOrderItem = "DELETE from sr_order_item WHERE order_id = ?";
		final String sqlDeleteOrderDelivery = "DELETE from sr_order_delivery WHERE order_id = ?";
		final String sqlDeleteOrderStatus = "DELETE from sr_order_status WHERE order_id = ?";
		final String sqlDeleteOrderComment = "DELETE from sr_order_comment WHERE order_id = ?";		
		final String sqlDeleteOrderCrmConnect = "DELETE from sr_order_crm_connect WHERE order_id = ?";
		Order order = findById(orderId);
		this.jdbcTemplate.update(sqlDeleteOrderItem, new Object[] {order.getId()});
		this.jdbcTemplate.update(sqlDeleteOrderDelivery, new Object[] {order.getId()});
		this.jdbcTemplate.update(sqlDeleteOrderStatus, new Object[] {order.getId()});
		this.jdbcTemplate.update(sqlDeleteOrderComment, new Object[] {order.getId()});
		this.jdbcTemplate.update(sqlDeleteOrderCrmConnect, new Object[] {order.getId()});
		this.jdbcTemplate.update(sqlDeleteOrder, new Object[] {order.getId()});
		
		if (isDeleteCustomer) {
			customerDao.eraseCustomer(order.getCustomer().getId());
		}
	}
	
	@Override
	public boolean checkNotUniqueOrderNo(int orderId, int orderNo, int orderYear) {
		final String sqlSelectCountNoOrder = "SELECT COUNT(o.order_no) COUNT_ORDER_NO FROM sr_order o"
				+ "  WHERE o.order_year = ? "
				+ "    AND o.id <> ?"
				+ "    AND o.order_no = ?";
		Integer count = this.jdbcTemplate.queryForObject(sqlSelectCountNoOrder,
		        new Object[]{orderYear, orderId, orderNo},
		        new RowMapper<Integer>() {
					@Override
		            public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
		            	return Integer.valueOf(rs.getInt("COUNT_ORDER_NO"));	
		            }
		        });
		return (count > 0);
	}
	
	private List<OrderStatusItem> getStatusesByOrder(Order order) {
		final String sqlSelectOrderStatusItems = "SELECT *"
		        + "  FROM sr_order_status os"
		        + "  WHERE os.order_id = ?"
		        + "  ORDER BY id";		
		List<OrderStatusItem> orderStatusItems = this.jdbcTemplate.query(sqlSelectOrderStatusItems,
				new Object[]{order.getId()},
		        new RowMapper<OrderStatusItem>() {
					@Override
		            public OrderStatusItem mapRow(ResultSet rs, int rowNum) throws SQLException {
		            	OrderStatusItem orderStatusItem = new OrderStatusItem(order);			                
		            	orderStatusItem.setId(rs.getInt("ID"));
		            	orderStatusItem.setNo(rowNum);		            	
		            	orderStatusItem.setStatus(OrderStatuses.getValueById(rs.getInt("STATUS")));
		            	orderStatusItem.setCrmStatus(rs.getString("CRM_STATUS"));
		            	orderStatusItem.setCrmSubStatus(rs.getString("CRM_SUB_STATUS"));
		            	orderStatusItem.setAddedDate(DateTimeUtils.timestampToDate(rs.getTimestamp("DATE_ADDED")));	   
		                return orderStatusItem;
		            }		            
		        });
		return orderStatusItems;		
	}
	
	private Set<Comment> getCommentsByOrder(int orderId) {
		final String sqlSelectOrderComments = "SELECT *"
		        + "  FROM sr_order_comment oc"
		        + "  WHERE oc.order_id = ?"
		        + "  ORDER BY oc.comment_type, oc.id";
		
		List<Comment> orderCommentList = this.jdbcTemplate.query(sqlSelectOrderComments,
				new Object[]{orderId},
		        new RowMapper<Comment>() {
		            public Comment mapRow(ResultSet rs, int rowNum) throws SQLException {
		            	Comment orderComment = new Comment(CommentTypes.getValueById(rs.getInt("COMMENT_TYPE")));			                
		            	orderComment.setId(rs.getInt("ID"));		            	
		            	orderComment.setKey(rs.getString("CODE"));
		            	orderComment.setValue(rs.getString("VALUE"));
		                return orderComment;
		            }		            
		        });
		Set<Comment> orderComments = new HashSet<Comment>();
		for (Comment orderComment : orderCommentList) {
			orderComments.add(orderComment);			
		}
		return orderComments;		
	}
	
	private List<OrderExternalCrm> getExternalCrmsByOrder(Order order) {
		final String sqlSelectOrderExternalCrms = "SELECT *"
		        + "  FROM sr_order_crm_connect oc"
		        + "  WHERE oc.order_id = ?"
		        + "  ORDER BY oc.crm_id, oc.id";
		
		List<OrderExternalCrm> orderExternalCrms = this.jdbcTemplate.query(sqlSelectOrderExternalCrms,
				new Object[]{order.getId()},
		        new RowMapper<OrderExternalCrm>() {
		            public OrderExternalCrm mapRow(ResultSet rs, int rowNum) throws SQLException {		            	
		            			            	
		            	OrderExternalCrm crm = new OrderExternalCrm(order);		                
		                crm.setId(rs.getInt("ID"));
		                crm.setCrm(CrmTypes.getValueById(rs.getInt("CRM_ID")));		                
		                crm.setParentId(rs.getInt("PARENT_CRM_ID"));
		                crm.setParentCode(rs.getString("PARENT_CRM_CODE"));
		                
		                crm.setStatus(CrmStatuses.getValueById(rs.getInt("CRM_STATUS")));
		                order.getExternalCrms().add(crm);
		                return crm;
		            }		            
		        });
		
		return orderExternalCrms;		
	}
	
	@Override
	public List<OrderItem> getItemsByOrder(Order order) {
		
		final String sqlSelectOrderItems = "SELECT *"
		        + "  FROM sr_order_item oi"
		        + "  WHERE oi.order_id = ?"
		        + "  ORDER BY no";   
		
		List<OrderItem> orderItems = this.jdbcTemplate.query(sqlSelectOrderItems,
				new Object[]{order.getId()},
		        new RowMapper<OrderItem>() {
					@Override
		            public OrderItem mapRow(ResultSet rs, int rowNum) throws SQLException {
		            	OrderItem orderItem = new OrderItem(order);			                
		            	orderItem.setId(rs.getInt("ID"));
		            	orderItem.setNo(rs.getInt("NO"));
		            	orderItem.setProduct(wikiDao.getProductById(rs.getInt("PRODUCT_ID")));
		            	orderItem.setPrice(rs.getBigDecimal("PRICE"));
		            	orderItem.setQuantity(rs.getInt("QUANTITY"));
		            	orderItem.setDiscountRate(rs.getBigDecimal("DISCOUNT_RATE"));
		            	orderItem.setAmount(rs.getBigDecimal("AMOUNT"));
		            	orderItem.setSupplierAmount(rs.getBigDecimal("AMOUNT_SUPPLIER"));
		                return orderItem;
		            }
		        });
		return orderItems;
	}
	
	@Override
	public Map<OrderStatuses, List<Order>> getDeliveryOrders(Date date) {
		
		final Map<OrderStatuses, List<Order>> result = new HashMap<OrderStatuses, List<Order>>();
		
		Pair<Date> dates = new Pair<Date>();
		dates.setStart(date);
		dates.setEnd(DateTimeUtils.afterAnyDate(date, 1));

		result.put(OrderStatuses.DELIVERING, getDeliveryOrdersByStatus(dates, OrderStatuses.DELIVERING));
		result.put(OrderStatuses.READY_GIVE_AWAY, getDeliveryOrdersByStatus(dates, OrderStatuses.READY_GIVE_AWAY));
		result.put(OrderStatuses.DELIVERED, getDeliveryOrdersByStatus(dates, OrderStatuses.DELIVERED));
		
		return result;		
	}
	
	private List<Order> getDeliveryOrdersByStatus(Pair<Date> dates, OrderStatuses status) {
		
		final String sqlSelectOrders = "SELECT o.order_no, min(o.id) ORDER_ID,  min(o.short_name) SHORT_NAME,  min(o.long_name) LONG_NAME, min(o.address) DELIVERY_ADDRESS"
				+ "		  FROM sr_v_order o, sr_order_status os"
				+ "		  WHERE delivery_type in (101, 102, 103, 104, 201, 401, 402, 403, 405, 501, 701, 601, 801, 802, 803)"
				+ "		    and o.id = os.order_id and os.status = ?"
				+ "		    and (os.date_added between ? and ?)"
				+ "       GROUP BY o.order_no";
		
		List<Order> deliveringOrders = this.jdbcTemplate.query(sqlSelectOrders,				
				new Object[]{status.getId(), dates.getStart(), dates.getEnd()},
		        new RowMapper<Order>() {
					@Override
		            public Order mapRow(ResultSet rs, int rowNum) throws SQLException {
						Order order = new Order();
						order.setId(rs.getInt("ORDER_ID"));
						order.setNo(rs.getInt("ORDER_NO"));						
						ForeignerCustomer customer = new ForeignerCustomer();
						customer.setFirstName(rs.getString("SHORT_NAME"));
						customer.setLastName(rs.getString("LONG_NAME"));						
						order.setCustomer(customer);						
						order.getDelivery().getAddress().setAddress(rs.getString("DELIVERY_ADDRESS"));						
						return order;
						
		            }
		        });
		
		return deliveringOrders;
	}

	private OrderDelivery getDeliveryByOrder(Order order) {

		final String sqlSelectOrderDelivery = "SELECT * FROM sr_order_delivery od"
				+ "  WHERE od.order_id = ?"
				+ "  ORDER BY id";

		OrderDelivery orderDelivery = this.jdbcTemplate.queryForObject(sqlSelectOrderDelivery,
				new Object[] {order.getId()}, 
					new RowMapper<OrderDelivery>() {
					@Override
					public OrderDelivery mapRow(ResultSet rs, int rowNum) throws SQLException {
						OrderDelivery orderDelivery = new OrderDelivery(order);
						orderDelivery.setId(rs.getInt("ID"));
						orderDelivery.setDeliveryType(DeliveryTypes.getValueById(rs.getInt("DELIVERY_TYPE")));
						orderDelivery.setPaymentDeliveryType(PaymentDeliveryTypes.getValueById(rs.getInt("PAYMENT_DELIVERY_TYPE")));
						orderDelivery.setAddress(customerDao.getAddress(rs.getInt("ADDRESS_ID")));
						
						orderDelivery.setPrice(rs.getBigDecimal("PRICE"));
						orderDelivery.setFactSellerPrice(rs.getBigDecimal("SELLER_PRICE"));
						orderDelivery.setFactCustomerPrice(rs.getBigDecimal("CUSTOMER_PRICE"));
																		
						orderDelivery.setAnnotation(rs.getString("ANNOTATION"));
						orderDelivery.setTrackCode(rs.getString("TRACK_CODE"));
						orderDelivery.getAddress().getCarrierInfo().getCourierInfo().setDeliveryDate(rs.getDate("DATE_DELIVERY"));
						orderDelivery.getAddress().getCarrierInfo().getCourierInfo().setStart(DateTimeUtils.timestampToDate(rs.getTimestamp("TIME_IN")));
						orderDelivery.getAddress().getCarrierInfo().getCourierInfo().setEnd(DateTimeUtils.timestampToDate(rs.getTimestamp("TIME_OUT")));
						
						if (rs.getInt("RECIPIENT_ID") >= 0) {
							Person recipient = customerDao.getPerson(rs.getInt("RECIPIENT_ID"));
							orderDelivery.setRecipient(recipient);
						}
						return orderDelivery;
					}
				});
		return orderDelivery;
	}

}
