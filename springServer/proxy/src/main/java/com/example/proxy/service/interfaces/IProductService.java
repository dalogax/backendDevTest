package com.example.proxy.service.interfaces;

import com.example.proxy.dto.ProductDetailDTO;

import java.util.List;

public interface IProductService {
    List<ProductDetailDTO> getSimilarProducts(String productId);
}
