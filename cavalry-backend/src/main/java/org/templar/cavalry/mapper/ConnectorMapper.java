package org.templar.cavalry.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.templar.cavalry.dto.ConnectorDto;
import org.templar.cavalry.entity.Connector;

@Mapper(componentModel = "spring")
public interface ConnectorMapper {

    @Mapping(source = "station.id", target = "stationId")
    ConnectorDto toDto(Connector entity);
}