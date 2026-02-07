package com.globant.interview.david.msdavidmobilephone.domain.model;

import java.util.List;

public record SimilarProducts(List<Product> products) {

    public SimilarProducts {
        products = List.copyOf(products);
    }

    public static SimilarProducts empty() {
        return new SimilarProducts(List.of());
    }
}
