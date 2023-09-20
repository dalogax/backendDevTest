package com.zara.controller.impl;

import com.zara.controller.SimilarProductController;
import com.zara.dto.ProductDetailDTO;
import com.zara.service.SimilarProductService;
import com.zara.service.impl.ProductDetailServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController("/product")
public class SimilarProductControllerImpl implements SimilarProductController {

    private SimilarProductService similarProductService;

    @Autowired
    private ProductDetailServiceImpl productDetailService;

    @GetMapping("{productId}/similar")
    public Flux<ProductDetailDTO> getSimilarProducts(@PathVariable("productId") String productId){

        return similarProductService.getSimilarProducts(productId);
    }

    @Autowired
    public void setSimilarProductService(SimilarProductService similarProductService) {
        this.similarProductService = similarProductService;
    }
}
