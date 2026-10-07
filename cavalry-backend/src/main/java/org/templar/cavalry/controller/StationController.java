package org.templar.cavalry.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.templar.cavalry.annotation.ExtractUserId;
import org.templar.cavalry.annotation.PostCreateEndpoint;
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

    @PostCreateEndpoint
    public StationDto create(@Valid @RequestBody StationCreateRequest request) {
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

    @GetMapping("/my-actions")
    public String getMyActions(@ExtractUserId String userId) {
        if (userId == null) {
            return "User is not authenticated";
        }

        return "List of actions for Keycloak user with ID: " + userId;
    }

    @PostMapping("/{id}/sync-external")
    public ResponseEntity<String> triggerExternalSync(@PathVariable java.util.UUID id) {
        service.syncStationData(id);
        return ResponseEntity.ok("External sync complete. There should be an AOP warning in the logs.");
    }

    @PostMapping("/{id}/sync-internal")
    public ResponseEntity<String> triggerInternalSync(@PathVariable java.util.UUID id) {
        service.updateAndSyncInternal(id);
        return ResponseEntity.ok("Internal sync complete. AOP will not trigger (demonstration of self-invocation).");
    }

    @PostMapping("/test-retry")
    public ResponseEntity<String> testRetryLogic() {
        String result = service.performUnreliableOperation();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/masked")
    public ResponseEntity<List<StationDto>> getAllStationsMasked() {
        List<StationDto> maskedStations = service.getAllStationsMasked();
        return ResponseEntity.ok(maskedStations);
    }
}
