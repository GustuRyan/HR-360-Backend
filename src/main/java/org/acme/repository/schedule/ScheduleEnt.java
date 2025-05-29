package org.acme.repository.schedule;

import org.jdbi.v3.core.mapper.reflect.ColumnName;

import java.util.Set;

public record ScheduleEnt(
    @ColumnName(EMP_SCHEDULE_ID) Long empSchId,
    @ColumnName(EMPLOYEE_ID) Long employeeId,
    @ColumnName(PERIOD_ID) Long periodId
) {
    public static final String TABLE_NAME = "hr_emp_schedule";

    public static final String EMP_SCHEDULE_ID = "EMP_SCHEDULE_ID";
    public static final String EMPLOYEE_ID = "EMPLOYEE_ID";
    public static final String PERIOD_ID = "PERIOD_ID";

    public static final String FIELDS = String.join(",",
            EMP_SCHEDULE_ID, EMPLOYEE_ID, PERIOD_ID
    );

}
