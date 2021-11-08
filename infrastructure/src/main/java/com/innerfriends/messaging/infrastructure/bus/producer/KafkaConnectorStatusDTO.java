package com.innerfriends.messaging.infrastructure.bus.producer;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public final class KafkaConnectorStatusDTO {

    public String name;
    public ConnectorDTO connector;

}
