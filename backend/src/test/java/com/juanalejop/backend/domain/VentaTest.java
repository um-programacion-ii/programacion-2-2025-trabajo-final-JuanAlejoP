package com.juanalejop.backend.domain;

import static com.juanalejop.backend.domain.EventoTestSamples.*;
import static com.juanalejop.backend.domain.TicketTestSamples.*;
import static com.juanalejop.backend.domain.VentaTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.juanalejop.backend.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class VentaTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Venta.class);
        Venta venta1 = getVentaSample1();
        Venta venta2 = new Venta();
        assertThat(venta1).isNotEqualTo(venta2);

        venta2.setId(venta1.getId());
        assertThat(venta1).isEqualTo(venta2);

        venta2 = getVentaSample2();
        assertThat(venta1).isNotEqualTo(venta2);
    }

    @Test
    void ticketTest() {
        Venta venta = getVentaRandomSampleGenerator();
        Ticket ticketBack = getTicketRandomSampleGenerator();

        venta.addTicket(ticketBack);
        assertThat(venta.getTickets()).containsOnly(ticketBack);
        assertThat(ticketBack.getVenta()).isEqualTo(venta);

        venta.removeTicket(ticketBack);
        assertThat(venta.getTickets()).doesNotContain(ticketBack);
        assertThat(ticketBack.getVenta()).isNull();

        venta.tickets(new HashSet<>(Set.of(ticketBack)));
        assertThat(venta.getTickets()).containsOnly(ticketBack);
        assertThat(ticketBack.getVenta()).isEqualTo(venta);

        venta.setTickets(new HashSet<>());
        assertThat(venta.getTickets()).doesNotContain(ticketBack);
        assertThat(ticketBack.getVenta()).isNull();
    }

    @Test
    void eventoTest() {
        Venta venta = getVentaRandomSampleGenerator();
        Evento eventoBack = getEventoRandomSampleGenerator();

        venta.setEvento(eventoBack);
        assertThat(venta.getEvento()).isEqualTo(eventoBack);

        venta.evento(null);
        assertThat(venta.getEvento()).isNull();
    }
}
