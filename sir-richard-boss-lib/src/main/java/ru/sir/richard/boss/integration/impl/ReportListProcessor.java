package ru.sir.richard.boss.integration.impl;

import lombok.extern.slf4j.Slf4j;
import ru.sir.richard.boss.exception.CoreException;
import ru.sir.richard.boss.integration.Processor;
import ru.sir.richard.boss.integration.msg.Payload;

@Slf4j
public class ReportListProcessor implements Processor {

    @Override
    public void process(Payload payload) throws CoreException {
        /*
        @SuppressWarnings("unchecked")
        ReportListResponseModel response = convertSEReportsToResponseModel((Collection<SEReport>) payload.msgOut.getBody());

        String jsonResponse = Utils.toJSON(response);

        payload.msgOut = new Msg<>(ENFormat.JSON,
                ReportListProducer.outType,
                payload.msgIn.getRequestId(),
                payload.msgIn.getReciever(),
                payload.msgIn.getSender(),
                null,
                jsonResponse);
         */
    }

    @Override
    public void init() throws Exception {
        Processor.super.init();
    }
}
