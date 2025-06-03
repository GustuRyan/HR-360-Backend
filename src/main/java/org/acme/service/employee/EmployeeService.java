package org.acme.service.employee;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.acme.dto.employee.EmployeeDTO;
import org.acme.repository.employee.EmployeeEnt;
import org.acme.repository.employee.EmployeeRepository;

@ApplicationScoped
public class EmployeeService {
    @Inject
    EmployeeRepository employeeRepository;

    public String updateEmployee(Long id, EmployeeDTO dto) {

        EmployeeEnt existing = employeeRepository.getEmployeeDetail(id);

        EmployeeEnt updated = new EmployeeEnt(
                id,
                dto.fullName != null ? dto.fullName : existing.fullName(),
                dto.address != null ? dto.address : existing.address(),
                dto.email != null ? dto.email : existing.email(),
                dto.phone != null ? dto.phone : existing.phone(),
                dto.gender != null ? dto.gender : existing.gender(),
                dto.birthDate != null ? dto.birthDate : existing.birthDate(),
                dto.birthPlace != null ? dto.birthPlace : existing.birthPlace(),
                dto.bloodType != null ? dto.bloodType : existing.bloodType(),
                dto.maritalStatus != null ? dto.maritalStatus : existing.maritalStatus(),
                dto.nationalityId != null ? dto.nationalityId : existing.nationalityId(),
                dto.nameEmg != null ? dto.nameEmg : existing.nameEmg(),
                dto.phoneEmg != null ? dto.phoneEmg : existing.phoneEmg(),
                dto.addressEmg != null ? dto.addressEmg : existing.addressEmg()
        );

        return employeeRepository.updateEmployeeDTO(updated);
    }

}
