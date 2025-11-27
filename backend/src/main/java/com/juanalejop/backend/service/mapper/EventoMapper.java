package com.juanalejop.backend.service.mapper;

import com.juanalejop.backend.domain.Evento;
import com.juanalejop.backend.service.dto.EventoDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Evento} and its DTO {@link EventoDTO}.
 */
@Mapper(componentModel = "spring")
public interface EventoMapper extends EntityMapper<EventoDTO, Evento> {}
