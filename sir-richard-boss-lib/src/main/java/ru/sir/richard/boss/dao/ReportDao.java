package ru.sir.richard.boss.dao;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import ru.sir.richard.boss.model.data.Order;
import ru.sir.richard.boss.model.data.ProductCategory;
import ru.sir.richard.boss.model.data.SupplierStock;
import ru.sir.richard.boss.model.data.conditions.OrderConditions;
import ru.sir.richard.boss.model.data.conditions.ProductSalesReportConditions;
import ru.sir.richard.boss.model.data.report.ProductSalesReportBean;
import ru.sir.richard.boss.model.data.report.SalesFunnelReportBean;
import ru.sir.richard.boss.model.types.CustomerTypes;
import ru.sir.richard.boss.model.types.DeliveryTypes;
import ru.sir.richard.boss.model.types.OrderAdvertTypes;
import ru.sir.richard.boss.model.types.OrderAmountTypes;
import ru.sir.richard.boss.model.types.OrderSourceTypes;
import ru.sir.richard.boss.model.types.OrderStatuses;
import ru.sir.richard.boss.model.types.OrderTypes;
import ru.sir.richard.boss.model.types.PaymentTypes;
import ru.sir.richard.boss.model.types.ReportPeriodTypes;
import ru.sir.richard.boss.model.types.ReportQueryNames;
import ru.sir.richard.boss.model.utils.Pair;

@Repository
public class ReportDao extends AnyDaoImpl {
	
	private final Logger logger = LoggerFactory.getLogger(ReportDao.class);
	
	@Autowired
	private WikiDao wikiDao;
	
	@Autowired
	private OrderDao orderDao;
	
	public List<ProductSalesReportBean> productSales(Pair<Date> period) {
		logger.debug("productSales():{}", period);
		final String sqlSelectProductSales = "SELECT p.product_id, MAX(p.name) product_name, MAX(p.category_annotation) category_annotation, SUM(oi.quantity) quantity, SUM(oi.amount) amount" + 
				"		  FROM sr_v_order o, sr_order_item oi, sr_v_product p" + 
				"		  WHERE (o.id = oi.order_id) AND" + 
				"		        (oi.product_id = p.product_id) AND" + 
				"		        (o.order_date between ? and ?) AND" + 
				"		        (o.status IN (2, 4, 5, 7, 12, 8, 10)) AND"	+ 
				"               (o.order_type in (1, 2))" +
				"		  GROUP BY category_annotation, p.product_id";
		
		/*
		APPROVED(2, "подтвержден", ""),	// margin > 0, postpay > 0
		PAY_WAITING(3, "ожидаем оплату", "warning"), // margin = 0, postpay = ?
		PAY_ON(4, "оплата поступила", "warning"), // margin > 0, postpay = ?		
		DELIVERING(5, "доставляется", ""), // margin > 0, postpay > 0
		READY_GIVE_AWAY(7, "прибыл", ""), // margin > 0, postpay > 0
		READY_GIVE_AWAY_TROUBLE(12, "заканчивается срок хранения", "danger"), // margin > 0, postpay > 0
		DELIVERED(10, "получен", ""), 
		DOC_NOT_EXIST(11, "нет ТОРГ-12", ""), // margin > 0, postpay = 0
		FINISHED(8, "завершен", "success"), // margin > 0, postpay = 0	
		REDELIVERY(9, "отказ от вручения", "secondary"), // margin = 0, postpay > 0
		CANCELED(13, "отменен", "danger"), // margin = 0, postpay = 0
		REDELIVERY_FINISHED(15, "возврат получен", "danger"), // --
		LOST(16, "утерян", "lost"); // margin = 0, postpay = 0
		*/
		List<ProductSalesReportBean> beans = this.jdbcTemplate.query(sqlSelectProductSales,
				new Object[]{period.getStart(), period.getEnd()},
				new int[] {Types.DATE, Types.DATE },
				new RowMapper<ProductSalesReportBean>() {
					@Override
		            public ProductSalesReportBean mapRow(ResultSet rs, int rowNum) throws SQLException {		            	
		            	ProductSalesReportBean bean = new ProductSalesReportBean();
		            	bean.setProduct(wikiDao.getProductById(rs.getInt("PRODUCT_ID")));
		            	bean.setQuantity(rs.getInt("QUANTITY"));
		            	bean.setAmount(rs.getBigDecimal("AMOUNT"));		            	
		                return bean;
		            }
		        });
		return beans;		
	}

	public List<ProductSalesReportBean> productSalesByQuery(ProductSalesReportConditions conditions) {
		
		boolean isDiscretCondition = true;
		
		OrderStatuses fixedStatus;
		if (StringUtils.isNotEmpty(conditions.getIdsPaymentTypes()) && conditions.getIdsPaymentTypes().equals(String.valueOf(PaymentTypes.YANDEX_PAY.getId()))) {
			// это оплата банковской картой - завершено (могут быть курьерка у которой завершено есть, а доставлено - нет)
			fixedStatus = OrderStatuses.FINISHED;
			
		} else {
			// все остальное - доставлено
			fixedStatus = OrderStatuses.DELIVERED;
		}
		
		String sqlSelectProductSales = "SELECT p.product_id, MAX(p.name) product_name, MAX(p.category_annotation) category_annotation, SUM(oi.quantity) quantity, SUM(oi.amount) amount"
		  + " FROM sr_v_order o, sr_order_item oi, sr_v_product p"
		  + " WHERE (o.id = oi.order_id) AND"
		  + "       (oi.product_id = p.product_id) AND"
		  + "       (o.id in (select order_id from sr_order_status os WHERE (os.date_added between ? and ?) AND os.status in (" + fixedStatus.getId() + "))) ";
				
		if (StringUtils.isNotEmpty(conditions.getIdsDeliveryTypes())) {			
			if (isDiscretCondition) {
				sqlSelectProductSales += " AND ";
			}
			sqlSelectProductSales += "(delivery_type in (" + conditions.getIdsDeliveryTypes() + "))";
			isDiscretCondition = true;
		}		
		if (StringUtils.isNotEmpty(conditions.getIdsCustomerTypes())) {			
			if (isDiscretCondition) {
				sqlSelectProductSales += " AND ";
			}
			sqlSelectProductSales += "(customer_type in (" + conditions.getIdsCustomerTypes() + "))";
			isDiscretCondition = true;
		}		
		if (StringUtils.isNotEmpty(conditions.getIdsPaymentTypes())) {			
			if (isDiscretCondition) {
				sqlSelectProductSales += " AND ";
			}
			sqlSelectProductSales += "(payment_type in (" + conditions.getIdsPaymentTypes() + "))";
			isDiscretCondition = true;
		}		
		if (StringUtils.isNotEmpty(conditions.getIdsAdvertTypes())) {			
			if (isDiscretCondition) {
				sqlSelectProductSales += " AND ";
			}
			sqlSelectProductSales += "(advert_type in (" + conditions.getIdsAdvertTypes() + "))";
			isDiscretCondition = true;
		}		
		sqlSelectProductSales += " GROUP BY category_annotation, p.product_id";
		
		logger.debug("productSalesByQuery sql: {}", sqlSelectProductSales);		
		List<ProductSalesReportBean> beans = this.jdbcTemplate.query(sqlSelectProductSales,
				new Object[] { conditions.getPeriod().getStart(), conditions.getPeriod().getEnd() },
				new int[] { Types.DATE, Types.DATE },
				new RowMapper<ProductSalesReportBean>() {
					@Override
		            public ProductSalesReportBean mapRow(ResultSet rs, int rowNum) throws SQLException {		            	
		            	ProductSalesReportBean bean = new ProductSalesReportBean();
		            	bean.setProduct(wikiDao.getProductById(rs.getInt("PRODUCT_ID")));
		            	bean.setQuantity(rs.getInt("QUANTITY"));
		            	bean.setAmount(rs.getBigDecimal("AMOUNT"));		            	
		                return bean;
		            }
		        });
		return beans;	
	}
	
	public List<ProductSalesReportBean> productSalesByQueryName(ProductSalesReportConditions conditions) {

		String conditionsIdsDeliveryTypes = "";
		String conditionsIdsCustomerTypes = "";
		String conditionsIdsPaymentTypes = "";
		String conditionsIdsAdvertTypes = "";
		String conditionsFixedIdsStatuses = OrderStatuses.convertValuesToSplitedString(OrderStatuses.DELIVERED); 		

		if (conditions.getQueryName() == ReportQueryNames.YOOKASSA_PREPAYMENT) {
			
			// это оплата банковской картой - завершено (могут быть курьерка у которой завершено есть, а доставлено - нет)			
			conditionsIdsCustomerTypes =  CustomerTypes.convertValuesToSplitedString(CustomerTypes.CUSTOMER, CustomerTypes.FOREIGNER_CUSTOMER);
			conditionsIdsPaymentTypes = PaymentTypes.convertValuesToSplitedString(PaymentTypes.YANDEX_PAY);			
			conditionsFixedIdsStatuses = OrderStatuses.convertValuesToSplitedString(OrderStatuses.DELIVERED, OrderStatuses.FINISHED);
		} else if (conditions.getQueryName() == ReportQueryNames.CDEK_POSTPAYMENT) {
			
			conditionsIdsDeliveryTypes = DeliveryTypes.convertValuesToSplitedString(DeliveryTypes.CDEK_COURIER, 
					DeliveryTypes.CDEK_COURIER_ECONOMY, 
					DeliveryTypes.CDEK_PVZ_TYPICAL,
					DeliveryTypes.CDEK_PVZ_ECONOMY,
					DeliveryTypes.PICKUP);
			conditionsIdsCustomerTypes =  CustomerTypes.convertValuesToSplitedString(CustomerTypes.CUSTOMER, CustomerTypes.FOREIGNER_CUSTOMER);			
			conditionsIdsPaymentTypes = PaymentTypes.convertValuesToSplitedString(PaymentTypes.POSTPAY);
		} else if (conditions.getQueryName() == ReportQueryNames.OZON_ROCKET_POSTPAYMENT) {
			
			conditionsIdsDeliveryTypes = DeliveryTypes.convertValuesToSplitedString(DeliveryTypes.OZON_ROCKET_COURIER, 
					DeliveryTypes.OZON_ROCKET_POSTAMAT, 
					DeliveryTypes.OZON_ROCKET_PICKPOINT);			
			conditionsIdsCustomerTypes =  CustomerTypes.convertValuesToSplitedString(CustomerTypes.CUSTOMER, CustomerTypes.FOREIGNER_CUSTOMER);
			conditionsIdsPaymentTypes = PaymentTypes.convertValuesToSplitedString(PaymentTypes.POSTPAY);
		} else if (conditions.getQueryName() == ReportQueryNames.OZON) {
			
			conditionsIdsAdvertTypes = OrderAdvertTypes.convertValuesToSplitedString(OrderAdvertTypes.OZON);
		} else if (conditions.getQueryName() == ReportQueryNames.YANDEX_MARKET) {
			
			conditionsIdsAdvertTypes = OrderAdvertTypes.convertValuesToSplitedString(OrderAdvertTypes.YANDEX_MARKET);
		}
		boolean isDiscretCondition = true;		
		String sqlSelectProductSales = "SELECT p.product_id, MAX(p.name) product_name, MAX(p.category_annotation) category_annotation, SUM(oi.quantity) quantity, SUM(oi.amount) amount"
				  + " FROM sr_v_order o, sr_order_item oi, sr_v_product p"
				  + " WHERE (o.id = oi.order_id) AND"
				  + "       (oi.product_id = p.product_id) AND"
				  + "       (o.id in (select order_id from sr_order_status os WHERE (os.date_added between ? and ?) AND os.status in (" + conditionsFixedIdsStatuses + "))) ";
						
		if (StringUtils.isNotEmpty(conditionsIdsDeliveryTypes)) {			
			if (isDiscretCondition) {
				sqlSelectProductSales += " AND ";
			}
			sqlSelectProductSales += "(delivery_type in (" + conditionsIdsDeliveryTypes + "))";
			isDiscretCondition = true;
		}
		if (StringUtils.isNotEmpty(conditionsIdsCustomerTypes)) {			
			if (isDiscretCondition) {
				sqlSelectProductSales += " AND ";
			}
			sqlSelectProductSales += "(customer_type in (" + conditionsIdsCustomerTypes + "))";
			isDiscretCondition = true;
		}		
		if (StringUtils.isNotEmpty(conditionsIdsPaymentTypes)) {			
			if (isDiscretCondition) {
				sqlSelectProductSales += " AND ";
			}
			sqlSelectProductSales += "(payment_type in (" + conditionsIdsPaymentTypes + "))";
			isDiscretCondition = true;
		}		
		if (StringUtils.isNotEmpty(conditionsIdsAdvertTypes)) {			
			if (isDiscretCondition) {
				sqlSelectProductSales += " AND ";
			}
			sqlSelectProductSales += "(advert_type in (" + conditionsIdsAdvertTypes + "))";
			isDiscretCondition = true;
		}		
		sqlSelectProductSales += " GROUP BY category_annotation, p.product_id";
				
		logger.debug("productSalesByQueryName sql: {}", sqlSelectProductSales);		
		List<ProductSalesReportBean> beans = this.jdbcTemplate.query(sqlSelectProductSales,
				new Object[]{conditions.getPeriod().getStart(), conditions.getPeriod().getEnd()},
				new int[] {Types.DATE, Types.DATE },
				new RowMapper<ProductSalesReportBean>() {
					@Override
		            public ProductSalesReportBean mapRow(ResultSet rs, int rowNum) throws SQLException {		            	
		            	ProductSalesReportBean bean = new ProductSalesReportBean();
		            	bean.setProduct(wikiDao.getProductById(rs.getInt("PRODUCT_ID")));
		            	bean.setQuantity(rs.getInt("QUANTITY"));
		            	bean.setAmount(rs.getBigDecimal("AMOUNT"));		            	
		                return bean;
		            }
		        });
		return beans;			
	}	
	
	public List<SalesFunnelReportBean> salesFunnel(Pair<Date> period) {
		
		SalesFunnelReportBean bean = new SalesFunnelReportBean();
		
		BigDecimal advertBudget = wikiDao.ejectTotalAmountsByConditions(OrderAmountTypes.ADVERT_BUDGET, period);
		int visits = wikiDao.ejectTotalAmountsByConditions(OrderAmountTypes.COUNT_VISITS, period).intValue();
		int uniqueVisitors = wikiDao.ejectTotalAmountsByConditions(OrderAmountTypes.COUNT_UNIQUE_VISITORS, period).intValue();
		int newVisitors = wikiDao.ejectTotalAmountsByConditions(OrderAmountTypes.COUNT_NEW_VISITORS, period).intValue();
					
		bean.setAdvertBudget(advertBudget);
		bean.setVisits(visits);
		bean.setUniqueVisitors(uniqueVisitors);
		bean.setNewVisitors(newVisitors);
		
		OrderConditions orderConditions = new OrderConditions(ReportPeriodTypes.ANY_MONTH);
		orderConditions.setPeriod(period);
		List<Order> orders = orderDao.listOrdersByConditions(orderConditions);
		
		// LEADS
		int siteSourceLeads = 0;
		int emailSourceLeads = 0;
		int callSourceLeads = 0;
		int chatSourceLeads = 0;
		int othersSourceLeads = 0;	
		
		int paidChannelLeads = 0;
		int organicChannelLeads = 0;
		int socialNetworkChannelLeads = 0;
		int directChannelLeads = 0;
		int othersChannelLeads = 0;
		int yandexMarketChannelLeads = 0;
		int ozonMarketChannelLeads = 0;
		
		int orderTypeLeads = 0;
		int billTypeLeads = 0;
		int kpTypeLeads = 0;
		int consultationTypeLeads = 0;
		int othersTypeLeads = 0;
		int refundTypeLeads = 0;
		
		int newLeads = 0;
		int repeatLeads = 0;
		
		int personLeads = 0;
		int companyLeads = 0;
		
		Set<ProductCategory> categories = new HashSet<ProductCategory>();
		
		int totalOrders = 0;
		int personTotalOrders = 0;
		int marketPlaceTotalOrders = 0;
		
		int companyTotalOrders = 0;
						
		BigDecimal totalAmount = BigDecimal.ZERO;
		BigDecimal totalSupplierAmount = BigDecimal.ZERO;
		BigDecimal totalMarginAmount = BigDecimal.ZERO;
		
		BigDecimal personOrdersAmount = BigDecimal.ZERO;
		BigDecimal marketPlaceOrdersAmount = BigDecimal.ZERO;
		BigDecimal companyOrdersAmount = BigDecimal.ZERO;
				
		int myselfDeliveryOrders = 0;
		int courierServiceDeliveryOrders = 0;
		int postServiceDeliveryOrders = 0;
		int marketPlaceDeliveryOrders = 0;
				
		int cdekServiceDeliveryOrders = 0;
		int dellinServiceDeliveryOrders = 0;
		
		int yandexGoDeliveryOrders = 0;
		int ozonRocketDeliveryOrders = 0;
									
		BigDecimal myselfDeliveryOrdersAmount = BigDecimal.ZERO;
		BigDecimal courierServiceDeliveryOrdersAmount = BigDecimal.ZERO;
		BigDecimal postServiceDeliveryOrdersAmount = BigDecimal.ZERO;		
		BigDecimal marketPlaceDeliveryOrdersAmount = BigDecimal.ZERO;
				
		BigDecimal courierServiceDeliveryOrdersCost = BigDecimal.ZERO;
		BigDecimal postServiceDeliveryOrdersCost = BigDecimal.ZERO;
		BigDecimal marketPlaceDeliveryOrdersCost = BigDecimal.ZERO;
		
		BigDecimal personMarginAmount = BigDecimal.ZERO;
		BigDecimal marketPlaceMarginAmount = BigDecimal.ZERO;
		BigDecimal companyMarginAmount = BigDecimal.ZERO;
		
		for (Order order : orders) {
			// source
			if (order.getSourceType() == OrderSourceTypes.LID || order.getSourceType() == OrderSourceTypes.FAST_LID) {
				siteSourceLeads++;
			} else if (order.getSourceType() == OrderSourceTypes.EMAIL) {
				emailSourceLeads++;
			} else if (order.getSourceType() == OrderSourceTypes.CALL || order.getSourceType() == OrderSourceTypes.CALL_BACK) {
				callSourceLeads++;
			} else if (order.getSourceType() == OrderSourceTypes.CHAT) {
				chatSourceLeads++;
			} else {
				othersSourceLeads++;
			}
			
			// channel
			if (order.getAdvertType() == OrderAdvertTypes.ADVERT) {
				paidChannelLeads++;
			} else if (order.getAdvertType() == OrderAdvertTypes.CONTEXT) {
				organicChannelLeads++;
			} else if (order.getAdvertType() == OrderAdvertTypes.YOUTUBE) {
				socialNetworkChannelLeads++;
			} else if (order.getAdvertType() == OrderAdvertTypes.REPEAT_CALL) {
				directChannelLeads++;
			} else if (order.getAdvertType() == OrderAdvertTypes.YANDEX_MARKET) {
				yandexMarketChannelLeads++;
			} else if (order.getAdvertType() == OrderAdvertTypes.OZON) {
				ozonMarketChannelLeads++;
			} else {
				othersChannelLeads++;
			}
			
			if (order.getOrderType() == OrderTypes.ORDER) {
				orderTypeLeads++;
			} else if (order.getOrderType() == OrderTypes.BILL) {
				billTypeLeads++;
			} else if (order.getOrderType() == OrderTypes.KP) {
				kpTypeLeads++;
			} else if (order.getOrderType() == OrderTypes.CONSULTATION) {
				consultationTypeLeads++;
			} else if (order.getOrderType() == OrderTypes.REFUND) {
				refundTypeLeads++;
			} else {
				othersTypeLeads++;
			}			
			if (order.getAdvertType() == OrderAdvertTypes.REPEAT_CALL) {
				repeatLeads++;
			} else if (order.getAdvertType() == OrderAdvertTypes.LOYALTY) {
				repeatLeads++;
			} else {
				newLeads++;
			}			
			categories.add(order.getProductCategory());
						
			if (order.isBillAmount()) {
				totalOrders++;
				totalAmount = totalAmount.add(order.getAmounts().getBill());
				totalSupplierAmount = totalSupplierAmount.add(order.getAmounts().getSupplier());
				totalMarginAmount = totalMarginAmount.add(order.getAmounts().getMargin());
								
				
				if (order.getAdvertType() == OrderAdvertTypes.CDEK_MARKET || order.getAdvertType() == OrderAdvertTypes.OZON || order.getAdvertType() == OrderAdvertTypes.YANDEX_MARKET) {
					marketPlaceTotalOrders++;
					marketPlaceOrdersAmount = marketPlaceOrdersAmount.add(order.getAmounts().getBill());
					marketPlaceMarginAmount = marketPlaceMarginAmount.add(order.getAmounts().getMargin()); 
					
				} else if (order.getCustomer().isPerson()) {
					personTotalOrders++;
					personOrdersAmount = personOrdersAmount.add(order.getAmounts().getBill());
					personMarginAmount = personMarginAmount.add(order.getAmounts().getMargin()); 
					
				} else {
					companyTotalOrders++;
					companyOrdersAmount = companyOrdersAmount.add(order.getAmounts().getBill());
					companyMarginAmount = companyMarginAmount.add(order.getAmounts().getMargin());
				}				 
				
				if (order.getDelivery().getDeliveryType().isCourier()) {
					myselfDeliveryOrders++;
					myselfDeliveryOrdersAmount = myselfDeliveryOrdersAmount.add(order.getAmounts().getBill());	
				} else if (order.getDelivery().getDeliveryType().isСdek() || (order.getDelivery().getDeliveryType() == DeliveryTypes.DELLIN) || order.getDelivery().getDeliveryType().isOzonRocket() || order.getDelivery().getDeliveryType() == DeliveryTypes.YANDEX_GO) {
					courierServiceDeliveryOrders++;
					courierServiceDeliveryOrdersAmount = courierServiceDeliveryOrdersAmount.add(order.getAmounts().getBill());
					// расходы (транспорт + ккм + налоги) = доход - маржа - закупка					
					BigDecimal iCourierServiceDeliveryOrdersCost = order.getAmounts().getTotalWithDelivery().subtract(order.getAmounts().getMargin()).subtract(order.getAmounts().getSupplier()); 
					courierServiceDeliveryOrdersCost = courierServiceDeliveryOrdersCost.add(iCourierServiceDeliveryOrdersCost);
				} else if (order.getDelivery().getDeliveryType().isPost()) {
					postServiceDeliveryOrders++;
					postServiceDeliveryOrdersAmount = postServiceDeliveryOrdersAmount.add(order.getAmounts().getBill());
					postServiceDeliveryOrdersCost = postServiceDeliveryOrdersCost.add(order.getDelivery().getPrice());
				} else if (order.getDelivery().getDeliveryType() == DeliveryTypes.YANDEX_MARKET_FBS || order.getDelivery().getDeliveryType() == DeliveryTypes.OZON_FBS) {
					marketPlaceDeliveryOrders++;
					marketPlaceDeliveryOrdersAmount = marketPlaceDeliveryOrdersAmount.add(order.getAmounts().getBill());
					marketPlaceDeliveryOrdersCost = marketPlaceDeliveryOrdersCost.add(order.getDelivery().getPrice());
				} else {
					courierServiceDeliveryOrders++;
					courierServiceDeliveryOrdersAmount = courierServiceDeliveryOrdersAmount.add(order.getAmounts().getBill());
					courierServiceDeliveryOrdersCost = courierServiceDeliveryOrdersCost.add(order.getDelivery().getPrice());
				}
				
				if (order.getDelivery().getDeliveryType().isСdek()) {
					cdekServiceDeliveryOrders++;
				} else if (order.getDelivery().getDeliveryType() == DeliveryTypes.DELLIN) {
					dellinServiceDeliveryOrders++;
				} else if (order.getDelivery().getDeliveryType().isOzonRocket()) {
					ozonRocketDeliveryOrders++;
				} else if (order.getDelivery().getDeliveryType() == DeliveryTypes.YANDEX_GO) {
					yandexGoDeliveryOrders++;
				} else {
					// ?
				}				
			}
			
			if (order.getCustomer().isPerson()) {
				personLeads++;
			} else {
				companyLeads++;
			}
			
		}
		bean.setSiteSourceLeads(siteSourceLeads);
		bean.setEmailSourceLeads(emailSourceLeads);
		bean.setCallSourceLeads(callSourceLeads);
		bean.setChatSourceLeads(chatSourceLeads);
		bean.setOthersSourceLeads(othersSourceLeads);
		
		bean.setPaidChannelLeads(paidChannelLeads);
		bean.setOrganicChannelLeads(organicChannelLeads);
		bean.setSocialNetworkChannelLeads(socialNetworkChannelLeads);
		bean.setDirectChannelLeads(directChannelLeads);
		bean.setYandexMarketChannelLeads(yandexMarketChannelLeads);
		bean.setOzonMarketChannelLeads(ozonMarketChannelLeads);		
		bean.setOthersChannelLeads(othersChannelLeads);
		
		bean.setOrderTypeLeads(orderTypeLeads);
		bean.setBillTypeLeads(billTypeLeads);
		bean.setKpTypeLeads(kpTypeLeads);
		bean.setConsultationTypeLeads(consultationTypeLeads);
		bean.setRefundTypeLeads(refundTypeLeads);
		bean.setOthersTypeLeads(othersTypeLeads);

		bean.setNewLeads(newLeads);
		bean.setRepeatLeads(repeatLeads);
		
		bean.setPersonLeads(personLeads);
		bean.setCompanyLeads(companyLeads);
		
		bean.setTotalOrders(totalOrders);
		bean.setTotalAmount(totalAmount);
		bean.setTotalSupplierAmount(totalSupplierAmount);
		
		totalMarginAmount = totalMarginAmount.subtract(advertBudget);
		bean.setTotalMarginAmount(totalMarginAmount);
		
		bean.setMyselfDeliveryOrders(myselfDeliveryOrders);
		bean.setCourierServiceDeliveryOrders(courierServiceDeliveryOrders);
		bean.setPostServiceDeliveryOrders(postServiceDeliveryOrders);
		bean.setMarketPlaceDeliveryOrders(marketPlaceDeliveryOrders);
				
		bean.setCdekDeliveryOrders(cdekServiceDeliveryOrders);
		bean.setDellinDeliveryOrders(dellinServiceDeliveryOrders);
		
		bean.setOzonRocketDeliveryOrders(ozonRocketDeliveryOrders);
		bean.setYandexGoDeliveryOrders(yandexGoDeliveryOrders);	
				
		bean.setMyselfDeliveryOrdersAmount(myselfDeliveryOrdersAmount);
		bean.setCourierServiceDeliveryOrdersAmount(courierServiceDeliveryOrdersAmount);
		bean.setPostServiceDeliveryOrdersAmount(postServiceDeliveryOrdersAmount);
		bean.setMarketPlaceDeliveryOrdersAmount(marketPlaceDeliveryOrdersAmount);
				
		bean.setCourierServiceDeliveryOrdersCost(courierServiceDeliveryOrdersCost);
		bean.setPostServiceDeliveryOrdersCost(postServiceDeliveryOrdersCost);
		bean.setMarketPlaceDeliveryOrdersCost(marketPlaceDeliveryOrdersCost);		
		
		bean.setPersonTotalOrders(personTotalOrders);
		bean.setMarketPlaceTotalOrders(marketPlaceTotalOrders);
		bean.setCompanyTotalOrders(companyTotalOrders);
		
		bean.setPersonOrdersAmount(personOrdersAmount);
		bean.setMarketPlaceOrdersAmount(marketPlaceOrdersAmount);		
		bean.setCompanyOrdersAmount(companyOrdersAmount);
		
		bean.setPersonMarginAmount(personMarginAmount);
		bean.setMarketPlaceMarginAmount(marketPlaceMarginAmount);
		
		bean.setCompanyMarginAmount(companyMarginAmount);
		
		Map<OrderAmountTypes, BigDecimal> totalAmounts = orderDao.calcTotalOrdersAmountsByConditions(orders, period);
		bean.setPostpayAmount(totalAmounts.get(OrderAmountTypes.POSTPAY));
		
		SupplierStock stock = wikiDao.getSupplierStocks();
		bean.setStockAmount(stock.getTotalSupplierAmount());
		
		// расчет средней ставки для рекламы
		// ставка = (прибыль/2) / число кликов 
									
		
		/*		 
				(101, "рекламный бюджет за период"),
				(102, "число кликов за период"),
				(107, "число уникальных посетителей за период"),
				(111, "число новых посетителей за период"),
				
		сеансы
			organic search органика
			paid платный трафик
			social network социальные сети
			direct прямые переходы
			others прочие
			
		+ новые посетители
		+ уникальные посетители		
			
		>>>>> сеансы		
				+ сеансы за период ...
					+ стоимость сеанса = рекламный бюджет / число сеансов
					
		>>>>> уники			 
				+ уники за период ....
					+ стоимость посетителя = рекламный бюджет / число уников
					
		>>>>> новые посетители			 
				+ новые посетители за период ....
				+ % новых = новые / уникам
				
		>>>>> повторно пришли посетители			 
				+ постоянные клиенты за период = уники - новые
				+ % постоянных = постоянные / уникам		
								
		>>>>> заявки		
				+ лиды за период ....
											
					источник
						+ лид с сайта
						+ письмо
						+ звонок
						+ чат
						+ прочие
										
					канал
						+ реклама
						+ поиск
						+ ютуб
						+ повторное обращение
						+ прочие
						
					тип
						+ заказ
						+ счет
						+ кп
						+ консультация
						+ прочие	
						
					+ новые...
					+ повторное обращение ....	
						
					+ категории			
								
				+ конверсия = число лидов / число уников
				              число лидов / число посещений
				+ стоимость лида = рекламный бюджет / число лидов	
						
		>>>>> заказы								
				+ заказы за период ...
				+ конверсия = число заказов / число лидов
				
		>>>>> деньги		
				+ суммарный доход
				+ средний чек	= суммарный доход / число заказов
				
				+ суммарная закупка
				+ суммарная прибыль = доход - закупку				
				+ средняя прибыль = суммарная прибыль / число заказов  
		*/	
		
		List<SalesFunnelReportBean> results = new ArrayList<SalesFunnelReportBean>();
		results.add(bean);
		return results;
	}
}
