package ru.sir.richard.boss.model.types;

import lombok.Getter;

@Getter
public enum AddressTypes {
		
	MAIN(1, "основной"),
	ADDITIONAL(2, "дополнительный"),
	UNKNOWN(3, "неизвестный");

	private final int id;
    private final String annotation;

    AddressTypes(int id, String annotation) {
    	this.id = id;
        this.annotation = annotation;
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
