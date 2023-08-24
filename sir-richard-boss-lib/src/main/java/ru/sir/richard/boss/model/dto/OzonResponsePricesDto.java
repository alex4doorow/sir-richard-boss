package ru.sir.richard.boss.model.dto;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class OzonResponsePricesDto extends OzonResponseDto {
	
	@JsonProperty("result")
	List<OzonResponseStockDto> ozonRequestPricesDtos;
	
	public List<OzonResponseErrorDto> getErrors()
	{
		List<OzonResponseErrorDto> errors = new ArrayList<>();
		ozonRequestPricesDtos.forEach(s -> {
			errors.addAll(s.getErrors());
		});
		return errors;
	}
}
