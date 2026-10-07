package org.templar.cavalry.dto;

import org.templar.cavalry.entity.ConnectorStatus;
import org.templar.cavalry.entity.ConnectorType;

import java.util.UUID;

public record ConnectorDto(
        UUID id,
        UUID stationId,
        ConnectorType type,
        Integer maxPowerKw,
        ConnectorStatus status
) {
}
