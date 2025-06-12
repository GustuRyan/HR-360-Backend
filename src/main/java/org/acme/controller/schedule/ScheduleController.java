package org.acme.controller.schedule;

import io.quarkus.security.Authenticated;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.acme.controller.auth.LoginController;
import org.acme.repository.schedule.ScheduleRepository;

import java.util.List;
import java.util.Map;

@Path("/api/v1/schedule")
@Authenticated
public class ScheduleController {

    @Inject
    ScheduleRepository scheduleRepository;

    @Inject
    LoginController loginController;

    public Long currentPeriodSchedule() {
        return scheduleRepository.currentPeriodSchedule(loginController.userProfile().employeeId());
    }

    @GET
    @Path("/today-schedule")
    public Map<String, Object> todaySchedule() {
        return scheduleRepository.todaySchedule(currentPeriodSchedule());
    }

    @POST
    @Path("/clock-in")
    public String clockIn() {
        return scheduleRepository.clockIn(currentPeriodSchedule());
    }

    @POST
    @Path("/clock-out")
    public String clockOut() {
        return scheduleRepository.clockOut(currentPeriodSchedule());
    }

    @GET
    @Path("/week-list")
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, Object> weekList(@QueryParam("month") int month,
                        @QueryParam("year") int year)
    {
        return scheduleRepository.getWeekCountInMonth(month, year);
    }

    @GET
    @Path("/weekly")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Object> weeklySchedule(@QueryParam("week") int week,
                                       @QueryParam("month") int month,
                                       @QueryParam("year") int year
    ) {
        return scheduleRepository.weekScheduleList(loginController.userProfile().employeeId(), week, month, year);
    }
}
