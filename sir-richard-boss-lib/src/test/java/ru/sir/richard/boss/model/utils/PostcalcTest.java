package ru.sir.richard.boss.model.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;

import org.json.JSONObject;



import ru.sir.richard.boss.crm.DeliveryService;
import ru.sir.richard.boss.crm.DeliveryServiceImpl;
import ru.sir.richard.boss.model.data.Address;
import ru.sir.richard.boss.model.data.Order;
import ru.sir.richard.boss.model.data.crm.DeliveryServiceResult;
import ru.sir.richard.boss.model.types.AddressTypes;
import ru.sir.richard.boss.model.types.Countries;
import ru.sir.richard.boss.model.types.DeliveryTypes;

public class PostcalcTest {
	
	public static void main1(String[] args) throws Exception {		
		 String url = "http://api.ipinfodb.com/v3/ip-city/?key=d64fcfdfacc213c7ddf4ef911dfe97b55e4696be3532bf8302876c09ebd06b&ip=74.125.45.100&format=json";
	     URL obj = new URL(url);
	     HttpURLConnection con = (HttpURLConnection) obj.openConnection();
	     // optional default is GET
	     con.setRequestMethod("GET");
	     //add request header
	     con.setRequestProperty("User-Agent", "Mozilla/5.0");
	     int responseCode = con.getResponseCode();
	     System.out.println("\nSending 'GET' request to URL : " + url);
	     System.out.println("Response Code : " + responseCode);
	     BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
	     String inputLine;
	     StringBuffer response = new StringBuffer();
	     while ((inputLine = in.readLine()) != null) {
	     	response.append(inputLine);
	     }
	     in.close();
	     //print in String
	     System.out.println(response.toString());
	     //Read JSON response and print
	     JSONObject myResponse = new JSONObject(response.toString());
	     System.out.println("result after Reading JSON Response");
	     System.out.println("statusCode- "+myResponse.getString("statusCode"));
	     System.out.println("statusMessage- "+myResponse.getString("statusMessage"));
	     System.out.println("ipAddress- "+myResponse.getString("ipAddress"));
	     System.out.println("countryCode- "+myResponse.getString("countryCode"));
	     System.out.println("countryName- "+myResponse.getString("countryName"));
	     System.out.println("regionName- "+myResponse.getString("regionName"));
	     System.out.println("cityName- "+myResponse.getString("cityName"));
	     System.out.println("zipCode- "+myResponse.getString("zipCode"));
	     System.out.println("latitude- "+myResponse.getString("latitude"));
	     System.out.println("longitude- "+myResponse.getString("longitude"));
	     System.out.println("timeZone- "+myResponse.getString("timeZone"));  
	}
	
	public static void main(String[] args) throws Exception {
		
		DeliveryService deliveryService = new DeliveryServiceImpl();
		
		
		Address to = new Address(Countries.RUSSIA, AddressTypes.MAIN);
		to.setAddress("238210, Калининградская обл. → Гвардейский р-н → Гвардейск г. Петра Набойченко ул. 20-3");
		Order order = new Order();
		DeliveryServiceResult result = deliveryService.calc(order, new BigDecimal("1000"), DeliveryTypes.POST_TYPICAL, to);
		
		System.out.println("result: " + result);
		
		
		
		
		
		
		int weight = 100;
		BigDecimal amount = new BigDecimal("1000"); 
		Date calculateDate = DateTimeUtils.sysDate();
		String responcePostIndex = "238210";
		String url = "http://test.postcalc.ru/web.php?Extend=0&Output=JSON&From=107241&Weight=" + weight + "&Valuation=" + amount.intValue() + "&Step=0&Date=" + DateTimeUtils.defaultFormatDate(calculateDate) + "&IBase=f&ProcessingFee=0&PackingFee=50&Round=0.01&VAT=1&To=" + responcePostIndex + "&Charset=UTF-8";
		//&st=sir-richard.ru
	    URL obj = new URL(url);
	    
	    HttpURLConnection con = (HttpURLConnection) obj.openConnection();
	    // optional default is GET
	    con.setRequestMethod("GET");
	    //add request header
	    con.setRequestProperty("User-Agent", "Mozilla/5.0");
	    //setRequestProperty("Accept-Encoding", "gzip, deflate");
	    
	    
	    
	    /*
	    Accept-Encoding: gzip, deflate
	    Accept-Language: ru,en;q=0.9
	    Connection: keep-alive
	    Cookie: _ga=GA1.2.1701253531.1537945212; _ym_uid=1537945212968874663; _ym_d=1537945212; ProcessingFee=0; Round=0.01; VAT=1; From=107241; LocationTo=%D0%9A%D0%BE%D0%BC%D0%B8%20%D1%80%D0%B5%D1%81%D0%BF%D1%83%D0%B1%D0%BB%D0%B8%D0%BA%D0%B0; To=169500; Weight=500; Valuation=2190; PackingFee=50; _gid=GA1.2.2079818993.1552304515; _ym_isad=2; IBase=f; _gat_gtag_UA_52140000_1=1
	    Host: www.postcalc.ru
	    Referer: http://www.postcalc.ru/
	    User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/72.0.3626.121 Safari/537.36
	    */
	    
	    
	    /*
	    int responseCode = con.getResponseCode();
	    System.out.println("\nSending 'GET' request to URL : " + url);
	    System.out.println("Response Code : " + responseCode);
	    BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
	    String inputLine;
	    StringBuffer response = new StringBuffer();
	    while ((inputLine = in.readLine()) != null) {
	     	response.append(inputLine);
	    }
	    in.close();
	    
	    //print in String
	    System.out.println(response.toString());
	    //Read JSON response and print
	    JSONObject myResponse = new JSONObject(response.toString());
	    System.out.println("result after Reading JSON Response: " + myResponse.toString());
	    
	    JSONObject parselData = (JSONObject) myResponse.get("ЦеннаяПосылка");	    
	    BigDecimal parselDataPostpayAmount = new BigDecimal((String) parselData.get("ОценкаПолная"));
	    BigDecimal parselDataDeliveryAmount = new BigDecimal((String) parselData.get("Доставка"));
	    String parselDataDeliveryTerm =(String) parselData.get("СрокДоставки");
	    
	    JSONObject parselEmsData = (JSONObject) myResponse.get("EMS");	    
	    BigDecimal parselEmsDataPostpayAmount = new BigDecimal((String) parselEmsData.get("ОценкаПолная"));
	    BigDecimal parselEmsDataDeliveryAmount = new BigDecimal((String) parselEmsData.get("Доставка"));
	    String parselEmsDataDeliveryTerm =(String) parselEmsData.get("СрокДоставки");
	    
	    
	    JSONObject parsel1ClassData = (JSONObject) myResponse.get("ЦеннаяБандероль1Класс");	    
	    BigDecimal parsel1ClassDataPostpayAmount = new BigDecimal((String) parsel1ClassData.get("ОценкаПолная"));
	    BigDecimal parsel1ClassDataDeliveryAmount = new BigDecimal((String) parsel1ClassData.get("Доставка"));
	    String parsel1ClassDataDeliveryTerm =(String) parsel1ClassData.get("СрокДоставки");
	    */
	}
}
