package ru.sir.richard.boss.model.dto;

import java.util.List;

import lombok.Data;

@Data
public class CdekEntityRecipientDto {
	private String company;
	private String name;
	private String email;
	private List<CdekPhoneDto> phones;
}
