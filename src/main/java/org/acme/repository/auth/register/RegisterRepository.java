package org.acme.repository.auth.register;

import at.favre.lib.crypto.bcrypt.BCrypt;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.acme.repository.auth.AuthEnt;
import org.jdbi.v3.core.Jdbi;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import static org.acme.repository.auth.AuthEnt.*;

@ApplicationScoped
public class RegisterRepository {
    @Inject
    Jdbi jdbi;

    public AuthEnt register(AuthEnt register) {
        Long nextUserId = getNextUserId();
        String hashedPassword = BCrypt.withDefaults().hashToString(12, register.password().toCharArray());
        Timestamp now = Timestamp.valueOf(LocalDateTime.now());

        var ent = new AuthEnt(
                nextUserId,
                hashedPassword,
                register.email(),
                register.fullName(),
                now,
                register.employeeId(),
                1,
                register.adminStatus()
        );

        var query = String.format("INSERT INTO %s (%s) VALUES (%s)", TABLE_NAME, FIELDS, BINDER);

        jdbi.withHandle(handle -> handle.createUpdate(query).bindMethods(ent).execute());

        return ent;
    }

    private Long getNextUserId() {
        // This finds the maximum ID and adds 1, with 2001111L as the starting point if no records exist
        return jdbi.withHandle(handle ->
                handle.createQuery("SELECT COALESCE(MAX(USER_ID) + 1, 2001110) FROM " + TABLE_NAME)
                        .mapTo(Long.class)
                        .one());
    }
}
