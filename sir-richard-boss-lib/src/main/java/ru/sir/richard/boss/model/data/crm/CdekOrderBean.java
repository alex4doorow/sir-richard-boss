package ru.sir.richard.boss.model.data.crm;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import ru.sir.richard.boss.model.data.AnyId;
import ru.sir.richard.boss.model.data.Order;
import ru.sir.richard.boss.model.types.DeliveryTypes;

public class CdekOrderBean extends AnyId {
	
	// Номер отправления	Город получателя	Индекс	Получатель	ФИО получателя	Адрес получателя	Код ПВЗ	Телефон получателя	Доп сбор за доставку с получателя в т.ч. НДС	Ставка НДС с доп.сбора за доставку	Сумма НДС с доп.сбора за доставку	
	// Истинный продавец	Комментарий	Порядковый номер места	Вес места, кг	Длина места, см	Ширина места, см	Высота места, см	Описание места	Код товара/артикул	Наименование товара	Стоимость единицы товара	Оплата с получателя за ед товара в т.ч. НДС	Вес товара, кг	Количество, шт	Ставка НДС, %	Сумма НДС за ед.
	// 5428	Ставрополь		Рощупко Ю. В.	Рощупко Ю. В.	ул. Мира, 278д	STV6	(918) 888-11-28	250.0000			
	// ИП Федоров А.А.		1	0,5	10	10	10	оборудование	Ста	Отпугиватель собак, кошек, лис, кроликов стационарный "Weitech WK-0055"	3990.0000	3990.0000	0,01	1	0	0
	
	private final Order order;
	
	private int no;
	
	private String city;
	private int cityId;
	private String postCode;	
	private String pvz;
	
	private String street;
	private String house;
	private String flat;
		
	private String recipient;
	private String recipientPerson;
	
	private String recipientAddress;
	
	private String recipientPhone;
	private String recipientEmail;
	
	private DeliveryTypes deliveryType;
	private BigDecimal deliveryPay;
	
	private String seller;
	private String deliveryAnnotation;
	
	private String productCategory;
	
	private String productSku;
	private String productName;
	private BigDecimal productPrice;
	private BigDecimal productPay;
	private BigDecimal productWeight;
	private int productQuantity;
	
	private List<CdekOrderItemBean> items;
	
	public CdekOrderBean(Order order) {
		this.order = order;
		this.items = new ArrayList<CdekOrderItemBean>();
	}

	public int getNo() {
		return no;
	}

	public void setNo(int no) {
		this.no = no;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public int getCityId() {
		return cityId;
	}

	public void setCityId(int cityId) {
		this.cityId = cityId;
	}

	public String getPostCode() {
		return postCode;
	}

	public void setPostCode(String postCode) {
		this.postCode = postCode;
	}

	public String getPvz() {
		return pvz;
	}

	public void setPvz(String pvz) {
		this.pvz = pvz;
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

	public String getRecipient() {
		return recipient;
	}

	public void setRecipient(String recipient) {
		this.recipient = recipient;
	}

	public String getRecipientPerson() {
		return recipientPerson;
	}

	public void setRecipientPerson(String recipientPerson) {
		this.recipientPerson = recipientPerson;
	}

	public String getRecipientAddress() {
		return recipientAddress;
	}

	public void setRecipientAddress(String recipientAddress) {
		this.recipientAddress = recipientAddress;
	}

	public String getRecipientPhone() {
		return recipientPhone;
	}

	public void setRecipientPhone(String recipientPhone) {
		this.recipientPhone = recipientPhone;
	}

	public String getRecipientEmail() {
		return recipientEmail;
	}

	public void setRecipientEmail(String recipientEmail) {
		this.recipientEmail = recipientEmail;
	}

	public DeliveryTypes getDeliveryType() {
		return deliveryType;
	}

	public void setDeliveryType(DeliveryTypes deliveryType) {
		this.deliveryType = deliveryType;
	}

	public BigDecimal getDeliveryPay() {
		return deliveryPay;
	}

	public void setDeliveryPay(BigDecimal deliveryPay) {
		this.deliveryPay = deliveryPay;
	}

	public String getSeller() {
		return seller;
	}

	public void setSeller(String seller) {
		this.seller = seller;
	}
	
	public String getDeliveryAnnotation() {
		return deliveryAnnotation;
	}

	public void setDeliveryAnnotation(String deliveryAnnotation) {
		this.deliveryAnnotation = deliveryAnnotation;
	}

	public String getProductCategory() {
		return productCategory;
	}

	public void setProductCategory(String productCategory) {
		this.productCategory = productCategory;
	}

	public String getProductSku() {
		return productSku;
	}

	public void setProductSku(String productSku) {
		this.productSku = productSku;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public BigDecimal getProductPrice() {
		return productPrice;
	}

	public void setProductPrice(BigDecimal productPrice) {
		this.productPrice = productPrice;
	}

	public BigDecimal getProductPay() {
		return productPay;
	}

	public void setProductPay(BigDecimal productPay) {
		this.productPay = productPay;
	}

	public BigDecimal getProductWeight() {
		return productWeight;
	}

	public void setProductWeight(BigDecimal productWeight) {
		this.productWeight = productWeight;
	}

	public int getProductQuantity() {
		return productQuantity;
	}

	public void setProductQuantity(int productQuantity) {
		this.productQuantity = productQuantity;
	}

	public Order getOrder() {
		return order;
	}

	public List<CdekOrderItemBean> getItems() {
		return items;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((deliveryAnnotation == null) ? 0 : deliveryAnnotation.hashCode());
		result = prime * result + ((city == null) ? 0 : city.hashCode());
		result = prime * result + ((deliveryPay == null) ? 0 : deliveryPay.hashCode());
		result = prime * result + no;
		result = prime * result + ((order == null) ? 0 : order.hashCode());
		result = prime * result + ((postCode == null) ? 0 : postCode.hashCode());
		result = prime * result + ((productName == null) ? 0 : productName.hashCode());
		result = prime * result + ((productPay == null) ? 0 : productPay.hashCode());
		result = prime * result + ((productPrice == null) ? 0 : productPrice.hashCode());
		result = prime * result + productQuantity;
		result = prime * result + ((productSku == null) ? 0 : productSku.hashCode());
		result = prime * result + ((productWeight == null) ? 0 : productWeight.hashCode());
		result = prime * result + ((pvz == null) ? 0 : pvz.hashCode());
		result = prime * result + ((recipient == null) ? 0 : recipient.hashCode());
		result = prime * result + ((recipientAddress == null) ? 0 : recipientAddress.hashCode());
		result = prime * result + ((recipientPerson == null) ? 0 : recipientPerson.hashCode());
		result = prime * result + ((recipientPhone == null) ? 0 : recipientPhone.hashCode());
		result = prime * result + ((seller == null) ? 0 : seller.hashCode());
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
		CdekOrderBean other = (CdekOrderBean) obj;
		if (deliveryAnnotation == null) {
			if (other.deliveryAnnotation != null)
				return false;
		} else if (!deliveryAnnotation.equals(other.deliveryAnnotation))
			return false;
		if (city == null) {
			if (other.city != null)
				return false;
		} else if (!city.equals(other.city))
			return false;
		if (deliveryPay == null) {
			if (other.deliveryPay != null)
				return false;
		} else if (!deliveryPay.equals(other.deliveryPay))
			return false;
		if (no != other.no)
			return false;
		if (order == null) {
			if (other.order != null)
				return false;
		} else if (!order.equals(other.order))
			return false;
		if (postCode == null) {
			if (other.postCode != null)
				return false;
		} else if (!postCode.equals(other.postCode))
			return false;
		if (productName == null) {
			if (other.productName != null)
				return false;
		} else if (!productName.equals(other.productName))
			return false;
		if (productPay == null) {
			if (other.productPay != null)
				return false;
		} else if (!productPay.equals(other.productPay))
			return false;
		if (productPrice == null) {
			if (other.productPrice != null)
				return false;
		} else if (!productPrice.equals(other.productPrice))
			return false;
		if (productQuantity != other.productQuantity)
			return false;
		if (productSku == null) {
			if (other.productSku != null)
				return false;
		} else if (!productSku.equals(other.productSku))
			return false;
		if (productWeight == null) {
			if (other.productWeight != null)
				return false;
		} else if (!productWeight.equals(other.productWeight))
			return false;
		if (pvz == null) {
			if (other.pvz != null)
				return false;
		} else if (!pvz.equals(other.pvz))
			return false;
		if (recipient == null) {
			if (other.recipient != null)
				return false;
		} else if (!recipient.equals(other.recipient))
			return false;
		if (recipientAddress == null) {
			if (other.recipientAddress != null)
				return false;
		} else if (!recipientAddress.equals(other.recipientAddress))
			return false;
		if (recipientPerson == null) {
			if (other.recipientPerson != null)
				return false;
		} else if (!recipientPerson.equals(other.recipientPerson))
			return false;
		if (recipientPhone == null) {
			if (other.recipientPhone != null)
				return false;
		} else if (!recipientPhone.equals(other.recipientPhone))
			return false;
		if (seller == null) {
			if (other.seller != null)
				return false;
		} else if (!seller.equals(other.seller))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "CdekCrmExportOrder [order=" + order + ", no=" + no + ", city=" + city + ", postCode=" + postCode
				+ ", pvz=" + pvz + ", recipient=" + recipient + ", recipientPerson=" + recipientPerson
				+ ", recipientAddress=" + recipientAddress + ", recipientPhone=" + recipientPhone + ", deliveryPay="
				+ deliveryPay + ", seller=" + seller + ", annotation=" + deliveryAnnotation + ", productSku=" + productSku
				+ ", productName=" + productName + ", productPrice=" + productPrice + ", productPay=" + productPay
				+ ", productWeight=" + productWeight + ", productQuantity=" + productQuantity + "]";
	}
}
