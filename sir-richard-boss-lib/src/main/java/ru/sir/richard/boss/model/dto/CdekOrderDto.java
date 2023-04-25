package ru.sir.richard.boss.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

@Data
public class CdekOrderDto {
	private CdekEntityOrderDto entity;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private List<CdekEntityOrderRequestDto> requests;
}