package com.example.demo.core.domain;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class Product {

    private String id;
    private String name;
    private Number price;
    private boolean availability;
}
