package ru.sir.richard.boss.model.data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Данные для фронта
 * @author alex4doorow
 *
 */
public class ProductStore implements Cloneable {

	private String jan;
	private String isbn;
	private int stockStatusId;	
	private int points;
	private Date availableDate;
	
	private int minimum;
	private int sortOrder;
	private int status;
	private Date addedDate;
	private Date modifiedDate;
	private int viewed;

	private String description;
	
	private String metaDescription;
	private String metaKeyword;
	private String metaTitle;
	private String tag;
	
	private int manufacturerId;
	private String manufacturer;
	private String countryOrigin;
	
	private String picture;
	
	private int weightClassId; // 1 kg 2 g
	private int lengthClassId; // 1 cm 2 mm
	private BigDecimal weight; // вес в кг
	
	// габариты, см
	private int length; // длина, см
	private int width;  // ширина, см
	private int height; // высота, см 
	
	// price (на витрине)
	/*
	private BigDecimal beforeDiscountPrice; 
	private BigDecimal afterDiscountPrice;	
	*/
	private List<ProductSpecialPrice> specialPrices;
	
	private List<ProductImage> images;	

	public ProductStore() {
		super();
		this.images = new ArrayList<ProductImage>();
		this.specialPrices = new ArrayList<ProductSpecialPrice>();
		this.weight = BigDecimal.valueOf(0.5);
		this.weightClassId = 1;
		this.lengthClassId = 1;
		this.length = 10;
		this.width = 10;
		this.height = 10;
	}

	public String getJan() {
		return jan;
	}

	public void setJan(String jan) {
		this.jan = jan;
	}

	public String getIsbn() {
		return isbn;
	}

	public void setIsbn(String isbn) {
		this.isbn = isbn;
	}

	public int getStockStatusId() {
		return stockStatusId;
	}

	public void setStockStatusId(int stockStatusId) {
		this.stockStatusId = stockStatusId;
	}

	public int getManufacturerId() {
		return manufacturerId;
	}

	public void setManufacturerId(int manufacturerId) {
		this.manufacturerId = manufacturerId;
	}

	public int getPoints() {
		return points;
	}

	public void setPoints(int points) {
		this.points = points;
	}

	public Date getAvailableDate() {
		return availableDate;
	}

	public void setAvailableDate(Date availableDate) {
		this.availableDate = availableDate;
	}

	public int getWeightClassId() {
		return weightClassId;
	}

	public void setWeightClassId(int weightClassId) {
		this.weightClassId = weightClassId;
	}

	public int getLengthClassId() {
		return lengthClassId;
	}

	public void setLengthClassId(int lengthClassId) {
		this.lengthClassId = lengthClassId;
	}

	public int getMinimum() {
		return minimum;
	}

	public void setMinimum(int minimum) {
		this.minimum = minimum;
	}

	public int getSortOrder() {
		return sortOrder;
	}

	public void setSortOrder(int sortOrder) {
		this.sortOrder = sortOrder;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public Date getAddedDate() {
		return addedDate;
	}

	public void setAddedDate(Date addedDate) {
		this.addedDate = addedDate;
	}

	public Date getModifiedDate() {
		return modifiedDate;
	}

	public void setModifiedDate(Date modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	public int getViewed() {
		return viewed;
	}

	public void setViewed(int viewed) {
		this.viewed = viewed;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getMetaDescription() {
		return metaDescription;
	}

	public void setMetaDescription(String metaDescription) {
		this.metaDescription = metaDescription;
	}

	public String getMetaKeyword() {
		return metaKeyword;
	}

	public void setMetaKeyword(String metaKeyword) {
		this.metaKeyword = metaKeyword;
	}

	public String getMetaTitle() {
		return metaTitle;
	}

	public void setMetaTitle(String metaTitle) {
		this.metaTitle = metaTitle;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}
	
	public List<ProductImage> getImages() {
		return images;
	}
		
	public void setImages(List<ProductImage> images) {
		this.images = images;
	}

	public List<ProductSpecialPrice> getSpecialPrices() {
		return specialPrices;
	}

	public void setSpecialPrices(List<ProductSpecialPrice> specialPrices) {
		this.specialPrices = specialPrices;
	}	
	
	public String getManufacturer() {
		return manufacturer;
	}

	public void setManufacturer(String manufacturer) {
		this.manufacturer = manufacturer;
	}

	public String getCountryOrigin() {
		return countryOrigin;
	}

	public void setCountryOrigin(String countryOrigin) {
		this.countryOrigin = countryOrigin;
	}

	public String getPicture() {
		return picture;
	}

	public void setPicture(String picture) {
		this.picture = picture;
	}
	
	public BigDecimal getWeight() {
		return weight;
	}

	public void setWeight(BigDecimal weight) {
		this.weight = weight;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}		

	@Override
	public ProductStore clone() throws CloneNotSupportedException  {		
		ProductStore clone = (ProductStore) super.clone();
		// ...
		return clone;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((addedDate == null) ? 0 : addedDate.hashCode());
		result = prime * result + ((availableDate == null) ? 0 : availableDate.hashCode());
		result = prime * result + ((images == null) ? 0 : images.hashCode());
		result = prime * result + ((isbn == null) ? 0 : isbn.hashCode());
		result = prime * result + ((jan == null) ? 0 : jan.hashCode());
		result = prime * result + lengthClassId;
		result = prime * result + manufacturerId;
		result = prime * result + ((metaDescription == null) ? 0 : metaDescription.hashCode());
		result = prime * result + ((metaKeyword == null) ? 0 : metaKeyword.hashCode());
		result = prime * result + ((metaTitle == null) ? 0 : metaTitle.hashCode());
		result = prime * result + minimum;
		result = prime * result + ((modifiedDate == null) ? 0 : modifiedDate.hashCode());
		result = prime * result + points;
		result = prime * result + sortOrder;
		result = prime * result + status;
		result = prime * result + stockStatusId;
		result = prime * result + ((tag == null) ? 0 : tag.hashCode());
		result = prime * result + viewed;
		result = prime * result + weightClassId;
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
		ProductStore other = (ProductStore) obj;
		if (addedDate == null) {
			if (other.addedDate != null)
				return false;
		} else if (!addedDate.equals(other.addedDate))
			return false;
		if (availableDate == null) {
			if (other.availableDate != null)
				return false;
		} else if (!availableDate.equals(other.availableDate))
			return false;
		
		if (images == null) {
			if (other.images != null)
				return false;
		} else if (!images.equals(other.images))
			return false;
		if (isbn == null) {
			if (other.isbn != null)
				return false;
		} else if (!isbn.equals(other.isbn))
			return false;
		if (jan == null) {
			if (other.jan != null)
				return false;
		} else if (!jan.equals(other.jan))
			return false;
		if (lengthClassId != other.lengthClassId)
			return false;
		if (manufacturerId != other.manufacturerId)
			return false;
		if (metaDescription == null) {
			if (other.metaDescription != null)
				return false;
		} else if (!metaDescription.equals(other.metaDescription))
			return false;
		if (metaKeyword == null) {
			if (other.metaKeyword != null)
				return false;
		} else if (!metaKeyword.equals(other.metaKeyword))
			return false;
		if (metaTitle == null) {
			if (other.metaTitle != null)
				return false;
		} else if (!metaTitle.equals(other.metaTitle))
			return false;
		if (minimum != other.minimum)
			return false;
		if (modifiedDate == null) {
			if (other.modifiedDate != null)
				return false;
		} else if (!modifiedDate.equals(other.modifiedDate))
			return false;
		if (points != other.points)
			return false;
		if (sortOrder != other.sortOrder)
			return false;
		if (status != other.status)
			return false;
		if (stockStatusId != other.stockStatusId)
			return false;
		if (tag == null) {
			if (other.tag != null)
				return false;
		} else if (!tag.equals(other.tag))
			return false;
		if (viewed != other.viewed)
			return false;
		if (weightClassId != other.weightClassId)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ProductStore [jan=" + jan + ", isbn=" + isbn + ", stockStatusId=" + stockStatusId 
				+ ", manufacturerId=" + manufacturerId + ", points=" + points + ", availableDate=" + availableDate
				+ ", weightClassId=" + weightClassId + ", lengthClassId=" + lengthClassId + ", minimum=" + minimum
				+ ", sortOrder=" + sortOrder + ", status=" + status + ", addedDate=" + addedDate + ", modifiedDate="
				+ modifiedDate + ", viewed=" + viewed + ", metaDescription=" + metaDescription + ", metaKeyword="
				+ metaKeyword + ", metaTitle=" + metaTitle + ", tag=" + tag + ", images=" + images + "]";
	}

}
