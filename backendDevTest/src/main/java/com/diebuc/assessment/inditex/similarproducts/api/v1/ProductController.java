package com.diebuc.assessment.inditex.similarproducts.api.v1;

import com.diebuc.assessment.inditex.similarproducts.dto.ProductDetailDTO;
import com.diebuc.assessment.inditex.similarproducts.service.SimilarProductsService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Validated
@Tag(name = "Product", description = "Product Similar Details controller")
@RestController
@RequestMapping("/api/v1")
public class ProductController implements ProductApi {

    private SimilarProductsService similarProductsService;

    public ProductController(SimilarProductsService similarProductsService) {
        this.similarProductsService = similarProductsService;
    }

    @Override
    public Mono<ResponseEntity<Flux<ProductDetailDTO>>> _getProductSimilar(String productId, ServerWebExchange exchange) {
        Flux<ProductDetailDTO> productDetails = similarProductsService.getSimilarProducts(productId);
        Mono<ResponseEntity<Flux<ProductDetailDTO>>> result = Mono.just(ResponseEntity.ok(productDetails));
        return result;
    }

}
