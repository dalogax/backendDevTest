package com.cibernos.prueba.application.services;
import com.cibernos.prueba.application.repository.ProductRepository;
import com.cibernos.prueba.domain.Product;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static org.mockito.ArgumentMatchers.anyInt;

@RunWith(MockitoJUnitRunner.class)
public class ProductServiceTest {
    @Mock
    ProductRepository productRepository;

    @Test
    public void testProductService() {
        ProductService productService = new ProductService(productRepository);
        Product product = new Product();
        product.setId(1);
        product.setAvailability(true);
        product.setName("Shirt");
        product.setPrice(9.99);
        Mockito.when(productRepository.getProductsById(1)).thenReturn(product);
        Product productRetrieved = productService.getProductById(1);
        assertEquals(productRetrieved.getId(), product.getId());
    }

    @Test
    public void testProductServiceAnotherValue() {
        ProductService productService = new ProductService(productRepository);
        Product product = new Product();
        product.setId(2);
        Mockito.when(productRepository.getProductsById(2)).thenReturn(product);
        Product productRetrieved = productService.getProductById(2);
        assertNotNull(productRetrieved);
    }

    @Test
    public void testProductServiceGetProducts() {
        ProductService productService = new ProductService(productRepository);
        List myList = new ArrayList();
        myList.add(5);
        myList.add(2);
        myList.add(1);
        Mockito.when(productRepository.getProducts(anyInt())).thenReturn(myList);
        List<Integer> productsRetrieved = productService.getProducts(2);
        assertEquals(myList,productsRetrieved);
    }


    @Test
    public void testProductServiceGetProductsAnotherVal() {
        ProductService productService = new ProductService(productRepository);
        List myList = new ArrayList();
        myList.add(1);
        myList.add(2);
        myList.add(6);
        Mockito.when(productRepository.getProducts(anyInt())).thenReturn(myList);
        List<Integer> productsRetrieved = productService.getProducts(5);
        assertNotNull(productsRetrieved);
    }
}