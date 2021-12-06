package com.innerfriends.messaging.infrastructure.resources;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class QuarkusTestResourcesLifecycleManager implements QuarkusTestResourceLifecycleManager {

    private final static Logger LOGGER = LoggerFactory.getLogger(QuarkusTestResourcesLifecycleManager.class);

    private static final Network NETWORK = Network.newNetwork();

    private final static GenericContainer<?> HAZELCAST_CONTAINER = new GenericContainer("hazelcast/hazelcast:4.1.5")
            .withExposedPorts(5701)
            .withNetwork(NETWORK)
            .waitingFor(Wait.forLogMessage(".*is STARTED.*\\n", 1))
            .withLogConsumer(new Slf4jLogConsumer(LOGGER));

    private static final PostgreSQLContainer<?> POSTGRES_MUTABLE_CONTAINER = new PostgreSQLContainer<>(
            DockerImageName.parse("debezium/postgres:11-alpine")
                    .asCompatibleSubstituteFor("postgres"))
            .withNetwork(NETWORK)
            .withNetworkAliases("mutable")
            .withDatabaseName("mutable")
            .withUsername("postgresql")
            .withPassword("postgresql")
            .waitingFor(Wait.forLogMessage(".*database system is ready to accept connections.*", 1))
            .withLogConsumer(new Slf4jLogConsumer(LOGGER));

    private static final GenericContainer<?> RED_PANDA_CONTAINER = new RedPandaKafkaContainer(NETWORK)
            .withLogConsumer(new Slf4jLogConsumer(LOGGER));

    private static final GenericContainer<?> DEBEZIUM_CONNECT_CONTAINER = new GenericContainer<>("debezium/connect:1.8")
            .withExposedPorts(8083)
            .withNetwork(NETWORK)
            .withEnv("KAFKA_LOG4J_OPTS", "-Dlog4j.configuration=file:/opt/kafka/config/connect-log4j.properties")
            .withEnv("BOOTSTRAP_SERVERS", "redpanda:" + 9092)
            .withEnv("KEY_CONVERTER", "org.apache.kafka.connect.json.JsonConverter")
            .withEnv("VALUE_CONVERTER", "org.apache.kafka.connect.json.JsonConverter")
            .withEnv("GROUP_ID", "1")
            .withEnv("CONFIG_STORAGE_TOPIC", "my_connect_configs")
            .withEnv("OFFSET_STORAGE_TOPIC", "my_connect_offsets")
            .withEnv("STATUS_STORAGE_TOPIC", "my_connect_statuses")
            .waitingFor(Wait.forLogMessage(".*Finished starting connectors and tasks.*", 1))
            .withLogConsumer(new Slf4jLogConsumer(LOGGER));

    private static final GenericContainer<?> JAEGER_TRACING_ALL_IN_ONE_CONTAINER = new GenericContainer("jaegertracing/all-in-one:1.25.0")
            .withExposedPorts(16686, 14268, 14250)
            .withNetwork(NETWORK)
            .withNetworkAliases("jaeger-all-in-one")
            .waitingFor(Wait.forLogMessage(".*Channel Connectivity change to READY.*\\n", 1))
            .withLogConsumer(new Slf4jLogConsumer(LOGGER));

    private static final GenericContainer<?> OTEL_OPENTELEMETRY_COLLECTOR_CONTAINER = new GenericContainer("otel/opentelemetry-collector:0.33.0")
            .withExposedPorts(13133, 4317, 55680)
            .withNetwork(NETWORK)
            .withNetworkAliases("otel-collector")
            .withCommand("--config=/etc/otel-collector-config.yaml")
            .withClasspathResourceMapping("/given/otel-collector-config.yaml", "/etc/otel-collector-config.yaml", BindMode.READ_ONLY)
            .waitingFor(Wait.forLogMessage(".*Everything is ready. Begin running and processing data.*\\n", 1))
            .withLogConsumer(new Slf4jLogConsumer(LOGGER));


    @Override
    public Map<String, String> start() {
        Stream.of(HAZELCAST_CONTAINER, POSTGRES_MUTABLE_CONTAINER,
                RED_PANDA_CONTAINER, DEBEZIUM_CONNECT_CONTAINER, JAEGER_TRACING_ALL_IN_ONE_CONTAINER,
                OTEL_OPENTELEMETRY_COLLECTOR_CONTAINER)
                .forEach(genericContainer -> genericContainer.start());
        return new HashMap<>() {{
            // hazelcast
            put("quarkus.hazelcast-client.cluster-name", "dev");
            put("quarkus.hazelcast-client.cluster-members", String.format("localhost:%d", HAZELCAST_CONTAINER.getMappedPort(5701)));
            // datasource
            put("quarkus.datasource.jdbc.url", POSTGRES_MUTABLE_CONTAINER.getJdbcUrl());
            put("quarkus.datasource.username", POSTGRES_MUTABLE_CONTAINER.getUsername());
            put("quarkus.datasource.password", POSTGRES_MUTABLE_CONTAINER.getPassword());
            put("kafka-connector-api/mp-rest/url",
                    String.format("http://%s:%d", "localhost", DEBEZIUM_CONNECT_CONTAINER.getMappedPort(8083)));
            // connector
            put("connector.mutable.database.hostname", "mutable");
            put("connector.mutable.database.username", POSTGRES_MUTABLE_CONTAINER.getUsername());
            put("connector.mutable.database.password", POSTGRES_MUTABLE_CONTAINER.getPassword());
            put("connector.mutable.database.port", "5432");
            put("connector.mutable.database.dbname", POSTGRES_MUTABLE_CONTAINER.getDatabaseName());
            put("slot.drop.on.stop", "true");
            put("snapshot.mode", "always");
            put("kafka.exposed.port.9092", RED_PANDA_CONTAINER.getMappedPort(9092).toString());
            // jaeger
            put("jaeger.exposed.port.16686", JAEGER_TRACING_ALL_IN_ONE_CONTAINER.getMappedPort(16686).toString());
            // opentelemtry
            put("quarkus.opentelemetry.tracer.exporter.otlp.endpoint", String.format("http://localhost:%d", OTEL_OPENTELEMETRY_COLLECTOR_CONTAINER.getMappedPort(55680)));
        }};
    }

    @Override
    public void stop() {}

}
