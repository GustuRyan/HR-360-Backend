package org.acme.repository.auth;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import org.jdbi.v3.core.mapper.reflect.ColumnName;

import java.sql.Timestamp;

public record AuthEnt(
        @JsonIgnore @ColumnName(USER_ID) Long userId,
        @NotBlank @ColumnName(PASSWORD) String password,
        @NotBlank @ColumnName(EMAIL) String email,
        @NotBlank @ColumnName(FULL_NAME) String fullName,
        @ColumnName(REG_DATE) Timestamp regDate,
        @Positive @ColumnName(EMPLOYEE_ID) Long employeeId,
        @ColumnName(USER_STATUS) Integer userStatus,
        @ColumnName(ADMIN_STATUS) Integer adminStatus
) {
    public static final String TABLE_NAME = "hr_app_user";

    public static final String USER_ID = "USER_ID";
    public static final String PASSWORD = "PASSWORD";
    public static final String EMAIL = "EMAIL";
    public static final String FULL_NAME = "FULL_NAME";
    public static final String REG_DATE = "REG_DATE";
    public static final String EMPLOYEE_ID = "EMPLOYEE_ID";
    public static final String ADMIN_STATUS = "ADMIN_STATUS";
    public static final String USER_STATUS = "USER_STATUS";

    public static final String FIELDS = String.join(",", USER_ID, PASSWORD, EMAIL, FULL_NAME, REG_DATE, EMPLOYEE_ID, USER_STATUS, ADMIN_STATUS);
    public static final String BINDER = ":userId, :password, :email, :fullName, :regDate, :employeeId, :userStatus, :adminStatus";

    public AuthEnt withId(Long id) {
        return new AuthEnt(id, password, email, fullName, regDate, employeeId, userStatus, adminStatus);
    }
}
