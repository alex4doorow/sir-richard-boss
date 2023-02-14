package ru.sir.richard.boss.integration.impl;

import lombok.extern.slf4j.Slf4j;
import ru.sir.richard.boss.exception.CoreException;
import ru.sir.richard.boss.integration.Consumer;
import ru.sir.richard.boss.integration.msg.Payload;

@Slf4j
public class ReportListConsumer implements Consumer {

    @Override
    public void consume(Payload payload) throws CoreException {
/*
        ReportListRequestModel request = (ReportListRequestModel) payload.msgIn.getBody();
        Collection<SEReport> seReports = reportBL.findReportsByParams(request.getReportListParams().getStDtm(),
                request.getReportListParams().getEndDtm(),
                request.getReportListParams().getRptTypes(),
                request.getReportListParams().getStatuses(),
                request.getReportListParams().getSender().getParticipant(),
                request.getReportListParams().getSender().getUser());

        payload.msgOut = new Msg<Collection<SEReport>>(ENFormat.JSON,
                ReportListProducer.outType,
                payload.msgIn.getRequestId(),
                payload.msgIn.getReciever(),
                payload.msgIn.getSender(),
                null,
                seReports);

 */
    }
}

