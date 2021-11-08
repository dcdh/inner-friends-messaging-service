package com.innerfriends.messaging.infrastructure.bus.producer;

import io.quarkus.runtime.annotations.RegisterForReflection;

import java.util.Objects;

@RegisterForReflection
public final class KafkaConnectorConfigurationDTO {

    public String name;

    public kafkaConnectorConfigurationConfigDTO config;

    private KafkaConnectorConfigurationDTO(final Builder builder) {
        this.name = Objects.requireNonNull(builder.name);
        this.config = Objects.requireNonNull(builder.config);
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static class Builder {
        private String name;
        private kafkaConnectorConfigurationConfigDTO config;

        public Builder withName(final String name) {
            this.name = name;
            return this;
        }

        public Builder withConfig(final kafkaConnectorConfigurationConfigDTO config) {
            this.config = config;
            return this;
        }

        public KafkaConnectorConfigurationDTO build() {
            return new KafkaConnectorConfigurationDTO(this);
        }

    }

    @Override
    public String toString() {
        return "KafkaConnectorConfigurationDTO{" +
                "name='" + name + '\'' +
                ", config=" + config +
                '}';
    }
}
