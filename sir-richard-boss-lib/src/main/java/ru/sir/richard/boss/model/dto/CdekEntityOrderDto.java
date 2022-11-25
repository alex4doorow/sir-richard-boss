package ru.sir.richard.boss.model.dto;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import ru.sir.richard.boss.model.utils.TextUtils;

@Data
public class CdekEntityOrderDto {
	private String uuid;
	private int type;
	@JsonProperty("cdek_number")
	private String cdekNumber;
	private String number;
	@JsonProperty("tariff_code")
	private int tariffCode;
	@JsonProperty("delivery_point")
	private String deliveryPoint;
	@JsonProperty("delivery_recipient_cost")
	private CdekEntityAmountDto deliveryRecipientCost;
	private CdekEntityRecipientDto recipient;
	@JsonProperty("from_location")
	private CdekEntityOrderLocationDto fromLocation;
	@JsonProperty("to_location")
	private CdekEntityOrderLocationDto toLocation;	
	private List<CdekEntityServiceOrderDto> services;
	private List<CdekEntityOrderPackageDto> packages;
	private List<CdekEntityOrderStatusDto> statuses;
	@JsonProperty("delivery_mode")
	private String deliveryMode;
	@JsonProperty("delivery_date")
	private Date deliveryDate;
	@JsonProperty("delivery_detail")
	private CdekEntityOrderDeliveryDetailDto deliveryDetail;
	
	public int getNo() {
		if (StringUtils.isEmpty(this.getNumber())) {
			return 0;
		}
		return Integer.valueOf(TextUtils.numberDigit(this.getNumber()));
	}
}
