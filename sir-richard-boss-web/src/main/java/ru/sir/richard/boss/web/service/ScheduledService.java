package ru.sir.richard.boss.web.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.sir.richard.boss.api.market.YandexMarketApi;
import ru.sir.richard.boss.crm.CrmManager;
import ru.sir.richard.boss.crm.DeliveryService;
import ru.sir.richard.boss.dao.WikiDao;
import ru.sir.richard.boss.model.data.Product;
import ru.sir.richard.boss.model.data.conditions.ProductConditions;
import ru.sir.richard.boss.model.utils.DateTimeUtils;
import ru.sir.richard.boss.model.utils.SingleExecutor;
import ru.sir.richard.boss.repository.AppUserRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
@Scope("singleton")
@Slf4j
public class ScheduledService {

    @Autowired
    private WikiDao wikiDao;

    @Autowired
    private CrmManager crmManager;

    @Autowired
    private DeliveryService deliveryService;

    @Autowired
    private WikiService wikiService;

    @Autowired
    private PricerService pricerService;

    @Autowired
    AppUserRepository wsUserRepository;

    @Autowired
    private Environment environment;

    /**
     * 1) главный шедулер: грузит товары в мозг, обновляет прайсы, устанавливает
     * цены и количество на маркетплейсы
     */
    @Scheduled(fixedDelay = 4 * 60 * 60 * 1000, initialDelay = 0 * 60 * 1000)
    public void scheduleProductReload() {

        // ввел новую опт, розницу - в яме, озоне, на сайте, у нас в бэке - все
        // поменялось
        // забыл лиды обновить - загрузились
        try {
            // 1) актуализация товаров
            wikiDao.init(true);
            if (Boolean.parseBoolean(environment.getProperty("application.production"))) {
                // для тестового режима не запускаем автоматическую интеграцию с crm'ами

                log.debug("scheduleProductReload {}: start", Thread.currentThread().getName());
                // 2) актуализация цен на яндекс маркете
                ProductConditions productConditions = new ProductConditions();
                productConditions.setYandexSellerExist(1);
                productConditions.setSupplierStockExist(0);
                List<Product> productsForOfferUpdates = wikiDao.listYmProductsByConditions(productConditions);
                YandexMarketApi yandexMarketApi = new YandexMarketApi(this.environment);
                yandexMarketApi.offerPricesUpdatesByAllWarehouses(productsForOfferUpdates);
                // 3) актуализация цен и остатков на ozon
                wikiService.ozonOfferPricesUpdates(getScheduledUserId(),false);
                // 4) загрузка лидов
                crmManager.setExecutorDate(DateTimeUtils.sysDate());
                crmManager.importRun();
                // 5) загрузка прайса сититек
                pricerService.runSititek();
                log.debug("scheduleProductReload {}: end", Thread.currentThread().getName());
                // 6) принудительно обновляем продукты
                wikiDao.init(false);
            }
        } catch (CannotGetJdbcConnectionException e) {
            log.error("CannotGetJdbcConnectionException: {}", "Ошибка подключения. Перезапустите dbConnect.");
        }
    }

    /**
     * 2) шедулер обновления остатков на озоне
     */
    @Scheduled(fixedDelay = 2 * 60 * 60 * 1000, initialDelay = 5 * 60 * 1000)
    public void scheduleOzon() {

        try {
            if (Boolean.parseBoolean(environment.getProperty("application.production"))) {
                log.debug("scheduleOzon.init(): start");
                wikiService.ozonOfferPricesUpdates(getScheduledUserId(),false);
                log.debug("scheduleOzon.init(): end");
            }
        } catch (CannotGetJdbcConnectionException e) {
            log.error("CannotGetJdbcConnectionException: {}", "Ошибка подключения. Перезапустите dbConnect.");
        }
    }

    /**
     * 3) шедулер синхронизации статусов заказов с маркетплейсами и сдэк
     */
    @Scheduled(fixedDelay = 60 * 60 * 1000, initialDelay = 10 * 60 * 1000)
    public void scheduleDeliveryStatusReload() {
        log.debug("deliveryService.ordersStatusesReload(): start");
        try {
            deliveryService.scheduledOrdersStatusesReload();
            log.debug("deliveryService.ordersStatusesReload(): end");
            SingleExecutor.DELIVERY_STATUS_CHANGE = false;
        } catch (CannotGetJdbcConnectionException e) {
            log.error("CannotGetJdbcConnectionException: {}", "Ошибка подключения. Перезапустите dbConnect.");
        }
    }

    @Scheduled(fixedDelay = 5 * 60 * 1000, initialDelay = 1 * 60 * 1000)
    public void scheduleMarketplacesCheater() {

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH");
        LocalDateTime now = LocalDateTime.now();

        int hh = Integer.parseInt(dtf.format(now));
        if ((hh >= 13 && hh < 14) && !SingleExecutor.MARKETPLACES_CHEATER_STATUS_SET) {
            // set in
            log.debug("scheduleMarketplacesCheater {}: set in", Thread.currentThread().getName());
            SingleExecutor.MARKETPLACES_CHEATER_STATUS_SET = true;
      		/*
    		List<Product> cheaterProducts = wikiDao.updateCheaterProductsStart();    		
    		YandexMarketApi yandexMarketApi = new YandexMarketApi(this.environment);
    		yandexMarketApi.offerPricesUpdatesByAllWarehouses(cheaterProducts);    		
    		OzonMarketApi ozonMarketApi = new OzonMarketApi(this.environment);
    		ozonMarketApi.offerPrices(cheaterProducts);
    		*/
        }
        if ((hh >= 14) && !SingleExecutor.MARKETPLACES_CHEATER_STATUS_ROLLBACK) {
            // set of
            log.debug("scheduleMarketplacesCheater {}: rollback", Thread.currentThread().getName());
            SingleExecutor.MARKETPLACES_CHEATER_STATUS_ROLLBACK = true;
 		
    		/*
    		List<Product> cheaterProducts = wikiDao.updateCheaterProductsRollback();    		
    		YandexMarketApi yandexMarketApi = new YandexMarketApi(this.environment);
    		yandexMarketApi.offerPricesUpdatesByAllWarehouses(cheaterProducts);    		
    		OzonMarketApi ozonMarketApi = new OzonMarketApi(this.environment);
    		ozonMarketApi.offerPrices(cheaterProducts);
    		*/
        }
        if (hh >= 23) {
            log.debug("scheduleMarketplacesCheater {}: clear", Thread.currentThread().getName());
            SingleExecutor.MARKETPLACES_CHEATER_STATUS_SET = false;
            SingleExecutor.MARKETPLACES_CHEATER_STATUS_ROLLBACK = false;
        }
    }

    private int getScheduledUserId() {
        return wsUserRepository.findByUsername("scheduler").getId().intValue();
    }

}
