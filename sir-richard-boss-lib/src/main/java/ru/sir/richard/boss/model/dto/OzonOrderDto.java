package ru.sir.richard.boss.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@JsonIgnoreProperties
@Data
public class OzonOrderDto {

    @JsonProperty("result")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private OzonOrderMainPartDto result;
           
}



