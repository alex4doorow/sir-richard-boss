package ru.sir.richard.boss.model.dto;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class CdekEntityAmountDto {
	private BigDecimal value;
	@JsonProperty("vat_sum")
	private BigDecimal vatSum;
}
