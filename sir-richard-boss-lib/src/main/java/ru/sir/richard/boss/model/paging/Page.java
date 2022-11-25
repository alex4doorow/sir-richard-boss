package ru.sir.richard.boss.model.paging;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonView;

public class Page<T> {

	@JsonView(PageViews.Public.class)
	List<T> data;
	
	@JsonView(PageViews.Public.class)
	int recordsFiltered;
	
	@JsonView(PageViews.Public.class)
	int recordsTotal;
	
	@JsonView(PageViews.Public.class)
	int draw;

	public Page(List<T> data) {
		this.data = data;
	}

	public List<T> getData() {
		return data;
	}

	public void setData(List<T> data) {
		this.data = data;
	}

	public int getRecordsFiltered() {
		return recordsFiltered;
	}

	public void setRecordsFiltered(int recordsFiltered) {
		this.recordsFiltered = recordsFiltered;
	}

	public int getRecordsTotal() {
		return recordsTotal;
	}

	public void setRecordsTotal(int recordsTotal) {
		this.recordsTotal = recordsTotal;
	}

	public int getDraw() {
		return draw;
	}

	public void setDraw(int draw) {
		this.draw = draw;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((data == null) ? 0 : data.hashCode());
		result = prime * result + draw;
		result = prime * result + recordsFiltered;
		result = prime * result + recordsTotal;
		return result;
	}


	@Override
	public String toString() {
		return "Page [data=" + data + ", recordsFiltered=" + recordsFiltered + ", recordsTotal=" + recordsTotal
				+ ", draw=" + draw + "]";
	}
	
}
