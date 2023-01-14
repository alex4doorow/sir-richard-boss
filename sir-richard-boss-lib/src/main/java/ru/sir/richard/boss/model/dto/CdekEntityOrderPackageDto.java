package ru.sir.richard.boss.model.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class CdekEntityOrderPackageDto {
	private String number;
    private int weight;
    private int length;
    private int width;
    private int height;
    @JsonProperty("weight_volume")
    private int weightVolume;
    @JsonProperty("weight_calc")
    private int weightCalc;
    private String comment;
    private List<CdekEntityPackageItemDto> items;
}
