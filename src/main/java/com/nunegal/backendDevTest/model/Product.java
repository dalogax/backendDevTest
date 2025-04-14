package com.nunegal.backendDevTest.model;

// This class represents a product with its details such as ID, name, price, and availability.

// ✅ ¿Qué hace este cliente?
// Usa RestTemplate para hacer llamadas HTTP.

// Inyecta la base de la URL desde application.properties (external.api.url).

// Tiene manejo básico de errores envolviendo en RuntimeException.

public class Product {
    private String id;
    private String name;
    private Double price;
    private Boolean availability;

    // Default constructor
    public Product() {
    }

    // Parameterized constructor
    public Product(String id, String name, Double price, Boolean availability) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.availability = availability;
    }

    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Boolean getAvailability() {
        return availability;
    }

    public void setAvailability(Boolean availability) {
        this.availability = availability;
    }
}
