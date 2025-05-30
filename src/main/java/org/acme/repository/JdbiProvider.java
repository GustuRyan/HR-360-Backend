package org.acme.repository;

import io.agroal.api.AgroalDataSource;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.ws.rs.Produces;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.statement.Slf4JSqlLogger;

public class JdbiProvider {

    @Inject
    AgroalDataSource ds;

    @Singleton
    @Produces
    public Jdbi jdbi() {
        return Jdbi.create(ds).setSqlLogger(new Slf4JSqlLogger());
    }
}
