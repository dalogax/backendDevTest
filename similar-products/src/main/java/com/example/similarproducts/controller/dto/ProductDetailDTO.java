package com.example.similarproducts.controller.dto;

public record ProductDetailDTO(
    String id,
    String name,
    double price,
    boolean availability
) {

}

