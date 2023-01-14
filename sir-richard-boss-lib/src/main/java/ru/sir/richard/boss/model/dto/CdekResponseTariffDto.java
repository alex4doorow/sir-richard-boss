package ru.sir.richard.boss.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class CdekResponseTariffDto {
    @JsonProperty("delivery_sum")
    private BigDecimal deliverySum;
    @JsonProperty("period_min")
    private Integer periodMin;
    @JsonProperty("period_max")
    private Integer periodMax;
    @JsonProperty("calendar_min")
    private Integer calendarMin;
    @JsonProperty("calendar_max")
    private Integer calendarMax;
    @JsonProperty("weight_calc")
    private Integer weightCalc;
    @JsonInclude(JsonInclude.Include.NON_NULL)
	private List<CdekEntityServiceOrderDto> services;
    @JsonProperty("total_sum")
    private BigDecimal totalSum;
    private String currency;
}
