package ru.sir.richard.boss.model.types;

public enum ReportQueryNames {
	
	YOOKASSA_PREPAYMENT(1, "Предоплаты, по банковской карте"),
	OZON_ROCKET_POSTPAYMENT(2, "Постоплаты, OZON.Rocket"),
	CDEK_POSTPAYMENT(3, "Постоплаты, СДЭК"),
	YANDEX_MARKET(4, "Яндекс.Маркет"),
	OZON(5, "OZON");
	
	private int id;
	private String annotation;
	
	private ReportQueryNames(int id, String annotation) {
		this.id = id;
		this.annotation = annotation;
	}
		
	public int getId() {
		return id;
	}
	
	public String getAnnotation() {
		return annotation;
	}
	
	public static ReportQueryNames getValueById(int value) {
		if (value == 1) {
			return ReportQueryNames.YOOKASSA_PREPAYMENT;
		} else if (value == 2) {
			return ReportQueryNames.OZON_ROCKET_POSTPAYMENT;			
		} else if (value == 3) {
			return ReportQueryNames.CDEK_POSTPAYMENT;
		} else if (value == 4) {
			return ReportQueryNames.YANDEX_MARKET;
		} else if (value == 5) {
			return ReportQueryNames.OZON;
		} else {
			return ReportQueryNames.YOOKASSA_PREPAYMENT;
		} 	
	}
}
