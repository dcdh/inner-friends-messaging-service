package com.innerfriends.messaging.domain;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

import java.time.ZoneId;
import java.time.ZonedDateTime;

import static org.assertj.core.api.Assertions.assertThat;

public class EventAtTest {

    @Test
    public void should_verify_equality() {
        EqualsVerifier.forClass(EventAt.class).verify();
    }

    @Test
    public void should_return_event_at() {
        assertThat(new EventAt(buildZonedDateTime()).at()).isEqualTo(buildZonedDateTime());
    }

    @Test
    public void should_return_event_at_from_posted_at() {
        assertThat(new EventAt(new PostedAt(buildZonedDateTime())).at()).isEqualTo(buildZonedDateTime());
    }

    private ZonedDateTime buildZonedDateTime() {
        return ZonedDateTime.of(2021, 10, 31, 0, 0, 0, 0, ZoneId.of("Europe/Paris"));
    }

}
