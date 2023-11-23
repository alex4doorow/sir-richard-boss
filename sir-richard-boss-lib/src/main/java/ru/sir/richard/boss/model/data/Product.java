package ru.sir.richard.boss.model.data;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import ru.sir.richard.boss.model.types.CrmTypes;
import ru.sir.richard.boss.model.types.PaymentDeliveryMethods;
import ru.sir.richard.boss.model.types.ProductTypes;
import ru.sir.richard.boss.model.types.SupplierTypes;
import ru.sir.richard.boss.model.utils.NumberUtils;

public class Product extends AnyCatalog {
	private String model;
	private String sku;
	private String deliveryName;
	private int quantity;
	private BigDecimal price; // фактическая цена
	private BigDecimal priceWithoutDiscount; // цена без дисконта
	private BigDecimal priceWithDiscount; // цена c дисконтом
	private BigDecimal supplierPrice;
	private int supplierQuantity;
	private int stockQuantity;
	private int slaveQuantity;
	private ProductCategory category;
	private PaymentDeliveryMethods deliveryMethod;
	private boolean optionalExist;
	private int linkId;
	private SupplierTypes mainSupplier;
	private ProductTypes type;
	private boolean composite; // комплект
	private Set<Product> kitComponents;
	private boolean visible; // видимость (да, нет)
	private ProductStore store;	// фронт
	private Set<ProductMarket> markets;

	public Product(int id, String name) {
		super(id, name);
		this.quantity = 1;
		this.stockQuantity = 0;
		this.slaveQuantity = -1;
		this.supplierPrice = BigDecimal.ZERO;
		this.price = BigDecimal.ZERO;

		this.priceWithoutDiscount = BigDecimal.ZERO;
		this.priceWithDiscount = BigDecimal.ZERO;

		this.model = "";
		this.sku = "";
		this.optionalExist = false;
		this.linkId = -1;
		this.mainSupplier = null;
		this.composite = false;
		this.kitComponents = new HashSet<Product>();
		this.deliveryMethod = PaymentDeliveryMethods.CURRENT;
		this.type = ProductTypes.MAIN;
		this.visible = true;
		this.store = new ProductStore();

		this.markets = new HashSet<ProductMarket>();

		ProductMarket ozon = new ProductMarket(CrmTypes.OZON);
		this.markets.add(ozon);
		ProductMarket yandex = new ProductMarket(CrmTypes.YANDEX_MARKET);
		this.markets.add(yandex);
	}

	public Product() {
		this(0, "");
	}

	public String getDeliveryName() {
		return deliveryName;
	}

	public void setDeliveryName(String deliveryName) {
		this.deliveryName = deliveryName;
	}

	public boolean isComposite() {
		return composite;
	}

	public void setComposite(boolean composite) {
		this.composite = composite;
	}

	public Set<Product> getKitComponents() {
		return kitComponents;
	}

	public void setKitComponents(Set<Product> kitComponents) {
		this.kitComponents = kitComponents;
	}

	public String getViewSKU() {
		String result = "";
		if (StringUtils.isEmpty(this.sku)) {
			result = StringUtils.truncate(this.model.trim(), 3);
		} else {
			result = this.sku;
		}
		return StringUtils.upperCase(result);
	}

	public boolean isMarketSeller() {
		if (getMarkets() == null) {
			return false;
		}
		return (getMarket(CrmTypes.YANDEX_MARKET).isMarketSeller() || getMarket(CrmTypes.OZON).isMarketSeller());
	}

	public ProductTypes getType() {
		return type;
	}

	public void setType(ProductTypes type) {
		this.type = type;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public boolean isSKUVisible() {
		if (this.type == ProductTypes.MAIN) {
			return visible;
		} else if (this.type == ProductTypes.ADDITIONAL) {
			return true;
		} else {
			return visible;
		}
	}

	public String getViewName() {
		final int MAX_VIEW_LENGTH = 139;
		String result = "";
		if (StringUtils.isNotEmpty(this.sku)) {
			result = "[" + StringUtils.upperCase(this.sku) + "] " + this.getName();
		} else {
			result = this.getName();
		}
		int index = Math.min(result.length(), MAX_VIEW_LENGTH);
		if (index < result.length()) {
			return result.substring(0, index) + "...";
		} else {
			return result.substring(0, index);
		}
	}

	public String getViewNameShort() {
		if (getName() != null) {
			final int MAX_VIEW_LENGTH = 120;
			int index = Math.min(getName().length(), MAX_VIEW_LENGTH);
			if (index < getName().length()) {
				return getName().substring(0, index) + "...";
			} else {
				return getName().substring(0, index);
			}
		}
		return null;
	}

	public String getViewModelShort() {
		if (getModel() != null) {
			final int MAX_VIEW_LENGTH = 45;
			int index = Math.min(getModel().length(), MAX_VIEW_LENGTH);
			if (index < getModel().length()) {
				return getModel().substring(0, index) + "...";
			} else {
				return getModel().substring(0, index);
			}
		}
		return null;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public String getModel() {
		return model;
	}

	public String getSku() {
		return sku;
	}

	public void setSku(String sku) {
		this.sku = sku;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public int getQuantity() {
		return quantity;
	}

	public int getSupplierQuantity() {
		return supplierQuantity;
	}

	public void setSupplierQuantity(int supplierQuantity) {
		this.supplierQuantity = supplierQuantity;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public BigDecimal getPriceWithoutDiscount() {
		return priceWithoutDiscount;
	}

	public void setPriceWithoutDiscount(BigDecimal priceWithoutDiscount) {
		this.priceWithoutDiscount = priceWithoutDiscount;
	}

	public BigDecimal getPriceWithDiscount() {
		return priceWithDiscount;
	}

	public void setPriceWithDiscount(BigDecimal priceWithDiscount) {
		this.priceWithDiscount = priceWithDiscount;
	}

	public BigDecimal getSupplierPrice() {
		return supplierPrice;
	}

	public void setSupplierPrice(BigDecimal supplierPrice) {
		this.supplierPrice = supplierPrice;
	}

	public int getStockQuantity() {
		return stockQuantity;
	}

	public void setStockQuantity(int stockQuantity) {
		this.stockQuantity = stockQuantity;
	}

	public int getSlaveQuantity() {
		return slaveQuantity;
	}

	public void setSlaveQuantity(int slaveQuantity) {
		this.slaveQuantity = slaveQuantity;
	}

	public int getCompositeStockQuantity() {
		if (this.isComposite()) {
			int result = -1;
			for (Product slave : this.kitComponents) {
				if (result == -1) {
					result = slave.getStockQuantity();
				} else {
					int quantity = slave.getStockQuantity() / slave.getSlaveQuantity();
					result = Math.min(result, quantity);
				}
			}
			return result;
		} else {
			return this.getStockQuantity();
		}
	}

	public ProductCategory getCategory() {
		return category;
	}

	public void setCategory(ProductCategory category) {
		this.category = category;
	}

	public PaymentDeliveryMethods getDeliveryMethod() {
		return deliveryMethod;
	}

	public void setDeliveryMethod(PaymentDeliveryMethods deliveryMethod) {
		this.deliveryMethod = deliveryMethod;
	}

	public int getLinkId() {
		return linkId;
	}

	public void setLinkId(int linkId) {
		this.linkId = linkId;
	}

	public boolean isOptionalExist() {
		return optionalExist;
	}

	public void setOptionalExist(boolean optionalExist) {
		this.optionalExist = optionalExist;
	}

	public SupplierTypes getMainSupplier() {
		return mainSupplier;
	}

	public void setMainSupplier(SupplierTypes mainSupplier) {
		this.mainSupplier = mainSupplier;
	}

	public ProductStore getStore() {
		return store;
	}

	public void setStore(ProductStore value) {
		this.store = value;
	}
	/*
	public ProductYandexMarket getYm() {
		return ym;
	}

	public void setYm(ProductYandexMarket value) {
		this.ym = value;
	}
	*/

	public Set<ProductMarket> getMarkets() {
		return markets;
	}

	public void setMarkets(Set<ProductMarket> markets) {
		this.markets = markets;
	}

	public ProductMarket getMarket(CrmTypes marketType) {
		if (markets == null || markets.size() == 0) {
			return null;
		}
		for (ProductMarket productMarket : markets) {
			if (productMarket.getMarketType() == marketType) {
				return productMarket;
			}
		}
		return null;
	}

	public String getViewStockQuantityText() {

		String weightText = "";
		String supplierAnnotation = "";
		String ymExist = "";

		if (this.getCompositeStockQuantity() >= 1) {
			supplierAnnotation = "";
		} else {
			supplierAnnotation = ", уточнить наличие";
		}

		if (this.getMarket(CrmTypes.YANDEX_MARKET).isMarketSeller()) {
			ymExist = "размещен на \"ЯНДЕКС МАРКЕТ\", ";
		}

		if (this.getStore().getWeight().compareTo(BigDecimal.ONE) < 0) {
			weightText = "";
		} else {
			weightText = ", вес " + NumberUtils.formatNumber(this.getStore().getWeight(), "#,##0") + " кг";
		}

		if (this.getId() <= 0) {
			return "";
		} else if (this.getMainSupplier() == null) {
			return "На складе: 0 ("+ ymExist + "на сайте: " + this.getQuantity() + ", уточнить наличие" + weightText + ")";
		} else if (this.getMainSupplier() == SupplierTypes.SITITEK) {
			return "На складе: " + this.getCompositeStockQuantity() + " (" + ymExist + "всего: " + this.getQuantity() + ", поставщик " + this.getMainSupplier().getAnnotation() + ": " + this.getSupplierQuantity() + weightText + ")";
		} else if (this.getMainSupplier() == SupplierTypes.LADIA) {
			return "На складе: " + this.getCompositeStockQuantity() + " (" + ymExist + "поставщик " + this.getMainSupplier().getAnnotation() + ", на сайте: " + this.getQuantity() + weightText + ")";
		} else {
			if (this.getQuantity() <= 0) {
				return "На складе: " + this.getCompositeStockQuantity() + " (" + ymExist + "поставщик " + this.getMainSupplier().getAnnotation() + ", на сайте: \"нет в наличии\")";
			} else {
				return "На складе: " + this.getCompositeStockQuantity() + " (" + ymExist + "поставщик " + this.getMainSupplier().getAnnotation() + ", на сайте: " + this.getQuantity() + supplierAnnotation + weightText + ")";
			}
		}
	}

	public String getViewStockQuantityClass() {
		if (this.getId() <= 0) {
			return "light";
		} else if (this.getMainSupplier() == null) {
			return "danger";
		} else if (this.getMainSupplier() == SupplierTypes.SITITEK) {
			if (this.getCompositeStockQuantity() <= 0 && this.getQuantity() <= 0) {
				return "danger";
			} else if (this.getCompositeStockQuantity() > 0 && this.getQuantity() <= 0) {
				return "warning";
			} else if (this.getCompositeStockQuantity() <= 0 && this.getQuantity() > 0) {
				return "warning";
			} else {
				return "light";
			}
		} else {
			if (this.getCompositeStockQuantity() <= 0 && this.getQuantity() <= 0) {
				return "danger";
			} else if (this.getQuantity() <= 0) {
				return "warning";
			} else if (this.getCompositeStockQuantity() > 0) {
				return "light";
			} else {
				return "warning";
			}
		}
	}

	@Override
	public Product clone() throws CloneNotSupportedException  {
		Product clone = (Product) super.clone();
		clone.model = this.model == null ? null : new String(this.model);
		clone.sku = this.sku == null ? null : new String(this.sku);
		clone.quantity = this.quantity;
		clone.price = this.price == null ? null : new BigDecimal(this.price.toString());
		clone.supplierPrice = this.supplierPrice == null ? null : new BigDecimal(this.supplierPrice.toString());
		clone.supplierQuantity = this.supplierQuantity;

		clone.stockQuantity = this.stockQuantity;
		clone.category = this.category == null ? null : this.category;
		clone.optionalExist = this.optionalExist;
		clone.linkId = this.linkId;
		/*
		clone.getStore().getWeight() = this.getStore().getWeight() == null ? null : new BigDecimal(this.weight.toString());
		clone.height = this.height;
		clone.length = this.length;
		clone.width = this.width;
		*/
		return clone;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((category == null) ? 0 : category.hashCode());
		result = prime * result + (composite ? 1231 : 1237);
		result = prime * result + ((deliveryMethod == null) ? 0 : deliveryMethod.hashCode());


		result = prime * result + ((kitComponents == null) ? 0 : kitComponents.hashCode());

		result = prime * result + linkId;
		result = prime * result + ((mainSupplier == null) ? 0 : mainSupplier.hashCode());
		result = prime * result + ((model == null) ? 0 : model.hashCode());
		result = prime * result + (optionalExist ? 1231 : 1237);

		result = prime * result + ((price == null) ? 0 : price.hashCode());
		result = prime * result + quantity;
		result = prime * result + ((sku == null) ? 0 : sku.hashCode());
		result = prime * result + slaveQuantity;
		result = prime * result + stockQuantity;
		result = prime * result + ((store == null) ? 0 : store.hashCode());
		result = prime * result + ((supplierPrice == null) ? 0 : supplierPrice.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());

		result = prime * result + (visible ? 1231 : 1237);

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
		Product other = (Product) obj;
		if (category == null) {
			if (other.category != null)
				return false;
		} else if (!category.equals(other.category))
			return false;
		if (composite != other.composite)
			return false;
		if (deliveryMethod != other.deliveryMethod)
			return false;
		if (kitComponents == null) {
			if (other.kitComponents != null)
				return false;
		} else if (!kitComponents.equals(other.kitComponents))
			return false;
		if (linkId != other.linkId)
			return false;
		if (mainSupplier != other.mainSupplier)
			return false;
		if (model == null) {
			if (other.model != null)
				return false;
		} else if (!model.equals(other.model))
			return false;
		if (optionalExist != other.optionalExist)
			return false;
		if (price == null) {
			if (other.price != null)
				return false;
		} else if (!price.equals(other.price))
			return false;
		if (quantity != other.quantity)
			return false;
		if (sku == null) {
			if (other.sku != null)
				return false;
		} else if (!sku.equals(other.sku))
			return false;
		if (slaveQuantity != other.slaveQuantity)
			return false;
		if (stockQuantity != other.stockQuantity)
			return false;
		if (store == null) {
			if (other.store != null)
				return false;
		} else if (!store.equals(other.store))
			return false;
		if (supplierPrice == null) {
			if (other.supplierPrice != null)
				return false;
		} else if (!supplierPrice.equals(other.supplierPrice))
			return false;
		if (type != other.type)
			return false;

		if (visible != other.visible)
			return false;

		return true;
	}

	@Override
	public String toString() {
		return "Product [id=" + getId() + ", name=" + getName()
				//+ ", model=" + model
				+ ", sku=" + sku
				//+ ", quantity=" + quantity
				+ ", price=" + price
				//+ ", stockQuantity: " + stockQuantity + ", supplierPrice: " + supplierPrice
				//+ ", category=" + category
				+ "]";
	}

	public static Product createEmpty() {
		Product result = new Product();
		result.setModel("?");
		result.setName("");
		return result;
	}
}
