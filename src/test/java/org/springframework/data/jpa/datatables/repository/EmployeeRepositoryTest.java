package org.springframework.data.jpa.datatables.repository;

import static java.util.Arrays.asList;
import static java.util.Collections.emptySet;
import static org.assertj.core.api.Assertions.assertThat;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.datatables.Config;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.data.jpa.datatables.mapping.SearchPanes;
import org.springframework.data.jpa.datatables.model.Employee;
import org.springframework.data.jpa.datatables.model.EmployeeDto;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
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

    @BeforeEach
    public void init() {
        employeeRepository.deleteAll();
        employeeRepository.saveAll(Employee.ALL);
        input = getBasicInput();
    }

    @Test
    void basic() {
        DataTablesOutput<Employee> output = getOutput(input);
        assertThat(output.getDraw()).isEqualTo(1);
        assertThat(output.getError()).isNull();
        assertThat(output.getRecordsFiltered()).isEqualTo(Employee.ALL.size());
        assertThat(output.getRecordsTotal()).isEqualTo(Employee.ALL.size());
        assertThat(output.getData()).containsAll(Employee.ALL);
    }

    @Test
    void paginated() {
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
    void sortAscending() {
        input.addOrder("age", true);

        DataTablesOutput<Employee> output = getOutput(input);
        assertThat(output.getData()).containsExactlyElementsOf(Employee.ALL_SORTED_BY_AGE);
    }

    @Test
    void sortDescending() {
        input.addOrder("age", false);

        DataTablesOutput<Employee> output = getOutput(input);
        assertThat(output.getData()).containsExactlyElementsOf(Employee.ALL_SORTED_BY_AGE_DESC);
    }

    @Test
    void globalFilter() {
        input.getSearch().setValue("William");

        DataTablesOutput<Employee> output = getOutput(input);
        assertThat(output.getData()).containsOnly(Employee.BRIELLE_WILLIAMSON);
    }

    @Test
    void globalFilterIgnoreCaseIgnoreSpace() {
        input.getSearch().setValue(" aMoS  ");

        DataTablesOutput<Employee> output = getOutput(input);
        assertThat(output.getData()).containsOnly(Employee.ANGELICA_RAMOS);
    }

    @Test
    void columnFilter() {
        input.getColumn("lastName").setSearchValue("  AmOs ");

        DataTablesOutput<Employee> output = getOutput(input);
        assertThat(output.getData()).containsOnly(Employee.ANGELICA_RAMOS);
    }

    @Test
    void multipleColumnFilters() {
        input.getColumn("age").setSearchValue("28");
        input.getColumn("position").setSearchValue("Software");

        DataTablesOutput<Employee> output = getOutput(input);
        assertThat(output.getData()).containsOnly(Employee.BRENDEN_WAGNER);
    }

    @Test
    void columnFilterWithMultipleCases() {
        input.getColumn("position").setSearchValue("Accountant+Junior Technical Author");

        DataTablesOutput<Employee> output = getOutput(input);
        assertThat(output.getRecordsFiltered()).isEqualTo(2);
        assertThat(output.getData()).containsOnly(Employee.AIRI_SATOU, Employee.ASHTON_COX);
    }

    @Test
    void columnFilterWithNoCase() {
        input.getColumn("position").setSearchValue("+");

        DataTablesOutput<Employee> output = getOutput(input);
        assertThat(output.getRecordsFiltered()).isEqualTo(Employee.ALL.size());
    }

    @Test
    void zeroLength() {
        input.setLength(0);

        DataTablesOutput<Employee> output = getOutput(input);
        assertThat(output.getRecordsFiltered()).isZero();
        assertThat(output.getData()).isEmpty();
    }

    @Test
    void negativeLength() {
        input.setLength(-1);

        DataTablesOutput<Employee> output = getOutput(input);
        assertThat(output.getRecordsFiltered()).isEqualTo(Employee.ALL.size());
        assertThat(output.getRecordsTotal()).isEqualTo(Employee.ALL.size());
    }

    @Test
    void multipleColumnFiltersOnManyToOneRelationship() {
        input.getColumn("office.city").setSearchValue("new york");
        input.getColumn("office.country").setSearchValue("USA");

        DataTablesOutput<Employee> output = getOutput(input);
        assertThat(output.getRecordsFiltered()).isEqualTo(1);
        assertThat(output.getData()).containsOnly(Employee.BRIELLE_WILLIAMSON);
    }

    @Test
    void withConverter() {
        input.getColumn("firstName").setSearchValue("airi");

        DataTablesOutput<EmployeeDto> output = getOutput(input, employee ->
                new EmployeeDto(employee.getId(), employee.getFirstName(), employee.getLastName()));
        assertThat(output.getData()).containsOnly(EmployeeDto.AIRI_SATOU);
    }

    @Test
    protected void withAnAdditionalSpecification() {
        DataTablesOutput<Employee> output = employeeRepository.findAll(input, new SoftwareEngineersOnly<>());
        assertThat(output.getRecordsFiltered()).isEqualTo(2);
        assertThat(output.getRecordsTotal()).isEqualTo(Employee.ALL.size());
    }

    @Test
    protected void withAPreFilteringSpecification() {
        DataTablesOutput<Employee> output = employeeRepository.findAll(input, null, new SoftwareEngineersOnly<>());
        assertThat(output.getRecordsFiltered()).isEqualTo(2);
        assertThat(output.getRecordsTotal()).isEqualTo(2);
    }

    private static class SoftwareEngineersOnly<T> implements Specification<T> {
        @Override
        public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
            return criteriaBuilder.equal(root.get("position"), "Software Engineer");
        }
    }

    @Test
    void columnFilterWithNull() {
        input.getColumn("comment").setSearchValue("NULL");

        DataTablesOutput<Employee> output = getOutput(input);
        assertThat(output.getData()).containsOnly(Employee.AIRI_SATOU);
    }

    @Test
    void columnFilterWithNullEscaped() {
        input.getColumn("comment").setSearchValue("\\NULL");

        DataTablesOutput<Employee> output = getOutput(input);
        assertThat(output.getData()).containsOnly(Employee.ANGELICA_RAMOS);
    }

    @Test
    void columnFilterWithEscapeCharacters() {
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
    void columnFilterWithValueOrNull() {
        input.getColumn("comment").setSearchValue("@foo@@+NULL");

        DataTablesOutput<Employee> output = getOutput(input);
        assertThat(output.getData()).containsOnly(Employee.AIRI_SATOU, Employee.BRIELLE_WILLIAMSON);
    }

    @Test
    void columnFilterBoolean() {
        input.getColumn("isWorkingRemotely").setSearchValue("true");

        DataTablesOutput<Employee> output = getOutput(input);
        assertThat(output.getData()).containsOnly(Employee.ASHTON_COX);
    }

    @Test
    void columnFilterBooleanBothCases() {
        input.getColumn("isWorkingRemotely").setSearchValue("true+false");

        DataTablesOutput<Employee> output = getOutput(input);
        assertThat(output.getData()).containsAll(Employee.ALL);
    }

    @Test
    protected void unknownColumn() {
        input.addColumn("unknown", true, true, "test");

        DataTablesOutput<Employee> output = getOutput(input);
        assertThat(output.getError()).isNotNull();
    }

    @Test
    void withSearchPanes() {
        Map<String, Set<String>> searchPanes = new HashMap<>();
        searchPanes.put("position", new HashSet<>(asList("Software Engineer", "Integration Specialist")));
        searchPanes.put("age", emptySet());

        input.setSearchPanes(searchPanes);

        DataTablesOutput<Employee> output = getOutput(input);
        assertThat(output.getRecordsFiltered()).isEqualTo(3);
        assertThat(output.getSearchPanes()).isNotNull();

        assertThat(output.getSearchPanes().getOptions().get("position")).containsOnly(
                new SearchPanes.Item("Software Engineer", "Software Engineer", 2, 2),
                new SearchPanes.Item("Integration Specialist", "Integration Specialist", 1, 1)
        );
        assertThat(output.getSearchPanes().getOptions().get("age")).containsOnly(
                new SearchPanes.Item("28", "28", 1, 1),
                new SearchPanes.Item("41", "41", 1, 1),
                new SearchPanes.Item("61", "61", 1, 1)
        );
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