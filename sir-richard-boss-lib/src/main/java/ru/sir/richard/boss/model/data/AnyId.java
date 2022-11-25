package ru.sir.richard.boss.model.data;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode
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
	public String toString() {
		return "AnyId [id=" + id + "]";
	}	

}
