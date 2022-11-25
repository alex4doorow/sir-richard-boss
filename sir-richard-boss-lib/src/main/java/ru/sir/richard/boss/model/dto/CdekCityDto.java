package ru.sir.richard.boss.model.dto;

import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@JsonIgnoreProperties
@Data
public class CdekCityDto {
    Integer code;
    String city;
    @JsonProperty("country_code")
    String countryCode;
    String country;
    String region;
    @JsonProperty("region_code")
    Integer regionCode;
    @JsonProperty("sub_region")
    String subRegion;
    @JsonProperty("postal_codes")
    List<String> postalCodes;
    BigDecimal longitude;
    BigDecimal latitude;
    String timeZone;      
}
