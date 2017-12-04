package org.springframework.data.jpa.datatables.repository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.datatables.Config;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.data.jpa.datatables.model.Employee;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = Config.class)
public class EmployeeRepositoryTest {
    @Autowired
    private EmployeeRepository employeeRepository;

    protected DataTablesOutput<Employee> getOutput(DataTablesInput input) {
        return employeeRepository.findAll(input);
    }

    @Before
    public void init() {
        employeeRepository.deleteAll();
        employeeRepository.save(asList(
                Employee.AIRI_SATOU,
                Employee.ANGELICA_RAMOS,
                Employee.ASHTON_COX,
                Employee.BRADLEY_GREER,
                Employee.BRENDEN_WAGNER,
                Employee.BRIELLE_WILLIAMSON
        ));
    }

    @Test
    public void testMultipleFilterOnManyToOneRelationship() {
        DataTablesInput input = getBasicInput();
        input.getColumn("office.city").setSearchValue("new york");
        input.getColumn("office.country").setSearchValue("USA");

        DataTablesOutput<Employee> output = getOutput(input);

        assertEquals(1, output.getRecordsFiltered());
        assertTrue(output.getData().contains(Employee.BRIELLE_WILLIAMSON));
    }

    private static DataTablesInput getBasicInput() {
        DataTablesInput input = new DataTablesInput();
        input.addColumn("id", true, true, "");
        input.addColumn("firstName", true, true, "");
        input.addColumn("lastName", true, true, "");
        input.addColumn("fullName", false, true, "");
        input.addColumn("position", true, true, "");
        input.addColumn("action_column", false, false, "");

        input.addColumn("office.id", true, false, "");
        input.addColumn("office.city", true, true, "");
        input.addColumn("office.country", true, true, "");

        input.addOrder("id", true);
        return input;
    }
}