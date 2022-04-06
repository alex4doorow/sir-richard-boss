package ru.sir.richard.boss.web.data;

import ru.sir.richard.boss.model.data.SupplierStockProduct;

public class FormSupplierStockProduct extends SupplierStockProduct {
	
	public FormSupplierStockProduct() {
		super();
	}
	
	public static FormSupplierStockProduct createForm(SupplierStockProduct source) {
		FormSupplierStockProduct result = new FormSupplierStockProduct();
		result.setId(source.getId());
		result.setProduct(source.getProduct());
		result.setSupplier(source.getSupplier());
		result.setComment(source.getComment());		
		return result;
	}
		

}
