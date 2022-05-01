package ru.sir.richard.boss.crm;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import ru.sir.richard.boss.api.market.OzonMarketApi;
import ru.sir.richard.boss.dao.AnyDaoImpl;
import ru.sir.richard.boss.dao.CustomerDao;
import ru.sir.richard.boss.dao.OrderDao;
import ru.sir.richard.boss.dao.WikiDao;
import ru.sir.richard.boss.model.calc.AnyOrderTotalAmountsCalculator;
import ru.sir.richard.boss.model.data.ForeignerCustomer;
import ru.sir.richard.boss.model.data.Order;
import ru.sir.richard.boss.model.data.OrderAmounts;
import ru.sir.richard.boss.model.data.OrderItem;
import ru.sir.richard.boss.model.data.Product;
import ru.sir.richard.boss.model.data.conditions.CustomerConditions;
import ru.sir.richard.boss.model.factories.OrderTotalAmountsCalculatorFactory;
import ru.sir.richard.boss.model.types.OrderStatuses;
import ru.sir.richard.boss.model.utils.DateTimeUtils;
import ru.sir.richard.boss.model.utils.Pair;


@Repository
public class OzonExecutorImpl extends AnyDaoImpl implements CrmExecutable, OzonExecutor {
	
private final Logger logger = LoggerFactory.getLogger(OzonExecutorImpl.class);
	
	private Date executorDate;
	
	@Autowired
	private Environment environment;
	
	@Autowired
	private WikiDao wikiDao;
	
	@Autowired
	private OrderDao orderDao;
	
	@Autowired
	private CustomerDao customerDao;

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
	
	private boolean isExistOzonOrder(int ozonNo) {
		
		final String sqlSelectCountCrmOrder = "SELECT count(*) COUNT_ID FROM sr_order_crm_connect WHERE parent_crm_id = ?";
		Integer countOzon = this.jdbcTemplate.queryForObject(sqlSelectCountCrmOrder,
		        new Object[]{
		        		ozonNo
		        		},
		        new RowMapper<Integer>() {
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
		
		OzonMarketApi ozonMarketApi = new OzonMarketApi(this.environment);		
		Pair<Date> period = new Pair<Date>(DateTimeUtils.beforeAnyDate(executorDate, 1), DateTimeUtils.afterAnyDate(executorDate, 1));		
		List<Order> crmOrders = ozonMarketApi.getOrders(period, "awaiting_deliver"); // awaiting_deliver
		
		for (Order crmOrder : crmOrders) {
			
			if (isExistOzonOrder(crmOrder.getId())) {
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
			
			 //order.getAmounts().setValue(OrderAmountTypes.TOTAL, rs.getBigDecimal("TOTAL"));
             //order.getAmounts().setValue(OrderAmountTypes.TOTAL_WITH_DELIVERY, rs.getBigDecimal("TOTAL"));
			
			//orderStatusHistory
			
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

}
