package org.acme.controller.schedule;

import io.quarkus.security.Authenticated;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
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

    @PUT
    @Path("/clock-in")
    public String clockIn() {
        return scheduleRepository.clockIn(currentPeriodSchedule());
    }

    @PUT
    @Path("/clock-out")
    public String clockOut() {
        return scheduleRepository.clockOut(currentPeriodSchedule());
    }

    @GET
    @Path("/week-schedule-list")
    public List<Object> weekScheduleList() {
        return scheduleRepository.weekScheduleList(currentPeriodSchedule());
    }

}
