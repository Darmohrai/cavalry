package org.templar.cavalry.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "connectors")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Connector {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "station_id", nullable = false)
    private ChargingStation station;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private ConnectorType type;

    @Column(name = "max_power_kw", nullable = false)
    private Integer maxPowerKw;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private ConnectorStatus status;
}
