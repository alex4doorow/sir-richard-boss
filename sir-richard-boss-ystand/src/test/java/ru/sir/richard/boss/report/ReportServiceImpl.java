package ru.sir.richard.boss.report;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import ru.sir.richard.boss.dao.ConfigDao;
import ru.sir.richard.boss.dao.OrderDao;
import ru.sir.richard.boss.model.data.Order;

@Service("reportService")
public class ReportServiceImpl implements ReportService {

	private final Logger logger = LoggerFactory.getLogger(ReportServiceImpl.class);
		
	@Autowired
	private OrderDao orderDao;
	
	@Autowired
	private ConfigDao configDao;
	
	@Override
	public void run() {

		testOrdersDelivery();
		/*
		testBill();
		testPostPostpay();
		testPostAddressTicketWithPostpay();		
		testBillSdek(3590);
		testBillSdek(3741);
		testBillSdek(3788);
		testKKM(3823);
		testGarantTicketAll(3788);
*/
	}
	
	private void testOrdersDelivery() {
		
		logger.debug("testOrdersDelivery():{}", "start");
		
		String masterReportFileName = "c:\\src\\sir-richard-boss\\sir-richard-boss-web\\x-resources\\jasperreports\\template\\report-orders-delivery-master-v1.jrxml";
		
		List<Order> ordersDeliverySending = new ArrayList<Order>();

		int orderId = 3422;
		Order one = orderDao.findById(orderId);
		ordersDeliverySending.add(one);
				
		JRBeanCollectionDataSource beanColDataSource = new JRBeanCollectionDataSource(ordersDeliverySending);
		try {
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.putAll(configDao.getConfig());
			JasperPrint jasperPrint = JasperFillManager.fillReport(masterReportFileName, parameters, beanColDataSource);
			File outDir = new File("c:\\src\\sir-richard-boss\\sir-richard-boss-web\\x-resources\\jasperreports\\out");
			outDir.mkdirs();

			// Export to PDF.
			JasperExportManager.exportReportToPdfFile(jasperPrint, "c:\\src\\sir-richard-boss\\sir-richard-boss-web\\x-resources\\jasperreports\\out\\report-orders-delivery.pdf");

		} catch (JRException e) {

			e.printStackTrace();
		}
		
		
		
		logger.debug("testOrdersDelivery():{}", "end");
		
	}
	
	private void testBill() {

		logger.debug("testBill():{}", "start");

		String masterReportFileName = "c:\\src\\sir-richard-boss\\sir-richard-boss-web\\x-resources\\jasperreports\\template\\bill-master-v1.jasper";
		//String masterReportFileName = "c:\\src\\sir-richard-boss\\sir-richard-boss-web\\src\\main\\webapp\\resources\\jasperreports\\bill-master-v1.jasper";
		
		//String masterReportFileName = "c:\\src\\sir-richard-boss\\sir-richard-boss-web\\x-resources\\jasperreports\\template\\bill-master-v1.jrxml";
		//String subReportFileName = "c:\\src\\sir-richard-boss\\sir-richard-boss-web\\x-resources\\jasperreports\\template\\bill-slave-v1.jasper";
		
 
		List<Order> orders = new ArrayList<Order>();
				
		int orderId = 3422;
		Order one = orderDao.findById(orderId);
		//Order one = orderDao.findById(3625);

		orders.add(one);


		JRBeanCollectionDataSource beanColDataSource = new JRBeanCollectionDataSource(orders);
		try {
			
			//JasperReport jasperMasterReport = JasperCompileManager.compileReport(masterReportFileName);

			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.putAll(configDao.getConfig());
						
			//parameters.put("subreportParameter", subReportFileName);


			JasperPrint jasperPrint = JasperFillManager.fillReport(masterReportFileName, parameters, beanColDataSource);
			File outDir = new File("c:\\src\\sir-richard-boss\\sir-richard-boss-web\\x-resources\\jasperreports\\out");
			outDir.mkdirs();

			// Export to PDF.
			JasperExportManager.exportReportToPdfFile(jasperPrint, "c:\\src\\sir-richard-boss\\sir-richard-boss-web\\x-resources\\jasperreports\\out\\order-bill-" + orderId + ".pdf");

		} catch (JRException e) {

			e.printStackTrace();
		}
		logger.debug("testBill():{}", "finish");
	}
	
	private void testPostAddressTicketWithPostpay() {
		logger.debug("testPostAddressTicketWithPostpay():{}", "start");
		
		String reportFileName = "c:\\src\\sir-richard-boss\\sir-richard-boss-web\\x-resources\\jasperreports\\template\\post-address-ticket-v2.jasper";
		List<Order> orders = new ArrayList<Order>();
		
		int orderId = 3624;
		Order one = orderDao.findById(orderId);
		orders.add(one);

		JRBeanCollectionDataSource beanColDataSource = new JRBeanCollectionDataSource(orders);
		try {
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.putAll(configDao.getConfig());
	
			JasperPrint jasperPrint = JasperFillManager.fillReport(reportFileName, parameters, beanColDataSource);
			File outDir = new File("c:\\src\\sir-richard-boss\\sir-richard-boss-web\\x-resources\\jasperreports\\out");
			outDir.mkdirs();
			JasperExportManager.exportReportToPdfFile(jasperPrint, "c:\\src\\sir-richard-boss\\sir-richard-boss-web\\x-resources\\jasperreports\\out\\post-address-ticket-" + orderId + ".pdf");
		} catch (JRException e) {

			e.printStackTrace();
		}
		
		
		logger.debug("testPostAddressTicketWithPostpay():{}", "end");
				
	}
	
	private void testPostPostpay() {
		logger.debug("testPostPostpay():{}", "start");
		
		
		String reportFileName = "c:\\src\\sir-richard-boss\\sir-richard-boss-web\\x-resources\\jasperreports\\template\\post-postpay-v3.jasper";
		List<Order> orders = new ArrayList<Order>();
		
		int orderId = 3624;
		Order one = orderDao.findById(orderId);
		//Order one = orderDao.findById(3625);

		orders.add(one);


		JRBeanCollectionDataSource beanColDataSource = new JRBeanCollectionDataSource(orders);
		try {
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.putAll(configDao.getConfig());
	
			JasperPrint jasperPrint = JasperFillManager.fillReport(reportFileName, parameters, beanColDataSource);
			File outDir = new File("c:\\src\\sir-richard-boss\\sir-richard-boss-web\\x-resources\\jasperreports\\out");
			outDir.mkdirs();
			JasperExportManager.exportReportToPdfFile(jasperPrint, "c:\\src\\sir-richard-boss\\sir-richard-boss-web\\x-resources\\jasperreports\\out\\post-postpay-" + orderId + ".pdf");
		} catch (JRException e) {

			e.printStackTrace();
		}
		logger.debug("testPostPostpay():{}", "finish");		
	}
	
	
	private void testBillSdek(int orderId) {

		logger.debug("testBillSdek():{},{}", "start", orderId);

		String masterReportFileName = "c:\\src\\sir-richard-boss\\sir-richard-boss-web\\x-resources\\jasperreports\\template\\bill-sdek-master-v3.jasper";
		//String masterReportFileNameJ = "c:\\src\\sir-richard-boss\\sir-richard-boss-web\\x-resources\\jasperreports\\template\\bill-sdek-master-v1.jrxml";
	
		List<Order> orders = new ArrayList<Order>();
		
		Order one = orderDao.findById(orderId);
		//Object o = one.getDelivery().getCourierInfo();
		orders.add(one);
		orders.add(one);
		JRBeanCollectionDataSource beanColDataSource = new JRBeanCollectionDataSource(orders);
		try {
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.putAll(configDao.getConfig());	
			//JasperReport jasperMasterReport = JasperCompileManager.compileReport(masterReportFileNameJ);
			
			JasperPrint jasperPrint = JasperFillManager.fillReport(masterReportFileName, parameters, beanColDataSource);
			
			//JasperPrint jasperPrint = JasperFillManager.fillReport(jasperMasterReport, parameters, beanColDataSource);
			
			
			File outDir = new File("c:\\src\\sir-richard-boss\\sir-richard-boss-web\\x-resources\\jasperreports\\out");
			outDir.mkdirs();

			// Export to PDF.
			JasperExportManager.exportReportToPdfFile(jasperPrint, "c:\\src\\sir-richard-boss\\sir-richard-boss-web\\x-resources\\jasperreports\\out\\order-bill-sdek-" + orderId + ".pdf");

		} catch (JRException e) {

			e.printStackTrace();
		}
		logger.debug("testBillSdek():{}", "finish");		
	}

	private void testKKM(int orderId) {

		logger.debug("testKKM():{},{}", "start", orderId);
	
		String masterReportFileName = "c:\\src\\sir-richard-boss\\sir-richard-boss-web\\x-resources\\jasperreports\\template\\kkm-master-v1.jasper";
	
		List<Order> orders = new ArrayList<Order>();
		
		Order one = orderDao.findById(orderId);
		orders.add(one);
		JRBeanCollectionDataSource beanColDataSource = new JRBeanCollectionDataSource(orders);
		try {
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.putAll(configDao.getConfig());
			JasperPrint jasperPrint = JasperFillManager.fillReport(masterReportFileName, parameters, beanColDataSource);
	
			File outDir = new File("c:\\src\\sir-richard-boss\\sir-richard-boss-web\\x-resources\\jasperreports\\out");
			outDir.mkdirs();
	
			// Export to PDF.
			JasperExportManager.exportReportToPdfFile(jasperPrint, "c:\\src\\sir-richard-boss\\sir-richard-boss-web\\x-resources\\jasperreports\\out\\kkm-" + orderId + ".pdf");
	
		} catch (JRException e) {
	
			e.printStackTrace();
		}
		logger.debug("testKKM():{}", "finish");
	}
	
	private void testGarantTicketAll(int orderId) {

		logger.debug("testGarantTicketAll():{},{}", "start", orderId);

		String masterReportFileName = "c:\\src\\sir-richard-boss\\sir-richard-boss-web\\x-resources\\jasperreports\\template\\garant-ticket-all-master-v1.jasper";
	
		List<Order> orders = new ArrayList<Order>();
		
		Order one = orderDao.findById(orderId);
		orders.add(one);
		JRBeanCollectionDataSource beanColDataSource = new JRBeanCollectionDataSource(orders);
		try {
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.putAll(configDao.getConfig());	
			JasperPrint jasperPrint = JasperFillManager.fillReport(masterReportFileName, parameters, beanColDataSource);
			File outDir = new File("c:\\src\\sir-richard-boss\\sir-richard-boss-web\\x-resources\\jasperreports\\out");
			outDir.mkdirs();
			JasperExportManager.exportReportToPdfFile(jasperPrint, "c:\\src\\sir-richard-boss\\sir-richard-boss-web\\x-resources\\jasperreports\\out\\garant-ticket-all-" + orderId + ".pdf");
		} catch (JRException e) {
			e.printStackTrace();
		}
		logger.debug("testBillSdek():{}", "finish");		
	}
		
}







