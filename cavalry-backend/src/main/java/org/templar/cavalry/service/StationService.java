package org.templar.cavalry.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.templar.cavalry.annotation.ExecutionMonitor;
import org.templar.cavalry.annotation.MaskLocation;
import org.templar.cavalry.annotation.RetryOnFailure;
import org.templar.cavalry.config.RabbitMQConfig;
import org.templar.cavalry.dto.StationCreateRequest;
import org.templar.cavalry.dto.StationDto;
import org.templar.cavalry.dto.StationUpdateRequest;
import org.templar.cavalry.entity.ChargingStation;
import org.templar.cavalry.entity.StationStatus;
import org.templar.cavalry.mapper.StationMapper;
import org.templar.cavalry.publisher.NotificationPublisher;
import org.templar.cavalry.repository.ChargingStationRepository;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class StationService {

    private final ChargingStationRepository repository;
    private final StationMapper mapper;
    private final NotificationPublisher notificationPublisher;

    @Transactional(readOnly = true)
    public List<StationDto> getAllStations() {
        return repository.findAll().stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public StationDto createStation(StationCreateRequest request) {
        // Твоя існуюча логіка створення
        ChargingStation station = mapper.toEntity(request);
        ChargingStation savedStation = repository.save(station);

        notificationPublisher.sendStationCreatedEvent(savedStation.getId());

        return mapper.toDto(savedStation);
    }

    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR')")
    public StationDto updateStatus(UUID id, StationStatus newStatus) {
        ChargingStation station = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Station not found"));
        station.setStatus(newStatus);
        return mapper.toDto(repository.save(station));
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public StationDto updateStation(UUID id, StationUpdateRequest request) {
        ChargingStation station = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Station not found with id: " + id));

        mapper.updateEntity(request, station);
        return mapper.toDto(repository.save(station));
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteStation(UUID id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Station not found with id: " + id);
        }
        repository.deleteById(id);
    }

    @ExecutionMonitor(thresholdMs = 50)
    public void syncStationData(UUID stationId) {
        log.info("Executing syncStationData for station: {}", stationId);
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public void updateAndSyncInternal(UUID stationId) {
        log.info("Internal update of station {}...", stationId);
        this.syncStationData(stationId);
    }

    @MaskLocation(maskString = "--- HIDDEN BY AOP ---")
    public List<StationDto> getAllStationsMasked() {
        log.info("Fetching all stations...");
        return repository.findAll().stream()
                .map(mapper::toDto)
                .toList();
    }

    private int attemptCounter = 0;

    @RetryOnFailure(maxAttempts = 3, delayMs = 1000)
    public String performUnreliableOperation() {
        attemptCounter++;
        log.info("Executing unreliable operation... Attempt #{}", attemptCounter);

        if (attemptCounter < 3) {
            throw new RuntimeException("Simulated connection timeout");
        }

        attemptCounter = 0;
        return "SUCCESS AFTER RETRIES!";
    }
}
