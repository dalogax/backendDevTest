package com.cibernos.similarproducts.service;

import com.cibernos.similarproducts.dto.ProductInfoDto;

import java.util.List;

public interface SimilarService {

    List<ProductInfoDto> getSimilarProducts(String productId);
}
