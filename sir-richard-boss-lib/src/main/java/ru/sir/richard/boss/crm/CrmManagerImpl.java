package ru.sir.richard.boss.crm;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import ru.sir.richard.boss.dao.AnyDaoImpl;

@Repository
public class CrmManagerImpl extends AnyDaoImpl implements CrmManager {
	
	@SuppressWarnings("unused")
	private final Logger logger = LoggerFactory.getLogger(CrmManagerImpl.class);
		
	private List<CrmExecutable> executors;
	
	private Date executorDate;
	
	@Autowired
	private OpencartExecutor opencartExecutor;
	
	@Autowired
	private EmailFastOrderExecutor emailFastOrderExecutor;
	
	@Autowired
	private OzonExecutor ozonExecutor;	
	
	public CrmManagerImpl() {
		
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

	@Override
	public void setExecutorDate(Date executorDate) {
		this.executorDate = executorDate;
	}

	@Override
	public void importRun() {
		for (CrmExecutable executor : executors) {
			executor.setExecutorDate(executorDate);
			executor.run();
		}		
	}
	
}
