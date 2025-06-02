package org.acme.repository.schedule;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.WebApplicationException;
import org.jdbi.v3.core.Jdbi;

import java.sql.Timestamp;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static org.acme.repository.schedule.ScheduleEnt.*;

@ApplicationScoped
public class ScheduleRepository {

    @Inject
    Jdbi jdbi;

    private final Timestamp now = Timestamp.valueOf(LocalDateTime.now());
    private final String todayDate = String.valueOf(LocalDate.now().getDayOfMonth());

    public Long currentPeriod() {
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM yyyy", new Locale("id", "ID"));
        String monthYear = today.format(formatter);

        return jdbi.withHandle(handle ->
                handle.createQuery("SELECT PERIOD_ID FROM hr_period WHERE PERIOD = :period")
                        .bind("period", "April 2025")
                        .mapTo(Long.class)
                        .findOne()
                        .orElseThrow(() -> new WebApplicationException("Period not found", 404))
        );
    }

    public Long currentPeriodSchedule(Long employeeId) {
        var thisPeriod = currentPeriod();

        var query = String.format("SELECT %s FROM %s WHERE %s = :employeeId AND %s = :periodId", EMP_SCHEDULE_ID, TABLE_NAME, EMPLOYEE_ID, PERIOD_ID);

        return jdbi.withHandle(handle -> handle.createQuery(query)
                .bind("employeeId", employeeId)
                .bind("periodId", thisPeriod)
                .mapTo(Long.class)
                .findOne()
                .orElseThrow(() -> new WebApplicationException("Schedule not found", 404))
        );
    }

    public Map<String, Object> todaySchedule(Long empSchId) {
        var query = String.format("SELECT hr_schedule_symbol.SCHEDULE, hr_schedule_symbol.SYMBOL, hr_emp_schedule.IN%s, hr_emp_schedule.OUT%s FROM hr_schedule_symbol LEFT JOIN hr_emp_schedule ON hr_schedule_symbol.SCHEDULE_ID = hr_emp_schedule.D%s WHERE hr_emp_schedule.EMP_SCHEDULE_ID = :empSchId", todayDate, todayDate, todayDate);

        return jdbi.withHandle(handle -> handle.createQuery(query)
                .bind("empSchId", empSchId)
                .mapToMap()
                .findOne()
                .orElseThrow(() -> new WebApplicationException("Schedule Symbol not found", 404))
        );
    }

    public String clockIn(Long empSchId){
        var query =  String.format("UPDATE %s SET IN%s = :now WHERE %s = :empSchId AND IN%s IS NULL", TABLE_NAME, todayDate, EMP_SCHEDULE_ID, todayDate);

        int rowsAffected = jdbi.withHandle(handle -> handle.createUpdate(query)
                .bind("now", now)
                .bind("empSchId", empSchId)
                .execute()
        );

        if (rowsAffected > 0) {
            return String.format("Clock in berhasil pada %s", now);
        } else {
            return "Clock in gagal: Mungkin sudah melakukan clock in sebelumnya atau ID tidak ditemukan.";
        }
    }

    public String clockOut(Long empSchId){
        var query =  String.format("UPDATE %s SET OUT%s = :now WHERE %s = :empSchId AND OUT%s IS NULL", TABLE_NAME, todayDate, EMP_SCHEDULE_ID, todayDate);

        int rowsAffected = jdbi.withHandle(handle ->
                handle.createUpdate(query)
                        .bind("now", now)
                        .bind("empSchId", empSchId)
                        .execute()
        );

        if (rowsAffected > 0) {
            return String.format("Clock out berhasil pada %s", now);
        } else {
            return "Clock out gagal: Mungkin sudah melakukan clock out sebelumnya atau ID tidak ditemukan.";
        }
    }

    public Map<String, Object> perDateSchedule(Long empSchId, String date) {

        var query = String.format("SELECT hr_schedule_symbol.SCHEDULE, hr_schedule_symbol.SYMBOL, hr_emp_schedule.IN%s, hr_emp_schedule.OUT%s FROM hr_schedule_symbol LEFT JOIN hr_emp_schedule ON hr_schedule_symbol.SCHEDULE_ID = hr_emp_schedule.D%s WHERE hr_emp_schedule.EMP_SCHEDULE_ID = :empSchId", date, date, date);

        return jdbi.withHandle(handle -> handle.createQuery(query)
                .bind("empSchId", empSchId)
                .mapToMap()
                .findOne()
                .orElseThrow(() -> new WebApplicationException("Schedule Symbol not found", 404))
        );
    }

    public static List<String> getDatesInWeekOfMonth(int year, Month month, int weekNumber) {
        List<String> result = new ArrayList<>();
        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());

        WeekFields weekFields = WeekFields.of(DayOfWeek.SUNDAY, 1);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd");

        for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {
            int weekOfMonth = date.get(weekFields.weekOfMonth());
            if (weekOfMonth == weekNumber) {
                result.add(date.format(formatter));
            }
        }

        return result;
    }

    public List<Object> weekScheduleList(Long empSchId) {
        List<String> dates = getDatesInWeekOfMonth(2025, Month.APRIL, 3);
        List<Object> scheduleResult = new ArrayList<>();

        for (String date : dates) {
            scheduleResult.add(perDateSchedule(empSchId, date));
        }

        return scheduleResult;
    }
}
