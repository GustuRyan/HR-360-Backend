package org.acme.repository.employee;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.WebApplicationException;
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

    public String generateEmployeeDetail(List<String> fields){

        return String.format("SELECT " + fields.stream()
                .filter(field -> !field.equals(EMPLOYEE_ID))
                .filter(field -> !field.equals(MARITAL_ID))
                .filter(field -> !field.equals(NATIONALITY_ID))
                .map(field -> TABLE_NAME + "." + field)
                .collect(Collectors.joining(", ")) + ", hr_marital.MARITAL_STATUS, hr_nationality.NATIONALITY_NAME FROM " + TABLE_NAME
                + " RIGHT JOIN hr_marital ON " + TABLE_NAME + ".MARITAL_ID = hr_marital.MARITAL_ID "
                + "RIGHT JOIN hr_nationality ON " + TABLE_NAME + ".NATIONALITY_ID = hr_nationality.NATIONALITY_ID "
                + "WHERE EMPLOYEE_ID = :employeeId");
    }

    public Map<String, Object> retrieveEmployeeDetail(Long employeeId){
        List<String> fieldList = Arrays.asList(FIELDS.split(","));

        var query = generateEmployeeDetail(fieldList);

        return jdbi.withHandle(handle -> handle.createQuery(query)
                .bind("employeeId", employeeId)
                .mapToMap()
                .findOne()
                .orElseThrow(() -> new WebApplicationException("Employee detail not found", 404))
        );
    }

    public EmployeeEnt getEmployeeDetail(Long employeeId) {
        var query = String.format("SELECT %s from %s where EMPLOYEE_ID = :employeeId", FIELDS, TABLE_NAME);

        return jdbi.withHandle(handle -> handle.createQuery(query).bind("employeeId", employeeId)
                .registerRowMapper(ConstructorMapper.factory(EmployeeEnt.class))
                .mapTo(EmployeeEnt.class)
                .findOne()
                .orElseThrow(() -> new WebApplicationException("Employee not found", 404)
                )
        );
    }

    public String generateEmployeeUpdate(List<String> fields) {

        return String.format("UPDATE %s SET " + fields.stream()
                .filter(field -> !field.equals(EMPLOYEE_ID))
                .map(field -> field + " = :" + field)
                .collect(Collectors.joining(", ")) + " WHERE %s = :employeeId", TABLE_NAME, EMPLOYEE_ID);
    }

    public String updateEmployee(Long employeeId, EmployeeEnt employee) {
        List<String> fieldList = Arrays.asList(FIELDS.split(","));

        var query = generateEmployeeUpdate(fieldList);

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
                .bind("MARITAL_ID", employee.maritalStatus())
                .bind("NATIONALITY_ID", employee.nationalityId())
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

    public String updateEmployeeDTO(EmployeeEnt employee) {
        List<String> fieldList = Arrays.asList(FIELDS.split(","));

        var query = generateEmployeeUpdate(fieldList);

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
                .bind("MARITAL_ID", employee.maritalStatus())
                .bind("NATIONALITY_ID", employee.nationalityId())
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
