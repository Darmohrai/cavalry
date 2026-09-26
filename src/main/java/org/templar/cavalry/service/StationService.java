package org.templar.cavalry.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.templar.cavalry.dto.StationCreateRequest;
import org.templar.cavalry.dto.StationDto;
import org.templar.cavalry.dto.StationUpdateRequest;
import org.templar.cavalry.entity.ChargingStation;
import org.templar.cavalry.entity.StationStatus;
import org.templar.cavalry.mapper.StationMapper;
import org.templar.cavalry.repository.ChargingStationRepository;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StationService {

    private final ChargingStationRepository repository;
    private final StationMapper mapper;

    @Transactional(readOnly = true)
    public List<StationDto> getAllStations() {
        return repository.findAll().stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public StationDto createStation(StationCreateRequest request) {
        ChargingStation entity = mapper.toEntity(request);
        return mapper.toDto(repository.save(entity));
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
}
