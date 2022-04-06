package ru.sir.richard.boss.model.utils.sender.email;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Properties;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.search.AndTerm;
import javax.mail.search.ComparisonTerm;
import javax.mail.search.ReceivedDateTerm;
import javax.mail.search.SearchTerm;

import ru.sir.richard.boss.model.data.Customer;
import ru.sir.richard.boss.model.data.Order;
import ru.sir.richard.boss.model.data.OrderItem;
import ru.sir.richard.boss.model.data.Product;
import ru.sir.richard.boss.model.utils.DateTimeUtils;
import ru.sir.richard.boss.model.utils.TextUtils;

public class SSLEmail {
	
public static void main0(String[] args) throws AddressException, MessagingException, UnsupportedEncodingException {
		
		//final String fromEmail = "sir-richard@sir-richard.ru"; //requires valid mail id
		//final String password = "Cuba2017"; // correct password for mail id
		
		final String fromEmail = "notice@sir-richard.ru"; //requires valid mail id
		final String password = "d899ZnF69U8Y7B2"; // correct password for mail id
							
		System.out.println("SSLEmail Start");
		Properties props = new Properties();
		props.put("mail.smtp.host", "smtp.yandex.ru"); //SMTP Host
		props.put("mail.smtp.socketFactory.port", "465"); //SSL Port
		props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory"); //SSL Factory Class
		props.put("mail.smtp.auth", "true"); //Enabling SMTP Authentication
		props.put("mail.smtp.port", "465"); //SMTP Port
		
		Session session = Session.getDefaultInstance(props,
                new javax.mail.Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(fromEmail, password);
                    }
                });

		MimeMessage message = new MimeMessage(session);
        message.setSubject("Ваш заказ отправлен - test", "utf-8");
        message.setText("Алексей! Заказ интернет-магазина www.sir-richard.ru 1105446815 (5389) отправлен. Сумма к оплате 7500 руб. Отследить заказ можно здесь https://www.cdek.ru/track.html?order_id=1105446815?", 
        		"utf-8");
        message.setFrom(new InternetAddress(fromEmail, "sir-richard.ru", "utf-8"));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse("natulya.isakova.2020@mail.ru")); 
        
        message.addHeader("Content-type", "text/plain; charset=\"utf-8\"");
		message.addHeader("format", "flowed");
		message.addHeader("Content-Transfer-Encoding", "8bit");
        
        Transport.send(message);
        System.out.println("SSLEmail End");
		
	}
	
	
	
	public static void main51(String[] args)  {

		/*
		адрес почтового сервера — imap.yandex.ru;
		защита соединения — SSL;
		порт — 993.
			*/
		

		String host = "imap.yandex.ru";// change accordingly
	    String mailStoreType = "imaps";
	    
	    String username = "sir-richard@sir-richard.ru";// change accordingly
	    String password = "Cuba2017";// change accordingly
	    
		//String username = "notice@sir-richard.ru"; //requires valid mail id
		//String password = "d899ZnF69U8Y7B2"; // correct password for mail id

	    
	    checkImap(host, mailStoreType, username, password);		
		
	}

	public static void main(String[] args) {

		/*
		 * адрес почтового сервера — imap.yandex.ru; защита соединения — SSL; порт —
		 * 993.
		 */

		String host = "imap.yandex.ru";// change accordingly
		String mailStoreType = "imaps";

		String user = "info@pribormaster.ru";// change accordingly
		String password = "rNs-N8g-gn5-YwK";// change accordingly

		Properties properties = new Properties();
		properties.put("mail.imap.host", "imap.yandex.ru"); //SMTP Host
		properties.put("mail.imap.port", "993"); //SMTP Port
		properties.put("mail.imap.ssl.enable", "true");
		properties.put("mail.imap.user", "info@pribormaster.ru");
		
		
		
		Session emailSession = Session.getDefaultInstance(properties);
		try {
			Store store = emailSession.getStore(mailStoreType);
			store.connect(host, user, password);
			System.out.println("done!");
			store.close();
			
			
			
		} catch (NoSuchProviderException e) {
			e.printStackTrace();
		} catch (MessagingException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	
	public static void main53(String[] args) {

		String host = "pop.yandex.ru";// change accordingly
		String mailStoreType = "pop3s";

		String user = "info@pribormaster.ru";// change accordingly
		String password = "rNs-N8g-gn5-YwK";// change accordingly
/*
		try {

			// create properties field
			Properties properties = new Properties();

			properties.put("mail.pop3.host", host);
			properties.put("mail.pop3.port", "995");
			properties.put("mail.pop3.starttls.enable", "true");
			Session emailSession = Session.getDefaultInstance(properties);

			// create the POP3 store object and connect with the pop server
			Store store = emailSession.getStore("pop3s");

			store.connect(host, user, password);
			System.out.println("done!");
			store.close();

		} catch (NoSuchProviderException e) {
			e.printStackTrace();
		} catch (MessagingException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		*/
		checkPop52(host, mailStoreType, user, password);

	}
	
	public static void checkPop52(String host, String storeType, String user, String password) {
	      try {

	      //create properties field
	      Properties properties = new Properties();

	      properties.put("mail.pop3.host", host);
	      properties.put("mail.pop3.port", "995");
	      properties.put("mail.pop3.starttls.enable", "true");
	      Session emailSession = Session.getDefaultInstance(properties);
	  
	      //create the POP3 store object and connect with the pop server
	      Store store = emailSession.getStore(storeType);
	      store.connect(host, user, password);
	      System.out.println("done!");

	      //create the folder object and open it
	      Folder emailFolder = store.getFolder("INBOX");
	      emailFolder.open(Folder.READ_ONLY);

	      // retrieve the messages from the folder in an array and print it
	      Message[] messages = emailFolder.getMessages();
	      System.out.println("messages.length---" + messages.length);

	     
	      for (int i = 0, n = messages.length; i < n; i++) {
	         Message message = messages[i];
	         		         
	         System.out.println("---------------------------------");
	         System.out.println("Email Number " + (i + 1));
	         System.out.println("Subject: " + message.getSubject());
	         System.out.println("From: " + message.getFrom()[0]);
	         System.out.println("Text: " + message.getContent().toString());
	         System.out.println("RecieviedDate: " + message.getReceivedDate());
	         
	         if (i > 3) {
	        	 break;
	        	 
	         }
	         

	      }
	      

	      //close the store and folder objects
	      emailFolder.close(false);
	      store.close();

	      } catch (NoSuchProviderException e) {
	         e.printStackTrace();
	      } catch (MessagingException e) {
	         e.printStackTrace();
	      } catch (Exception e) {
	         e.printStackTrace();
	      }
	   }	
	
	      	      

	
	public static void checkImap(String host, String storeType, String user, String password) {
	      try {

	      //create properties field
	      Properties properties = new Properties();
	      Session emailSession = Session.getDefaultInstance(properties);
	  
	      Store store = emailSession.getStore("imaps");	      
	      store.connect(host, user, password);
          
          System.out.println("done!");
           
          // Get the Inbox folder
          Folder emailFolder = store.getFolder("lead");
           
          // Set the mode to the read-only mode
          emailFolder.open(Folder.READ_ONLY);
          
	      // retrieve the messages from the folder in an array and print it
          Date someFutureDate = DateTimeUtils.stringToDate("26.05.2019", "dd.MM.yyyy");
          Date somePastDate = DateTimeUtils.stringToDate("19.05.2019", "dd.MM.yyyy");
          
	      //Message[] messages = emailFolder.getMessages();
	      //System.out.println("messages.length---" + messages.length);
	      
	      SearchTerm olderThan = new ReceivedDateTerm(ComparisonTerm.LT, someFutureDate);	      
	      SearchTerm newerThan = new ReceivedDateTerm(ComparisonTerm.GT, somePastDate);
	      SearchTerm andTerm = new AndTerm(olderThan, newerThan);
	      Message[] messages = emailFolder.search(andTerm);

	      int i = 0;
	      for ( Message message : messages ) {
	    	    	
	    	    	String text = message.getContent().toString();
	    	    	String[] tt = text.split("\r\n");
	    	    	
	    	    	if ("Быстрый заказ".equals(tt[0])) {
	  	    
		   	        	 System.out.println("---------------------------------");
		   		         System.out.println("Email Number " + (i + 1));
		   		         //System.out.println("Subject: " + message.getSubject());
		   		         System.out.println("From: " + message.getFrom()[0]);
		   		         System.out.println("Text: " + message.getContent().toString());
		   		         	   		      
		   		         System.out.println("SentDate: " + message.getSentDate());
		   		         System.out.println("RecieviedDate: " + message.getReceivedDate());
		   		         
		   		         Order order = new Order();
		   		         
		   		         Date orderDate = DateTimeUtils.stringToDate(tt[2].substring(13), "dd.MM.yyyy HH:mm");
		   		         order.setOrderDate(orderDate);
		   		         
		   		         Customer customer = new Customer();
		   		         customer.setFirstName(tt[3].substring(10).trim());
		   		         String phoneNumber = tt[4].substring(9).trim();
		   		         phoneNumber = TextUtils.formatPhoneNumber(phoneNumber);
		   		         
		   		         customer.setPhoneNumber(phoneNumber);
		   		         order.getDelivery().setAnnotation(tt[4].substring(13).trim());
		   		         
		   		         String productName = tt[7].substring(7).trim();
		   		         
		   		         OrderItem orderItem = new OrderItem();
		   		         Product product = new Product(0, productName);
		   		         
		   		         orderItem.setProduct(product);
		   		         orderItem.setQuantity(1);
		   		         
		   		         order.getItems().add(orderItem);
		   		         
		   		         
		   		         

	    	    		
	    	    	}
	   	        	 
	    	    	
	   		         i++;
	    	    }
	      

	     
	      

	      //close the store and folder objects
	      emailFolder.close(false);
	      store.close();

	      } catch (NoSuchProviderException e) {
	         e.printStackTrace();
	      } catch (MessagingException e) {
	         e.printStackTrace();
	      } catch (Exception e) {
	         e.printStackTrace();
	      }
	      
	      System.out.println("end!");
	   }	


	

	public static void main4(String[] args)  {
		
		String host = "pop.yandex.ru";// change accordingly
	    String mailStoreType = "pop3";
	    
	    String username = "sir-richard@sir-richard.ru";// change accordingly
	    String password = "Cuba2017";// change accordingly

	    checkPop3(host, mailStoreType, username, password);
		
	}
	
	
	 public static void checkPop3(String host, String storeType, String user, String password) {
		      try {

		      //create properties field
		      Properties properties = new Properties();

		      properties.put("mail.pop3.host", host);
		      properties.put("mail.pop3.port", "995");
		      properties.put("mail.pop3.starttls.enable", "true");
		      Session emailSession = Session.getDefaultInstance(properties);
		  
		      //create the POP3 store object and connect with the pop server
		      Store store = emailSession.getStore("pop3s");

		      store.connect(host, user, password);

		      //create the folder object and open it
		      Folder emailFolder = store.getFolder("INBOX");
		      emailFolder.open(Folder.READ_ONLY);

		      // retrieve the messages from the folder in an array and print it
		      Message[] messages = emailFolder.getMessages();
		      System.out.println("messages.length---" + messages.length);

		     
		      for (int i = 0, n = messages.length; i < n; i++) {
		         Message message = messages[i];
		         		         
		         System.out.println("---------------------------------");
		         System.out.println("Email Number " + (i + 1));
		         System.out.println("Subject: " + message.getSubject());
		         System.out.println("From: " + message.getFrom()[0]);
		         System.out.println("Text: " + message.getContent().toString());
		         System.out.println("RecieviedDate: " + message.getReceivedDate());
		         
		         if (i > 3) {
		        	 break;
		        	 
		         }
		         

		      }
		      

		      //close the store and folder objects
		      emailFolder.close(false);
		      store.close();

		      } catch (NoSuchProviderException e) {
		         e.printStackTrace();
		      } catch (MessagingException e) {
		         e.printStackTrace();
		      } catch (Exception e) {
		         e.printStackTrace();
		      }
		   }	
	
	
	

	
	public static void main2(String[] args) throws AddressException, MessagingException, UnsupportedEncodingException {
		
		final String fromEmail = "sir-richard@sir-richard.ru"; //requires valid gmail id
		final String password = "Cuba2017"; // correct password for gmail id
				
			
		System.out.println("SSLEmail Start");
		Properties props = new Properties();
		props.put("mail.smtp.host", "smtp.sir-richard.ru"); //SMTP Host
		props.put("mail.smtp.socketFactory.port", "465"); //SSL Port
		props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory"); //SSL Factory Class
		props.put("mail.smtp.auth", "true"); //Enabling SMTP Authentication
		props.put("mail.smtp.port", "465"); //SMTP Port
		
		Session session = Session.getDefaultInstance(props,
                new javax.mail.Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(fromEmail, password);
                    }
                });

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(fromEmail, "sir-richard.ru", "UTF-8"));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse("alex4doorow@gmail.com")); 
        message.setSubject("Ваш заказ отправлен - test");
        message.setText("Алексей! Заказ интернет-магазина www.sir-richard.ru 1105446815 (5389) отправлен. Сумма к оплате 7500 руб. Отследить заказ можно здесь https://www.cdek.ru/track.html?order_id=1105446815");
        Transport.send(message);
        System.out.println("SSLEmail End");
		
	}
	
	public static void main1(String[] args) throws AddressException, MessagingException, UnsupportedEncodingException {
		final String fromEmail = "sir.richard.sales@yandex.ru"; //requires valid gmail id
		final String password = "Dominika2021"; // correct password for gmail id
				
			
		System.out.println("SSLEmail Start");
		Properties props = new Properties();
		props.put("mail.smtp.host", "smtp.yandex.ru"); //SMTP Host
		props.put("mail.smtp.socketFactory.port", "465"); //SSL Port
		props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory"); //SSL Factory Class
		props.put("mail.smtp.auth", "true"); //Enabling SMTP Authentication
		props.put("mail.smtp.port", "465"); //SMTP Port
		
		Session session = Session.getDefaultInstance(props,
                new javax.mail.Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(fromEmail, password);
                    }
                });

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(fromEmail, "sir-richard.ru", "UTF-8"));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse("alex4doorow@gmail.com")); 
        message.setSubject("Ваш заказ отправлен");
        message.setText("Алексей! Заказ интернет-магазина www.sir-richard.ru 1105446815 (5389) отправлен. Сумма к оплате 7500 руб. Отследить заказ можно здесь https://www.cdek.ru/track.html?order_id=1105446815");
        Transport.send(message);
        System.out.println("SSLEmail End");
		
	
	}

}
