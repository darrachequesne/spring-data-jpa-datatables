package org.springframework.data.jpa.datatables.repository;

import com.google.common.collect.Lists;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.datatables.Config;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.data.jpa.datatables.model.Employee;
import org.springframework.data.jpa.datatables.model.EmployeeDto;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = Config.class)
public class EmployeeRepositoryTest {
    protected DataTablesInput input;

    @Autowired
    private EmployeeRepository employeeRepository;

    protected DataTablesOutput<Employee> getOutput(DataTablesInput input) {
        return employeeRepository.findAll(input);
    }

    protected DataTablesOutput<EmployeeDto> getOutput(DataTablesInput input, Function<Employee, EmployeeDto> converter) {
        return employeeRepository.findAll(input, converter);
    }

    @Before
    public void init() {
        employeeRepository.deleteAll();
        employeeRepository.saveAll(Employee.ALL);
        input = getBasicInput();
    }

    @Test
    public void basic() {
        DataTablesOutput<Employee> output = getOutput(input);
        assertThat(output.getDraw()).isEqualTo(1);
        assertThat(output.getError()).isNull();
        assertThat(output.getRecordsFiltered()).isEqualTo(Employee.ALL.size());
        assertThat(output.getRecordsTotal()).isEqualTo(Employee.ALL.size());
        assertThat(output.getData()).containsAll(Employee.ALL);
    }

    @Test
    public void paginated() {
        input.setDraw(2);
        input.setLength(5);
        input.setStart(5);

        DataTablesOutput<Employee> output = getOutput(input);
        assertThat(output.getDraw()).isEqualTo(2);
        assertThat(output.getRecordsFiltered()).isEqualTo(Employee.ALL.size());
        assertThat(output.getRecordsTotal()).isEqualTo(Employee.ALL.size());
        assertThat(output.getData()).hasSize(Employee.ALL.size() % 5);
    }

    @Test
    public void sortAscending() {
        input.addOrder("age", true);

        DataTablesOutput<Employee> output = getOutput(input);
        assertThat(output.getData()).containsExactlyElementsOf(Employee.ALL_SORTED_BY_AGE);
    }

    @Test
    public void sortDescending() {
        input.addOrder("age", false);

        DataTablesOutput<Employee> output = getOutput(input);
        assertThat(output.getData()).containsExactlyElementsOf(Lists.reverse(Employee.ALL_SORTED_BY_AGE));
    }

    @Test
    public void globalFilter() {
        input.getSearch().setValue("William");

        DataTablesOutput<Employee> output = getOutput(input);
        assertThat(output.getData()).containsOnly(Employee.BRIELLE_WILLIAMSON);
    }

    @Test
    public void globalFilterIgnoreCaseIgnoreSpace() {
        input.getSearch().setValue(" aMoS  ");

        DataTablesOutput<Employee> output = getOutput(input);
        assertThat(output.getData()).containsOnly(Employee.ANGELICA_RAMOS);
    }

    @Test
    public void columnFilter() {
        input.getColumn("lastName").setSearchValue("  AmOs ");

        DataTablesOutput<Employee> output = getOutput(input);
        assertThat(output.getData()).containsOnly(Employee.ANGELICA_RAMOS);
    }

    @Test
    public void multipleColumnFilters() {
        input.getColumn("age").setSearchValue("28");
        input.getColumn("position").setSearchValue("Software");

        DataTablesOutput<Employee> output = getOutput(input);
        assertThat(output.getData()).containsOnly(Employee.BRENDEN_WAGNER);
    }

    @Test
    public void columnFilterWithMultipleCases() {
        input.getColumn("position").setSearchValue("Accountant+Junior Technical Author");

        DataTablesOutput<Employee> output = getOutput(input);
        assertThat(output.getRecordsFiltered()).isEqualTo(2);
        assertThat(output.getData()).containsOnly(Employee.AIRI_SATOU, Employee.ASHTON_COX);
    }

    @Test
    public void columnFilterWithNoCase() {
        input.getColumn("position").setSearchValue("+");

        DataTablesOutput<Employee> output = getOutput(input);
        assertThat(output.getRecordsFiltered()).isEqualTo(Employee.ALL.size());
    }

    @Test
    public void zeroLength() {
        input.setLength(0);

        DataTablesOutput<Employee> output = getOutput(input);
        assertThat(output.getRecordsFiltered()).isEqualTo(0);
        assertThat(output.getData()).hasSize(0);
    }

    @Test
    public void negativeLength() {
        input.setLength(-1);

        DataTablesOutput<Employee> output = getOutput(input);
        assertThat(output.getRecordsFiltered()).isEqualTo(Employee.ALL.size());
        assertThat(output.getRecordsTotal()).isEqualTo(Employee.ALL.size());
    }

    @Test
    public void multipleColumnFiltersOnManyToOneRelationship() {
        input.getColumn("office.city").setSearchValue("new york");
        input.getColumn("office.country").setSearchValue("USA");

        DataTablesOutput<Employee> output = getOutput(input);
        assertThat(output.getRecordsFiltered()).isEqualTo(1);
        assertThat(output.getData()).containsOnly(Employee.BRIELLE_WILLIAMSON);
    }

    @Test
    public void withConverter() {
        input.getColumn("firstName").setSearchValue("airi");

        DataTablesOutput<EmployeeDto> output = getOutput(input, employee ->
                new EmployeeDto(employee.getId(), employee.getFirstName(), employee.getLastName()));
        assertThat(output.getData()).containsOnly(EmployeeDto.AIRI_SATOU);
    }

    @Test
    public void withAnAdditionalSpecification() {
        DataTablesOutput<Employee> output = employeeRepository.findAll(input, new SoftwareEngineersOnly<>());
        assertThat(output.getRecordsFiltered()).isEqualTo(2);
        assertThat(output.getRecordsTotal()).isEqualTo(Employee.ALL.size());
    }

    @Test
    public void withAPreFilteringSpecification() {
        DataTablesOutput<Employee> output = employeeRepository.findAll(input, null, new SoftwareEngineersOnly<>());
        assertThat(output.getRecordsFiltered()).isEqualTo(2);
        assertThat(output.getRecordsTotal()).isEqualTo(2);
    }

    private class SoftwareEngineersOnly<T> implements Specification<T> {
        @Override
        public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
            return criteriaBuilder.equal(root.get("position"), "Software Engineer");
        }
    }

    @Test
    public void columnFilterWithNull() {
        input.getColumn("comment").setSearchValue("NULL");

        DataTablesOutput<Employee> output = getOutput(input);
        assertThat(output.getData()).containsOnly(Employee.AIRI_SATOU);
    }

    @Test
    public void columnFilterWithNullEscaped() {
        input.getColumn("comment").setSearchValue("\\NULL");

        DataTablesOutput<Employee> output = getOutput(input);
        assertThat(output.getData()).containsOnly(Employee.ANGELICA_RAMOS);
    }

    @Test
    public void columnFilterWithEscapeCharacters() {
        input.getColumn("comment").setSearchValue("foo~");
        DataTablesOutput<Employee> output = getOutput(input);
        assertThat(output.getData()).containsOnly(Employee.ASHTON_COX);

        input.getColumn("comment").setSearchValue("foo%");
        output = getOutput(input);
        assertThat(output.getData()).containsOnly(Employee.BRADLEY_GREER);

        input.getColumn("comment").setSearchValue("foo_");
        output = getOutput(input);
        assertThat(output.getData()).containsOnly(Employee.BRENDEN_WAGNER);
    }

    @Test
    public void columnFilterWithValueOrNull() {
        input.getColumn("comment").setSearchValue("@foo@@+NULL");

        DataTablesOutput<Employee> output = getOutput(input);
        assertThat(output.getData()).containsOnly(Employee.AIRI_SATOU, Employee.BRIELLE_WILLIAMSON);
    }

    @Test
    public void columnFilterBoolean() {
        input.getColumn("isWorkingRemotely").setSearchValue("true");

        DataTablesOutput<Employee> output = getOutput(input);
        assertThat(output.getData()).containsOnly(Employee.ASHTON_COX);
    }

    @Test
    public void columnFilterBooleanBothCases() {
        input.getColumn("isWorkingRemotely").setSearchValue("true+false");

        DataTablesOutput<Employee> output = getOutput(input);
        assertThat(output.getData()).containsAll(Employee.ALL);
    }

    @Test
    public void unknownColumn() {
        input.addColumn("unknown", true, true, "test");

        DataTablesOutput<Employee> output = getOutput(input);
        assertThat(output.getError()).isNotNull();
    }

    private static DataTablesInput getBasicInput() {
        DataTablesInput input = new DataTablesInput();
        input.addColumn("id", true, true, "");
        input.addColumn("firstName", true, true, "");
        input.addColumn("lastName", true, true, "");
        input.addColumn("fullName", false, true, "");
        input.addColumn("position", true, true, "");
        input.addColumn("age", true, true, "");
        input.addColumn("isWorkingRemotely", true, true, "");
        input.addColumn("comment", true, true, "");

        input.addColumn("action_column", false, false, "");

        input.addColumn("office.id", true, false, "");
        input.addColumn("office.city", true, true, "");
        input.addColumn("office.country", true, true, "");

        return input;
    }
}