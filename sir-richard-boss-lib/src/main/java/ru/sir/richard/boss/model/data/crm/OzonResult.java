package ru.sir.richard.boss.model.data.crm;

import lombok.Data;
import ru.sir.richard.boss.model.dto.OzonResponseDto;

import java.util.ArrayList;
import java.util.List;

@Data
public class OzonResult {
	
	private OzonResponseDto dirtyResponce;
	private List<OzonResultBean> response = new ArrayList<OzonResultBean>();	
	private boolean responseSuccess;
}
