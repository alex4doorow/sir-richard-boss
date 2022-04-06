package ru.sir.richard.boss.model.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.json.JSONObject;

import ru.sir.richard.boss.api.cdek.CdekApi;
import ru.sir.richard.boss.model.data.Order;

public class CdekTest {
		
	public static void main2(String[] args) throws Exception {
		String body = "618820, Пермский край → Горнозаводский р-н → Горнозаводск г., Гипроцемента ул. д.38 кв. 25";
		String replacedBody = body.replace("\"", "").replaceAll("→", "->").replaceAll("“", "\"").replaceAll("”", "\"");
		System.out.println(replacedBody);
		
		body = "Набор запасной для отпугивателя комаров “ThermaCell” (большой)";
		replacedBody = body.replace("\"", "").replaceAll("→", "->").replaceAll("“", "\"").replaceAll("”", "\"");
		System.out.println(replacedBody);
	}
	
	public static void main3(String[] args) throws Exception {
		CdekApi cdek = new CdekApi();
		
		List<Order> orders = new ArrayList<Order>();
		Order one = new Order();
		one.setNo(5852);
		one.getDelivery().setTrackCode("1118343441");
		orders.add(one);
		/*
		Order two = new Order();
		two.setNo(5747);
		two.getDelivery().setTrackCode("1116568973");
		orders.add(two);
		*/
		List<Order> cdekModifiedOrders = cdek.getStatuses(orders);
		System.out.println(cdekModifiedOrders);
		
		
		/*
		int receiverCityId = 393;
		int tariffId = 136;
		DeliveryServiceResult result = cdek.cdekCalc(BigDecimal.valueOf(0.5), DateTimeUtils.sysDate(), BigDecimal.valueOf(17500), 
				tariffId, receiverCityId);
		System.out.println(result);		
		*/
		
		//(int weight, Date calculateDate, BigDecimal totalAmount, DeliveryTypes type) throws Exception {
		//List<CdekPvz> pvzs = cdek.getPvzList(393);
	}	
	
	public static void main4(String[] args) throws Exception {
		
		CdekApi cdek = new CdekApi();
		
		Order order = new Order();
		BigDecimal price = BigDecimal.valueOf(76759).divide(BigDecimal.valueOf(11209), 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP);
		
		price = BigDecimal.valueOf(290).divide(BigDecimal.valueOf(1), 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(1)).setScale(2, RoundingMode.HALF_UP);
		order.getDelivery().setPrice(price);
		
		Locale locale = new Locale("en", "UK");
		DecimalFormatSymbols symbols = new DecimalFormatSymbols(locale);
		symbols.setDecimalSeparator('.');
		symbols.setGroupingSeparator(',');

		String pattern = "#,###.#";
		DecimalFormat decimalFormat = new DecimalFormat(pattern, symbols);
		
		
		String ss = decimalFormat.format(price);
		System.out.println(ss);
		
		
		//String trackCode = cdek.addOrder(order);
		//System.out.println(trackCode);
	}
	
	public static void main(String[] args) throws Exception {
		
		//CdekApi cdek = new CdekApi();
		//JSONObject json = cdek.getCities("Киров");
		
		
	
		
	}	
	
	
}
