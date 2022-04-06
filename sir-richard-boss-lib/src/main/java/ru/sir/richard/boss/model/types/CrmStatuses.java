package ru.sir.richard.boss.model.types;

public enum CrmStatuses {
	
	NONE(0, "нет обработки"),
	SUCCESS(1, "успешно"),	
	FAIL(2, "ошибки");
		
	private int id;
    private String annotation;
	
    CrmStatuses(int id, String annotation) {
    	this.id = id;
		this.annotation = annotation;
	}
        
    public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getAnnotation() {
		return annotation;
	}

	public void setAnnotation(String annotation) {
		this.annotation = annotation;
	}
	
	public static CrmStatuses getValueById(int value) {
		if (value == 1) {
			return CrmStatuses.SUCCESS;
		} else if (value == 2) {
			return CrmStatuses.FAIL;			
		} else {
			return CrmStatuses.NONE;
		}		
	}
}
