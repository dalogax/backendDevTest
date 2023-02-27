package com.cibernos.similarproducts.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductInfoDto {
    private String id;
    private String name;
    private Double price;
    private Boolean availability;
}
