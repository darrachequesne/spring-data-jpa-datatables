package org.springframework.data.jpa.datatables.repository;

import static org.assertj.core.api.Assertions.assertThat;

import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.datatables.Config;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.data.jpa.datatables.model.A;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = Config.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class RelationshipsRepositoryTest {
    @Autowired
    private RelationshipsRepository repository;

    @Autowired
    private SessionFactory sessionFactory;

    protected DataTablesOutput<A> getOutput(DataTablesInput input) {
        return repository.findAll(input);
    }

    @BeforeAll
    public void init() {
        repository.saveAll(A.ALL);
    }

    @Test
    void manyToOne() {
        DataTablesInput input = createInput();

        input.getColumn("c.someValue").setSearchValue("VAL2");
        DataTablesOutput<A> output = getOutput(input);
        assertThat(output.getData()).containsOnly(A.A2, A.A3);
    }

    @Test
    void twoLevels() {
        DataTablesInput input = createInput();

        input.getColumn("c.parent.someValue").setSearchValue("VAL3");
        DataTablesOutput<A> output = getOutput(input);
        assertThat(output.getData()).containsOnly(A.A1);
    }

    @Test
    void embedded() {
        DataTablesInput input = createInput();

        input.getColumn("d.someValue").setSearchValue("D1");
        DataTablesOutput<A> output = getOutput(input);
        assertThat(output.getData()).containsOnly(A.A1);
    }

    @Test
    protected void checkFetchJoin() {
        Statistics statistics = sessionFactory.getStatistics();
        statistics.setStatisticsEnabled(true);

        DataTablesOutput<A> output = getOutput(createInput());

        assertThat(output.getRecordsFiltered()).isEqualTo(3);
        assertThat(statistics.getPrepareStatementCount()).isEqualTo(2);
        assertThat(statistics.getEntityLoadCount()).isEqualTo(3 /* A */ + 3 /* C */);
    }

    protected static DataTablesInput createInput() {
        DataTablesInput input = new DataTablesInput();
        input.addColumn("name", true, true, "");
        input.addColumn("b.name", true, true, "");
        input.addColumn("b.someValue", true, true, "");
        input.addColumn("c.name", true, true, "");
        input.addColumn("c.someValue", true, true, "");
        input.addColumn("c.parent.name", true, true, "");
        input.addColumn("c.parent.someValue", true, true, "");
        input.addColumn("d.someValue", true, true, "");
        return input;
    }
}