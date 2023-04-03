package com.example.proxy.dto;

import lombok.Data;
import lombok.NonNull;

@Data
public class ProductDetailDTO {
    @NonNull
    private String id;
    @NonNull
    private String name;
    private long price;
    private boolean availability;
}
