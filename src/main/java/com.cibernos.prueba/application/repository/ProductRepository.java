package com.cibernos.prueba.application.repository;

import com.cibernos.prueba.domain.Product;

public interface ProductRepository {
    Iterable<Product> getProducts(Integer Id);
    Product getProductsById(Integer Id);
    Product saveProduct(Product product);
    void deleteProductById(Integer Id);
}
