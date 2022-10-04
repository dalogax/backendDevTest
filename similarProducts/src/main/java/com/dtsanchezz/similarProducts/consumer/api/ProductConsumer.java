package com.dtsanchezz.similarProducts.consumer.api;

import com.dtsanchezz.similarProducts.consumer.ConsumerBase;
import com.dtsanchezz.similarProducts.model.ProductDetail;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class ProductConsumer extends ConsumerBase {

    @Value("${product.mock.url}")
    private String productUrl;

    private static final String GET_PRODUCT_SIMILAR_IDS = "%s/{productId}/similarids";
    private static final String GET_PRODUCT_DETAIL = "%s/{productId}";

    @Cacheable("similar")
    public List<String> getSimilarIds(final String productId) {

        final String[] response = (String[]) this.consume(
                String.format(GET_PRODUCT_SIMILAR_IDS, this.productUrl),
                String[].class,
                productId
        );

        return Arrays.asList(response);
    }

    @Cacheable("product")
    public ProductDetail getProductDetail(final String productId) {

        return (ProductDetail) this.consume(
                String.format(GET_PRODUCT_DETAIL, this.productUrl),
                ProductDetail.class,
                productId
        );
    }
}
