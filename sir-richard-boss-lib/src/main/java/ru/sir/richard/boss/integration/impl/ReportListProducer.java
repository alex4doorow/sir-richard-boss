package ru.sir.richard.boss.integration.impl;

import lombok.extern.slf4j.Slf4j;
import ru.sir.richard.boss.exception.CoreException;
import ru.sir.richard.boss.exception.SecurException;
import ru.sir.richard.boss.integration.Producer;
import ru.sir.richard.boss.integration.msg.ENResult;
import ru.sir.richard.boss.integration.msg.ENFormat;
import ru.sir.richard.boss.integration.msg.Msg;
import ru.sir.richard.boss.integration.msg.Payload;
import ru.sir.richard.boss.utils.IntegrationUtils;
import ru.sir.richard.boss.integration.impl.model.ErrorResponse;
import ru.sir.richard.boss.integration.impl.model.ReportListRequestModel;

@Slf4j
public class ReportListProducer extends Producer<ReportListRequestModel, String> {

    public static final String inType = "ReportList";
    public static final String outType = "ReportListResponse";

    @Override
    public void verifySignature(Msg<ReportListRequestModel> inMsg) throws SecurException {
        // NO SIGNATURE
    }

    @Override
    public Msg<String> reply(Payload payload) throws CoreException {
        return (Msg<String>) payload.msgOut;
    }

    @Override
    public Msg<String> replyError(Payload payload, CoreException e) {
        ErrorResponse response = new ErrorResponse(e.getRespCode(), e.getRespDesc());
        try {
            return new Msg<String>(ENFormat.JSON,
                    ENResult.ERR,
                    e.getRespCode(), e.getRespDesc(),
                    IntegrationUtils.toJSON(response));

        } catch (CoreException ex) {//we are not able to do nothing with this error
            log.error("replyError failed", ex);
            throw new RuntimeException(ex);
        }
    }
}

