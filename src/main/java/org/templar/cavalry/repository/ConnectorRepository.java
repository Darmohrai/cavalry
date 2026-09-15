package org.templar.cavalry.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.templar.cavalry.entity.Connector;

import java.util.UUID;

public interface ConnectorRepository extends JpaRepository<Connector, UUID> {
}
