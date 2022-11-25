package ru.sir.richard.boss.model.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class OzonResponseStocksDto extends OzonResponseDto {
	
	@JsonProperty("result")
	List<OzonResponseStockDto> ozonRequestStockDtos;
}
