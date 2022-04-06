package ru.sir.richard.boss.dao;

import java.util.List;

import ru.sir.richard.boss.model.data.AlarmMessage;

public interface AlarmDao {
	
	public static final String CAR_MODULE = "car";

	List<AlarmMessage> listSystemMessageLog(String module);
	
	void requestCarState();	
	int getCurrentAlertCarState();	
	String actionExecute(int action);	
	
	public String getAlarmCarHost();

}
