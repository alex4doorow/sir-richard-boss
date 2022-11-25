package ru.sir.richard.boss.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class CdekPvzLocationDto {
	private String region;
	private String city;
	@JsonProperty("address")
	private String addressShort;
	@JsonProperty("address_full")
	private String addressFull;
}
