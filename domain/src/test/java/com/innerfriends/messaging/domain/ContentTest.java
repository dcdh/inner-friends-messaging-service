package com.innerfriends.messaging.domain;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class ContentTest {

    @Test
    public void should_verify_equality() {
        EqualsVerifier.forClass(Content.class).verify();
    }

    @Test
    public void should_fail_fast_when_content_is_more_than_499_length() {
        // Given
        final StringBuilder contentTooLong = new StringBuilder(500);
        for (int i = 0; i < 500; i++) {
            contentTooLong.append("A");
        }

        // When && Then
        assertThatThrownBy(() -> new Content(contentTooLong.toString())).isInstanceOf(IllegalStateException.class);
    }

}
