package ru.sir.richard.boss.crm;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import ru.sir.richard.boss.dao.AnyDaoImpl;
import ru.sir.richard.boss.dao.OrderDao;
import ru.sir.richard.boss.dao.WikiDao;
import ru.sir.richard.boss.model.data.Customer;
import ru.sir.richard.boss.model.data.Order;
import ru.sir.richard.boss.model.data.OrderItem;
import ru.sir.richard.boss.model.data.Product;
import ru.sir.richard.boss.model.types.OrderAdvertTypes;
import ru.sir.richard.boss.model.types.OrderSourceTypes;
import ru.sir.richard.boss.model.types.OrderTypes;
import ru.sir.richard.boss.model.types.PaymentTypes;
import ru.sir.richard.boss.model.types.StoreTypes;
import ru.sir.richard.boss.model.utils.DateTimeUtils;
import ru.sir.richard.boss.model.utils.TextUtils;
import ru.sir.richard.boss.model.utils.sender.email.EmailUtils;

@Repository
public class EmailFastOrderExecutorImpl extends AnyDaoImpl implements CrmExecutable, EmailFastOrderExecutor {

private final Logger logger = LoggerFactory.getLogger(OpencartExecutorImpl.class);
	
	private Date executorDate;
	
	@Autowired
	private Environment environment;
	
	@Autowired
	private WikiDao wikiDao;
	
	@Autowired
	private OrderDao orderDao;
	
	public EmailFastOrderExecutorImpl() {		
		
	}

	public Date getExecutorDate() {
		return executorDate;
	}

	public void setExecutorDate(Date executorDate) {
		this.executorDate = executorDate;
	}

	@Override
	public void run() {
		logger.debug("run(): start");
		importStoreOrders(StoreTypes.SR);
		importStoreOrders(StoreTypes.PM);
		logger.debug("run(): end");
	}
	
    private List<Order> importStoreOrders(StoreTypes store) {
		
		EmailUtils emailManager = new EmailUtils(environment);		
	    List<Order> crmOrders = new ArrayList<Order>();    
	    
	    List<String> textMessages = emailManager.loadMessagesFromEmail(store, executorDate, "lead");
	    for (String textMessage : textMessages) {
			Order order = extractOrderByMessage(store, textMessage);
			if (order != null) {
				crmOrders.add(order);					
			}
		}	
		for (Order crmOrder : crmOrders) {	    	
	    	if (getCountOrdersByConditions(crmOrder) == 0) {
	    		crmOrder.setNo(orderDao.nextOrderNo());
	    		orderDao.addOrder(crmOrder);	    		
	    	}
		}		
		return crmOrders;		
	}	
	
	private Order extractOrderByMessage(StoreTypes store, String text) {
		Order order = null;
		try {			
			
			String[] textValues = text.split("\r\n");

			if ("Быстрый заказ".equals(textValues[0])) {
				logger.info("text message:{}", text);

				order = new Order();
				order.setOrderType(OrderTypes.ORDER);
	            order.setPaymentType(PaymentTypes.POSTPAY);
	            order.setSourceType(OrderSourceTypes.FAST_LID);
	            order.setAdvertType(OrderAdvertTypes.ADVERT);
	            order.setStore(store);	            
	            order.setAnnotation(textValues[5].substring(13).trim());

				Date orderDate = DateTimeUtils.stringToDate(textValues[2].substring(13), "dd.MM.yyyy HH:mm");					
				orderDate = DateTimeUtils.truncateDate(orderDate);
				order.setOrderDate(orderDate);

				Customer customer = new Customer();
				customer.setFirstName(textValues[3].substring(10).trim());
				String phoneNumber = textValues[4].substring(9).trim();
				phoneNumber = TextUtils.formatPhoneNumber(phoneNumber);

				customer.setPhoneNumber(phoneNumber);				
				order.setCustomer(customer);

				String productName = textValues[7].substring(7).trim();
				OrderItem orderItem = new OrderItem();
				
				Product product = wikiDao.findSingleProductByName(productName);
				orderItem.setNo(1);
				orderItem.setProduct(product);
				orderItem.setSupplierAmount(product.getSupplierPrice());					
				orderItem.setQuantity(1);
				orderItem.setPrice(product.getPrice());
				orderItem.setAmount(product.getPrice());					
				order.setProductCategory(orderItem.getProduct().getCategory());
				order.getItems().add(orderItem);
			}						
			
		} catch (Exception e) {
			logger.error("email:{} {}", "fail", e);
		}		
		return order;		
	}
	
	private int getCountOrdersByConditions(Order order) {
		final String sqlSelectCountOrders = "SELECT count(*) COUNT_ORDER from sr_v_order"
				+ " WHERE phone_number like ? and order_date = ?";
		Integer count = this.jdbcTemplate.queryForObject(sqlSelectCountOrders,
		        new Object[]{order.getCustomer().getViewPhoneNumber(), order.getOrderDate()},
		        new RowMapper<Integer>() {
					@Override
		            public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
		            	return Integer.valueOf(rs.getInt("COUNT_ORDER"));	
		            }
		        });
		return count.intValue();
	}

}
