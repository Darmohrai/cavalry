package org.templar.cavalry.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.templar.cavalry.dto.StationCreateRequest;
import org.templar.cavalry.dto.StationDto;
import org.templar.cavalry.dto.StationUpdateRequest;
import org.templar.cavalry.entity.StationStatus;
import org.templar.cavalry.service.StationService;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StationControllerTest {

    @Mock
    private StationService service;

    @InjectMocks
    private StationController controller;

    @Test
    void getAll_returnsListOfStations() {
        // Arrange (Given)
        List<StationDto> expectedList = List.of(
                new StationDto(UUID.randomUUID(), "Hub 1", "Kyiv", StationStatus.ACTIVE, Instant.now())
        );
        when(service.getAllStations()).thenReturn(expectedList);

        // Act (When)
        List<StationDto> result = controller.getAll();

        // Assert (Then)
        assertEquals(expectedList, result);
        verify(service, times(1)).getAllStations();
    }

    @Test
    void create_validRequest_returnsCreatedStation() {
        // Arrange (Given)
        var request = new StationCreateRequest("New Hub", "Lviv");
        var expectedDto = new StationDto(UUID.randomUUID(), "New Hub", "Lviv", StationStatus.ACTIVE, Instant.now());

        when(service.createStation(request)).thenReturn(expectedDto);

        // Act (When)
        StationDto result = controller.create(request);

        // Assert (Then)
        assertEquals(expectedDto, result);
        verify(service, times(1)).createStation(request);
    }

    @Test
    void updateStatus_validData_returnsUpdatedStation() {
        // Arrange (Given)
        UUID id = UUID.randomUUID();
        StationStatus newStatus = StationStatus.MAINTENANCE;
        var expectedDto = new StationDto(id, "Hub 1", "Kyiv", newStatus, Instant.now());

        when(service.updateStatus(id, newStatus)).thenReturn(expectedDto);

        // Act (When)
        StationDto result = controller.updateStatus(id, newStatus);

        // Assert (Then)
        assertEquals(expectedDto, result);
        verify(service, times(1)).updateStatus(id, newStatus);
    }

    @Test
    void update_validData_returnsUpdatedStation() {
        // Arrange (Given)
        UUID id = UUID.randomUUID();
        var request = new StationUpdateRequest("Updated Hub", "Odesa", StationStatus.ACTIVE);
        var expectedDto = new StationDto(id, "Updated Hub", "Odesa", StationStatus.ACTIVE, Instant.now());

        when(service.updateStation(id, request)).thenReturn(expectedDto);

        // Act (When)
        StationDto result = controller.update(id, request);

        // Assert (Then)
        assertEquals(expectedDto, result);
        verify(service, times(1)).updateStation(id, request);
    }

    @Test
    void delete_existingId_callsServiceDelete() {
        // Arrange (Given)
        UUID id = UUID.randomUUID();

        // Act (When)
        controller.delete(id);

        // Assert (Then)
        // Перевіряємо побічний ефект — виклик сервісу для видалення (AC7)
        verify(service, times(1)).deleteStation(id);
    }
}