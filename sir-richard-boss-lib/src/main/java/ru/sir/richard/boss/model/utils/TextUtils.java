package ru.sir.richard.boss.model.utils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import com.ibm.icu.text.RuleBasedNumberFormat;

import ru.sir.richard.boss.model.types.CustomerTypes;
import ru.sir.richard.boss.model.types.DeliveryTypes;
import ru.sir.richard.boss.model.types.OrderAdvertTypes;
import ru.sir.richard.boss.model.types.OrderStatuses;
import ru.sir.richard.boss.model.types.PaymentTypes;

public class TextUtils {
		
	/**
	 * удаление символов html разметки
	 * @param text
	 * @return
	 */
	public static String removeHTMLShit(String text) {
				
		if (text == null || text.isEmpty()) {
			return text;
		}			
		return text.replaceAll("&quot;", "\"").replaceAll("  ", " ").replaceAll("&nbsp;", " ").trim();			
	}
	
	public static String repaceToHTMLTag(String text) {
		if (text == null || text.isEmpty()) {
			return text;
		}	
		// import org.apache.commons.text.StringEscapeUtils;
		return text.replaceAll("\"", "&quot;").trim();
	}
	
	/**
	 * экранирование кавычек
	 * @param text
	 * @return
	 */
	public static String escapingQuotes(String text) {
		if (text == null || text.isEmpty()) {
			return text;
		}	
		char quote = '"';
		char slash = '\\';
		String sn = String.valueOf(slash) + String.valueOf(slash) + String.valueOf(quote);
		String so = String.valueOf(quote);
		return text.replaceAll(so, sn).trim();
	}
		
	public static String numberDigit(String stringValue) {
		
		if (StringUtils.isEmpty(stringValue)) {
			return "";
		}
		String result = "";
		for (int i = 0; i < stringValue.length(); i++) {
			
			if (Character.isDigit(stringValue.charAt(i))) {
				result += stringValue.charAt(i);
			}
		}
		return result;		
	}
	
	public static String phoneNumberDigit(String phoneNumber) {
		return numberDigit(phoneNumber);
	}
	
	/* -> (999) 123-45-67 */
	public static String formatPhoneNumber(String phoneNumber) {
		String s = phoneNumber.trim();
		
		String sStart = StringUtils.substring(s, 0, 3);
		String sEnd = StringUtils.substring(s, 3, s.length());
		
		if (StringUtils.startsWith(s, "+") || StringUtils.startsWith(s, "7") || StringUtils.startsWith(s, "8")) {
			sStart = StringUtils.replace(sStart, "+7", "", 1);
			sStart = StringUtils.replace(sStart, "89", "9", 1);
			sStart = StringUtils.replace(sStart, "79", "9", 1);
			sStart = StringUtils.replace(sStart, "8(9", "9", 1);
			sStart = StringUtils.replace(sStart, "8 (9", "9", 1);		
			sStart = StringUtils.replace(sStart, "8-9", "9", 1);
			sStart = StringUtils.replace(sStart, "8 9", "9", 1);
		}		
		s = sStart + sEnd;
		s = phoneNumberDigit(s);
		
		String r1 = StringUtils.substring(s, 0, 3);
		String r2 = StringUtils.substring(s, 3, 6);
		String r3 = StringUtils.substring(s, 6, 8);
		String r4 = StringUtils.substring(s, 8, 10);
		
		return "(" + r1 + ") " + r2 + "-" + r3 + "-" + r4;		
	}
	
	public static boolean textIsEmail(String text) {
		String regex = "^(.+)@(.+)$";
		Pattern pattern = Pattern.compile(regex);
		String cleanText = text.trim();
		Matcher matcher = pattern.matcher(cleanText);
		return matcher.matches();
	}
	
	//Владимир, пр-т Строителей, 15 -> Владимир 
	public static String cutCityFromAddress(String address) {
		if (StringUtils.isEmpty(address)) {
			return "";
		}
		String[] addressParts = address.trim().split(",");
		String city = "";
		if (addressParts.length > 1) {
			city = addressParts[0];	
		}
		city = city.replace("г.", "").trim();
		return city.length() <= 30 ? city : city.substring(0, 30);
	}
	
	// Владимир, пр-т Строителей, 15 -> пр-т Строителей, 15
	public static String cutStreetFromAddress(String address) {
		if (StringUtils.isEmpty(address)) {
			return "";
		}
		String[] addressParts = address.trim().split(",");
		String city = "";
		String street = "";
		if (addressParts.length > 1) {
			city = addressParts[0];
			street = StringUtils.trim(StringUtils.substring(address, city.length() + 1, address.length()));
		}
		return street;
	} 
	
	/**
	 * сумма прописью
	 * @param value
	 * @return
	 */
	public static String numberToPhrase(Number value) {
		RuleBasedNumberFormat nf = new RuleBasedNumberFormat(Locale.forLanguageTag("ru"), RuleBasedNumberFormat.SPELLOUT);
		String result = nf.format(value);
		result = StringUtils.capitalize(result);
		return result;
	}
	
	/**
	 * рубли прописью
	 * @param value
	 * @return
	 */
	public static String roublesToPhrase(BigDecimal value) {
		BigInteger roubles = value.toBigInteger();		
		BigInteger cents = value.remainder(BigDecimal.ONE).multiply(BigDecimal.valueOf(100)).toBigInteger();
						
		String objectTextRoubles = createTailText(roubles, "рубль,рубля,рублей");
		String objectTextCents = createTailText(cents, "копейка,копейки,копеек");
				
		String phraseRoubles = numberToPhrase(roubles);
		return phraseRoubles + " " + objectTextRoubles + " " + StringUtils.leftPad(cents.toString(), 2, '0') + " " + objectTextCents;
	}
	
	public static List<String> convertDelimitedStringToList(String delimitedString) {

		List<String> result = new ArrayList<String>();
		if (!StringUtils.isEmpty(delimitedString)) {
			result = Arrays.asList(StringUtils.reverseDelimited(delimitedString, ','));
		}
		return result;
	}

	public static String convertStringListToDelimitedString(List<String> list) {

		String result = "";
		if (list != null) {
			result = StringUtils.join(list.toArray(), ',');
		}
		return result;
	}
	
	public static String convertIntegerListToDelimitedString(List<Integer> list) {

		String result = "";
		if (list != null) {
			result = StringUtils.join(list.toArray(), ',');
		}
		return result;
	}
	
	private static String createTailText(BigInteger partNumber, String stringObjectTextVariants) {
		

		String[] objectTextVariants = stringObjectTextVariants.split(",");		
		String textPartNumber = partNumber.toString();
		int textPartNumberLength = textPartNumber.length();
		
		int tailTextPartNumber2Dig;
		if (textPartNumber.length() == 1) {
			tailTextPartNumber2Dig = Integer.valueOf(textPartNumber.substring(textPartNumberLength - 1, textPartNumberLength));
		} else {
			tailTextPartNumber2Dig = Integer.valueOf(textPartNumber.substring(textPartNumberLength - 2, textPartNumberLength));		
		}
		int tailTextPartNumber1Dig = Integer.valueOf(textPartNumber.substring(textPartNumberLength - 1, textPartNumberLength));					
		
		String objectTextRoubles; 
		if (tailTextPartNumber2Dig < 10 || tailTextPartNumber2Dig > 20) {
			
			if (tailTextPartNumber1Dig == 1) {
				objectTextRoubles = objectTextVariants[0];
			} else if (tailTextPartNumber1Dig == 2 || tailTextPartNumber1Dig == 3 || tailTextPartNumber1Dig == 4) {
				objectTextRoubles = objectTextVariants[1];
			} else if (tailTextPartNumber1Dig == 5 || tailTextPartNumber1Dig == 6 || tailTextPartNumber1Dig == 7 || tailTextPartNumber1Dig == 8 || tailTextPartNumber1Dig == 9) {
				objectTextRoubles = objectTextVariants[2];
			} else {
				objectTextRoubles = objectTextVariants[2];
			} 
			
		} else if (tailTextPartNumber2Dig >= 10) {
			objectTextRoubles = objectTextVariants[2];
		} else {
			objectTextRoubles = objectTextVariants[2];
		}
		return objectTextRoubles;
	} 
	
	/**
	 * транслитерация
	 * @param message
	 * @return
	 */		
	public static String transliterate(String message) {
		char[] abcCyr = { ' ', '-', '.', 'а', 'б', 'в', 'г', 'д', 'е', 'ё', 'ж', 'з', 'и', 'й', 'к', 'л', 'м', 'н', 'о', 'п', 'р',
				'с', 'т', 'у', 'ф', 'х', 'ц', 'ч', 'ш', 'щ', 'ъ', 'ы', 'ь', 'э', 'ю', 'я', 'А', 'Б', 'В', 'Г', 'Д', 'Е',
				'Ё', 'Ж', 'З', 'И', 'Й', 'К', 'Л', 'М', 'Н', 'О', 'П', 'Р', 'С', 'Т', 'У', 'Ф', 'Х', 'Ц', 'Ч', 'Ш', 'Щ',
				'Ъ', 'Ы', 'Ь', 'Э', 'Ю', 'Я', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o',
				'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J',
				'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z' };
		String[] abcLat = { " ", "-", ".", "a", "b", "v", "g", "d", "e", "e", "zh", "z", "i", "y", "k", "l", "m", "n", "o", "p",
				"r", "s", "t", "u", "f", "h", "ts", "ch", "sh", "sch", "", "i", "", "e", "ju", "ja", "A", "B", "V", "G",
				"D", "E", "E", "Zh", "Z", "I", "Y", "K", "L", "M", "N", "O", "P", "R", "S", "T", "U", "F", "H", "Ts",
				"Ch", "Sh", "Sch", "", "I", "", "E", "Ju", "Ja", "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k",
				"l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z", "A", "B", "C", "D", "E", "F",
				"G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z" };
		StringBuilder builder = new StringBuilder();
		
		for (int i = 0; i < message.length(); i++) {
			for (int x = 0; x < abcCyr.length; x++) {
				if (message.charAt(i) == abcCyr[x]) {
					builder.append(abcLat[x]);
				}
			}
		}
		return builder.toString();
	}
	
	/**
	 * "1,2,3,4" -> Set<DeliveryTypes>
	 * @param stringArray
	 * @return
	 */
	public static Set<Object> getStatusesByArray(String strValues, Class<?> clazz) {
		Set<Object> results = new HashSet<Object>();
		if (StringUtils.isEmpty(strValues)) {
			return results;
		}
		final String spliter = ",";
		String[] arrValues = strValues.split(spliter);		
		for (String arrValue : arrValues) {
			Object status = null;
			if (clazz.equals(DeliveryTypes.class)) {
				status = DeliveryTypes.getValueById(Integer.valueOf(arrValue));
			} else if (clazz.equals(OrderStatuses.class)) {
				status = OrderStatuses.getValueById(Integer.valueOf(arrValue));
			} else if (clazz.equals(CustomerTypes.class)) {
				status = CustomerTypes.getValueById(Integer.valueOf(arrValue));
			} else if (clazz.equals(PaymentTypes.class)) {
				status = PaymentTypes.getValueById(Integer.valueOf(arrValue));
			} else if (clazz.equals(OrderAdvertTypes.class)) {
				status = OrderAdvertTypes.getValueById(Integer.valueOf(arrValue));
			}
			if (status != null) {
				results.add(status);
			}				
		}
		return results;
	}
	
	public static String getArrayByStatuses(Set<Object> values, Class<?> clazz) {
		if (values == null || values.size() == 0) {
			return "";
		}
		
		final String spliter = ",";
		String result = "";
		for (Object value : values) {			
			if (clazz.equals(DeliveryTypes.class)) {				
				result += String.valueOf(((DeliveryTypes) value).getId()) + spliter;	
			} else if (clazz.equals(OrderStatuses.class)) {				
				result += String.valueOf(((OrderStatuses) value).getId()) + spliter;	
			} else if (clazz.equals(PaymentTypes.class)) {				
				result += String.valueOf(((PaymentTypes) value).getId()) + spliter;	
			} else if (clazz.equals(CustomerTypes.class)) {				
				result += String.valueOf(((CustomerTypes) value).getId()) + spliter;	
			} else if (clazz.equals(OrderAdvertTypes.class)) {				
				result += String.valueOf(((OrderAdvertTypes) value).getId()) + spliter;	
			}
		}		
		result = result.trim();
		String spliter1 = result.substring(result.length() - 1, result.length()).trim();
		if (spliter1.equals(spliter)) {
			result = result.substring(0, result.length() - 1);
		}		
		return result.trim();		
	}
	
	public static String utf8EncodedString(String inputValue, Charset charset) {
		return new String(inputValue.getBytes(charset), charset);		
	}	
}
