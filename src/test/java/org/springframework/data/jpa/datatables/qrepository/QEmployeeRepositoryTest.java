package org.springframework.data.jpa.datatables.qrepository;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.datatables.Config;
import org.springframework.data.jpa.datatables.QConfig;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.data.jpa.datatables.model.Employee;
import org.springframework.data.jpa.datatables.model.EmployeeDto;
import org.springframework.data.jpa.datatables.model.QEmployee;
import org.springframework.data.jpa.datatables.repository.EmployeeRepositoryTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {Config.class, QConfig.class})
public class QEmployeeRepositoryTest extends EmployeeRepositoryTest {
    @Autowired
    private QEmployeeRepository employeeRepository;

    @Override
    protected DataTablesOutput<Employee> getOutput(DataTablesInput input) {
        return employeeRepository.findAll(input);
    }

    @Override
    protected DataTablesOutput<EmployeeDto> getOutput(DataTablesInput input, Function<Employee, EmployeeDto> converter) {
        return employeeRepository.findAll(input, converter);
    }

    @Test
    @Override
    public void withAnAdditionalSpecification() {
        DataTablesOutput<Employee> output = employeeRepository.findAll(input, QEmployee.employee.position.eq("Software Engineer"));
        assertThat(output.getRecordsFiltered()).isEqualTo(2);
        assertThat(output.getRecordsTotal()).isEqualTo(Employee.ALL.size());
    }

    @Test
    @Override
    public void withAPreFilteringSpecification() {
        DataTablesOutput<Employee> output = employeeRepository.findAll(input, null, QEmployee.employee.position.eq("Software Engineer"));
        assertThat(output.getRecordsFiltered()).isEqualTo(2);
        assertThat(output.getRecordsTotal()).isEqualTo(2);
    }
}