package org.springframework.data.jpa.datatables.model;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class User {

	@Id
	@GeneratedValue
	private Integer id;

	private String username;

	@Enumerated(EnumType.STRING)
	private UserRole role;

	@Enumerated(EnumType.STRING)
	private UserStatus status;

	public User() {
		super();
	}

	public enum UserRole {
		ADMIN("admin"), AUTHOR("author"), USER("user");

		private String role;

		private UserRole(String role) {
			this.role = role;
		}

		public String getRole() {
			return role;
		}

	}

	public enum UserStatus {
		ACTIVE("active"), BLOCKED("blocked");

		private String status;

		private UserStatus(String status) {
			this.status = status;
		}

		public String getStatus() {
			return status;
		}

	}

	public Integer getId() {
		return id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public UserRole getRole() {
		return role;
	}

	public void setRole(UserRole role) {
		this.role = role;
	}

	public UserStatus getStatus() {
		return status;
	}

	public void setStatus(UserStatus status) {
		this.status = status;
	}

}
