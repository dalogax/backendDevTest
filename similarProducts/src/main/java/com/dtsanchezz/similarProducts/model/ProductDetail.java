package com.dtsanchezz.similarProducts.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@NoArgsConstructor
@RequiredArgsConstructor
public class ProductDetail {

    @NonNull
    private String id;

    @NonNull
    private String name;

    @NonNull
    private Double price;

    @NonNull
    private Boolean availability;

}
