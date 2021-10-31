package com.innerfriends.messaging.domain.usecase;

import com.innerfriends.messaging.domain.ParticipantIdentifier;

import java.util.Objects;

public final class TestParticipantIdentifier implements ParticipantIdentifier {

    private final String identifier;

    public TestParticipantIdentifier(final String identifier) {
        this.identifier = identifier;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TestParticipantIdentifier)) return false;
        TestParticipantIdentifier that = (TestParticipantIdentifier) o;
        return Objects.equals(identifier, that.identifier);
    }

    @Override
    public int hashCode() {
        return Objects.hash(identifier);
    }

    @Override
    public String identifier() {
        return identifier;
    }

    @Override
    public String toString() {
        return "TestParticipantIdentifier{" +
                "identifier='" + identifier + '\'' +
                '}';
    }
}
