package test.backend.products.entity;

import lombok.Data;

@Data
public class ProductDetail {

    private String id;
    private String name;
    private Integer price;
    private Boolean availability;

}
