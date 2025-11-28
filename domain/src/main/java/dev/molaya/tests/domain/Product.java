package dev.molaya.tests.domain;

import java.math.BigDecimal;
import lombok.Builder;

@Builder(toBuilder = true)
public record Product(String id, String name, BigDecimal price, boolean available) {}
