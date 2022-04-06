package ru.sir.richard.boss.web.validator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import ru.sir.richard.boss.model.data.conditions.OrderConditions;

@Component
public class OrderConditionsFormValidator implements Validator {
	
private final Logger logger = LoggerFactory.getLogger(OrderConditionsFormValidator.class);
	
	//private OrderService orderService;
	
/*
	@Autowired
	public void setOrderService(OrderService orderService) {
		this.orderService = orderService;
	}
	*/

	@Override
	public boolean supports(Class<?> clazz) {
		return OrderConditions.class.equals(clazz);
	}
	
	@Override
	public void validate(Object target, Errors errors) {
		OrderConditions orderConditions = (OrderConditions) target;		
		logger.debug("orderConditions validate():{}", orderConditions);
		
	}

}
