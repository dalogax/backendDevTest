package dev.molaya.tests.application.service;

import dev.molaya.tests.application.in.dto.GetSimilarProductsInput;
import dev.molaya.tests.application.out.dto.ProductCommand;
import dev.molaya.tests.domain.Product;
import java.util.Set;
import java.util.stream.Stream;

public class Stubs {
    public static GetSimilarProductsInput validInput(final String id) {
        return GetSimilarProductsInput.builder().productId(id).build();
    }

    public static ProductCommand validCommand(final String id) {
        return new ProductCommand(id);
    }

    public static Set<String> productIds() {
        return Set.of("2", "3", "4");
    }

    public static Product product(final String id) {
        return Product.builder().id(id).name("Product " + id).build();
    }

    public static Stream<GetSimilarProductsInput> validInputs() {
        return Stream.of(validInput("1"), validInput("2"));
    }

    public static Stream<Set<String>> productIdLists() {
        return Stream.of(Set.of(), Set.of("2"), Set.of("2", "3", "4", "5"));
    }
}
