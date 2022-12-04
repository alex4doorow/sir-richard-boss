package ru.sir.richard.boss.model.data;

import lombok.Data;
import ru.sir.richard.boss.model.types.OrderAmountTypes;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class SupplierStock {
		
	private final List<SupplierStockProduct> supplierStockProduct;
	private final Map<OrderAmountTypes, BigDecimal> amounts = new HashMap<OrderAmountTypes, BigDecimal>();
	
	public SupplierStock(List<SupplierStockProduct> supplierStockProduct) {
		this.supplierStockProduct = supplierStockProduct;		
	}
	
	public SupplierStock() {
		this(new ArrayList<SupplierStockProduct>());
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

}
