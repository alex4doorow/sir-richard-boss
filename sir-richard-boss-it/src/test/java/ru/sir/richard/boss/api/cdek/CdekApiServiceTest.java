package ru.sir.richard.boss.api.cdek;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.PropertySource;

import lombok.extern.slf4j.Slf4j;
import ru.sir.richard.boss.model.data.Address;
import ru.sir.richard.boss.model.data.Order;

@Slf4j
@SpringBootTest
@PropertySource(value="classpath:application-it.properties", encoding="UTF-8")
public class CdekApiServiceTest {

	@Autowired
    private CdekApiService cdekApiService;

    @Test
    public void testGetCitiesSimple() throws URISyntaxException {
    	List<Address> cities = cdekApiService.getCities("Киров");
    	cities.forEach((city) -> log.debug("testGetCdekCities: {}, {}, {}", city.getAddress(), city.getCarrierInfo().getCityId(), city.getCarrierInfo().getPostalSize()));        
    	assertTrue(cities.size() == 8, "Киров");
    }
    
	@Test
	public void testGetCitiesOtherWize() {	
		
		List<Address> cities = cdekApiService.getCities("Авдеевка");
		cities.forEach((city) -> log.debug("testGetCdekCities: {}", city.getAddress()));		
		int i = 0;
		for (Address city : cities) {
			if ("Авдеевка, Хвастовичский район, Калужская область, Россия".equals(city.getAddress())) {
				i++;
			}			
		}
		assertTrue(i == 1, "Авдеевка, Хвастовичский район, Калужская область, Россия");

		cities = cdekApiService.getCities("Калуга");
		cities.forEach((city) -> log.debug("testGetCdekCities: {}", city.getAddress()));
		i = 0;
		int j = 0;
		for (Address city : cities) {
			if ("Калуга, Калужская область, Россия".equals(city.getAddress())) {
				i++;
			}			
			if ("Калуган, Тегеран, Иран".equals(city.getAddress())) {
				j++;
			}
		}
		assertTrue(i == 1, "Калуга, Калужская область, Россия");
		assertFalse(j == 1, "Калуган, Тегеран, Иран");		
	}
	
	@Test
	public void testGetPVZs() {
		List<Address> pvzs = cdekApiService.getPvzs(415);
		pvzs.forEach((pvz) -> log.debug("testGetPVZs: {}, {}, {}, {}", pvz.getCarrierInfo().getPvzId(), 
				pvz.getCarrierInfo().getPvz(), 
				pvz.getCarrierInfo().getFullAddress(), 
				pvz.getCarrierInfo().getAddressComment()));
		
	}
	
	@Test
	public void testGetOrder() {
		Order order = cdekApiService.getOrder("1378911159", null);
		log.debug("order: {}", order);		
	}
	
	@Test
	public void testGetOrderStatuses() {
		List<Order> orders = new ArrayList<Order>();
		Order one = new Order();
		one.getDelivery().setTrackCode("1350203193");
		orders.add(one);
		
		Order two = new Order();
		two.getDelivery().setTrackCode("1350203193");
		orders.add(two);
		
		Order three = new Order();
		three.getDelivery().setTrackCode("1378911159");
		orders.add(three);
				
		List<Order> withStatusesOrders = cdekApiService.getStatuses(orders);
		withStatusesOrders.forEach((withStatusesOrder) -> log.debug("withStatusesOrder: {}, {}, {}", withStatusesOrder.getNo(), 
				withStatusesOrder.getDelivery().getTrackCode(), 
				withStatusesOrder.getDelivery().getCarrierStatus()));		
	}
}
