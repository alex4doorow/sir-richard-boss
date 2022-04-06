package ru.sir.richard.boss.model.data;

import java.math.BigDecimal;

import ru.sir.richard.boss.model.types.CrmTypes;

public class ProductMarket implements Cloneable {
	
	private boolean supplierStock; /* 1- работа от склада поставщика,  0- от нашего склада  */ 
	private boolean marketSeller; /* продажи на marketplace (1- разрешены, 0- блокированы) */
	private String marketSku; /* маркетовый sku */
	private CrmTypes marketType;
	private BigDecimal specialPrice; 
				
	public ProductMarket() {
		super();
		supplierStock = false;
		marketSeller = false;
	}

	public ProductMarket(CrmTypes crmType) {
		this();
		marketType = crmType;
	}

	public boolean isSupplierStock() {
		return supplierStock;
	}

	public void setSupplierStock(boolean supplierStock) {
		this.supplierStock = supplierStock;
	}
	
	public boolean isMarketSeller() {
		return marketSeller;
	}

	public void setMarketSeller(boolean marketSeller) {
		this.marketSeller = marketSeller;
	}
	
	public CrmTypes getMarketType() {
		return marketType;
	}

	public void setMarketType(CrmTypes marketType) {
		this.marketType = marketType;
	}

	public String getMarketSku() {
		return marketSku;
	}

	public void setMarketSku(String marketSku) {
		this.marketSku = marketSku;
	}

	public BigDecimal getSpecialPrice() {
		return specialPrice;
	}

	public void setSpecialPrice(BigDecimal specialPrice) {
		this.specialPrice = specialPrice;
	}

	@Override
	public ProductMarket clone() throws CloneNotSupportedException  {		
		ProductMarket clone = (ProductMarket) super.clone();
		clone.marketType = this.marketType;
		clone.marketSku = this.marketSku;
		clone.supplierStock = this.supplierStock; 
		clone.marketSeller = this.marketSeller;
		clone.marketType = this.marketType;
		clone.specialPrice = this.specialPrice;
		return clone;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (marketSeller ? 1231 : 1237);
		result = prime * result + ((marketSku == null) ? 0 : marketSku.hashCode());
		result = prime * result + ((marketType == null) ? 0 : marketType.hashCode());
		result = prime * result + (supplierStock ? 1231 : 1237);
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
		ProductMarket other = (ProductMarket) obj;
		if (marketSeller != other.marketSeller)
			return false;
		if (marketSku == null) {
			if (other.marketSku != null)
				return false;
		} else if (!marketSku.equals(other.marketSku))
			return false;
		if (marketType != other.marketType)
			return false;
		if (supplierStock != other.supplierStock)
			return false;
		return true;
	}
	
	

}
