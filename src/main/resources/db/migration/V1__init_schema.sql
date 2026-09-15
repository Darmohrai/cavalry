CREATE TABLE charging_stations
(
    id         UUID PRIMARY KEY,
    name       VARCHAR(100) NOT NULL,
    location   VARCHAR(255) NOT NULL,
    status     VARCHAR(32)  NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE connectors
(
    id           UUID PRIMARY KEY,
    station_id   UUID        NOT NULL REFERENCES charging_stations (id) ON DELETE CASCADE,
    type         VARCHAR(32) NOT NULL,
    max_power_kw INT         NOT NULL,
    status       VARCHAR(32) NOT NULL
);

CREATE TABLE charging_sessions
(
    id                   UUID PRIMARY KEY,
    connector_id         UUID                     NOT NULL REFERENCES connectors (id),
    user_sub             VARCHAR(64)              NOT NULL,
    start_time           TIMESTAMP WITH TIME ZONE NOT NULL,
    end_time             TIMESTAMP WITH TIME ZONE,
    energy_delivered_kwh NUMERIC(6, 2) DEFAULT 0.00,
    status               VARCHAR(32)              NOT NULL
);