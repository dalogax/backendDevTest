package com.example.proxy.service;

import com.example.proxy.client.ProductClient;
import com.example.proxy.dto.ProductDetailDTO;
import com.example.proxy.dto.SimilarProductsDTO;
import com.example.proxy.service.interfaces.IProductService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProductService implements IProductService {
    private final ProductClient productClient;
    public ProductService(ProductClient productClient) {
        this.productClient = productClient;
    }

    @Override
    public List<ProductDetailDTO> getSimilarProducts(String productId) {
        List<ProductDetailDTO> productDetailDTOList = new ArrayList<>();
        SimilarProductsDTO similarProducts = productClient.getSimilarProducts(productId);

        similarProducts.getIds().forEach( id -> {
            productDetailDTOList.add(productClient.getProductDetail(id));
        });

        return productDetailDTOList;
    }
}
