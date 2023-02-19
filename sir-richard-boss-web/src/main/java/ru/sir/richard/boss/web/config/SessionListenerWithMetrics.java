package ru.sir.richard.boss.web.config;

import java.util.concurrent.atomic.AtomicInteger;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

public class SessionListenerWithMetrics implements HttpSessionListener {

    private final AtomicInteger activeSessions;

    public SessionListenerWithMetrics() {
        super();

        activeSessions = new AtomicInteger();
        //counterOfActiveSessions = MetricRegistrySingleton.metrics.counter("web.sessions.active.count");
    }

    public final int getTotalActiveSession() {
        return activeSessions.get();
    }

    @Override
    public final void sessionCreated(final HttpSessionEvent event) {
        activeSessions.incrementAndGet();
        //counterOfActiveSessions.inc();
    }

    @Override
    public final void sessionDestroyed(final HttpSessionEvent event) {
        activeSessions.decrementAndGet();
        //counterOfActiveSessions.dec();
    }

}
