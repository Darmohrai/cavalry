package org.templar.cavalry.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.templar.cavalry.dto.StationCreateRequest;
import org.templar.cavalry.dto.StationDto;
import org.templar.cavalry.dto.StationUpdateRequest;
import org.templar.cavalry.entity.ChargingStation;

@Mapper(componentModel = "spring")
public interface StationMapper {

    StationDto toDto(ChargingStation entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", constant = "ACTIVE")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "connectors", ignore = true)
    ChargingStation toEntity(StationCreateRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "connectors", ignore = true)
    void updateEntity(StationUpdateRequest request, @MappingTarget ChargingStation entity);
}