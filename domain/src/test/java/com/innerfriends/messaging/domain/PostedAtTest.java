package com.innerfriends.messaging.domain;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

import java.time.ZoneId;
import java.time.ZonedDateTime;

import static org.assertj.core.api.Assertions.assertThat;

public class PostedAtTest {

    @Test
    public void should_verify_equality() {
        EqualsVerifier.forClass(PostedAt.class).verify();
    }

    @Test
    public void should_return_posted_at() {
        assertThat(new PostedAt(buildZonedDateTime()).at()).isEqualTo(buildZonedDateTime());
    }

    @Test
    public void should_return_added_at_from_event_at() {
        assertThat(new PostedAt(new EventAt(buildZonedDateTime())).at()).isEqualTo(buildZonedDateTime());
    }

    private ZonedDateTime buildZonedDateTime() {
        return ZonedDateTime.of(2021, 10, 31, 0, 0, 0, 0, ZoneId.of("Europe/Paris"));
    }

}
