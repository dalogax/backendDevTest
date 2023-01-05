package org.mybranch.apps.shop.api.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public final class ProductDetailDTO {

    private String id;
    private String name;
    private BigDecimal price;
    private Boolean availability;

}
