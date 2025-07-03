package org.acme.repository.employee;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.annotation.Nullable;
import org.jdbi.v3.core.mapper.reflect.ColumnName;

import java.time.LocalDate;
import java.util.Date;

public record EmployeeEnt(
        @JsonIgnore @ColumnName(EMPLOYEE_ID) Long employeeId,
        @ColumnName(FULL_NAME) String fullName,
        @ColumnName(ADDRESS) String address,
        @ColumnName(EMAIL) String email,
        @ColumnName(PHONE) String phone,

        @JsonProperty("gender")
        @Nullable @ColumnName(GENDER) Integer gender,

        @Nullable @ColumnName(BIRTH_DATE) LocalDate birthDate,
        @Nullable @ColumnName(BIRTH_PLACE) String birthPlace,
        @Nullable @ColumnName(BLOOD_TYPE) String bloodType,
        @Nullable @ColumnName(MARITAL_ID) Long maritalId,
        @Nullable @ColumnName(NATIONALITY_ID) Long nationalityId,
        @Nullable @ColumnName(MARITAL_STATUS) String maritalStatus,
        @Nullable @ColumnName(NATIONALITY_NAME) String nationalityName,
        @Nullable @ColumnName(NAME_EMG) String nameEmg,
        @Nullable @ColumnName(PHONE_EMG) String phoneEmg,
        @Nullable @ColumnName(ADDRESS_EMG) String addressEmg
) {
    public static final String TABLE_NAME = "hr_employee";

    public static final String EMPLOYEE_ID = "EMPLOYEE_ID";
    public static final String FULL_NAME = "FULL_NAME";
    public static final String ADDRESS = "ADDRESS";
    public static final String EMAIL = "EMAIL";
    public static final String PHONE = "phone";
    public static final String GENDER = "SEX";
    public static final String BIRTH_DATE = "BIRTH_DATE";
    public static final String BIRTH_PLACE = "BIRTH_PLACE";
    public static final String BLOOD_TYPE = "BLOOD_TYPE";
    public static final String MARITAL_ID = "MARITAL_ID";
    public static final String NATIONALITY_ID = "NATIONALITY_ID";
    public static final String MARITAL_STATUS = "MARITAL_STATUS";
    public static final String NATIONALITY_NAME = "NATIONALITY_NAME";
    public static final String NAME_EMG = "EMERGENCY_NAME";
    public static final String PHONE_EMG = "PHONE_EMG";
    public static final String ADDRESS_EMG = "ADDRESS_EMG";

    public static final String FIELDS = String.join(",", EMPLOYEE_ID, FULL_NAME, ADDRESS, EMAIL, PHONE, GENDER, BIRTH_DATE, BIRTH_PLACE, BLOOD_TYPE, MARITAL_ID, NATIONALITY_ID, NAME_EMG, PHONE_EMG, ADDRESS_EMG);

}
