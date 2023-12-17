package ru.sir.richard.boss.utils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import ru.sir.richard.boss.model.data.CompanyCustomer;
import ru.sir.richard.boss.model.data.Contact;
import ru.sir.richard.boss.model.data.OrderItem;
import ru.sir.richard.boss.model.data.Product;

public class ConverterUtils {
		
	public static String clearPhoneNumber(String phoneNumberRaw) {
		if (phoneNumberRaw == null || phoneNumberRaw.trim().equals("")) {
			return "(000) 000-00-00";
		}		
		String result = phoneNumberRaw.trim();
		if (result.indexOf("(") == -1) {
			result = "(" + result;			
		}
		
		int length = result.length();
		//(000) 111-12-23
		if (length < 15) {
			return "(000) 000-00-00";			
		}
		if (length > 15) {
			result = result.substring(0, 15);
		}		
		return result;
	}
	
	public static boolean isPhoneNumberEmpty(String phoneNumber) {
		if ("(000) 000-00-00".equals(phoneNumber)) {
			return true;
		} else {
			return false;
		}		
	}	
	
	public static Contact separateNames(String inputName) {
		
		if (StringUtils.isEmpty(inputName)) {
			return null;			
		}	
		String customerF = ""; 
		String customerI = "";
		String customerO = "";  
		String[] customerFIO = inputName.trim().split(" ");
		if (customerFIO.length == 1) {
			customerI = customerFIO[0];
		} else if (customerFIO.length == 2) {
			customerI = customerFIO[0];
			customerF = customerFIO[1];
		} else if (customerFIO.length > 2) {
			customerI = customerFIO[1];
			customerO = customerFIO[2];						
			customerF = customerFIO[0];
		} 					
		Contact result = new Contact();
		result.setFirstName(customerI);
		result.setMiddleName(customerO);
		result.setLastName(customerF);
		return result;
		
	}
	
	public static String extractPhoneNumberOfText(String inputText) {
		if (StringUtils.isEmpty(inputText)) {
			return "";
		}
		String text = inputText.trim();		

		Pattern pattern = Pattern.compile(".*[^0-9-( )] ([0-9-( )]+)$");
		Matcher matcher = pattern.matcher(text);
		if (matcher.find()) {
		    return matcher.group(1).trim();
		}
		return "";
	}
	
	public static CompanyCustomer customerCompanyNameSeparate2CompanyNameAndContact(final String inputText, 
			String inputPhoneNumber, String inputEmail) {
		
		String longName = inputText.trim(); 
		
		CompanyCustomer result = new CompanyCustomer();	
		result.getMainContact().setFirstName("");				
		result.getMainContact().setLastName("");
		result.getMainContact().setMiddleName("");
		result.setLongName(longName);

		String[] namesArr = longName.split(",");
		if (namesArr.length == 0) {
			return result;
		}	
		
		String shortName = namesArr[0].trim();
		result.setShortName(shortName);
		boolean isExtractPhoneNumber = false;
		if (namesArr.length > 1) {
			String firstName = namesArr[1].trim();			
			String phoneNumber = ConverterUtils.extractPhoneNumberOfText(firstName);
			if (StringUtils.isNotEmpty(phoneNumber)) {
				isExtractPhoneNumber = true;				
				result.getMainContact().setPhoneNumber(phoneNumber);
				int phoneNumberIndex = firstName.indexOf(phoneNumber);
				firstName = firstName.substring(0, phoneNumberIndex - 1).trim();
				
				Contact companyContact = separateNames(firstName);
				result.getMainContact().setFirstName(companyContact.getFirstName());				
				result.getMainContact().setLastName(companyContact.getLastName());
				result.getMainContact().setMiddleName(companyContact.getMiddleName());
			} else {
				result.getMainContact().setFirstName(firstName);
				
				Contact companyContact = separateNames(firstName);
				result.getMainContact().setFirstName(companyContact.getFirstName());				
				result.getMainContact().setLastName(companyContact.getLastName());
				result.getMainContact().setMiddleName(companyContact.getMiddleName());
			}
		}
		if (!isExtractPhoneNumber) {
			String phoneNumber = inputPhoneNumber;		
			if (namesArr.length >= 3) {
				phoneNumber = namesArr[2].trim();
				phoneNumber = ConverterUtils.extractPhoneNumberOfText(phoneNumber);
				result.getMainContact().setPhoneNumber(phoneNumber);			
			} else {
				result.getMainContact().setPhoneNumber(phoneNumber);
			}
		}		
		result.getMainContact().setEmail(inputEmail);
		return result;
	}
	
	public static List<OrderItem> productTextSeparate2OrderItems(final String inputText, int quantity, BigDecimal price) {
		
		List<OrderItem> orderItems = new ArrayList<OrderItem>();
		if (StringUtils.isEmpty(inputText)) {
			return orderItems;
		}		
		String text = inputText.trim().replace(" шт", "шт"); 
		String[] orderItemsArr = text.replace('+', '!').split("!");
		
		
		if (orderItemsArr.length == 1) {
			OrderItem orderItem = new OrderItem();
			String orderItemProductName = text.trim();
			orderItem.setProduct(new Product(0, orderItemProductName));
			orderItem.setQuantity(quantity);
			orderItem.setPrice(price);		
			orderItems.add(orderItem);		
		} else {
			
			for (int j = 0; j <= orderItemsArr.length - 1; j++) {
				OrderItem orderItem = new OrderItem();
				String orderItemProductName = orderItemsArr[j].trim();
				
				int itemQuantityExist = orderItemProductName.trim().indexOf("шт");
				if (itemQuantityExist >= 0) {
					String newOrderItemProductName = orderItemProductName.replace("шт", "");
					int lastIndexOfQuantity = newOrderItemProductName.lastIndexOf(" ");									

					String newNewOrderItemQuantity = "";
					String productNameOrderItem = "";
					int quantityOrderItem = 0;
					try {
						if (lastIndexOfQuantity < 0) {
							productNameOrderItem = newOrderItemProductName.trim();
							newNewOrderItemQuantity = "1";
						} else {
							productNameOrderItem = newOrderItemProductName.substring(0, lastIndexOfQuantity).replace("x", "").trim();
							newNewOrderItemQuantity = newOrderItemProductName.substring(lastIndexOfQuantity, newOrderItemProductName.length()).replace(".", "").trim();
						}
						
						if (StringUtils.isEmpty(newNewOrderItemQuantity)) {
							quantityOrderItem = 1;
						} else {
							quantityOrderItem = Integer.valueOf(newNewOrderItemQuantity).intValue();	
						}				
						
					} catch (Exception ex) {
						//logger.error("newOrderItemProductName:{},{},{}", newOrderItemProductName, newNewOrderItemQuantity,orderItemProductName);
					}									
					orderItem.setProduct(new Product(0, convertProductName(productNameOrderItem)));
					orderItem.setQuantity(quantityOrderItem);														
					orderItems.add(orderItem);	
				} else {
					orderItem.setProduct(new Product(0, convertProductName(orderItemProductName)));
					orderItem.setQuantity(1);
					orderItems.add(orderItem);
				}							
				
				//orderItem.setProduct(new Product(0, orderItemProductName));
				//orderItem.setQuantity(1);
				//orderItems.add(orderItem);
			}
	
		}		
		return orderItems;
	}
	
	private static String convertProductName(String oldProductName) {
		if (StringUtils.isEmpty(oldProductName)) {
			return oldProductName;
		}
		if (oldProductName.trim().indexOf("батарейк") >= 0) {
			return "LR20";			
		}
		return oldProductName;
	}

}
