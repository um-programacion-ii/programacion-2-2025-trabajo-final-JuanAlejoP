package com.juanalejop.backend.service.mapper;

import com.juanalejop.backend.domain.Ticket;
import com.juanalejop.backend.domain.Venta;
import com.juanalejop.backend.service.dto.TicketDTO;
import com.juanalejop.backend.service.dto.VentaDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Ticket} and its DTO {@link TicketDTO}.
 */
@Mapper(componentModel = "spring")
public interface TicketMapper extends EntityMapper<TicketDTO, Ticket> {
    @Mapping(target = "venta", source = "venta", qualifiedByName = "ventaId")
    TicketDTO toDto(Ticket s);

    @Named("ventaId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    VentaDTO toDtoVentaId(Venta venta);
}
