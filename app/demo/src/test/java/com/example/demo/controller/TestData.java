package com.example.demo.controller;

import com.example.demo.model.Product;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TestData {

    public static final List<Product> givenSimilarProducts = IntStream
            .range(0, 10)
            .mapToObj(n -> generateRandomProduct())
            .collect(Collectors.toList());

    public static final Map<String, Product> givenSimilarProductMap = givenSimilarProducts.stream().collect(Collectors.toMap(
            Product::getId,
            p -> p
    ));

    public static final List<String> givenSimilarProductIds = givenSimilarProducts
            .stream()
            .map(Product::getId)
            .collect(Collectors.toList());

    private static Product generateRandomProduct() {
        return Product.builder()
                .id(UUID.randomUUID().toString())
                .price(Math.random() * 100 + 100)
                .name(String.format("The product name '%s'", Math.random()))
                .availability(Math.random() > 0.5)
                .build();
    }
}
