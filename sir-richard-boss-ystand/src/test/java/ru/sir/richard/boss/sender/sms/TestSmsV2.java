package ru.sir.richard.boss.sender.sms;

import java.math.BigDecimal;

import ru.sir.richard.boss.model.data.Customer;
import ru.sir.richard.boss.model.data.Order;
import ru.sir.richard.boss.model.types.DeliveryTypes;
import ru.sir.richard.boss.model.types.OrderStatuses;
import ru.sir.richard.boss.model.types.OrderTypes;
import ru.sir.richard.boss.model.utils.sender.AnySender;
import ru.sir.richard.boss.model.utils.sender.SendingResponseStatus;
import ru.sir.richard.boss.model.utils.sender.sms.SmsSender;

public class TestSmsV2 {
	
	public static void main(String[] args) {
		
		// *** 1 sdek, delivering
		Order order = new Order();
		order.setNo(5123);
		order.getDelivery().setDeliveryType(DeliveryTypes.CDEK_PVZ_TYPICAL);
		order.getDelivery().setTrackCode("1097448160");
		order.getAmounts().setTotalWithDelivery(BigDecimal.valueOf(5990));
		order.setOrderType(OrderTypes.ORDER);		
		order.setStatus(OrderStatuses.DELIVERING);
				
		Customer customer = new Customer();
		customer.setFirstName("Михаил"); 
		customer.setMiddleName("Аркадьевич");
		customer.setLastName("Ниязов");
		customer.setEmail("alex4doorow@gmail.com");
		customer.setPhoneNumber("(916) 169-90-99");
		order.setCustomer(customer);
		
		AnySender sender = new SmsSender();
		SendingResponseStatus result = sender.sendOrder(order);		
		System.out.println(result);
		/*
			
		// *** 2 post, delivering
		order = new Order();
		order.setNo(5123);
		order.getDelivery().setDeliveryType(DeliveryTypes.POST_TYPICAL);
		order.getDelivery().setTrackCode("1071992601934");
		order.getAmounts().setTotalWithDelivery(BigDecimal.valueOf(5990));
				
		order.setOrderType(OrderTypes.BILL);		
		order.setStatus(OrderStatuses.DELIVERING);
				
		customer = new Customer();
		customer.setFirstName("Михаил"); 
		customer.setMiddleName("Аркадьевич");
		customer.setLastName("Ниязов");
		customer.setEmail("alex4doorow@gmail.com");
		customer.setPhoneNumber("(916) 169-90-99");
		order.setCustomer(customer);
		
		sender = new SmsSender();
		result = sender.send(order);		
		System.out.println(result);
		
		// *** 3 cdek, ready 
		order = new Order();
		order.setNo(5123);
		order.getDelivery().setDeliveryType(DeliveryTypes.SDEK_PVZ_TYPICAL);
		order.getDelivery().setTrackCode("1097448161");
		order.getAmounts().setTotalWithDelivery(BigDecimal.valueOf(5990));
		Address deliveryAddress = new Address(Countries.RUSSIA, AddressTypes.MAIN);
		deliveryAddress.setAddress("г. Нижний Новгород ПВЗ на Ленина д.77");
		order.getDelivery().setAddress(deliveryAddress);
				
		order.setOrderType(OrderTypes.BILL);		
		order.setStatus(OrderStatuses.READY_GIVE_AWAY);
				
		customer = new Customer();
		customer.setFirstName("Михаил"); 
		customer.setMiddleName("Аркадьевич");
		customer.setLastName("Ниязов");
		customer.setEmail("alex4doorow@gmail.com");
		customer.setPhoneNumber("(916) 169-90-99");
		order.setCustomer(customer);
		
		sender = new SmsSender();
		result = sender.send(order);		
		System.out.println(result);
	
		// *** 4 post, ready
		order = new Order();
		order.setNo(5123);
		order.getDelivery().setDeliveryType(DeliveryTypes.POST_TYPICAL);
		order.getDelivery().setTrackCode("1071992601934");
		order.getAmounts().setTotalWithDelivery(BigDecimal.valueOf(5990));
		deliveryAddress = new Address(Countries.RUSSIA, AddressTypes.MAIN);
		deliveryAddress.setAddress("663430, Красноярский край → Богучанский р-н → Богучаны с. ул.Ленина 72 кв.2");
		order.getDelivery().setAddress(deliveryAddress);
						
		order.setOrderType(OrderTypes.BILL);		
		order.setStatus(OrderStatuses.READY_GIVE_AWAY);
				
		customer = new Customer();
		customer.setFirstName("Михаил"); 
		customer.setMiddleName("Аркадьевич");
		customer.setLastName("Ниязов");
		customer.setEmail("alex4doorow@gmail.com");
		customer.setPhoneNumber("(916) 169-90-99");
		order.setCustomer(customer);
		
		sender = new SmsSender();
		result = sender.send(order);		
		System.out.println(result);

		*/	      	      
	}
	
	/*
	public void testSms() {
		logger.debug("testSms");
		
		Order order = new Order();
		order.setNo(5123);
		order.getDelivery().setTrackCode("1097448160");
		order.getAmounts().setPostpay(BigDecimal.valueOf(5990));
		
		order.setOrderType(OrderTypes.BILL);		
		order.setStatus(OrderStatuses.DELIVERING);
		
		Customer customer = new Customer();
		customer.setFirstName("Михаил"); 
		customer.setMiddleName("Аркадьевич");
		customer.setLastName("Ниязов");
		customer.setEmail("alex4doorow@gmail.com");
		customer.setPhoneNumber("(916) 169-90-99");
		order.setCustomer(customer);
		//order.getCustomer().isPerson();
		
		
		//String result = f.sendSms(order);
		
		//DELIVERING
		//READY_GIVE_AWAY
		
		//phoneNumberDigit	
	}
	*/
}
