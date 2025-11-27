package com.juanalejop.backend.domain;

import static com.juanalejop.backend.domain.TicketTestSamples.*;
import static com.juanalejop.backend.domain.VentaTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.juanalejop.backend.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TicketTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Ticket.class);
        Ticket ticket1 = getTicketSample1();
        Ticket ticket2 = new Ticket();
        assertThat(ticket1).isNotEqualTo(ticket2);

        ticket2.setId(ticket1.getId());
        assertThat(ticket1).isEqualTo(ticket2);

        ticket2 = getTicketSample2();
        assertThat(ticket1).isNotEqualTo(ticket2);
    }

    @Test
    void ventaTest() {
        Ticket ticket = getTicketRandomSampleGenerator();
        Venta ventaBack = getVentaRandomSampleGenerator();

        ticket.setVenta(ventaBack);
        assertThat(ticket.getVenta()).isEqualTo(ventaBack);

        ticket.venta(null);
        assertThat(ticket.getVenta()).isNull();
    }
}
