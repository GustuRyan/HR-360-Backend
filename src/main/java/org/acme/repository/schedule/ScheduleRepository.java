package org.acme.repository.schedule;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import org.jdbi.v3.core.Jdbi;

import java.sql.Timestamp;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import java.util.*;

import static org.acme.repository.schedule.ScheduleEnt.*;

@ApplicationScoped
public class ScheduleRepository {

    @Inject
    Jdbi jdbi;

    private final String todayDate = String.valueOf(LocalDate.now().getDayOfMonth());

    public Long currentPeriod() {
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM yyyy", new Locale("id", "ID"));
        String monthYear = today.format(formatter);

        return jdbi.withHandle(handle ->
                handle.createQuery("SELECT PERIOD_ID FROM hr_period WHERE PERIOD = :period")
                        .bind("period", "September 2022")
                        .mapTo(Long.class)
                        .findOne()
                        .orElseThrow(() -> new WebApplicationException(Response.status(Response.Status.NOT_FOUND)
                                .entity(Map.of("error", "current period not found"))
                                .build()))
        );
    }

    public Long searchPeriod(String period) {
        return jdbi.withHandle(handle ->
                handle.createQuery("SELECT PERIOD_ID FROM hr_period WHERE PERIOD = :period")
                        .bind("period", period)
                        .mapTo(Long.class)
                        .findOne()
                        .orElseThrow(() -> new WebApplicationException(Response.status(Response.Status.NOT_FOUND)
                                .entity(Map.of("error", "period not found"))
                                .build()))
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
                .orElseThrow(() -> new WebApplicationException(Response.status(Response.Status.NOT_FOUND)
                        .entity(Map.of("error", "current period schedule not found"))
                        .build()))
        );
    }

    public Map<String, Object> todaySchedule(Long empSchId) {
        var query = String.format("SELECT hr_schedule_symbol.SCHEDULE, hr_schedule_symbol.SYMBOL, hr_schedule_symbol.TIME_IN, hr_schedule_symbol.TIME_OUT, hr_emp_schedule.IN%s, hr_emp_schedule.OUT%s FROM hr_schedule_symbol LEFT JOIN hr_emp_schedule ON hr_schedule_symbol.SCHEDULE_ID = hr_emp_schedule.D%s WHERE hr_emp_schedule.EMP_SCHEDULE_ID = :empSchId", todayDate, todayDate, todayDate);

        return jdbi.withHandle(handle -> handle.createQuery(query)
                .bind("empSchId", empSchId)
                .mapToMap()
                .findOne()
                .map(result -> {
                    Map<String, Object> mappedResult = new HashMap<>(result);
                    mappedResult.put("in_time", result.get("in" + todayDate));
                    mappedResult.put("out_time", result.get("out" + todayDate));
                    mappedResult.remove("in" + todayDate);
                    mappedResult.remove("out" + todayDate);
                    return mappedResult;
                })
                .orElseThrow(() -> new WebApplicationException(Response.status(Response.Status.NOT_FOUND)
                        .entity(Map.of("error", "today schedule not found"))
                        .build()))
        );
    }

    public Map<String, String> clockIn(Long empSchId) {
        final Timestamp now = Timestamp.valueOf(LocalDateTime.now());

        var query = String.format(
                "UPDATE %s SET IN%s = :now WHERE %s = :empSchId AND IN%s IS NULL",
                TABLE_NAME, todayDate, EMP_SCHEDULE_ID, todayDate
        );

        int rowsAffected = jdbi.withHandle(handle -> handle.createUpdate(query)
                .bind("now", now)
                .bind("empSchId", empSchId)
                .execute()
        );

        Map<String, String> response = new HashMap<>();

        if (rowsAffected > 0) {
            response.put("message", String.format("Clock in success in %s", now));
            response.put("status", "Clock in successful");
        } else {
            response.put("message", "Already clock in | or ID can't be found");
            response.put( "status", "Clock in failed");
        }

        return response;
    }

    public Map<String, String> clockOut(Long empSchId) {
        final Timestamp now = Timestamp.valueOf(LocalDateTime.now());

        var query = String.format("UPDATE %s SET OUT%s = :now WHERE %s = :empSchId AND OUT%s IS NULL", TABLE_NAME, todayDate, EMP_SCHEDULE_ID, todayDate);

        int rowsAffected = jdbi.withHandle(handle ->
                handle.createUpdate(query)
                        .bind("now", now)
                        .bind("empSchId", empSchId)
                        .execute()
        );

        Map<String, String> response = new HashMap<>();

        if (rowsAffected > 0) {
            response.put("message", String.format("Clock out success in %s", now));
            response.put("status", "Clock out successful");
        } else {
            response.put("message", "Already clock out | or ID can't be found");
            response.put( "status", "Clock out failed");
        }

        return response;
    }

    public Map<String, Object> getWeekCountInMonth(int month, int year) {
        Month monthName = Month.of(month);
        String monthPeriod = monthName.getDisplayName(java.time.format.TextStyle.FULL, new Locale("id", "ID")) + " " + year;

        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());

        WeekFields weekFields = WeekFields.of(Locale.getDefault());

        int startWeek = start.get(weekFields.weekOfMonth());
        int endWeek = end.get(weekFields.weekOfMonth());

        return Map.of("date", monthPeriod, "weeks", (endWeek - startWeek + 1));
    }

    public Map<String, Object> perDateSchedule(Long empSchId, String date) {
        if (date != null && date.length() == 2 && date.startsWith("0")) {
            date = String.valueOf(Integer.parseInt(date));
        }
        var query = String.format("SELECT hr_schedule_symbol.SCHEDULE, hr_schedule_symbol.SYMBOL, hr_emp_schedule.IN%s, hr_emp_schedule.OUT%s FROM hr_schedule_symbol LEFT JOIN hr_emp_schedule ON hr_schedule_symbol.SCHEDULE_ID = hr_emp_schedule.D%s WHERE hr_emp_schedule.EMP_SCHEDULE_ID = :empSchId", date, date, date);

        return jdbi.withHandle(handle -> handle.createQuery(query)
                .bind("empSchId", empSchId)
                .mapToMap()
                .findOne()
                .orElseThrow(() -> new WebApplicationException(Response.status(Response.Status.NOT_FOUND)
                        .entity(Map.of("error", "today schedule not found"))
                        .build()))
        );
    }

    public Map<String, Object> perDateWeeklySchedule(Long empId, String date, int month, int year) {
        if (date != null && date.length() == 2 && date.startsWith("0")) {
            date = String.valueOf(Integer.parseInt(date));
        }

        Month monthName = Month.of(month);
        String monthPeriod = monthName.getDisplayName(java.time.format.TextStyle.FULL, new Locale("id", "ID")) + " " + year;

        Long periodId = searchPeriod(monthPeriod);

        var query = String.format("SELECT hr_schedule_symbol.SCHEDULE, hr_schedule_symbol.SYMBOL, hr_schedule_symbol.TIME_IN, hr_schedule_symbol.TIME_OUT, hr_emp_schedule.IN%s, hr_emp_schedule.OUT%s FROM hr_schedule_symbol LEFT JOIN hr_emp_schedule ON hr_schedule_symbol.SCHEDULE_ID = hr_emp_schedule.D%s WHERE hr_emp_schedule.EMPLOYEE_ID = :empId AND hr_emp_schedule.PERIOD_ID = :periodId", date, date, date);

        String finalDate = date;
        return jdbi.withHandle(handle -> handle.createQuery(query)
                .bind("empId", empId)
                .bind("periodId", periodId)
                .mapToMap()
                .findOne()
                .map(result -> {
                    Map<String, Object> mappedResult = new HashMap<>(result);
                    mappedResult.put("in_time", result.get("in" + finalDate));
                    mappedResult.put("out_time", result.get("out" + finalDate));
                    mappedResult.remove("in" + finalDate);
                    mappedResult.remove("out" + finalDate);
                    return mappedResult;
                })
                .orElseThrow(() -> new WebApplicationException(Response.status(Response.Status.NOT_FOUND)
                        .entity(Map.of("error", "today schedule not found"))
                        .build()))
        );
    }

    public static List<String> getDatesInWeekOfMonth(int year, int month, int weekNumber) {
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

    public List<Object> weekScheduleList(Long empId, int week, int month, int year) {
        List<String> dates = getDatesInWeekOfMonth(year, month, week);
        List<Object> scheduleResult = new ArrayList<>();

        for (String date : dates) {
            scheduleResult.add(perDateWeeklySchedule(empId, date, month, year));
        }

        return scheduleResult;
    }
}
