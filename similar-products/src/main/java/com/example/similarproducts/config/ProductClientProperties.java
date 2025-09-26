package com.example.similarproducts.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@AllArgsConstructor
@ConfigurationProperties(prefix = "clients.products")
public class ProductClientProperties {

  private String baseUrl;
  private int connectTimeoutMs;
  private int readTimeoutMs;
  private String product;
  private String similarIds;

}