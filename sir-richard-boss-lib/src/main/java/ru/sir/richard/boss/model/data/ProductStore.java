package ru.sir.richard.boss.model.data;

import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Данные для фронта
 * @author alex4doorow
 *
 */
@Data
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
	
	/**
	 * JAN - вариант доставки 
	 * 		0, 102 - текущий,
	 * 		101 - полный
	 * 		103 - бесплатная доставка
	 * ISBN 
	 * 		101 - дополнительный продукт
	 * 		- обычный 
	 * MPN
	 * 		104 - цена по запросу
	 * 
	 */
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

	@Override
	public ProductStore clone() throws CloneNotSupportedException  {		
		ProductStore clone = (ProductStore) super.clone();
		// ...
		return clone;
	}

}
