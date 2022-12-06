package com.similar.application;

import com.similar.domain.SimilarProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/product")
@RequiredArgsConstructor
public class SimilarProductController {

    private final SimilarProductService similarProductService;

    @GetMapping(path = "/{productId}/similar")
    ResponseEntity<?> getSimilarProductBy(@PathVariable final Long productId) {
        return ResponseEntity.status(HttpStatus.OK).body(similarProductService.getSimilarProductBy(productId));
    }

}
