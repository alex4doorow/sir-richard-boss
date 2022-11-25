package ru.sir.richard.boss.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class OzonRequestPriceDto {
	
	@JsonProperty("offer_id")
	private String offerId;
	
	@JsonProperty("product_id")
	private Long productId;

	private String price;	
	
	@JsonProperty("old_price")
	private String oldPrice;
}
