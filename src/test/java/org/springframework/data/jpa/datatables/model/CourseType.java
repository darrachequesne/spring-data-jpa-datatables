package org.springframework.data.jpa.datatables.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "course_type")
public class CourseType {

  @Id
  @GeneratedValue
  private Long id;

  private String name;

  public CourseType() {}

  public Long getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

}
