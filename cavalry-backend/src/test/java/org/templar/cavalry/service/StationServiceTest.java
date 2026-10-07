package org.templar.cavalry.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.templar.cavalry.dto.StationCreateRequest;
import org.templar.cavalry.dto.StationDto;
import org.templar.cavalry.dto.StationUpdateRequest;
import org.templar.cavalry.entity.ChargingStation;
import org.templar.cavalry.entity.StationStatus;
import org.templar.cavalry.mapper.StationMapper;
import org.templar.cavalry.publisher.NotificationPublisher;
import org.templar.cavalry.repository.ChargingStationRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StationServiceTest {

    @Mock
    private ChargingStationRepository repository;

    @Mock
    private StationMapper mapper;

    @Mock
    private NotificationPublisher notificationPublisher; // ДОДАЛИ МОК

    @InjectMocks
    private StationService stationService;

    @Test
    void getAllStations_stationsExist_returnsMappedDtoList() {
        UUID stationId = UUID.randomUUID();
        ChargingStation station = ChargingStation.builder()
                .id(stationId)
                .name("Test Station")
                .status(StationStatus.ACTIVE)
                .build();

        StationDto expectedDto = new StationDto(stationId, "Test Station", "Location", StationStatus.ACTIVE, Instant.now());

        when(repository.findAll()).thenReturn(List.of(station));
        when(mapper.toDto(station)).thenReturn(expectedDto);

        List<StationDto> result = stationService.getAllStations();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(expectedDto, result.get(0));
        verify(repository, times(1)).findAll();
        verify(mapper, times(1)).toDto(station);
    }

    @Test
    void deleteStation_existingId_deletesSuccessfully() {
        UUID stationId = UUID.randomUUID();
        when(repository.existsById(stationId)).thenReturn(true);

        stationService.deleteStation(stationId);

        verify(repository, times(1)).existsById(stationId);
        verify(repository, times(1)).deleteById(stationId);
    }

    @Test
    void deleteStation_nonExistingId_throwsRuntimeException() {
        UUID stationId = UUID.randomUUID();
        when(repository.existsById(stationId)).thenReturn(false);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            stationService.deleteStation(stationId);
        });

        assertEquals("Station not found with id: " + stationId, exception.getMessage());
        verify(repository, times(1)).existsById(stationId);
        verify(repository, never()).deleteById(any());
    }

    @Test
    void createStation_validRequest_savesAndReturnsDto() {
        var request = new StationCreateRequest("New Station", "Kyiv");

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

        StationDto result = stationService.createStation(request);

        assertEquals(expectedDto, result);
        verify(repository, times(1)).save(mappedEntity);
        // ПЕРЕВІРЯЄМО, ЩО ПАБЛІШЕР БУВ ВИКЛИКАНИЙ
        verify(notificationPublisher, times(1)).sendStationCreatedEvent(savedEntity.getId());
    }

    @Test
    void updateStatus_existingId_updatesStatusAndSaves() {
        UUID stationId = UUID.randomUUID();
        ChargingStation existingStation = ChargingStation.builder()
                .id(stationId)
                .status(StationStatus.ACTIVE)
                .build();

        when(repository.findById(stationId)).thenReturn(Optional.of(existingStation));

        stationService.updateStatus(stationId, StationStatus.MAINTENANCE);

        ArgumentCaptor<ChargingStation> captor = ArgumentCaptor.forClass(ChargingStation.class);
        verify(repository).save(captor.capture());
        ChargingStation capturedStation = captor.getValue();
        assertEquals(StationStatus.MAINTENANCE, capturedStation.getStatus());
    }

    @Test
    void updateStatus_nonExistingId_throwsException() {
        UUID stationId = UUID.randomUUID();
        when(repository.findById(stationId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            stationService.updateStatus(stationId, StationStatus.MAINTENANCE);
        });

        assertEquals("Station not found", exception.getMessage());
        verify(repository, never()).save(any());
    }

    @Test
    void updateStation_existingId_updatesAndSaves() {
        UUID stationId = UUID.randomUUID();
        var updateRequest = new StationUpdateRequest("Updated Name", "Lviv", StationStatus.ACTIVE);
        ChargingStation existingStation = ChargingStation.builder().id(stationId).build();

        when(repository.findById(stationId)).thenReturn(Optional.of(existingStation));

        stationService.updateStation(stationId, updateRequest);

        verify(mapper).updateEntity(updateRequest, existingStation);
        verify(repository).save(existingStation);
    }

    @Test
    void updateStation_nonExistingId_throwsException() {
        UUID stationId = UUID.randomUUID();
        var updateRequest = new StationUpdateRequest("Updated Name", "Lviv", StationStatus.ACTIVE);
        when(repository.findById(stationId)).thenReturn(java.util.Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            stationService.updateStation(stationId, updateRequest);
        });

        assertEquals("Station not found with id: " + stationId, exception.getMessage());
        verify(mapper, never()).updateEntity(any(), any());
        verify(repository, never()).save(any());
    }

    @Test
    void getAllStations_noStations_returnsEmptyList() {
        when(repository.findAll()).thenReturn(List.of());

        List<StationDto> result = stationService.getAllStations();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(repository, times(1)).findAll();
        verify(mapper, never()).toDto(any());
    }
}