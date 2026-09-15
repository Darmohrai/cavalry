package org.templar.cavalry.dto;

public record StationCreateRequest(
        String name,
        String location
) {}