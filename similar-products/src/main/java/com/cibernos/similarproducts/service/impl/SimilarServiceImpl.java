package com.cibernos.similarproducts.service.impl;

import com.cibernos.similarproducts.dto.ProductInfoDto;
import com.cibernos.similarproducts.exception.SimilarProductException;
import com.cibernos.similarproducts.existingapi.ExistingApiClient;
import com.cibernos.similarproducts.existingapi.impl.ExistingApiClientImpl;
import com.cibernos.similarproducts.service.SimilarService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class SimilarServiceImpl implements SimilarService {

    private final Logger log = LoggerFactory.getLogger(ExistingApiClientImpl.class);

    @Autowired
    private ExistingApiClient existingApiClient;

    public List<ProductInfoDto> getSimilarProducts(String productId) {
        log.info("getSimilarProductsService(): {}", productId);
        List<String> productSimilarIds = existingApiClient.getProductSimilarIds(productId);
        log.info("List of product similar ids(): {}", productSimilarIds);
        if(productSimilarIds == null){
            throw new SimilarProductException("Product Not found", HttpStatus.NOT_FOUND);
        }
        return productSimilarIds.stream()
                .map(existingApiClient::getProductInfo)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}
