package ru.sir.richard.boss.api.cdek;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import ru.sir.richard.boss.converter.CdekConverter;
import ru.sir.richard.boss.model.data.Address;
import ru.sir.richard.boss.model.data.Order;
import ru.sir.richard.boss.model.dto.CdekAccessDto;
import ru.sir.richard.boss.model.dto.CdekCityDto;
import ru.sir.richard.boss.model.dto.CdekOrderDto;
import ru.sir.richard.boss.model.dto.CdekPvzDto;

import javax.annotation.PostConstruct;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class CdekApiService {

    @Autowired
    private Environment environment;
    
    @Autowired
    private CdekConverter cdekAddressConverter;

    private WebClient webClient;

    @PostConstruct
    private void init() {
        final int size = 16 * 1024 * 1024;
        final ExchangeStrategies strategies = ExchangeStrategies.builder()
                .codecs(codecs -> codecs.defaultCodecs().maxInMemorySize(size))
                .build();
        webClient = WebClient.builder()
                .exchangeStrategies(strategies)
                .defaultHeader(HttpHeaders.HOST, "api.cdek.ru")
                .defaultHeader(HttpHeaders.ACCEPT_ENCODING, "gzip, deflate, br")
                .defaultHeader(HttpHeaders.CONNECTION, "keep-alive")
                .defaultHeader(HttpHeaders.USER_AGENT, "PostmanRuntime/7.29.2")
                .build();
    }

    public CdekAccessDto authorization() {

        final String url = "https://api.cdek.ru/v2/oauth/token";
        LinkedMultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
        map.add("client_id", environment.getProperty("cdek.auth.login"));
        map.add("client_secret", environment.getProperty("cdek.auth.secure"));
        map.add("grant_type", "client_credentials");
        CdekAccessDto result;
        try {
            result = webClient.post()
                    .uri(new URI(url))
                    .header(HttpHeaders.CONTENT_TYPE, "multipart/form-data; boundary=<calculated when request is sent>")
                    .contentType(MediaType.valueOf(MediaType.MULTIPART_FORM_DATA_VALUE))
                    .body(BodyInserters.fromMultipartData(map))
                    .retrieve()
                    .bodyToMono(CdekAccessDto.class)
                    .retry(3)
                    .log()
                    .block();
            //log.debug("result: {}", result);
            return result;
        } catch (Exception e) {
            log.error("result: {}", e);
        }
        return null;
    }

    public List<Address> getCities(String cityContext) {
        log.debug("getCities(): {}", cityContext);
        try {
            CdekAccessDto access = authorization();
            CdekCityDto[] cdekCityDtoArray = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .scheme("https")
                            .host("api.cdek.ru")
                            .path("/v2/location/cities")
                            .queryParam("country_codes", "RU")
                            .queryParam("city", cityContext)
                            .queryParam("lang", "rus")
                            .build())
                    .header(HttpHeaders.AUTHORIZATION, access.getSecret())
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .bodyToMono(CdekCityDto[].class)
                    .log()
                    .block();
	        List<Address> result = cdekAddressConverter.convertCityDtosToAddresses(cdekCityDtoArray);
	        log.debug("result: {}", result);
	        return result;
        } catch (Exception e) {
            log.error("result: {}", e);
        }
        // 401
        return null;
    }
    
    /*
    public List<CarrierInfo> getPvzs(int cityId) {
    	log.debug("getPvzs(): {}", cityId);    	
        try {
            CdekAccessDto access = authorization();
            CdekPvzDto[] cdekPvzDtoArray = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .scheme("https")
                            .host("api.cdek.ru")
                            .path("/v2/deliverypoints")
                            .queryParam("country_codes", "RU")
                            .queryParam("city_code", cityId)
                            .queryParam("type", "PVZ")                            
                            .build())
                    .header(HttpHeaders.AUTHORIZATION, access.getSecret())
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .bodyToMono(CdekPvzDto[].class)
                    .log()
                    .block();
            List<CarrierInfo> result = cdekAddressConverter.convertPvzDtosToCarrierInfo(cdekPvzDtoArray);
	        log.debug("result: {}", result);
	        return result;
        } catch (Exception e) {
            log.error("result: {}", e);
        }        
        return null;
    }
    */
    
    public List<Address> getPvzs(int cityId) {
    	log.debug("getPvzs(): {}", cityId);    	
        try {
            CdekAccessDto access = authorization();
            CdekPvzDto[] cdekPvzDtoArray = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .scheme("https")
                            .host("api.cdek.ru")
                            .path("/v2/deliverypoints")
                            .queryParam("country_codes", "RU")
                            .queryParam("city_code", cityId)
                            .queryParam("type", "PVZ")                            
                            .build())
                    .header(HttpHeaders.AUTHORIZATION, access.getSecret())
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .bodyToMono(CdekPvzDto[].class)
                    .log()
                    .block();
            List<Address> result = cdekAddressConverter.convertPvzDtosToAddresses(cdekPvzDtoArray);
	        log.debug("result: {}", result);
	        return result;
        } catch (Exception e) {
            log.error("result: {}", e);
        }        
        return null;  
    }
    
    public Order getOrder(String trackCode, CdekAccessDto inputAccess) {
    	log.debug("getOrder(): {}", trackCode);    	
        try {
        	CdekAccessDto access = inputAccess == null ? authorization() : inputAccess;
            CdekOrderDto cdekOrderDto = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .scheme("https")
                            .host("api.cdek.ru")
                            .path("/v2/orders")
                            .queryParam("cdek_number", trackCode)
                            .build())
                    .header(HttpHeaders.AUTHORIZATION, access.getSecret())
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .bodyToMono(CdekOrderDto.class)
                    .log()
                    .block();
            log.debug("result: {}", cdekOrderDto);
            Order result = cdekAddressConverter.convertCdekOrderDtoToOrder(cdekOrderDto);
	        log.debug("result: {}", result);
	        return result;
        } catch (Exception e) {
            log.error("result: {}", e);
        }        
        return null;
    }
    
    public List<Order> getStatuses(List<Order> inputOrders) {
    	CdekAccessDto access = authorization();
    	List<Order> results = new ArrayList<Order>();
    	inputOrders.forEach(inputOrder -> {
    		if (StringUtils.isNotEmpty(inputOrder.getDelivery().getTrackCode())) {
    			Order resultOrder = getOrder(inputOrder.getDelivery().getTrackCode(), access);
    			if (resultOrder != null) {
    				results.add(resultOrder);
    			}    			
    		}
    	});
    	return results;
    }    
}
