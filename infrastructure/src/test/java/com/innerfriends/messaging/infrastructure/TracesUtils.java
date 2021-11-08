package com.innerfriends.messaging.infrastructure;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.innerfriends.messaging.infrastructure.resources.OpenTelemetryLifecycleManager;
import io.restassured.http.ContentType;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.enterprise.context.ApplicationScoped;
import java.net.URL;
import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static io.restassured.RestAssured.given;
import static org.awaitility.Awaitility.await;

@ApplicationScoped
public class TracesUtils {

    @ConfigProperty(name = "quarkus.application.name")
    String serviceName;

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class Traces {

        public List<Data> data;

        public List<String> getOperationNames() {
            return data
                    .stream()
                    .flatMap(d -> d.getSpans().stream())
                    .map(Span::getOperationName)
                    .collect(Collectors.toList());
        }

        public List<String> getOperationNamesInError() {
            return data
                    .stream()
                    .flatMap(d -> d.getSpans().stream())
                    .filter(Span::inError)
                    .map(Span::getOperationName)
                    .collect(Collectors.toList());
        }

        public List<Integer> getHttpStatus() {
            return data
                    .stream()
                    .flatMap(d -> d.getSpans().stream())
                    .flatMap(s -> s.httpStatus().stream())
                    .collect(Collectors.toList());
        }

    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class Data {

        public List<Span> spans;

        public List<Span> getSpans() {
            return spans;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class Span {

        public String operationName;
        public List<Tag> tags;

        public String getOperationName() {
            return operationName;
        }

        public boolean inError() {
            return tags.stream().anyMatch(Tag::inError);
        }

        public List<Integer> httpStatus() {
            return tags.stream()
                    .map(t -> t.httpStatus())
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        }

    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class Tag {

        public String key;
        public Object value;

        public boolean inError() {
            return "error".equals(key) && Boolean.TRUE.equals(value);
        }

        public Integer httpStatus() {
            if ("http.status_code".equals(key)) {
                return (Integer) value;
            }
            return null;
        }

    }

    public Traces getTraces(final String httpTargetValue) throws Exception {
        final Integer hostPort = OpenTelemetryLifecycleManager.getJaegerRestApiHostPort();
        await().atMost(15, TimeUnit.SECONDS)
                .pollInterval(Duration.ofSeconds(1l))
                .until(() -> {
                    final Traces traces = given()
                            .when()
                            .queryParam("limit", "1")
                            .queryParam("service", serviceName)
                            .queryParam("tags", "{\"http.target\":\""+httpTargetValue+"\"}")
                            .get(new URL(String.format("http://localhost:%d/api/traces", hostPort)))
                            .then()
                            .log().all()
                            .contentType(ContentType.JSON)
                            .extract()
                            .body().as(Traces.class);
                    if (traces.getOperationNames().isEmpty()) {
                        return false;
                    }
                    return true;
                });
        TimeUnit.SECONDS.sleep(5l);// Need to wait to have all expected traces using an empirical time
        return given()
                .when()
                .queryParam("limit", "1")
                .queryParam("service", serviceName)
                .queryParam("tags", "{\"http.target\":\""+httpTargetValue+"\"}")
                .get(new URL(String.format("http://localhost:%d/api/traces", hostPort)))
                .then()
                .log().all()
                .extract()
                .body().as(Traces.class);
    }

}
