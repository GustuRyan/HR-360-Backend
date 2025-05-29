package org.acme.repository.employee;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.jdbi.v3.core.mapper.reflect.ColumnName;

public record EmployeeEnt(
    @JsonIgnore @ColumnName(EMPLOYEE_ID) Long employeeId,
    @ColumnName(FULL_NAME) String fullName,
    @ColumnName(ADDRESS) String address,
    @ColumnName(HANDPHONE) String handphone
) {
    public static final String TABLE_NAME = "hr_employee";

    public static final String EMPLOYEE_ID = "EMPLOYEE_ID";
    public static final String FULL_NAME = "FULL_NAME";
    public static final String ADDRESS = "ADDRESS";
    public static final String HANDPHONE = "HANDPHONE";

    public static final String FIELDS = String.join(",", EMPLOYEE_ID, FULL_NAME, ADDRESS, HANDPHONE);

}
