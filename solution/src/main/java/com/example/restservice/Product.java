package com.example.restservice;

public class Product {
    public String id;
    public String name;
    public float price;
    public boolean availability;

    public Product(String id, String name, float price, boolean availability) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.availability = availability;
    }
    public Product() {
        this.id = "";
        this.name = "";
        this.price = 0;
        this.availability = false;
    }
}
