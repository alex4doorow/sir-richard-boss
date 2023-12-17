package ru.sir.richard.boss.data.raw;

import ru.sir.richard.boss.model.data.AnyCatalog;

public class ManufacturerDataRaw extends AnyCatalog {
	
	private String image;
	private int sortOrder;
		
	public ManufacturerDataRaw() {
		super();
	}
	public ManufacturerDataRaw(int id, String name) {
		super(id, name);
	}
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}
	public int getSortOrder() {
		return sortOrder;
	}
	public void setSortOrder(int sortOrder) {
		this.sortOrder = sortOrder;
	}
	
}
