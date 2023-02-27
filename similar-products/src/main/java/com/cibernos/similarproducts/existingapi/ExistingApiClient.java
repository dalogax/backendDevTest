package com.cibernos.similarproducts.existingapi;

import com.cibernos.similarproducts.dto.ProductInfoDto;

import java.util.List;

public interface ExistingApiClient {
    List<String> getProductSimilarIds(String productId);
    ProductInfoDto getProductInfo(String productId);
}
