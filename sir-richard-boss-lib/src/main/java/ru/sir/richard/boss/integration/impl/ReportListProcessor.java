package ru.sir.richard.boss.integration.impl;

import lombok.extern.slf4j.Slf4j;
import ru.sir.richard.boss.exception.CoreException;
import ru.sir.richard.boss.integration.Processor;
import ru.sir.richard.boss.integration.impl.model.ReportListResponseModel;
import ru.sir.richard.boss.integration.msg.ENFormat;
import ru.sir.richard.boss.integration.msg.Msg;
import ru.sir.richard.boss.integration.msg.Payload;
import ru.sir.richard.boss.utils.IntegrationUtils;

@Slf4j
public class ReportListProcessor implements Processor {

    @Override
    public void process(Payload payload) throws CoreException {
    	log.debug("report parameters: {}", payload.msgIn.getBody());
    	
/*
        @SuppressWarnings("unchecked")
        ReportListResponseModel response = convertSEReportsToResponseModel((Collection<SEReport>) payload.msgOut.getBody());
*/
        ReportListResponseModel response = null;

        String jsonResponse = IntegrationUtils.toJSON(response);
        payload.msgOut = new Msg<>(ENFormat.JSON,
                ReportListProducer.outType,
                payload.msgIn.getRequestId(),
                payload.msgIn.getReciever(),
                payload.msgIn.getSender(),
                null,
                jsonResponse);
    }

}
