package ru.sir.richard.boss.model.data.report;

import java.math.BigDecimal;

import ru.sir.richard.boss.model.data.Product;

public class ProductSalesReportBean {
	
	private Product product;
	private int quantity;
	private BigDecimal amount; 
	
	public ProductSalesReportBean() {
		super();
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	public BigDecimal getAmount() {
		return amount;
	}
	
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	
	
}
