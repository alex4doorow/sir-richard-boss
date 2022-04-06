package ru.sir.richard.boss.model.types;

public enum OrderEmailStatuses {
	
	UNKNOWN(0, "неопределен"),
	FEEDBACK(1, "запрос отзыва"),
	TERM_EXPAIRED(2, "запрос на актуальность");
		
	private int id;
	private String annotation;
	
	OrderEmailStatuses(int id, String annotation) {
		this.id = id;
		this.annotation = annotation;
	}
	
	public String getAnnotation() {
		return annotation;
	}

	public int getId() {
		return id;
	}	
	
	public static OrderEmailStatuses getValueById(int value) {
		if (value == 1) {
			return OrderEmailStatuses.FEEDBACK;
		} else if (value == 2) {
			return OrderEmailStatuses.TERM_EXPAIRED;
		} else {
			return OrderEmailStatuses.UNKNOWN;
		}
	}

}
