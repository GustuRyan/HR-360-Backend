package org.acme.repository.employee;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.WebApplicationException;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.mapper.reflect.ConstructorMapper;

import static org.acme.repository.employee.EmployeeEnt.*;

@ApplicationScoped
public class EmployeeRepository {

    @Inject
    Jdbi jdbi;

    public EmployeeEnt getEmployeeDetail (Long employeeId) {
        var query = String.format("SELECT %s from %s where EMPLOYEE_ID = :employeeId", FIELDS, TABLE_NAME);

        return jdbi.withHandle(handle -> handle.createQuery(query).bind("employeeId", employeeId)
                .registerRowMapper(ConstructorMapper.factory(EmployeeEnt.class))
                .mapTo(EmployeeEnt.class)
                .findOne()
                .orElseThrow(() -> new WebApplicationException("Employee not found", 404)
                )
        );
    }
}
