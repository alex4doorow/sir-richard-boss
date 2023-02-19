package ru.sir.richard.boss.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import ru.sir.richard.boss.utils.DateTimeUtils;

import java.util.Date;
import java.util.List;

@Data
public class CdekRequestEntityOrderDto {
    @JsonProperty("request_uuid")
    private String requestUuid;
    private String type;
    private String state;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DateTimeUtils.DATE_FORMAT_CDEK)
    @JsonProperty("date_time")
    private Date dateTime;
    private List<CdekNoticeRequestEntityOrderDto> errors;
    private List<CdekNoticeRequestEntityOrderDto> warnings;
}