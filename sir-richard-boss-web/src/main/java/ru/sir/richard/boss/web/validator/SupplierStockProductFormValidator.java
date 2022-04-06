package ru.sir.richard.boss.web.validator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import ru.sir.richard.boss.web.data.FormSupplierStockProduct;

@Component
public class SupplierStockProductFormValidator implements Validator {
	
	private final Logger logger = LoggerFactory.getLogger(SupplierStockProductFormValidator.class);

	@Override
	public boolean supports(Class<?> clazz) {
		return FormSupplierStockProduct.class.equals(clazz);
		
	}

	@Override
	public void validate(Object target, Errors errors) {
		FormSupplierStockProduct supplierStockProduct = (FormSupplierStockProduct) target;		
		logger.debug("formSupplierStockProduct validate():{}", supplierStockProduct.getId());
		
	}

}
