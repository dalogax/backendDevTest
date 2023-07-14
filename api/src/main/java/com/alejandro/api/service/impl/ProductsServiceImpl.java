package com.alejandro.api.service.impl;

import com.alejandro.api.dto.ProductDetailDTO;
import com.alejandro.api.mapper.ProductDetailMapper;
import com.alejandro.api.model.ProductDetailEntity;
import com.alejandro.api.repository.IProductsRepository;
import com.alejandro.api.service.IProductsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductsServiceImpl implements IProductsService {

    private final IProductsRepository productsRepository;

    private final ProductDetailMapper productDetailMapper;

    @Override
    public Optional<List<String>> getSimilarProductIds(String productId) {
        return productsRepository
                .getSimilarProductIds(productId);
    }

    @Override
    public Optional<ProductDetailDTO> getProductDetail(String productId) {
        Optional<ProductDetailEntity> productDetailEntity = productsRepository
                .getProductDetail(productId);

        if (productDetailEntity.isPresent()){
            return Optional.of(productDetailMapper
                    .productEntityToDto(productDetailEntity.get()));
        } else{
            return Optional.empty();
        }
    }
}
