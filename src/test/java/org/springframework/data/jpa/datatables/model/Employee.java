package org.springframework.data.jpa.datatables.model;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.Setter;
import lombok.experimental.Tolerate;

import javax.persistence.*;
import java.util.List;

import static java.util.Arrays.asList;

@Data
@Setter(AccessLevel.NONE)
@Builder
@Entity
@Table(name = "employees")
public class Employee {
    @Id private int id;
    private String firstName;
    private String lastName;
    private String position;
    private int age;
    private boolean isWorkingRemotely;
    private String comment;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "id_office")
    private Office office;

    @Tolerate
    private Employee() {}

    public static Employee AIRI_SATOU = Employee.builder()
            .id(5407)
            .firstName("Airi")
            .lastName("Satou")
            .position("Accountant")
            .age(33)
            .office(Office.TOKYO)
            .comment(null)
            .build();

    public static Employee ANGELICA_RAMOS = Employee.builder()
            .id(5797)
            .firstName("Angelica")
            .lastName("Ramos")
            .position("Chief Executive Officer (CEO)")
            .age(47)
            .office(Office.LONDON)
            .comment("\\NULL")
            .build();

    public static Employee ASHTON_COX = Employee.builder()
            .id(1562)
            .firstName("Ashton")
            .lastName("Cox")
            .position("Junior Technical Author")
            .age(66)
            .office(Office.SAN_FRANCISCO)
            .isWorkingRemotely(true)
            .comment("~foo~~")
            .build();

    public static Employee BRADLEY_GREER = Employee.builder()
            .id(2558)
            .firstName("Bradley")
            .lastName("Greer")
            .position("Software Engineer")
            .age(41)
            .office(Office.LONDON)
            .comment("%foo%%")
            .build();

    public static Employee BRENDEN_WAGNER = Employee.builder()
            .id(1314)
            .firstName("Brenden")
            .lastName("Wagner")
            .position("Software Engineer")
            .age(28)
            .office(Office.SAN_FRANCISCO)
            .comment("_foo__")
            .build();

    public static Employee BRIELLE_WILLIAMSON = Employee.builder()
            .id(4804)
            .firstName("Brielle")
            .lastName("Williamson")
            .position("Integration Specialist")
            .age(61)
            .office(Office.NEW_YORK)
            .comment("@foo@@")
            .build();

    public static List<Employee> ALL = asList(
            AIRI_SATOU,
            ANGELICA_RAMOS,
            ASHTON_COX,
            BRADLEY_GREER,
            BRENDEN_WAGNER,
            BRIELLE_WILLIAMSON
    );

    public static List<Employee> ALL_SORTED_BY_AGE = asList(
            BRENDEN_WAGNER,
            AIRI_SATOU,
            BRADLEY_GREER,
            ANGELICA_RAMOS,
            BRIELLE_WILLIAMSON,
            ASHTON_COX
    );

}