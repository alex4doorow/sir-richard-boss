package ru.sir.richard.boss.exception;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CoreException extends Exception {

	public static final String INTERNAL_ERROR = "INTERNAL_ERROR";
	public static final String EXT_SERVICE_ERROR = "EXT_SERVICE_ERROR";
	public static final String EXT_SERVICE_REJECT = "EXT_SERVICE_REJECT";
	public static final String CONVERSION_ERROR = "CONVERSION_ERROR";
	public static final String PARSE_ERROR = "PARSE_ERROR";
	public static final String JSON_PROCESSING_ERROR = "JSON_PROCESSING_ERROR";
	public static final String CONNECTION_ERROR = "CONNECTION_ERROR";
	public static final String CONFIG_ERROR = "CONFIG_ERROR";
	public static final String SEND_ERROR = "SEND_ERROR";
	
	public static final String ACCOUNT_NOT_FOUND_ERROR = "ACCOUNT_NOT_FOUND_ERROR";
		
	public static final String DIR_DOES_NOT_EXIST = "DIR_DOES_NOT_EXIST";
	public static final String FAIL_TO_CREATE_FILE = "FAIL_TO_CREATE_FILE";
	
	public static final String LDAP_CONNECTION_FAILED = "LDAP_CONNECTION_FAILED";
	public static final String LDAP_PASSWORD_EXPIRED = "LDAP_PASSWORD_EXPIRED";
	public static final String LDAP_SEARCH_EXCEPTION = "LDAP_SEARCH_EXCEPTION";

	private boolean fatalException = false;
	private String respCode = null;
	private String respDesc = null;
	
	public CoreException(String respCode, String respDesc) {
		this.respCode = respCode;
		this.respDesc = respDesc;
	}
	
	public CoreException(String respCode) {
		this.respCode = respCode;
	}
	
	public CoreException(String respCode, String respDesc, boolean fatalException) {
		this.respCode = respCode;
		this.respDesc = respDesc;
		this.fatalException = fatalException;
	}
	
	@Override
	public String toString() {
		return getRespCode() + ":" + getRespDesc();
	}
	
	@Override
	public String getMessage() {
		return toString();
	}
}
