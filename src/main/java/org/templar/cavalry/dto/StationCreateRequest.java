package org.templar.cavalry.dto;

import jakarta.validation.constraints.NotBlank;
import org.templar.cavalry.validation.location.ValidGpsLocation;

public record StationCreateRequest(
        @NotBlank(message = "Назва не може бути порожньою")
        String name,

        @NotBlank(message = "Локація не може бути порожньою")
        @ValidGpsLocation
        String location
) {}