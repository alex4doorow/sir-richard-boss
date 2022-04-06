package ru.sir.richard.boss.model.data;

public abstract class AnyId implements Cloneable {
		
	private int id;
	
	public AnyId() {
		super();
		clear();
	}
	
	public AnyId(int id) {
		this();
		this.id = id;
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public boolean isNew() {
		return (this.id == 0);
	}
	
	@Override
	public AnyId clone() throws CloneNotSupportedException  {		
		AnyId clone = (AnyId) super.clone();
		clone.id = this.id;
		return clone;
	}
	
	protected void clear() {
		//
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
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
		AnyId other = (AnyId) obj;
		if (id != other.id)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "AnyId [id=" + id + "]";
	}	

}
