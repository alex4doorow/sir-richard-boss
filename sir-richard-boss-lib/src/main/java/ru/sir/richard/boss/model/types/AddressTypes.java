package ru.sir.richard.boss.model.types;

public enum AddressTypes {
		
	MAIN(1, "основной"),
	ADDITIONAL(2, "дополнительный"),
	UNKNOWN(3, "неизвестный");

	private int id;
    private String annotation;

    AddressTypes(int id, String annotation) {
    	this.id = id;
        this.annotation = annotation;
    }

    public String getAnnotation() {
        return annotation;
    }

	public int getId() {
		return id;
	}
	
	public static AddressTypes getValueById(int value) {
		if (value == 1) {
			return AddressTypes.MAIN;
		} else if (value == 2) {
			return AddressTypes.ADDITIONAL;
		} else {
			return AddressTypes.UNKNOWN;
		}
		
	}
    

}
