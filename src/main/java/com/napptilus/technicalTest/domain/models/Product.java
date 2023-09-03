package com.napptilus.technicalTest.domain.models;

import com.napptilus.technicalTest.domain.models.valueObjects.Price;
import com.napptilus.technicalTest.domain.models.valueObjects.ProductName;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Product {
    private final Integer id;
    private ProductName name;
    private Price price;
    private Boolean availability;

    public Product(Integer id, String name, Double price, Boolean availability) {
        this.id = id;
        this.name = new ProductName(name);
        this.price = new Price(price);
        this.availability = availability;
    }
}
