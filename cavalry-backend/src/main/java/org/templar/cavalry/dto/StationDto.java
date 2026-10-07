package org.templar.cavalry.dto;

import org.templar.cavalry.entity.StationStatus;

import java.time.Instant;
import java.util.UUID;

public record StationDto(
        UUID id,
        String name,
        String location,
        StationStatus status,
        Instant createdAt
) {
}
