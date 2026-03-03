package com.juanalejop.backend.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class EventoTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Evento getEventoSample1() {
        return new Evento()
            .id(1L)
            .idCatedra(1L)
            .titulo("titulo1")
            .resumen("resumen1")
            .descripcion("descripcion1")
            .direccion("direccion1")
            .imagenUrl("imagenUrl1");
    }

    public static Evento getEventoSample2() {
        return new Evento()
            .id(2L)
            .idCatedra(2L)
            .titulo("titulo2")
            .resumen("resumen2")
            .descripcion("descripcion2")
            .direccion("direccion2")
            .imagenUrl("imagenUrl2");
    }

    public static Evento getEventoRandomSampleGenerator() {
        return new Evento()
            .id(longCount.incrementAndGet())
            .idCatedra(longCount.incrementAndGet())
            .titulo(UUID.randomUUID().toString())
            .resumen(UUID.randomUUID().toString())
            .descripcion(UUID.randomUUID().toString())
            .direccion(UUID.randomUUID().toString())
            .imagenUrl(UUID.randomUUID().toString());
    }
}
