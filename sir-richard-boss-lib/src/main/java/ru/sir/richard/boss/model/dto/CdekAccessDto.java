package ru.sir.richard.boss.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CdekAccessDto {
    @JsonProperty("access_token")
    String accessToken;
    @JsonProperty("token_type")
    String tokenType;
    @JsonProperty("expires_in")
    String expiresIn;
    String scope;
    String jti;
    
    public String getSecret() {
    	return "Bearer " + getAccessToken();
    }
}
