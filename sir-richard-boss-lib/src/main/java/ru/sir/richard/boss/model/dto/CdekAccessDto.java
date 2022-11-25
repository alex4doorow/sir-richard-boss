package ru.sir.richard.boss.model.dto;

import lombok.Data;

@Data
public class CdekAccessDto {
    String access_token;
    String token_type;
    String expires_in;
    String scope;
    String jti;
    
    public String getSecret() {
    	return "Bearer " + getAccess_token();
    }
}
