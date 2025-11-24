package com.example.demo.products.infrastructure.entity;


import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString

//@Entity
//@Table(name="products", schema = "mydb")
public class ProductEntity {

    //@Id
    @NotBlank(message = "ID is required and cannot be empty")
    @Size(min = 1, message = "ID must have at least 1 character")
    //@Column(unique = true, nullable = false, length = 255)
    private String id;

    @NotBlank(message = "Name is required and cannot be empty")
    @Size(min = 1, message = "Name must have at least 1 character")
    //@Column(nullable = false, length = 255)
    private String name;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Price must be positive")
    //@Column(nullable = false)
    private Double price;

    @NotNull(message = "Availability is required")
    //@Column(nullable = false)
    private Boolean availability;


}
