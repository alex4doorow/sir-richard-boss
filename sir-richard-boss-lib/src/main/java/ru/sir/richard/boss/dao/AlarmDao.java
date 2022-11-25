package ru.sir.richard.boss.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import ru.sir.richard.boss.api.alert.AlarmApi;
import ru.sir.richard.boss.model.data.AlarmMessage;
import ru.sir.richard.boss.model.utils.DateTimeUtils;

@Repository
public class AlarmDao extends AnyDaoImpl {

    public static final String CAR_MODULE = "car";

    private final Logger logger = LoggerFactory.getLogger(AnyDaoImpl.class);

    /**
     * 0 - выключена 1 - поставлена на охрану 2 - снята с охраны 3 - сработка
     * датчика
     */
    private static int ALERT_CAR_STATE = AlarmApi.STATE_SLEEP;

    // private static final String HOST_KEY = "alarm_car_host";

    // @Autowired
    // private ConfigDao config;

    public String getAlarmCarHost() {
	// return config.getConfigValue(HOST_KEY);
	return "";
    }

    public void requestCarState() {

	// запрос статуса
	AlarmApi alarm = new AlarmApi();
	int newState = alarm.gpioState(getAlarmCarHost(), AlarmApi.ACTION_HEART_BEAT);
	// если статус изменился или "поставлен" или "снят", то запись в журнал
	if (newState != getCurrentAlertCarState()) {
	    setCurrentAlertCarState(newState);
	    newSystemMessageLog(String.valueOf(newState), CAR_MODULE, getAlertStateMessage(newState));
	}
    }

    public List<AlarmMessage> listSystemMessageLog(String module) {
	logger.debug("listSystemMessageLog(): {}", module);

	final String sqlSelectListSystemMessage = "SELECT * FROM sr_sys_message_log " + "  WHERE module = ? "
		+ "  ORDER BY id DESC LIMIT 30";

	List<AlarmMessage> alarmMessages = this.jdbcTemplate.query(sqlSelectListSystemMessage, new Object[] { module },
		new int[] { Types.VARCHAR }, new RowMapper<AlarmMessage>() {
		    @Override
		    public AlarmMessage mapRow(ResultSet rs, int rowNum) throws SQLException {
			AlarmMessage alarmMessage = new AlarmMessage();
			alarmMessage.setId(rs.getInt("ID"));
			alarmMessage.setCode(rs.getString("CODE"));
			alarmMessage.setModule(rs.getString("MODULE"));
			alarmMessage.setMessage(rs.getString("MESSAGE"));
			alarmMessage.setAddedDate(DateTimeUtils.timestampToDate(rs.getTimestamp("DATE_ADDED")));
			return alarmMessage;
		    }
		});
	return alarmMessages;
    }

    public String actionExecute(int action) {

	AlarmApi alarm = new AlarmApi();
	return alarm.gpioActionExecute(getAlarmCarHost(), action);
    }

    public int getCurrentAlertCarState() {
	return ALERT_CAR_STATE;
    }

    private void setCurrentAlertCarState(int value) {
	ALERT_CAR_STATE = value;
    }

    private void newSystemMessageLog(String code, String module, String message) {

	final String sqlInsertSystemMessageLog = "INSERT INTO sr_sys_message_log"
		+ "  (module, message, code, date_added)" + "  VALUES" + "  (?, ?, ?, ?)";
	this.jdbcTemplate.update(sqlInsertSystemMessageLog, new Object[] { module, message, code, new Date() });
    }

    private String getAlertStateMessage(int state) {

	String message;
	if (state == 0) {
	    message = "Сигнализация выключена";
	} else if (state == 1) {
	    message = "Сигнализация поставлена на охрану";
	} else if (state == 2) {
	    message = "Сигнализация снята с охраны";
	} else if (state == 3) {
	    message = "Вторжение! Сработка датчиков";
	} else {
	    message = "Событие не определено. Ошибка в определении статуса";
	}
	return message;
    }

}
