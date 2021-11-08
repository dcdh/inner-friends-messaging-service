package com.innerfriends.messaging.infrastructure.bus.producer;

import net.jodah.failsafe.Failsafe;
import net.jodah.failsafe.RetryPolicy;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import java.time.Duration;
import java.util.List;
import java.util.Objects;

@ApplicationScoped
public class OutboxConnectorStarter {

    private final static String OUTBOX_CONNECTOR = "outbox-connector";

    private final Logger LOGGER = LoggerFactory.getLogger(OutboxConnectorStarter.class);

    private final KafkaConnectorConfigurationGenerator kafkaConnectorConfigurationGenerator;

    private final KafkaConnectorApi kafkaConnectorApi;

    public OutboxConnectorStarter(@RestClient final KafkaConnectorApi kafkaConnectorApi,
                                  final KafkaConnectorConfigurationGenerator kafkaConnectorConfigurationGenerator) {
        this.kafkaConnectorApi = Objects.requireNonNull(kafkaConnectorApi);
        this.kafkaConnectorConfigurationGenerator = Objects.requireNonNull(kafkaConnectorConfigurationGenerator);
    }

    public void start() {
        final KafkaConnectorConfigurationDTO connectorConfiguration = kafkaConnectorConfigurationGenerator.generateConnectorConfiguration(OUTBOX_CONNECTOR);
        final List<String> connectors = kafkaConnectorApi.getAllConnectors();
        if (!connectors.contains(OUTBOX_CONNECTOR)) {
            kafkaConnectorApi.registerConnector(connectorConfiguration);
            LOGGER.info(String.format("Outbox connector registered using this configuration '%s'", connectorConfiguration.toString()));
        }
        final RetryPolicy<KafkaConnectorStatusDTO> retryPolicy = new RetryPolicy<KafkaConnectorStatusDTO>()
                .handleResultIf(kafkaConnectorStatus -> "RUNNING" != kafkaConnectorStatus.connector.state)
                .withDelay(Duration.ofSeconds(1))
                .withMaxRetries(100)
                .onFailedAttempt(e -> LOGGER.error(String.format("Connector running attempt failed - connector current state '%s' - attempt count '%d'", e.getLastResult().connector.state, e.getAttemptCount())))
                .onRetry(e -> LOGGER.warn(String.format("Connector not running yet - connector current state '%s' - attempt count '%d'", e.getLastResult().connector.state, e.getAttemptCount())))
                .onRetriesExceeded(e -> LOGGER.warn("Failed for connector to run - Max retries exceeded - connector current state '%s' - attempt count '%d'", String.format(e.getResult().connector.state, e.getAttemptCount())))
                .onAbort(e -> LOGGER.warn("Wait for connector running state aborted - connector current state '%s' - attempt count '%d'", String.format(e.getResult().connector.state, e.getAttemptCount())));
        Failsafe.with(retryPolicy).run(() -> kafkaConnectorApi.connectorStatus(OUTBOX_CONNECTOR));
    }

}
