package com.innerfriends.messaging.infrastructure.resources;

import io.quarkus.test.common.QuarkusTestResource;

@QuarkusTestResource(HazelcastTestResourceLifecycleManager.class)
@QuarkusTestResource(OpenTelemetryLifecycleManager.class)
//@QuarkusTestResource(KafkaTestResourceLifecycleManager.class)
@QuarkusTestResource(RedpandaTestResourceLifecycleManager.class)
public class QuarkusTestResources {
}
