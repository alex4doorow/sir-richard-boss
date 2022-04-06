package ru.sir.richard.boss.model.utils;

import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import ru.sir.richard.boss.model.data.Product;

public class ProductUtils {
		
	public static Product findProductByKeyword(String searchText, List<Product> products) {
		Iterator<Product> it = products.iterator();
		while (it.hasNext()) {
			Product item4Check = it.next();
			if (isProductExist(searchText, item4Check)) {
				return item4Check;				
			}			
		}
		return new Product(0, "не определен");
	}
	
	public static boolean isProductExist(String searchText, Product product4Check) {
		if (StringUtils.isEmpty(searchText)) {
			return false;
		}

		String[] splitSearchTexts = searchText.split("[+]");
		String localSearchText;
		if (splitSearchTexts.length > 0) {
			localSearchText = splitSearchTexts[0].trim();			
		} else {
			localSearchText = searchText.trim(); 
		}
		if (isStringExist(localSearchText, product4Check.getName())) {
			return true;			
		}
		if (isStringExist(localSearchText, product4Check.getModel())) {
			return true;			
		}
		if (isStringExist(localSearchText, product4Check.getViewSKU())) {
			return true;			
		}
		return false;
	}
	
	public static boolean isStringExist(String searchText, String string4Check) {
		if (StringUtils.isEmpty(searchText)) {
			return false;
		}		
		if (StringUtils.isEmpty(string4Check)) {
			return false;
		}
		int index = string4Check.indexOf(searchText);
		if (index > 0) {
			return true;
		}		
		return false;		
	}

	
}
