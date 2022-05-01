package ru.sir.richard.boss.api.market;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.sir.richard.boss.AnyTestEnvironment;
import ru.sir.richard.boss.api.market.OzonMarketApi;
import ru.sir.richard.boss.model.data.Order;
import ru.sir.richard.boss.model.data.Product;
import ru.sir.richard.boss.model.data.crm.OzonResult;
import ru.sir.richard.boss.model.types.CrmTypes;
import ru.sir.richard.boss.model.utils.DateTimeUtils;
import ru.sir.richard.boss.model.utils.Pair;

public class OzonMarketApiTest {
			
	private class TestEnvironment extends AnyTestEnvironment {
	
		@SuppressWarnings("serial")
		@Override
		protected Map<String, String> getEnvironmentMap() {
			return new HashMap<String, String>(){{
			    put("ozon.market.host", "api-seller.ozon.ru");
			    put("ozon.market.url", "https://api-seller.ozon.ru");			    
			    put("ozon.market.client.id", "***");
			    put("ozon.market.client.key", "***");
			    
			    put("ozon.market.cdek.provider", "1");			    
			    put("ozon.market.cdek.warehouse", "2");			    
			    put("ozon.market.cdek.delivery.courier.main", "5");
			    put("ozon.market.cdek.delivery.courier.economy", "5");			    
			    put("ozon.market.cdek.delivery.pvz.main", "3");
			    put("ozon.market.cdek.delivery.pvz.economy", "3");
			    
			    put("ozon.market.ozon.provider", "2");
			    put("ozon.market.ozon.warehouse.courier", "2*");
			    put("ozon.market.ozon.warehouse.pickup", "2*");			    
			}};
		}	
		
	}	


}
