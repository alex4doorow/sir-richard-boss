package ru.sir.richard.boss.model.dto;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import ru.sir.richard.boss.utils.DateTimeUtils;
import ru.sir.richard.boss.utils.TextUtils;

@Data
public class CdekEntityOrderDto {
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private String uuid;
	private int type;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("cdek_number")
	private String cdekNumber;
	private String number;
	@JsonProperty("tariff_code")
	private int tariffCode;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private String comment;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("shipment_point")
	private String shipmentPoint;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("delivery_point")
	private String deliveryPoint;
	@JsonProperty("delivery_recipient_cost")
	private CdekEntityAmountDto deliveryRecipientCost;
	private CdekEntityRecipientDto recipient;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("from_location")
	private CdekEntityOrderLocationDto fromLocation;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("to_location")
	private CdekEntityOrderLocationDto toLocation;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private List<CdekEntityServiceOrderDto> services;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("delivery_mode")
	private String deliveryMode;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DateTimeUtils.DATE_FORMAT_yyyy_MM_dd)
	@JsonProperty("delivery_date")
	private Date deliveryDate;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("delivery_detail")
	private CdekEntityOrderDeliveryDetailDto deliveryDetail;
	private List<CdekEntityOrderPackageDto> packages;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private List<CdekEntityOrderStatusDto> statuses;

	public int getNo() {
		if (StringUtils.isEmpty(this.getNumber())) {
			return 0;
		}
		return Integer.valueOf(TextUtils.numberDigit(this.getNumber()));
	}
}
/*
requests": [
		{
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
		}

 */