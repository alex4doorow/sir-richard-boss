package ru.sir.richard.boss.api.market;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import ru.sir.richard.boss.model.data.Product;

public class YandexMarketApiTest {
	
	private final Logger logger = LoggerFactory.getLogger(YandexMarketApiTest.class);
		
	//@Test
	public void testPribormasterCart() throws UnirestException {
		
		Map<String, String> headers = new HashMap<>();
		headers.put("content-type", "application/json;charset=utf-8");
		headers.put("authorization", "7C00000130783B1E");

		JSONObject jsonObject = null;
		try {		     
			String jsonObjectString = "{ \"cart\": { \"businessId\": 893360, \"currency\": \"RUR\", \"items\": [ { \"feedId\": 935744, \"offerId\": \"SFLMF30-0,14\", \"offerName\": \"Леска флюорокарбон Shii Saido Magic Flurry, L-30 м, d-0,135 мм, test-1,29 кг, прозрачная\", \"subsidy\": 0, \"count\": 1, \"fulfilmentShopId\": 1017201, \"warehouseId\": 55269, \"partnerWarehouseId\": \"46902712-00b8-4b38-ac5c-a86e02b42b75\" } ], \"delivery\": { \"region\": { \"id\": 213, \"name\": \"Москва\", \"type\": \"CITY\", \"parent\": { \"id\": 1, \"name\": \"Москва и Московская область\", \"type\": \"SUBJECT_FEDERATION\", \"parent\": { \"id\": 3, \"name\": \"Центральный федеральный округ\", \"type\": \"COUNTRY_DISTRICT\", \"parent\": { \"id\": 225, \"name\": \"Россия\", \"type\": \"COUNTRY\" } } } } } } }";
			jsonObject = new JSONObject(jsonObjectString);		     
		     
		} catch (JSONException err){
		     logger.error("JSONException", err);
		}
		
		HttpResponse<JsonNode> jsonResponseCart = Unirest.post("https://pribormaster.ru/index.php?route=extension/module/yandex_market/cart")
				.headers(headers)
				.body(jsonObject)
				.asJson();
		assertEquals(200, jsonResponseCart.getStatus());
		//assertEquals("{\"cart\":{\"items\":[{\"feedId\":935744,\"count\":1,\"offerId\":\"SFLMF30-0,14\"}]}}", jsonResponseCart.getBody().toString());
		
		try {		     
			String jsonObjectString = "{ \"cart\": { \"businessId\": 893360, \"currency\": \"RUR\", \"items\": [ { \"feedId\": 935744, \"offerId\": \"SUN-BATTERY-SC-09\", \"offerName\": \"Аккумулятор SITITEK Sun-Battery SC-09, черный\", \"subsidy\": 0, \"count\": 1, \"fulfilmentShopId\": 1017201, \"warehouseId\": 55269, \"partnerWarehouseId\": \"46902712-00b8-4b38-ac5c-a86e02b42b75\" } ], \"delivery\": { \"region\": { \"id\": 213, \"name\": \"Москва\", \"type\": \"CITY\", \"parent\": { \"id\": 1, \"name\": \"Москва и Московская область\", \"type\": \"SUBJECT_FEDERATION\", \"parent\": { \"id\": 3, \"name\": \"Центральный федеральный округ\", \"type\": \"COUNTRY_DISTRICT\", \"parent\": { \"id\": 225, \"name\": \"Россия\", \"type\": \"COUNTRY\" } } } } } } }";
			jsonObject = new JSONObject(jsonObjectString);		     
		     
		} catch (JSONException err){
		     logger.error("JSONException", err);
		}
		jsonResponseCart = Unirest.post("https://pribormaster.ru/index.php?route=extension/module/yandex_market/cart")
				.headers(headers)
				.body(jsonObject)
				.asJson();
		assertEquals(200, jsonResponseCart.getStatus());
		//assertEquals("{\"cart\":{\"items\":[{\"feedId\":935744,\"count\":2,\"offerId\":\"SUN-BATTERY-SC-09\"}]}}", jsonResponseCart.getBody().toString());
		
		try {		     
			String jsonObjectString = "{ \"cart\": { \"businessId\": 893360, \"currency\": \"RUR\", \"items\": [ { \"feedId\": 935744, \"offerId\": \"LS-989\", \"offerName\": \"Ультразвуковой отпугиватель ЭкоСнайпер LS-989 (230 кв.м.)\", \"subsidy\": 0, \"count\": 1, \"fulfilmentShopId\": 1017201, \"warehouseId\": 55269, \"partnerWarehouseId\": \"46902712-00b8-4b38-ac5c-a86e02b42b75\" } ], \"delivery\": { \"region\": { \"id\": 213, \"name\": \"Москва\", \"type\": \"CITY\", \"parent\": { \"id\": 1, \"name\": \"Москва и Московская область\", \"type\": \"SUBJECT_FEDERATION\", \"parent\": { \"id\": 3, \"name\": \"Центральный федеральный округ\", \"type\": \"COUNTRY_DISTRICT\", \"parent\": { \"id\": 225, \"name\": \"Россия\", \"type\": \"COUNTRY\" } } } } } } }";
			jsonObject = new JSONObject(jsonObjectString);		     
		     
		} catch (JSONException err){
		     logger.error("JSONException", err);
		}
		jsonResponseCart = Unirest.post("https://pribormaster.ru/index.php?route=extension/module/yandex_market/cart")
				.headers(headers)
				.body(jsonObject)
				.asJson();
		assertEquals(200, jsonResponseCart.getStatus());
		//assertEquals("{\"cart\":{\"items\":[{\"feedId\":935744,\"count\":1,\"offerId\":\"LS-989\"}]}}", jsonResponseCart.getBody().toString());
	}
	
	//@Test
	public void testPribormasterStocks() throws UnirestException {
		Map<String, String> headers = new HashMap<>();
		headers.put("content-type", "application/json;charset=utf-8");
		headers.put("authorization", "7C00000130783B1E");

		JSONObject jsonObject = null;
		try {		     
			String jsonObjectString = "{ \"warehouseId\": 55269, \"partnerWarehouseId\": \"46902712-00b8-4b38-ac5c-a86e02b42b75\", \"skus\": [ \"924\", \"ZON-EL08-SINGLE-MEGAPHONE\", \"CAMPING-WORLD-SNOWBAG-20L\", \"CAMPING-WORLD-SNOWBAG-30L\", \"PF-BT-20\", \"BAMBOO-1\", \"BAMBOO-2\", \"BIRD-GARD-PRO\", \"BUGHUNTER-BH-02-RAPID\", \"BUGHUNTER-BH-03-EXPERT\", \"CAMPING-WORLD-SNOWBAG-10L\", \"CARKU-PRO-10\", \"CARKU-PRO-30\", \"CARKU-PRO-60\", \"CH-316B\", \"ELANG-RELE-POWERCONTROL\", \"ELANG-RELE-POWERCONTROL-PRO\", \"ELANG-RELE-THERMOCONTROL\", \"ELANG-SOCKET\", \"GP2-4A\", \"GRAD-DUOS-S\", \"HOMESTAR-CLASSIC\", \"HOONT-H973\", \"INKUBATOR-SITITEK-112\", \"KENAR-GD100-CN\", \"KENAR-GD100-N\", \"KENAR-GV-80-DN15\", \"KENAR-GV-80-DN20\", \"KENAR-GV-90-DN20\", \"KENAR-GV-90-DN25\", \"LS-107\", \"LS-2001\", \"LS-912\", \"LS-919\", \"LS-927M\", \"LS-928\", \"LS-937CD\", \"LS-967-3D\", \"LS-968\", \"LS-977F\", \"LS-987BF\", \"LS-989\", \"LS-997M\", \"LS-997MR\", \"LS-997P\", \"G6252-BK\", \"PF-TWP-10\", \"PF-TWP-28\", \"SAPSAN-GSM-SOCKET-PRO-10\", \"SITITEK-360\", \"SITITEK-EYE\", \"TELEMETRIKA-T4\", \"SN-310\", \"SUN-BATTERY-SC-09\", \"TELEMETRIKA-T20-SLAVE\", \"TELEMETRIKA-T40-MASTER\", \"TELEMETRIKA-T60-SLAVE\", \"TELEMETRIKA-T80-MASTER\", \"TERROR-EYES\", \"WEITECH-WK-0052\", \"WEITECH-WK-0053\", \"WEITECH-WK-0055\", \"WEITECH-WK-600\", \"WEITECH-WK0677\", \"WK-0020\", \"WK-0108\", \"WK-0432\", \"WK-2030\", \"ВСПЫШКАPLUS\", \"ГРАД-А-1000-ПРО\", \"ГРАД-А-1000-ПРО-PLUS\", \"ГРАД-А-500\", \"ГРАД-А-506\", \"ГРАД-А-550УЗ\", \"ГРАД-УЛЬТРА-3D\", \"ГРОМ-125\", \"ГРОМ-250М\", \"ГРОМ-ПРОФИ-LEDPLUS\", \"ГРОМ-ПРОФИ-М\", \"МОСКИТО-MV-01\", \"СОБАКАМ.НЕТ\", \"ТОРНАДО-400\", \"ТОРНАДО-800\", \"ФИЛИН\", \"ХОЗЯЙКА-31М\", \"ХОЗЯЙКА-31М-ПРО\", \"ХОЗЯЙКА-40М\", \"ЮСТ-КОРШУН-16\", \"ЮСТ-КОРШУН-8\", \"ЮСТ-КРУК\", \"ЮСТ-КРУК-НА-ФЛАГШТОКЕ\", \"ЮСТ-САПСАН-3\", \"CARKU-POWER-BANK-8000-PLUS\", \"PF-TW-14\", \"MB-IS-166\", \"MB-IS-129\", \"MB-IS-131\", \"MB-IS-132\", \"MB-IS-21\", \"PF-PK-10\", \"STT100-0,261\", \"STT100-0,286\", \"STT100-0,37\", \"STT100-0,405\", \"STT100-0,437\", \"STT100-0,467\", \"STT100-0,496\", \"PF-TWP-27\", \"STT100-0,128\", \"STT100-0,181\", \"STT100-0,203\", \"STT100-0,234\", \"STT100-0,309\", \"STT100-0,331\", \"G103\", \"NIG30-0,08\", \"NIG30-0,26\", \"SFLMF30-0,2\", \"SFLMF30-0,23\", \"SMOMD30-0,074\", \"SMOMD30-0,091\", \"SMOMD30-0,105\", \"SMOMD30-0,128\", \"SMOMD30-0,148\", \"SMOMD30-0,165\", \"STT100-0,148\", \"STT100-0,165\", \"SMOIS30-0,165\", \"SMOIS30-0,234\", \"SMOIS30-0,261\", \"SMOIS30-0,286\", \"SMOIS30-0,309\", \"SFLMF30-0,12\", \"SFLMF30-0,14\", \"SFLMF30-0,16\", \"SFLMF30-0,18\", \"NIG30-0,20\", \"Я-БР102\", \"NIG30-0,18\", \"NIG30-0,23\", \"NIG30-0,10\", \"NIG30-0,12\", \"NIG30-0,14\", \"NIG30-0,16\", \"Я-БР53\", \"SMOMD30-0,309\", \"SMOMD30-0,181\", \"SMOMD30-0,234\", \"SMOMD30-0,261\", \"SMOMD30-0,286\", \"Я-БР132\", \"Я-БР430\", \"Я-БР122\", \"Я-БР123\", \"Я-БР128\", \"Я-БР277\", \"Я-БР55\", \"Я-БР215\", \"Я-БР217\", \"Я-БР47\", \"Я-БР48\", \"Я-БР10\", \"Я-БР160\", \"Я-БР290\", \"Я-БР502\", \"Я-БР503\", \"Я-БР100\", \"Я-БР275\", \"N-BOX16\", \"N-BOX25\", \"N-BOX26\", \"N-BOX34\", \"N-BOX35\", \"N-BOX36\", \"N-BOX37\", \"N-BOX38\", \"N-BOX39\", \"PF-PFL-HL29\", \"G101H\", \"PF-PK-17\", \"G202\", \"G202B\", \"PF-MT-09\", \"WK-0025\", \"SMOIS30-0,074\", \"SMOIS30-0,091\", \"SMOIS30-0,105\", \"SMOIS30-0,128\", \"SMOIS30-0,181\", \"SMOIS30-0,203\", \"AR-112\", \"G8012-OR\", \"GK-2C-BLACK\", \"SR-02\", \"WEITECH-WK-0054\", \"T4L-IO-102-25\", \"LS-216\", \"FH51-GB\", \"G615\", \"G619\", \"G740-BK\", \"PF-PK-02\", \"F615\", \"F619\", \"PF-TWP-21\", \"DSS-01\", \"CAMPING-WORLD-SNOWBAG-05L\", \"ALCOHUNTER-ECONOM\", \"F616\", \"LS-977\", \"ALCOHUNTER-PROFESSIONAL-X\", \"ALCOHUNTER-PROFESSIONAL-PLUS\", \"GB-30L\", \"GC1-16\", \"GC1-40\", \"GC2-40D\", \"GF-4WB\", \"GK-2C-WHITE\", \"GE2\", \"GH-4N\", \"GLT-3\", \"SAPSAN-GSM-PRO-4S-PULT\" ] } ";
			jsonObject = new JSONObject(jsonObjectString);		     
		     
		} catch (JSONException err){
		     logger.error("JSONException", err);
		}
		
		HttpResponse<JsonNode> jsonResponseCart = Unirest.post("https://pribormaster.ru/index.php?route=extension/module/yandex_market/stocks")
				.headers(headers)
				.body(jsonObject)
				.asJson();
		assertEquals(200, jsonResponseCart.getStatus());
		
		logger.debug(jsonResponseCart.getBody().toString());
		
		//{"skus":[{"warehouseId":55269,"sku":"924","items":[{"count":0,"type":"FIT","updatedAt":"2022-01-31T19:53:52+03:00"}]},{"warehouseId":55269,"sku":"ZON-EL08-SINGLE-MEGAPHONE","items":[{"count":"3","type":"FIT","updatedAt":"2022-01-31T19:53:52+03:00"}]},{"warehouseId":55269,"sku":"CAMPING-WORLD-SNOWBAG-20L","items":[{"count":"0","type":"FIT","updatedAt":"2022-01-31T19:53:52+03:00"}]},{"warehouseId":55269,"sku":"CAMPING-WORLD-SNOWBAG-30L","items":[{"count":"0","type":"FIT","updatedAt":"2022-01-31T19:53:52+03:00"}]},{"warehouseId":55269,"sku":"PF-BT-20","items":[{"count":0,"type":"FIT","updatedAt":"2022-01-31T19:53:52+03:00"}]},{"warehouseId":55269,"sku":"BAMBOO-1","items":[{"count":"2","type":"FIT","updatedAt":"2022-01-31T19:53:52+03:00"}]},{"warehouseId":55269,"sku":"BAMBOO-2","items":[{"count":"3","type":"FIT","updatedAt":"2022-01-31T19:53:52+03:00"}]},{"warehouseId":55269,"sku":"BIRD-GARD-PRO","items":[{"count":0,"type":"FIT","updatedAt":"2022-01-31T19:53:52+03:00"}]},{"warehouseId":55269,"sku":"BUGHUNTER-BH-02-RAPID","items":[{"count":0,"type":"FIT","updatedAt":"2022-01-31T19:53:52+03:00"}]},{"warehouseId":55269,"sku":"BUGHUNTER-BH-03-EXPERT","items":[{"count":"0","type":"FIT","updatedAt":"2022-01-31T19:53:52+03:00"}]},{"warehouseId":55269,"sku":"CAMPING-WORLD-SNOWBAG-10L","items":[{"count":"1","type":"FIT","updatedAt":"2022-01-31T19:53:52+03:00"}]},{"warehouseId":55269,"sku":"CARKU-PRO-10","items":[{"count":"0","type":"FIT","updatedAt":"2022-01-31T19:53:52+03:00"}]},{"warehouseId":55269,"sku":"CARKU-PRO-30","items":[{"count":"0","type":"FIT","updatedAt":"2022-01-31T19:53:52+03:00"}]},{"warehouseId":55269,"sku":"CARKU-PRO-60","items":[{"count":"0","type":"FIT","updatedAt":"2022-01-31T19:53:52+03:00"}]},{"warehouseId":55269,"sku":"CH-316B","items":[{"count":"2","type":"FIT","updatedAt":"2022-01-31T19:53:52+03:00"}]},{"warehouseId":55269,"sku":"ELANG-RELE-POWERCONTROL","items":[{"count":"8","type":"FIT","updatedAt":"2022-01-31T19:53:52+03:00"}]},{"warehouseId":55269,"sku":"ELANG-RELE-POWERCONTROL-PRO","items":[{"count":"0","type":"FIT","updatedAt":"2022-01-31T19:53:52+03:00"}]},{"warehouseId":55269,"sku":"ELANG-RELE-THERMOCONTROL","items":[{"count":"0","type":"FIT","updatedAt":"2022-01-31T19:53:52+03:00"}]},{"warehouseId":55269,"sku":"ELANG-SOCKET","items":[{"count":"0","type":"FIT","updatedAt":"2022-01-31T19:53:52+03:00"}]},{"warehouseId":55269,"sku":"GP2-4A","items":[{"count":"0","type":"FIT","updatedAt":"2022-01-31T19:53:52+03:00"}]},{"warehouseId":55269,"sku":"GRAD-DUOS-S","items":[{"count":"2","type":"FIT","updatedAt":"2022-01-31T19:53:52+03:00"}]},{"warehouseId":55269,"sku":"HOMESTAR-CLASSIC","items":[{"count":"0","type":"FIT","updatedAt":"2022-01-31T19:53:52+03:00"}]},{"warehouseId":55269,"sku":"HOONT-H973","items":[{"count":"9","type":"FIT","updatedAt":"2022-01-31T19:53:52+03:00"}]},{"warehouseId":55269,"sku":"INKUBATOR-SITITEK-112","items":[{"count":"1","type":"FIT","updatedAt":"2022-01-31T19:53:52+03:00"}]},{"warehouseId":55269,"sku":"KENAR-GD100-CN","items":[{"count":"0","type":"FIT","updatedAt":"2022-01-31T19:53:52+03:00"}]},{"warehouseId":55269,"sku":"KENAR-GD100-N","items":[{"count":"2","type":"FIT","updatedAt":"2022-01-31T19:53:52+03:00"}]},{"warehouseId":55269,"sku":"KENAR-GV-80-DN15","items":[{"count":"0","type":"FIT","updatedAt":"2022-01-31T19:53:52+03:00"}]},{"warehouseId":55269,"sku":"KENAR-GV-80-DN20","items":[{"count":"0","type":"FIT","updatedAt":"2022-01-31T19:53:52+03:00"}]},{"warehouseId":55269,"sku":"KENAR-GV-90-DN20","items":[{"count":"1","type":"FIT","updatedAt":"2022-01-31T19:53:52+03:00"}]},{"warehouseId":55269,"sku":"KENAR-GV-90-DN25","items":[{"count":"2","type":"FIT","updatedAt":"2022-01-31T19:53:52+03:00"}]},{"warehouseId":55269,"sku":"LS-107","items":[{"count":"19","type":"FIT","updatedAt":"2022-01-31T19:53:52+03:00"}]},{"warehouseId":55269,"sku":"LS-2001","items":[{"count":"3","type":"FIT","updatedAt":"2022-01-31T19:53:52+03:00"}]},{"warehouseId":55269,"sku":"LS-912","items":[{"count":"3","type":"FIT","updatedAt":"2022-01-31T19:53:52+03:00"}]},{"warehouseId":55269,"sku":"LS-919","items":[{"count":"8","type":"FIT","updatedAt":"2022-01-31T19:53:52+03:00"}]},{"warehouseId":55269,"sku":"LS-927M","items":[{"count":"3","type":"FIT","updatedAt":"2022-01-31T19:53:52+03:00"}]},{"warehouseId":55269,"sku":"LS-928","items":[{"count":"1","type":"FIT","updatedAt":"2022-01-31T19:53:52+03:00"}]},{"warehouseId":55269,"sku":"LS-937CD","items":[{"count":"0","type":"FIT","updatedAt":"2022-01-31T19:53:52+03:00"}]},{"warehouseId":55269,"sku":"LS-967-3D","items":[{"count":"0","type":"FIT","updatedAt":"2022-01-31T19:53:52+03:00"}]},{"warehouseId":55269,"sku":"LS-968","items":[{"count":"1","type":"FIT","updatedAt":"2022-01-31T19:53:52+03:00"}]},{"warehouseId":55269,"sku":"LS-977F","items":[{"count":"1","type":"FIT","updatedAt":"2022-01-31T19:53:52+03:00"}]},{"warehouseId":55269,"sku":"LS-987BF","items":[{"count":"1","type":"FIT","updatedAt":"2022-01-31T19:53:52+03:00"}]},{"warehouseId":55269,"sku":"LS-989","items":[{"count":"1","type":"FIT","updatedAt":"2022-01-31T19:53:52+03:00"}]},{"warehouseId":55269,"sku":"LS-997M","items":[{"count":"2","type":"FIT","updatedAt":"2022-01-31T19:53:52+03:00"}]},{"warehouseId":55269,"sku":"LS-997MR","items":[{"count":"10","type":"FIT","updatedAt":"2022-01-31T19:53:52+03:00"}]},{"warehouseId":55269,"sku":"LS-997P","items":[{"count":"4","type":"FIT","updatedAt":"2022-01-31T19:53:52+03:00"}]},{"warehouseId":55269,"sku":"G6252-BK","items":[{"count":"0","type":"FIT","updatedAt":"2022-01-31T19:53:52+03:00"}]},{"warehouseId":55269,"sku":"PF-TWP-10","items":[{"count":"0","type":"FIT","updatedAt":"2022-01-31T19:53:52+03:00"}]},{"warehouseId":55269,"sku":"PF-TWP-28","items":[{"count":"1","type":"FIT","updatedAt":"2022-01-31T19:53:52+03:00"}]},{"warehouseId":55269,"sku":"SAPSAN-GSM-SOCKET-PRO-10","items":[{"count":"0","type":"FIT","updatedAt":"2022-01-31T19:53:52+03:00"}]},{"warehouseId":55269,"sku":"SITITEK-360","items":[{"count":"0","type":"FIT","updatedAt":"2022-01-31T19:53:52+03:00"}]},{"warehouseId":55269,"sku":"SITITEK-EYE","items":[{"count":"1","type":"FIT","updatedAt":"2022-01-31T19:53:52+03:00"}]},{"warehouseId":55269,"sku":"TELEMETRIKA-T4","items":[{"count":"0","type":"FIT","updatedAt":"2022-01-31T19:53:52+03:00"}]},{"warehouseId":55269,"sku":"SN-310","items":[{"count":"0","type":"FIT","updatedAt":"2022-01-31T19:53:52+03:00"}]},{"warehouseId":55269,"sku":"SUN-BATTERY-SC-09","items":[{"count":"2","type":"FIT","updatedAt":"2022-01-31T19:53:52+03:00"}]},{"warehouseId":55269,"sku":"TELEMETRIKA-T20-SLAVE","items":[{"count":"9","type":"FIT","updatedAt":"2022-01-31T19:53:52+03:00"}]},{"warehouseId":55269,"sku":"TELEMETRIKA-T40-MASTER","items":[{"count":"0","type":"FIT","updatedAt":"2022-01-31T19:53:52+03:00"}]},{"warehouseId":55269,"sku":"TELEMETRIKA-T60-SLAVE","items":[{"count":"7","type":"FIT","updatedAt":"2022-01-31T19:53:52+03:00"}]},{"warehouseId":55269,"sku":"TELEMETRIKA-T80-MASTER","items":[{"count":"9","type":"FIT","updatedAt":"2022-01-31T19:53:52+03:00"}]},{"warehouseId":55269,"sku":"TERROR-EYES","items":[{"count":"1","type":"FIT","updatedAt":"2022-01-31T19:53:52+03:00"}]},{"warehouseId":55269,"sku":"WEITECH-WK-0052","items":[{"count":"2","type":"FIT","updatedAt":"2022-01-31T19:53:52+03:00"}]},{"warehouseId":55269,"sku":"WEITECH-WK-0053","items":[{"count":"1","type":"FIT","updatedAt":"2022-01-31T19:53:52+03:00"}]},{"warehouseId":55269,"sku":"WEITECH-WK-0055","items":[{"count":"2","type":"FIT","updatedAt":"2022-01-31T19:53:52+03:00"}]},{"warehouseId":55269,"sku":"WEITECH-WK-600","items":[{"count":"2","type":"FIT","updatedAt":"2022-01-31T19:53:52+03:00"}]},{"warehouseId":55269,"sku":"WEITECH-WK0677","items":[{"count":"1","type":"FIT","updatedAt":"2022-01-31T19:53:52+03:00"}]},{"warehouseId":55269,"sku":"WK-0020","items":[{"count":"1","type":"FIT","updatedAt":"2022-01-31T19:53:52+03:00"}]},{"warehouseId":55269,"sku":"WK-0108","items":[{"count":"2","type":"FIT","updatedAt":"2022-01-31T19:53:52+03:00"}]},{"warehouseId":55269,"sku":"WK-0432","items":[{"count":"1","type":"FIT","updatedAt":"2022-01-31T19:53:52+03:00"}]},{"warehouseId":55269,"sku":"WK-2030","items":[{"count":"2","type":"FIT","updatedAt":"2022-01-31T19:53:52+03:00"}]},{"warehouseId":55269,"sku":"ВСПЫШКАPLUS","items":[{"count":"7","type":"FIT","updatedAt":"2022-01-31T19:53:52+03:00"}]},{"warehouseId":55269,"sku":"ГРАД-А-1000-ПРО","items":[{"count":"2","type":"FIT","updatedAt":"2022-01-31T19:53:52+03:00"}]},{"warehouseId":55269,"sku":"ГРАД-А-1000-ПРО-PLUS","items":[{"count":"2","type":"FIT","updatedAt":"2022-01-31T19:53:52+03:00"}]},{"warehouseId":55269,"sku":"ГРАД-А-500","items":[{"count":"3","type":"FIT","updatedAt":"2022-01-31T19:53:52+03:00"}]},{"warehouseId":55269,"sku":"ГРАД-А-506","items":[{"count":"5","type":"FIT","updatedAt":"2022-01-31T19:53:52+03:00"}]},{"warehouseId":55269,"sku":"ГРАД-А-550УЗ","items":[{"count":"7","type":"FIT","updatedAt":"2022-01-31T19:53:52+03:00"}]},{"warehouseId":55269,"sku":"ГРАД-УЛЬТРА-3D","items":[{"count":"1","type":"FIT","updatedAt":"2022-01-31T19:53:52+03:00"}]},{"warehouseId":55269,"sku":"ГРОМ-125","items":[{"count":"4","type":"FIT","updatedAt":"2022-01-31T19:53:52+03:00"}]},{"warehouseId":55269,"sku":"ГРОМ-250М","items":[{"count":"2","type":"FIT","updatedAt":"2022-01-31T19:53:52+03:00"}]},{"warehouseId":55269,"sku":"ГРОМ-ПРОФИ-LEDPLUS","items":[{"count":"2","type":"FIT","updatedAt":"2022-01-31T19:53:52+03:00"}]},{"warehouseId":55269,"sku":"ГРОМ-ПРОФИ-М","items":[{"count":"2","type":"FIT","updatedAt":"2022-01-31T19:53:52+03:00"}]},{"warehouseId":55269,"sku":"МОСКИТО-MV-01","items":[{"count":"0","type":"FIT","updatedAt":"2022-01-31T19:53:52+03:00"}]},{"warehouseId":55269,"sku":"СОБАКАМ.НЕТ","items":[{"count":"2","type":"FIT","updatedAt":"2022-01-31T19:53:52+03:00"}]},{"warehouseId":55269,"sku":"ТОРНАДО-400","items":[{"count":0,"type":"FIT","updatedAt":"2022-01-31T19:53:52+03:00"}]},{"warehouseId":55269,"sku":"ТОРНАДО-800","items":[{"count":0,"type":"FIT","updatedAt":"2022...
	}
	
	//@Test
	public void testOfferPricesUpdates() {		
		YandexMarketApi yandexMarketApi = new YandexMarketApi();
		
		List<Product> products = new ArrayList<Product>();
		Product productOne = new Product();
		productOne.setSku("СОБАКАМ.НЕТ");
		productOne.setPrice(BigDecimal.valueOf(4850));
		products.add(productOne);
				
		Product productTwo = new Product();
		productTwo.setSku("WEITECH-WK-0054");
		productTwo.setPrice(BigDecimal.valueOf(11190));
		products.add(productTwo);
		
		Product productThree = new Product();
		productThree.setSku("ГРАД-А-506");
		productThree.setPrice(BigDecimal.valueOf(4890));
		products.add(productThree);
	
		String result = yandexMarketApi.offerPricesUpdatesByAllWarehouses(products);
		logger.debug("offerMappingEntries() responseCode:{}", result);	
	
	}
	
	//@Test
	public void testOfferPricesUpdatesByAllWarehouse() {		
		YandexMarketApi yandexMarketApi = new YandexMarketApi();		
		List<Product> products = new ArrayList<Product>();
		Product productOne = new Product();
		productOne.setSku("HOONT-H973");
		productOne.setPrice(BigDecimal.valueOf(4690));
		products.add(productOne);
				
		String result = yandexMarketApi.offerPricesUpdatesByAllWarehouses(products);
		logger.debug("offerPricesUpdatesByAllWarehouse() responseCode:{}", result);				
	}
	

}
