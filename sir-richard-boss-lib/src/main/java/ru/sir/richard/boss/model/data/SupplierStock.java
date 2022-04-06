package ru.sir.richard.boss.model.data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.sir.richard.boss.model.types.OrderAmountTypes;

public class SupplierStock {
		
	private final List<SupplierStockProduct> supplierStockProduct;
	private final Map<OrderAmountTypes, BigDecimal> amounts = new HashMap<OrderAmountTypes, BigDecimal>();
	
	public SupplierStock(List<SupplierStockProduct> supplierStockProduct) {
		this.supplierStockProduct = supplierStockProduct;		
	}
	
	public SupplierStock() {
		this(new ArrayList<SupplierStockProduct>());
	}
	
	public List<SupplierStockProduct> getSupplierStockProduct() {
		return supplierStockProduct;
	}
	
	public void setAmount(OrderAmountTypes type, BigDecimal amount) {
		this.amounts.put(type, amount);
	}
	
	public BigDecimal getSupplierAmount() {
		return this.amounts.get(OrderAmountTypes.SUPPLIER);
	}
	
	public BigDecimal getBillAmount() {
		return this.amounts.get(OrderAmountTypes.BILL);
	}
	
	public BigDecimal getTotalSupplierAmount() {
		return this.amounts.get(OrderAmountTypes.TOTAL_SUPPLIER);
	}
	
	public BigDecimal getTotalBillAmount() {
		return this.amounts.get(OrderAmountTypes.TOTAL_BILL);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((amounts == null) ? 0 : amounts.hashCode());
		result = prime * result + ((supplierStockProduct == null) ? 0 : supplierStockProduct.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SupplierStock other = (SupplierStock) obj;
		if (amounts == null) {
			if (other.amounts != null)
				return false;
		} else if (!amounts.equals(other.amounts))
			return false;
		if (supplierStockProduct == null) {
			if (other.supplierStockProduct != null)
				return false;
		} else if (!supplierStockProduct.equals(other.supplierStockProduct))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "SupplierStock [supplierStockProduct=" + supplierStockProduct + ", amounts=" + amounts + "]";
	}
	
	

}
