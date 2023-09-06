package com.dev.test.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

@Data
@Builder(toBuilder = true)
public class ProductDetailDTO {
  @NonNull
  private final String id;
  @NonNull
  private final String name;
  @NonNull
  private final Integer price;
  @NonNull
  private final Boolean availability;
}
