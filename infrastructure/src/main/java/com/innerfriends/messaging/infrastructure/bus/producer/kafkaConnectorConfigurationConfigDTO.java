package com.innerfriends.messaging.infrastructure.bus.producer;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.runtime.annotations.RegisterForReflection;

import java.util.Objects;

@RegisterForReflection
public final class kafkaConnectorConfigurationConfigDTO {

    public String databaseHostname;
    public String databasePort;
    public String databaseUser;
    public String databasePassword;
    public String databaseDbname;
    public String databaseServerName;
    public String slotDropOnStop;
    public String snapshotMode;

    private kafkaConnectorConfigurationConfigDTO(final Builder builder) {
        this.databaseHostname = Objects.requireNonNull(builder.databaseHostname);
        this.databasePort = Objects.requireNonNull(builder.databasePort);
        this.databaseUser = Objects.requireNonNull(builder.databaseUser);
        this.databasePassword = Objects.requireNonNull(builder.databasePassword);
        this.databaseDbname = Objects.requireNonNull(builder.databaseDbname);
        this.databaseServerName = Objects.requireNonNull(builder.databaseServerName);
        this.slotDropOnStop = Objects.requireNonNull(builder.slotDropOnStop);
        this.snapshotMode = Objects.requireNonNull(builder.snapshotMode);
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static class Builder {
        private String databaseHostname;
        private String databasePort;
        private String databaseUser;
        private String databasePassword;
        private String databaseDbname;
        private String databaseServerName;
        private String slotDropOnStop;
        private String snapshotMode;

        public Builder withDatabaseHostname(final String databaseHostname) {
            this.databaseHostname = databaseHostname;
            return this;
        }

        public Builder withDatabasePort(final String databasePort) {
            this.databasePort = databasePort;
            return this;
        }

        public Builder withDatabaseUser(final String databaseUser) {
            this.databaseUser = databaseUser;
            return this;
        }

        public Builder withDatabasePassword(final String databasePassword) {
            this.databasePassword = databasePassword;
            return this;
        }

        public Builder withDatabaseDbname(final String databaseDbname) {
            this.databaseDbname = databaseDbname;
            return this;
        }

        public Builder withDatabaseServerName(final String databaseServerName) {
            this.databaseServerName = databaseServerName;
            return this;
        }

        public Builder withSlotDropOnStop(final String slotDropOnStop) {
            this.slotDropOnStop = slotDropOnStop;
            return this;
        }

        public Builder withSnapshotMode(final String snapshotMode) {
            this.snapshotMode = snapshotMode;
            return this;
        }

        public kafkaConnectorConfigurationConfigDTO build() {
            return new kafkaConnectorConfigurationConfigDTO(this);
        }
    }

    @JsonProperty("connector.class")
    public String getConnectorClass() {
        return "io.debezium.connector.postgresql.PostgresConnector";
    }

    @JsonProperty("tasks.max")
    public String getTasksMax() {
        return "1";
    }

    @JsonProperty("database.hostname")
    public String getDatabaseHostname() {
        return databaseHostname;
    }

    @JsonProperty("database.port")
    public String getDatabasePort() {
        return databasePort;
    }

    @JsonProperty("database.user")
    public String getDatabaseUser() {
        return databaseUser;
    }

    @JsonProperty("database.password")
    public String getDatabasePassword() {
        return databasePassword;
    }

    @JsonProperty("database.dbname")
    public String getDatabaseDbname() {
        return databaseDbname;
    }

    @JsonProperty("database.server.name")
    public String getDatabaseServerName() {
        return databaseServerName;
    }

    @JsonProperty("schema.include.list")
    public String getSchemaIncludelist() {
        return "public";
    }

    @JsonProperty("table.include.list")
    public String getTableIncludelist() {
        return "public.outboxevent";
    }

    @JsonProperty("tombstones.on.delete")
    public String getTombstonesOnDelete() {
        return "false";
    }

    @JsonProperty("transforms")
    public String getTransforms() {
        return "outbox";
    }

    @JsonProperty("transforms.outbox.type")
    public String getTransformsRouterType() {
        return "io.debezium.transforms.outbox.EventRouter";
    }

    @JsonProperty("transforms.outbox.route.topic.replacement")
    public String getTransformsOutboxRouteTopicReplacement() {
        return "${routedByValue}.events";
    }

    @JsonProperty("transforms.outbox.table.field.event.timestamp")
    public String getTransformsOutboxTableFieldEventTimestamp() {
        return "timestamp";
    }

    @JsonProperty("transforms.outbox.table.field.event.payload.id")
    public String getTransformsOutboxTableFieldEventPayloadId() {
        return "id";
    }

    @JsonProperty("transforms.outbox.table.fields.additional.placement")
    public String getTransformsOutboxTableFieldsAdditionalPlacement() {
        return "type:header:eventType,aggregateid:header:aggregateId";
    }

    @JsonProperty("key.converter")
    public String getKeyConverter() {
        return "org.apache.kafka.connect.storage.StringConverter";
    }

    @JsonProperty("value.converter")
    public String getValueConverter() {
        return "org.apache.kafka.connect.storage.StringConverter";
    }

    @JsonProperty("key.converter.schemas.enable")
    public String getKeyConverterSchemaEnable() {
        return "false";
    }

    @JsonProperty("value.converter.schemas.enable")
    public String getValueConverterSchemaEnable() {
        return "false";
    }

    @JsonProperty("group.id")
    public String getGroupId() {
        return "connect";
    }

    @JsonProperty("slot.drop.on.stop")
    public String getSlotDropOnStop() {
        return slotDropOnStop;
    }

    @JsonProperty("snapshot.mode")
    public String getSnapshotMode() {
        return snapshotMode;
    }

    @Override
    public String toString() {
        return "kafkaConnectorConfigurationConfigDTO{" +
                "databaseHostname='*****'" +
                ", databasePort='*****'" +
                ", databaseUser='*****'" +
                ", databasePassword='*****'" +
                ", databaseDbname='*****'" +
                ", databaseServerName='*****'" +
                ", slotDropOnStop='" + slotDropOnStop + '\'' +
                '}';
    }
}
