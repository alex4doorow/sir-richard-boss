package ru.sir.richard.boss.report;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ru.sir.richard.boss.dao.OrderDao;
import ru.sir.richard.boss.model.data.Order;
import ru.sir.richard.boss.model.data.OrderItem;

@Service("cdekService")
public class CdekServiceImpl implements CdekService {

	private final Logger logger = LoggerFactory.getLogger(CdekServiceImpl.class);
	
	@Autowired
	private OrderDao orderDao;
	
	//@Autowired
	//private DeliveryService deliveryService;
	
	
	@Override
	public void run() {
		
		//List<Address> addresses = deliveryService.getCdekCities("Псков");
		int orderId;
		List<Order> orders = new ArrayList<Order>();
		try {	
			orderId = 3944;
			Order order = orderDao.findById(orderId);			
			orders.add(order);
			
			orderId = 3967;
			order = orderDao.findById(orderId);
			orders.add(order);
			
			writeIntoExcel(orders, "c:\\src\\sir-richard-boss\\sir-richard-boss-web\\x-resources\\cdek\\--out\\cdek-out-v2.xls");
		} catch (FileNotFoundException e) {
			logger.error(e.getMessage(), e);
			e.printStackTrace();
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
			e.printStackTrace();
		}

	}

	private void writeIntoExcel(List<Order> orders, String file) throws FileNotFoundException, IOException {
		
		logger.debug("writeIntoExcel():{}", "start");
		
		Workbook book = new HSSFWorkbook();
		Sheet sheet = book.createSheet("sheet-first");

		int rowIndex = 0;
		Row row = sheet.createRow(0);
		rowIndex++;

		Cell cell;
		int cellIndex = 0;
		
		// header
		cell = row.createCell(cellIndex);
		cell.setCellValue("Номер отправления");
		cellIndex++;

		cell = row.createCell(cellIndex);
		cell.setCellValue("Город получателя");
		cellIndex++;

		cell = row.createCell(cellIndex);
		cell.setCellValue("Индекс");
		cellIndex++;

		cell = row.createCell(cellIndex);
		cell.setCellValue("Получатель");
		cellIndex++;

		cell = row.createCell(cellIndex);
		cell.setCellValue("ФИО получателя");
		cellIndex++;

		cell = row.createCell(cellIndex);
		cell.setCellValue("Адрес получателя");
		cellIndex++;

		cell = row.createCell(cellIndex);
		cell.setCellValue("Код ПВЗ");
		cellIndex++;

		cell = row.createCell(cellIndex);
		cell.setCellValue("Телефон получателя");
		cellIndex++;

		cell = row.createCell(cellIndex);
		cell.setCellValue("Доп сбор за доставку с получателя в т.ч. НДС");
		cellIndex++;

		cell = row.createCell(cellIndex);
		cell.setCellValue("Ставка НДС с доп.сбора за доставку");
		cellIndex++;

		cell = row.createCell(cellIndex);
		cell.setCellValue("Сумма НДС с доп.сбора за доставку");
		cellIndex++;

		cell = row.createCell(cellIndex);
		cell.setCellValue("Истинный продавец");
		cellIndex++;

		cell = row.createCell(cellIndex);
		cell.setCellValue("Комментарий");
		cellIndex++;

		cell = row.createCell(cellIndex);
		cell.setCellValue("Порядковый номер места");
		cellIndex++;

		cell = row.createCell(cellIndex);
		cell.setCellValue("Вес места, кг");
		cellIndex++;

		cell = row.createCell(cellIndex);
		cell.setCellValue("Длина места, см");
		cellIndex++;

		cell = row.createCell(cellIndex);
		cell.setCellValue("Ширина места, см");
		cellIndex++;

		cell = row.createCell(cellIndex);
		cell.setCellValue("Высота места, см");
		cellIndex++;

		cell = row.createCell(cellIndex);
		cell.setCellValue("Описание места");
		cellIndex++;

		cell = row.createCell(cellIndex);
		cell.setCellValue("Код товара/артикул");
		cellIndex++;

		cell = row.createCell(cellIndex);
		cell.setCellValue("Наименование товара");
		cellIndex++;

		cell = row.createCell(cellIndex);
		cell.setCellValue("Стоимость единицы товара");
		cellIndex++;

		cell = row.createCell(cellIndex);
		cell.setCellValue("Оплата с получателя за ед товара в т.ч. НДС");
		cellIndex++;

		cell = row.createCell(cellIndex);
		cell.setCellValue("Вес товара, кг");
		cellIndex++;

		cell = row.createCell(cellIndex);
		cell.setCellValue("Количество, шт");
		cellIndex++;

		cell = row.createCell(cellIndex);
		cell.setCellValue("Ставка НДС, %");
		cellIndex++;

		cell = row.createCell(cellIndex);
		cell.setCellValue("Сумма НДС за ед.");
		cellIndex++;

		// order's row
		
		for (Order order : orders) {
			row = sheet.createRow(rowIndex);
			rowIndex++;
			
			cellIndex = 0;
			
			cell = row.createCell(cellIndex);
			cell.setCellValue(order.getNo());
			cellIndex++;

			String city = order.getDelivery().getAddress().getCity();			
			cell = row.createCell(cellIndex);
			cell.setCellValue(city);
			cellIndex++;
			
			cell = row.createCell(cellIndex);
			cell.setCellValue("");
			cellIndex++;
			
			cell = row.createCell(cellIndex);
			cell.setCellValue(order.getCustomer().getViewShortName());
			cellIndex++;
			
			cell = row.createCell(cellIndex);
			cell.setCellValue(order.getCustomer().getViewShortName());
			cellIndex++;
			
			String strretAddress = order.getDelivery().getAddress().getStreetAddress();			
			cell = row.createCell(cellIndex);
			cell.setCellValue(strretAddress);
			cellIndex++;
			
			String pvz = order.getDelivery().getAddress().getPvz();			
			cell = row.createCell(cellIndex);
			cell.setCellValue(pvz);
			cellIndex++;
			
			cell = row.createCell(cellIndex);
			cell.setCellValue(order.getCustomer().getViewPhoneNumber());
			cellIndex++;
			
			cell = row.createCell(cellIndex);			
			String strDeliveryAmount = order.getDelivery().getPrice() != null ? order.getDelivery().getPrice().toString() : "";  
			cell.setCellValue(strDeliveryAmount);
			cellIndex++;
			
			cell = row.createCell(cellIndex);
			cell.setCellValue("");
			cellIndex++;
			
			cell = row.createCell(cellIndex);
			cell.setCellValue("");
			cellIndex++;
			
			cell = row.createCell(cellIndex);
			cell.setCellValue("ИП Федоров А.А.");
			cellIndex++;
			
			cell = row.createCell(cellIndex);
			cell.setCellValue(order.getDelivery().getAnnotation());
			cellIndex++;
			
			cell = row.createCell(cellIndex);
			cell.setCellValue("1");
			cellIndex++;
			
			cell = row.createCell(cellIndex);
			cell.setCellValue("0,5");
			cellIndex++;
			
			cell = row.createCell(cellIndex);
			cell.setCellValue("10");
			cellIndex++;
			
			cell = row.createCell(cellIndex);
			cell.setCellValue("10");
			cellIndex++;
			
			cell = row.createCell(cellIndex);
			cell.setCellValue("10");
			cellIndex++;

			cell = row.createCell(cellIndex);
			cell.setCellValue("оборудование");
			cellIndex++;
			
			for (OrderItem orderItem : order.getItems()) {
			
				cell = row.createCell(cellIndex);
				cell.setCellValue(orderItem.getProduct().getSku());
				cellIndex++;
				
				cell = row.createCell(cellIndex);
				cell.setCellValue(orderItem.getProduct().getName());
				cellIndex++;
				
				cell = row.createCell(cellIndex);
				cell.setCellValue(orderItem.getPrice().toString());
				cellIndex++;
							
				cell = row.createCell(cellIndex);
				cell.setCellValue(orderItem.getPrice().toString());
				cellIndex++;
				
				cell = row.createCell(cellIndex);
				cell.setCellValue("0,01");
				cellIndex++;
				
				cell = row.createCell(cellIndex);
				cell.setCellValue(orderItem.getQuantity());
				cellIndex++;
				
				cell = row.createCell(cellIndex);
				cell.setCellValue("0");
				cellIndex++;
				
				cell = row.createCell(cellIndex);
				cell.setCellValue("0");
				cellIndex++;	
				
				break;				
			}			
		}
		// Записываем всё в файл
		book.write(new FileOutputStream(file));
		book.close();		
		logger.debug("writeIntoExcel():{}", "stop");
	}

}
