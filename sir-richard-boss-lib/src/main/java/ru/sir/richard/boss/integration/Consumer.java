package ru.sir.richard.boss.integration;

import ru.sir.richard.boss.exception.CoreException;
import ru.sir.richard.boss.integration.msg.Payload;

public interface Consumer {
    public void consume(Payload payload) throws CoreException;
    public default void init() throws Exception {}
}

