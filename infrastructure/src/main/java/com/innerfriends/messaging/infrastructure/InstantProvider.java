package com.innerfriends.messaging.infrastructure;

import javax.enterprise.context.ApplicationScoped;
import java.time.Instant;

@ApplicationScoped
public class InstantProvider {

    public Instant now() {
        return Instant.now();
    }

}
