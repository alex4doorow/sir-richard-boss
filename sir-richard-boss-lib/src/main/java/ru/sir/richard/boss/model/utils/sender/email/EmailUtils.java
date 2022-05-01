package ru.sir.richard.boss.model.utils.sender.email;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.search.AndTerm;
import javax.mail.search.ComparisonTerm;
import javax.mail.search.ReceivedDateTerm;
import javax.mail.search.SearchTerm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.PropertyResolver;

import ru.sir.richard.boss.model.types.StoreTypes;
import ru.sir.richard.boss.model.utils.DateTimeUtils;

public class EmailUtils {
	
	private final Logger logger = LoggerFactory.getLogger(EmailUtils.class);
	
	private final PropertyResolver environment;
	
	public EmailUtils(PropertyResolver environment) {
		super();
		this.environment = environment;
	} 
	
	public boolean sendSSLEmail(StoreTypes store, String toEmail, String subject, String body) {
		
		final String pmFromEmail = environment.getProperty("mail.auth.from");
		final String pmPassword = environment.getProperty("mail.auth.key");
		
		String fromEmail;
		String password;

		fromEmail = pmFromEmail;
		password = pmPassword;
		
		logger.debug("email:{}", "start");		
		Properties props = new Properties();
		props.put("mail.smtp.host", environment.getProperty("mail.smtp.host")); //SMTP Host
		props.put("mail.smtp.socketFactory.port", environment.getProperty("mail.smtp.socketFactory.port")); //SSL Port
		props.put("mail.smtp.socketFactory.class", environment.getProperty("mail.smtp.socketFactory.class")); //SSL Factory Class
		props.put("mail.smtp.auth", environment.getProperty("mail.smtp.auth")); //Enabling SMTP Authentication
		props.put("mail.smtp.port", environment.getProperty("mail.smtp.port")); //SMTP Port
				
		Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(fromEmail, password);
                    }
                });

		boolean result;
		try {
			MimeMessage message = new MimeMessage(session);
	        message.setFrom(new InternetAddress(fromEmail, store.getEmail(), "UTF-8"));  
	        message.addRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
	        message.addRecipients(Message.RecipientType.BCC, InternetAddress.parse(store.getEmail()));
	        message.setSubject(subject, "utf-8");
	        
	        String replacedBody = body.replace("\"", "").replaceAll("→", "->").replaceAll("“", "\"").replaceAll("”", "\"");   
	        message.setText(replacedBody, "utf-8");	        
	        
	        message.addHeader("Content-type", "text/plain; charset=\"utf-8\"");
	        message.addHeader("Content-Transfer-Encoding", "8bit");
	        message.addHeader("format", "flowed");			
	        	        
	        Transport.send(message);
	        logger.debug("email:{}", "end");
	        result = true;
			
		} catch (Exception e) {
			logger.error("email:{} {}", "fail", e);
			result = false;
		}
		return result;
	}
	
	public static boolean sendEmail(PropertyResolver environment, StoreTypes store, String toEmail, String subject, String body) {		
		EmailUtils emailUtils = new EmailUtils(environment);
		return emailUtils.sendSSLEmail(store, toEmail, subject, body);
	}
	
	public List<String> loadMessagesFromEmail(StoreTypes shopStore, Date executorDate, String folderName) {
				
		List<String> result = new ArrayList<String>();		
		String host = environment.getProperty("mail.imap.host");
	    String mailStoreType = "imaps";	    
	    String username = shopStore.getEmail();
	    String password;
	    Properties properties = new Properties();
	    password = environment.getProperty("mail.imap.auth.key");	    
		    	    
		try {
			Session emailSession = Session.getDefaultInstance(properties);
			Store store = emailSession.getStore(mailStoreType);
			store.connect(host, 993, username, password);
			Folder emailFolder = store.getFolder(folderName);
			emailFolder.open(Folder.READ_ONLY);
			
			Date someFutureDate = DateTimeUtils.afterAnyDate(executorDate, 1);
			Date somePastDate = DateTimeUtils.beforeAnyDate(executorDate, 2);
			SearchTerm olderThan = new ReceivedDateTerm(ComparisonTerm.LT, someFutureDate);
			SearchTerm newerThan = new ReceivedDateTerm(ComparisonTerm.GT, somePastDate);
			SearchTerm andTerm = new AndTerm(olderThan, newerThan);
			Message[] messages = emailFolder.search(andTerm);

			for (Message message : messages) {
				result.add(message.getContent().toString());
			}			
			emailFolder.close(false);
			store.close();
			
		} catch (MessagingException e) {
			logger.error("email:{} {}", "fail", e);
		} catch (IOException e) {
			logger.error("email:{} {}", "fail", e);
		} 
		
		return result;
	}
}