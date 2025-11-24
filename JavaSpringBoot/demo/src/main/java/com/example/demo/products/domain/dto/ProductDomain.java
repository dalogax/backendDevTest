package com.example.demo.products.domain.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString

public class ProductDomain {
    private String id;
    private String name;
    private Double price;
    private Boolean availability;
}
