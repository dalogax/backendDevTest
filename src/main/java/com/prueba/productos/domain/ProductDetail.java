package com.prueba.productos.domain;



import lombok.*;

@Builder
@Data
public class ProductDetail {

  private String id;
  private String name;
  private Double price;
  private Boolean availability;

  public ProductDetail() {
  }

  public ProductDetail(String id, String name, Double price, Boolean availability) {
    this.id = id;
    this.name = name;
    this.price = price;
    this.availability = availability;
  }
}
