package ru.sir.richard.boss.web.data;

import java.math.BigDecimal;

import ru.sir.richard.boss.model.data.Product;
import ru.sir.richard.boss.model.types.CrmTypes;

public class FormProduct extends Product {

	public FormProduct() {
		super();
	}

	public FormProduct(int id, String name) {
		super(id, name);
	}
	
	public BigDecimal getYandexSpecialPrice() {
		return this.getMarket(CrmTypes.YANDEX_MARKET).getSpecialPrice();
	}
	
	public void setYandexSpecialPrice(BigDecimal value) {
		this.getMarket(CrmTypes.YANDEX_MARKET).setSpecialPrice(value);
	}
	
	public BigDecimal getOzonSpecialPrice() {
		return this.getMarket(CrmTypes.OZON).getSpecialPrice();
	}
	
	public void setOzonSpecialPrice(BigDecimal value) {
		this.getMarket(CrmTypes.OZON).setSpecialPrice(value);
	}	
		
	public String getYandexSku() {
		return this.getMarket(CrmTypes.YANDEX_MARKET).getMarketSku();
	}
	
	public void setYandexSku(String value) {
		this.getMarket(CrmTypes.YANDEX_MARKET).setMarketSku(value);
	}
	
	public String getOzonSku() {
		return this.getMarket(CrmTypes.OZON).getMarketSku();
	}
	
	public void setOzonSku(String value) {
		this.getMarket(CrmTypes.OZON).setMarketSku(value);
	}
	
	public boolean isYandexSupplierStock() {
		return this.getMarket(CrmTypes.YANDEX_MARKET).isSupplierStock();
	}
	
	public void setYandexSupplierStock(boolean value) {
		this.getMarket(CrmTypes.YANDEX_MARKET).setSupplierStock(value);
	}
	
	public boolean isYandexMarketSeller() {
		return this.getMarket(CrmTypes.YANDEX_MARKET).isMarketSeller();
	}
	
	public void setYandexMarketSeller(boolean value) {
		this.getMarket(CrmTypes.YANDEX_MARKET).setMarketSeller(value);
	}
	
	public boolean isOzonMarketSeller() {
		return this.getMarket(CrmTypes.OZON).isMarketSeller();
	}
	
	public void setOzonMarketSeller(boolean value) {
		this.getMarket(CrmTypes.OZON).setMarketSeller(value);
	}
	
	public static FormProduct createForm(Product source) {
		FormProduct result = new FormProduct();
		result.setId(source.getId());
		result.setName(source.getName());
		
		result.setModel(source.getModel());
		result.setSku(source.getSku());
		result.setDeliveryName(source.getDeliveryName());
		
		result.setQuantity(source.getQuantity());
		result.setPrice(source.getPrice());
		result.setPriceWithoutDiscount(source.getPriceWithoutDiscount());
		result.setPriceWithDiscount(source.getPriceWithDiscount());
		
		result.setSupplierPrice(source.getSupplierPrice());
		result.setStockQuantity(source.getStockQuantity());
		
		result.setSlaveQuantity(source.getSlaveQuantity());
		result.setCategory(source.getCategory());
		result.setDeliveryMethod(source.getDeliveryMethod());
		result.setOptionalExist(source.isOptionalExist());
		result.setLinkId(source.getLinkId());
		result.setMainSupplier(source.getMainSupplier());
		result.setType(source.getType());
		result.setComposite(source.isComposite());
		result.setKitComponents(source.getKitComponents());
		result.setVisible(source.isVisible());
		result.setStore(source.getStore());
		result.setMarkets(source.getMarkets());
	
		return result;
	}

	
	
	
	

}
