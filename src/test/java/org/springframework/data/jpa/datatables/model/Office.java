package org.springframework.data.jpa.datatables.model;

import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Transient;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.Setter;
import lombok.experimental.Tolerate;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Data
@Setter(AccessLevel.NONE)
@Builder
@Entity
@Table(name = "offices")
public class Office {
    @Id private int id;
    private String city;
    private String country;

    @Tolerate
    private Office() {}

    @Transient
    public static Office TOKYO = Office.builder()
            .id(1)
            .city("Tokyo")
            .country("Japan")
            .build();

    @Transient
    public static Office LONDON = Office.builder()
            .id(2)
            .city("London")
            .country("UK")
            .build();

    @Transient
    public static Office SAN_FRANCISCO = Office.builder()
            .id(3)
            .city("San Francisco")
            .country("USA")
            .build();

    @Transient
    public static Office NEW_YORK = Office.builder()
            .id(4)
            .city("New York")
            .country("USA")
            .build();
}