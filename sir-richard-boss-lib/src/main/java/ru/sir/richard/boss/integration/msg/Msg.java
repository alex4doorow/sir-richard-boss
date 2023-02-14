package ru.sir.richard.boss.integration.msg;

import java.util.LinkedHashMap;

public class Msg<T> extends LinkedHashMap<String, Object> {
    /**
     *
     */
    private static final long serialVersionUID = 2203053105330143690L;

    public static final String _type = "type";
    public static final String _requestId = "requestId";
    public static final String _correlationId = "correlationId";

    public static final String _sender = "sender";
    public static final String _reciever = "reciever";
    public static final String _signature = "signature";

    public static final String _code = "code";
    public static final String _desc = "desc";

    public static final String _filename = "filename";

    private ENFormat format;
    private T body;
    private Msg<?> originalMsg;

    public Msg(ENFormat format, T body) {
        super();
        this.format = format;
        this.body = body;
    }

    public Msg(	ENFormat format,
                   String type,
                   String requestId,
                   String sender,
                   String reciever,
                   String signature,
                   T body) {
        super();
        this.format = format;
        this.body = body;
        put(_type, type);
        put(_requestId, requestId);
        put(_sender, sender);
        put(_reciever, reciever);
        put(_signature, signature);
    }

    public Msg(	ENFormat format,
                   ENResult type,
                   String code,
                   String desc,
                   T body
    ) {
        super();
        this.format = format;
        this.body = body;
        put(_type, type);
        put(_code, code);
        put(_desc, desc);
    }

    public ENFormat getFormat() {
        return format;
    }

    @SuppressWarnings("unchecked")
    public <RT>RT getType() {
        return (RT)get(_type);
    }

    public String getTypeAsString() {
        return _type != null ? _type.toString() : "";
    }

    public String getRequestId() {
        return (String)get(_requestId);
    }

    public String getSender() {
        return (String)get(_sender);
    }

    public String getReciever() {
        return (String)get(_reciever);
    }

    public String getSignature() {
        return (String)get(_signature);
    }

    public String getCode() {
        return (String)get(_code);
    }

    public String getDesc() {
        return (String)get(_desc);
    }

    public T getBody() {
        return body;
    }

    public void setBody(T body) {
        this.body = body;
    }

    public Msg<?> getOriginalMsg() {
        return originalMsg;
    }

    public void setOriginalMsg(Msg<?> originalMsg) {
        this.originalMsg = originalMsg;
    }

    public String trace() {
        StringBuilder sb = new StringBuilder("Msg[");
        sb.append("c:").append(getBody().getClass()).
                append(", f:").append(getFormat()).
                append(", t:").append(getTypeAsString()).
                append(", id:").append(getRequestId()).
                append(", sender:").append(getSender()).
                append(", reciever:").append(getReciever()).
                append("]");

        return sb.toString();
    }

    public String traceResult() {
        StringBuilder sb = new StringBuilder("Msg[");
        sb.append("c:").append(getBody().getClass()).
                append(", f:").append(getFormat()).
                append(", r:").append(getTypeAsString()).
                append(", code:").append(getCode()).
                append(", desc:").append(getDesc()).
                append("]");

        return sb.toString();
    }

    public boolean isErrorResponse() {
        ENResult result = (ENResult)getType();

        return result != ENResult.ACK;
    }
}

