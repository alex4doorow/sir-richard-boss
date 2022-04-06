package ru.sir.richard.boss.web.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import ru.sir.richard.boss.crm.DeliveryService;
import ru.sir.richard.boss.dao.AlarmDao;
import ru.sir.richard.boss.dao.WikiDao;
import ru.sir.richard.boss.model.utils.SingleExecutor;

@Service("scheduledService")
public class ScheduledServiceImpl {
	
	private final Logger logger = LoggerFactory.getLogger(ScheduledServiceImpl.class);
	
	@Autowired
	private WikiDao wikiDao;
	
	@Autowired
	private AlarmDao alarmDao;
	
	@Autowired
	private DeliveryService deliveryService;
	
	@Autowired
	private WikiService wikiService;	

	@Scheduled(fixedDelay = 60 * 60 * 1000)
	public void scheduleDeliveryStatusReload() {		
		logger.debug("deliveryService.ordersStatusesReload(): start");		
		deliveryService.scheduledOrdersStatusesReload();		
		logger.debug("deliveryService.ordersStatusesReload(): end");
		SingleExecutor.DELIVERY_STATUS_CHANGE = false;
	}
	
	@Scheduled(fixedDelay = 12 * 60 * 60 * 1000)
	public void scheduleProductReload() {		
		logger.debug("wikiDao.init(): start");
		wikiDao.init();
		logger.debug("wikiDao.init(): end");		
	}
	
	@Scheduled(fixedDelay = 2 * 60 * 60 * 1000)
	public void scheduleOzon() {		
		logger.debug("scheduleOzon.init(): start");
		wikiService.ozonOfferPricesUpdates(false);
		logger.debug("scheduleOzon.init(): end");		
	}
	
	
	/*
	@Scheduled(fixedDelay = 60 * 1000)
	public void scheduleAlertCarHeartBeat() {		
		alarmDao.requestCarState();		
	}
	*/	

}
