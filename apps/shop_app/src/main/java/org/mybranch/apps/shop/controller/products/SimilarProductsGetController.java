package org.mybranch.apps.shop.controller.products;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.mybranch.apps.shop.api.dto.ProductDetailDTO;
import org.mybranch.shop.products.application.search_similar_products.FindSimilarProductsById;
import org.mybranch.shop.products.domain.ProductDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/product")
@RequiredArgsConstructor
public final class SimilarProductsGetController {

    private final FindSimilarProductsById findSimilarProductsById;
    private final ObjectMapper objectMapper;

    @GetMapping("/{productId}/similar")
    public ResponseEntity<?> findSimilarProductsById(@PathVariable String productId) {

        List<ProductDetail> similarProducts = findSimilarProductsById.findSimilarProductsById(productId);

        if (CollectionUtils.isEmpty(similarProducts)) {
            // We return an empty list
            return ResponseEntity.ok().body(Collections.emptyList());
        }

        List<ProductDetailDTO> similarProductsDTO = similarProducts.stream()
                .map(productDetail -> objectMapper.convertValue(productDetail, ProductDetailDTO.class))
                .collect(Collectors.toList());

        return ResponseEntity.ok().body(similarProductsDTO);
    }

}
