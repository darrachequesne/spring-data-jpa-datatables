package org.springframework.data.jpa.datatables.model;

import lombok.Value;

@Value
public class EmployeeDto {
    private int id;
    private String firstName;
    private String lastName;

    public static EmployeeDto AIRI_SATOU = new EmployeeDto(5407, "Airi", "Satou");

}