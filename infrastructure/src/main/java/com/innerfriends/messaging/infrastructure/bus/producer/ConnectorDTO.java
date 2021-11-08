package com.innerfriends.messaging.infrastructure.bus.producer;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public final class ConnectorDTO {

    public String state;
    @JsonProperty("worker_id")
    public String workerId;

}
