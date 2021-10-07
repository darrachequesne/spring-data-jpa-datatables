package org.springframework.data.jpa.datatables.mapping;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * <a href="https://datatables.net/extensions/searchbuilder/predefined">Predefined Searches</a>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchBuilder {
  private List<Criteria> criteria;
  private String logic;

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class Criteria {
    private String condition;
    private String data;
    private String origData;
    private String type;
    private List<String> value;
    private String logic;
    private List<Criteria> criteria;
  }
}
