package com.alejandro.api.service;

import com.alejandro.api.dto.ProductDetailDTO;

import java.util.List;

public interface ISimilarProductsService {

    List<ProductDetailDTO> getSimilarProducts(String productId);
}
