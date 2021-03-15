package org.springframework.data.jpa.datatables.mapping;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.*;
import java.util.regex.Pattern;

@Data
@AllArgsConstructor
public class SearchPanes {
  private Map<String, List<Item>> options;

  @Data
  @AllArgsConstructor
  public static class Item {
    private String label;
    private String value;
    private long total;
    private long count;
  }

}
