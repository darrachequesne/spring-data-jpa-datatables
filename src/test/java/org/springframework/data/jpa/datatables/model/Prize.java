package org.springframework.data.jpa.datatables.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;

@Embeddable
public class Prize {

  @NotNull
  @Column(name = "prize_name")
  private String name;

  protected Prize() {}

  public Prize(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
}
