package com.juanalejop.backend.domain;

import static com.juanalejop.backend.domain.EventoTestSamples.*;
import static com.juanalejop.backend.domain.VentaTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.juanalejop.backend.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class EventoTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Evento.class);
        Evento evento1 = getEventoSample1();
        Evento evento2 = new Evento();
        assertThat(evento1).isNotEqualTo(evento2);

        evento2.setId(evento1.getId());
        assertThat(evento1).isEqualTo(evento2);

        evento2 = getEventoSample2();
        assertThat(evento1).isNotEqualTo(evento2);
    }

    @Test
    void ventaTest() {
        Evento evento = getEventoRandomSampleGenerator();
        Venta ventaBack = getVentaRandomSampleGenerator();

        evento.addVenta(ventaBack);
        assertThat(evento.getVentas()).containsOnly(ventaBack);
        assertThat(ventaBack.getEvento()).isEqualTo(evento);

        evento.removeVenta(ventaBack);
        assertThat(evento.getVentas()).doesNotContain(ventaBack);
        assertThat(ventaBack.getEvento()).isNull();

        evento.ventas(new HashSet<>(Set.of(ventaBack)));
        assertThat(evento.getVentas()).containsOnly(ventaBack);
        assertThat(ventaBack.getEvento()).isEqualTo(evento);

        evento.setVentas(new HashSet<>());
        assertThat(evento.getVentas()).doesNotContain(ventaBack);
        assertThat(ventaBack.getEvento()).isNull();
    }
}
