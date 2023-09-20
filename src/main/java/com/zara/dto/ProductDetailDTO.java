package com.zara.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
public class ProductDetailDTO {
    private String id;
    private String name;
    private Double price;
    private Boolean availability;
}
