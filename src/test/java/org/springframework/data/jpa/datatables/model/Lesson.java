package org.springframework.data.jpa.datatables.model;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "lesson")
public class Lesson {

  @Id
  @GeneratedValue
  private Long id;

  private String name;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "course_id")
  private Course course;

  public Lesson() {}

  public Long getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Course getCourse() {
    return course;
  }

  public void setCourse(Course course) {
    this.course = course;
  }

}
