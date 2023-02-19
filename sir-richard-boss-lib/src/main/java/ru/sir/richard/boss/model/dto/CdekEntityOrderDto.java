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
	private List<CdekEntityOrderPackageDto> packages;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private List<CdekEntityOrderStatusDto> statuses;
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
	
	public int getNo() {
		if (StringUtils.isEmpty(this.getNumber())) {
			return 0;
		}
		return Integer.valueOf(TextUtils.numberDigit(this.getNumber()));
	}
}
