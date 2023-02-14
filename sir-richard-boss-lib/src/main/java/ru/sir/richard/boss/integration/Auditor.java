package ru.sir.richard.boss.integration;

import ru.sir.richard.boss.integration.msg.Payload;

public interface Auditor {
    void audit(Payload payload);
    default void init() throws Exception {}
}
