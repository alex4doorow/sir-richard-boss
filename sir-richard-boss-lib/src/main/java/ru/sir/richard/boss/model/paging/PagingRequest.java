package ru.sir.richard.boss.model.paging;

import lombok.Data;

import java.util.List;

@Data
public class PagingRequest {

    private int start;
    private int length;
    private int draw;
    private List<SortingOrder> data;
    private List<Column> columns;
    private Search search;
	public int getStart() {
		return start;
	}
	public void setStart(int start) {
		this.start = start;
	}
	public int getLength() {
		return length;
	}
	public void setLength(int length) {
		this.length = length;
	}
	public int getDraw() {
		return draw;
	}
	public void setDraw(int draw) {
		this.draw = draw;
	}
	public List<SortingOrder> getData() {
		return data;
	}
	public void setData(List<SortingOrder> data) {
		this.data = data;
	}
	public List<Column> getColumns() {
		return columns;
	}
	public void setColumns(List<Column> columns) {
		this.columns = columns;
	}
	public Search getSearch() {
		return search;
	}
	public void setSearch(Search search) {
		this.search = search;
	}

}
