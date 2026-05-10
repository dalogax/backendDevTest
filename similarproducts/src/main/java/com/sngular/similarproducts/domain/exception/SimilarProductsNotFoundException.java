package com.sngular.similarproducts.domain.exception;

public class SimilarProductsNotFoundException extends RuntimeException {

    public SimilarProductsNotFoundException(String productId) {
        super("Similar products not found for productId: %s".formatted(productId));
    }

    public SimilarProductsNotFoundException(String productId, Throwable cause) {
        super("Similar products not found for productId: %s".formatted(productId), cause);
    }
}
