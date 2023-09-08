package com.example.demo.http.controller;

import com.example.demo.core.main.Provider;
import com.example.demo.http.dto.SimilarProductsResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.example.demo.core.use_case.GetSimilarProducts.*;

@RestController
@RequestMapping("/product")
public class SimilarProductsController {

    private final Provider provider;

    @Autowired
    public SimilarProductsController(Provider provider) {
        this.provider = provider;
    }

    @GetMapping("/{productId}/similar")
    public ResponseEntity<SimilarProductsResponse> getSimilarProducts(@PathVariable String productId) {
        Request request = new Request(productId);
        Response response = provider.getSimilarProducts().execute(request);

        return ResponseEntity.ok(new SimilarProductsResponse(response));
    }
}
