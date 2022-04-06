package ru.sir.richard.boss.web.response;

import com.fasterxml.jackson.annotation.JsonView;

public abstract class AnyAjaxResponseBody {
	
	@JsonView(Views.Public.class)
	String msg;
	
	@JsonView(Views.Public.class)
	String code;

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
	
	

}
