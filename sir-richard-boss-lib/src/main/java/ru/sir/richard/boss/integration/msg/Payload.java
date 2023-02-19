package ru.sir.richard.boss.integration.msg;

import ru.sir.richard.boss.utils.IntegrationUtils;

public class Payload {

    public Msg<?> msgIn;    //to store original message;
    public Msg<?> msgOut;    //to store response(acknowledgement)

    public Payload(Msg<?> msg) {
        this.msgIn = msg;
    }

    public Payload(Msg<?> msgIn, Msg<?> msgOut) {
        this.msgIn = msgIn;
        this.msgOut = msgOut;
    }

    public Msg<?> getOriginalInMsg() {
        return IntegrationUtils.NVL(msgIn.getOriginalMsg(), msgIn);
    }
}
