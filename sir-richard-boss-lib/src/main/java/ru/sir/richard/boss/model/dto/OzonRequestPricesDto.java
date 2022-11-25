package ru.sir.richard.boss.model.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class OzonRequestPricesDto {
	
	@JsonProperty("prices")
	List<OzonRequestPriceDto> ozonRequestPriceDtos;

}
