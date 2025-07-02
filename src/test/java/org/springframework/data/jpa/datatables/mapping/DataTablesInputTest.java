package org.springframework.data.jpa.datatables.mapping;

import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.*;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

public class DataTablesInputTest {

    @Test
    public void testParseSearchPanes() {
        DataTablesInput input = new DataTablesInput();
        HashMap<String, String> queryParams = new HashMap<>();
        queryParams.put("searchPanes.attr1.0", "1");
        queryParams.put("searchPanes.attr1.1", "2");
        queryParams.put("searchPanes.attr2.0", "3");
        queryParams.put("searchPanes.attr3.test", "4");
        queryParams.put("searchPanes.attr4.0", "5");
        queryParams.put("ignored", "6");
        queryParams.put("searchPanes.a.t.t.r.5.0", "7");

        input.parseSearchPanesFromQueryParams(queryParams, asList("attr1", "attr2", "a.t.t.r.5"));

        assertThat(input.getSearchPanes()).containsOnly(
                entry("attr1", Set.of("1", "2")),
                entry("attr2", Set.of("3")),
                entry("a.t.t.r.5", Set.of("7"))
        );
    }

    @Test
    public void testJavaSerialization() throws Exception {
        DataTablesInput input = new DataTablesInput();
        input.setDraw(1);
        input.setStart(0);
        input.setLength(10);
        input.setSearch(new Search("test", false));
        input.setOrder(Collections.singletonList(new Order(0, "asc")));

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(input);
        }

        try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray()))) {
            DataTablesInput deserialized = (DataTablesInput) ois.readObject();
            assertThat(deserialized.getDraw()).isEqualTo(input.getDraw());
            assertThat(deserialized.getStart()).isEqualTo(input.getStart());
            assertThat(deserialized.getLength()).isEqualTo(input.getLength());
            assertThat(deserialized.getSearch().getValue()).isEqualTo(input.getSearch().getValue());
            assertThat(deserialized.getOrder().get(0).getColumn()).isEqualTo(input.getOrder().get(0).getColumn());
        }
    }
}
