package org.templar.cavalry.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.templar.cavalry.entity.ChargingStation;
import java.util.UUID;

public interface ChargingStationRepository extends JpaRepository<ChargingStation, UUID> {
}