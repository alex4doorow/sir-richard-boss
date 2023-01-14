package ru.sir.richard.boss.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CdekEntityServiceOrderDto {
	private String code;
	private String parameter;	
	private BigDecimal sum;

	public CdekEntityServiceOrderDto(String code) {
		this.code = code;
		this.parameter = "";
		this.sum = BigDecimal.ZERO;
	}
}
