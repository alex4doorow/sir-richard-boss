package ru.sir.richard.boss.model.data;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
public class AlarmMessage extends AnyId {
	
	private Date addedDate;
	private String module;
	private String code;
	private String message;

	public AlarmMessage(int id) {
		super(id);
	}
}
