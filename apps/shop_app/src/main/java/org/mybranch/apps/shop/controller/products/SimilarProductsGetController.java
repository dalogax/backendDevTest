package org.mybranch.apps.shop.controller.products;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.mybranch.apps.shop.api.SimilarProductsGetApi;
import org.mybranch.apps.shop.api.dto.ProductDetailDto;
import org.mybranch.shop.products.application.search_similar_products.FindSimilarProductsById;
import org.mybranch.shop.products.domain.ProductDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class SimilarProductsGetController implements SimilarProductsGetApi {

    private final FindSimilarProductsById findSimilarProductsById;
    private final ObjectMapper objectMapper;

    @Override
    public ResponseEntity<List<ProductDetailDto>> getSimilarProducts(String productId) {
        List<ProductDetail> similarProducts = findSimilarProductsById.findSimilarProductsById(productId);

        if (CollectionUtils.isEmpty(similarProducts)) {
            // We return an empty list in case there are no errors but no products are returned
            return ResponseEntity.ok().body(Collections.emptyList());
        }

        List<ProductDetailDto> similarProductsDTO = similarProducts.stream()
                .map(productDetail -> objectMapper.convertValue(productDetail, ProductDetailDto.class))
                .collect(Collectors.toList());

        return ResponseEntity.ok().body(similarProductsDTO);
    }

}
