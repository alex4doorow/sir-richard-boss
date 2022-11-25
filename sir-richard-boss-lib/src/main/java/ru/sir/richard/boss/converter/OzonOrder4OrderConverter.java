package ru.sir.richard.boss.converter;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import ru.sir.richard.boss.api.market.OzonMarketApiService;
import ru.sir.richard.boss.model.data.Address;
import ru.sir.richard.boss.model.data.Customer;
import ru.sir.richard.boss.model.data.Order;
import ru.sir.richard.boss.model.data.OrderExternalCrm;
import ru.sir.richard.boss.model.data.OrderItem;
import ru.sir.richard.boss.model.data.OrderStatusItem;
import ru.sir.richard.boss.model.data.Product;
import ru.sir.richard.boss.model.dto.OzonOrderDto;
import ru.sir.richard.boss.model.dto.OzonOrdersDto;
import ru.sir.richard.boss.model.types.AddressTypes;
import ru.sir.richard.boss.model.types.Countries;
import ru.sir.richard.boss.model.types.CrmStatuses;
import ru.sir.richard.boss.model.types.CrmTypes;
import ru.sir.richard.boss.model.types.DeliveryTypes;
import ru.sir.richard.boss.model.types.OrderAdvertTypes;
import ru.sir.richard.boss.model.types.OrderSourceTypes;
import ru.sir.richard.boss.model.types.OrderStatuses;
import ru.sir.richard.boss.model.types.OrderTypes;
import ru.sir.richard.boss.model.types.PaymentDeliveryTypes;
import ru.sir.richard.boss.model.types.PaymentTypes;
import ru.sir.richard.boss.model.types.StoreTypes;

@Component
public class OzonOrder4OrderConverter {
	
	@Autowired
	private Environment environment;
	
	private ModelMapper modelMapper;
	
    public OzonOrder4OrderConverter() {
		super();
		modelMapper = new ModelMapper();		
	}	

    public OzonOrderDto convertToDto(Order order) {
        OzonOrderDto ozonOrderDto = modelMapper.map(order, OzonOrderDto.class);
        //postDto.setSubmissionDate(post.getSubmissionDate(),
        //        userService.getCurrentUser().getPreference().getTimezone());
        return ozonOrderDto;
    }

    /**
     * @param ozonOrderDto
     * @return
     * @throws ParseException
     */
    public Order convertToEntity(OzonOrderDto ozonOrderDto) throws ParseException {
        Order order = modelMapper.map(ozonOrderDto, Order.class);
        order.setId(0);
        order.setOrderDate(ozonOrderDto.getResult().getInProcessAt());
		order.setSourceType(OrderSourceTypes.LID);                
        order.setStore(StoreTypes.PM);
        order.setAdvertType(OrderAdvertTypes.OZON);
		order.setAnnotation("");
		
		String ozonOrderDtoStatus = ozonOrderDto.getResult().getStatus();
		/*
		acceptance_in_progress — идёт приёмка,
		awaiting_approve — ожидает подтверждения,
		awaiting_packaging — ожидает упаковки,
		awaiting_deliver — ожидает отгрузки,
		arbitration — арбитраж,
		client_arbitration — клиентский арбитраж доставки,
		delivering — доставляется,
		driver_pickup — у водителя,
		delivered — доставлено,
		cancelled — отменено,
		not_accepted — не принят на сортировочном центре.
		*/		
		if (ozonOrderDtoStatus.equalsIgnoreCase(OzonMarketApiService.OZON_MARKET_STATUS_AWAITING_APPROVE)) {
			order.setStatus(OrderStatuses.BID);			
		} else if (ozonOrderDtoStatus.equalsIgnoreCase("awaiting_packaging")) {
			order.setStatus(OrderStatuses.BID);			
		} else if (ozonOrderDtoStatus.equalsIgnoreCase("awaiting_deliver")) {
			order.setStatus(OrderStatuses.APPROVED);			
		} else if (ozonOrderDtoStatus.equalsIgnoreCase("delivering")) {
			order.setStatus(OrderStatuses.DELIVERING);		
		} else if (ozonOrderDtoStatus.equalsIgnoreCase("delivered")) {
			order.setStatus(OrderStatuses.DELIVERED);			
		} else if (ozonOrderDtoStatus.equalsIgnoreCase("cancelled")) {
			order.setStatus(OrderStatuses.CANCELED);			
		} else {
			order.setStatus(OrderStatuses.UNKNOWN);
		}  
		OrderStatusItem orderStatusItem = new OrderStatusItem(order);
		orderStatusItem.setAddedDate(new Date());			
		orderStatusItem.setStatus(order.getStatus());
		orderStatusItem.setCrmStatus(ozonOrderDtoStatus);
		orderStatusItem.setCrmSubStatus("");
		order.addStatusItem(orderStatusItem);
				
		Customer customer = new Customer();
		customer.setCountry(Countries.RUSSIA);		
		Address deliveryAddress = new Address(Countries.RUSSIA, AddressTypes.MAIN);		
		String deliveryAddressText = environment.getProperty("ozon.market.ozon.default.customer.address");
		deliveryAddress.setAddress(deliveryAddressText);		
		customer.getMainAddress().setAddress(deliveryAddressText);		
		if (ozonOrderDto.getResult().getDeliveryMethod().getTplProviderId() == environment.getProperty("ozon.market.ozon.provider", Integer.class)) {
			
			customer.setFirstName(environment.getProperty("ozon.market.ozon.default.customer.firstName"));
			customer.setLastName(environment.getProperty("ozon.market.ozon.default.customer.lastName"));			
			customer.setPhoneNumber(environment.getProperty("ozon.market.ozon.default.customer.phoneNumber"));				
			customer.setEmail(environment.getProperty("ozon.market.ozon.default.customer.email"));								
			if (ozonOrderDto.getResult().getAnalyticsData() != null && StringUtils.isNotEmpty(ozonOrderDto.getResult().getAnalyticsData().getAddress())) {				
				deliveryAddressText = ozonOrderDto.getResult().getAnalyticsData().getAddress();				
			}
			deliveryAddress.setAddress(deliveryAddressText);
	        order.getDelivery().setAddress(deliveryAddress);						
		}
		
        order.getDelivery().getAddress().getCarrierInfo().getCourierInfo().setDeliveryDate(ozonOrderDto.getResult().getShipmentDate());        
        order.getDelivery().setPaymentDeliveryType(PaymentDeliveryTypes.CUSTOMER);		                
        order.getDelivery().setTrackCode(ozonOrderDto.getResult().getTrackingNumber());
        order.getDelivery().setDeliveryType(findDeliveryTypeByOzonOrderDto(ozonOrderDto));
        
        order.setOrderType(OrderTypes.ORDER);
    	order.setPaymentType(PaymentTypes.POSTPAY);    	
    	order.setCustomer(customer);        
        order.setExternalCrms(Collections.singletonList(new OrderExternalCrm(CrmTypes.OZON, 
        		CrmStatuses.SUCCESS, 
        		ozonOrderDto.getResult().getOrderId(), 
        		ozonOrderDto.getResult().getPostingNumber())));

        final List<OrderItem> orderItems = new ArrayList<OrderItem>();
        Arrays.stream(ozonOrderDto.getResult().getProducts()).forEach((ozonProduct) -> {         	
        	OrderItem orderItem = new OrderItem();
			orderItem.setNo(orderItems.size());
			Product product = new Product();			
			product.setSku(ozonProduct.getOfferId());
			product.setName(ozonProduct.getName());									
			orderItem.setProduct(product);
			orderItem.setQuantity(ozonProduct.getQuantity());
			orderItem.setPrice(ozonProduct.getPrice());
			orderItems.add(orderItem);       
        });
        order.setItems(orderItems);
        return order;
    }
    
    private DeliveryTypes findDeliveryTypeByOzonOrderDto(OzonOrderDto ozonOrderDto) {
		DeliveryTypes result;
		if (StringUtils.equals(environment.getProperty("ozon.market.cdek.delivery.courier.main"), ozonOrderDto.getResult().getDeliveryMethod().getId())) {
			result = DeliveryTypes.CDEK_COURIER;
		} else if (StringUtils.equals(environment.getProperty("ozon.market.cdek.delivery.courier.economy"), ozonOrderDto.getResult().getDeliveryMethod().getId())) {
			result = DeliveryTypes.CDEK_COURIER_ECONOMY;
		} else if (StringUtils.equals(environment.getProperty("ozon.market.cdek.delivery.pvz.main"), ozonOrderDto.getResult().getDeliveryMethod().getId())) {
			result = DeliveryTypes.CDEK_PVZ_TYPICAL;
		} else if (StringUtils.equals(environment.getProperty("ozon.market.cdek.delivery.pvz.economy"), ozonOrderDto.getResult().getDeliveryMethod().getId())) {
			result = DeliveryTypes.CDEK_PVZ_ECONOMY;
		} else if (StringUtils.equals(environment.getProperty("ozon.market.ozon.warehouse.pickup"), ozonOrderDto.getResult().getDeliveryMethod().getId())) {
			result = DeliveryTypes.OZON_FBS;
		} else {
			result = DeliveryTypes.OZON_FBS;
		}
		return result;
	}
    
    public List<Order> convertToEntities(OzonOrdersDto ozonOrdersDto) {
    	
    	List<Order> result = new ArrayList<Order>();
    	Arrays.stream(ozonOrdersDto.getResult().getPostings()).forEach(posting -> {
    		OzonOrderDto ozonOrderDto = new OzonOrderDto();
    		ozonOrderDto.setResult(posting);
			try {
				Order order = convertToEntity(ozonOrderDto);
				result.add(order);
			} catch (ParseException e) {
				e.printStackTrace();
			}   		    		
    	});    	
    	return result;    	
    }
}
