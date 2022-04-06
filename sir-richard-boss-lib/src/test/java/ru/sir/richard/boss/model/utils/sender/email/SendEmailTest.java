package ru.sir.richard.boss.model.utils.sender.email;

import java.io.UnsupportedEncodingException;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class SendEmailTest {
	
	//private final Logger logger = LoggerFactory.getLogger(SendEmailTest.class);
	
	//@Test
	public void testSendEmail() throws UnsupportedEncodingException, MessagingException {	
		
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
        
        message.addHeader("Content-type", "text/plain; charset=\"utf-8\"");
		message.addHeader("format", "flowed");
		message.addHeader("Content-Transfer-Encoding", "8bit");
        
        message.setText("Алексей! Заказ интернет-магазина www.sir-richard.ru 1105446815 (5389) отправлен. Сумма к оплате 7500 руб. Отследить заказ можно здесь https://www.cdek.ru/track.html?order_id=1105446815");
        Transport.send(message);
        System.out.println("SSLEmail End");
			
		
	}

}
