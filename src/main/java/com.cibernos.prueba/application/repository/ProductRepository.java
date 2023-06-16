package com.cibernos.prueba.application.repository;

import com.cibernos.prueba.domain.Product;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

public interface ProductRepository {
    List<Integer> getProducts(Integer Id);
    Product getProductsById(Integer Id);
}
