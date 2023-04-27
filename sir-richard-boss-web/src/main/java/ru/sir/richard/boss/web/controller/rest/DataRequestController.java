package ru.sir.richard.boss.web.controller.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import ru.sir.richard.boss.exception.CoreException;
import ru.sir.richard.boss.integration.Producer;
import ru.sir.richard.boss.integration.impl.ReportListProducer;
import ru.sir.richard.boss.integration.impl.model.ErrorResponse;
import ru.sir.richard.boss.integration.impl.model.ReportListRequestModel;

import lombok.extern.slf4j.Slf4j;
import ru.sir.richard.boss.integration.msg.ENFormat;
import ru.sir.richard.boss.integration.msg.ENResult;
import ru.sir.richard.boss.integration.msg.Msg;
import ru.sir.richard.boss.model.utils.IntegrationUtils;

import static ru.sir.richard.boss.model.utils.IntegrationUtils.NOT_THROWS;

@Slf4j
@RestController
public class DataRequestController {

    @Autowired
    @Qualifier(value = "reportListProducer")
    private Producer<ReportListRequestModel, String> reportListProducer;

    @PostMapping("/view/request/reports")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> reportList(
            @RequestHeader(value = "X_Request_ID", required = false)
            String requestId,
            @RequestHeader(value = "header_token", required = false)
            String token,
            @RequestHeader(value = "header_dt", required = false)
            String dt,
            @RequestHeader(value = "header_rUser", required = false)
            String user,
            @RequestBody
            String body
    ) throws CoreException {

        final String inType = ReportListProducer.inType;
        log.info("[START] {} request:\n{}", inType, body);

        Msg<ReportListRequestModel> msg;
        try {
            msg = new Msg<ReportListRequestModel>(ENFormat.JSON,
                    inType,
                    requestId,
                    null,//TODO: sender from basic AUTH
                    user,
                    token,
                    IntegrationUtils.fromJSON(body, true, ReportListRequestModel.class));
        } catch (CoreException e) {
            String errBody = IntegrationUtils.toJSON(new ErrorResponse(e.getRespCode(), e.getRespDesc()), NOT_THROWS);
            return response(inType, requestId, errBody, true);
        }
        Msg<String> out = reportListProducer.produce(msg);
        return response(inType, requestId, out.getBody(), out.getType() == ENResult.ERR);
    }

    private ResponseEntity<String> response(String msgInType, String xRequestID, String responseBody, boolean isError) {
        String response = responseBody;
        log.info("[END] {} response:\n{}", msgInType, response);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        httpHeaders.add("X_Request_ID", xRequestID);

        return new ResponseEntity<String>(response, httpHeaders, (isError) ? HttpStatus.BAD_REQUEST : HttpStatus.OK);
    }
}
