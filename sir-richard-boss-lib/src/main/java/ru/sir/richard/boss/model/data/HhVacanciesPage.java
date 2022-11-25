package ru.sir.richard.boss.model.data;

import java.util.ArrayList;
import java.util.List;

public class HhVacanciesPage {
	
	private int pageIndex;
	private int pageCount;
	private int vacanciesCount;
	
	private List<HhVacancy> vacancies; 
	
	public HhVacanciesPage() {
		super();
		this.vacancies = new ArrayList<HhVacancy>();
	}
	
	public HhVacanciesPage(List<HhVacancy> vacancies) {
		super();
		this.vacancies = vacancies;		
	}
	
	public int getPageIndex() {
		return pageIndex;
	}
	
	public void setPageIndex(int pageIndex) {
		this.pageIndex = pageIndex;
	}
	
	public int getPageCount() {
		return pageCount;
	}
	
	public void setPageCount(int pageCount) {
		this.pageCount = pageCount;
	}
	
	public int getVacanciesCount() {
		return vacanciesCount;
	}
	
	public void setVacanciesCount(int vacanciesCount) {
		this.vacanciesCount = vacanciesCount;
	}

	public List<HhVacancy> getVacancies() {
		return vacancies;
	}

	public void setVacancies(List<HhVacancy> vacancies) {
		this.vacancies = vacancies;
	}

}
