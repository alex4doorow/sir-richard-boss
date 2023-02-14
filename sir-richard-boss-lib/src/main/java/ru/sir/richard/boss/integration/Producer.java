package ru.sir.richard.boss.integration;

import lombok.extern.slf4j.Slf4j;
import ru.sir.richard.boss.exception.CoreException;
import ru.sir.richard.boss.exception.SecurException;
import ru.sir.richard.boss.integration.msg.Msg;
import ru.sir.richard.boss.integration.msg.Payload;

import java.util.List;

@Slf4j
public abstract class Producer<IN, OUT> {
    private List<Processor> processorsList = null;
    private List<Processor> postProcessorsList = null;

    private Consumer consumer;
    private Auditor auditor;

    public Msg<OUT> produce(Msg<IN> msg) {
        Payload payload = new Payload(msg);
        Msg<OUT> reply = null;

        try {
            verifySignature(msg);
            if (processorsList != null)
                for (Processor p : processorsList)
                    p.process(payload);

            consumer.consume(payload);

            if (postProcessorsList != null)
                for (Processor p : postProcessorsList)
                    p.process(payload);

            reply = reply(payload);
        } catch (CoreException e) {
            log.error("processing failed with error", e);
            reply = replyError(payload, e);
            //TODO: FATAL PROCESSING???
        } catch (Throwable e) {
            log.error("processing failed with error", e);
            reply = replyError(payload, new CoreException(CoreException.INTERNAL_ERROR));
        }

        if (auditor != null)
            auditor.audit(payload);

        return reply;
    }

    public void init() throws Exception {
        if (processorsList != null)
            for (Processor p : processorsList)
                p.init();

        if (postProcessorsList != null)
            for (Processor p : postProcessorsList)
                p.init();

        consumer.init();

        if (auditor != null)
            auditor.init();
    }

    public abstract void verifySignature(Msg<IN> inMsg) throws SecurException;

    public abstract Msg<OUT> reply(Payload payload) throws CoreException;

    public abstract Msg<OUT> replyError(Payload payload, CoreException e);

    public void shutdown(int errorCode) {
    }

    public Consumer getConsumer() {
        return consumer;
    }

    public void setConsumer(Consumer consumer) {
        this.consumer = consumer;
    }

    public List<Processor> getProcessorsList() {
        return processorsList;
    }

    public void setProcessorsList(List<Processor> processorsList) {
        this.processorsList = processorsList;
    }

    public List<Processor> getPostProcessorsList() {
        return postProcessorsList;
    }

    public void setPostProcessorsList(List<Processor> postProcessorsList) {
        this.postProcessorsList = postProcessorsList;
    }
}
