package ru.sir.richard.boss.model.data;

import ru.sir.richard.boss.model.types.SupplierTypes;

public class SupplierStockProduct extends AnyId {
	
	private Product product;
	private SupplierTypes supplier;
	private String comment;
	
	public SupplierStockProduct() {
		this.product = new Product();
		supplier = SupplierTypes.SITITEK;
	}
	
	public SupplierStockProduct(Product product) {
		this.product = product;
	}

	public SupplierTypes getSupplier() {
		return supplier;
	}

	public void setSupplier(SupplierTypes supplier) {
		this.supplier = supplier;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}	
	
	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	@Override
	public SupplierStockProduct clone() throws CloneNotSupportedException  {
		SupplierStockProduct clone = (SupplierStockProduct) super.clone();
		clone.product = this.product == null ? null : this.product.clone();
		clone.supplier = this.supplier;
		return clone;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((product == null) ? 0 : product.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		SupplierStockProduct other = (SupplierStockProduct) obj;
		if (product == null) {
			if (other.product != null)
				return false;
		} else if (!product.equals(other.product))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "SupplierStockProduct [product=" + product + ", supplier=" + supplier + "]";
	}

	

}
