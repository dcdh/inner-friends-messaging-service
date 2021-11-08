package com.innerfriends.messaging.infrastructure.bus.producer;

import io.quarkus.runtime.StartupEvent;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.annotation.Priority;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import java.util.Objects;

@ApplicationScoped
public class OutboxConnectorInitializer {

    private final OutboxConnectorStarter outboxConnectorStarter;
    private final Boolean isEnabled;

    public OutboxConnectorInitializer(final OutboxConnectorStarter outboxConnectorStarter,
                                      @ConfigProperty(name = "connector.mutable.enabled") final Boolean isEnabled) {
        this.outboxConnectorStarter = Objects.requireNonNull(outboxConnectorStarter);
        this.isEnabled = Objects.requireNonNull(isEnabled);
    }

    public void onStart(@Observes @Priority(1) final StartupEvent ev) {
        if (Boolean.FALSE.equals(isEnabled)) {
            return;
        }
        outboxConnectorStarter.start();
    }
}
