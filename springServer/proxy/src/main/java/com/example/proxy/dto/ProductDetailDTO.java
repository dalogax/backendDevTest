package com.example.proxy.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

@Data
@AllArgsConstructor
public class ProductDetailDTO {
    @NonNull
    private String id;
    @NonNull
    private String name;
    private double price;
    private boolean availability;

}
