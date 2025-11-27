package com.juanalejop.backend.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class VentaTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Venta getVentaSample1() {
        return new Venta().id(1L).idVentaCatedra(1L).estado("estado1");
    }

    public static Venta getVentaSample2() {
        return new Venta().id(2L).idVentaCatedra(2L).estado("estado2");
    }

    public static Venta getVentaRandomSampleGenerator() {
        return new Venta().id(longCount.incrementAndGet()).idVentaCatedra(longCount.incrementAndGet()).estado(UUID.randomUUID().toString());
    }
}
