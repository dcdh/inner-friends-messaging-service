package com.innerfriends.messaging.infrastructure.bus.producer;

public interface KafkaConnectorConfigurationGenerator {

    KafkaConnectorConfigurationDTO generateConnectorConfiguration(String connectorName);

}
