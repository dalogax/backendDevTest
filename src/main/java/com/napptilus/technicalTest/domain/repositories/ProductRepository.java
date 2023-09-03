package com.napptilus.technicalTest.domain.repositories;

import com.napptilus.technicalTest.domain.models.Product;

import java.util.List;

public interface ProductRepository {


    List<Product> findProductsById(List<Integer> ids);

    List<Integer> findRelatedIds(Integer id);
}
