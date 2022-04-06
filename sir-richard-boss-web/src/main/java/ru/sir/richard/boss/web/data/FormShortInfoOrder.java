package ru.sir.richard.boss.web.data;

import ru.sir.richard.boss.model.data.AnyCatalog;

public class FormShortInfoOrder extends AnyCatalog {
	
	private FormOrder source;
	
	public FormShortInfoOrder(FormOrder source) {
		this.source = source;
		this.setId(source.getId());
		this.setName(source.getViewShortInfo());
	}

	protected FormOrder getSource() {
		return source;
	}	
	
}
