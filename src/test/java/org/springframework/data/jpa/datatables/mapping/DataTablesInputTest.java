package org.springframework.data.jpa.datatables.mapping;

import org.junit.jupiter.api.Test;;

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
}
