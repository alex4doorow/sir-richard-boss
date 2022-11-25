package ru.sir.richard.boss.model.dto;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import ru.sir.richard.boss.model.utils.DateTimeUtils;

@Data
public class CdekEntityOrderStatusDto {	
	private String code;
	@JsonProperty("date_time")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DateTimeUtils.DATE_FORMAT_CDEK)	
	private Date dateTime;
	private String city;
}