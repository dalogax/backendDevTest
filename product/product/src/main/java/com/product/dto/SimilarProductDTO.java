package com.product.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class SimilarProductDTO {
    
    private  List<ProductDetailDTO> details;
}
