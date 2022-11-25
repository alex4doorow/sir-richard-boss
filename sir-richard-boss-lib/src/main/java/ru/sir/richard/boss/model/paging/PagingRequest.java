package ru.sir.richard.boss.model.paging;

import java.util.List;

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
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((columns == null) ? 0 : columns.hashCode());
		result = prime * result + ((data == null) ? 0 : data.hashCode());
		result = prime * result + draw;
		result = prime * result + length;
		result = prime * result + ((search == null) ? 0 : search.hashCode());
		result = prime * result + start;
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PagingRequest other = (PagingRequest) obj;
		if (columns == null) {
			if (other.columns != null)
				return false;
		} else if (!columns.equals(other.columns))
			return false;
		if (data == null) {
			if (other.data != null)
				return false;
		} else if (!data.equals(other.data))
			return false;
		if (draw != other.draw)
			return false;
		if (length != other.length)
			return false;
		if (search == null) {
			if (other.search != null)
				return false;
		} else if (!search.equals(other.search))
			return false;
		if (start != other.start)
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "PagingRequest [start=" + start + ", length=" + length + ", draw=" + draw + ", data=" + data
				+ ", columns=" + columns + ", search=" + search + "]";
	}
    
    

}
