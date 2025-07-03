package org.acme.repository.employee;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.mapper.reflect.ConstructorMapper;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.acme.repository.employee.EmployeeEnt.*;

@ApplicationScoped
public class EmployeeRepository {

    @Inject
    Jdbi jdbi;

    public static String getAliasedFields(String alias) {
        return Arrays.stream(FIELDS.split(","))
                .map(String::trim)
                .map(field -> alias + "." + field)
                .collect(Collectors.joining(", "));
    }

    public EmployeeEnt getEmployeeDetail(Long employeeId) {
        var query = String.format("""
        SELECT 
            %s,
            m.MARITAL_STATUS,
            n.NATIONALITY_NAME
        FROM %s e
        LEFT JOIN hr_marital m ON e.MARITAL_ID = m.MARITAL_ID
        LEFT JOIN hr_nationality n ON e.NATIONALITY_ID = n.NATIONALITY_ID
        WHERE e.EMPLOYEE_ID = :employeeId
        """, getAliasedFields("e"), EmployeeEnt.TABLE_NAME);

        return jdbi.withHandle(handle -> handle.createQuery(query)
                .bind("employeeId", employeeId)
                .registerRowMapper(ConstructorMapper.factory(EmployeeEnt.class))
                .mapTo(EmployeeEnt.class)
                .findOne()
                .orElseThrow(() -> new WebApplicationException(Response.status(Response.Status.NOT_FOUND)
                        .entity(Map.of("error", "employee detail not found"))
                        .build()))
        );
    }

    public String generateEmployeeUpdate(List<String> fields) {

        return String.format("UPDATE %s SET " + fields.stream()
                .filter(field -> !field.equals(EMPLOYEE_ID))
                .map(field -> field + " = :" + field)
                .collect(Collectors.joining(", ")) + " WHERE %s = :employeeId", TABLE_NAME, EMPLOYEE_ID);
    }

    public Map<String, String> updateEmployee(Long employeeId, EmployeeEnt employee) {
        List<String> fieldList = Arrays.asList(FIELDS.split(","));

        var query = generateEmployeeUpdate(fieldList);
        var maritalQuery = "SELECT MARITAL_ID FROM hr_marital WHERE MARITAL_STATUS = :maritalStatus";
        var nationalityQuery = "SELECT NATIONALITY_ID FROM hr_nationality WHERE NATIONALITY_NAME = :nationalityName";

        var maritalId = jdbi.withHandle(handle ->
                handle.createQuery(maritalQuery)
                        .bind("maritalStatus", employee.maritalStatus())
                        .mapTo(Long.class)
                        .one()
        );

        var nationalityId = jdbi.withHandle(handle ->
                handle.createQuery(nationalityQuery)
                        .bind("nationalityName", employee.nationalityName())
                        .mapTo(Long.class)
                        .one()
        );

        int rowsAffected = jdbi.withHandle(handle -> handle.createUpdate(query)
                .bind("employeeId", employeeId)
                .bind("FULL_NAME", employee.fullName())
                .bind("ADDRESS", employee.address())
                .bind("EMAIL", employee.email())
                .bind("phone", employee.phone())
                .bind("SEX", employee.gender())
                .bind("BIRTH_DATE", employee.birthDate())
                .bind("BIRTH_PLACE", employee.birthPlace())
                .bind("BLOOD_TYPE", employee.bloodType())
                .bind("MARITAL_ID", maritalId)
                .bind("NATIONALITY_ID", nationalityId)
                .bind("EMERGENCY_NAME", employee.nameEmg())
                .bind("PHONE_EMG", employee.phoneEmg())
                .bind("ADDRESS_EMG", employee.addressEmg())
                .execute()
        );

        if (rowsAffected > 0) {
            return Map.of(
                    "status", "success",
                    "message", "Employee updated successfully"
            );
        } else {
            return Map.of(
                    "status", "error",
                    "message", "Employee update failed"
            );
        }
    }

    public String updateEmployeeDTO(EmployeeEnt employee) {
        List<String> fieldList = Arrays.asList(FIELDS.split(","));

        var query = generateEmployeeUpdate(fieldList);
        var maritalQuery = "SELECT MARITAL_ID FROM hr_marital WHERE MARITAL_STATUS = :maritalStatus";
        var nationalityQuery = "SELECT NATIONALITY_ID FROM hr_nationality WHERE NATIONALITY_NAME = :nationalityName";

        var maritalId = jdbi.withHandle(handle ->
                handle.createQuery(maritalQuery)
                        .bind("maritalStatus", employee.maritalStatus())
                        .mapTo(Long.class)
                        .one()
        );

        var nationalityId = jdbi.withHandle(handle ->
                handle.createQuery(nationalityQuery)
                        .bind("nationalityName", employee.nationalityName())
                        .mapTo(Long.class)
                        .one()
        );

        int rowsAffected = jdbi.withHandle(handle -> handle.createUpdate(query)
                .bind("employeeId", employee.employeeId())
                .bind("FULL_NAME", employee.fullName())
                .bind("ADDRESS", employee.address())
                .bind("EMAIL", employee.email())
                .bind("phone", employee.phone())
                .bind("SEX", employee.gender())
                .bind("BIRTH_DATE", employee.birthDate())
                .bind("BIRTH_PLACE", employee.birthPlace())
                .bind("BLOOD_TYPE", employee.bloodType())
                .bind("MARITAL_ID", maritalId)
                .bind("NATIONALITY_ID", nationalityId)
                .bind("EMERGENCY_NAME", employee.nameEmg())
                .bind("PHONE_EMG", employee.phoneEmg())
                .bind("ADDRESS_EMG", employee.addressEmg())
                .execute()
        );

        if (rowsAffected > 0) {
            return String.format("Update berhasil %s", employee);
        } else {
            return "Gagal update";
        }
    }
}
