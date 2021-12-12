## What

Messaging domain.

## Prerequisites

`docker` is mandatory to be able to build the project. The infrastructure relie on `testcontainers` to start containers before starting tests and kill and remove them after running the tests.

## Dev local env
1. run `docker-compose -f docker-compose-dev-run.yaml up && docker-compose -f docker-compose-dev-run.yaml rm --force` to start the stack and next remove container after
1. run `mvn compile quarkus:dev -f infrastructure/pom.xml`
1. access messaging swagger ui via `http://0.0.0.0:8080/q/swagger-ui/`
1. access friends swagger ui via `http://0.0.0.0:8082/q/swagger-ui/`
1. access jaeger ui via `http://localhost:16686/`
1. access kafka ui via `http://localhost:8081/`

## How to build and run

1. run to prepare application containers `docker pull quay.io/quarkus/ubi-quarkus-native-image:21.3-java11 && docker pull hazelcast/hazelcast:4.1.5 && docker pull debezium/postgres:11-alpine && docker pull debezium/kafka:1.8 && docker pull debezium/connect:1.8 && docker pull provectuslabs/kafka-ui:0.2.1 && docker pull jaegertracing/all-in-one:1.25.0 && docker pull otel/opentelemetry-collector:0.33.0 && docker pull vectorized/redpanda:v21.9.6`
1. run `mvn clean install verify -P native -Dquarkus.native.container-build=true` to build everything;
1. run `docker build -f infrastructure/src/main/docker/Dockerfile.native-distroless -t damdamdeo/inner-friends-messaging-service infrastructure/` to build docker image
1. run `docker-compose -f docker-compose-local-run.yaml up && docker-compose -f docker-compose-local-run.yaml rm --force` to start the stack and next remove container after
1. access messaging swagger ui via `http://0.0.0.0:8085/q/swagger-ui/`
1. access friends swagger ui via `http://0.0.0.0:8082/q/swagger-ui/`
1. access jaeger ui via `http://localhost:16686/`
1. access kafka ui via `http://localhost:8081/`

## Infra

### Docker

Stop and remove all containers `docker stop $(docker ps -a -q); docker rm $(docker ps -a -q)`

I do not know why I keep having 8080 already used... should investigate.

### Quarkus

#### Known issues

1. It is not possible to access the red panda in the dev mode by the quarkus instance.
TODO setup `advertise-kafka-addr` to use the container IP and not localhost or docker container network name like it is done in `RedPandaKafkaContainer`.
1. Native compilation does not work since version `2.4.0.Final`. Stuck to version `2.3.1.Final`.

###  Kafka

#### get connector status

> connect to `connect` container
> 
> curl http://localhost:8083/connectors/messaging-connector/status

#### list topics

> connect to `connect` container
> 
> bin/kafka-topics.sh --list --bootstrap-server kafka:9092

#### get `ContactBook.events` topic info

> connect to `connect` container 
>
> bin/kafka-topics.sh --bootstrap-server kafka:9092 --describe --topic ContactBook.events
> 
> bin/kafka-configs.sh --bootstrap-server kafka:9092 --describe --all --topic ContactBook.events

#### read `ContactBook.events` topic messages

> connect to `connect` container
>
> bin/kafka-console-consumer.sh --bootstrap-server kafka:9092 --property print.key=true --property print.headers=true --property print.timestamp=true --topic ContactBook.events --from-beginning

#### get `Conversation.events` topic info

> connect to `connect` container 
>
> bin/kafka-topics.sh --bootstrap-server kafka:9092 --describe --topic Conversation.events
> 
> bin/kafka-configs.sh --bootstrap-server kafka:9092 --describe --all --topic Conversation.events

#### read `Conversation.events` topic messages

> connect to `connect` container
>
> bin/kafka-console-consumer.sh --bootstrap-server kafka:9092 --property print.key=true --property print.headers=true --property print.timestamp=true --topic Conversation.events --from-beginning

#### Known issues

##### Kafka unable to start image `fixed in 1.8`

- https://issues.redhat.com/browse/DBZ-4262
- https://issues.redhat.com/browse/DBZ-4160

### vectorized/redpanda

- https://hub.docker.com/r/vectorized/redpanda
- https://vectorized.io/docs/quick-start-docker/#Bring-up-a-docker-compose-file
- https://vectorized.io/blog/redpanda-debezium/#Quick-tour-on-services

Regarding running redpanda using `testcontainers` we need to inject the container ip into `--advertise-kafka-addr` however the consumer in test would not be able to communicate with redpanda.

- https://github.com/debezium/docker-images/blob/main/kafka/1.8/docker-entrypoint.sh#L67
- https://github.com/debezium/docker-images/blob/main/kafka/1.8/docker-entrypoint.sh#L99
- https://github.com/quarkusio/quarkus/blob/2.4/extensions/kafka-client/deployment/src/main/java/io/quarkus/kafka/client/deployment/DevServicesKafkaProcessor.java#L337
- https://github.com/testcontainers/testcontainers-java/issues/452#issuecomment-331184470

#### list topic

> connect to `redpanda` container
>
> rpk topic list

#### read `ContactBook.events` topic messages

> connect to `redpanda` container
>
> rpk topic consume ContactBook.events

#### read `Conversation.events` topic messages

> connect to `redpanda` container
>
> rpk topic consume Conversation.events
