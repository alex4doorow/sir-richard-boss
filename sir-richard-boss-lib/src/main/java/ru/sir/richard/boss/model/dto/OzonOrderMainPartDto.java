package ru.sir.richard.boss.model.dto;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.sir.richard.boss.model.utils.DateTimeUtils;

/**
 * Main part - body of OzonOrder specification 
 * @author alex4doorow
 *
 */
@Data
public class OzonOrderMainPartDto {

	@JsonProperty("posting_number")
	private String postingNumber;
	@JsonProperty("order_id")
	private Long orderId;
	@JsonProperty("order_number")
	private String orderNumber;
	private String status;
	@JsonProperty("in_process_at")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DateTimeUtils.DATE_FORMAT_OZON)
	private Date inProcessAt;
	@JsonProperty("shipment_date")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DateTimeUtils.DATE_FORMAT_OZON)
	private Date shipmentDate;
	@JsonProperty("tracking_number")
	private String trackingNumber;
	@JsonProperty("delivery_method")
	private DeliveryMethodDto deliveryMethod;
	@JsonProperty("analytics_data")
	private AnalyticsDataDto analyticsData;

	@JsonInclude(JsonInclude.Include.ALWAYS)
	private OzonOrderProductDto[] products;
	
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OzonOrderProductDto {
        private BigDecimal price;
        @JsonProperty("offer_id")
        private String offerId;
        private String name;
        private String sku;
        private Integer quantity;
    }

    @Data
    public static class DeliveryMethodDto {
        private String id;
        private String name;
        @JsonProperty("warehouse_id")
        private Long warehouseId;
        private String warehouse;        
        @JsonProperty("tpl_provider_id")
        private Integer tplProviderId;
    }

    @Data
    public static class AnalyticsDataDto {
        private String region;
        private String city;
        @JsonProperty("delivery_type")
        private String deliveryType;
        @JsonProperty("warehouse_id")
        private Long warehouseId;
        
        public String getAddress() {
        	if (StringUtils.isEmpty(region) && StringUtils.isEmpty(region)) {
        		return "";
        	}
            return Objects.toString(region, "") + ", " + Objects.toString(city, "");
        }
    }
    
}
