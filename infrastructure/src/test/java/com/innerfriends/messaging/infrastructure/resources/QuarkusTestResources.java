package com.innerfriends.messaging.infrastructure.resources;

import io.quarkus.test.common.QuarkusTestResource;

@QuarkusTestResource(HazelcastTestResourceLifecycleManager.class)
@QuarkusTestResource(OpenTelemetryLifecycleManager.class)
@QuarkusTestResource(KafkaTestResourceLifecycleManager.class)
public class QuarkusTestResources {
}
