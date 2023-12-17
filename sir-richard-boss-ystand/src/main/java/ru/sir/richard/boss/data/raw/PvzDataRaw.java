package ru.sir.richard.boss.data.raw;

public class PvzDataRaw {
	
	private String city;
	private String code;
	private String address;
	private String phones;
	private String scheduleWorks;
	private String postCode;

	public PvzDataRaw() {
		super();
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}	
	
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getPhones() {
		return phones;
	}
	public void setPhones(String phones) {
		this.phones = phones;
	}
	public String getScheduleWorks() {
		return scheduleWorks;
	}
	public void setScheduleWorks(String scheduleWorks) {
		this.scheduleWorks = scheduleWorks;
	}
	public String getPostCode() {
		return postCode;
	}
	public void setPostCode(String postCode) {
		this.postCode = postCode;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((address == null) ? 0 : address.hashCode());
		result = prime * result + ((city == null) ? 0 : city.hashCode());
		result = prime * result + ((code == null) ? 0 : code.hashCode());
		result = prime * result + ((phones == null) ? 0 : phones.hashCode());
		result = prime * result + ((postCode == null) ? 0 : postCode.hashCode());
		result = prime * result + ((scheduleWorks == null) ? 0 : scheduleWorks.hashCode());
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
		PvzDataRaw other = (PvzDataRaw) obj;
		if (address == null) {
			if (other.address != null)
				return false;
		} else if (!address.equals(other.address))
			return false;
		if (city == null) {
			if (other.city != null)
				return false;
		} else if (!city.equals(other.city))
			return false;
		if (code == null) {
			if (other.code != null)
				return false;
		} else if (!code.equals(other.code))
			return false;
		if (phones == null) {
			if (other.phones != null)
				return false;
		} else if (!phones.equals(other.phones))
			return false;
		if (postCode == null) {
			if (other.postCode != null)
				return false;
		} else if (!postCode.equals(other.postCode))
			return false;
		if (scheduleWorks == null) {
			if (other.scheduleWorks != null)
				return false;
		} else if (!scheduleWorks.equals(other.scheduleWorks))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "PvzDataRaw [city=" + city + ", code=" + code + ", address=" + address + ", phones=" + phones
				+ ", scheduleWorks=" + scheduleWorks + ", postCode=" + postCode + "]";
	}
	
	

}
