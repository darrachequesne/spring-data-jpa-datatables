package org.springframework.data.jpa.datatables.mapping;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
public class SearchPanes implements Serializable {
  @Serial private static final long serialVersionUID = -3964402355226757466L;
  private Map<String, List<Item>> options;

  @Data
  @AllArgsConstructor
  public static class Item implements Serializable {
    @Serial private static final long serialVersionUID = -3951832089769040968L;
    private String label;
    private String value;
    private long total;
    private long count;
  }

}
