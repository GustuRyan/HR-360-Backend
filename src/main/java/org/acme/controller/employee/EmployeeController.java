package org.acme.controller.employee;

import io.quarkus.security.Authenticated;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.acme.controller.auth.LoginController;
import org.acme.dto.employee.EmployeeDTO;
import org.acme.repository.employee.EmployeeEnt;
import org.acme.repository.employee.EmployeeRepository;
//import org.acme.service.employee.EmployeeService;

import java.util.Map;

@Path("/api/v1/employee")
@Authenticated
public class EmployeeController {

    @Inject
    EmployeeRepository employeeRepository;

    @Inject
    LoginController loginController;

    @Inject
//    EmployeeService employeeService;

    @GET
    @Path("/detail")
    public EmployeeEnt getEmployeeDetail() {
        return employeeRepository.getEmployeeDetail(loginController.userProfile().employeeId());
    }

    @PUT
    @Path("/update")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, String> updateEmployee(@Valid EmployeeEnt employee) {
        return employeeRepository.updateEmployee(loginController.userProfile().employeeId(), employee);
    }

//    @PUT
//    @Path("/update-dto")
//    @Consumes(MediaType.APPLICATION_JSON)
//    @Produces(MediaType.APPLICATION_JSON)
//    public String updateEmployeeDTO(@Valid EmployeeDTO employee) {
//        return employeeService.updateEmployee(loginController.userProfile().employeeId(), employee);
//    }

}
