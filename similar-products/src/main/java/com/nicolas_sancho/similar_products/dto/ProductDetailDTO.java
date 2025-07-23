package com.nicolas_sancho.similar_products.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a single product detail.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductDetailDTO {

    private String id;          // required
    private String name;        // required
    private Double price;       // required
    private Boolean availability; // required
}
