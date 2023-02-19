package ru.sir.richard.boss.model.data.conditions;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ru.sir.richard.boss.model.types.SupplierTypes;
import ru.sir.richard.boss.utils.TextUtils;

public class ProductConditions extends AnyConditions {
	
	private int productId;
	private String name;
	private String sku;
	private String yandexSku;
	private String ozonSku;

	private String priceWithDiscountText;
	private String priceWithoutDiscountText;
	
	private int yandexSellerExist;
	private int ozonSellerExist;
	private int supplierStockExist; // 1- работа от склада поставщика,  0- от нашего склада
	
	private Set<SupplierTypes> suppliers;
		
	private int statusVisible;

//	manufacturer
	
	public ProductConditions() {
		super();
	}

	public int getProductId() {
		return productId;
	}

	public void setProductId(int productId) {
		this.productId = productId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}	

	public String getPriceWithDiscountText() {
		return priceWithDiscountText;
	}

	public void setPriceWithDiscountText(String priceWithDiscountText) {
		this.priceWithDiscountText = priceWithDiscountText;
	}

	public String getPriceWithoutDiscountText() {
		return priceWithoutDiscountText;
	}

	public void setPriceWithoutDiscountText(String priceWithoutDiscountText) {
		this.priceWithoutDiscountText = priceWithoutDiscountText;
	}
	
	public String getSku() {
		return sku;
	}

	public void setSku(String sku) {
		this.sku = sku;
	}

	public String getYandexSku() {
		return yandexSku;
	}

	public void setYandexSku(String yandexSku) {
		this.yandexSku = yandexSku;
	}

	public String getOzonSku() {
		return ozonSku;
	}

	public void setOzonSku(String ozonSku) {
		this.ozonSku = ozonSku;
	}

	public int getYandexSellerExist() {
		return yandexSellerExist;
	}

	public void setYandexSellerExist(int yandexSellerExist) {
		this.yandexSellerExist = yandexSellerExist;
	}

	public int getOzonSellerExist() {
		return ozonSellerExist;
	}

	public void setOzonSellerExist(int ozonSellerExist) {
		this.ozonSellerExist = ozonSellerExist;
	}

	public int getSupplierStockExist() {
		return supplierStockExist;
	}

	public void setSupplierStockExist(int supplierStockExist) {
		this.supplierStockExist = supplierStockExist;
	}

	public Set<SupplierTypes> getSuppliers() {
		return suppliers;
	}

	public void setSuppliers(Set<SupplierTypes> suppliers) {
		this.suppliers = suppliers;
	}

	public int getStatusVisible() {
		return statusVisible;
	}

	public void setStatusVisible(int statusVisible) {
		this.statusVisible = statusVisible;
	}
	
	public List<String> getViewSuppliers() {
		List<String> results = new ArrayList<String>();
		for (SupplierTypes supplier : this.suppliers) {
			results.add(supplier.getAnnotation());
		}		
		return results;
	}
	
	public void setViewSuppliers(List<String> annotationSuppliers) {
		
		Set<SupplierTypes> suppliers = new HashSet<SupplierTypes>();
		for (String annotationSupplier : annotationSuppliers) {
			suppliers.add(SupplierTypes.getValueByAnnotation(annotationSupplier));
		}
		this.suppliers = suppliers;
	}
	
	public String getIdsSuppliers() {
		List<Integer> ids = new ArrayList<Integer>();
		for (SupplierTypes supplier : this.suppliers) {
			ids.add(supplier.getId());
		}	
		return TextUtils.convertIntegerListToDelimitedString(ids);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + productId;
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
		ProductConditions other = (ProductConditions) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (productId != other.productId)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ProductConditions [productId=" + productId + ", name=" + name + "]";
	}
}
