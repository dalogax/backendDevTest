package com.inditex.myapp.domain.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@EqualsAndHashCode
public class ProductDetail {

    private String id;

    private String name;

    private BigDecimal price;

    private Boolean availability;
}
