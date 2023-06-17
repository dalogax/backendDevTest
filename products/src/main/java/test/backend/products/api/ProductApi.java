package test.backend.products.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import test.backend.products.entity.ProductDetail;
import test.backend.products.useCase.RetrieveSortedSimilarProductsUseCase;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/product")
public class ProductApi {

    private final RetrieveSortedSimilarProductsUseCase retrieveSortedSimilarProductsUseCase;

    public ProductApi(RetrieveSortedSimilarProductsUseCase retrieveSortedSimilarProductsUseCase) {
        this.retrieveSortedSimilarProductsUseCase = retrieveSortedSimilarProductsUseCase;
    }

    @GetMapping("/{productId}/similar")
    public ResponseEntity<List<ProductDetail>> getSimilarProducts(@PathVariable String productId) {
        log.info("Retrieve similar products to product with id: " + productId);
        List<ProductDetail> similarProducts = retrieveSortedSimilarProductsUseCase.execute(productId);
        return ResponseEntity.ok(similarProducts);
    }

}
