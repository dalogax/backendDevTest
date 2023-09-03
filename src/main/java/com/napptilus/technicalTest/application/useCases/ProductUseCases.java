package com.napptilus.technicalTest.application.useCases;

import com.napptilus.technicalTest.domain.models.Product;

import java.util.List;

public interface ProductUseCases {

    List<Product> findRelatedProducts(Integer id);
}
