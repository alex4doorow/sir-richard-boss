package ru.sir.richard.boss.integration.impl.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ErrorResponse {
    public ErrorResponse(final String errorCode, final String info) {
        this.errorCode = errorCode;
        //TODO: this.description= Locale.getBundle(errorCode, EN);
        this.info = info;
    }

    private String errorCode;
    private String description;
    private String info;

}
