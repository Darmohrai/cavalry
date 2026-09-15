package org.templar.cavalry.dto;

import org.templar.cavalry.entity.StationStatus;

public record StationUpdateRequest(
        String name,
        String location,
        StationStatus status
) {}