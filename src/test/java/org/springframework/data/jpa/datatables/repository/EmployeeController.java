package org.springframework.data.jpa.datatables.repository;

import jakarta.validation.Valid;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.data.jpa.datatables.model.Employee;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
class EmployeeController {
  private final EmployeeRepository employeeRepository;

  public EmployeeController(EmployeeRepository employeeRepository) {
    this.employeeRepository = employeeRepository;
  }

  @RequestMapping(value = "/employees", method = RequestMethod.GET)
  public DataTablesOutput<Employee> findEmployees(@Valid DataTablesInput input) {
    return employeeRepository.findAll(input);
  }

  @RequestMapping(value = "/employees", method = RequestMethod.POST)
  public DataTablesOutput<Employee> findEmployeesWithPOST(
      @Valid @RequestBody DataTablesInput input) {
    return employeeRepository.findAll(input);
  }
}
