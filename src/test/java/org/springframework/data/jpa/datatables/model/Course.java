package org.springframework.data.jpa.datatables.model;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "course")
public class Course {

  @Id
  @GeneratedValue
  private Long id;

  private String name;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "course_type_id")
  private CourseType type;

  public Course() {}

  public Long getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public CourseType getType() {
    return type;
  }

  public void setType(CourseType type) {
    this.type = type;
  }

}
