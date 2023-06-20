package com.pablolizarraga.solution.infrastructure.adapter.in.controller.rest;

import com.pablolizarraga.solution.application.port.in.SimilarProductServicePort;
import com.pablolizarraga.solution.domain.ProductDetail;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class SimilarProductRestController {

    public static final String GET_SIMILAR = "/product/{productId}/similar";

    private final SimilarProductServicePort similarProductService;

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(GET_SIMILAR)
    public List<ProductDetail> getSimilarProducts(@PathVariable String productId) {
        return similarProductService.getSimilarProducts(productId);
    }

}
