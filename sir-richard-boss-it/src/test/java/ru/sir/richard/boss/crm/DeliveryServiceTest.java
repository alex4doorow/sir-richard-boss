package ru.sir.richard.boss.crm;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.PropertySource;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest
@PropertySource(value="classpath:application-it.properties", encoding="UTF-8")
public class DeliveryServiceTest {
	
	@Autowired
	private DeliveryService deliveryService;
	
	@Test
	public void testOrdersStatusesReload() {
		deliveryService.ordersStatusesReload();
	}		

}
