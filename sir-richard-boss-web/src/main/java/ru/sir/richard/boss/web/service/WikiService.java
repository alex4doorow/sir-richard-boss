package ru.sir.richard.boss.web.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import ru.sir.richard.boss.api.market.OzonMarketApiService;
import ru.sir.richard.boss.api.market.YandexMarketApi;
import ru.sir.richard.boss.dao.ConfigDao;
import ru.sir.richard.boss.dao.WikiDao;
import ru.sir.richard.boss.model.data.Product;
import ru.sir.richard.boss.model.data.conditions.ProductConditions;
import ru.sir.richard.boss.model.types.CrmTypes;
import ru.sir.richard.boss.model.utils.NumberUtils;
import ru.sir.richard.boss.web.controller.OrderListController;

@Service
public class WikiService {
	
	@Autowired
	private WikiDao wikiDao;
	
	@Autowired
	private ConfigDao configDao;
	
	@Autowired
	private Environment environment;
	
	@Autowired
	private OzonMarketApiService ozonMarketApiService;
			
	public WikiDao getWiki() {
		return wikiDao;		
	}
	
	public Map<String, String> getConfigData() {
		return configDao.getConfig();		
	}	
	
	public ConfigDao getConfig() {
		return configDao;		
	}

	public Product findProductByName(String contextString) {
		List<Product> products = getWiki().findProductsByName(contextString);
		if (products == null || products.size() == 0) {
			return new Product();
		}
		return products.get(0);
	}
	
	public String ymOfferPricesUpdates(int userId) {
		
		YandexMarketApi yandexMarketApi = new YandexMarketApi(this.environment);
		
		ProductConditions productConditions = getConfig().loadYmProductConditions(userId);
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

		if (productsForQuantityUpdates.size() > 0) {
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
			 //StringUtils.isNotEmpty(item.getMarket(CrmTypes.YANDEX_MARKET).getMarketSku())) {
			if (item.getMarket(CrmTypes.YANDEX_MARKET).isMarketSeller() && StringUtils.isNotEmpty(item.getSku())) {
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
		return resultMessage;
	}
		
	public String ozonOfferPricesUpdates(int userId, boolean isConditions) {
		
		String resultMessage = "";					
		Boolean isEnabled = Boolean.valueOf(getConfig().getConfig().get("ozon_enabled"));
		if (!isEnabled) {
			return resultMessage;
		}		
		ProductConditions productConditions;
		if (isConditions) {
			productConditions = getConfig().loadOzonProductConditions(userId);
		} else {
			productConditions = new ProductConditions();
			productConditions.setOzonSellerExist(1);
			productConditions.setSupplierStockExist(0);						
		}
			
		List<Product> products = getWiki().listOzonProductsByConditions(productConditions);		
		Map<Integer, List<Product>> portionProductsMap = createPortionProductsMap(products);
		for (int key : portionProductsMap.keySet()) {
			List<Product> portionProducts = portionProductsMap.get(key);
			ozonMarketApiService.offerPrices(portionProducts);
			ozonMarketApiService.offerWarehouseStocks(portionProducts);
		}			
		return resultMessage;		
	}

	public void ozonReconnect() {
		configDao.setConfigValue("ozon_enabled", String.valueOf(Boolean.TRUE));
	}
				
	public String ozonDisconnect() {
		
		configDao.setConfigValue("ozon_enabled", String.valueOf(Boolean.FALSE));		
		String resultMessage = "";		
		
		ProductConditions productConditions;		
		productConditions = new ProductConditions();
		productConditions.setOzonSellerExist(1);
		productConditions.setSupplierStockExist(0);				
					
		List<Product> products = getWiki().listOzonProductsByConditions(productConditions);
		// снимаем остатки с продаж
		for (Product product : products) {
			product.setQuantity(0);
		}
		Map<Integer, List<Product>> portionProductsMap = createPortionProductsMap(products);
		for (int key : portionProductsMap.keySet()) {
			List<Product> portionProducts = portionProductsMap.get(key);
			ozonMarketApiService.offerWarehouseStocks(portionProducts);
		}
		return resultMessage;		
	}
		
	/**
	 * Создание порционного контейнера. размер каждой порции не более 30 записей, иначе озон будет ругаться
	 * @param totalProducts
	 * @return
	 */
	private Map<Integer, List<Product>> createPortionProductsMap(List<Product> totalProducts) {

		final int MAX_PORTION = 30;
		Map<Integer, List<Product>> portionProductsMap = new HashMap<Integer, List<Product>>();
		List<Product> portionProducts = new ArrayList<Product>();
		int i = 0;
		int portionIndex = 0;
		for (Product product : totalProducts) {
			if (i > MAX_PORTION - 1) {
				portionProductsMap.put(portionIndex, portionProducts);
				portionProducts = new ArrayList<Product>();
				i = 0;
				portionIndex++;
			}
			portionProducts.add(product);
			i++;
		}
		if (portionProducts.size() > 0) {
			portionProductsMap.put(portionIndex, portionProducts);
		}
		return portionProductsMap;
	}
}
