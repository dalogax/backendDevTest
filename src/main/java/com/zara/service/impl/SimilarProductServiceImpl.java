package com.zara.service.impl;

import com.zara.client.ProductClient;
import com.zara.dto.ProductDetailDTO;
import com.zara.service.ProductDetailService;
import com.zara.service.SimilarProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Slf4j
@Service
public class SimilarProductServiceImpl implements SimilarProductService {

    private ProductClient productClient;
    private ProductDetailService productDetailService;

    public Flux<ProductDetailDTO> getSimilarProducts(String productId) {
        log.info("SimilarProductServiceImpl::getSimilarProducts() method started with request: {}", productId);
        return productClient.retrieveSimilarIds(productId)
                .flatMap(productDetailService::getProductDetail);
    }


    @Autowired
    public void setProductClient(ProductClient productClient) {
        this.productClient = productClient;
    }

    @Autowired
    public void setProductDetailService(ProductDetailService productDetailService) {
        this.productDetailService = productDetailService;
    }
}
