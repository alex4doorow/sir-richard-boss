package ru.sir.richard.boss.data.raw;

import ru.sir.richard.boss.model.data.AnyCatalog;

public class CategoryDataRaw extends AnyCatalog {
	
	private int mainCategory = 0;

	public CategoryDataRaw() {
		super();
	}

	public CategoryDataRaw(int id, String name) {
		super(id, name);
	}
		
	public int getMainCategory() {
		return mainCategory;
	}

	public void setMainCategory(int mainCategory) {
		this.mainCategory = mainCategory;
	}

	@Override
	public String toString() {
		return "CategoryDataRaw [id=" + getId() +", name=" + getName() + "]";
	}

}
