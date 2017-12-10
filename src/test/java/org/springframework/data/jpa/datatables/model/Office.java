package org.springframework.data.jpa.datatables.model;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.Setter;
import lombok.experimental.Tolerate;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

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

    public static Office TOKYO = Office.builder()
            .id(1)
            .city("Tokyo")
            .country("Japan")
            .build();

    public static Office LONDON = Office.builder()
            .id(2)
            .city("London")
            .country("UK")
            .build();

    public static Office SAN_FRANCISCO = Office.builder()
            .id(3)
            .city("San Francisco")
            .country("USA")
            .build();

    public static Office NEW_YORK = Office.builder()
            .id(4)
            .city("New York")
            .country("USA")
            .build();
}