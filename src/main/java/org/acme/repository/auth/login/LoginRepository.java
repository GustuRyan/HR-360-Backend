package org.acme.repository.auth.login;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import org.acme.repository.auth.AuthEnt;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.mapper.reflect.ConstructorMapper;

import java.util.Map;

import static org.acme.repository.auth.AuthEnt.*;

@ApplicationScoped
public class LoginRepository {

    @Inject
    Jdbi jdbi;

    public AuthEnt findByEmail(String email) {
        var query = String.format("SELECT %s FROM %s WHERE EMAIL = :email", FIELDS, TABLE_NAME);

        return jdbi.withHandle(handle -> handle.createQuery(query).bind("email", email)
                .registerRowMapper(ConstructorMapper.factory(AuthEnt.class))
                .mapTo(AuthEnt.class)
                .findOne()
                .orElseThrow(() -> new WebApplicationException(Response.status(Response.Status.NOT_FOUND)
                        .entity(Map.of("error", "User not found"))
                        .build()))
        );
    }

}
