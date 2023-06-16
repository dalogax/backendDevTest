package com.cibernos.prueba.infrastructure.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

//@Entity
//Table(name = "users")
//@Data
//@NoArgsConstructor
public class ProductEntity {
    //@Id
    //GeneratedValue(strategy = GeneratioType.IDENTITY)
    private Integer id;
    private String name;
    private Double price;
    private Boolean availability;
}