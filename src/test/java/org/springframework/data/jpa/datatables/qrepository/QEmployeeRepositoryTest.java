package org.springframework.data.jpa.datatables.qrepository;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.datatables.Config;
import org.springframework.data.jpa.datatables.QConfig;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.data.jpa.datatables.model.Employee;
import org.springframework.data.jpa.datatables.repository.EmployeeRepositoryTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {Config.class, QConfig.class})
public class QEmployeeRepositoryTest extends EmployeeRepositoryTest {
    @Autowired
    private QEmployeeRepository employeeRepository;

    @Override
    protected DataTablesOutput<Employee> getOutput(DataTablesInput input) {
        return employeeRepository.findAll(input);
    }
}