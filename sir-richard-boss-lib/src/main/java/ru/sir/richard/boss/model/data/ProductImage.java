package ru.sir.richard.boss.model.data;

public class ProductImage extends AnyId {
	
	private String image;
	private int SortOrder;

	public ProductImage() {
		super();
	}

	public ProductImage(int id) {
		super(id);
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public int getSortOrder() {
		return SortOrder;
	}

	public void setSortOrder(int sortOrder) {
		SortOrder = sortOrder;
	}

}
