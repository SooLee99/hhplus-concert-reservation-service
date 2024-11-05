package org.example.hhplusconcertreservationservice.global.config;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.function.Supplier;

@Component
public class TransactionHandler {

    @Transactional
    public <T> T runOnWriteTransaction(Supplier<T> supplier) {
        return supplier.get();
    }
}
