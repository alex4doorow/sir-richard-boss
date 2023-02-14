package ru.sir.richard.boss.integration.impl;

import lombok.extern.slf4j.Slf4j;
import ru.sir.richard.boss.exception.CoreException;
import ru.sir.richard.boss.integration.Consumer;
import ru.sir.richard.boss.integration.impl.model.ReportListRequestModel;
import ru.sir.richard.boss.integration.msg.ENFormat;
import ru.sir.richard.boss.integration.msg.Msg;
import ru.sir.richard.boss.integration.msg.Payload;
import ru.sir.richard.boss.model.data.Product;

import java.util.Collection;

@Slf4j
public class ReportListConsumer implements Consumer {

    @Override
    public void consume(Payload payload) throws CoreException {
    	ReportListRequestModel request = (ReportListRequestModel) payload.msgIn.getBody();
    	log.debug("request: {}", request);

        
        /*
        Collection<SEReport> seReports = reportBL.findReportsByParams(request.getReportListParams().getStDtm(),
                request.getReportListParams().getEndDtm(),
                request.getReportListParams().getRptTypes(),
                request.getReportListParams().getStatuses(),
                request.getReportListParams().getSender().getParticipant(),
                request.getReportListParams().getSender().getUser());
                */

        Collection<Product> products = null;

        payload.msgOut = new Msg<Collection<Product>>(ENFormat.JSON,
                ReportListProducer.outType,
                payload.msgIn.getRequestId(),
                payload.msgIn.getReciever(),
                payload.msgIn.getSender(),
                null,
                products);


    }
}

