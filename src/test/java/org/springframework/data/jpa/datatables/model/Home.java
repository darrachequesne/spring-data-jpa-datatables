package org.springframework.data.jpa.datatables.model;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@Entity
public class Home {

	@Id
	@GeneratedValue
	private Integer id;

	private String town;

	@OneToMany(mappedBy = "home")
	private List<User> inhabitants;

	public Home() {
		super();
	}

	public Integer getId() {
		return id;
	}

	public String getTown() {
		return town;
	}

	public void setTown(String town) {
		this.town = town;
	}

	public List<User> getInhabitants() {
		return inhabitants;
	}

	public void setInhabitants(List<User> inhabitants) {
		this.inhabitants = inhabitants;
	}

}