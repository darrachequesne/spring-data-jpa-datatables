package org.springframework.data.jpa.datatables.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Bill {

  @Id
  @GeneratedValue
  private Integer id;

  private String description;

  private Integer amount;

  private Boolean hasBeenPayed;

  public Bill() {
    super();
  }

  public Integer getId() {
    return id;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Integer getAmount() {
    return amount;
  }

  public void setAmount(Integer amount) {
    this.amount = amount;
  }

  public Boolean getHasBeenPayed() {
    return hasBeenPayed;
  }

  public void setHasBeenPayed(Boolean hasBeenPayed) {
    this.hasBeenPayed = hasBeenPayed;
  }

}
