package ru.sir.richard.boss.model.types;

public enum StoreTypes {
	
	/**
	 * [1] sir-richard.ru
	 */
	SR(1, "sir-richard.ru", "sir-richard@sir-richard.ru", "СЭР РИЧАРД РУ", "sr"),
	/**
	 * [2] pribormaster.ru
	 */
	PM(2, "pribormaster.ru", "info@pribormaster.ru", "ПРИБОРМАСТЕР", "pm");
		
	private int id;
	private String site;	
    private String annotation;
    private String email;
    private String preffix;
	
    StoreTypes(int id, String site, String email, String annotation, String preffix) {
    	this.id = id;
    	this.site = site;
    	this.email = email;
		this.annotation = annotation;	
		this.preffix = preffix;
	}
        
    public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getSite() {
		return site;
	}

	public void setSite(String site) {
		this.site = site;
	}

	public String getAnnotation() {
		return annotation;
	}

	public void setAnnotation(String annotation) {
		this.annotation = annotation;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPreffix() {
		return preffix;
	}

	public void setPreffix(String preffix) {
		this.preffix = preffix;
	}

	public static StoreTypes getValueById(int value) {
		if (value == 1) {
			return StoreTypes.SR;
		} else if (value == 2) {
			return StoreTypes.PM;			
		} else {
			return StoreTypes.PM;
		} 	
	}
	
	

}
