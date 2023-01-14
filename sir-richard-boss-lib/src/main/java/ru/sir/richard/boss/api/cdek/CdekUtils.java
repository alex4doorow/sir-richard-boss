package ru.sir.richard.boss.api.cdek;

import ru.sir.richard.boss.model.types.DeliveryTypes;

public class CdekUtils {
    public static final Byte ORDER_TYPE_ONLINE_STORE = 1;
    public static final Byte ORDER_TYPE_DELIVERY = 2;
	public static final String ORDER_SERVICE_INSURANCE = "INSURANCE";

    public static int getCdekTariffId(DeliveryTypes deliveryType) {
		int tariffId;
		if (deliveryType == DeliveryTypes.CDEK_COURIER) {
			tariffId = 137;
		} else if (deliveryType == DeliveryTypes.CDEK_COURIER_ECONOMY) {
			tariffId = 233;
	    } else if (deliveryType == DeliveryTypes.CDEK_PVZ_TYPICAL) {
	    	tariffId = 136;
	    } else if (deliveryType == DeliveryTypes.PICKUP) {
	    	tariffId = 136;
	    } else if (deliveryType == DeliveryTypes.CDEK_PVZ_ECONOMY) {
	    	tariffId = 234;
	    } else {
	    	tariffId = 0;
	    }
		return tariffId;
	}
}
