package ru.sir.richard.boss.web.validator;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import ru.sir.richard.boss.web.data.FormSupplierStockProduct;

@Component
@Slf4j
public class SupplierStockProductFormValidator implements Validator {

	@Override
	public boolean supports(Class<?> clazz) {
		return FormSupplierStockProduct.class.equals(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		FormSupplierStockProduct supplierStockProduct = (FormSupplierStockProduct) target;		
		log.debug("formSupplierStockProduct validate():{}", supplierStockProduct.getId());
	}
}
