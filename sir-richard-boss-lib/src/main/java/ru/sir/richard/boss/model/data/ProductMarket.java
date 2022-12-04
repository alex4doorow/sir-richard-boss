package ru.sir.richard.boss.model.data;

import java.math.BigDecimal;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.sir.richard.boss.model.types.CrmTypes;

@Data
@NoArgsConstructor
public class ProductMarket implements Cloneable {
	
	private boolean supplierStock; /* 1- работа от склада поставщика,  0- от нашего склада  */ 
	private boolean marketSeller; /* продажи на marketplace (1- разрешены, 0- блокированы) */
	private String marketSku; /* маркетовый sku */
	private CrmTypes marketType;
	private BigDecimal specialPrice; 

	public ProductMarket(CrmTypes crmType) {
		this();
		marketType = crmType;
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
}
