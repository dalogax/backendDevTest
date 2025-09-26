package com.example.similarproducts.model;

import lombok.Data;

@Data
public class ProductDetail {

  private String id;
  private String name;
  private Double price;
  private Boolean availability;
}
