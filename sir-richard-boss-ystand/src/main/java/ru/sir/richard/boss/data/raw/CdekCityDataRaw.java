package ru.sir.richard.boss.data.raw;

public class CdekCityDataRaw {
		
	private int id;
	private String name;
	private String cityName;
	private String regionName;
	private int center;
	private double casheLimit;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCityName() {
		return cityName;
	}
	public void setCityName(String cityName) {
		this.cityName = cityName;
	}
	public String getRegionName() {
		return regionName;
	}
	public void setRegionName(String regionName) {
		this.regionName = regionName;
	}
	public int getCenter() {
		return center;
	}
	public void setCenter(int center) {
		this.center = center;
	}
	public double getCasheLimit() {
		return casheLimit;
	}
	public void setCasheLimit(double casheLimit) {
		this.casheLimit = casheLimit;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(casheLimit);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + center;
		result = prime * result + ((cityName == null) ? 0 : cityName.hashCode());
		result = prime * result + id;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((regionName == null) ? 0 : regionName.hashCode());
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
		CdekCityDataRaw other = (CdekCityDataRaw) obj;
		if (Double.doubleToLongBits(casheLimit) != Double.doubleToLongBits(other.casheLimit))
			return false;
		if (center != other.center)
			return false;
		if (cityName == null) {
			if (other.cityName != null)
				return false;
		} else if (!cityName.equals(other.cityName))
			return false;
		if (id != other.id)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (regionName == null) {
			if (other.regionName != null)
				return false;
		} else if (!regionName.equals(other.regionName))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "CdekCityDataRaw [id=" + id + ", name=" + name + ", cityName=" + cityName + ", regionName=" + regionName
				+ ", center=" + center + ", casheLimit=" + casheLimit + "]";
	}

}
