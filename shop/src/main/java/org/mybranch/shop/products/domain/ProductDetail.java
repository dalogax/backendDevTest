package org.mybranch.shop.products.domain;

import lombok.Data;

import java.math.BigDecimal;

@Data
public final class ProductDetail {

    private String id;
    private String name;
    private BigDecimal price;
    private Boolean availability;

}
