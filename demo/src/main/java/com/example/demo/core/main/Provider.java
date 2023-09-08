package com.example.demo.core.main;

import com.example.demo.core.use_case.GetSimilarProducts;

public class Provider {

    private final Dependencies dependencies;

    public Provider(Dependencies dependencies) {
        this.dependencies = dependencies;
    }

    public GetSimilarProducts getSimilarProducts() {
        return new GetSimilarProducts(dependencies.productRepository());
    }
}
