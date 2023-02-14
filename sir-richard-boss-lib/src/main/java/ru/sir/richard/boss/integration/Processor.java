package ru.sir.richard.boss.integration;

import ru.sir.richard.boss.exception.CoreException;
import ru.sir.richard.boss.integration.msg.Payload;

public interface Processor {
    void process(Payload payload) throws CoreException;
    default void init() throws Exception {}
}
