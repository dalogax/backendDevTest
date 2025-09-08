package org.backendDevTest.infra.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.backendDevTest.application.service.ProductService;
import org.backendDevTest.infra.model.ProductDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/product")
@CrossOrigin()
public class ProductApiControllerImpl implements ProductApi {

    @Autowired
    private ProductService productService;

    @Override
    @GetMapping()
    public ResponseEntity<List<ProductDetail>> getAllProducts() {
        try {
            return ResponseEntity.ok(productService.getProducts());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    @GetMapping("/{productId}")
    public ResponseEntity<ProductDetail> getProductId(@PathVariable("productId") String productId) {
        try {
            ProductDetail productDetail = productService.getProductId(productId);
            return ObjectUtils.isEmpty(productDetail) ? ResponseEntity.notFound().build() : ResponseEntity.ok(productDetail);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    @GetMapping("/{productId}/similar")
    public ResponseEntity<List<ProductDetail>> getSimilarProductDetails(@PathVariable("productId") String productId) {
        try {
            List<ProductDetail> details = productService.getSimilarProduct(productId);
            return details.isEmpty() ? ResponseEntity.notFound().build() : ResponseEntity.ok(details);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    @GetMapping("/{productId}/similarids")
    public ResponseEntity<List<String>> getSimilarProductsIds(@PathVariable("productId") String productId) {
        try {
            List<String> similarIds = productService.getSimilarIds(productId);
            return similarIds.isEmpty() ? ResponseEntity.notFound().build() : ResponseEntity.ok(similarIds);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}