package com.juanalejop.backend.service.mapper;

import com.juanalejop.backend.domain.Evento;
import com.juanalejop.backend.domain.Venta;
import com.juanalejop.backend.service.dto.EventoDTO;
import com.juanalejop.backend.service.dto.VentaDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Venta} and its DTO {@link VentaDTO}.
 */
@Mapper(componentModel = "spring")
public interface VentaMapper extends EntityMapper<VentaDTO, Venta> {
    @Mapping(target = "evento", source = "evento", qualifiedByName = "eventoTitulo")
    VentaDTO toDto(Venta s);

    @Named("eventoTitulo")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "titulo", source = "titulo")
    EventoDTO toDtoEventoTitulo(Evento evento);
}
