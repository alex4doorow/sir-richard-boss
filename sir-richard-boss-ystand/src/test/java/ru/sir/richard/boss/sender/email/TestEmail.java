package ru.sir.richard.boss.sender.email;

import java.math.BigDecimal;

import ru.sir.richard.boss.model.data.Customer;
import ru.sir.richard.boss.model.data.Order;
import ru.sir.richard.boss.model.data.OrderItem;
import ru.sir.richard.boss.model.data.Product;
import ru.sir.richard.boss.model.types.DeliveryTypes;
import ru.sir.richard.boss.model.types.OrderEmailStatuses;
import ru.sir.richard.boss.model.types.OrderStatuses;
import ru.sir.richard.boss.model.types.OrderTypes;
import ru.sir.richard.boss.model.utils.sender.AnySender;
import ru.sir.richard.boss.model.utils.sender.SendingResponseStatus;
import ru.sir.richard.boss.model.utils.sender.email.EmailSender;

public class TestEmail {
	
	public static void main(String[] args) {
		
		Order order = new Order();
		order.setNo(5123);
		order.getDelivery().setDeliveryType(DeliveryTypes.CDEK_PVZ_TYPICAL);
		order.getDelivery().setTrackCode("1097448160");
		order.getAmounts().setTotalWithDelivery(BigDecimal.valueOf(5990));
		order.setOrderType(OrderTypes.ORDER);		
		order.setStatus(OrderStatuses.DELIVERED);
		
		Product product = new Product();
		product.setName("Ультразвуковой отпугиватель мышей и крыс \"ГРАД А-1000 ПРО+\"");
				
		OrderItem item = new OrderItem();
		item.setProduct(product);
		item.setQuantity(1);		
		order.getItems().add(item);
					
		Customer customer = new Customer();
		customer.setFirstName("Михаил"); 
		customer.setMiddleName("Аркадьевич");
		customer.setLastName("Ниязов");
		customer.setEmail("alex4doorow@gmail.com");
		customer.setPhoneNumber("(916) 169-90-99");
		order.setCustomer(customer);
		
		AnySender sender = new EmailSender();
		SendingResponseStatus result = sender.sendFeedback(order, OrderEmailStatuses.FEEDBACK);		
		System.out.println(result);
		
	}

}
