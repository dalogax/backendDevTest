package dev.molaya.tests.adapters;

import dev.molaya.tests.adapters.in.rest.gen.openapi.dto.ProductDetail;
import dev.molaya.tests.domain.Product;
import java.math.BigDecimal;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class Stubs {
    public static Product product() {
        return new Product("A", "Product 1", BigDecimal.valueOf(10.0), true);
    }

    public static Product product(String id) {
        return product().toBuilder().id(id).build();
    }

    public static ProductDetail productDetail() {
        final var detail = new ProductDetail();
        detail.setId("A");
        detail.setName("Product 1");
        detail.setPrice(BigDecimal.valueOf(10.0));
        detail.setAvailability(Boolean.TRUE);
        return detail;
    }

    public static dev.molaya.tests.adapters.out.client.gen.openapi.dto.ProductDetail productDetailOUT() {
        final var detail = new dev.molaya.tests.adapters.out.client.gen.openapi.dto.ProductDetail();
        detail.setId("A");
        detail.setName("Product 1");
        detail.setPrice(BigDecimal.valueOf(10.0));
        detail.setAvailability(Boolean.TRUE);
        return detail;
    }

    public static Set<String> similarIds() {
        return Stream.of("A", "B", "C").collect(Collectors.toSet());
    }
}
