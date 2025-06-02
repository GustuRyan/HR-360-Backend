package org.acme.controller.employee;

import io.quarkus.security.Authenticated;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.acme.controller.auth.LoginController;
import org.acme.repository.employee.EmployeeEnt;
import org.acme.repository.employee.EmployeeRepository;

import java.util.Map;

@Path("/api/v1/employee")
@Authenticated
public class EmployeeController {

    @Inject
    EmployeeRepository employeeRepository;

    @Inject
    LoginController loginController;

    @GET
    @Path("/retrieve-detail")
    public Map<String, Object> retrieveEmployee() {
        return employeeRepository.retrieveEmployeeDetail(loginController.userProfile().employeeId());
    }

    @GET
    @Path("/detail")
    public EmployeeEnt getEmployeeDetail() {
        return employeeRepository.getEmployeeDetail(loginController.userProfile().employeeId());
    }

    @PUT
    @Path("/update")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String updateEmployee(@Valid EmployeeEnt employee) {
        return employeeRepository.updateEmployee(loginController.userProfile().employeeId(), employee);
    }

}
