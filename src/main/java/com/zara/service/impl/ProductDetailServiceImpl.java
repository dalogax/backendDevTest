package com.zara.service.impl;

import com.zara.client.ProductClient;
import com.zara.dto.ProductDetailDTO;
import com.zara.service.ProductDetailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import java.time.Duration;

@Slf4j
@Service
public class ProductDetailServiceImpl implements ProductDetailService {
    private ProductClient productClient;

    private int monoCacheTTL;
    @Cacheable("product-details-cache")
    public Mono<ProductDetailDTO> getProductDetail(String id){
        log.info("ProductDetailServiceImpl::getProductDetail: Method starting with id {}", id);
        return productClient.getProduct(id)
                .doOnError(ex-> log.error("Error retrieving product detail: {}", ex.getMessage()) )
                .onErrorResume(res-> Mono.empty() )
                .cache(Duration.ofMillis(monoCacheTTL+5000)); //its recommended that the Mono ttl be larger than the Cacheable one
    }

    @Autowired
    public void setProductClient(ProductClient productClient) {
        this.productClient = productClient;
    }

    @Value("${cache.spring.product-detail-cache.ttl}")
    public void setMonoCacheTTL(int monoCacheTTL) {
        this.monoCacheTTL = monoCacheTTL;
    }
}
