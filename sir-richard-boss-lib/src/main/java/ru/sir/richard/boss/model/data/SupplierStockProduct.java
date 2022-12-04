package ru.sir.richard.boss.model.data;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import ru.sir.richard.boss.model.types.SupplierTypes;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class SupplierStockProduct extends AnyId {
	
	private Product product;
	private SupplierTypes supplier = SupplierTypes.SITITEK;
	private String comment;

	public SupplierStockProduct(Product product) {
		this();
		this.product = product;
	}

	@Override
	public SupplierStockProduct clone() throws CloneNotSupportedException  {
		SupplierStockProduct clone = (SupplierStockProduct) super.clone();
		clone.product = this.product == null ? null : this.product.clone();
		clone.supplier = this.supplier;
		return clone;
	}
}
