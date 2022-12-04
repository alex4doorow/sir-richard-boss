package ru.sir.richard.boss.web.validator;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import ru.sir.richard.boss.model.data.conditions.OrderConditions;

@Component
@Slf4j
public class OrderConditionsFormValidator implements Validator {

	@Override
	public boolean supports(Class<?> clazz) {
		return OrderConditions.class.equals(clazz);
	}
	
	@Override
	public void validate(Object target, Errors errors) {
		OrderConditions orderConditions = (OrderConditions) target;		
		log.debug("orderConditions validate():{}", orderConditions);
	}
}
