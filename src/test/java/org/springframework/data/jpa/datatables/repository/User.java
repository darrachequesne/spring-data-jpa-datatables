package org.springframework.data.jpa.datatables.repository;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import org.hibernate.annotations.Nationalized;

@Entity
@Data
@Table(name = "users")
public class User {

    @Id long id;
    @Nationalized String name;

    public User() {
    }

    public User(long id, String name) {
        this.id = id;
        this.name = name;
    }
}
