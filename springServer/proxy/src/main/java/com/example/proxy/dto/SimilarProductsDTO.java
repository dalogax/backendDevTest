package com.example.proxy.dto;

import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
public class SimilarProductsDTO {
    private Set<String> ids = new HashSet<>();
}
