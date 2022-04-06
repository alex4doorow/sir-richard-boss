package ru.sir.richard.boss.dao;

import java.util.Date;
import java.util.List;

import ru.sir.richard.boss.model.data.conditions.ProductSalesReportConditions;
import ru.sir.richard.boss.model.data.report.ProductSalesReportBean;
import ru.sir.richard.boss.model.data.report.SalesFunnelReportBean;
import ru.sir.richard.boss.model.utils.Pair;

public interface ReportDao {
	
	List<ProductSalesReportBean> productSales(Pair<Date> period);
	List<ProductSalesReportBean> productSalesByQuery(ProductSalesReportConditions conditions);
	List<SalesFunnelReportBean> salesFunnel(Pair<Date> period);

}
