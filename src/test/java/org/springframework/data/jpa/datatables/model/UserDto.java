package org.springframework.data.jpa.datatables.model;

public class UserDto {

  private final Integer id;
  private final String username;
  private final String role;
  private final String status;

  public UserDto(Integer id, String username, String role, String status) {
    this.id = id;
    this.username = username;
    this.role = role;
    this.status = status;
  }

  public Integer getId() {
    return id;
  }

  public String getUsername() {
    return username;
  }

  public String getRole() {
    return role;
  }

  public String getStatus() {
    return status;
  }

}
