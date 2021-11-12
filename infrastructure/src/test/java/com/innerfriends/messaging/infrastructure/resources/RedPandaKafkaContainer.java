package com.innerfriends.messaging.infrastructure.resources;

import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.dockerjava.api.model.ContainerNetwork;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.images.builder.Transferable;
import org.testcontainers.utility.DockerImageName;

import java.nio.charset.StandardCharsets;
import java.util.Map;

public class RedPandaKafkaContainer extends GenericContainer<RedPandaKafkaContainer> {

    private static final int KAFKA_PORT = 9092;

    private static final String STARTER_SCRIPT = "/var/lib/redpanda/redpanda.sh";

    public RedPandaKafkaContainer(final Network network) {
        super(DockerImageName.parse("vectorized/redpanda:v21.9.6"));
        withNetwork(Network.SHARED)
                .withExposedPorts(KAFKA_PORT)
                .withNetwork(network)
                .withNetworkAliases("redpanda")
                .withCreateContainerCmdModifier(cmd -> {
                    cmd.withEntrypoint("sh");
                })
                .withCommand("-c", "while [ ! -f " + STARTER_SCRIPT + " ]; do sleep 0.1; done; " + STARTER_SCRIPT)
                .waitingFor(Wait.forLogMessage(".*Successfully started Redpanda!.*", 1));
    }

    @Override
    protected void containerIsStarting(InspectContainerResponse containerInfo, boolean reused) {
        super.containerIsStarting(containerInfo, reused);

        // Start and configure the advertised address
        String command = "#!/bin/bash\n";
        command += "/usr/bin/rpk redpanda start --check=false --node-id 0 --smp 1 ";
        command += "--memory 1G --overprovisioned --reserve-memory 0M ";
        command += String.format("--kafka-addr PLAINTEXT://0.0.0.0:29092,OUTSIDE://0.0.0.0:%d ", KAFKA_PORT);
        command += String.format("--advertise-kafka-addr PLAINTEXT://%s:29092,OUTSIDE://%s:%d", getContainerIp(),
                getContainerIp(), KAFKA_PORT);
        //noinspection OctalInteger
        copyFileToContainer(
                Transferable.of(command.getBytes(StandardCharsets.UTF_8), 0777),
                STARTER_SCRIPT);
    }

    private String getContainerIp() {
        final Map.Entry<String, ContainerNetwork> entry = this.getContainerInfo().getNetworkSettings().getNetworks().entrySet().iterator().next();
        return entry.getValue().getIpAddress();
    }

}
