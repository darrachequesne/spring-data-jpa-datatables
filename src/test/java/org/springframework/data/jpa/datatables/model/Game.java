package org.springframework.data.jpa.datatables.model;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Game {

    @Id
    @GeneratedValue
    private Integer id;

    @Embedded
    private Prize prize;

    protected Game() {
    }

    public Game(Prize prize) {
        this.prize = prize;
    }

    public Integer getId() {
        return id;
    }

    public Prize getPrize() {
        return prize;
    }
}
