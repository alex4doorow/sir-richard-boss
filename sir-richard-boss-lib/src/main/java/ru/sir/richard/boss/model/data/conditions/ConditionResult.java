package ru.sir.richard.boss.model.data.conditions;

/**
 * результат сконструированного запроса по заказам
 * @author alex4doorow
 *
 */
public class ConditionResult {
	private String conditionText;
	private boolean periodExist;
			
	public ConditionResult() {
		this.conditionText = "";
		this.periodExist = true;
	}

	public String getConditionText() {
		return conditionText;
	}

	public void setConditionText(String conditionText) {
		this.conditionText = conditionText;
	}

	public boolean isPeriodExist() {
		return periodExist;
	}

	public void setPeriodExist(boolean periodExist) {
		this.periodExist = periodExist;
	}
	
}
