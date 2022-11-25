package ru.sir.richard.boss.model.data;

import java.math.BigDecimal;
import java.util.List;

public class CarrierInfo implements Cloneable {
	
	private String region;
	private String cityContext;
	private String shortAddress;
	private String fullAddress;
	
	private int pvzId;
	private int cityId;	
	private String pvz;
	private String street;
	private String house;
	private String flat;
	
	private Long deliveryVariantId;
	
	// add info pvz
	private String workTime;
	private String addressComment;
	private String phone; 
	private String email;
	private String note;
	private String pvzType;
	private String ownerCode;
	private String haveCash;
	private String allowedCod;
	private String nearestStation;
	private String metroStation;
	private String url;
	private String weightMax;
	private BigDecimal coordX;
	private BigDecimal coordY;
	private List<String> postalCodes;
		
	private CourierInfo courierInfo;
	
	public CarrierInfo() {
		this.courierInfo = new CourierInfo();		
	}	

	public BigDecimal getCoordX() {
		return coordX;
	}

	public void setCoordX(BigDecimal coordX) {
		this.coordX = coordX;
	}

	public BigDecimal getCoordY() {
		return coordY;
	}

	public void setCoordY(BigDecimal coordY) {
		this.coordY = coordY;
	}

	public String getPvz() {
		return pvz;
	}

	public void setPvz(String pvz) {
		this.pvz = pvz;
	}

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}
	
	public int getCityId() {
		return cityId;
	}

	public void setCityId(int cityId) {
		this.cityId = cityId;
	}

	public int getPvzId() {
		return pvzId;
	}

	public void setPvzId(int pvzId) {
		this.pvzId = pvzId;
	}

	public String getCityContext() {
		return cityContext;
	}

	public void setCityContext(String cityContext) {
		this.cityContext = cityContext;
	}

	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	public String getHouse() {
		return house;
	}

	public void setHouse(String house) {
		this.house = house;
	}

	public String getFlat() {
		return flat;
	}

	public void setFlat(String flat) {
		this.flat = flat;
	}

	public String getShortAddress() {
		return shortAddress;
	}

	public void setShortAddress(String shortAddress) {
		this.shortAddress = shortAddress;
	}

	public String getFullAddress() {
		return fullAddress;
	}

	public void setFullAddress(String fullAddress) {
		this.fullAddress = fullAddress;
	}
	
	public Long getDeliveryVariantId() {
		return deliveryVariantId;
	}

	public void setDeliveryVariantId(Long deliveryVariantId) {
		this.deliveryVariantId = deliveryVariantId;
	}

	public String getWorkTime() {
		return workTime;
	}

	public void setWorkTime(String workTime) {
		this.workTime = workTime;
	}

	public String getAddressComment() {
		return addressComment;
	}

	public void setAddressComment(String addressComment) {
		this.addressComment = addressComment;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public String getPvzType() {
		return pvzType;
	}

	public void setPvzType(String pvzType) {
		this.pvzType = pvzType;
	}

	public String getOwnerCode() {
		return ownerCode;
	}

	public void setOwnerCode(String ownerCode) {
		this.ownerCode = ownerCode;
	}

	public String getHaveCash() {
		return haveCash;
	}

	public void setHaveCash(String haveCash) {
		this.haveCash = haveCash;
	}

	public String getAllowedCod() {
		return allowedCod;
	}

	public void setAllowedCod(String allowedCod) {
		this.allowedCod = allowedCod;
	}

	public String getNearestStation() {
		return nearestStation;
	}

	public void setNearestStation(String nearestStation) {
		this.nearestStation = nearestStation;
	}

	public String getMetroStation() {
		return metroStation;
	}

	public void setMetroStation(String metroStation) {
		this.metroStation = metroStation;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getWeightMax() {
		return weightMax;
	}

	public void setWeightMax(String weightMax) {
		this.weightMax = weightMax;
	}

	public CourierInfo getCourierInfo() {
		return courierInfo;
	}

	public void setCourierInfo(CourierInfo courierInfo) {
		this.courierInfo = courierInfo;
	}

	public List<String> getPostalCodes() {
		return postalCodes;
	}

	public void setPostalCodes(List<String> postalCodes) {
		this.postalCodes = postalCodes;
	}

	public Integer getPostalSize() {
    	if (postalCodes == null) {
    		return 0;
    	}
    	return postalCodes.size();
    }	
	
	@Override
	public CarrierInfo clone() throws CloneNotSupportedException  {
		CarrierInfo clone = (CarrierInfo) super.clone();		
		clone.region = this.region == null ? null : new String(this.region);		
		clone.cityContext = this.cityContext == null ? null : new String(this.cityContext);
		clone.shortAddress = this.shortAddress == null ? null : new String(this.shortAddress);
		clone.fullAddress = this.fullAddress == null ? null : new String(this.fullAddress);		
		clone.cityId = this.cityId;
		clone.pvz = this.pvz == null ? null : new String(this.pvz);
		clone.street = this.street == null ? null : new String(this.street);		
		clone.house = this.house == null ? null : new String(this.house);
		clone.flat = this.flat == null ? null : new String(this.flat);
		clone.courierInfo = this.courierInfo == null ? null : new CourierInfo();
		return clone;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((cityContext == null) ? 0 : cityContext.hashCode());
		result = prime * result + cityId;
		result = prime * result + ((pvz == null) ? 0 : pvz.hashCode());
		result = prime * result + ((fullAddress == null) ? 0 : fullAddress.hashCode());
		result = prime * result + ((region == null) ? 0 : region.hashCode());
		result = prime * result + ((shortAddress == null) ? 0 : shortAddress.hashCode());
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
		CarrierInfo other = (CarrierInfo) obj;
		if (cityContext == null) {
			if (other.cityContext != null)
				return false;
		} else if (!cityContext.equals(other.cityContext))
			return false;
		if (cityId != other.cityId)
			return false;
		if (pvz == null) {
			if (other.pvz != null)
				return false;
		} else if (!pvz.equals(other.pvz))
			return false;
		if (fullAddress == null) {
			if (other.fullAddress != null)
				return false;
		} else if (!fullAddress.equals(other.fullAddress))
			return false;
		if (region == null) {
			if (other.region != null)
				return false;
		} else if (!region.equals(other.region))
			return false;
		if (shortAddress == null) {
			if (other.shortAddress != null)
				return false;
		} else if (!shortAddress.equals(other.shortAddress))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "CdekPvz [pvz=" + pvz + ", region=" + region + ", cityId=" + cityId + ", cityContext=" + cityContext
				+ ", shortAddress=" + shortAddress + ", fullAddress=" + fullAddress 
				+ "]";
	}
	
}

	