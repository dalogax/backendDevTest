package com.example.demo.products.application.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class ProductApplication {
    private String id;
    private String name;
    private Double price;
    private Boolean availability;
}
