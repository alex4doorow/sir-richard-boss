package ru.sir.richard.boss.data.raw;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ru.sir.richard.boss.model.data.AnyCatalog;

public class ProductDataRaw extends AnyCatalog {
	
	private String vendorCode;
	private String categoryProduct;	
	private String quantity;
	private String price;
	private String supplierPrice;
	
	private String model;
	private String sku;
	private String jan;
	private int stockStatusId;	
	private String image;
	private int manufacturerId;	
	private Date dateAvailable;
	private BigDecimal weight;	
	private int weightClassId;
	private BigDecimal length;
	private BigDecimal width;
	private BigDecimal height;
	private int lengthClassId;	
	private int subtract;
	private int minimum;
	private int sortOrder;
	private int status;
	private int viewed;
	private Date dateAdded;
	private Date dateModified;
	private int categoryGroupId;
	private int composite;
		
	private String description;
	private String metaDescription;
	private String metaKeyword;
	private String tag;
	
	private List<String> images = new ArrayList<String>();
	private List<BigDecimal> specialPrices = new ArrayList<BigDecimal>();
	private Set<CategoryDataRaw> categories = new HashSet<CategoryDataRaw>();
				
	public ProductDataRaw() {
		super(0, "");
	}

	public ProductDataRaw(int id, String name) {
		super(id, name);	
	}

	public ProductDataRaw(String productName, String quantity, String price, String supplierPrice) {
		super(0, productName);
		this.quantity = quantity;
		this.price = price;
		this.supplierPrice = supplierPrice;
	}

	public List<String> getImages() {
		return images;
	}	

	public void setImages(List<String> images) {
		this.images = images;
	}
	
	public List<BigDecimal> getSpecialPrices() {
		return specialPrices;
	}

	public void setSpecialPrices(List<BigDecimal> specialPrices) {
		this.specialPrices = specialPrices;
	}

	public int getStockStatusId() {
		return stockStatusId;
	}

	public void setStockStatusId(int stockStatusId) {
		this.stockStatusId = stockStatusId;
	}

	public String getVendorCode() {
		return vendorCode;
	}	

	public String getSku() {
		return sku;
	}

	public void setSku(String sku) {
		this.sku = sku;
	}

	public void setVendorCode(String vendorCode) {
		this.vendorCode = vendorCode;
	}

	public String getQuantity() {
		return quantity;
	}

	public String getProductName() {
		return getName();
	}

	public String getPrice() {
		return price;
	}	
		
	public int getViewed() {
		return viewed;
	}

	public void setViewed(int viewed) {
		this.viewed = viewed;
	}

	public String getCategoryProduct() {
		return categoryProduct;
	}

	public void setCategoryProduct(String categoryProduct) {
		this.categoryProduct = categoryProduct;
	}

	public String getSupplierPrice() {
		return supplierPrice;
	}

	public void setSupplierPrice(String supplierPrice) {
		this.supplierPrice = supplierPrice;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}	

	public int getComposite() {
		return composite;
	}

	public void setComposite(int composite) {
		this.composite = composite;
	}

	public String getJan() {
		return jan;
	}

	public void setJan(String jan) {
		this.jan = jan;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public int getManufacturerId() {
		return manufacturerId;
	}

	public void setManufacturerId(int manufacturerId) {
		this.manufacturerId = manufacturerId;
	}

	public Date getDateAvailable() {
		return dateAvailable;
	}

	public void setDateAvailable(Date date_available) {
		this.dateAvailable = date_available;
	}

	public BigDecimal getWeight() {
		return weight;
	}

	public void setWeight(BigDecimal weight) {
		this.weight = weight;
	}

	public int getWeightClassId() {
		return weightClassId;
	}

	public void setWeightClassId(int weightClassId) {
		this.weightClassId = weightClassId;
	}

	public BigDecimal getLength() {
		return length;
	}

	public void setLength(BigDecimal length) {
		this.length = length;
	}

	public BigDecimal getWidth() {
		return width;
	}

	public void setWidth(BigDecimal width) {
		this.width = width;
	}

	public BigDecimal getHeight() {
		return height;
	}

	public void setHeight(BigDecimal height) {
		this.height = height;
	}

	public int getLengthClassId() {
		return lengthClassId;
	}

	public void setLengthClassId(int lengthClassId) {
		this.lengthClassId = lengthClassId;
	}

	public int getSubtract() {
		return subtract;
	}

	public void setSubtract(int subtract) {
		this.subtract = subtract;
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

	public void setSortOrder(int sort_order) {
		this.sortOrder = sort_order;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public Date getDateAdded() {
		return dateAdded;
	}

	public void setDateAdded(Date dateAdded) {
		this.dateAdded = dateAdded;
	}

	public Date getDateModified() {
		return dateModified;
	}

	public void setDateModified(Date dateModified) {
		this.dateModified = dateModified;
	}

	public int getCategoryGroupId() {
		return categoryGroupId;
	}

	public void setCategoryGroupId(int categoryGroupId) {
		this.categoryGroupId = categoryGroupId;
	}

	public void setQuantity(String quantity) {
		this.quantity = quantity;
	}

	public void setPrice(String price) {
		this.price = price;
	}	

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
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

	public Set<CategoryDataRaw> getCategories() {
		return categories;
	}

	public void setCategories(Set<CategoryDataRaw> categories) {
		this.categories = categories;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((categories == null) ? 0 : categories.hashCode());
		result = prime * result + categoryGroupId;
		result = prime * result + ((categoryProduct == null) ? 0 : categoryProduct.hashCode());
		result = prime * result + composite;
		result = prime * result + ((dateAdded == null) ? 0 : dateAdded.hashCode());
		result = prime * result + ((dateAvailable == null) ? 0 : dateAvailable.hashCode());
		result = prime * result + ((dateModified == null) ? 0 : dateModified.hashCode());
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((height == null) ? 0 : height.hashCode());
		result = prime * result + ((image == null) ? 0 : image.hashCode());
		result = prime * result + ((images == null) ? 0 : images.hashCode());
		result = prime * result + ((jan == null) ? 0 : jan.hashCode());
		result = prime * result + ((length == null) ? 0 : length.hashCode());
		result = prime * result + lengthClassId;
		result = prime * result + manufacturerId;
		result = prime * result + ((metaDescription == null) ? 0 : metaDescription.hashCode());
		result = prime * result + ((metaKeyword == null) ? 0 : metaKeyword.hashCode());
		result = prime * result + minimum;
		result = prime * result + ((model == null) ? 0 : model.hashCode());
		result = prime * result + ((price == null) ? 0 : price.hashCode());
		result = prime * result + ((quantity == null) ? 0 : quantity.hashCode());
		result = prime * result + ((sku == null) ? 0 : sku.hashCode());
		result = prime * result + sortOrder;
		result = prime * result + ((specialPrices == null) ? 0 : specialPrices.hashCode());
		result = prime * result + status;
		result = prime * result + stockStatusId;
		result = prime * result + subtract;
		result = prime * result + ((supplierPrice == null) ? 0 : supplierPrice.hashCode());
		result = prime * result + ((tag == null) ? 0 : tag.hashCode());
		result = prime * result + ((vendorCode == null) ? 0 : vendorCode.hashCode());
		result = prime * result + viewed;
		result = prime * result + ((weight == null) ? 0 : weight.hashCode());
		result = prime * result + weightClassId;
		result = prime * result + ((width == null) ? 0 : width.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		ProductDataRaw other = (ProductDataRaw) obj;
		if (categories == null) {
			if (other.categories != null)
				return false;
		} else if (!categories.equals(other.categories))
			return false;
		if (categoryGroupId != other.categoryGroupId)
			return false;
		if (categoryProduct == null) {
			if (other.categoryProduct != null)
				return false;
		} else if (!categoryProduct.equals(other.categoryProduct))
			return false;
		if (composite != other.composite)
			return false;
		if (dateAdded == null) {
			if (other.dateAdded != null)
				return false;
		} else if (!dateAdded.equals(other.dateAdded))
			return false;
		if (dateAvailable == null) {
			if (other.dateAvailable != null)
				return false;
		} else if (!dateAvailable.equals(other.dateAvailable))
			return false;
		if (dateModified == null) {
			if (other.dateModified != null)
				return false;
		} else if (!dateModified.equals(other.dateModified))
			return false;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (height == null) {
			if (other.height != null)
				return false;
		} else if (!height.equals(other.height))
			return false;
		if (image == null) {
			if (other.image != null)
				return false;
		} else if (!image.equals(other.image))
			return false;
		if (images == null) {
			if (other.images != null)
				return false;
		} else if (!images.equals(other.images))
			return false;
		if (jan == null) {
			if (other.jan != null)
				return false;
		} else if (!jan.equals(other.jan))
			return false;
		if (length == null) {
			if (other.length != null)
				return false;
		} else if (!length.equals(other.length))
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
		if (minimum != other.minimum)
			return false;
		if (model == null) {
			if (other.model != null)
				return false;
		} else if (!model.equals(other.model))
			return false;
		if (price == null) {
			if (other.price != null)
				return false;
		} else if (!price.equals(other.price))
			return false;
		if (quantity == null) {
			if (other.quantity != null)
				return false;
		} else if (!quantity.equals(other.quantity))
			return false;
		if (sku == null) {
			if (other.sku != null)
				return false;
		} else if (!sku.equals(other.sku))
			return false;
		if (sortOrder != other.sortOrder)
			return false;
		if (specialPrices == null) {
			if (other.specialPrices != null)
				return false;
		} else if (!specialPrices.equals(other.specialPrices))
			return false;
		if (status != other.status)
			return false;
		if (stockStatusId != other.stockStatusId)
			return false;
		if (subtract != other.subtract)
			return false;
		if (supplierPrice == null) {
			if (other.supplierPrice != null)
				return false;
		} else if (!supplierPrice.equals(other.supplierPrice))
			return false;
		if (tag == null) {
			if (other.tag != null)
				return false;
		} else if (!tag.equals(other.tag))
			return false;
		if (vendorCode == null) {
			if (other.vendorCode != null)
				return false;
		} else if (!vendorCode.equals(other.vendorCode))
			return false;
		if (viewed != other.viewed)
			return false;
		if (weight == null) {
			if (other.weight != null)
				return false;
		} else if (!weight.equals(other.weight))
			return false;
		if (weightClassId != other.weightClassId)
			return false;
		if (width == null) {
			if (other.width != null)
				return false;
		} else if (!width.equals(other.width))
			return false;
		return true;
	}

	@Override
	public String toString() {		
		return "{productName= " + getName() + ", sku=" +sku + ", category=" + categoryProduct +", quantity= " + quantity + ", price= " + price + ", supplierPrice= " + supplierPrice + "}\b\r";
	}	

}
