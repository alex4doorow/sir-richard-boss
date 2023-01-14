package ru.sir.richard.boss.model.dto;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class CdekEntityOrderLocationDto {
	private Integer code;
	private String city;
	@JsonProperty("country_code")
	private String countryCode;	
	private String country;
	private String region;
	@JsonProperty("region_code")
	private String regionCode;
	private BigDecimal longitude;
	private BigDecimal latitude;
	private String address;
}	
