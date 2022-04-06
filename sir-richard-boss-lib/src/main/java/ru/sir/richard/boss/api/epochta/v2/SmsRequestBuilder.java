package ru.sir.richard.boss.api.epochta.v2;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SmsRequestBuilder {

	String URL;

	public SmsRequestBuilder(String URL) {
		this.URL = URL;
	}
		
	public String doXMLQuery(String xml) {
		StringBuilder responseString = new StringBuilder();

		Map<String, String> params = new HashMap<String, String>();
		params.put("XML", xml);
		try {
			SmsConnector.sendPostRequest(this.URL, params);
			String[] response = SmsConnector.readMultipleLinesRespone();
			for (String line : response) {
				responseString.append(line);
			}
			
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		SmsConnector.disconnect();
		return responseString.toString();
		
	}
}