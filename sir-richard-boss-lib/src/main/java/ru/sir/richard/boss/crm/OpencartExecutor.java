package ru.sir.richard.boss.crm;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import ru.sir.richard.boss.api.market.YandexMarketApi;
import ru.sir.richard.boss.dao.AnyDaoImpl;
import ru.sir.richard.boss.dao.CustomerDao;
import ru.sir.richard.boss.dao.OrderDao;
import ru.sir.richard.boss.dao.WikiDao;
import ru.sir.richard.boss.model.calc.AnyOrderTotalAmountsCalculator;
import ru.sir.richard.boss.model.data.Address;
import ru.sir.richard.boss.model.data.Customer;
import ru.sir.richard.boss.model.data.ForeignerCustomer;
import ru.sir.richard.boss.model.data.Order;
import ru.sir.richard.boss.model.data.OrderAmounts;
import ru.sir.richard.boss.model.data.OrderExternalCrm;
import ru.sir.richard.boss.model.data.OrderItem;
import ru.sir.richard.boss.model.data.OrderStatusItem;
import ru.sir.richard.boss.model.data.Product;
import ru.sir.richard.boss.model.data.conditions.CustomerConditions;
import ru.sir.richard.boss.model.factories.OrderTotalAmountsCalculatorFactory;
import ru.sir.richard.boss.model.types.AddressTypes;
import ru.sir.richard.boss.model.types.Countries;
import ru.sir.richard.boss.model.types.CrmTypes;
import ru.sir.richard.boss.model.types.DeliveryTypes;
import ru.sir.richard.boss.model.types.OrderAdvertTypes;
import ru.sir.richard.boss.model.types.OrderAmountTypes;
import ru.sir.richard.boss.model.types.OrderSourceTypes;
import ru.sir.richard.boss.model.types.OrderStatuses;
import ru.sir.richard.boss.model.types.OrderTypes;
import ru.sir.richard.boss.model.types.PaymentDeliveryTypes;
import ru.sir.richard.boss.model.types.PaymentTypes;
import ru.sir.richard.boss.model.types.StoreTypes;
import ru.sir.richard.boss.model.utils.DateTimeUtils;
import ru.sir.richard.boss.model.utils.TextUtils;

@Service
@Slf4j
public class OpencartExecutor extends AnyDaoImpl implements CrmExecutable {

	private Date executorDate;
	
	@Autowired
	private Environment environment;
	
	@Autowired
	private WikiDao wikiDao;
	
	@Autowired
	private OrderDao orderDao;
	
	@Autowired
	private CustomerDao customerDao;
	
	public OpencartExecutor() {		
		
	}

	public Date getExecutorDate() {
		return executorDate;
	}

	@Override
	public void setExecutorDate(Date executorDate) {
		this.executorDate = executorDate;
	}

	@Override
	public void run() {
		log.debug("run(): start");
		importFromCrmOrders(StoreTypes.PM);		
		log.debug("run(): end");
	}	
	
	private List<Order> importFromCrmOrders(StoreTypes store) {		
		List<Order> crmOrders;
		crmOrders = importFromPmCrm();
				
		// addOrder
		for (Order crmOrder : crmOrders) {
			// проверка на существующего клиента
			ForeignerCustomer personCustomer = (ForeignerCustomer) crmOrder.getCustomer();			
			CustomerConditions customerConditions = new CustomerConditions(crmOrder.getCustomer().getType());
			customerConditions.setPersonPhoneNumber(personCustomer.getPhoneNumber());				
			ForeignerCustomer checkCustomer = (ForeignerCustomer) customerDao.findByConditions(customerConditions);
			if (checkCustomer != null) {
				// это новый заказ уже существующего клиента
				if (StringUtils.isEmpty(personCustomer.getLastName()) && StringUtils.isNotEmpty(checkCustomer.getLastName())) {
					personCustomer.setLastName(checkCustomer.getLastName());
				}
				if (StringUtils.isEmpty(personCustomer.getMiddleName()) && StringUtils.isNotEmpty(checkCustomer.getMiddleName())) {
					personCustomer.setMiddleName(checkCustomer.getMiddleName());
				}
				if (StringUtils.isEmpty(personCustomer.getEmail()) && StringUtils.isNotEmpty(checkCustomer.getEmail())) {
					personCustomer.setEmail(checkCustomer.getEmail());
				}
				if (StringUtils.isEmpty(personCustomer.getMainAddress().getAddress()) && StringUtils.isNotEmpty(checkCustomer.getMainAddress().getAddress())) {
					personCustomer.getMainAddress().setAddress(checkCustomer.getMainAddress().getAddress());
				}
				crmOrder.getCustomer().setId(checkCustomer.getId()); // понадобится для яма - найти правильный warehouseId для урла - получить адрес из яма
			}
			
			crmOrder.setNo(orderDao.nextOrderNo());
			/*	
			if (crmOrder.getAdvertType() == OrderAdvertTypes.YANDEX_MARKET && crmOrder.getExternalCrmByCode(CrmTypes.YANDEX_MARKET) != null) {
				
				AnyOrderTotalAmountsCalculator calculator = OrderTotalAmountsCalculatorFactory.createCalculator(crmOrder);
				OrderAmounts recalcOrderAmounts = calculator.calc();			
				crmOrder.setAmounts(recalcOrderAmounts);
				
				OrderExternalCrm orderExternalCrm = crmOrder.getExternalCrmByCode(CrmTypes.YANDEX_MARKET);
									
				YandexMarketApi yandexMarketApi = new YandexMarketApi(this.environment);
				// поставим в адрес доставки данные из ЯМа				
				Order ymOrder = yandexMarketApi.order(orderExternalCrm.getParentId(), crmOrder); 
				if (ymOrder != null) {
					String addressText = ymOrder.getDelivery().getAddress().getAddress();				
					crmOrder.getDelivery().getAddress().setAddress(addressText);		
				}
				
			}	
			*/			
			int orderId = orderDao.addOrder(crmOrder);			
			orderDao.operateSubstactProductQuantityOrder(crmOrder, OrderStatuses.BID);						
			changeCrmOrderImportStatus(store, orderId, crmOrder);	
						
			Order order = orderDao.findById(orderId);
			if (order.getAdvertType() == OrderAdvertTypes.YANDEX_MARKET && order.getExternalCrmByCode(CrmTypes.YANDEX_MARKET) != null) {
				AnyOrderTotalAmountsCalculator calculator = OrderTotalAmountsCalculatorFactory.createCalculator(order);
				OrderAmounts recalcOrderAmounts = calculator.calc();			
				order.setAmounts(recalcOrderAmounts);
								
				YandexMarketApi yandexMarketApi = new YandexMarketApi(this.environment);
				// поставим в адрес доставки данные из ЯМа
				order.getExternalCrmByCode(CrmTypes.YANDEX_MARKET).getParentId();			
				Order ymOrder = yandexMarketApi.order(order.getExternalCrmByCode(CrmTypes.YANDEX_MARKET).getParentId(), crmOrder); 
				if (ymOrder != null) {
					String addressText = ymOrder.getDelivery().getAddress().getAddress();				
					order.getDelivery().getAddress().setAddress(addressText);					
					customerDao.updateAddress(order.getDelivery().getAddress().getId(), order.getDelivery().getAddress());					
				}	
			}
		}		
		return crmOrders;		
	}

	
	private List<Order> importFromPmCrm() {
	
		Date dateExecute = DateTimeUtils.beforeAnyDate(DateTimeUtils.sysDate(), 3);		
		final String sqlSelectCrmOrders = "SELECT * FROM oc_order WHERE order_status_id in (1, 15) AND date_added >= ?";
				
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);		
		List<Order> crmOrders = jdbcTemplate.query(sqlSelectCrmOrders,
				new Object[] { dateExecute },
				new int[] { Types.DATE },
				new RowMapper<Order>() {
					@Override
					public Order mapRow(ResultSet rs, int rowNum) throws SQLException {
						
						OrderExternalCrm crmYandexMarket = null;
						List<OrderStatusItem> crmOrderStatuses = new ArrayList<OrderStatusItem>();
						
						final Order order = new Order();						
						OrderExternalCrm crmOpenCart = new OrderExternalCrm(order);
				        crmOpenCart.setCrm(CrmTypes.OPENCART);
										
		                order.setId(rs.getInt("ORDER_ID"));
		                //order.setNo(orderDao.nextOrderNo());
		                order.setOrderDate(DateTimeUtils.sysDate());
		                
		                order.setSourceType(OrderSourceTypes.LID);                
		                order.setStore(StoreTypes.PM);
		                if (rs.getString("user_agent").equalsIgnoreCase("Yandex-Modul-OpenCart")) {                	
		                	// это яндекс маркет
		                	crmYandexMarket = new OrderExternalCrm(order);
		            		crmYandexMarket.setCrm(CrmTypes.YANDEX_MARKET);                	                	
		                	order.setAdvertType(OrderAdvertTypes.YANDEX_MARKET);        
		                	// paymentcode PREPAID.YANDEX                	
		                } else {
		                	// заявка оформлена через сайт
		                	order.setAdvertType(OrderAdvertTypes.ADVERT);                
		                }                       
		                order.setAnnotation(rs.getString("COMMENT"));
		                String deliveryAddressText = StringUtils.trim(StringUtils.defaultString(rs.getString("SHIPPING_ZONE")) + ", " + StringUtils.defaultString(rs.getString("SHIPPING_CITY")) + ", " + StringUtils.defaultString(rs.getString("SHIPPING_ADDRESS_1")) + " " + StringUtils.defaultString(rs.getString("SHIPPING_ADDRESS_2")));
		                                               
		                if (rs.getString("PAYMENT_CODE").equals("bank_transfer")) {
		                	order.setOrderType(OrderTypes.BILL);
		                	order.setPaymentType(PaymentTypes.PREPAYMENT);
		                } else if (rs.getString("PAYMENT_CODE").equals("yandex_money")) {
		                	order.setOrderType(OrderTypes.BILL);
		                	order.setPaymentType(PaymentTypes.YANDEX_PAY);
		                } else {
		                	order.setOrderType(OrderTypes.ORDER);
		                	order.setPaymentType(PaymentTypes.POSTPAY);
		                }
		                
		                if (order.getAdvertType() == OrderAdvertTypes.YANDEX_MARKET) {
		                	// отгрузка на пункт яндекс-маркета
		                	order.getDelivery().setDeliveryType(DeliveryTypes.YANDEX_MARKET_FBS);
		                	deliveryAddressText = StringUtils.trim(StringUtils.defaultString(rs.getString("SHIPPING_ADDRESS_1")));                	
		                } else if (rs.getString("SHIPPING_CODE").equalsIgnoreCase("ll_cdek.ll_cdek_136")) {
		                	// сдэк, пвз
		                	order.getDelivery().setDeliveryType(DeliveryTypes.CDEK_PVZ_TYPICAL);
		                	deliveryAddressText = StringUtils.trim(StringUtils.substring(StringUtils.defaultString(rs.getString("SHIPPING_METHOD")), 96));
		                } else if (rs.getString("SHIPPING_CODE").equalsIgnoreCase("ll_cdek.ll_cdek_234")) {
		                	// сдэк, эконом пвз
		                	order.getDelivery().setDeliveryType(DeliveryTypes.CDEK_PVZ_ECONOMY);
		                	deliveryAddressText = StringUtils.trim(StringUtils.substring(StringUtils.defaultString(rs.getString("SHIPPING_METHOD")), 114));
		                } else if (rs.getString("SHIPPING_CODE").equalsIgnoreCase("ll_cdek.ll_cdek_137")) {
		                	// сдэк, курьер
		                	order.getDelivery().setDeliveryType(DeliveryTypes.CDEK_COURIER);
		                	deliveryAddressText = StringUtils.trim(StringUtils.substring(StringUtils.defaultString(rs.getString("SHIPPING_METHOD")), 90));
		                } else if (rs.getString("SHIPPING_CODE").equalsIgnoreCase("ll_cdek.ll_cdek_233")) {
		                	// сдэк, эконом курьер
		                	order.getDelivery().setDeliveryType(DeliveryTypes.CDEK_COURIER_ECONOMY);
		                	deliveryAddressText = StringUtils.trim(StringUtils.substring(StringUtils.defaultString(rs.getString("SHIPPING_METHOD")), 114));
		                } else if (rs.getString("SHIPPING_CODE").equalsIgnoreCase("ll_ozon.ll_ozon_pickpoint")) {
		                	// озон рокет 
		                	order.getDelivery().setDeliveryType(DeliveryTypes.OZON_ROCKET_PICKPOINT);              	
		                } else if (rs.getString("SHIPPING_CODE").equalsIgnoreCase("russianpost2.rp1")) {
		                	// почта, обычное отправление
		                	order.getDelivery().setDeliveryType(DeliveryTypes.POST_TYPICAL);              	
		                } else if (rs.getString("SHIPPING_CODE").equalsIgnoreCase("russianpost2f1.rp1")) {
		                	// почта, отправление 1-й класс
		                	order.getDelivery().setDeliveryType(DeliveryTypes.POST_I_CLASS);              	
		                } else if (rs.getString("SHIPPING_CODE").equalsIgnoreCase("russianpost2f2.rp1")) {
		                	// почта, отправление ems
		                	order.getDelivery().setDeliveryType(DeliveryTypes.POST_EMS);              	
		                } else if (rs.getString("SHIPPING_METHOD").equals("Доставим курьером 1-2 дня")) {
		                	order.getDelivery().setDeliveryType(DeliveryTypes.COURIER_MOSCOW_TYPICAL);                	
		                } else if (StringUtils.contains(rs.getString("SHIPPING_METHOD"), "Курьер, сегодня")) {
		                	order.getDelivery().setDeliveryType(DeliveryTypes.COURIER_MOSCOW_FAST);
		                } else if (StringUtils.contains(rs.getString("SHIPPING_METHOD"), "Самовывоз")) {
		                	order.getDelivery().setDeliveryType(DeliveryTypes.PICKUP);
		                } else {
		                	order.getDelivery().setDeliveryType(DeliveryTypes.CDEK_PVZ_TYPICAL);
		                }
		                Address deliveryAddress = new Address(Countries.RUSSIA, AddressTypes.MAIN);
		                deliveryAddress.setAddress(deliveryAddressText);
		                order.getDelivery().setAddress(deliveryAddress);
		                
		                if (order.getAdvertType() == OrderAdvertTypes.YANDEX_MARKET) {                	
		                	order.getDelivery().getAddress().getCarrierInfo().getCourierInfo().setDeliveryDate(DateTimeUtils.timestampToDate(rs.getTimestamp("SHIPMENT_DATE")));                	
		                }
		                
		                order.getDelivery().setPaymentDeliveryType(PaymentDeliveryTypes.CUSTOMER);		                
		                order.getDelivery().setTrackCode("");		                               
		               
		                order.setProductCategory(wikiDao.getCategoryById(0));
		                
		                order.getAmounts().setValue(OrderAmountTypes.TOTAL, rs.getBigDecimal("TOTAL"));
		                order.getAmounts().setValue(OrderAmountTypes.TOTAL_WITH_DELIVERY, rs.getBigDecimal("TOTAL"));

		                order.setStatus(OrderStatuses.BID);                
		                if (order.getAdvertType() == OrderAdvertTypes.YANDEX_MARKET) { 
		                	
		                	final String sqlSelectCrmOrderStatuses = "select * from oc_order_history"
		                			+ " where order_id = ? and order_status_id > 0 and market_status IS NOT NULL"
		                			+ " order by 1";   
		                	JdbcTemplate jdbcTemplateStatuses = new JdbcTemplate(dataSource);
		                	crmOrderStatuses = jdbcTemplateStatuses.query(sqlSelectCrmOrderStatuses,
		            				new Object[] { order.getId() },
		            				new int[] { Types.INTEGER },		            				
		            				new RowMapper<OrderStatusItem>() {
		            					@Override
		            		            public OrderStatusItem mapRow(ResultSet rsStatuses, int rowNum) throws SQLException {
		            						OrderStatusItem orderStatusItemI = new OrderStatusItem(order);
		    		        				orderStatusItemI.setStatus(OrderStatuses.BID);
		    		                     	orderStatusItemI.setCrmStatus(rsStatuses.getString("MARKET_STATUS"));
		    		                     	orderStatusItemI.setCrmSubStatus(rsStatuses.getString("MARKET_SUB_STATUS"));
		    		                     	orderStatusItemI.setAddedDate(DateTimeUtils.timestampToDate(rsStatuses.getTimestamp("DATE_ADDED")));
		    		                     	if (StringUtils.isEmpty(rs.getString("MARKET_STATUS"))) {
		    		                    		orderStatusItemI.setStatus(OrderStatuses.BID);
		    		                    		orderStatusItemI.setCrmStatus("NONE");
		    		                        	orderStatusItemI.setCrmSubStatus("NONE");                    		
		    		                    	} else if (rsStatuses.getString("MARKET_STATUS").trim().equalsIgnoreCase("PROCESSING")) {
		    		                    		orderStatusItemI.setStatus(OrderStatuses.BID);
		    		                    	} else if (rsStatuses.getString("MARKET_STATUS").trim().equalsIgnoreCase("CANCELLED")) {
		    		                    		orderStatusItemI.setStatus(OrderStatuses.CANCELED);
		    		                    	} else if (rsStatuses.getString("MARKET_STATUS").trim().equalsIgnoreCase("UNPAID")) {
		    		                    		orderStatusItemI.setStatus(OrderStatuses.BID);
		    		                    	}
		            		                return orderStatusItemI;
		            		            }
		            		        });
		       
		        			if (crmOrderStatuses.size() == 0) {
		        				OrderStatusItem orderStatusItemI = new OrderStatusItem(order);
		        				orderStatusItemI.setStatus(OrderStatuses.BID);
		                    	orderStatusItemI.setCrmStatus(rs.getString("MARKET_STATUS"));
		                    	orderStatusItemI.setCrmSubStatus(rs.getString("MARKET_SUB_STATUS")); 
		                    	
		                    	if (StringUtils.isEmpty(rs.getString("MARKET_STATUS"))) {
		                    		orderStatusItemI.setStatus(OrderStatuses.BID);
		                    		orderStatusItemI.setCrmStatus("NONE");
		                        	orderStatusItemI.setCrmSubStatus("NONE");                    		
		                    	} else if (rs.getString("MARKET_STATUS").trim().equalsIgnoreCase("PROCESSING")) {
		                    		orderStatusItemI.setStatus(OrderStatuses.BID);
		                    	} else if (rs.getString("MARKET_STATUS").trim().equalsIgnoreCase("CANCELLED")) {
		                    		orderStatusItemI.setStatus(OrderStatuses.CANCELED);
		                    	} else if (rs.getString("MARKET_STATUS").trim().equalsIgnoreCase("UNPAID")) {
		                    		orderStatusItemI.setStatus(OrderStatuses.BID);
		                    	}
		                    	crmOrderStatuses.add(orderStatusItemI);     
		        			}   	            		
		            		crmYandexMarket.setParentId(rs.getLong("MARKET_ORDER_ID"));
		                }                
		                Customer customer = new Customer();						
						customer.setCountry(Countries.RUSSIA);				
						customer.setFirstName(rs.getString("FIRSTNAME"));
						customer.setLastName(rs.getString("LASTNAME"));
						customer.setPhoneNumber(TextUtils.formatPhoneNumber(rs.getString("TELEPHONE")));
						String email = rs.getString("EMAIL");
						if (StringUtils.contains(email, "@localhost")) {
							customer.setEmail("");	
						} else {
							customer.setEmail(email);							
						}						
						Address customerAddress = customer.getMainAddress();
						if (StringUtils.isNoneEmpty(rs.getString("PAYMENT_CITY")) || StringUtils.isNoneEmpty(rs.getString("PAYMENT_ADDRESS_1"))) {
							customerAddress.setAddress(StringUtils.trim(StringUtils.defaultString(rs.getString("PAYMENT_CITY")) + " " + StringUtils.defaultString(rs.getString("PAYMENT_ADDRESS_1")) + " " + StringUtils.defaultString(rs.getString("PAYMENT_ADDRESS_2"))));	
						}						
		                order.setCustomer(customer);                
		           		                
		                crmOpenCart.setId(rs.getInt("ORDER_ID"));                
		                crmOpenCart.setParentId(rs.getLong("ORDER_ID"));
		                order.getExternalCrms().add(crmOpenCart);
		                                
		                if (crmYandexMarket != null && rs.getInt("MARKET_ORDER_ID") > 0) {
		                	crmYandexMarket.setId(rs.getInt("MARKET_ORDER_ID"));
		                	crmYandexMarket.setParentId(rs.getLong("MARKET_ORDER_ID"));
		                	order.getExternalCrms().add(crmYandexMarket);
		                }       
		                
		                Order orderAm = setPmCrmOrderAmount(order);
		                order.setAmounts(orderAm.getAmounts());		                
		                order.getDelivery().setPrice(orderAm.getDelivery().getPrice());
		        		order.getDelivery().setFactCustomerPrice(orderAm.getDelivery().getFactCustomerPrice());
		        		order.getDelivery().setFactSellerPrice(orderAm.getDelivery().getFactSellerPrice());
		                		                
		           
		                order.setItems(setPmCrmOrderItems(order));
		                		                
		                order.setAnnotation(setPmCrmOrderOptions(order));
		                
		                if (crmOrderStatuses.size() > 0) {
		                	order.setStatuses(crmOrderStatuses);
		                }                
			
						return order;
					}
				});
		return crmOrders;
	}
	
	private String setPmCrmOrderOptions(Order crmOrder) {
		
		final String sqlSelectCrmOrderOptions = "SELECT * FROM oc_order_option WHERE order_id = ?";				
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);		
		List<Product> optionProducts = jdbcTemplate.query(sqlSelectCrmOrderOptions,
				new Object[] { crmOrder.getId() },
				new int[] { Types.INTEGER },
				new RowMapper<Product>() {
					@Override
		            public Product mapRow(ResultSet rs, int rowNum) throws SQLException {
						Product optionProduct = wikiDao.getProductById(rs.getInt("ORDER_PRODUCT_ID"));
						String option = optionProduct.getSku() + " " + rs.getString("NAME") + ": " + rs.getString("VALUE");
						optionProduct.getStore().setDescription(option);
					
		                return optionProduct;
		            }
		        });
		
		String options = "";
		for (Product optionProduct : optionProducts) {
			options += optionProduct.getStore().getDescription();			
		}
		if (StringUtils.isEmpty(crmOrder.getAnnotation())) {
			return options;
		} else {
			return crmOrder.getAnnotation() + ", " + options;			
		}		
		
	}	

	private Order setPmCrmOrderAmount(Order crmOrder) {
		
		final String sqlSelectCrmAmounts = "SELECT * FROM oc_order_total WHERE order_id = ? ORDER BY order_total_id";
					
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);		
		List<Map<String, BigDecimal>> crmOrderAmounts = jdbcTemplate.query(sqlSelectCrmAmounts,
				new Object[] { crmOrder.getId() },
				new int[] { Types.INTEGER },
				new RowMapper<Map<String, BigDecimal>>() {
					@Override
		            public Map<String, BigDecimal> mapRow(ResultSet rs, int rowNum) throws SQLException {
						
						Map<String, BigDecimal> record = new HashMap<String, BigDecimal>();
						record.put(rs.getString("CODE"), rs.getBigDecimal("VALUE"));
						return record;
		            }
		        });
		
		for (Map<String, BigDecimal> crmOrderAmount : crmOrderAmounts) {
			BigDecimal value;
			value = crmOrderAmount.get("sub_total");
			if (value != null) {
				crmOrder.getAmounts().setTotal(value);
			}
			value = crmOrderAmount.get("shipping");
			if (value != null) {
				crmOrder.getAmounts().setDelivery(value);
			}
			value = crmOrderAmount.get("cash_on_delivery");
			if (value != null) {
				crmOrder.getAmounts().setCashOnDelivery(value);
			}			
			value = crmOrderAmount.get("total");
			if (value != null) {
				crmOrder.getAmounts().setTotalWithDelivery(value);
			}
		}	
		BigDecimal delivery = BigDecimal.ZERO;
		if (crmOrder.getAmounts().getDelivery() != null && crmOrder.getAmounts().getCashOnDelivery() != null) {			
			delivery = crmOrder.getAmounts().getDelivery().add(crmOrder.getAmounts().getCashOnDelivery());
		}
		crmOrder.getDelivery().setPrice(delivery);
		crmOrder.getDelivery().setFactCustomerPrice(delivery);
		crmOrder.getDelivery().setFactSellerPrice(delivery);
		
		return crmOrder;
	}
		
	private List<OrderItem> setPmCrmOrderItems(Order crmOrder) {
		
		final String sqlSelectCrmOrderProduct = "SELECT * FROM oc_order_product WHERE order_id = ? ORDER BY order_product_id";		
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);		
		List<OrderItem> orderItems = jdbcTemplate.query(sqlSelectCrmOrderProduct,
				new Object[] { crmOrder.getId() },
				new int[] { Types.INTEGER },
				new RowMapper<OrderItem>() {
					@Override
		            public OrderItem mapRow(ResultSet rs, int rowNum) throws SQLException {
						
						OrderItem orderItem = new OrderItem(crmOrder);
						orderItem.setNo(rs.getInt("ORDER_PRODUCT_ID"));						
						Product product = wikiDao.getProductById(rs.getInt("PRODUCT_ID"));
						orderItem.setProduct(product);
						orderItem.setSupplierAmount(product.getSupplierPrice());						
						orderItem.setQuantity(rs.getInt("QUANTITY"));
						orderItem.setPrice(rs.getBigDecimal("PRICE"));
						orderItem.setAmount(rs.getBigDecimal("TOTAL"));						
						crmOrder.setProductCategory(orderItem.getProduct().getCategory());
						return orderItem;
		            }
		        });
		return orderItems;
	} 
	
	private void changeCrmOrderImportStatus(StoreTypes store, int orderId, Order crmOrder) {
		orderDao.addCrmOrderImport(orderId, crmOrder);
		changeCrmOrderImportStatusSource(store, orderId, crmOrder);		
	}
	
	private void changeCrmOrderImportStatusSource(StoreTypes store, int orderId, Order crmOrder) {
		
		final String sqUpdateParentCrmStatus = "UPDATE oc_order SET order_status_id = 17 WHERE order_id = ?";		
		for (OrderExternalCrm externalCrm : crmOrder.getExternalCrms()) {
			if (externalCrm.getCrm() == CrmTypes.OPENCART) {
									
				JdbcTemplate jdbcTemplateUpdate = new JdbcTemplate(dataSource);
				jdbcTemplateUpdate.update(sqUpdateParentCrmStatus, new Object[] { externalCrm.getParentId() });	
							
			}
		}			
	}
}
