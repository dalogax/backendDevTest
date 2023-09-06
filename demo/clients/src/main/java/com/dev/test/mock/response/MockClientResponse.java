package com.dev.test.mock.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class MockClientResponse {
  @JsonProperty("id")
  private String id;

  @JsonProperty("name")
  private String name;

  @JsonProperty("price")
  private Integer price;

  @JsonProperty("availability")
  private Boolean availability;
}
