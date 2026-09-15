package org.templar.cavalry.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.templar.cavalry.entity.ChargingSession;

import java.util.UUID;

public interface ChargingSessionRepository extends JpaRepository<ChargingSession, UUID> {
}
