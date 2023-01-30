package ru.sir.richard.boss.dao;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.sir.richard.boss.model.data.Order;
import ru.sir.richard.boss.model.data.ProductCategory;
import ru.sir.richard.boss.model.data.SupplierStock;
import ru.sir.richard.boss.model.data.conditions.OrderConditions;
import ru.sir.richard.boss.model.data.conditions.ProductSalesReportConditions;
import ru.sir.richard.boss.model.data.report.AggregateProductSalesReportBean;
import ru.sir.richard.boss.model.data.report.ProductSalesReportBean;
import ru.sir.richard.boss.model.data.report.SalesFunnelReportBean;
import ru.sir.richard.boss.model.data.report.TempProductSalesReportBean;
import ru.sir.richard.boss.model.types.*;
import ru.sir.richard.boss.model.utils.DateTimeUtils;
import ru.sir.richard.boss.model.utils.Pair;

import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.*;

@Repository
@Slf4j
public class ReportDao extends AnyDaoImpl {

	@Autowired
	private WikiDao wikiDao;

	@Autowired
	private OrderDao orderDao;

	public void aggregateProductSalesWriteIntoExcel(List<AggregateProductSalesReportBean> beans) throws IOException {

		//с	по	итого продано, шт	isocket	sapsan	sanseit	эланг	эланг реле	IQsocket Mobile	Телеметрика Т80	Телеметрика Т60	итого
		// сититек gsm	сититек i8	сититек eye	остальные	итого
		// gsm сигнализации	usb микроскоп	планетарий	антижучки
		// отпугиватели птиц	отпугиватели грызунов	отпугиватель кротов	отпугиватель змей	отпугиватель собак	антидог	ThermaCELL	уничтожители комаров
		// экотестеры	ножеточки	столики для ноутбука	автокормушки	пуско-зарядные устройства CARKU	эхолоты	иные	расходы на рекламу, руб	расходы рекламы на прибор


		Workbook workBook = new HSSFWorkbook();
		Sheet sheet = workBook.createSheet("sheet-first");

		int rowIndex = 0;
		Row row = sheet.createRow(0);
		rowIndex++;

		Cell cell;
		int cellIndex = 0;

		// header
		cell = row.createCell(cellIndex);
		cell.setCellValue("c");
		cellIndex++;

		cell = row.createCell(cellIndex);
		cell.setCellValue("по");
		cellIndex++;

		cell = row.createCell(cellIndex);
		cell.setCellValue("итого продано, шт");
		cellIndex++;

		cell = row.createCell(cellIndex);
		cell.setCellValue("телеметрика т80");
		cellIndex++;

		cell = row.createCell(cellIndex);
		cell.setCellValue("телеметрика т60");
		cellIndex++;

		cell = row.createCell(cellIndex);
		cell.setCellValue("реле эланг");
		cellIndex++;

		cell = row.createCell(cellIndex);
		cell.setCellValue("другие");
		cellIndex++;

		cell = row.createCell(cellIndex);
		cell.setCellValue("итого");
		cellIndex++;

		cell = row.createCell(cellIndex);
		cell.setCellValue("видеоглазки");
		cellIndex++;

		cell = row.createCell(cellIndex);
		cell.setCellValue("gsm сигнализации");
		cellIndex++;

		cell = row.createCell(cellIndex);
		cell.setCellValue("планетарии");
		cellIndex++;

		cell = row.createCell(cellIndex);
		cell.setCellValue("антижучки");
		cellIndex++;

		cell = row.createCell(cellIndex);
		cell.setCellValue("отпугиватели птиц");
		cellIndex++;

		cell = row.createCell(cellIndex);
		cell.setCellValue("отпугиватели грызунов");
		cellIndex++;

		cell = row.createCell(cellIndex);
		cell.setCellValue("отпугиватели кротов");
		cellIndex++;

		cell = row.createCell(cellIndex);
		cell.setCellValue("отпугиватели змей");
		cellIndex++;

		cell = row.createCell(cellIndex);
		cell.setCellValue("отпугиватели собак");
		cellIndex++;

		cell = row.createCell(cellIndex);
		cell.setCellValue("антидог");
		cellIndex++;

		cell = row.createCell(cellIndex);
		cell.setCellValue("thermaCELL");
		cellIndex++;

		cell = row.createCell(cellIndex);
		cell.setCellValue("уничтожители комаров");
		cellIndex++;

		cell = row.createCell(cellIndex);
		cell.setCellValue("ножеточки");
		cellIndex++;

		cell = row.createCell(cellIndex);
		cell.setCellValue("столики для ноутбуков");
		cellIndex++;

		cell = row.createCell(cellIndex);
		cell.setCellValue("автокормушки");
		cellIndex++;

		cell = row.createCell(cellIndex);
		cell.setCellValue("CARKU");
		cellIndex++;

		cell = row.createCell(cellIndex);
		cell.setCellValue("эхолоты и камеры");
		cellIndex++;

		cell = row.createCell(cellIndex);
		cell.setCellValue("палатки");
		cellIndex++;

		cell = row.createCell(cellIndex);
		cell.setCellValue("расходы на рекламу");
		cellIndex++;

		cell = row.createCell(cellIndex);
		cell.setCellValue("расходы на продажу");
		cellIndex++;

		for (AggregateProductSalesReportBean bean : beans) {
			row = sheet.createRow(rowIndex);
			rowIndex++;

			cellIndex = 0;

			cell = row.createCell(cellIndex);
			cell.setCellValue(DateTimeUtils.defaultFormatDate(bean.getPeriod().getStart()));
			cellIndex++;

			cell = row.createCell(cellIndex);
			cell.setCellValue(DateTimeUtils.defaultFormatDate(bean.getPeriod().getEnd()));
			cellIndex++;

			cell = row.createCell(cellIndex);
			cell.setCellValue(bean.getTotal());
			cellIndex++;

			cell = row.createCell(cellIndex);
			cell.setCellValue(bean.getTelemetrikaT80());
			cellIndex++;

			cell = row.createCell(cellIndex);
			cell.setCellValue(bean.getTelemetrikaT60());
			cellIndex++;

			cell = row.createCell(cellIndex);
			cell.setCellValue(bean.getElang());
			cellIndex++;

			cell = row.createCell(cellIndex);
			cell.setCellValue(bean.getOtherGsmSocket());
			cellIndex++;

			cell = row.createCell(cellIndex);
			cell.setCellValue(bean.getTotalGsmSocket());
			cellIndex++;

			cell = row.createCell(cellIndex);
			cell.setCellValue(bean.getVideoEye());
			cellIndex++;

			cell = row.createCell(cellIndex);
			cell.setCellValue(bean.getGsmAlarm());
			cellIndex++;

			cell = row.createCell(cellIndex);
			cell.setCellValue(bean.getAstroEye());
			cellIndex++;

			cell = row.createCell(cellIndex);
			cell.setCellValue(bean.getBugHunter());
			cellIndex++;

			cell = row.createCell(cellIndex);
			cell.setCellValue(bean.getBirdRepeller());
			cellIndex++;

			cell = row.createCell(cellIndex);
			cell.setCellValue(bean.getMouseRepeller());
			cellIndex++;

			cell = row.createCell(cellIndex);
			cell.setCellValue(bean.getMoleRepeller());
			cellIndex++;

			cell = row.createCell(cellIndex);
			cell.setCellValue(bean.getSnakeRepeller());
			cellIndex++;

			cell = row.createCell(cellIndex);
			cell.setCellValue(bean.getUltrasonicDogRepeller());
			cellIndex++;

			cell = row.createCell(cellIndex);
			cell.setCellValue(bean.getAntidog());
			cellIndex++;

			cell = row.createCell(cellIndex);
			cell.setCellValue(bean.getThermacell());
			cellIndex++;

			cell = row.createCell(cellIndex);
			cell.setCellValue(bean.getMosquitoKiller());
			cellIndex++;

			cell = row.createCell(cellIndex);
			cell.setCellValue(bean.getKnifePoint());
			cellIndex++;

			cell = row.createCell(cellIndex);
			cell.setCellValue(bean.getBamboo());
			cellIndex++;

			cell = row.createCell(cellIndex);
			cell.setCellValue(bean.getAutoFeeder());
			cellIndex++;

			cell = row.createCell(cellIndex);
			cell.setCellValue(bean.getCarku());
			cellIndex++;

			cell = row.createCell(cellIndex);
			cell.setCellValue(bean.getPraktic());
			cellIndex++;

			cell = row.createCell(cellIndex);
			cell.setCellValue(bean.getTent());
			cellIndex++;

			cell = row.createCell(cellIndex);
			cell.setCellValue(0);
			cellIndex++;

			cell = row.createCell(cellIndex);
			cell.setCellValue(0);
			cellIndex++;
		}
		FileOutputStream outputStream = new FileOutputStream("d:\\src\\sir-richard-boss\\--1-save\\aggregate-sales.xls");
		workBook.write(outputStream);
		workBook.close();

	}

	public AggregateProductSalesReportBean aggregateProductSales(Pair<Date> period) {
		final String sqlSelectProductSales = "select oi.quantity quantity, oi.product_id product_id, min(o.category_product_id) category_product_id, min(p.sku) sku, min(p.name) name" +
				"  from sr_order_item oi, sr_v_order o, sr_v_product_light p" +
				"  where (oi.order_id = o.id)" +
				"    and (p.product_id = oi.product_id)" +
				"    and (o.order_date between ? and ?)" +
				"    and (o.status in (2,3,4,5,7,12,10,8))" +
				"    group by oi.quantity, oi.product_id";

		List<TempProductSalesReportBean> tempBeans = this.jdbcTemplate.query(sqlSelectProductSales,
				new Object[]{period.getStart(), period.getEnd()},
				new int[] {Types.DATE, Types.DATE },
				new RowMapper<TempProductSalesReportBean>() {
					@Override
					public TempProductSalesReportBean mapRow(ResultSet rs, int rowNum) throws SQLException {

						TempProductSalesReportBean bean = new TempProductSalesReportBean();
						bean.setCategoryProductId(rs.getInt("category_product_id"));
						bean.setProductId(rs.getInt("product_id"));
						bean.setSku(rs.getString("sku"));
						bean.setName(rs.getString("name"));
						bean.setQuantity(rs.getInt("quantity"));
						return bean;
					}
				});

/*
						102	отпугиватели	отпугиватели змей
						103	отпугиватели	отпугиватели птиц
						104	отпугиватели	отпугиватели грызунов
						105	отпугиватели	отпугиватели комаров
						107	отпугиватели	отпугиватели ос
						108	отпугиватели	отпугиватели собак
						109	отпугиватели	уничтожители насекомых
						110	отпугиватели	отпугиватели клещей
						201	для дома	gsm розетки и реле
						202	для дома	gsm сигнализации
						203	для дома	автономные извещатели
						204	для дома	видеоглазки и видеодомофоны
						205	для дома	видеонаблюдение
						206	для дома	ножеточки
						207	для дома	эконаборы
						208	для дома	светильники
						209	для дома	столики для ноутбука
						210	для дома	роботы для уборки
						211	для дома	средства защиты
						301	для автомобиля	алкотестеры
						302	для автомобиля	пуско-зарядные устройства
						303	для автомобиля	гибкие камеры
						304	для автомобиля	гаджеты
						401	для дачи	изотермика
						403	для дачи	мобильный душ
						404	для дачи	системы полива
						405	для дачи	термосы
						501	для детей	микроскопы USB
						502	для детей	домашние планетарии
						503	для детей	видеоняни
						504	для детей	конструкторы
						601	безопасность	антижучки
						602	безопасность	обнаружители видеокамер
						603	безопасность	подавители диктофонов
						604	безопасность	подавители сотовых телефонов
						701	путешествия	стельки с подогревом
						702	путешествия	возвращатели
						703	путешествия	мини электростанции
						801	музыка	наушники
						802	музыка	колонки
						901	для домашних животных	фурминатор для кошек
						902	для домашних животных	фурминатор для собак
						903	для домашних животных	автокормушки и автопоилки
						904	для домашних животных	электронные ошейники
						1111	производство	инкубаторы
						1101	прочие	элементы питания
						1102	прочие	инструменты
						1103	прочие	подарки
						1104	прочие	для рыбалки
						1105	прочие	для охоты
						1106	прочие	фонари

 */


		AggregateProductSalesReportBean bean = new AggregateProductSalesReportBean();
		bean.getPeriod().setStart(period.getStart());
		bean.getPeriod().setEnd(period.getEnd());


		//isocket	sapsan	sanseit	эланг	эланг реле	IQsocket Mobile	Телеметрика Т80	Телеметрика Т60
		// итого	сититек gsm	сититек i8	сититек eye	остальные	итого	gsm сигнализации	usb микроскоп	планетарий	антижучки
		// отпугиватели птиц	отпугиватели грызунов	отпугиватель кротов	отпугиватель змей	отпугиватель собак	антидог
		// ThermaCELL	уничтожители комаров	экотестеры
		// ножеточки	столики для ноутбука
		// автокормушки	пуско-зарядные устройства CARKU	эхолоты	иные



		tempBeans.forEach(tempBean -> {
			if (tempBean.getSku().equalsIgnoreCase("TELEMETRIKA-T80-MASTER")) {
				bean.setTelemetrikaT80(tempBean.getQuantity());
			} else if (tempBean.getSku().equalsIgnoreCase("TELEMETRIKA-T60-SLAVE")) {
				bean.setTelemetrikaT60(tempBean.getQuantity());
			} else if (tempBean.getSku().equalsIgnoreCase("ELANG-RELE-POWERCONTROL-PRO")) {
				bean.setElang(bean.getElang() + tempBean.getQuantity());
			} else if (tempBean.getSku().equalsIgnoreCase("ELANG-RELE-POWERCONTROL")) {
				bean.setElang(bean.getElang() + tempBean.getQuantity());
			} else if (tempBean.getSku().equalsIgnoreCase("ELANG-RELE-THERMOCONTROL")) {
				bean.setElang(bean.getElang() + tempBean.getQuantity());
			} else if (tempBean.getSku().equalsIgnoreCase("ELANG-SOCKET")) {
				bean.setOtherGsmSocket(bean.getOtherGsmSocket() + tempBean.getQuantity());
			} else if (tempBean.getCategoryProductId() == 201) {
				bean.setOtherGsmSocket(bean.getOtherGsmSocket() + tempBean.getQuantity());
			} else if (tempBean.getCategoryProductId() == 202) {
				bean.setGsmAlarm(tempBean.getQuantity());
			} else if (tempBean.getCategoryProductId() == 204) {
				bean.setVideoEye(tempBean.getQuantity());
			} else if (tempBean.getCategoryProductId() == 502) {
				bean.setAstroEye(tempBean.getQuantity());
			} else if (tempBean.getCategoryProductId() == 601 || tempBean.getCategoryProductId() == 602 || tempBean.getCategoryProductId() == 603 || tempBean.getCategoryProductId() == 604) {
				bean.setBugHunter(tempBean.getQuantity());
			} else if (tempBean.getCategoryProductId() == 102) {
				bean.setSnakeRepeller(bean.getSnakeRepeller() + tempBean.getQuantity());
			} else if (tempBean.getCategoryProductId() == 103) {
				if (StringUtils.containsIgnoreCase(tempBean.getName(), "шипы ")) {
					bean.setBirdRepeller(bean.getBirdRepeller() + tempBean.getQuantity() / 10);
				} else {
					bean.setBirdRepeller(bean.getBirdRepeller() + tempBean.getQuantity());
				}
			} else if (tempBean.getCategoryProductId() == 104) {
				bean.setMouseRepeller(bean.getMouseRepeller() + tempBean.getQuantity());
			}  else if (tempBean.getCategoryProductId() == 108) {
				if (tempBean.getSku().equalsIgnoreCase("ANTIDOG")) {
					bean.setAntidog(bean.getAntidog() + tempBean.getQuantity());
				} else if (tempBean.getSku().equalsIgnoreCase("ANTIDOG-BLACK")) {
					bean.setAntidog(bean.getAntidog() + tempBean.getQuantity());
				} else if (tempBean.getSku().equalsIgnoreCase("ANTIDOG-WHITE")) {
					bean.setAntidog(bean.getAntidog() + tempBean.getQuantity());
				} else if (tempBean.getSku().equalsIgnoreCase("ANTIDOG-KOMPLEKT")) {
					bean.setAntidog(bean.getAntidog() + tempBean.getQuantity());
				} else if (tempBean.getSku().equalsIgnoreCase("ANTIDOG-M")) {
					bean.setAntidog(bean.getAntidog() + tempBean.getQuantity());
				} else if (tempBean.getSku().equalsIgnoreCase("PYRODEFENDER")) {
					bean.setAntidog(bean.getAntidog() + tempBean.getQuantity());
				} else if (StringUtils.containsIgnoreCase(tempBean.getSku(), "PYRODEFENDER-KOMPLEKT")) {
					bean.setAntidog(bean.getAntidog() + tempBean.getQuantity());
				} else if (StringUtils.containsIgnoreCase(tempBean.getName(), "чехол")) {
					//
				} else if (StringUtils.containsIgnoreCase(tempBean.getName(), "упаковка")) {
					//
				} else {
					bean.setUltrasonicDogRepeller(bean.getUltrasonicDogRepeller() + tempBean.getQuantity());
				}
			} else if (tempBean.getCategoryProductId() == 206) {
				bean.setKnifePoint(bean.getKnifePoint() + tempBean.getQuantity());
			} else if (tempBean.getCategoryProductId() == 209) {
				bean.setBamboo(bean.getBamboo() + tempBean.getQuantity());
			} else if (tempBean.getCategoryProductId() == 302) {
				bean.setCarku(bean.getCarku() + tempBean.getQuantity());
			} else if (tempBean.getCategoryProductId() == 1104) {
				if (StringUtils.contains(tempBean.getName(), "палатк")) {
					bean.setTent(bean.getTent() + tempBean.getQuantity());
				} else if (StringUtils.containsIgnoreCase(tempBean.getName(), "эхолот")) {
					bean.setPraktic(bean.getPraktic() + tempBean.getQuantity());
				} else if (StringUtils.containsIgnoreCase(tempBean.getName(), "камера")) {
					bean.setPraktic(bean.getPraktic() + tempBean.getQuantity());
				} else {
					//
				}
			} else {
				bean.setOthers(tempBean.getQuantity());
			}
		});
		return bean;
	}
	
	public List<ProductSalesReportBean> productSales(Pair<Date> period) {
		log.debug("productSales():{}", period);
		final String sqlSelectProductSales = "SELECT p.product_id, MAX(p.name) product_name, MAX(p.category_annotation) category_annotation, SUM(oi.quantity) quantity, SUM(oi.amount) amount" + 
				"		  FROM sr_v_order o, sr_order_item oi, sr_v_product p" + 
				"		  WHERE (o.id = oi.order_id) AND" + 
				"		        (oi.product_id = p.product_id) AND" + 
				"		        (o.order_date between ? and ?) AND" + 
				"		        (o.status IN (2, 4, 5, 7, 12, 8, 10)) AND"	+ 
				"               (o.order_type in (1, 2))" +
				"		  GROUP BY category_annotation, p.product_id";
		
		/*
		APPROVED(2, "подтвержден", ""),	// margin > 0, postpay > 0
		PAY_WAITING(3, "ожидаем оплату", "warning"), // margin = 0, postpay = ?
		PAY_ON(4, "оплата поступила", "warning"), // margin > 0, postpay = ?		
		DELIVERING(5, "доставляется", ""), // margin > 0, postpay > 0
		READY_GIVE_AWAY(7, "прибыл", ""), // margin > 0, postpay > 0
		READY_GIVE_AWAY_TROUBLE(12, "заканчивается срок хранения", "danger"), // margin > 0, postpay > 0
		DELIVERED(10, "получен", ""), 
		DOC_NOT_EXIST(11, "нет ТОРГ-12", ""), // margin > 0, postpay = 0
		FINISHED(8, "завершен", "success"), // margin > 0, postpay = 0	
		REDELIVERY(9, "отказ от вручения", "secondary"), // margin = 0, postpay > 0
		CANCELED(13, "отменен", "danger"), // margin = 0, postpay = 0
		REDELIVERY_FINISHED(15, "возврат получен", "danger"), // --
		LOST(16, "утерян", "lost"); // margin = 0, postpay = 0
		*/
		List<ProductSalesReportBean> beans = this.jdbcTemplate.query(sqlSelectProductSales,
				new Object[]{period.getStart(), period.getEnd()},
				new int[] {Types.DATE, Types.DATE },
				new RowMapper<ProductSalesReportBean>() {
					@Override
		            public ProductSalesReportBean mapRow(ResultSet rs, int rowNum) throws SQLException {		            	
		            	ProductSalesReportBean bean = new ProductSalesReportBean();
		            	bean.setProduct(wikiDao.getProductById(rs.getInt("PRODUCT_ID")));
		            	bean.setQuantity(rs.getInt("QUANTITY"));
		            	bean.setAmount(rs.getBigDecimal("AMOUNT"));		            	
		                return bean;
		            }
		        });
		return beans;		
	}

	public List<ProductSalesReportBean> productSalesByQuery(ProductSalesReportConditions conditions) {
		
		boolean isDiscretCondition = true;
		
		OrderStatuses fixedStatus;
		if (StringUtils.isNotEmpty(conditions.getIdsPaymentTypes()) && conditions.getIdsPaymentTypes().equals(String.valueOf(PaymentTypes.YANDEX_PAY.getId()))) {
			// это оплата банковской картой - завершено (могут быть курьерка у которой завершено есть, а доставлено - нет)
			fixedStatus = OrderStatuses.FINISHED;
			
		} else {
			// все остальное - доставлено
			fixedStatus = OrderStatuses.DELIVERED;
		}
		
		String sqlSelectProductSales = "SELECT p.product_id, MAX(p.name) product_name, MAX(p.category_annotation) category_annotation, SUM(oi.quantity) quantity, SUM(oi.amount) amount"
		  + " FROM sr_v_order o, sr_order_item oi, sr_v_product p"
		  + " WHERE (o.id = oi.order_id) AND"
		  + "       (oi.product_id = p.product_id) AND"
		  + "       (o.id in (select order_id from sr_order_status os WHERE (os.date_added between ? and ?) AND os.status in (" + fixedStatus.getId() + "))) ";
				
		if (StringUtils.isNotEmpty(conditions.getIdsDeliveryTypes())) {			
			if (isDiscretCondition) {
				sqlSelectProductSales += " AND ";
			}
			sqlSelectProductSales += "(delivery_type in (" + conditions.getIdsDeliveryTypes() + "))";
			isDiscretCondition = true;
		}		
		if (StringUtils.isNotEmpty(conditions.getIdsCustomerTypes())) {			
			if (isDiscretCondition) {
				sqlSelectProductSales += " AND ";
			}
			sqlSelectProductSales += "(customer_type in (" + conditions.getIdsCustomerTypes() + "))";
			isDiscretCondition = true;
		}		
		if (StringUtils.isNotEmpty(conditions.getIdsPaymentTypes())) {			
			if (isDiscretCondition) {
				sqlSelectProductSales += " AND ";
			}
			sqlSelectProductSales += "(payment_type in (" + conditions.getIdsPaymentTypes() + "))";
			isDiscretCondition = true;
		}		
		if (StringUtils.isNotEmpty(conditions.getIdsAdvertTypes())) {			
			if (isDiscretCondition) {
				sqlSelectProductSales += " AND ";
			}
			sqlSelectProductSales += "(advert_type in (" + conditions.getIdsAdvertTypes() + "))";
			isDiscretCondition = true;
		}		
		sqlSelectProductSales += " GROUP BY category_annotation, p.product_id";
		
		log.debug("productSalesByQuery sql: {}", sqlSelectProductSales);
		List<ProductSalesReportBean> beans = this.jdbcTemplate.query(sqlSelectProductSales,
				new Object[] { conditions.getPeriod().getStart(), conditions.getPeriod().getEnd() },
				new int[] { Types.DATE, Types.DATE },
				new RowMapper<ProductSalesReportBean>() {
					@Override
		            public ProductSalesReportBean mapRow(ResultSet rs, int rowNum) throws SQLException {		            	
		            	ProductSalesReportBean bean = new ProductSalesReportBean();
		            	bean.setProduct(wikiDao.getProductById(rs.getInt("PRODUCT_ID")));
		            	bean.setQuantity(rs.getInt("QUANTITY"));
		            	bean.setAmount(rs.getBigDecimal("AMOUNT"));		            	
		                return bean;
		            }
		        });
		return beans;	
	}
	
	public List<ProductSalesReportBean> productSalesByQueryName(ProductSalesReportConditions conditions) {

		String conditionsIdsDeliveryTypes = "";
		String conditionsIdsCustomerTypes = "";
		String conditionsIdsPaymentTypes = "";
		String conditionsIdsAdvertTypes = "";
		String conditionsFixedIdsStatuses = OrderStatuses.convertValuesToSplitedString(OrderStatuses.DELIVERED); 		

		if (conditions.getQueryName() == ReportQueryNames.YOOKASSA_PREPAYMENT) {
			
			// это оплата банковской картой - завершено (могут быть курьерка у которой завершено есть, а доставлено - нет)			
			conditionsIdsCustomerTypes =  CustomerTypes.convertValuesToSplitedString(CustomerTypes.CUSTOMER, CustomerTypes.FOREIGNER_CUSTOMER);
			conditionsIdsPaymentTypes = PaymentTypes.convertValuesToSplitedString(PaymentTypes.YANDEX_PAY);			
			conditionsFixedIdsStatuses = OrderStatuses.convertValuesToSplitedString(OrderStatuses.DELIVERED, OrderStatuses.FINISHED);
		} else if (conditions.getQueryName() == ReportQueryNames.CDEK_POSTPAYMENT) {
			
			conditionsIdsDeliveryTypes = DeliveryTypes.convertValuesToSplitedString(DeliveryTypes.CDEK_COURIER, 
					DeliveryTypes.CDEK_COURIER_ECONOMY, 
					DeliveryTypes.CDEK_PVZ_TYPICAL,
					DeliveryTypes.CDEK_PVZ_ECONOMY,
					DeliveryTypes.PICKUP);
			conditionsIdsCustomerTypes =  CustomerTypes.convertValuesToSplitedString(CustomerTypes.CUSTOMER, CustomerTypes.FOREIGNER_CUSTOMER);			
			conditionsIdsPaymentTypes = PaymentTypes.convertValuesToSplitedString(PaymentTypes.POSTPAY);
		} else if (conditions.getQueryName() == ReportQueryNames.OZON_ROCKET_POSTPAYMENT) {
			
			conditionsIdsDeliveryTypes = DeliveryTypes.convertValuesToSplitedString(DeliveryTypes.OZON_ROCKET_COURIER, 
					DeliveryTypes.OZON_ROCKET_POSTAMAT, 
					DeliveryTypes.OZON_ROCKET_PICKPOINT);			
			conditionsIdsCustomerTypes =  CustomerTypes.convertValuesToSplitedString(CustomerTypes.CUSTOMER, CustomerTypes.FOREIGNER_CUSTOMER);
			conditionsIdsPaymentTypes = PaymentTypes.convertValuesToSplitedString(PaymentTypes.POSTPAY);
		} else if (conditions.getQueryName() == ReportQueryNames.OZON) {
			
			conditionsIdsAdvertTypes = OrderAdvertTypes.convertValuesToSplitedString(OrderAdvertTypes.OZON);
		} else if (conditions.getQueryName() == ReportQueryNames.YANDEX_MARKET) {
			
			conditionsIdsAdvertTypes = OrderAdvertTypes.convertValuesToSplitedString(OrderAdvertTypes.YANDEX_MARKET);
		}
		boolean isDiscretCondition = true;		
		String sqlSelectProductSales = "SELECT p.product_id, MAX(p.name) product_name, MAX(p.category_annotation) category_annotation, SUM(oi.quantity) quantity, SUM(oi.amount) amount"
				  + " FROM sr_v_order o, sr_order_item oi, sr_v_product p"
				  + " WHERE (o.id = oi.order_id) AND"
				  + "       (oi.product_id = p.product_id) AND"
				  + "       (o.id in (select order_id from sr_order_status os WHERE (os.date_added between ? and ?) AND os.status in (" + conditionsFixedIdsStatuses + "))) ";
						
		if (StringUtils.isNotEmpty(conditionsIdsDeliveryTypes)) {			
			if (isDiscretCondition) {
				sqlSelectProductSales += " AND ";
			}
			sqlSelectProductSales += "(delivery_type in (" + conditionsIdsDeliveryTypes + "))";
			isDiscretCondition = true;
		}
		if (StringUtils.isNotEmpty(conditionsIdsCustomerTypes)) {			
			if (isDiscretCondition) {
				sqlSelectProductSales += " AND ";
			}
			sqlSelectProductSales += "(customer_type in (" + conditionsIdsCustomerTypes + "))";
			isDiscretCondition = true;
		}		
		if (StringUtils.isNotEmpty(conditionsIdsPaymentTypes)) {			
			if (isDiscretCondition) {
				sqlSelectProductSales += " AND ";
			}
			sqlSelectProductSales += "(payment_type in (" + conditionsIdsPaymentTypes + "))";
			isDiscretCondition = true;
		}		
		if (StringUtils.isNotEmpty(conditionsIdsAdvertTypes)) {			
			if (isDiscretCondition) {
				sqlSelectProductSales += " AND ";
			}
			sqlSelectProductSales += "(advert_type in (" + conditionsIdsAdvertTypes + "))";
			isDiscretCondition = true;
		}		
		sqlSelectProductSales += " GROUP BY category_annotation, p.product_id";
				
		log.debug("productSalesByQueryName sql: {}", sqlSelectProductSales);
		List<ProductSalesReportBean> beans = this.jdbcTemplate.query(sqlSelectProductSales,
				new Object[]{conditions.getPeriod().getStart(), conditions.getPeriod().getEnd()},
				new int[] {Types.DATE, Types.DATE },
				new RowMapper<ProductSalesReportBean>() {
					@Override
		            public ProductSalesReportBean mapRow(ResultSet rs, int rowNum) throws SQLException {		            	
		            	ProductSalesReportBean bean = new ProductSalesReportBean();
		            	bean.setProduct(wikiDao.getProductById(rs.getInt("PRODUCT_ID")));
		            	bean.setQuantity(rs.getInt("QUANTITY"));
		            	bean.setAmount(rs.getBigDecimal("AMOUNT"));		            	
		                return bean;
		            }
		        });
		return beans;			
	}	
	
	public List<SalesFunnelReportBean> salesFunnel(Pair<Date> period) {
		
		SalesFunnelReportBean bean = new SalesFunnelReportBean();
		
		BigDecimal advertBudget = wikiDao.ejectTotalAmountsByConditions(OrderAmountTypes.ADVERT_BUDGET, period);
		int visits = wikiDao.ejectTotalAmountsByConditions(OrderAmountTypes.COUNT_VISITS, period).intValue();
		int uniqueVisitors = wikiDao.ejectTotalAmountsByConditions(OrderAmountTypes.COUNT_UNIQUE_VISITORS, period).intValue();
		int newVisitors = wikiDao.ejectTotalAmountsByConditions(OrderAmountTypes.COUNT_NEW_VISITORS, period).intValue();
					
		bean.setAdvertBudget(advertBudget);
		bean.setVisits(visits);
		bean.setUniqueVisitors(uniqueVisitors);
		bean.setNewVisitors(newVisitors);
		
		OrderConditions orderConditions = new OrderConditions(ReportPeriodTypes.ANY_MONTH);
		orderConditions.setPeriod(period);
		List<Order> orders = orderDao.listOrdersByConditions(orderConditions);
		
		// LEADS
		int siteSourceLeads = 0;
		int emailSourceLeads = 0;
		int callSourceLeads = 0;
		int chatSourceLeads = 0;
		int othersSourceLeads = 0;	
		
		int paidChannelLeads = 0;
		int organicChannelLeads = 0;
		int socialNetworkChannelLeads = 0;
		int directChannelLeads = 0;
		int othersChannelLeads = 0;
		int yandexMarketChannelLeads = 0;
		int ozonMarketChannelLeads = 0;
		
		int orderTypeLeads = 0;
		int billTypeLeads = 0;
		int kpTypeLeads = 0;
		int consultationTypeLeads = 0;
		int othersTypeLeads = 0;
		int refundTypeLeads = 0;
		
		int newLeads = 0;
		int repeatLeads = 0;
		
		int personLeads = 0;
		int companyLeads = 0;
		
		Set<ProductCategory> categories = new HashSet<ProductCategory>();
		
		int totalOrders = 0;
		int personTotalOrders = 0;
		int marketPlaceTotalOrders = 0;
		
		int companyTotalOrders = 0;
						
		BigDecimal totalAmount = BigDecimal.ZERO;
		BigDecimal totalSupplierAmount = BigDecimal.ZERO;
		BigDecimal totalMarginAmount = BigDecimal.ZERO;
		
		BigDecimal personOrdersAmount = BigDecimal.ZERO;
		BigDecimal marketPlaceOrdersAmount = BigDecimal.ZERO;
		BigDecimal companyOrdersAmount = BigDecimal.ZERO;
				
		int myselfDeliveryOrders = 0;
		int courierServiceDeliveryOrders = 0;
		int postServiceDeliveryOrders = 0;
		int marketPlaceDeliveryOrders = 0;
				
		int cdekServiceDeliveryOrders = 0;
		int dellinServiceDeliveryOrders = 0;
		
		int yandexGoDeliveryOrders = 0;
		int ozonRocketDeliveryOrders = 0;
									
		BigDecimal myselfDeliveryOrdersAmount = BigDecimal.ZERO;
		BigDecimal courierServiceDeliveryOrdersAmount = BigDecimal.ZERO;
		BigDecimal postServiceDeliveryOrdersAmount = BigDecimal.ZERO;		
		BigDecimal marketPlaceDeliveryOrdersAmount = BigDecimal.ZERO;
				
		BigDecimal courierServiceDeliveryOrdersCost = BigDecimal.ZERO;
		BigDecimal postServiceDeliveryOrdersCost = BigDecimal.ZERO;
		BigDecimal marketPlaceDeliveryOrdersCost = BigDecimal.ZERO;
		
		BigDecimal personMarginAmount = BigDecimal.ZERO;
		BigDecimal marketPlaceMarginAmount = BigDecimal.ZERO;
		BigDecimal companyMarginAmount = BigDecimal.ZERO;
		
		for (Order order : orders) {
			// source
			if (order.getSourceType() == OrderSourceTypes.LID || order.getSourceType() == OrderSourceTypes.FAST_LID) {
				siteSourceLeads++;
			} else if (order.getSourceType() == OrderSourceTypes.EMAIL) {
				emailSourceLeads++;
			} else if (order.getSourceType() == OrderSourceTypes.CALL || order.getSourceType() == OrderSourceTypes.CALL_BACK) {
				callSourceLeads++;
			} else if (order.getSourceType() == OrderSourceTypes.CHAT) {
				chatSourceLeads++;
			} else {
				othersSourceLeads++;
			}
			
			// channel
			if (order.getAdvertType() == OrderAdvertTypes.ADVERT) {
				paidChannelLeads++;
			} else if (order.getAdvertType() == OrderAdvertTypes.CONTEXT) {
				organicChannelLeads++;
			} else if (order.getAdvertType() == OrderAdvertTypes.YOUTUBE) {
				socialNetworkChannelLeads++;
			} else if (order.getAdvertType() == OrderAdvertTypes.REPEAT_CALL) {
				directChannelLeads++;
			} else if (order.getAdvertType() == OrderAdvertTypes.YANDEX_MARKET) {
				yandexMarketChannelLeads++;
			} else if (order.getAdvertType() == OrderAdvertTypes.OZON) {
				ozonMarketChannelLeads++;
			} else {
				othersChannelLeads++;
			}
			
			if (order.getOrderType() == OrderTypes.ORDER) {
				orderTypeLeads++;
			} else if (order.getOrderType() == OrderTypes.BILL) {
				billTypeLeads++;
			} else if (order.getOrderType() == OrderTypes.KP) {
				kpTypeLeads++;
			} else if (order.getOrderType() == OrderTypes.CONSULTATION) {
				consultationTypeLeads++;
			} else if (order.getOrderType() == OrderTypes.REFUND) {
				refundTypeLeads++;
			} else {
				othersTypeLeads++;
			}			
			if (order.getAdvertType() == OrderAdvertTypes.REPEAT_CALL) {
				repeatLeads++;
			} else if (order.getAdvertType() == OrderAdvertTypes.LOYALTY) {
				repeatLeads++;
			} else {
				newLeads++;
			}			
			categories.add(order.getProductCategory());
						
			if (order.isBillAmount()) {
				totalOrders++;
				totalAmount = totalAmount.add(order.getAmounts().getBill());
				totalSupplierAmount = totalSupplierAmount.add(order.getAmounts().getSupplier());
				totalMarginAmount = totalMarginAmount.add(order.getAmounts().getMargin());
								
				
				if (order.getAdvertType() == OrderAdvertTypes.CDEK_MARKET || order.getAdvertType() == OrderAdvertTypes.OZON || order.getAdvertType() == OrderAdvertTypes.YANDEX_MARKET) {
					marketPlaceTotalOrders++;
					marketPlaceOrdersAmount = marketPlaceOrdersAmount.add(order.getAmounts().getBill());
					marketPlaceMarginAmount = marketPlaceMarginAmount.add(order.getAmounts().getMargin()); 
					
				} else if (order.getCustomer().isPerson()) {
					personTotalOrders++;
					personOrdersAmount = personOrdersAmount.add(order.getAmounts().getBill());
					personMarginAmount = personMarginAmount.add(order.getAmounts().getMargin()); 
					
				} else {
					companyTotalOrders++;
					companyOrdersAmount = companyOrdersAmount.add(order.getAmounts().getBill());
					companyMarginAmount = companyMarginAmount.add(order.getAmounts().getMargin());
				}				 
				
				if (order.getDelivery().getDeliveryType().isCourier()) {
					myselfDeliveryOrders++;
					myselfDeliveryOrdersAmount = myselfDeliveryOrdersAmount.add(order.getAmounts().getBill());	
				} else if (order.getDelivery().getDeliveryType().isCdek() || (order.getDelivery().getDeliveryType() == DeliveryTypes.DELLIN) || order.getDelivery().getDeliveryType().isOzonRocket() || order.getDelivery().getDeliveryType() == DeliveryTypes.YANDEX_GO) {
					courierServiceDeliveryOrders++;
					courierServiceDeliveryOrdersAmount = courierServiceDeliveryOrdersAmount.add(order.getAmounts().getBill());
					// расходы (транспорт + ккм + налоги) = доход - маржа - закупка					
					BigDecimal iCourierServiceDeliveryOrdersCost = order.getAmounts().getTotalWithDelivery().subtract(order.getAmounts().getMargin()).subtract(order.getAmounts().getSupplier()); 
					courierServiceDeliveryOrdersCost = courierServiceDeliveryOrdersCost.add(iCourierServiceDeliveryOrdersCost);
				} else if (order.getDelivery().getDeliveryType().isPost()) {
					postServiceDeliveryOrders++;
					postServiceDeliveryOrdersAmount = postServiceDeliveryOrdersAmount.add(order.getAmounts().getBill());
					postServiceDeliveryOrdersCost = postServiceDeliveryOrdersCost.add(order.getDelivery().getPrice());
				} else if (order.getDelivery().getDeliveryType() == DeliveryTypes.YANDEX_MARKET_FBS || order.getDelivery().getDeliveryType() == DeliveryTypes.OZON_FBS) {
					marketPlaceDeliveryOrders++;
					marketPlaceDeliveryOrdersAmount = marketPlaceDeliveryOrdersAmount.add(order.getAmounts().getBill());
					marketPlaceDeliveryOrdersCost = marketPlaceDeliveryOrdersCost.add(order.getDelivery().getPrice());
				} else {
					courierServiceDeliveryOrders++;
					courierServiceDeliveryOrdersAmount = courierServiceDeliveryOrdersAmount.add(order.getAmounts().getBill());
					courierServiceDeliveryOrdersCost = courierServiceDeliveryOrdersCost.add(order.getDelivery().getPrice());
				}
				
				if (order.getDelivery().getDeliveryType().isCdek()) {
					cdekServiceDeliveryOrders++;
				} else if (order.getDelivery().getDeliveryType() == DeliveryTypes.DELLIN) {
					dellinServiceDeliveryOrders++;
				} else if (order.getDelivery().getDeliveryType().isOzonRocket()) {
					ozonRocketDeliveryOrders++;
				} else if (order.getDelivery().getDeliveryType() == DeliveryTypes.YANDEX_GO) {
					yandexGoDeliveryOrders++;
				} else {
					// ?
				}				
			}
			
			if (order.getCustomer().isPerson()) {
				personLeads++;
			} else {
				companyLeads++;
			}
			
		}
		bean.setSiteSourceLeads(siteSourceLeads);
		bean.setEmailSourceLeads(emailSourceLeads);
		bean.setCallSourceLeads(callSourceLeads);
		bean.setChatSourceLeads(chatSourceLeads);
		bean.setOthersSourceLeads(othersSourceLeads);
		
		bean.setPaidChannelLeads(paidChannelLeads);
		bean.setOrganicChannelLeads(organicChannelLeads);
		bean.setSocialNetworkChannelLeads(socialNetworkChannelLeads);
		bean.setDirectChannelLeads(directChannelLeads);
		bean.setYandexMarketChannelLeads(yandexMarketChannelLeads);
		bean.setOzonMarketChannelLeads(ozonMarketChannelLeads);		
		bean.setOthersChannelLeads(othersChannelLeads);
		
		bean.setOrderTypeLeads(orderTypeLeads);
		bean.setBillTypeLeads(billTypeLeads);
		bean.setKpTypeLeads(kpTypeLeads);
		bean.setConsultationTypeLeads(consultationTypeLeads);
		bean.setRefundTypeLeads(refundTypeLeads);
		bean.setOthersTypeLeads(othersTypeLeads);

		bean.setNewLeads(newLeads);
		bean.setRepeatLeads(repeatLeads);
		
		bean.setPersonLeads(personLeads);
		bean.setCompanyLeads(companyLeads);
		
		bean.setTotalOrders(totalOrders);
		bean.setTotalAmount(totalAmount);
		bean.setTotalSupplierAmount(totalSupplierAmount);
		
		totalMarginAmount = totalMarginAmount.subtract(advertBudget);
		bean.setTotalMarginAmount(totalMarginAmount);
		
		bean.setMyselfDeliveryOrders(myselfDeliveryOrders);
		bean.setCourierServiceDeliveryOrders(courierServiceDeliveryOrders);
		bean.setPostServiceDeliveryOrders(postServiceDeliveryOrders);
		bean.setMarketPlaceDeliveryOrders(marketPlaceDeliveryOrders);
				
		bean.setCdekDeliveryOrders(cdekServiceDeliveryOrders);
		bean.setDellinDeliveryOrders(dellinServiceDeliveryOrders);
		
		bean.setOzonRocketDeliveryOrders(ozonRocketDeliveryOrders);
		bean.setYandexGoDeliveryOrders(yandexGoDeliveryOrders);	
				
		bean.setMyselfDeliveryOrdersAmount(myselfDeliveryOrdersAmount);
		bean.setCourierServiceDeliveryOrdersAmount(courierServiceDeliveryOrdersAmount);
		bean.setPostServiceDeliveryOrdersAmount(postServiceDeliveryOrdersAmount);
		bean.setMarketPlaceDeliveryOrdersAmount(marketPlaceDeliveryOrdersAmount);
				
		bean.setCourierServiceDeliveryOrdersCost(courierServiceDeliveryOrdersCost);
		bean.setPostServiceDeliveryOrdersCost(postServiceDeliveryOrdersCost);
		bean.setMarketPlaceDeliveryOrdersCost(marketPlaceDeliveryOrdersCost);		
		
		bean.setPersonTotalOrders(personTotalOrders);
		bean.setMarketPlaceTotalOrders(marketPlaceTotalOrders);
		bean.setCompanyTotalOrders(companyTotalOrders);
		
		bean.setPersonOrdersAmount(personOrdersAmount);
		bean.setMarketPlaceOrdersAmount(marketPlaceOrdersAmount);		
		bean.setCompanyOrdersAmount(companyOrdersAmount);
		
		bean.setPersonMarginAmount(personMarginAmount);
		bean.setMarketPlaceMarginAmount(marketPlaceMarginAmount);
		
		bean.setCompanyMarginAmount(companyMarginAmount);
		
		Map<OrderAmountTypes, BigDecimal> totalAmounts = orderDao.calcTotalOrdersAmountsByConditions(orders, period);
		bean.setPostpayAmount(totalAmounts.get(OrderAmountTypes.POSTPAY));
		
		SupplierStock stock = wikiDao.getSupplierStocks();
		bean.setStockAmount(stock.getTotalSupplierAmount());
		
		// расчет средней ставки для рекламы
		// ставка = (прибыль/2) / число кликов 
									
		
		/*		 
				(101, "рекламный бюджет за период"),
				(102, "число кликов за период"),
				(107, "число уникальных посетителей за период"),
				(111, "число новых посетителей за период"),
				
		сеансы
			organic search органика
			paid платный трафик
			social network социальные сети
			direct прямые переходы
			others прочие
			
		+ новые посетители
		+ уникальные посетители		
			
		>>>>> сеансы		
				+ сеансы за период ...
					+ стоимость сеанса = рекламный бюджет / число сеансов
					
		>>>>> уники			 
				+ уники за период ....
					+ стоимость посетителя = рекламный бюджет / число уников
					
		>>>>> новые посетители			 
				+ новые посетители за период ....
				+ % новых = новые / уникам
				
		>>>>> повторно пришли посетители			 
				+ постоянные клиенты за период = уники - новые
				+ % постоянных = постоянные / уникам		
								
		>>>>> заявки		
				+ лиды за период ....
											
					источник
						+ лид с сайта
						+ письмо
						+ звонок
						+ чат
						+ прочие
										
					канал
						+ реклама
						+ поиск
						+ ютуб
						+ повторное обращение
						+ прочие
						
					тип
						+ заказ
						+ счет
						+ кп
						+ консультация
						+ прочие	
						
					+ новые...
					+ повторное обращение ....	
						
					+ категории			
								
				+ конверсия = число лидов / число уников
				              число лидов / число посещений
				+ стоимость лида = рекламный бюджет / число лидов	
						
		>>>>> заказы								
				+ заказы за период ...
				+ конверсия = число заказов / число лидов
				
		>>>>> деньги		
				+ суммарный доход
				+ средний чек	= суммарный доход / число заказов
				
				+ суммарная закупка
				+ суммарная прибыль = доход - закупку				
				+ средняя прибыль = суммарная прибыль / число заказов  
		*/	
		
		List<SalesFunnelReportBean> results = new ArrayList<>();
		results.add(bean);
		return results;
	}



}
