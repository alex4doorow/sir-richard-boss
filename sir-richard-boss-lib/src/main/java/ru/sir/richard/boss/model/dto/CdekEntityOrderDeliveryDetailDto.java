package ru.sir.richard.boss.model.dto;

import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import ru.sir.richard.boss.utils.DateTimeUtils;

@Data
public class CdekEntityOrderDeliveryDetailDto {
	
	@JsonProperty("date")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DateTimeUtils.DATE_FORMAT_yyyy_MM_dd)
	private Date dateDetail;
	@JsonProperty("recipient_name")
	private String recipientName;
	@JsonProperty("delivery_sum")
	private BigDecimal deliverySum;
	@JsonProperty("total_sum")
	private BigDecimal totalSum;
}
