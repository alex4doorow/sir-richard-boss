package ru.sir.richard.boss.model.paging;

public class SortingOrder {

    private Integer column;
    private Direction dir;
	public Integer getColumn() {
		return column;
	}
	public void setColumn(Integer column) {
		this.column = column;
	}
	public Direction getDir() {
		return dir;
	}
	public void setDir(Direction dir) {
		this.dir = dir;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((column == null) ? 0 : column.hashCode());
		result = prime * result + ((dir == null) ? 0 : dir.hashCode());
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
		SortingOrder other = (SortingOrder) obj;
		if (column == null) {
			if (other.column != null)
				return false;
		} else if (!column.equals(other.column))
			return false;
		if (dir != other.dir)
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "SortingOrder [column=" + column + ", dir=" + dir + "]";
	}
    
    

}
