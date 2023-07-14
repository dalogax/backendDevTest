package com.alejandro.api.model;


import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductDetailEntity {

    private String id;

    private String name;

    private int price;

    private boolean availability;
}
