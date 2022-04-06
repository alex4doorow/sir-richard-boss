package ru.sir.richard.boss.web.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import ru.sir.richard.boss.api.alert.AlarmApi;
import ru.sir.richard.boss.dao.AlarmDao;
import ru.sir.richard.boss.model.data.AlarmMessage;

/*
 состояния
 0- выкл
 1- вкл, поставлена на охрану
 2- вкл, снята с охраны
 3- сработка
 
 действия
 1- поставить на охрану
 2- снять с охраны
 3- hartbeat - запрос состояния 
*/
@Controller
public class AlarmController extends AnyController {
	
	private final Logger logger = LoggerFactory.getLogger(AlarmController.class);
	
	@Autowired
	private AlarmDao alarm;
	
	/**
	 * 
	 * @param model
	 * @return
	 */	
	@RequestMapping(value = "/alarm/car", method = RequestMethod.GET)
	public String listAlarmCar(Model model) {
		
		logger.debug("listAlarmCar()");
		
		alarm.requestCarState();		
		List<AlarmMessage> messages = alarm.listSystemMessageLog(AlarmDao.CAR_MODULE);
		int currentAlertCarState = alarm.getCurrentAlertCarState();

		String headerState = "";
		String messageState = "";
		String classState = "";
		if (currentAlertCarState == AlarmApi.STATE_SLEEP) {			
			headerState = "Сигнализация недоступна.";
			messageState = "Отключена, или находится вне зоны действия сигнала. Стоит проверить. ";
			classState = "warning";
		} else if (currentAlertCarState == AlarmApi.STATE_ON) {			
			headerState = "Сигнализация установлена.";
			messageState = "Включена, доступна, находится в зоне действия сигнала. Все в порядке.";
			classState = "success";
		} else if (currentAlertCarState == AlarmApi.STATE_OFF) {			
			headerState = "Сигнализация выключена.";
			messageState = "Доступна, находится в зоне действия сигнала. Необходимо поставить на охрану!";
			classState = "info";
		} else if (currentAlertCarState == AlarmApi.STATE_ALARM) {			
			headerState = "Внимание! Сработка датчиков!";
			messageState = "Кто-то проник внутрь! Немедленно принимай меры!";
			classState = "danger";
		} else {
			headerState = "Не понятно.";
			messageState = "Состояние не определено!";
			classState = "dark";
		}
				
		model.addAttribute("messages", messages);
		model.addAttribute("currentAlertCarState", currentAlertCarState);
		model.addAttribute("headerState", headerState);
		model.addAttribute("messageState", messageState);
		model.addAttribute("classState", classState);
		
		populateDefaultModel(model);		
		return "alarm/car";
	}
	
	@RequestMapping(value = "/alarm/car/action/{action}", method = RequestMethod.GET)
	public String alarmCarAction(@PathVariable("action") int action, Model model) {
		
		logger.debug("alarmCarAction(): {}", action);		
		alarm.actionExecute(action);
		alarm.requestCarState();
		// delay
		return "redirect:/alarm/car";	
		
	}
	

}
