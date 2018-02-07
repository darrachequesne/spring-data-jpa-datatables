package org.springframework.data.jpa.datatables.model;

import lombok.*;

import javax.persistence.*;
import java.util.Collections;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;

@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode(exclude = "b")
@ToString(exclude = "b")
@Builder
@Entity
@Table(name = "a")
public class A {

    private @Id String name;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name="a_id")
    private List<B> b;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "c_id")
    private C c;

    @Embedded
    private D d;

    private static C C1 = new C("C1", "VAL1", new C("C3", "VAL3", null));
    private static C C2 = new C("C2", "VAL2", null);

    public static A A1 = A.builder()
            .name("A1")
            .b(asList(
                    new B("B1", "VAL1"),
                    new B("B2", "VAL2")
            ))
            .c(C1)
            .d(new D("D1"))
            .build();

    public static A A2 = A.builder()
            .name("A2")
            .b(singletonList(
                    new B("B3", "VAL3")
            ))
            .c(C2)
            .d(new D("D2"))
            .build();

    public static A A3 = A.builder()
            .name("A3")
            .b(Collections.emptyList())
            .c(C2)
            .build();

    public static List<A> ALL = asList(
            A1,
            A2,
            A3
    );

}