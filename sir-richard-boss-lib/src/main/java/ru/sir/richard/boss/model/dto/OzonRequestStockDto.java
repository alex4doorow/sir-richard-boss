package ru.sir.richard.boss.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@JsonIgnoreProperties
@Data
public class OzonRequestStockDto {
	@JsonProperty("offer_id")
	private String offerId;
	@JsonProperty("product_id")
	private Long productId;
	private int stock;
	@JsonProperty("warehouse_id")
	private Long warehouseId;
}
