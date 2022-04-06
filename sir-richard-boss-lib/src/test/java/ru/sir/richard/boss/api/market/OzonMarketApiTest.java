package ru.sir.richard.boss.api.market;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ru.sir.richard.boss.api.market.OzonMarketApi;
import ru.sir.richard.boss.model.data.Order;
import ru.sir.richard.boss.model.data.Product;
import ru.sir.richard.boss.model.data.crm.OzonResult;
import ru.sir.richard.boss.model.types.CrmTypes;
import ru.sir.richard.boss.model.utils.DateTimeUtils;
import ru.sir.richard.boss.model.utils.Pair;

public class OzonMarketApiTest {
	
	//@Test
	public void testOfferStoks() {
		
		List<Product> products = new ArrayList<Product>();
		/*
		Product productOne = new Product();
		productOne.setSku("BUGHUNTER-BH-03-EXPERT");
		productOne.setQuantity(0);	
		productOne.setPrice(BigDecimal.valueOf(29490));
		productOne.getMarket(CrmTypes.OZON).setMarketSku("316083803");
		products.add(productOne);
			*/
		
		//315590021
		
		
		Product productTwo = new Product();
		productTwo.setSku("ГРАД-А-550УЗ");
		productTwo.setQuantity(-1);		
		productTwo.setPrice(BigDecimal.valueOf(3780));
		productTwo.getMarket(CrmTypes.OZON).setMarketSku("299342069");
		productTwo.getMarket(CrmTypes.OZON).setMarketSeller(true);
		
		products.add(productTwo);

		OzonMarketApi ozonMarketApi = new OzonMarketApi();
		OzonResult ozonResult;
		/*
		OzonResult ozonResult = ozonMarketApi.offerStocks(products);				
		System.out.println("ozonResult.isResponseSuccess(): " + ozonResult.isResponseSuccess());	
		System.out.println("ozonResult: " + ozonResult);
		
		*/
		ozonResult = ozonMarketApi.offerWarehouseStocks(products);
		String result = ozonMarketApi.offerPrices(products);	
		
		/*
		 https://api-seller.ozon.ru/v1/product/import/prices 
		 
		 192612
		 ce712209-09e6-44e7-87bf-97028532fae7
		 
{
    "prices": [
        {
            "offer_id": "BUGHUNTER-BH-03-EXPERT",
            "old_price": "0",
            "premium_price": "0",
            "price": "29490",
            "product_id": 316083803
        }
    ]
}



{
    "result": [
        {
            "warehouse_id": 22067517155000,
            "name": "Первый",
            "is_rfbs": true
        },
        {
            "warehouse_id": 22067558791000,
            "name": "Второй",
            "is_rfbs": false
        }
    ]
}


{
    "stocks": [
        {
            "offer_id": "string",
            "product_id": 0,
            "stock": 0,
            "warehouse_id": 0
        }
    ]
}
		  
		 * */
		
	}
	
	//@Test
	public void testOfferWarehouseStoks() {
		
		List<Product> products = new ArrayList<Product>();
		/*
		Product productOne = new Product();
		productOne.setSku("BUGHUNTER-BH-03-EXPERT");
		productOne.setQuantity(0);	
		productOne.setPrice(BigDecimal.valueOf(29490));
		productOne.getMarket(CrmTypes.OZON).setMarketSku("316083803");
		products.add(productOne);
			*/
		
		//315590021
		
		
		Product productTwo = new Product();
		productTwo.setSku("ГРАД-А-550УЗ");
		productTwo.setQuantity(0);		
		productTwo.setPrice(BigDecimal.valueOf(3290));
		productTwo.getMarket(CrmTypes.OZON).setMarketSku("299342069");
		productTwo.getMarket(CrmTypes.OZON).setMarketSeller(true);
		products.add(productTwo);

		OzonMarketApi ozonMarketApi = new OzonMarketApi(); 
		
		OzonResult ozonResult;
		ozonResult = ozonMarketApi.offerStocks(products);
		ozonResult = ozonMarketApi.offerWarehouseStocks(products);
		System.out.println("ozonResult.isResponseSuccess(): " + ozonResult.isResponseSuccess());	
		System.out.println("ozonResult: " + ozonResult);	
	}
	
	//@Test
	public void testGetOrders() throws ParseException {
		
		OzonMarketApi ozonMarketApi = new OzonMarketApi();
		
		/*
		acceptance_in_progress — идёт приёмка,
		awaiting_approve — ожидает подтверждения,
		awaiting_packaging — ожидает упаковки,
		awaiting_deliver — ожидает отгрузки,
		arbitration — арбитраж,
		client_arbitration — клиентский арбитраж доставки,
		delivering — доставляется,
		driver_pickup — у водителя,
		delivered — доставлено,
		cancelled — отменено,
		not_accepted — не принят на сортировочном центре.
		*/
		Date executorDate = DateTimeUtils.sysDate();
		Pair<Date> period = new Pair<Date>(DateTimeUtils.beforeAnyDate(executorDate, 10), DateTimeUtils.afterAnyDate(executorDate, 10));		
		ozonMarketApi.getOrders(period, "delivering");
		

		
	}
	
	//@Test
	public void testGetOrder() {
		
		OzonMarketApi ozonMarketApi = new OzonMarketApi();
		Order order = ozonMarketApi.getOrder("17797402-0235-1");
		
	}

}
