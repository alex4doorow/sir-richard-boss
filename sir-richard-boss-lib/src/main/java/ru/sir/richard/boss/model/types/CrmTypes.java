package ru.sir.richard.boss.model.types;

public enum CrmTypes {
	
	UNKNOWN(0, "нет"),
	OPENCART(1, "Админка сайта Opencart"),
	EMAIL(2, "Быстрй заказ"),
	CDEK(3, "СДЭК Маркет"),
	YANDEX_MARKET(4, "Яндекс Маркет"),
	OZON(5, "ОЗОН Маркет");
		
	private int id;
    private String annotation;
	
    CrmTypes(int id, String annotation) {
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

	public static CrmTypes getValueById(int value) {
		if (value == 1) {
			return CrmTypes.OPENCART;
		} else if (value == 2) {
			return CrmTypes.EMAIL;			
		} else if (value == 3) {
			return CrmTypes.CDEK;
		} else if (value == 4) {
			return CrmTypes.YANDEX_MARKET;			
		} else if (value == 5) {
			return CrmTypes.OZON;			
		} else {
			return CrmTypes.UNKNOWN;
		}		
	}
}
