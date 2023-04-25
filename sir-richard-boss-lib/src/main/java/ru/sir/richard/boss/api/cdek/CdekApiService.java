package ru.sir.richard.boss.api.cdek;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import ru.sir.richard.boss.model.data.crm.DeliveryServiceResult;
import ru.sir.richard.boss.model.dto.*;

import javax.annotation.PostConstruct;
import javax.print.DocFlavor;
import java.io.File;
import java.math.BigDecimal;
import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
//https://api-docs.cdek.ru/29923741.html
public class CdekApiService {

    @Autowired
    private Environment environment;

    @Autowired
    private CdekConverter cdekConverter;

    private WebClient webClient;
    private ObjectMapper mapper;

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

        mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
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
            log.error("result:", e);
        }
        return null;
    }

    public CdekResponseOrderDto addOrder(Order order, int weightOfG, CdekAccessDto inputAccess) {
        log.debug("addOrder(): {}", order);
        CdekOrderDto cdekOrder = cdekConverter.convertOrderToCdekOrderDto(order, weightOfG);
/*
        try {
            String json = mapper.writeValueAsString(cdekOrder.getEntity());
            log.info(json);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

 */
        final String url = "https://api.cdek.ru/v2/orders";
        try {
            CdekAccessDto access = inputAccess == null ? authorization() : inputAccess;
            CdekResponseOrderDto result = webClient.post()
                    .uri(new URI(url))
                    .header(HttpHeaders.AUTHORIZATION, access.getSecret())
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(cdekOrder.getEntity())
                    .retrieve()
                    .bodyToMono(CdekResponseOrderDto.class)
                    .log()
                    .block();
            log.debug("result: {}", result);
            return result;
        } catch (Exception e) {
            log.error("result:", e);
        }
        return null;
    }

    /**
     * https://api.cdek.ru/v2/orders
     *
     * @param order
     * @param weightOfG
     * @return result of operation
     */
    public CdekResponseOrderDto addOrder(Order order, int weightOfG) {
        CdekAccessDto access = authorization();
        return addOrder(order, weightOfG, access);
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
            List<Address> result = cdekConverter.convertCityDtosToAddresses(cdekCityDtoArray);
            log.debug("result: {}", result);
            return result;
        } catch (Exception e) {
            log.error("result:", e);
        }
        // 401
        return null;
    }

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
            List<Address> result = cdekConverter.convertPvzDtosToAddresses(cdekPvzDtoArray);
            log.debug("result: {}", result);
            return result;
        } catch (Exception e) {
            log.error("result:", e);
        }
        return null;
    }

    public Order getOrderByUUIDTryingTen(String uuId) {
        Order orderByUUID = null;
        String trackCode = "";
        if (uuId != null) {
            int repeatedIndex = 0;
            while (StringUtils.isEmpty(trackCode)) {
                if (repeatedIndex > 10) {
                    log.debug("order.trackCode not load. uid:{}", uuId);
                    break;
                }
                orderByUUID = getOrderByUUID(uuId, null);
                if (orderByUUID == null) {
                    break;
                }
                trackCode = orderByUUID.getDelivery().getTrackCode();
                repeatedIndex++;
            }
        }
        if (StringUtils.isNotEmpty(trackCode)) {
            log.debug("order.trackCode:{}", trackCode);
        }
        return orderByUUID;
    }

    public Order getOrderByUUID(String uuId, CdekAccessDto inputAccess) {
        log.debug("getOrder(): {}", uuId);

        final String url = "https://api.cdek.ru/v2/orders/" + uuId;
        try {
            CdekAccessDto access = inputAccess == null ? authorization() : inputAccess;
            CdekOrderDto cdekOrderDto = webClient.get()
                    .uri(url)
                    .header(HttpHeaders.AUTHORIZATION, access.getSecret())
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .bodyToMono(CdekOrderDto.class)
                    .log()
                    .block();
            List<CdekEntityOrderStatusDto> statuses = cdekOrderDto.getEntity().getStatuses();
            log.debug("result.requests: {}", cdekOrderDto.getRequests());
            log.debug("result.statuses: {}", statuses);
            if (statuses != null && statuses.size() > 0 && "INVALID".equals(statuses.get(0).getCode())) {
                log.debug("result.statuses.error: {}", statuses.get(0).getCode());
                return null;
            }
            Order result = cdekConverter.convertCdekOrderDtoToOrder(cdekOrderDto);
            log.debug("result: {}", result);
            return result;
        } catch (Exception e) {
            log.error("result:", e);
        }
        return null;
    }

    public Order getOrderByTrackCode(String trackCode, CdekAccessDto inputAccess) {
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
            Order result = cdekConverter.convertCdekOrderDtoToOrder(cdekOrderDto);
            log.debug("result: {}", result);
            return result;
        } catch (Exception e) {
            log.error("result:", e);
        }
        return null;
    }

    public List<Order> getStatuses(List<Order> inputOrders) {
        CdekAccessDto access = authorization();
        List<Order> results = new ArrayList<>();
        inputOrders.forEach(inputOrder -> {
            if (StringUtils.isNotEmpty(inputOrder.getDelivery().getTrackCode())) {
                Order resultOrder = getOrderByTrackCode(inputOrder.getDelivery().getTrackCode(), access);
                if (resultOrder != null) {
                    results.add(resultOrder);
                }
            }
        });
        return results;
    }

    /**
     * Алгоритм, учитывает комиссию за наложенный платеж и обрабатывает корректно параметр "наложенный платеж"
     *
     * @param weightOfG
     * @param calculateDate
     * @param totalAmount
     * @param tariffId
     * @param receiverCityId
     * @param isPostpay      постоплата (да/нет)
     * @param isPaySeller    кто платит за доставку (продавец/покупатель)
     * @return DeliveryServiceResult calculated data
     * @throws Exception
     */
    public DeliveryServiceResult calculate(int weightOfG,
                                           Date calculateDate,
                                           BigDecimal totalAmount,
                                           int tariffId,
                                           int receiverCityId,
                                           boolean isPostpay,
                                           boolean isPaySeller) throws Exception {
        if (tariffId == 0) {
            return DeliveryServiceResult.createEmpty();
        }
        log.debug("calculate(): weightOfG={}, totalAmount={}, tariffId={}, receiverCityId={}, isPostpay={}, isPaySeller={}",
                weightOfG, totalAmount, tariffId, receiverCityId,
                isPostpay, isPaySeller);

        CdekEntityOrderDto requestData = cdekConverter.convertTariffDataToCdekOrderDto(weightOfG, tariffId,
                receiverCityId);

        final String url = "https://api.cdek.ru/v2/calculator/tariff";
        CdekResponseTariffDto responseData;
        try {
            CdekAccessDto access = authorization();
            responseData = webClient.post()
                    .uri(new URI(url))
                    .header(HttpHeaders.AUTHORIZATION, access.getSecret())
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(requestData)
                    .retrieve()
                    .bodyToMono(CdekResponseTariffDto.class)
                    .log()
                    .block();
            log.debug("responseData: deliverySum={}, totalSum={}", responseData.getDeliverySum(), responseData.getTotalSum());
        } catch (Exception e) {
            log.error("result:", e);
            return DeliveryServiceResult.createEmpty();
        }
        return cdekConverter.convertTariffDataToDeliveryServiceResult(responseData,
                weightOfG,
                totalAmount,
                tariffId,
                receiverCityId,
                isPostpay,
                isPaySeller);
    }
}
