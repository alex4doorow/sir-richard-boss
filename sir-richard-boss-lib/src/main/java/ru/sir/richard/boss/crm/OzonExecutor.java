package ru.sir.richard.boss.crm;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import ru.sir.richard.boss.api.market.OzonMarketApiService;
import ru.sir.richard.boss.dao.AnyDaoImpl;
import ru.sir.richard.boss.dao.CustomerDao;
import ru.sir.richard.boss.dao.OrderDao;
import ru.sir.richard.boss.dao.WikiDao;
import ru.sir.richard.boss.model.calc.AnyOrderTotalAmountsCalculator;
import ru.sir.richard.boss.model.data.ForeignerCustomer;
import ru.sir.richard.boss.model.data.Order;
import ru.sir.richard.boss.model.data.OrderAmounts;
import ru.sir.richard.boss.model.data.OrderItem;
import ru.sir.richard.boss.model.data.OrderStatusItem;
import ru.sir.richard.boss.model.data.Product;
import ru.sir.richard.boss.model.data.conditions.CustomerConditions;
import ru.sir.richard.boss.model.data.conditions.OrderConditions;
import ru.sir.richard.boss.model.factories.OrderTotalAmountsCalculatorFactory;
import ru.sir.richard.boss.model.types.CrmTypes;
import ru.sir.richard.boss.model.types.DeliveryTypes;
import ru.sir.richard.boss.model.types.OrderAdvertTypes;
import ru.sir.richard.boss.model.types.OrderStatuses;
import ru.sir.richard.boss.model.utils.DateTimeUtils;
import ru.sir.richard.boss.model.utils.Pair;

@Service
public class OzonExecutor extends AnyDaoImpl implements CrmExecutable {

    private final Logger logger = LoggerFactory.getLogger(OzonExecutor.class);

    private Date executorDate;

    @Autowired
    private WikiDao wikiDao;

    @Autowired
    private OrderDao orderDao;

    @Autowired
    private CustomerDao customerDao;
    
	@Autowired
	private OzonMarketApiService ozonMarketApiService;

    @Override
    public void setExecutorDate(Date executorDate) {
	this.executorDate = executorDate;
    }

    @Override
    public void run() {
	logger.debug("run(): start");
	importFromCrm();
	logger.debug("run(): end");
    }

	private boolean isExistOzonOrder(String ozonNo) {

		final String sqlSelectCountCrmOrder = "SELECT count(*) COUNT_ID FROM sr_order_crm_connect WHERE parent_crm_code = ?";
		Integer countOzon = this.jdbcTemplate.queryForObject(sqlSelectCountCrmOrder, new Object[] { ozonNo },
				new int[] { Types.VARCHAR }, new RowMapper<Integer>() {
					@Override
					public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
						return rs.getInt("COUNT_ID");
					}
				});
		if (countOzon > 0) {
			return true;
		}
		return false;
	}

	private List<Order> importFromCrm() {
		Pair<Date> period = new Pair<Date>(DateTimeUtils.beforeAnyDate(executorDate, 1),
				DateTimeUtils.afterAnyDate(executorDate, 1));
		List<Order> crmOrders = ozonMarketApiService.getOrders(period, OzonMarketApiService.OZON_MARKET_STATUS_AWAITING_DELIVER);
		for (Order crmOrder : crmOrders) {
			if (isExistOzonOrder(crmOrder.getExternalCrmByCode(CrmTypes.OZON).getParentCode())) {
				continue;
			}
			crmOrder.setOrderDate(DateTimeUtils.sysDate());
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
			}
			crmOrder.setProductCategory(wikiDao.getCategoryById(0));
			for (OrderItem crmOrderItem : crmOrder.getItems()) {
				Product checkProduct = wikiDao.findProductBySku(crmOrderItem.getProduct().getSku());
				if (checkProduct == null) {
					continue;
				}
				crmOrderItem.setProduct(checkProduct);
				crmOrderItem.setSupplierAmount(checkProduct.getSupplierPrice());

				BigDecimal total = crmOrderItem.getPrice().multiply(BigDecimal.valueOf(crmOrderItem.getQuantity()));

				crmOrderItem.setAmount(total);
				crmOrder.setProductCategory(crmOrderItem.getProduct().getCategory());
			}

			crmOrder.setNo(orderDao.nextOrderNo());
			AnyOrderTotalAmountsCalculator calculator = OrderTotalAmountsCalculatorFactory.createCalculator(crmOrder);
			OrderAmounts recalcOrderAmounts = calculator.calc();
			crmOrder.setAmounts(recalcOrderAmounts);

			int orderId = orderDao.addOrder(crmOrder);
			orderDao.addCrmOrderImport(orderId, crmOrder);

			Order newOrder = orderDao.findById(orderId);
			newOrder.setStatus(OrderStatuses.APPROVED);
			orderDao.changeFullStatusOrder(newOrder);
		}
		return crmOrders;
	}

    /**
     * Актуализации наложки от озона озон - алгоритм: завершить все заказы озона в
     * состоянии "получено" по алгоритму: если дата 26.10, то все полученные заказы
     * с 01.10. по 15.10 в статусе "получено" -> "завершено" если дата 15.10, то все
     * полученные заказы с 15.09. по 31.09 в статусе "получено" -> "завершено"
     */
	public String actualizationPostpay(Date executorDate) {

		logger.debug("actualizationPostpay() : start");
		// 1 получить от даты 27.10 дату 25.10
		Date recalculatorDate = findRecalculatedDate(executorDate);
		// 2 получить от 25.10 дату 15.10, от 15.10 дату 31.09
		Date deliveredOderDate = findDeliveredOrdersDate(recalculatorDate);

		// 3 получить список <= 15.10 заказов озон со статусом получено
		// 4 перевести заказы в статус завершен

		OrderConditions conditions = new OrderConditions();
		Set<OrderStatuses> statuses = new HashSet<OrderStatuses>();
		statuses.add(OrderStatuses.DELIVERED);
		conditions.setStatuses(statuses);

		Set<DeliveryTypes> deliveryTypes = new HashSet<DeliveryTypes>();
		deliveryTypes.add(DeliveryTypes.OZON_FBS);
		conditions.setDeliveryTypes(deliveryTypes);

		Set<OrderAdvertTypes> advertTypes = new HashSet<OrderAdvertTypes>();
		advertTypes.add(OrderAdvertTypes.OZON);
		conditions.setAdvertTypes(advertTypes);

		conditions.setPeriodExist(false);
		conditions.setTrackCodeNotExist(false);

		List<Order> ozonOrders = orderDao.listOrdersByConditions(conditions);

		int deliveredCount = 0;
		String debugInfo = "";
		String debugInfoItem = "";
		List<Order> willModifingOrders = new ArrayList<Order>();
		for (Order ozonOrder : ozonOrders) {

			List<OrderStatusItem> ozonOrderStatuses = orderDao.getStatusesByOrder(ozonOrder);
			ozonOrder.setStatuses(ozonOrderStatuses);

			OrderStatusItem deliveredStatus = ozonOrder.getLastStatusItem();
			if (deliveredStatus.getStatus() == OrderStatuses.DELIVERED
					&& deliveredStatus.getAddedDate().before(deliveredOderDate)) {

				// заказ получен и получен до даты оплаты --> FINISHED
				ozonOrder.setStatus(OrderStatuses.FINISHED);
				willModifingOrders.add(ozonOrder);

				willModifingOrders.add(ozonOrder);
				debugInfoItem = "- получен " + DateTimeUtils.defaultFormatDate(deliveredStatus.getAddedDate()) + ", "
						+ ozonOrder.getNo() + ", " + ozonOrder.getCustomer().getViewShortName() + ", "
						+ ozonOrder.getDelivery().getAddress().getAddress();
				debugInfo = debugInfo + debugInfoItem + "<br>";
				logger.debug("получен {}: {}, {}, {}", DateTimeUtils.defaultFormatDate(deliveredStatus.getAddedDate()),
						ozonOrder.getNo(), ozonOrder.getCustomer().getViewShortName(),
						ozonOrder.getDelivery().getAddress().getAddress());
				deliveredCount++;
			}
		}
		for (Order willModifiedOrder : willModifingOrders) {
			Order currentOrder = orderDao.findById(willModifiedOrder.getId());
			OrderStatusItem newOrderStatusValue = willModifiedOrder.getLastStatusItem();
			orderDao.changeStatusOrder(willModifiedOrder.getId(), willModifiedOrder.getStatus(),
					currentOrder.getAnnotation(), currentOrder.getDelivery().getTrackCode(), newOrderStatusValue);
		}
		String resultNow = String.format("Всего заказов было исключено из постоплаты ОЗОН: %d", deliveredCount);
		if (deliveredCount > 0) {
			resultNow = resultNow + ":<br>" + debugInfo;
		}
		logger.debug("actualizationPostpay() : end");
		return resultNow;
	}

	/**
	 * Определение даты пересчета для закрытия погашенных заказов
	 * @param executorDate
	 * @return
	 */
	public Date findRecalculatedDate(Date executorDate) {

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(executorDate);

		int day = calendar.get(Calendar.DAY_OF_MONTH);
		int month = calendar.get(Calendar.MONTH);
		int year = calendar.get(Calendar.YEAR);

		int nDay = day;
		int nMonth = month;
		int nYear = year;
		if (day >= 25) {
			nDay = 25;
			nMonth = month;
			nYear = year;
		}
		if (day < 15) {
			nDay = 25;
			nMonth = (month > 0) ? month - 1 : 12;
			nYear = (month > 0) ? year : year - 1;
		}
		if (day >= 15 && day < 25) {
			nDay = 15;
			nMonth = month;
		}
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		calendar.set(Calendar.DAY_OF_MONTH, nDay);
		calendar.set(Calendar.MONTH, nMonth);
		calendar.set(Calendar.YEAR, nYear);

		return calendar.getTime();
	}

	public Date findDeliveredOrdersDate(Date recalculatorDate) {

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(recalculatorDate);

		int day = calendar.get(Calendar.DAY_OF_MONTH);
		if (day == 25) {
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MILLISECOND, 0);
			calendar.set(Calendar.DAY_OF_MONTH, 15);
			return calendar.getTime();
		}
		if (day == 15) {
			return DateTimeUtils.beforeDate(DateTimeUtils.firstDayOfMonth(recalculatorDate));
		}
		return null;
	}
}
