package org.springframework.data.jpa.datatables.mapping;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Column {

  /**
   * Column's data source
   * 
   * @see http://datatables.net/reference/option/columns.data
   */
  @NotBlank
  private String data;

  /**
   * Column's name
   * 
   * @see http://datatables.net/reference/option/columns.name
   */
  private String name;

  /**
   * Flag to indicate if this column is searchable (true) or not (false).
   * 
   * @see http://datatables.net/reference/option/columns.searchable
   */
  @NotNull
  private Boolean searchable;

  /**
   * Flag to indicate if this column is orderable (true) or not (false).
   * 
   * @see http://datatables.net/reference/option/columns.orderable
   */
  @NotNull
  private Boolean orderable;

  /**
   * Search value to apply to this specific column.
   */
  @NotNull
  private Search search;

  /**
   * Set the search value to apply to this column
   *
   * @param searchValue if any, the search value to apply
   */
  public void setSearchValue(String searchValue) {
    this.search.setValue(searchValue);
  }

}
