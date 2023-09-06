package com.dev.test.domain.aggregate;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class ProductDetail {
  private final String id;
  private final String name;
  private final Integer price;
  private final Boolean availability;
}
