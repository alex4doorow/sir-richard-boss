package ru.sir.richard.boss.api.hh;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.core.env.PropertyResolver;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import ru.sir.richard.boss.model.data.HhVacanciesPage;
import ru.sir.richard.boss.model.data.HhVacancy;
import ru.sir.richard.boss.utils.DateTimeUtils;

@Slf4j
public class HhApi {
	
	public HhApi(PropertyResolver environment) {
		super();
	}
	
	public HhVacanciesPage createPage(final String inputText, int pageIndex) {
		
		HhVacanciesPage result = null;
				
		Map<String, String> headers = new HashMap<>();
		headers.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/106.0.0.0 Safari/537.36");
		
		// https://hh.ru/search/vacancy?text=java&salary=&schedule=remote&clusters=true&ored_clusters=true&search_field=name&search_field=description&enable_snippets=true&search_period=1
		// https://hh.ru/search/vacancy?text=java&salary=&schedule=remote&clusters=true&ored_clusters=true&search_field=name&enable_snippets=true&search_period=1&excluded_text=game
		// https://hh.ru/search/vacancy?text=java&salary=&schedule=remote&clusters=true&ored_clusters=true&search_field=name&enable_snippets=true&search_period=1&excluded_text=%D0%B8%D0%B3%D1%80%D1%8B%2C+game
		String url = "https://api.hh.ru/vacancies?text=" + inputText + "&schedule=remote&period=1&order=creation_time_desc&search_field=name&excluded_text=game";
		
		if (pageIndex > 0) {
			url += "&page=" + pageIndex;			
		}	

		try {
			// &area=1&area=2758&area=13&area=159&page=2 // &employment=
			HttpResponse<JsonNode> jsonResponseHhRuVacancies = Unirest.get(url) 
					.headers(headers)
					.asJson();
		
			if (jsonResponseHhRuVacancies.getStatus() == 200) {
				
				List<HhVacancy> hhVacancies = createVacancies(jsonResponseHhRuVacancies);
								
				JSONObject jsonResult = jsonResponseHhRuVacancies.getBody().getObject();
				
				result = new HhVacanciesPage(hhVacancies);
				result.setVacancies(hhVacancies);				
				result.setPageCount(jsonResult.getInt("pages"));
				result.setPageIndex(jsonResult.getInt("page"));
				result.setVacanciesCount(jsonResult.getInt("found"));
								
			}
			if (jsonResponseHhRuVacancies.getStatus() == 401 || jsonResponseHhRuVacancies.getStatus() == 403) {
				log.error("HH api error. Status: {}", jsonResponseHhRuVacancies.getStatus());
			}
		} catch (JSONException e) {
			log.error("JSONException:", e);
		} catch (UnirestException e) {
			log.error("UnirestException:", e);
		}
		return result;
	}

	private List<HhVacancy> createVacancies(HttpResponse<JsonNode> jsonResponseHhRuVacancies) {
		
		List<HhVacancy> result = new ArrayList<HhVacancy>();
		
		JSONObject jsonResult = jsonResponseHhRuVacancies.getBody().getObject();
		
		int vacancyIndex = 0;
		try {		
			JSONArray jsonResultArray = jsonResult.getJSONArray("items");				
			for (int i = 0; i < jsonResultArray.length(); i++) {
				
				HhVacancy hhVacancy = new HhVacancy();				
				JSONObject jsonObjectVacancy = jsonResultArray.getJSONObject(i);
				hhVacancy.setId(jsonObjectVacancy.getInt("id"));
				
				hhVacancy.setName(jsonObjectVacancy.getString("name"));
				//String created_at = vacancy.getString("created_at");

				try {
					//public static String DATA_FORMAT_UTC_Z = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
					// 2022-10-08T15:21:32+0300
					Date publishedDate = DateTimeUtils.stringToDate(jsonObjectVacancy.getString("published_at"), "yyyy-MM-dd'T'HH:mm:ssZ");
					hhVacancy.setPublished(publishedDate);					
				} catch (ParseException e) {						
					log.error("ParseException", e);
				}
				
				
				hhVacancy.setResponsibility(jsonObjectVacancy.getJSONObject("snippet").getString("responsibility"));
				hhVacancy.setRequirement(jsonObjectVacancy.getJSONObject("snippet").getString("requirement"));
				
				String salaryFrom = "";
				/*
				if (vacancy.has("salary") && vacancy.getString("salary") != null && vacancy.getJSONObject("salary").has("from")) {
					salaryFrom = vacancy.getJSONObject("salary").getString("from");							
				}
				*/
				String salaryTo = "";
				/*
				if (vacancy.has("salary") && vacancy.getString("salary") != null && vacancy.getJSONObject("salary").has("to")) {
					salaryTo = vacancy.getJSONObject("salary").getString("to");							
				}
				*/	
				hhVacancy.setScheduleName(jsonObjectVacancy.getJSONObject("schedule").getString("name"));					
				//String areaId = vacancy.getJSONObject("area").getString("id");
				hhVacancy.setAreaName(jsonObjectVacancy.getJSONObject("area").getString("name"));
				/*
				String experienceName = "";
				if (vacancy.has("experience") && vacancy.getString("experience") != null && vacancy.getJSONObject("experience").has("name")) {
					experienceName = vacancy.getJSONObject("experience").getString("name");							
				}
				*/					
				hhVacancy.setVacancyUrl(jsonObjectVacancy.getString("alternate_url"));
				hhVacancy.setArchived(jsonObjectVacancy.getBoolean("archived"));					
				hhVacancy.setEmployerName(jsonObjectVacancy.getJSONObject("employer").getString("name"));
				
				result.add(hhVacancy);
				
				log.info("{}: {}, {}", vacancyIndex, hhVacancy.getId(), hhVacancy.getName());
				log.info("    {}", hhVacancy.getResponsibility());
				log.info("    {}", hhVacancy.getRequirement());
				log.info("    {}", hhVacancy.getAreaName());
				log.info("    {}", DateTimeUtils.formatDate(hhVacancy.getPublished(), "dd.MM.yyyy HH:mm:ss"));
				log.info("    {}", hhVacancy.getScheduleName());
				log.info("    {} - {}", salaryFrom, salaryTo);
				log.info("    {}", hhVacancy.getEmployerName());
				log.info("    {}", hhVacancy.getVacancyUrl());
				log.info("    архив:{}", hhVacancy.isArchived());
			
								
				// {"snippet":{"responsibility":"Сопровождение существующих систем. Разработка новых модулей.","requirement":"Понимание работы web-приложений. Наличие опыта работы с реляционными СУБД. Знания <highlighttext>Java<\/highlighttext> Core (Collections, Concurrency). Навыки работы с Spring Framework..."},"insider_interview":null,"sort_point_distance":null,"created_at":"2022-10-03T17:59:32+0300","salary":{"gross":false,"from":50000,"currency":"RUR","to":null},"type":{"name":"Открытая","id":"open"},"apply_alternate_url":"https:\/\/hh.ru\/applicant\/vacancy_response?vacancyId=69730106","working_time_modes":[],"archived":false,"premium":false,"employer":{"trusted":true,"logo_urls":{"original":"https:\/\/hhcdn.ru\/employer-logo-original\/235489.jpg","90":"https:\/\/hhcdn.ru\/employer-logo\/1384633.jpeg","240":"https:\/\/hhcdn.ru\/employer-logo\/1384634.jpeg"},"vacancies_url":"https:\/\/api.hh.ru\/vacancies?employer_id=1144524","name":"Аксиоматика","id":"1144524","alternate_url":"https:\/\/hh.ru\/employer\/1144524","url":"https:\/\/api.hh.ru\/employers\/1144524"},"accept_temporary":false,"id":"69730106","department":null,"published_at":"2022-10-03T17:59:32+0300","adv_response_url":"https:\/\/api.hh.ru\/vacancies\/69730106\/adv_response?host=hh.ru","area":{"name":"Волгоград","id":"24","url":"https:\/\/api.hh.ru\/areas\/24"},"address":{"lng":44.496233,"city":"Волгоград","street":"Баррикадная улица","metro":null,"description":null,"raw":"Волгоград, Баррикадная улица, 1Б","id":"667181","metro_stations":[],"building":"1Б","lat":48.689781},"working_days":[],"working_time_intervals":[],"url":"https:\/\/api.hh.ru\/vacancies\/69730106?host=hh.ru","schedule":{"name":"Гибкий график","id":"flexible"},"has_test":false,"name":"Junior Java-разработчик","response_letter_required":false,"relations":[],"alternate_url":"https:\/\/hh.ru\/vacancy\/69730106","contacts":null,"response_url":null}
			}			
			
			
		} catch (JSONException e) {
			log.error("JSONException:", e);
		}	
		
		return result;
	}

}
