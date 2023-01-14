package ru.sir.richard.boss.model.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class CdekResponseOrderDto {
    CdekResponseEntityOrderDto entity;
    List<CdekRequestEntityOrderDto> requests;

    public boolean isHaveErrors() {
        return getRequests().size() > 0 && getRequests().get(0).getErrors() != null && getRequests().get(0).getErrors().size() > 0;
    }

    public List<CdekNoticeRequestEntityOrderDto> getErrors() {
        if (isHaveErrors()) {
            return getRequests().get(0).getErrors();
        } else {
            return new ArrayList<>();
        }
    }
}
