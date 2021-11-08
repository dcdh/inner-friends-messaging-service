## What

Messaging domain.

## Prerequisites

`docker` is mandatory to be able to build the project. The infrastructure relie on `testcontainers` to start containers before starting tests and kill and remove them after running the tests.

## Dev local env
1. run `docker-compose -f docker-compose-dev-run.yaml up && docker-compose -f docker-compose-dev-run.yaml rm --force` to start the stack and next remove container after
1. run `mvn compile quarkus:dev -f infrastructure/pom.xml`
1. access swagger ui via `http://0.0.0.0:8080/q/swagger-ui/`
1. access jaeger ui via `http://localhost:16686/`

## How to build and run

1. run to prepare application containers `docker pull hazelcast/hazelcast:4.1.5 && docker pull debezium/postgres:11-alpine && docker pull debezium/kafka:1.8 && docker pull debezium/connect:1.8 && docker pull provectuslabs/kafka-ui:0.2.1 && docker pull jaegertracing/all-in-one:1.25.0 && docker pull otel/opentelemetry-collector:0.33.0`
1. run `mvn clean install verify` to build everything;
1. run `docker build -f infrastructure/src/main/docker/Dockerfile.native-distroless -t damdamdeo/inner-friends-messaging-service infrastructure/` to build docker image
1. run `docker-compose -f docker-compose-local-run.yaml up && docker-compose -f docker-compose-local-run.yaml rm --force` to start the stack and next remove container after
1. access swagger ui via `http://0.0.0.0:8080/q/swagger-ui/`
1. access jaeger ui via `http://localhost:16686/`

## Infra

### Known issues

https://issues.redhat.com/browse/DBZ-4262
