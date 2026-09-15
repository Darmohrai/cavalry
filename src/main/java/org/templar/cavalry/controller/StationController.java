package org.templar.cavalry.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.templar.cavalry.dto.StationCreateRequest;
import org.templar.cavalry.dto.StationDto;
import org.templar.cavalry.dto.StationUpdateRequest;
import org.templar.cavalry.entity.StationStatus;
import org.templar.cavalry.service.StationService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/stations")
@RequiredArgsConstructor
public class StationController {

    private final StationService service;

    @GetMapping
    public List<StationDto> getAll() {
        return service.getAllStations();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public StationDto create(@RequestBody StationCreateRequest request) {
        return service.createStation(request);
    }

    @PatchMapping("/{id}/status")
    public StationDto updateStatus(@PathVariable UUID id, @RequestParam StationStatus status) {
        return service.updateStatus(id, status);
    }

    @PutMapping("/{id}")
    public StationDto update(@PathVariable UUID id, @RequestBody StationUpdateRequest request) {
        return service.updateStation(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        service.deleteStation(id);
    }
}