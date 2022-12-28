package org.springframework.data.jpa.datatables.repository;

import static org.assertj.core.api.Assertions.assertThat;

import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
public class RelationshipsRepositoryTest {
    protected DataTablesInput input;

    @Autowired
    private RelationshipsRepository repository;

    @Autowired
    private SessionFactory sessionFactory;

    protected DataTablesOutput<A> getOutput(DataTablesInput input) {
        return repository.findAll(input);
    }

    @BeforeEach
    public void init() {
        repository.deleteAll();
        repository.saveAll(A.ALL);
        input = getBasicInput();
    }

    @Test
    void manyToOne() {
        input.getColumn("c.someValue").setSearchValue("VAL2");
        DataTablesOutput<A> output = getOutput(input);
        assertThat(output.getData()).containsOnly(A.A2, A.A3);
    }

    @Test
    void twoLevels() {
        input.getColumn("c.parent.someValue").setSearchValue("VAL3");
        DataTablesOutput<A> output = getOutput(input);
        assertThat(output.getData()).containsOnly(A.A1);
    }

    @Test
    void embedded() {
        input.getColumn("d.someValue").setSearchValue("D1");
        DataTablesOutput<A> output = getOutput(input);
        assertThat(output.getData()).containsOnly(A.A1);
    }

    @Test
    protected void checkFetchJoin() {
        Statistics statistics = sessionFactory.getStatistics();
        statistics.setStatisticsEnabled(true);

        DataTablesOutput<A> output = getOutput(input);

        assertThat(output.getRecordsFiltered()).isEqualTo(3);
        assertThat(statistics.getPrepareStatementCount()).isEqualTo(2);
        assertThat(statistics.getEntityLoadCount()).isEqualTo(3 /* A */ + 3 /* C */);
    }

    private static DataTablesInput getBasicInput() {
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