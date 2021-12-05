package com.innerfriends.messaging.domain;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

import java.time.ZoneId;
import java.time.ZonedDateTime;

import static org.assertj.core.api.Assertions.assertThat;

public class CreatedAtTest {

    @Test
    public void should_verify_equality() {
        EqualsVerifier.forClass(CreatedAt.class).verify();
    }

    @Test
    public void should_return_created_at() {
        assertThat(new CreatedAt(buildZonedDateTime()).at()).isEqualTo(buildZonedDateTime());
    }

    private ZonedDateTime buildZonedDateTime() {
        return ZonedDateTime.of(2021, 10, 31, 0, 0, 0, 0, ZoneId.of("Europe/Paris"));
    }

}