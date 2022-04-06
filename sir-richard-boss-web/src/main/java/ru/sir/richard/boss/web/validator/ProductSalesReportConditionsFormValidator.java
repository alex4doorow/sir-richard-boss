package ru.sir.richard.boss.web.validator;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import ru.sir.richard.boss.web.data.FormProductSalesReport;

@Component
public class ProductSalesReportConditionsFormValidator implements Validator {

	@Override
	public boolean supports(Class<?> clazz) {
		return FormProductSalesReport.class.equals(clazz);
		
	}

	@Override
	public void validate(Object target, Errors errors) {
		
	}

}
