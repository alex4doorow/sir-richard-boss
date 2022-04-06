package ru.sir.richard.boss.web.service;

import java.util.Map;

import ru.sir.richard.boss.dao.ConfigDao;
import ru.sir.richard.boss.dao.WikiDao;
import ru.sir.richard.boss.model.data.Product;

public interface WikiService {
	
	WikiDao getWiki();
	Map<String, String> getConfigData();	
	ConfigDao getConfig();
	Product findProductByName(String contextString);
	String ymOfferPricesUpdates();
	String ozonOfferPricesUpdates(boolean isConditions);
	String ozonDisconnect();
	

}
