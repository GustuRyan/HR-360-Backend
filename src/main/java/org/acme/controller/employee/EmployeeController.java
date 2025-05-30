package org.acme.controller.employee;

import io.quarkus.security.Authenticated;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import org.acme.controller.auth.LoginController;
import org.acme.repository.employee.EmployeeEnt;
import org.acme.repository.employee.EmployeeRepository;

@Path("/api/v1/employee")
@Authenticated
public class EmployeeController {

    @Inject
    EmployeeRepository employeeRepository;

    @Inject
    LoginController loginController;

    @GET
    @Path("/detail")
    public EmployeeEnt getEmployeeDetail() {
        return employeeRepository.getEmployeeDetail(loginController.userProfile().employeeId());
    }

}
