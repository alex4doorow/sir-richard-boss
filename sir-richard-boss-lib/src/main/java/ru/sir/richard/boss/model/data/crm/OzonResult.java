package ru.sir.richard.boss.model.data.crm;

import java.util.ArrayList;
import java.util.List;


public class OzonResult {
	
	private String dirtyResponce;
	private List<OzonResultBean> response;	
	private boolean responseSuccess;
	
	public OzonResult() {
		super();
		response = new ArrayList<OzonResultBean>();

	}


	public List<OzonResultBean> getResponse() {
		return response;
	}


	public boolean isResponseSuccess() {
		return responseSuccess;
	}

	public void setResponseSuccess(boolean responseSuccess) {
		this.responseSuccess = responseSuccess;
	}


	public String getDirtyResponce() {
		return dirtyResponce;
	}


	public void setDirtyResponce(String dirtyResponce) {
		this.dirtyResponce = dirtyResponce;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((response == null) ? 0 : response.hashCode());
		result = prime * result + (responseSuccess ? 1231 : 1237);
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		OzonResult other = (OzonResult) obj;
		if (response == null) {
			if (other.response != null)
				return false;
		} else if (!response.equals(other.response))
			return false;
		if (responseSuccess != other.responseSuccess)
			return false;
		return true;
	}


	@Override
	public String toString() {
		return "OzonResult [response=" + response + ", responseSuccess=" + responseSuccess + "]";
	}
	
	
	

}
