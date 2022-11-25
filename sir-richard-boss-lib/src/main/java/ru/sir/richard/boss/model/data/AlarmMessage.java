package ru.sir.richard.boss.model.data;

import java.util.Date;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@NoArgsConstructor
@ToString
public class AlarmMessage extends AnyId {
	
	private Date addedDate;
	private String module;
	private String code;
	private String message;

	public AlarmMessage(int id) {
		super(id);
	}
}
