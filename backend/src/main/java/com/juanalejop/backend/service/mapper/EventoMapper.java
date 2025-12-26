package com.juanalejop.backend.service.mapper;

import com.juanalejop.backend.domain.Evento;
import com.juanalejop.backend.service.dto.EventoDTO;
import com.juanalejop.backend.service.dto.EventoTipoDTO;
import com.juanalejop.backend.service.dto.IntegranteDTO;
import org.mapstruct.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import java.util.List;
import java.util.ArrayList;

@Mapper(componentModel = "spring")
public interface EventoMapper extends EntityMapper<EventoDTO, Evento> {

    ObjectMapper objectMapper = new ObjectMapper();

    @Mapping(target = "tipoNombre", source = "eventoTipo.nombre")
    @Mapping(target = "tipoDescripcion", source = "eventoTipo.descripcion")
    @Mapping(target = "integrantesJson", expression = "java(mapIntegrantesToString(dto.getIntegrantes()))")
    Evento toEntity(EventoDTO dto);

    @Mapping(target = "eventoTipo", expression = "java(mapToEventoTipo(entity))")
    @Mapping(target = "integrantes", expression = "java(mapStringToIntegrantes(entity.getIntegrantesJson()))")
    @Mapping(target = "asientos", ignore = true)
    EventoDTO toDto(Evento entity);

    default String mapIntegrantesToString(List<IntegranteDTO> integrantes) {
        if (integrantes == null) return null;
        try {
            return objectMapper.writeValueAsString(integrantes);
        } catch (JsonProcessingException e) {
            return "[]";
        }
    }

    default List<IntegranteDTO> mapStringToIntegrantes(String json) {
        if (json == null || json.isEmpty()) return new ArrayList<>();
        try {
            return objectMapper.readValue(json, new TypeReference<List<IntegranteDTO>>(){});
        } catch (JsonProcessingException e) {
            return new ArrayList<>();
        }
    }

    default EventoTipoDTO mapToEventoTipo(Evento entity) {
        if (entity.getTipoNombre() == null) return null;
        EventoTipoDTO tipo = new EventoTipoDTO();
        tipo.setNombre(entity.getTipoNombre());
        tipo.setDescripcion(entity.getTipoDescripcion());
        return tipo;
    }
}
