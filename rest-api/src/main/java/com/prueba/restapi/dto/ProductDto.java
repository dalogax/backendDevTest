package com.prueba.restapi.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductDto {
    
    private int id;
    private String name;
    private BigDecimal price;
    private boolean availability; 
}
