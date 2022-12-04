package ru.sir.richard.boss.web.validator;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import ru.sir.richard.boss.model.types.CustomerTypes;
import ru.sir.richard.boss.web.data.FormOrder;
import ru.sir.richard.boss.web.service.OrderService;

import javax.validation.constraints.NotNull;

@Component
@Slf4j
public class OrderFormValidator implements Validator {

	@Autowired
	private OrderService orderService;
	
	@Override
	public boolean supports(Class<?> clazz) {
		return FormOrder.class.equals(clazz);
	}
	
	@Override
	public void validate(Object target, Errors errors) {
		
		FormOrder order = (FormOrder) target;		
		log.debug("order validate():{}", order.getNo());

		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "no", "order.form.fields.notEmpty.no");
		if (order.getNo() <= 0) {
			errors.rejectValue("no", "order.form.fields.diff.no");
		}
		log.debug("order checkNotUniqueOrderNo(id, no, year):{}, {}, {}", order.getId(), order.getNo(), order.getOrderYear());
		if (orderService.getOrderDao().checkNotUniqueOrderNo(order.getId(), order.getNo(), order.getOrderYear())) {
			errors.rejectValue("no", "order.form.fields.unique.no");
		}
		
		if (order.getFormCustomer().getCustomerType() == CustomerTypes.CUSTOMER) {
			
			if (StringUtils.isEmpty(order.getFormCustomer().getFirstName())) {
				errors.rejectValue("formCustomer.firstName", "order.form.fields.notEmpty.formCustomer.firstName");				
			}
			if (StringUtils.isEmpty(order.getFormCustomer().getPhoneNumber())) {
				errors.rejectValue("formCustomer.phoneNumber", "order.form.fields.notEmpty.formCustomer.phoneNumber");			
			}
		} else if (order.getFormCustomer().getCustomerType() == CustomerTypes.COMPANY) {
			if (StringUtils.isEmpty(order.getFormCustomer().getShortName())) {
				errors.rejectValue("formCustomer.shortName", "order.form.fields.notEmpty.formCustomer.shortName");				
			}
			if (StringUtils.isNotEmpty(order.getFormCustomer().getInn()) && order.getFormCustomer().getInn().trim().length() != 10) {
				errors.rejectValue("formCustomer.inn", "order.form.fields.diff.formCustomer.company.inn");				
			}
			if (StringUtils.isEmpty(order.getFormCustomer().getMainContact().getEmail())) {
				errors.rejectValue("formCustomer.mainContact.email", "order.form.fields.notEmpty.formCustomer.mainContact.email");			
			}
			if (StringUtils.isEmpty(order.getFormCustomer().getMainContact().getPhoneNumber())) {
				errors.rejectValue("formCustomer.mainContact.phoneNumber", "order.form.fields.notEmpty.formCustomer.mainContact.phoneNumber");			
			}
			if (StringUtils.isEmpty(order.getFormCustomer().getMainContact().getFirstName())) {
				errors.rejectValue("formCustomer.mainContact.firstName", "order.form.fields.notEmpty.formCustomer.mainContact.firstName");			
			}			
		} else if (order.getFormCustomer().getCustomerType() == CustomerTypes.BUSINESSMAN) {
			if (StringUtils.isEmpty(order.getFormCustomer().getShortName())) {
				errors.rejectValue("formCustomer.shortName", "order.form.fields.diff.formCustomer.shortName");				
			}
			if (StringUtils.isNotEmpty(order.getFormCustomer().getInn()) && order.getFormCustomer().getInn().trim().length() != 12) {
				errors.rejectValue("formCustomer.inn", "order.form.fields.diff.formCustomer.businessman.inn");				
			}
			if (StringUtils.isEmpty(order.getFormCustomer().getMainContact().getFirstName())) {
				errors.rejectValue("formCustomer.mainContact.firstName", "order.form.fields.notEmpty.formCustomer.mainContact.firstName");			
			}
			if (StringUtils.isEmpty(order.getFormCustomer().getMainContact().getEmail())) {
				errors.rejectValue("formCustomer.mainContact.email", "order.form.fields.notEmpty.formCustomer.mainContact.email");			
			}			
			if (StringUtils.isEmpty(order.getFormCustomer().getMainContact().getPhoneNumber())) {
				errors.rejectValue("formCustomer.mainContact.phoneNumber", "order.form.fields.notEmpty.formCustomer.mainContact.phoneNumber");			
			}
		}
	}

}
