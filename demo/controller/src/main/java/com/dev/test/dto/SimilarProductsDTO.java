package com.dev.test.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder(toBuilder = true)
public class SimilarProductsDTO {
  private final List<ProductDetailDTO> details;
}
