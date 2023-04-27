package ru.sir.richard.boss.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import ru.sir.richard.boss.model.utils.DateTimeUtils;

import java.util.Date;
import java.util.List;

@Data
public class CdekEntityOrderRequestDto {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("request_uuid")
    private String requestUuid;
    private String type;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DateTimeUtils.DATE_FORMAT_CDEK)
    @JsonProperty("date_time")
    private Date dateTime;
    private String state;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<CdekEntityOrderErrorDto> errors;

}
/*
"request_uuid": "d05b8281-ce3c-4ab8-a0bc-494b19f762e4",
        "type": "CREATE",
        "date_time": "2023-04-25T11:41:16+0000",
        "state": "INVALID",
        "errors": [
        {
        "code": "error_validate_package_height_negatierror_validate_or_more_1500",
        "message": "Высота должна принадлежать диапазону от 1 см до 1500 см"
        },
        {
        "code": "error_validate_package_length_negatierror_validate_or_more_1500",
        "message": "Длина должна принадлежать диапазону от 1 см до 1500 см"
        }
        ]

 */