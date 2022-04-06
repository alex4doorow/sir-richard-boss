package ru.sir.richard.boss.crm;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.xml.sax.SAXException;

import ru.sir.richard.boss.api.cdek.CdekApi;
import ru.sir.richard.boss.api.market.OzonMarketApi;
import ru.sir.richard.boss.api.market.OzonRocketApi;
import ru.sir.richard.boss.api.postcalc.PostcalcApi;
import ru.sir.richard.boss.dao.AnyDaoImpl;
import ru.sir.richard.boss.dao.OrderDao;
import ru.sir.richard.boss.dao.WikiDao;
import ru.sir.richard.boss.model.data.Address;
import ru.sir.richard.boss.model.data.CarrierInfo;
import ru.sir.richard.boss.model.data.ForeignerCompanyCustomer;
import ru.sir.richard.boss.model.data.Order;
import ru.sir.richard.boss.model.data.OrderExternalCrm;
import ru.sir.richard.boss.model.data.OrderItem;
import ru.sir.richard.boss.model.data.OrderStatusItem;
import ru.sir.richard.boss.model.data.Product;
import ru.sir.richard.boss.model.data.conditions.OrderConditions;
import ru.sir.richard.boss.model.data.crm.CdekOrderBean;
import ru.sir.richard.boss.model.data.crm.CdekOrderItemBean;
import ru.sir.richard.boss.model.data.crm.DeliveryServiceResult;
import ru.sir.richard.boss.model.types.CarrierStatuses;
import ru.sir.richard.boss.model.types.CrmStatuses;
import ru.sir.richard.boss.model.types.CrmTypes;
import ru.sir.richard.boss.model.types.DeliveryPrices;
import ru.sir.richard.boss.model.types.DeliveryTypes;
import ru.sir.richard.boss.model.types.OrderAdvertTypes;
import ru.sir.richard.boss.model.types.OrderStatuses;
import ru.sir.richard.boss.model.types.OrderTypes;
import ru.sir.richard.boss.model.types.PaymentDeliveryMethods;
import ru.sir.richard.boss.model.types.PaymentDeliveryTypes;
import ru.sir.richard.boss.model.types.PaymentTypes;
import ru.sir.richard.boss.model.types.ReportPeriodTypes;
import ru.sir.richard.boss.model.utils.DateTimeUtils;
import ru.sir.richard.boss.model.utils.Pair;
import ru.sir.richard.boss.model.utils.SingleExecutor;
import ru.sir.richard.boss.model.utils.sender.MessageManager;

@Repository
public class DeliveryServiceImpl extends AnyDaoImpl implements DeliveryService {
		
	private final Logger logger = LoggerFactory.getLogger(DeliveryService.class);

	@Autowired
	private WikiDao wikiDao;
	
	@Autowired
	private OrderDao orderDao;
	
	@Override
	public String addCdekParcelOrder(Order order) {
		DeliveryTypes deliveryType = order.getDelivery().getDeliveryType();
		String trackCode = "";
		if (deliveryType.isСdek()) {
			CdekApi cdek = new CdekApi();			
			// добавляем накладную в сдэк
			
			CdekOrderBean cdekOrderBean = convert4CdekCrmExportBean(order);
						
			trackCode = cdek.addOrder(cdekOrderBean, CdekApi.getCdekTariffId(order.getDelivery().getDeliveryType()), calcTotalWeightG(order));
			// обновляем трэккод на нашем заказе
			if (StringUtils.isNoneEmpty(trackCode)) {
				orderDao.changeStatusOrder(order.getId(), order.getStatus(), order.getAnnotation(), trackCode, null);
			}	
		}	
		return trackCode;
	}
		
	@Override
	public String addOzonRocketParcelOrder(Order order) {		
		DeliveryTypes deliveryType = order.getDelivery().getDeliveryType();
		String trackCode = "";
		if (deliveryType.isOzonRocket()) {
			OzonRocketApi ozonRocketApi = new OzonRocketApi();	
			// добавляем накладную в озон рокет
			trackCode = ozonRocketApi.addOrder(order);
			// обновляем трэккод на нашем заказе
			if (StringUtils.isNoneEmpty(trackCode)) {
				orderDao.changeStatusOrder(order.getId(), order.getStatus(), order.getAnnotation(), trackCode, null);
			}	
		}	
		return trackCode;		
	}
	
	@Override
	public DeliveryServiceResult calc(Order order, BigDecimal totalAmount, DeliveryTypes deliveryType, Address to) {
		
		BigDecimal MOSCOW_PARCEL_DELIVERY_PRICE = BigDecimal.valueOf(200);		
		BigDecimal MOSCOW_PICKUP_DELIVERY_PRICE = BigDecimal.valueOf(170);
		BigDecimal MIN_GOOD_MOSCOW_PARCEL_IS_FREE = BigDecimal.valueOf(3000);
		BigDecimal MIN_GOOD_MOSCOW_COURIER_IS_FREE = BigDecimal.valueOf(10000);
		int MOSCOW_CITY_ID = 44;
		
		boolean isDmcPvz = false;
		boolean isDmcEconomyPvz = false;
		
		DeliveryServiceResult result = null;
		
		if (to.getCarrierInfo().getCityId() == MOSCOW_CITY_ID) {
			isDmcPvz = true;
			isDmcEconomyPvz = false;
		} else {			
			DeliveryServiceResult fakeResultEconomy;
			try {
				fakeResultEconomy = cdekCalc(order, totalAmount, DeliveryTypes.CDEK_PVZ_ECONOMY, to);
				if ("Невозможно осуществить доставку по этому направлению при заданных условиях".equalsIgnoreCase(fakeResultEconomy.getErrorText())) {
					isDmcPvz = true;
					isDmcEconomyPvz = false;
				} else {
					isDmcPvz = false;
					isDmcEconomyPvz = true;			
				}
			} catch (Exception e1) {
				logger.error("Exception", e1);
			}	
		}
		
		Product etalonProduct = null;
		PaymentDeliveryMethods paymentDeliveryMethod = PaymentDeliveryMethods.FULL;
		for (OrderItem item : order.getItems()) {
			etalonProduct = wikiDao.getProductById(item.getProduct().getId());
			break;
		}
		if (etalonProduct != null) {
			paymentDeliveryMethod = etalonProduct.getDeliveryMethod();
		}		
								
		if (deliveryType == DeliveryTypes.COURIER_MOSCOW_TYPICAL) {			
			BigDecimal deliveryPrice = DeliveryPrices.COURIER_MOSCOW_TYPICAL.getPrice();
			if (totalAmount.compareTo(MIN_GOOD_MOSCOW_COURIER_IS_FREE) > 0) {
				deliveryPrice = BigDecimal.ZERO;				
			}						
			result = new DeliveryServiceResult();												
			result.setDeliveryPrice(deliveryPrice);
			result.setDeliveryFullPrice(deliveryPrice);
			result.setDeliverySellerSummary(BigDecimal.ZERO);
			result.setDeliveryCustomerSummary(deliveryPrice);
			result.setTo("курьер, Москва");
			result.setTermText("1-2 дня");						
			result.setWeightText(calcTotalWeightKg(order).toPlainString() + " кг.");			
			return result;			
			
		} else if (deliveryType == DeliveryTypes.COURIER_MOSCOW_FAST) {			
			BigDecimal deliveryPrice = DeliveryPrices.COURIER_MOSCOW_FAST.getPrice();									
			result = new DeliveryServiceResult();												
			result.setDeliveryPrice(deliveryPrice);
			result.setDeliveryFullPrice(deliveryPrice);
			result.setDeliverySellerSummary(BigDecimal.ZERO);
			result.setDeliveryCustomerSummary(deliveryPrice);
			result.setTo("курьер, Москва");
			result.setTermText("сегодня");						
			result.setWeightText(calcTotalWeightKg(order).toPlainString() + " кг.");
			return result;	
			
		} if (deliveryType == DeliveryTypes.COURIER_MO_TYPICAL) {			
			BigDecimal deliveryPrice = order.getDelivery().getDeliveryPrice().getPrice();										
			result = new DeliveryServiceResult();												
			result.setDeliveryPrice(deliveryPrice);
			result.setDeliveryFullPrice(deliveryPrice);
			result.setDeliverySellerSummary(BigDecimal.ZERO);
			result.setDeliveryCustomerSummary(deliveryPrice);
			result.setTo("курьер, Подмосковье");
			result.setTermText(order.getDelivery().getDeliveryPrice().getAnnotation());						
			result.setWeightText(calcTotalWeightKg(order).toPlainString() + " кг.");
			return result;			
		} else if (deliveryType == DeliveryTypes.PICKUP) {			
			BigDecimal deliveryPrice = MOSCOW_PICKUP_DELIVERY_PRICE;
			if (totalAmount.compareTo(MIN_GOOD_MOSCOW_PARCEL_IS_FREE) > 0) {
				deliveryPrice = BigDecimal.ZERO;				
			}						
			result = new DeliveryServiceResult();												
			result.setDeliveryPrice(deliveryPrice);
			result.setDeliveryFullPrice(deliveryPrice);
			result.setDeliverySellerSummary(MOSCOW_PICKUP_DELIVERY_PRICE);
			result.setDeliveryCustomerSummary(deliveryPrice);
			result.setTo(deliveryType.getAnnotation());
			result.setTermText("сегодня");						
			result.setWeightText(calcTotalWeightKg(order).toPlainString() + " кг.");
			return result;	
			
		} else if (deliveryType == DeliveryTypes.DELLIN) {
			result = new DeliveryServiceResult();												
			result.setDeliveryPrice(BigDecimal.ZERO);
			result.setDeliveryFullPrice(BigDecimal.ZERO);
			result.setDeliverySellerSummary(BigDecimal.ZERO);
			result.setDeliveryCustomerSummary(BigDecimal.ZERO);
			result.setTo(deliveryType.getAnnotation());
			result.setTermText("уточнить на www.dellin.ru");						
			result.setWeightText(calcTotalWeightKg(order).toPlainString() + " кг.");
			return result;	
			
		} else if (deliveryType.isPost()) {
			try {
				return postCalc(order, totalAmount, deliveryType, to);
			} catch (IOException e) {
				logger.error("IOException", e);
			}			
		} else if (deliveryType.isСdek()) {
			try {
				result = cdekCalc(order, totalAmount, deliveryType, to);
								
				BigDecimal deliveryPrice = result.getDeliverySellerSummary();
				if (order.getDelivery().getPaymentDeliveryType() == PaymentDeliveryTypes.CUSTOMER) {
					
					if (order.getPaymentType() == PaymentTypes.POSTPAY) {					    
					    if (paymentDeliveryMethod == PaymentDeliveryMethods.FULL) {
					        // FULL
					    	deliveryPrice = result.getDeliverySellerSummary();	
					        
					    } else if (paymentDeliveryMethod == PaymentDeliveryMethods.PVZ_FREE) {
					        // PVZ
					    	deliveryPrice = result.getDeliverySellerSummary();	
					        
					    } else {
					        // CURRENT
					    	deliveryPrice = result.getDeliveryCustomerSummary();
					    }
					    
					} else {
						deliveryPrice = result.getDeliveryPrice();
					}
				}
				
				if (deliveryType == DeliveryTypes.CDEK_PVZ_TYPICAL && to.getCarrierInfo().getCityId() == MOSCOW_CITY_ID) {					
					
					if (paymentDeliveryMethod == PaymentDeliveryMethods.FULL) {
						//deliveryPrice = result.getDeliverySellerSummary();	
						
				        if (deliveryPrice.compareTo(MOSCOW_PARCEL_DELIVERY_PRICE) < 0) {
				        	deliveryPrice = MOSCOW_PARCEL_DELIVERY_PRICE;
				        }
				        
				    } else if (paymentDeliveryMethod == PaymentDeliveryMethods.CURRENT) {				    	
				    	deliveryPrice = MOSCOW_PARCEL_DELIVERY_PRICE;
				        if (totalAmount.compareTo(MIN_GOOD_MOSCOW_PARCEL_IS_FREE) > 0) {
							deliveryPrice = BigDecimal.ZERO;				
						}	
				    }					
					result.setDeliveryFullPrice(deliveryPrice);
				} else if (deliveryType == DeliveryTypes.CDEK_COURIER && to.getCarrierInfo().getCityId() == MOSCOW_CITY_ID) {
					deliveryPrice = DeliveryPrices.COURIER_MOSCOW_TYPICAL.getPrice();
					if (totalAmount.compareTo(MIN_GOOD_MOSCOW_COURIER_IS_FREE) > 0) {
						deliveryPrice = BigDecimal.ZERO;				
					}
					result.setDeliveryFullPrice(deliveryPrice);
					result.setDeliveryCustomerSummary(deliveryPrice);											
				} else if (deliveryType == DeliveryTypes.CDEK_PVZ_TYPICAL && isDmcPvz) {
					if (paymentDeliveryMethod == PaymentDeliveryMethods.PVZ_FREE) {	
						deliveryPrice = BigDecimal.ZERO;
					}		
					
				} else if (deliveryType == DeliveryTypes.CDEK_PVZ_ECONOMY && isDmcEconomyPvz) {
					if (paymentDeliveryMethod == PaymentDeliveryMethods.PVZ_FREE) {	
						deliveryPrice = BigDecimal.ZERO;
					}
				}
				result.setDeliveryFullPrice(deliveryPrice);
				result.setDeliveryCustomerSummary(deliveryPrice);
				return result;
				
			} catch (Exception e) {
				logger.error("Exception", e);
			}
			
		} else if (deliveryType.isOzonRocket()) {
						
			result = ozonRocketCalc(order, totalAmount, deliveryType, to);								
			BigDecimal deliveryPrice = result.getDeliverySellerSummary();
			if (order.getDelivery().getPaymentDeliveryType() == PaymentDeliveryTypes.CUSTOMER) {					
				if (order.getPaymentType() == PaymentTypes.POSTPAY) {					    
				    if (paymentDeliveryMethod == PaymentDeliveryMethods.FULL) {
				        // FULL
				    	deliveryPrice = result.getDeliverySellerSummary();					        
				    } else if (paymentDeliveryMethod == PaymentDeliveryMethods.PVZ_FREE) {
				        // PVZ
				    	deliveryPrice = result.getDeliverySellerSummary();
				    } else {
				        // CURRENT
				    	deliveryPrice = result.getDeliveryCustomerSummary();
				    }
				    
				} else {
					deliveryPrice = result.getDeliveryPrice();
				}
			} 
			result.setDeliveryFullPrice(deliveryPrice);
			result.setDeliveryCustomerSummary(deliveryPrice);
			return result;
			
		} else {
			return null;
		}
		return null;		
	}
	
	@Override
	public List<Address> getCdekCities(String city) {
		
		final String cityConditions = city;
		final String sqlCdekCities = "SELECT * FROM oc_cdek_city"
				+ " WHERE cityName LIKE " + cityConditions
				+ " ORDER BY regionName, id";		 
		List<Address> cdekCities = this.jdbcTemplate.query(sqlCdekCities,
				new Object[]{},
				new RowMapper<Address>() {
					@Override
		            public Address mapRow(ResultSet rs, int rowNum) throws SQLException {						
		            	Address address = new Address();		            	
		            	address.getCarrierInfo().setCityId(rs.getInt("ID"));
		            	address.getCarrierInfo().setCityContext(rs.getString("CITYNAME"));
		            	address.getCarrierInfo().setRegion(rs.getString("REGIONNAME"));
		            	address.setAddress(rs.getString("NAME"));
		            	return address;
		            }
		        });
		return cdekCities; 
	}
	
	@Override
	public List<Address> getCdekPvzs(int cityId) {		
		CdekApi cdek = new CdekApi();
		List<Address> pvzs = new ArrayList<Address>();
		
		try {
			List<CarrierInfo> cdekPvzs = cdek.getPvzs(cityId);
			for (CarrierInfo cdekPvz : cdekPvzs) {
				Address pvz = new Address();
				pvz.setCarrierInfo(cdekPvz);
				pvzs.add(pvz);
			}
		} catch (IOException e) {
			logger.error("IOException:", e);
		} catch (SAXException e) {
			logger.error("SAXException:", e);			
		} catch (ParserConfigurationException e) {
			logger.error("ParserConfigurationException:", e);			
		}
		return pvzs;
	}
	
	@Override
	public List<Address> getCdekPvz(int cityId, String pvzCode) {
		
		CdekApi cdek = new CdekApi();
		List<Address> pvzs = new ArrayList<Address>();		
		try {
			CarrierInfo cdekPvz = cdek.getPvz(cityId, pvzCode);
			if (cdekPvz != null && StringUtils.isNotEmpty(cdekPvz.getPvz())) {
				Address pvz = new Address();
				pvz.setCarrierInfo(cdekPvz);
				pvzs.add(pvz);
			}		
			
		} catch (IOException e) {
			logger.error("IOException", e);
		} catch (SAXException e) {
			logger.error("SAXException", e);
		} catch (ParserConfigurationException e) {
			logger.error("ParserConfigurationException", e);
		}
		return pvzs;
		
	}
	
	
	/*
	@Override
	public String ordersSendFeedback(Date dateStart) {
		// --> OrderService
		List<Order> ordersForFeedback = orderDao.listOrdersForFeedback(dateStart);
		
		logger.debug("");
		String debugInfoItem = "";
		String debugInfo = "";
		int deliveredCount = 0;

		MessageManager messageManager = new MessageManager();
		for (Order orderForFeedback : ordersForFeedback) {
						
			Order currentOrder = orderDao.findById(orderForFeedback.getId());
			messageManager.sendOrderFeedback(currentOrder, OrderEmailStatuses.FEEDBACK);			
			orderDao.changeEmailStatusOrder(orderForFeedback.getId(), OrderEmailStatuses.FEEDBACK);
											
			debugInfoItem = "- запрос на отзыв: " + currentOrder.getNo() + ", " + currentOrder.getCustomer().getViewShortName() + ", " + currentOrder.getDelivery().getAddress().getAddress();
			debugInfo = debugInfo + debugInfoItem + "<br>";
			logger.debug("запрос на отзыв: {}, {}, {}", currentOrder.getNo(), currentOrder.getCustomer().getViewShortName(), currentOrder.getDelivery().getAddress().getAddress());
			deliveredCount++;
		}
		
		int changedCount = deliveredCount;
		String result = String.format("Всего отправлено запросов на отзыв: %d", changedCount);
		if (changedCount > 0) {
			result = result + ":<br>" + debugInfo;
		}		
		return result;
	}
	*/
	
	@Override
	public String scheduledOrdersStatusesReload() {
		if (SingleExecutor.DELIVERY_STATUS_CHANGE) {
			return "";			
		} 
		return ordersStatusesReload();
	}
	
	public String ordersOzonRoketStatusesReload() {
		return null;
	}
	
	@Override
	public String ordersStatusesReload() {
		SingleExecutor.DELIVERY_STATUS_CHANGE = true;
								
		CdekApi cdek = new CdekApi();
		MessageManager messageManager = new MessageManager();
		
		List<Order> cdekModifiedOrders = null;
		// условия по отбору: isSdek статусы: подтвержден, отправлен, прибыли и долго не забирают
		//List<Order> orders = orderDao.listOrdersByConditions(orderCondition)
		
		OrderConditions conditions = new OrderConditions();
		Set<OrderStatuses> statuses = new HashSet<OrderStatuses>();
		statuses.add(OrderStatuses.APPROVED);
		statuses.add(OrderStatuses.DELIVERING);
		statuses.add(OrderStatuses.READY_GIVE_AWAY);
		statuses.add(OrderStatuses.READY_GIVE_AWAY_TROUBLE);
		statuses.add(OrderStatuses.PAY_ON);		
		conditions.setStatuses(statuses);
				
		Set<DeliveryTypes> deliveryTypes = new HashSet<DeliveryTypes>();
		deliveryTypes.add(DeliveryTypes.CDEK_COURIER);
		deliveryTypes.add(DeliveryTypes.CDEK_COURIER_ECONOMY);
		deliveryTypes.add(DeliveryTypes.CDEK_PVZ_ECONOMY);
		deliveryTypes.add(DeliveryTypes.CDEK_PVZ_TYPICAL);
		deliveryTypes.add(DeliveryTypes.PICKUP);
		conditions.setDeliveryTypes(deliveryTypes);
		
		conditions.setPeriodExist(false);
		conditions.setTrackCodeNotExist(false);		
		//conditions.setTrackCode("1118343441");		
		
		List<Order> orders = orderDao.listOrdersByConditions(conditions);
		
		List<Order> willModifingOrders = new ArrayList<Order>();
		int deliveringCount = 0;
		int readyGiveAwayCount = 0;
		int deliveredCount = 0;
		int cancelledCount = 0;
		String debugInfo = "";
								
		try {
			// cdek
			cdekModifiedOrders = cdek.getStatuses(orders);			
			updateCrmCdekMarketConnect(cdekModifiedOrders);			
			logger.debug("cdek:");
			String debugInfoItem = "";			
			for (Order currentStatusOrder : orders) {
				Order cdekModifiedOrder = findModifiedOrderByTrackCode(cdekModifiedOrders, currentStatusOrder);
				if (cdekModifiedOrder != null) {
					OrderStatuses currentStatus = currentStatusOrder.getStatus();
					CarrierStatuses modifiedSdekStatus = cdekModifiedOrder.getDelivery().getCarrierStatus();
					
					if (currentStatus == OrderStatuses.APPROVED && modifiedSdekStatus.getOrderStatus() == OrderStatuses.DELIVERING) {
						// посылка прибыла в сдэк на отправку и поехала --> DELIVERING
						currentStatusOrder.setStatus(OrderStatuses.DELIVERING);
						willModifingOrders.add(currentStatusOrder);
						
						debugInfoItem = "- доставляется: " + currentStatusOrder.getNo() + ", " + currentStatusOrder.getCustomer().getViewShortName() + ", " + currentStatusOrder.getDelivery().getAddress().getAddress();
						debugInfo = debugInfo + debugInfoItem + "<br>";						
						logger.debug("доставляется: {}, {}, {}", currentStatusOrder.getNo(), currentStatusOrder.getCustomer().getViewShortName(), currentStatusOrder.getDelivery().getAddress().getAddress());
						deliveringCount++;
					} else if (currentStatus == OrderStatuses.PAY_ON && modifiedSdekStatus.getOrderStatus() == OrderStatuses.DELIVERING) {
						// посылка по предоплате прибыла в сдэк на отправку и поехала --> DELIVERING
						currentStatusOrder.setStatus(OrderStatuses.DELIVERING);
						willModifingOrders.add(currentStatusOrder);
						
						debugInfoItem = "- доставляется: " + currentStatusOrder.getNo() + ", " + currentStatusOrder.getCustomer().getViewShortName() + ", " + currentStatusOrder.getDelivery().getAddress().getAddress();
						debugInfo = debugInfo + debugInfoItem + "<br>";
						logger.debug("доставляется: {}, {}, {}", currentStatusOrder.getNo(), currentStatusOrder.getCustomer().getViewShortName(), currentStatusOrder.getDelivery().getAddress().getAddress());
						deliveringCount++;
					} else if (currentStatus == OrderStatuses.APPROVED && modifiedSdekStatus.getOrderStatus() == OrderStatuses.READY_GIVE_AWAY) {
						// посылка готова к выдаче --> READY_GIVE_AWAY
						currentStatusOrder.setStatus(OrderStatuses.READY_GIVE_AWAY);
						willModifingOrders.add(currentStatusOrder);
						
						debugInfoItem = "- прибыл: " + currentStatusOrder.getNo() + ", " + currentStatusOrder.getCustomer().getViewShortName() + ", " + currentStatusOrder.getDelivery().getAddress().getAddress();
						debugInfo = debugInfo + debugInfoItem + "<br>";
						logger.debug("прибыл: {}, {}, {}", currentStatusOrder.getNo(), currentStatusOrder.getCustomer().getViewShortName(), currentStatusOrder.getDelivery().getAddress().getAddress());
						readyGiveAwayCount++;
					} else if (currentStatus == OrderStatuses.APPROVED && modifiedSdekStatus.getOrderStatus() == OrderStatuses.DELIVERED) {
						// посылка доставлена --> DELIVERED
						currentStatusOrder.setStatus(OrderStatuses.DELIVERED);
						willModifingOrders.add(currentStatusOrder);
						
						debugInfoItem = "- доставлен: " + currentStatusOrder.getNo() + ", " + currentStatusOrder.getCustomer().getViewShortName() + ", " + currentStatusOrder.getDelivery().getAddress().getAddress();
						debugInfo = debugInfo + debugInfoItem + "<br>";
						logger.debug("доставлен: {}, {}, {}", currentStatusOrder.getNo(), currentStatusOrder.getCustomer().getViewShortName(), currentStatusOrder.getDelivery().getAddress().getAddress());
						deliveredCount++;
					} else if (currentStatus == OrderStatuses.DELIVERING && modifiedSdekStatus.getOrderStatus() == OrderStatuses.READY_GIVE_AWAY) {
						// посылка доставляется --> READY_GIVE_AWAY
						currentStatusOrder.setStatus(OrderStatuses.READY_GIVE_AWAY);
						willModifingOrders.add(currentStatusOrder);
						
						debugInfoItem = "- прибыл: " + currentStatusOrder.getNo() + ", " + currentStatusOrder.getCustomer().getViewShortName() + ", " + currentStatusOrder.getDelivery().getAddress().getAddress();
						debugInfo = debugInfo + debugInfoItem + "<br>";
						logger.debug("прибыл: {}, {}, {}", currentStatusOrder.getNo(), currentStatusOrder.getCustomer().getViewShortName(), currentStatusOrder.getDelivery().getAddress().getAddress());
						readyGiveAwayCount++;
					} else if (currentStatus == OrderStatuses.DELIVERING && modifiedSdekStatus.getOrderStatus() == OrderStatuses.DELIVERED) {
						// посылка доставляется --> DELIVERED
						currentStatusOrder.setStatus(OrderStatuses.DELIVERED);
						willModifingOrders.add(currentStatusOrder);
						
						debugInfoItem = "- доставлен: " + currentStatusOrder.getNo() + ", " + currentStatusOrder.getCustomer().getViewShortName() + ", " + currentStatusOrder.getDelivery().getAddress().getAddress();
						debugInfo = debugInfo + debugInfoItem + "<br>";
						logger.debug("доставлен: {}, {}, {}", currentStatusOrder.getNo(), currentStatusOrder.getCustomer().getViewShortName(), currentStatusOrder.getDelivery().getAddress().getAddress());
						deliveredCount++;
					} else if ((currentStatus == OrderStatuses.READY_GIVE_AWAY || currentStatus == OrderStatuses.READY_GIVE_AWAY_TROUBLE) && modifiedSdekStatus.getOrderStatus() == OrderStatuses.DELIVERED) {
						// посылка готова к выдаче --> DELIVERED
						currentStatusOrder.setStatus(OrderStatuses.DELIVERED);
						willModifingOrders.add(currentStatusOrder);
						
						debugInfoItem = "- доставлен: " + currentStatusOrder.getNo() + ", " + currentStatusOrder.getCustomer().getViewShortName() + ", " + currentStatusOrder.getDelivery().getAddress().getAddress();
						debugInfo = debugInfo + debugInfoItem + "<br>";
						logger.debug("доставлен: {}, {}, {}", currentStatusOrder.getNo(), currentStatusOrder.getCustomer().getViewShortName(), currentStatusOrder.getDelivery().getAddress().getAddress());
						deliveredCount++;
					}
				}				
			}
			
			OrderConditions ozonRocketsConditions = new OrderConditions();
			Set<OrderStatuses> ozonRocketsStatuses = new HashSet<OrderStatuses>();
			ozonRocketsStatuses.add(OrderStatuses.APPROVED);
			ozonRocketsStatuses.add(OrderStatuses.DELIVERING);
			ozonRocketsStatuses.add(OrderStatuses.READY_GIVE_AWAY);
			ozonRocketsStatuses.add(OrderStatuses.READY_GIVE_AWAY_TROUBLE);
			ozonRocketsStatuses.add(OrderStatuses.PAY_ON);		
			ozonRocketsConditions.setStatuses(ozonRocketsStatuses);
					
			Set<DeliveryTypes> ozonRocketDeliveryTypes = new HashSet<DeliveryTypes>();
			ozonRocketDeliveryTypes.add(DeliveryTypes.OZON_ROCKET_COURIER);
			ozonRocketDeliveryTypes.add(DeliveryTypes.OZON_ROCKET_PICKPOINT);
			ozonRocketDeliveryTypes.add(DeliveryTypes.OZON_ROCKET_POSTAMAT);
			ozonRocketsConditions.setDeliveryTypes(ozonRocketDeliveryTypes);
			
			ozonRocketsConditions.setPeriodExist(false);
			ozonRocketsConditions.setTrackCodeNotExist(false);
			
			List<Order> ozonRoketOrders = orderDao.listOrdersByConditions(ozonRocketsConditions);
			logger.debug("ozon.rocket:");
			
			OzonRocketApi ozonRocketApi = new OzonRocketApi();			
			List<Order> ozonRocketModifiedOrders = ozonRocketApi.getStatuses(ozonRoketOrders);	
			
			for (Order currentStatusOrder : ozonRoketOrders) {
				Order ozonRocketModifiedOrder = findModifiedOrderByTrackCode(ozonRocketModifiedOrders, currentStatusOrder);
				if (ozonRocketModifiedOrder != null) {
					OrderStatuses currentStatus = currentStatusOrder.getStatus();
					CarrierStatuses modifiedSdekStatus = ozonRocketModifiedOrder.getDelivery().getCarrierStatus();
					
					if (currentStatus == OrderStatuses.APPROVED && modifiedSdekStatus.getOrderStatus() == OrderStatuses.DELIVERING) {
						// посылка прибыла в сдэк на отправку и поехала --> DELIVERING
						currentStatusOrder.setStatus(OrderStatuses.DELIVERING);
						willModifingOrders.add(currentStatusOrder);
						
						debugInfoItem = "- доставляется: " + currentStatusOrder.getNo() + ", " + currentStatusOrder.getCustomer().getViewShortName() + ", " + currentStatusOrder.getDelivery().getAddress().getAddress();
						debugInfo = debugInfo + debugInfoItem + "<br>";						
						logger.debug("доставляется: {}, {}, {}", currentStatusOrder.getNo(), currentStatusOrder.getCustomer().getViewShortName(), currentStatusOrder.getDelivery().getAddress().getAddress());
						deliveringCount++;
					} else if (currentStatus == OrderStatuses.PAY_ON && modifiedSdekStatus.getOrderStatus() == OrderStatuses.DELIVERING) {
						// посылка по предоплате прибыла в сдэк на отправку и поехала --> DELIVERING
						currentStatusOrder.setStatus(OrderStatuses.DELIVERING);
						willModifingOrders.add(currentStatusOrder);
						
						debugInfoItem = "- доставляется: " + currentStatusOrder.getNo() + ", " + currentStatusOrder.getCustomer().getViewShortName() + ", " + currentStatusOrder.getDelivery().getAddress().getAddress();
						debugInfo = debugInfo + debugInfoItem + "<br>";
						logger.debug("доставляется: {}, {}, {}", currentStatusOrder.getNo(), currentStatusOrder.getCustomer().getViewShortName(), currentStatusOrder.getDelivery().getAddress().getAddress());
						deliveringCount++;
					} else if (currentStatus == OrderStatuses.APPROVED && modifiedSdekStatus.getOrderStatus() == OrderStatuses.READY_GIVE_AWAY) {
						// посылка готова к выдаче --> READY_GIVE_AWAY
						currentStatusOrder.setStatus(OrderStatuses.READY_GIVE_AWAY);
						willModifingOrders.add(currentStatusOrder);
						
						debugInfoItem = "- прибыл: " + currentStatusOrder.getNo() + ", " + currentStatusOrder.getCustomer().getViewShortName() + ", " + currentStatusOrder.getDelivery().getAddress().getAddress();
						debugInfo = debugInfo + debugInfoItem + "<br>";
						logger.debug("прибыл: {}, {}, {}", currentStatusOrder.getNo(), currentStatusOrder.getCustomer().getViewShortName(), currentStatusOrder.getDelivery().getAddress().getAddress());
						readyGiveAwayCount++;
					} else if (currentStatus == OrderStatuses.APPROVED && modifiedSdekStatus.getOrderStatus() == OrderStatuses.DELIVERED) {
						// посылка доставлена --> DELIVERED
						currentStatusOrder.setStatus(OrderStatuses.DELIVERED);
						willModifingOrders.add(currentStatusOrder);
						
						debugInfoItem = "- доставлен: " + currentStatusOrder.getNo() + ", " + currentStatusOrder.getCustomer().getViewShortName() + ", " + currentStatusOrder.getDelivery().getAddress().getAddress();
						debugInfo = debugInfo + debugInfoItem + "<br>";
						logger.debug("доставлен: {}, {}, {}", currentStatusOrder.getNo(), currentStatusOrder.getCustomer().getViewShortName(), currentStatusOrder.getDelivery().getAddress().getAddress());
						deliveredCount++;
					} else if (currentStatus == OrderStatuses.DELIVERING && modifiedSdekStatus.getOrderStatus() == OrderStatuses.READY_GIVE_AWAY) {
						// посылка доставляется --> READY_GIVE_AWAY
						currentStatusOrder.setStatus(OrderStatuses.READY_GIVE_AWAY);
						willModifingOrders.add(currentStatusOrder);
						
						debugInfoItem = "- прибыл: " + currentStatusOrder.getNo() + ", " + currentStatusOrder.getCustomer().getViewShortName() + ", " + currentStatusOrder.getDelivery().getAddress().getAddress();
						debugInfo = debugInfo + debugInfoItem + "<br>";
						logger.debug("прибыл: {}, {}, {}", currentStatusOrder.getNo(), currentStatusOrder.getCustomer().getViewShortName(), currentStatusOrder.getDelivery().getAddress().getAddress());
						readyGiveAwayCount++;
					} else if (currentStatus == OrderStatuses.DELIVERING && modifiedSdekStatus.getOrderStatus() == OrderStatuses.DELIVERED) {
						// посылка доставляется --> DELIVERED
						currentStatusOrder.setStatus(OrderStatuses.DELIVERED);
						willModifingOrders.add(currentStatusOrder);
						
						debugInfoItem = "- доставлен: " + currentStatusOrder.getNo() + ", " + currentStatusOrder.getCustomer().getViewShortName() + ", " + currentStatusOrder.getDelivery().getAddress().getAddress();
						debugInfo = debugInfo + debugInfoItem + "<br>";
						logger.debug("доставлен: {}, {}, {}", currentStatusOrder.getNo(), currentStatusOrder.getCustomer().getViewShortName(), currentStatusOrder.getDelivery().getAddress().getAddress());
						deliveredCount++;
					} else if ((currentStatus == OrderStatuses.READY_GIVE_AWAY || currentStatus == OrderStatuses.READY_GIVE_AWAY_TROUBLE) && modifiedSdekStatus.getOrderStatus() == OrderStatuses.DELIVERED) {
						// посылка готова к выдаче --> DELIVERED
						currentStatusOrder.setStatus(OrderStatuses.DELIVERED);
						willModifingOrders.add(currentStatusOrder);
						
						debugInfoItem = "- доставлен: " + currentStatusOrder.getNo() + ", " + currentStatusOrder.getCustomer().getViewShortName() + ", " + currentStatusOrder.getDelivery().getAddress().getAddress();
						debugInfo = debugInfo + debugInfoItem + "<br>";
						logger.debug("доставлен: {}, {}, {}", currentStatusOrder.getNo(), currentStatusOrder.getCustomer().getViewShortName(), currentStatusOrder.getDelivery().getAddress().getAddress());
						deliveredCount++;
					}
				}				
			}		
			
			logger.debug("ozon:");			
			final String sqlSelectListOrders = "SELECT * from sr_v_order where advert_type = 11 and delivery_type = 701 and status in (1, 2, 5, 7)";
			
			Object[] conditionPeriod;		
			conditionPeriod = new Object[]{
						};	
			List<Order> beforeOzonOrders = this.jdbcTemplate.query(sqlSelectListOrders,
					conditionPeriod,				
					new RowMapper<Order>() {
						@Override
			            public Order mapRow(ResultSet rs, int rowNum) throws SQLException {
			                Order order = new Order();	
			                order.setId(rs.getInt("ID"));		                	                
			                return order;
			            }
			        });
			
			OzonMarketApi ozonMarketApi = new OzonMarketApi();
			
			for (Order currentStatusOrder : beforeOzonOrders) {
				
				currentStatusOrder = orderDao.findById(currentStatusOrder.getId());
				OrderExternalCrm ozonCrm = currentStatusOrder.getExternalCrmByCode(CrmTypes.OZON);
				
				if (ozonCrm == null) {
					continue;
				}
				OrderStatuses currentStatus = currentStatusOrder.getStatus();
				
				Order modifiedOrder = ozonMarketApi.getOrder(ozonCrm.getParentCode());			
				OrderStatuses modifiedStatus = modifiedOrder.getStatus();
				if (currentStatus == OrderStatuses.APPROVED && modifiedStatus == OrderStatuses.DELIVERING) {
					// посылка прибыла на отправку и поехала --> DELIVERING
					currentStatusOrder.setStatus(OrderStatuses.DELIVERING);
					willModifingOrders.add(currentStatusOrder);
					
					debugInfoItem = "- доставляется: " + currentStatusOrder.getNo() + ", " + currentStatusOrder.getCustomer().getViewShortName() + ", " + currentStatusOrder.getDelivery().getAddress().getAddress();
					debugInfo = debugInfo + debugInfoItem + "<br>";						
					logger.debug("доставляется: {}, {}, {}", currentStatusOrder.getNo(), currentStatusOrder.getCustomer().getViewShortName(), currentStatusOrder.getDelivery().getAddress().getAddress());
					deliveringCount++;
				} else if (currentStatus == OrderStatuses.APPROVED && modifiedStatus == OrderStatuses.DELIVERED) {
					// посылка доставлена --> DELIVERED
					currentStatusOrder.setStatus(OrderStatuses.DELIVERED);
					willModifingOrders.add(currentStatusOrder);
					
					debugInfoItem = "- доставлен: " + currentStatusOrder.getNo() + ", " + currentStatusOrder.getCustomer().getViewShortName() + ", " + currentStatusOrder.getDelivery().getAddress().getAddress();
					debugInfo = debugInfo + debugInfoItem + "<br>";
					logger.debug("доставлен: {}, {}, {}", currentStatusOrder.getNo(), currentStatusOrder.getCustomer().getViewShortName(), currentStatusOrder.getDelivery().getAddress().getAddress());
					deliveredCount++;
				} else if (currentStatus == OrderStatuses.DELIVERING && modifiedStatus == OrderStatuses.DELIVERED) {
					// посылка доставляется --> DELIVERED
					currentStatusOrder.setStatus(OrderStatuses.DELIVERED);
					willModifingOrders.add(currentStatusOrder);
					
					debugInfoItem = "- доставлен: " + currentStatusOrder.getNo() + ", " + currentStatusOrder.getCustomer().getViewShortName() + ", " + currentStatusOrder.getDelivery().getAddress().getAddress();
					debugInfo = debugInfo + debugInfoItem + "<br>";
					logger.debug("доставлен: {}, {}, {}", currentStatusOrder.getNo(), currentStatusOrder.getCustomer().getViewShortName(), currentStatusOrder.getDelivery().getAddress().getAddress());
					deliveredCount++;
				} else if (currentStatus != OrderStatuses.CANCELED && modifiedStatus == OrderStatuses.CANCELED) {
					// посылка доставляется --> CANCELED
					currentStatusOrder.setStatus(OrderStatuses.CANCELED);
					willModifingOrders.add(currentStatusOrder);
					
					debugInfoItem = "- отменен: " + currentStatusOrder.getNo() + ", " + currentStatusOrder.getCustomer().getViewShortName() + ", " + currentStatusOrder.getDelivery().getAddress().getAddress();
					debugInfo = debugInfo + debugInfoItem + "<br>";
					logger.debug("отменен: {}, {}, {}", currentStatusOrder.getNo(), currentStatusOrder.getCustomer().getViewShortName(), currentStatusOrder.getDelivery().getAddress().getAddress());
					cancelledCount++;

				}  			
				willModifingOrders.add(currentStatusOrder);
			}
			
			logger.debug("");		
			// перебираем список modifiedOrders и у посылок меняем статус + у посылок с новыми статусами:			
			// DELIVERING или READY_GIVE_AWAY - оповещение	
			for (Order willModifiedOrder: willModifingOrders) {
				
				Order currentOrder = orderDao.findById(willModifiedOrder.getId());
				OrderStatusItem newOrderStatusValue = willModifiedOrder.getLastStatusItem();
				orderDao.changeStatusOrder(willModifiedOrder.getId(), 
						willModifiedOrder.getStatus(), currentOrder.getAnnotation(), currentOrder.getDelivery().getTrackCode(), 
						newOrderStatusValue);
				
				if (currentOrder.getAdvertType() == OrderAdvertTypes.OZON || currentOrder.getAdvertType() == OrderAdvertTypes.YANDEX_MARKET) {
					continue;
				}
				currentOrder = orderDao.findById(willModifiedOrder.getId());
				if (willModifiedOrder.getStatus() == OrderStatuses.DELIVERING) {
					// sms										
					messageManager.sendOrderMessage(currentOrder, true);
				}
				if (willModifiedOrder.getStatus() == OrderStatuses.READY_GIVE_AWAY) {
					// sms
					messageManager.sendOrderMessage(currentOrder, true);					
				}
			}			
			
		} catch (Exception e) {
			logger.error("email:{} {}", "fail", e);			
		} 
		// перебираем все ордера со статусами на изменение и меняем статусы + рассылки смс
		int changedCount = deliveringCount + readyGiveAwayCount + deliveredCount + cancelledCount;
		
		String resultNow = String.format("Всего посылок изменило статус: %d (из них доставляется: %d, прибыло на пункты выдачи: %d, доставлено: %d, отменено: %d)", 
				changedCount, deliveringCount, readyGiveAwayCount, deliveredCount, cancelledCount);
		if (changedCount > 0) {
			resultNow = resultNow + ":<br>" + debugInfo;
		}	
		// считаем все ордера, что были сегодня: отправлены, прибыли, получены
		/*
		Map<OrderStatuses, Integer> deliveryOrders = orderDao.getDeliveryOrders(DateTimeUtils.sysDate());
		String resultToday = String.format("За сегодня: (отправлено: %d, прибыло на пункты выдачи: %d, доставлено: %d)", 
				deliveryOrders.get(OrderStatuses.DELIVERING), 
				deliveryOrders.get(OrderStatuses.READY_GIVE_AWAY), 
				deliveryOrders.get(OrderStatuses.DELIVERED));
		
		String result = resultNow + "<br><strong>" + resultToday + "</strong>";
		return result;
		*/
		return resultNow;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	private void updateCrmCdekMarketConnect(List<Order> cdekModifiedOrders) {
		
		for (Order cdekModifiedOrder : cdekModifiedOrders) {
			if (cdekModifiedOrder.getAdvertType() == OrderAdvertTypes.CDEK_MARKET) {
				
				final String sqlDeleteCrmStatus = "DELETE FROM sr_order_crm_connect WHERE order_id = ?";	
				
				final String sqlInsertCrmStatus = "INSERT INTO sr_order_crm_connect"
						+ " (order_id, crm_id, parent_crm_id, parent_crm_code, crm_status)"
						+ " VALUES"
						+ " (?, ?, ?, ?, ?)";
				this.jdbcTemplate.update(sqlDeleteCrmStatus, new Object[] {cdekModifiedOrder.getId()});
				
				for (OrderExternalCrm externalCrm : cdekModifiedOrder.getExternalCrms()) {			
					this.jdbcTemplate.update(sqlInsertCrmStatus, new Object[] { 
							cdekModifiedOrder.getId(), 
							externalCrm.getCrm().getId(), 
							externalCrm.getParentId(),
							externalCrm.getParentCode(),
							CrmStatuses.SUCCESS.getId()});			
					
				}		
				
				
			}
					
		}
		
		
	}

	private Order findModifiedOrderByTrackCode(List<Order> cdekModifiedOrders, Order currentStatusOrder) {
		for (Order cdekModifiedOrder : cdekModifiedOrders) {
			if (StringUtils.equals(cdekModifiedOrder.getDelivery().getTrackCode(), currentStatusOrder.getDelivery().getTrackCode())) {
				return cdekModifiedOrder;
			}			
		}
		return null;
	}
	
	private DeliveryServiceResult ozonRocketCalc(Order order, BigDecimal totalAmount, DeliveryTypes deliveryType, Address to) {
		
		OzonRocketApi ozonRocketApi = new OzonRocketApi();
		
		boolean isPostpay = false;	
		boolean isCash  = false;
		boolean isCard  = false;
		boolean isLegalEntity  = false;
		if (order.getOrderType() == OrderTypes.ORDER) {
			isPostpay = order.getPaymentType() == PaymentTypes.POSTPAY ? true : false;
			if (isPostpay) {
				isCash = true;
				isCard = true;				
			}			
		} else {
			isPostpay = false;		
			isCash = false;
			isCard = false;
		}
		if (order.getCustomer() != null && order.getCustomer().isCompany()) {
			isLegalEntity = true;
		} 	
		boolean isPaySeller = order.getDelivery().getPaymentDeliveryType() == PaymentDeliveryTypes.SELLER ? true : false;
		
		DeliveryServiceResult result;
		if (deliveryType == DeliveryTypes.OZON_ROCKET_COURIER) {			
			result = ozonRocketApi.calculateByAddress(calcTotalWeightG(order), DateTimeUtils.sysDate(), totalAmount,
					to.getAddress(),					
					isPostpay, isPaySeller);
			result.getAddresses().add(to);
			
		} else {
			Long deliveryVariantId;
			if (to.getCarrierInfo().getDeliveryVariantId() != null && to.getCarrierInfo().getDeliveryVariantId() > 0) {			
				deliveryVariantId = to.getCarrierInfo().getDeliveryVariantId();
			} else {
				deliveryVariantId = ozonRocketApi.getDeliveryVariantId(to.getCarrierInfo().getCityContext(), deliveryType.getCode(), 
						calcTotalWeightG(order), 
						totalAmount,
						isLegalEntity,
						isPostpay, 
						isCash,
						isCard);			
			} 		
			
			if (deliveryVariantId == null) {
				result = new DeliveryServiceResult();
			} else {
				result = ozonRocketApi.calculate(calcTotalWeightG(order), DateTimeUtils.sysDate(), totalAmount,
						deliveryVariantId,
						isPostpay, isPaySeller);			
				Address deliveryVariant = ozonRocketApi.getDeliveryVariant(deliveryVariantId);
				result.getAddresses().add(deliveryVariant);
			}			
		}

		result.setParcelType(deliveryType.getAnnotation()); 
		result.setTo(to.getAddress());
		return result;
	}

	private DeliveryServiceResult cdekCalc(Order order, BigDecimal totalAmount, DeliveryTypes deliveryType, Address to) throws Exception {
		
		CdekApi cdek = new CdekApi();
		
		int tariffId = CdekApi.getCdekTariffId(deliveryType);		
		int receiverCityId = to.getCarrierInfo().getCityId();
		
		boolean isPostpay = false;
		if (order.getOrderType() == OrderTypes.ORDER) {
			isPostpay = order.getPaymentType() == PaymentTypes.POSTPAY ? true : false;			
		}				
		boolean isPaySeller = order.getDelivery().getPaymentDeliveryType() == PaymentDeliveryTypes.SELLER ? true : false;				
		DeliveryServiceResult result = cdek.calculate(calcTotalWeightKg(order), DateTimeUtils.sysDate(), 
				totalAmount, tariffId, receiverCityId, isPostpay, isPaySeller);
		result.setParcelType(deliveryType.getAnnotation()); 
		result.setTo(to.getAddress());
		return result;
	}
	
	/*
	private int getCdekTariffId(DeliveryTypes deliveryType) {
		int tariffId;
		if (deliveryType == DeliveryTypes.CDEK_COURIER) {
			tariffId = 137;
		} else if (deliveryType == DeliveryTypes.CDEK_COURIER_ECONOMY) {
			tariffId = 233;			
	    } else if (deliveryType == DeliveryTypes.CDEK_PVZ_TYPICAL) {
	    	tariffId = 136;
	    } else if (deliveryType == DeliveryTypes.PICKUP) {
	    	tariffId = 136;
	    } else if (deliveryType == DeliveryTypes.CDEK_PVZ_ECONOMY) {
	    	tariffId = 234;	
	    } else {
	    	tariffId = 0;       
	    }
		return tariffId;
	}
	*/
	
	private DeliveryServiceResult postCalc(Order order, BigDecimal totalAmount, DeliveryTypes type, Address to) throws IOException {
		
		if (StringUtils.isEmpty(to.getPostCode())) {
			return DeliveryServiceResult.createEmpty();
		}
		String iBase = order.isPrepayment() ? "p" : "f";
		
		String parcelDataName;
		if (type == DeliveryTypes.POST_TYPICAL) {
			parcelDataName = "ЦеннаяПосылка";
	    } else if (type == DeliveryTypes.POST_EMS) {
	    	parcelDataName = "EMS";
	    } else if (type == DeliveryTypes.POST_I_CLASS) {
	    	parcelDataName = "Посылка1Класс";	
	    } else {
	    	parcelDataName = "";       
	    }
		
		/*
		int weight = 0;
		BigDecimal totalWeight = BigDecimal.ZERO;
		for (OrderItem item : order.getItems()) {
			Product product = wikiDao.getProductById(item.getProduct().getId());
			BigDecimal itemWeight = product.getWeight().multiply(BigDecimal.valueOf(item.getQuantity()));
			totalWeight = totalWeight.add(itemWeight);
		}
		weight = totalWeight.multiply(BigDecimal.valueOf(1000)).round(new MathContext(4, RoundingMode.HALF_UP)).intValue();
		weight = weight == 0 ? 500 : weight;
		*/
		PostcalcApi postcalc = new PostcalcApi();
		return postcalc.postCalc(calcTotalWeightG(order), DateTimeUtils.sysDate(), totalAmount, parcelDataName, to, iBase);
	}
	
	private int calcTotalWeightG(Order order) {
		
		int weight = 0;
		BigDecimal totalWeight = BigDecimal.ZERO;
		for (OrderItem item : order.getItems()) {
			Product product = wikiDao.getProductById(item.getProduct().getId());
			BigDecimal itemWeight = product.getStore().getWeight().multiply(BigDecimal.valueOf(item.getQuantity()));
			totalWeight = totalWeight.add(itemWeight);
		}
		weight = totalWeight.multiply(BigDecimal.valueOf(1000)).round(new MathContext(4, RoundingMode.HALF_UP)).intValue();
		weight = weight == 0 ? 500 : weight;		
		return weight;		
	}
	
	private BigDecimal calcTotalWeightKg(Order order) {
		int weightG = calcTotalWeightG(order);
		BigDecimal weightKg = BigDecimal.valueOf(0.001).multiply(BigDecimal.valueOf(weightG));
		return weightKg;
	}	
	
	@Override
	public void exportParcelOrdersToExcel(int orderId, OutputStream outStream, Date executorDate, CrmTypes crmType) {
		
		if (crmType == CrmTypes.CDEK) {			
			List<Order> orders = new ArrayList<Order>();
			try {	
				if (orderId > 0) {
					Order order = orderDao.findById(orderId);			
					orders.add(order);	
				} else {
					List<Order> lazyOrders = exportCdekListOrders(executorDate);
					for (Order lazyOrder : lazyOrders) {
						Order order = orderDao.findById(lazyOrder.getId());			
						orders.add(order);	
					}
				}
				List<CdekOrderBean> exportBeans = convert4CdekCrmExportBeans(orders);			
				writeIntoExcel(exportBeans, outStream);
			} catch (FileNotFoundException e) {
				logger.error("FileNotFoundException", e);
			} catch (IOException e) {
				logger.error("IOException", e);
			}			
		} 
		
	}
	
	private List<Order> exportCdekListOrders(Date exportDate) {
		
		OrderConditions orderConditions = new OrderConditions(ReportPeriodTypes.ANY_PERIOD);
		orderConditions.setPeriod(new Pair<Date>(DateTimeUtils.beforeDate(exportDate), exportDate));
		
		Set<OrderStatuses> statuses = new HashSet<OrderStatuses>();
		statuses.add(OrderStatuses.APPROVED);
		orderConditions.setStatuses(statuses);
		
		Set<OrderTypes> types = new HashSet<OrderTypes>();
		types.add(OrderTypes.ORDER);
		orderConditions.setTypes(types);
		
		Set<DeliveryTypes> deliveryTypes = new HashSet<DeliveryTypes>();
		deliveryTypes.add(DeliveryTypes.CDEK_COURIER);
		deliveryTypes.add(DeliveryTypes.CDEK_COURIER_ECONOMY);
		deliveryTypes.add(DeliveryTypes.CDEK_PVZ_ECONOMY);
		deliveryTypes.add(DeliveryTypes.CDEK_PVZ_TYPICAL);
		deliveryTypes.add(DeliveryTypes.PICKUP);		
		orderConditions.setDeliveryTypes(deliveryTypes);
		
		orderConditions.setTrackCodeNotExist(true);
				
		//final String sqlSelectListOrders = createSQLQueryListOrdersByConditions(orderConditions).getConditionText();
		//logger.debug(sqlSelectListOrders);
		return orderDao.listOrdersByConditions(orderConditions);		
	}	
	
	private List<CdekOrderBean> convert4CdekCrmExportBeans(List<Order> orders) throws FileNotFoundException, IOException {
		
		List<CdekOrderBean> beans = new ArrayList<CdekOrderBean>();		
		for (Order order : orders) {
			logger.debug("order:{},{},{}", order.getId(), order.getNo(), order.getCustomer().getViewShortName());
			
			CdekOrderBean bean = convert4CdekCrmExportBean(order);
			beans.add(bean);
		}		
		return beans;
	}
	
	private CdekOrderBean convert4CdekCrmExportBean(Order order) {
		
		CdekOrderBean bean = new CdekOrderBean(order);
		bean.setNo(order.getNo());
		
		bean.setCity(order.getDelivery().getAddress().getCity());		
		bean.setCityId(order.getDelivery().getAddress().getCarrierInfo().getCityId());
		bean.setStreet(order.getDelivery().getAddress().getCarrierInfo().getStreet());
		bean.setHouse(order.getDelivery().getAddress().getCarrierInfo().getHouse());
		bean.setFlat(order.getDelivery().getAddress().getCarrierInfo().getFlat());
				
		bean.setPostCode("");
		
		bean.setDeliveryType(order.getDelivery().getDeliveryType());
		
		bean.setRecipientAddress(order.getDelivery().getAddress().getStreetAddress());
		bean.setRecipientPhone(order.getCustomer().getViewPhoneNumber());
		bean.setRecipientEmail(order.getCustomer().getEmail());
		
		if (order.getCustomer().isPerson()) {
			
			if (order.getDelivery().getRecipient() == null) {
				bean.setRecipientPerson(order.getCustomer().getViewLongName());
				bean.setRecipient(order.getCustomer().getViewLongName());				
			} else {
				bean.setRecipientPerson(order.getDelivery().getRecipient().getViewLongName());
				bean.setRecipient(order.getDelivery().getRecipient().getViewLongName());
				if (StringUtils.isNotEmpty(order.getDelivery().getRecipient().getPhoneNumber())) {
					bean.setRecipientPhone(order.getDelivery().getRecipient().getPhoneNumber().trim());
				}
			}
			//bean.setRecipient(order.getCustomer().getViewLongName());
			
		} else {
			ForeignerCompanyCustomer company = (ForeignerCompanyCustomer) order.getCustomer();  
			bean.setRecipient(company.getShortName());
			bean.setRecipientPerson(company.getMainContact().getViewLongName());
		}	
		
		String pvz = order.getDelivery().getAddress().getCarrierInfo().getPvz();
		if (StringUtils.isEmpty(pvz)) {
			pvz = wikiDao.findCdekPvzByAddress(order.getDelivery().getAddress());
		}
		logger.debug("writeIntoExcel():{},{}", pvz, order.getDelivery().getAddress());			
		bean.setPvz(pvz);
		BigDecimal deliveryAmount = BigDecimal.ZERO;
		if (order.isPrepayment()) {
			deliveryAmount = BigDecimal.ZERO;				
		} else {				
			if (order.getAdvertType() == OrderAdvertTypes.OZON) {
				deliveryAmount = BigDecimal.ZERO;
			} else if (order.getCustomer().isCompany()) {
				deliveryAmount = BigDecimal.ZERO;
			} else if (order.getDelivery().getDeliveryType() == DeliveryTypes.PICKUP) {
				deliveryAmount = order.getAmounts().getTotal();
				bean.setCityId(44);
				bean.setPvz("MSK17");	
			} else {
				deliveryAmount = order.getDelivery().getCustomerPrice() != null ? order.getDelivery().getCustomerPrice() : BigDecimal.ZERO;
			}
		}				
		bean.setDeliveryPay(deliveryAmount);
		
		bean.setSeller("ИП Федоров А.А.");
		bean.setDeliveryAnnotation(order.getDelivery().getAnnotation());			
		bean.setProductCategory("оборудование");
		
		for (OrderItem orderItem : order.getItems()) {		
			
			CdekOrderItemBean orderItemBean = new CdekOrderItemBean();
			
			String productName;
			if (StringUtils.isNoneEmpty((orderItem.getProduct().getDeliveryName()))) {
				productName = orderItem.getProduct().getDeliveryName();
			} else {
				productName = orderItem.getProduct().getName(); 
			}				
			orderItemBean.setProductName(productName);
			orderItemBean.setProductSku(orderItem.getProduct().getViewSKU());
									
			BigDecimal productPrice = BigDecimal.ZERO;
			BigDecimal productPay = BigDecimal.ZERO;
			if (order.isPrepayment()) {
				productPrice = orderItem.getPrice();
				productPay = BigDecimal.ZERO;					
			} else {
				if (order.getAdvertType() == OrderAdvertTypes.OZON) {
					productPrice = orderItem.getPrice();
					productPay = BigDecimal.ZERO;
				} else if (order.getCustomer().isCompany()) {
					productPrice = orderItem.getPrice();
					productPay = BigDecimal.ZERO;					
				} else if (order.getDelivery().getDeliveryType() == DeliveryTypes.PICKUP) {
					productPrice = BigDecimal.ONE;
					productPay = BigDecimal.ZERO;					
				} else {
					productPrice = orderItem.calcPriceWithDiscount();
					productPay = orderItem.calcPriceWithDiscount();
				}					
			}
			orderItemBean.setProductPrice(productPrice);
			orderItemBean.setProductPay(productPay);			
			orderItemBean.setProductQuantity(orderItem.getQuantity());
			
			if (bean.getItems().size() == 0) {				
				
				bean.setProductSku(orderItem.getProduct().getViewSKU());
				bean.setProductName(orderItem.getProduct().getName());
				bean.setProductPrice(productPrice);
				bean.setProductPay(productPay);				
				bean.setProductQuantity(orderItem.getQuantity());
				
			}			
			bean.getItems().add(orderItemBean);
		}			
		return bean;		
	}
	
	private void writeIntoExcel(List<CdekOrderBean> beans, OutputStream outStream) throws FileNotFoundException, IOException {
		
		logger.debug("writeIntoExcel(): {}", "start");

		Workbook workBook = new HSSFWorkbook();
		Sheet sheet = workBook.createSheet("sheet-first");

		int rowIndex = 0;
		Row row = sheet.createRow(0);
		rowIndex++;

		Cell cell;
		int cellIndex = 0;
		
		// header
		cell = row.createCell(cellIndex);
		cell.setCellValue("Номер отправления");
		cellIndex++;

		cell = row.createCell(cellIndex);
		cell.setCellValue("Город получателя");
		cellIndex++;

		cell = row.createCell(cellIndex);
		cell.setCellValue("Индекс");
		cellIndex++;

		cell = row.createCell(cellIndex);
		cell.setCellValue("Получатель");
		cellIndex++;

		cell = row.createCell(cellIndex);
		cell.setCellValue("ФИО получателя");
		cellIndex++;

		cell = row.createCell(cellIndex);
		cell.setCellValue("Адрес получателя");
		cellIndex++;

		cell = row.createCell(cellIndex);
		cell.setCellValue("Код ПВЗ");
		cellIndex++;

		cell = row.createCell(cellIndex);
		cell.setCellValue("Телефон получателя");
		cellIndex++;

		cell = row.createCell(cellIndex);
		cell.setCellValue("Доп сбор за доставку с получателя в т.ч. НДС");
		cellIndex++;

		cell = row.createCell(cellIndex);
		cell.setCellValue("Ставка НДС с доп.сбора за доставку");
		cellIndex++;

		cell = row.createCell(cellIndex);
		cell.setCellValue("Сумма НДС с доп.сбора за доставку");
		cellIndex++;

		cell = row.createCell(cellIndex);
		cell.setCellValue("Истинный продавец");
		cellIndex++;

		cell = row.createCell(cellIndex);
		cell.setCellValue("Комментарий");
		cellIndex++;

		cell = row.createCell(cellIndex);
		cell.setCellValue("Порядковый номер места");
		cellIndex++;

		cell = row.createCell(cellIndex);
		cell.setCellValue("Вес места, кг");
		cellIndex++;

		cell = row.createCell(cellIndex);
		cell.setCellValue("Длина места, см");
		cellIndex++;

		cell = row.createCell(cellIndex);
		cell.setCellValue("Ширина места, см");
		cellIndex++;

		cell = row.createCell(cellIndex);
		cell.setCellValue("Высота места, см");
		cellIndex++;

		cell = row.createCell(cellIndex);
		cell.setCellValue("Описание места");
		cellIndex++;

		cell = row.createCell(cellIndex);
		cell.setCellValue("Код товара/артикул");
		cellIndex++;

		cell = row.createCell(cellIndex);
		cell.setCellValue("Наименование товара");
		cellIndex++;

		cell = row.createCell(cellIndex);
		cell.setCellValue("Стоимость единицы товара");
		cellIndex++;

		cell = row.createCell(cellIndex);
		cell.setCellValue("Оплата с получателя за ед товара в т.ч. НДС");
		cellIndex++;

		cell = row.createCell(cellIndex);
		cell.setCellValue("Вес товара, кг");
		cellIndex++;

		cell = row.createCell(cellIndex);
		cell.setCellValue("Количество, шт");
		cellIndex++;

		cell = row.createCell(cellIndex);
		cell.setCellValue("Ставка НДС, %");
		cellIndex++;

		cell = row.createCell(cellIndex);
		cell.setCellValue("Сумма НДС за ед.");
		cellIndex++;

		// order's row
		
		for (CdekOrderBean bean : beans) {
			row = sheet.createRow(rowIndex);
			rowIndex++;
			
			cellIndex = 0;
			
			cell = row.createCell(cellIndex);
			cell.setCellValue(bean.getNo());
			cellIndex++;
						
			cell = row.createCell(cellIndex);
			cell.setCellValue(bean.getCity());
			cellIndex++;
			
			cell = row.createCell(cellIndex);
			cell.setCellValue("");
			cellIndex++;
			
			cell = row.createCell(cellIndex);
			cell.setCellValue(bean.getRecipient());
			cellIndex++;
			
			cell = row.createCell(cellIndex);
			cell.setCellValue(bean.getRecipientPerson());
			cellIndex++;
			
			cell = row.createCell(cellIndex);
			cell.setCellValue(bean.getRecipientAddress());
			cellIndex++;
			
			cell = row.createCell(cellIndex);			
			cell.setCellValue(bean.getPvz());
			cellIndex++;
			
			cell = row.createCell(cellIndex);
			cell.setCellValue(bean.getRecipientPhone());
			cellIndex++;
			
			cell = row.createCell(cellIndex);
			cell.setCellValue(bean.getDeliveryPay().toString());
			cellIndex++;
			
			cell = row.createCell(cellIndex);
			cell.setCellValue("");
			cellIndex++;
			
			cell = row.createCell(cellIndex);
			cell.setCellValue("");
			cellIndex++;
			
			cell = row.createCell(cellIndex);
			cell.setCellValue("ИП Федоров А.А.");
			cellIndex++;
			
			cell = row.createCell(cellIndex);
			cell.setCellValue(bean.getDeliveryAnnotation());
			cellIndex++;
			
			cell = row.createCell(cellIndex);
			cell.setCellValue("1");
			cellIndex++;
			
			cell = row.createCell(cellIndex);
			cell.setCellValue("0,5");
			cellIndex++;
			
			cell = row.createCell(cellIndex);
			cell.setCellValue("10");
			cellIndex++;
			
			cell = row.createCell(cellIndex);
			cell.setCellValue("10");
			cellIndex++;
			
			cell = row.createCell(cellIndex);
			cell.setCellValue("10");
			cellIndex++;

			cell = row.createCell(cellIndex);
			cell.setCellValue(bean.getProductCategory());
			cellIndex++;		
			
			cell = row.createCell(cellIndex);
			cell.setCellValue(bean.getProductSku());
			cellIndex++;
			
			cell = row.createCell(cellIndex);
			cell.setCellValue(bean.getProductName());
			cellIndex++;
			
			cell = row.createCell(cellIndex);
			cell.setCellValue(bean.getProductPrice().toString());				
			cellIndex++;
			
			cell = row.createCell(cellIndex);
			cell.setCellValue(bean.getProductPay().toString());				
			cellIndex++;
									
			cell = row.createCell(cellIndex);
			cell.setCellValue("0,01");
			cellIndex++;
			
			cell = row.createCell(cellIndex);
			cell.setCellValue(bean.getProductQuantity());
			cellIndex++;
			
			cell = row.createCell(cellIndex);
			cell.setCellValue("0");
			cellIndex++;
			
			cell = row.createCell(cellIndex);
			cell.setCellValue("0");
			cellIndex++;				
					
		}
		row = sheet.createRow(rowIndex);
		rowIndex++;
		workBook.write(outStream);
		workBook.close();
		logger.debug("writeIntoExcel(): {}", "stop");
	}	

}
