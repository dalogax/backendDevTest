package com.product.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class ProductDetailDTO {
    
    private  String id;
    
    private  String name;
    
    private  Integer price;
    
    private  Boolean availability;
}
