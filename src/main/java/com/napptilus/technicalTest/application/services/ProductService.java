package com.napptilus.technicalTest.application.services;

import com.napptilus.technicalTest.application.useCases.ProductUseCases;
import com.napptilus.technicalTest.domain.models.Product;
import com.napptilus.technicalTest.domain.repositories.ProductRepository;
import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class ProductService implements ProductUseCases {

    private final ProductRepository productRepository;

    @Override
    public List<Product> findRelatedProducts(Integer id) {
        List<Integer> relatedIds = this.productRepository.findRelatedIds(id);

        return this.productRepository.findProductsById(relatedIds);
    }
}
