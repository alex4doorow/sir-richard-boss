package ru.sir.richard.boss.crm;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ru.sir.richard.boss.dao.AnyDaoImpl;

@Service
public class CrmManager extends AnyDaoImpl {
		
	private List<CrmExecutable> executors;
	
	private Date executorDate;
	
	@Autowired
	private OpencartExecutor opencartExecutor;
	
	@Autowired
	private EmailFastOrderExecutor emailFastOrderExecutor;
	
	@Autowired
	private OzonExecutor ozonExecutor;	
	
	public CrmManager() {
		
	}
	
	@PostConstruct
	public void init() {
		executors = new ArrayList<CrmExecutable>();		
		executors.add((CrmExecutable) opencartExecutor);		
		executors.add((CrmExecutable) emailFastOrderExecutor);		
		executors.add((CrmExecutable) ozonExecutor);
	}
	
	public Date getExecutorDate() {
		return executorDate;
	}
	
	public void setExecutorDate(Date executorDate) {
		this.executorDate = executorDate;
	}

	public void importRun() {
		for (CrmExecutable executor : executors) {
			executor.setExecutorDate(executorDate);
			executor.run();
		}		
	}
	
	/**
	 * Актуализации наложки от озона, сдэк, я.маркета
	 * сдэк - руками, нет реестра на дату
	 * я.маркет - ежедневные выплаты
	 * озон - алгоритм:
	 * завершить все заказы озона в состоянии "получено" по алгоритму:
	 * если дата 26.10, то все полученные заказы с 01.10. по 15.10 в статусе "получено" -> "завершено"
	 * если дата 15.10, то все полученные заказы с 15.09. по 31.09 в статусе "получено" -> "завершено"
	 */
	public String actualizationPostpay(Date executorDate) {	
		return ozonExecutor.actualizationPostpay(executorDate);		
	}	
		
}
