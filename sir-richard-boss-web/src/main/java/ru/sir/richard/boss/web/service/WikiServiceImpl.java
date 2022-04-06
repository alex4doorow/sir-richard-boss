package ru.sir.richard.boss.web.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ru.sir.richard.boss.api.market.OzonMarketApi;
import ru.sir.richard.boss.api.market.YandexMarketApi;
import ru.sir.richard.boss.dao.ConfigDao;
import ru.sir.richard.boss.dao.WikiDao;
import ru.sir.richard.boss.model.data.Product;
import ru.sir.richard.boss.model.data.conditions.ProductConditions;
import ru.sir.richard.boss.model.types.CrmTypes;
import ru.sir.richard.boss.model.utils.NumberUtils;
import ru.sir.richard.boss.web.controller.OrderListController;

@Service("wikiService")
public class WikiServiceImpl implements WikiService {
	
	@Autowired
	private WikiDao wikiDao;
	
	@Autowired
	private ConfigDao configDao;
			
	@Override
	public WikiDao getWiki() {
		return wikiDao;		
	}
	
	@Override
	public Map<String, String> getConfigData() {
		return configDao.getConfig();		
	}	
	
	@Override
	public ConfigDao getConfig() {
		return configDao;		
	}

	@Override
	public Product findProductByName(String contextString) {
		List<Product> products = getWiki().findProductsByName(contextString);
		if (products == null || products.size() == 0) {
			return new Product();
		}
		return products.get(0);
	}
	
	@Override
	public String ymOfferPricesUpdates() {
		
		YandexMarketApi yandexMarketApi = new YandexMarketApi();
		
		ProductConditions productConditions = getConfig().loadYmProductConditions(OrderListController.USER_ID);		
		List<Product> products = getWiki().listYmProductsByConditions(productConditions);
					
		List<Product> productsForQuantityUpdates = new ArrayList<Product>();
		List<Product> productsWithoutMarketSku = new ArrayList<Product>(); 
		String resultMessage = "";
		for (Product item : products) {
			if (StringUtils.isEmpty(item.getMarket(CrmTypes.YANDEX_MARKET).getMarketSku())) {
				// нет маркет sku
				productsWithoutMarketSku.add(item);
			}			
			if (item.getMarket(CrmTypes.YANDEX_MARKET).isMarketSeller() && !item.getMarket(CrmTypes.YANDEX_MARKET).isSupplierStock()) {
				// работа от нашего склада
				productsForQuantityUpdates.add(item);
			}			
		}
		/*
		String resultMarketSku = "";		
		if (productsWithoutMarketSku.size() > 0) {
			resultMessage += "Добавлен маппинг на marketSku для \"Яндекс-Маркет\" у товаров: " + productsWithoutMarketSku.size() + "<br/>";
			for (Product item : productsWithoutMarketSku) {
				Product resultProductWithoutMarketSku = yandexMarketApi.offerMappingEntriesByShopSku(item.getSku());
				resultMarketSku += "- " + resultProductWithoutMarketSku.getSku() + ", marketSku: " + resultProductWithoutMarketSku.getYm().getMarketSku() + "<br/>"; 
								
			}
			wikiDao.updateYmOfferMapping(productsWithoutMarketSku);				
			resultMessage += resultMarketSku;
		}
		*/
		if (productsForQuantityUpdates.size() > 0) {
			wikiDao.updateQuantityProductsBySuplierStockProducts(productsForQuantityUpdates);			
			resultMessage += "<br/>Обновлены остатки для \"Яндекс-Маркет\" у товаров: " + productsForQuantityUpdates.size() + "<br/>";
			String resultQuantityUpdate = "";
			for (Product item : productsForQuantityUpdates) {				
				resultQuantityUpdate += "- " + item.getSku() + ", остаток: "+ item.getQuantity() + ", " + item.getName() + "<br/>";								
			}
			resultMessage += resultQuantityUpdate;			
		}	
		
		products = getWiki().listYmProductsByConditions(productConditions);
		List<Product> productsForOfferUpdates = new ArrayList<Product>(); 	
		for (Product item : products) {	
			if (item.getMarket(CrmTypes.YANDEX_MARKET).isMarketSeller() && StringUtils.isNotEmpty(item.getMarket(CrmTypes.YANDEX_MARKET).getMarketSku())) {
				// список на синхронизацию цены
				productsForOfferUpdates.add(item);
			}
		}
		
		if (productsForOfferUpdates.size() > 0) {
			yandexMarketApi.offerPricesUpdatesByAllWarehouses(productsForOfferUpdates);			
			resultMessage += "<br/>Установлены цены в \"Яндекс-Маркет\" у товаров: " + productsForOfferUpdates.size() + "<br/>";			
			String resultOfferUpdates = "";
			for (Product item : productsForOfferUpdates) {				
				resultOfferUpdates += "- " + item.getSku() + ", цена: "+ NumberUtils.formatRoubles(item.getPrice()) + ", " + item.getName() + "<br/>";								
			}
			resultMessage += resultOfferUpdates;	
						
		}	
		//wikiDao.init();
		return resultMessage;
	}
	
	
	@Override
	public String ozonOfferPricesUpdates(boolean isConditions) {
		
		String resultMessage = "";
		
		OzonMarketApi ozonMarketApi = new OzonMarketApi();
		
		ProductConditions productConditions;
		if (isConditions) {
			productConditions = getConfig().loadOzonProductConditions(OrderListController.USER_ID);			
		} else {
			productConditions = new ProductConditions();
			productConditions.setOzonSellerExist(1);
			productConditions.setSupplierStockExist(0);						
		}
			
		List<Product> products = getWiki().listOzonProductsByConditions(productConditions);
		
		ozonMarketApi.offerPrices(products);		
		/*
		OzonResult offerStocksResult = ozonMarketApi.offerStocks(products);
		if (offerStocksResult.isResponseSuccess()) {
			resultMessage += "<br/>Обновлены остатки для \"OZON\" у товаров: " + offerStocksResult.getResponse().size() + "<br/>";			
		} else {
			resultMessage += "<br/>Обновлены остатки для \"OZON\" у товаров с ошибками:<br/>";
			for (OzonResultBean ozonResultBean : offerStocksResult.getResponse()) {
				resultMessage += "<br/>Ошибка установки остатков у товара: " + ozonResultBean.getOfferId() + ", ответ OZON: " + ozonResultBean.getErrors() + "<br/>";
			}
		}
		*/		
		ozonMarketApi.offerWarehouseStocks(products);
				
		return resultMessage;
		
	}
	
	@Override
	public String ozonDisconnect() {
		
		String resultMessage = "";
		
		OzonMarketApi ozonMarketApi = new OzonMarketApi();
		
		ProductConditions productConditions;
		
		productConditions = new ProductConditions();
		productConditions.setOzonSellerExist(1);
		productConditions.setSupplierStockExist(0);						
					
		List<Product> products = getWiki().listOzonProductsByConditions(productConditions);
		
		// снимаем остатки с продаж
		for (Product product : products) {
			product.setQuantity(0);
		}		
		ozonMarketApi.offerWarehouseStocks(products);
				
		return resultMessage;
		
	}
}
