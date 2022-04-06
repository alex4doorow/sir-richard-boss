package ru.sir.richard.boss.web.validator;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import ru.sir.richard.boss.model.types.CustomerTypes;
import ru.sir.richard.boss.web.data.FormOrder;
import ru.sir.richard.boss.web.service.OrderService;

@Component
public class OrderFormValidator implements Validator {
	
	private final Logger logger = LoggerFactory.getLogger(OrderFormValidator.class);
	
	@Autowired
	private OrderService orderService;
	
	@Override
	public boolean supports(Class<?> clazz) {
		return FormOrder.class.equals(clazz);
	}
	
	@Override
	public void validate(Object target, Errors errors) {
		
		FormOrder order = (FormOrder) target;		
		logger.debug("order validate():{}", order.getNo());

		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "no", "order.form.fields.notEmpty.no");
		if (order.getNo() <= 0) {
			errors.rejectValue("no", "order.form.fields.diff.no");
		}
		logger.debug("order checkNotUniqueOrderNo(id, no, year):{}, {}, {}", order.getId(), order.getNo(), order.getOrderYear());
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
		
		/*
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "email", "NotEmpty.userForm.email");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "address", "NotEmpty.userForm.address");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password", "NotEmpty.userForm.password");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "confirmPassword","NotEmpty.userForm.confirmPassword");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "sex", "NotEmpty.userForm.sex");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "country", "NotEmpty.userForm.country");

		if(!emailValidator.valid(user.getEmail())){
			errors.rejectValue("email", "Pattern.userForm.email");
		}
		
		if(user.getNumber()==null || user.getNumber()<=0){
			errors.rejectValue("number", "NotEmpty.userForm.number");
		}
		
		if(user.getCountry().equalsIgnoreCase("none")){
			errors.rejectValue("country", "NotEmpty.userForm.country");
		}
		
		if (!user.getPassword().equals(user.getConfirmPassword())) {
			errors.rejectValue("confirmPassword", "Diff.userform.confirmPassword");
		}
		
		if (user.getFramework() == null || user.getFramework().size() < 2) {
			errors.rejectValue("framework", "Valid.userForm.framework");
		}

		if (user.getSkill() == null || user.getSkill().size() < 3) {
			errors.rejectValue("skill", "Valid.userForm.skill");
		}
		*/

		
	}

}
