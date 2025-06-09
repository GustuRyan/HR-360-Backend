package org.acme.repository.auth.register;

import at.favre.lib.crypto.bcrypt.BCrypt;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import org.acme.repository.auth.AuthEnt;
import org.acme.repository.auth.login.LoginRepository;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.mapper.reflect.ConstructorMapper;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Map;

import static org.acme.repository.auth.AuthEnt.*;

@ApplicationScoped
public class RegisterRepository {
    @Inject
    Jdbi jdbi;

    @Inject
    LoginRepository loginRepository;

    public boolean findDupeEmail(String email) {
        var query = String.format("SELECT %s FROM %s WHERE EMAIL = :email", EMAIL, TABLE_NAME);
        String foundEmail = "";

        return jdbi.withHandle(handle -> handle.createQuery(query).bind("email", email)
                .mapTo(foundEmail.getClass())
                .findOne()
                .isPresent()
        );
    }

    public boolean findDupeEmpId(Long empId) {
        var query = String.format("SELECT %s FROM %s WHERE EMPLOYEE_ID = :empId", EMAIL, TABLE_NAME);
        String foundEmpId = "";

        return jdbi.withHandle(handle -> handle.createQuery(query).bind("empId", empId)
                .mapTo(foundEmpId.getClass())
                .findOne()
                .isPresent()
        );
    }

    public AuthEnt register(AuthEnt register) {
        Long nextUserId = getNextUserId();
        String hashedPassword = BCrypt.withDefaults().hashToString(12, register.password().toCharArray());
        Timestamp now = Timestamp.valueOf(LocalDateTime.now());

        if (findDupeEmail(register.email())) {
            throw new WebApplicationException(
                    Response.status(Response.Status.CONFLICT)
                            .entity(Map.of("message", "email already in use"))
                            .build());
        }

        if (findDupeEmpId(register.employeeId())) {
            throw new WebApplicationException(
                    Response.status(Response.Status.CONFLICT)
                            .entity(Map.of("message", "employee id already in use"))
                            .build());
        }

        var ent = new AuthEnt(
                nextUserId,
                hashedPassword,
                register.email(),
                register.fullName(),
                now,
                register.employeeId(),
                1,
                0
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
