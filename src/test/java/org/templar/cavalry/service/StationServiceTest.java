package org.templar.cavalry.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.templar.cavalry.dto.StationDto;
import org.templar.cavalry.entity.ChargingStation;
import org.templar.cavalry.entity.StationStatus;
import org.templar.cavalry.mapper.StationMapper;
import org.templar.cavalry.repository.ChargingStationRepository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StationServiceTest {

    @Mock
    private ChargingStationRepository repository;

    @Mock
    private StationMapper mapper;

    @InjectMocks
    private StationService stationService;

    // ==========================================
    // Тести для методу getAllStations
    // ==========================================

    @Test
    void getAllStations_stationsExist_returnsMappedDtoList() {
        // Arrange (Given)
        UUID stationId = UUID.randomUUID();
        ChargingStation station = ChargingStation.builder()
                .id(stationId)
                .name("Test Station")
                .status(StationStatus.ACTIVE)
                .build();

        StationDto expectedDto = new StationDto(stationId, "Test Station", "Location", StationStatus.ACTIVE, Instant.now());

        when(repository.findAll()).thenReturn(List.of(station));
        when(mapper.toDto(station)).thenReturn(expectedDto);

        // Act (When)
        List<StationDto> result = stationService.getAllStations();

        // Assert (Then)
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(expectedDto, result.get(0));

        verify(repository, times(1)).findAll();
        verify(mapper, times(1)).toDto(station);
    }

    // ==========================================
    // Тести для методу deleteStation
    // ==========================================

    @Test
    void deleteStation_existingId_deletesSuccessfully() {
        // Arrange (Given)
        UUID stationId = UUID.randomUUID();
        when(repository.existsById(stationId)).thenReturn(true);

        // Act (When)
        stationService.deleteStation(stationId);

        // Assert (Then)
        // Використовуємо verify для перевірки побічного ефекту (AC7)
        verify(repository, times(1)).existsById(stationId);
        verify(repository, times(1)).deleteById(stationId);
    }

    @Test
    void deleteStation_nonExistingId_throwsRuntimeException() {
        // Arrange (Given)
        UUID stationId = UUID.randomUUID();
        when(repository.existsById(stationId)).thenReturn(false);

        // Act (When) & Assert (Then)
        // Перевірка обробки невалідних даних / відмови (AC2)
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            stationService.deleteStation(stationId);
        });

        assertEquals("Station not found with id: " + stationId, exception.getMessage());

        verify(repository, times(1)).existsById(stationId);
        verify(repository, never()).deleteById(any()); // Перевіряємо, що видалення не відбулося
    }

    // ==========================================
    // Тести для методу createStation
    // ==========================================

    @Test
    void createStation_validRequest_savesAndReturnsDto() {
        // Arrange
        var request = new org.templar.cavalry.dto.StationCreateRequest("New Station", "Kyiv");
        ChargingStation mappedEntity = ChargingStation.builder()
                .name("New Station")
                .location("Kyiv")
                .status(StationStatus.ACTIVE)
                .build();

        ChargingStation savedEntity = ChargingStation.builder()
                .id(UUID.randomUUID())
                .name("New Station")
                .location("Kyiv")
                .status(StationStatus.ACTIVE)
                .build();

        StationDto expectedDto = new StationDto(savedEntity.getId(), "New Station", "Kyiv", StationStatus.ACTIVE, Instant.now());

        when(mapper.toEntity(request)).thenReturn(mappedEntity);
        when(repository.save(mappedEntity)).thenReturn(savedEntity);
        when(mapper.toDto(savedEntity)).thenReturn(expectedDto);

        // Act
        StationDto result = stationService.createStation(request);

        // Assert
        assertEquals(expectedDto, result);

        // AC7: Перевірка самого факту виклику збереження
        verify(repository, times(1)).save(mappedEntity);
    }

    // ==========================================
    // Тести для методу updateStatus
    // ==========================================

    @Test
    void updateStatus_existingId_updatesStatusAndSaves() {
        // Arrange
        UUID stationId = UUID.randomUUID();
        ChargingStation existingStation = ChargingStation.builder()
                .id(stationId)
                .status(StationStatus.ACTIVE)
                .build();

        when(repository.findById(stationId)).thenReturn(java.util.Optional.of(existingStation));

        // Act
        stationService.updateStatus(stationId, StationStatus.MAINTENANCE);

        // Assert
        // AC7: Використовуємо ArgumentCaptor для перевірки побічного ефекту та параметрів
        org.mockito.ArgumentCaptor<ChargingStation> captor = org.mockito.ArgumentCaptor.forClass(ChargingStation.class);
        verify(repository).save(captor.capture());

        ChargingStation capturedStation = captor.getValue();
        assertEquals(StationStatus.MAINTENANCE, capturedStation.getStatus());
    }

    @Test
    void updateStatus_nonExistingId_throwsException() {
        // Arrange
        UUID stationId = UUID.randomUUID();
        when(repository.findById(stationId)).thenReturn(java.util.Optional.empty());

        // Act & Assert (AC2 - обробка відмови)
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            stationService.updateStatus(stationId, StationStatus.MAINTENANCE);
        });

        assertEquals("Station not found", exception.getMessage());
        verify(repository, never()).save(any());
    }

    // ==========================================
    // Тести для методу updateStation
    // ==========================================

    @Test
    void updateStation_existingId_updatesAndSaves() {
        // Arrange
        UUID stationId = UUID.randomUUID();
        var updateRequest = new org.templar.cavalry.dto.StationUpdateRequest("Updated Name", "Lviv", StationStatus.ACTIVE);
        ChargingStation existingStation = ChargingStation.builder().id(stationId).build();

        when(repository.findById(stationId)).thenReturn(java.util.Optional.of(existingStation));

        // Act
        stationService.updateStation(stationId, updateRequest);

        // Assert
        verify(mapper).updateEntity(updateRequest, existingStation);
        verify(repository).save(existingStation);
    }

    @Test
    void updateStation_nonExistingId_throwsException() {
        // Arrange
        UUID stationId = UUID.randomUUID();
        var updateRequest = new org.templar.cavalry.dto.StationUpdateRequest("Updated Name", "Lviv", StationStatus.ACTIVE);
        when(repository.findById(stationId)).thenReturn(java.util.Optional.empty());

        // Act & Assert (AC2 - обробка відмови)
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            stationService.updateStation(stationId, updateRequest);
        });

        assertEquals("Station not found with id: " + stationId, exception.getMessage());
        verify(mapper, never()).updateEntity(any(), any());
        verify(repository, never()).save(any());
    }
}