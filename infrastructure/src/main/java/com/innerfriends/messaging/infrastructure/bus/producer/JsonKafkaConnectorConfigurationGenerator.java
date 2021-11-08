package com.innerfriends.messaging.infrastructure.bus.producer;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.enterprise.context.ApplicationScoped;
import java.util.Objects;
import java.util.Optional;

@ApplicationScoped
public class JsonKafkaConnectorConfigurationGenerator implements KafkaConnectorConfigurationGenerator {

    final String mutableHostname;
    final String mutableUsername;
    final String mutablePassword;
    final Integer mutablePort;
    final String mutableDbname;
    final Boolean slotDropOnStop;
    final String snapshotMode;

    public JsonKafkaConnectorConfigurationGenerator(@ConfigProperty(name = "connector.mutable.database.hostname") final String mutableHostname,
                                                    @ConfigProperty(name = "connector.mutable.database.username") final String mutableUsername,
                                                    @ConfigProperty(name = "connector.mutable.database.password") final String mutablePassword,
                                                    @ConfigProperty(name = "connector.mutable.database.port") final Integer mutablePort,
                                                    @ConfigProperty(name = "connector.mutable.database.dbname") final String mutableDbname,
                                                    @ConfigProperty(name = "slot.drop.on.stop") final Boolean slotDropOnStop,
                                                    @ConfigProperty(name = "snapshot.mode") final String snapshotMode) {
        this.mutableHostname = Objects.requireNonNull(mutableHostname);
        this.mutableUsername = Objects.requireNonNull(mutableUsername);
        this.mutablePassword = Objects.requireNonNull(mutablePassword);
        this.mutablePort = Objects.requireNonNull(mutablePort);
        this.mutableDbname = Objects.requireNonNull(mutableDbname);
        this.slotDropOnStop = Optional.ofNullable(slotDropOnStop)
                .orElse(Boolean.FALSE);
        this.snapshotMode = Objects.requireNonNull(snapshotMode);
    }

    @Override
    public KafkaConnectorConfigurationDTO generateConnectorConfiguration(final String connectorName) {
        return KafkaConnectorConfigurationDTO
                .newBuilder()
                .withName(connectorName)
                .withConfig(kafkaConnectorConfigurationConfigDTO
                        .newBuilder()
                        .withDatabaseHostname(mutableHostname)
                        .withDatabasePort(mutablePort.toString())
                        .withDatabaseUser(mutableUsername)
                        .withDatabasePassword(mutablePassword)
                        .withDatabaseDbname(mutableDbname)
                        .withDatabaseServerName(mutableHostname)
                        .withSlotDropOnStop(slotDropOnStop.toString())
                        .withSnapshotMode(snapshotMode)
                        .build()
                )
                .build();
    }

}
