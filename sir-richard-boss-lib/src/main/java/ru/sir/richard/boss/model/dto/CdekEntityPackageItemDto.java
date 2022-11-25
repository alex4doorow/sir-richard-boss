package ru.sir.richard.boss.model.dto;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class CdekEntityPackageItemDto {
	private String name;
	@JsonProperty("ware_key")
	private String sku;
	private CdekEntityAmountDto payment;
	private int weight;
	@JsonProperty("weight_gross")
	private int weightGross;
	private int amount;
	@JsonProperty("delivery_amount")
	private int deliveryAmount;
	private BigDecimal cost;
}
