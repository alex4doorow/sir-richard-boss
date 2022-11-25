package ru.sir.richard.boss.model.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
@JsonIgnoreProperties
public class OzonResponseStockDto {
	@JsonProperty("offer_id")
	private String offerId;
	@JsonProperty("product_id")
	private Long productId;
	@JsonProperty("warehouse_id")
	private Long warehouseId;
	private boolean updated;
	private List<OzonResponseErrorDto> errors;
}
