package org.springframework.data.jpa.datatables.mapping;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.io.Serial;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Column implements Serializable {
  @Serial private static final long serialVersionUID = 5646239805793775339L;

  /**
   * Column's data source
   * 
   * @see <a href="https://datatables.net/reference/option/columns.data">https://datatables.net/reference/option/columns.data</a>
   */
  @NotBlank
  private String data;

  /**
   * Column's name
   * 
   * @see <a href="https://datatables.net/reference/option/columns.name">https://datatables.net/reference/option/columns.name</a>
   */
  private String name;

  /**
   * Flag to indicate if this column is searchable (true) or not (false).
   * 
   * @see <a href="https://datatables.net/reference/option/columns.searchable">https://datatables.net/reference/option/columns.searchable</a>
   */
  @NotNull
  private Boolean searchable;

  /**
   * Flag to indicate if this column is orderable (true) or not (false).
   * 
   * @see <a href="https://datatables.net/reference/option/columns.orderable">https://datatables.net/reference/option/columns.orderable</a>
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
