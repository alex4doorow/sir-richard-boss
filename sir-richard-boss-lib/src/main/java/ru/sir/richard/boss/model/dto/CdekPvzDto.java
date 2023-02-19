package ru.sir.richard.boss.model.dto;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import ru.sir.richard.boss.utils.TextUtils;
@Data
public class CdekPvzDto {
	private String code;
	private String name;	
	@JsonProperty("address_comment")
	private String addressComment;
	@JsonProperty("nearest_station")
	private String nearestStation;
	@JsonProperty("work_time")
	private String workTime;
	private List<CdekPhoneDto> phones;
	private String email;
	private String note;
	private String type;
	@JsonProperty("take_only")
    private Boolean takeOnly;
    @JsonProperty("is_handout")
    private Boolean handout;
    @JsonProperty("is_reception")
    private Boolean reception;
    @JsonProperty("is_dressing_room")
    private Boolean dressingRoom;
    @JsonProperty("have_cashless")
    private Boolean haveCashless;
    @JsonProperty("have_cash")
    private Boolean haveCash;
    @JsonProperty("allowed_cod")
    private Boolean allowedCode;
    private CdekPvzLocationDto location;    
    @JsonProperty("weight_min")
    private Double weightMin;
    @JsonProperty("weight_max")
    private Double weightMax;
    
    public Integer getId() {
    	String number = TextUtils.numberDigit(this.getCode());
    	if (StringUtils.isEmpty(number)) {
    		return 0;
    	}
    	return Integer.valueOf(number);
    }
    
    public String getSinglePhone() {
    	if (phones == null || phones.size() == 0) {
    		return "";
    	}
    	return phones.get(0).getNumber();
    }
}
