package ru.sir.richard.boss.model.types;

public enum CommentTypes {
		
	ORDER(1, "комментарий к заказу"),
	CONVERTER(2, "данные из конвертера"),
	UNKNOWN(3, "неизвестный");

	private int id;
    private String annotation;

    CommentTypes(int id, String annotation) {
    	this.id = id;
        this.annotation = annotation;
    }

    public String getAnnotation() {
        return annotation;
    }

	public int getId() {
		return id;
	}
	
	public static CommentTypes getValueById(int value) {
		if (value == 1) {
			return CommentTypes.ORDER;
		} else if (value == 2) {
			return CommentTypes.CONVERTER;
		} else {
			return CommentTypes.UNKNOWN;
		}		
	}
}

