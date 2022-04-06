package ru.sir.richard.boss.model.data.crm;

import java.math.BigDecimal;

public class CdekOrderItemBean {
	
	private String productSku;
	private String productName;	
	private BigDecimal productPrice;
	private int productQuantity;
	private BigDecimal productPay; 
	
	public CdekOrderItemBean() {
		super();		
	}

	public String getProductSku() {
		return productSku;
	}

	public void setProductSku(String productSku) {
		this.productSku = productSku;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public BigDecimal getProductPrice() {
		return productPrice;
	}

	public void setProductPrice(BigDecimal productPrice) {
		this.productPrice = productPrice;
	}

	public int getProductQuantity() {
		return productQuantity;
	}

	public void setProductQuantity(int productQuantity) {
		this.productQuantity = productQuantity;
	}

	public BigDecimal getProductPay() {
		return productPay;
	}

	public void setProductPay(BigDecimal productPay) {
		this.productPay = productPay;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((productName == null) ? 0 : productName.hashCode());
		result = prime * result + ((productPay == null) ? 0 : productPay.hashCode());
		result = prime * result + ((productPrice == null) ? 0 : productPrice.hashCode());
		result = prime * result + productQuantity;
		result = prime * result + ((productSku == null) ? 0 : productSku.hashCode());
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
		CdekOrderItemBean other = (CdekOrderItemBean) obj;
		if (productName == null) {
			if (other.productName != null)
				return false;
		} else if (!productName.equals(other.productName))
			return false;
		if (productPay == null) {
			if (other.productPay != null)
				return false;
		} else if (!productPay.equals(other.productPay))
			return false;
		if (productPrice == null) {
			if (other.productPrice != null)
				return false;
		} else if (!productPrice.equals(other.productPrice))
			return false;
		if (productQuantity != other.productQuantity)
			return false;
		if (productSku == null) {
			if (other.productSku != null)
				return false;
		} else if (!productSku.equals(other.productSku))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "CdekOrderItemBean [productSku=" + productSku + ", productName=" + productName + ", productPrice="
				+ productPrice + ", productQuantity=" + productQuantity + ", productPay=" + productPay + "]";
	}	

	

}
