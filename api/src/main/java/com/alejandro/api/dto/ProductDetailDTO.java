package com.alejandro.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductDetailDTO {

    @Schema(description = "Product´s id.", type = "String", example = "1", minLength = 1)
    private String id;

    @Schema(description = "Product´s name.", type = "String", example = "Shirt", minLength = 1)
    private String name;

    @Schema(description = "Product´s price.", type = "Number(int)", example = "9.99")
    private int price;

    @Schema(description = "Product´s availability", type = "Boolean", example = "true")
    private boolean availability;
}
