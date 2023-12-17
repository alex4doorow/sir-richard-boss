package ru.sir.richard.boss.utils;

public class TestOrderType extends AnyTypes<TestOrderType> {
	
	private int id;
    private String annotation;
    
    	
    @Override
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Override
	public String getAnnotation() {
		return annotation;
	}

	public void setAnnotation(String annotation) {
		this.annotation = annotation;
	}

	@Override
	public TestOrderType getValueById(int value) {
		
		TestOrderType result = new TestOrderType();	
		
		if (value == 1) {
			result.setId(1);
			result.setAnnotation("BILL");	
		} else if (value == 2) {
			result.setId(2);
			result.setAnnotation("ORDER");
		} else if (value == 3) {
			result.setId(3);
			result.setAnnotation("TEST");
		}
		
		return result;

	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((annotation == null) ? 0 : annotation.hashCode());
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
		TestOrderType other = (TestOrderType) obj;
		if (annotation == null) {
			if (other.annotation != null)
				return false;
		} else if (!annotation.equals(other.annotation))
			return false;
		if (id != other.id)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "TestOrderType [id=" + id + ", annotation=" + annotation + "]";
	}

}
