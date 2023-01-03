package com.prueba.products.service.impl;

import com.prueba.products.model.Product;
import com.prueba.products.productsClient.ProductsClient;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@SpringBootTest
public class SimilarProductsServiceImplTest {

    @InjectMocks
    private SimilarProductsServiceImpl similarProductsService;

    @Mock
    private ProductsClient productsClient;

    @Test
    public void shouldReturnNullWhenGetSimilarProductsAndProductIdIsNull(){
        List<Product> response = similarProductsService.getSimilarProducts(null);
        assertEquals(response, null);
    }

    @Test
    public void shouldReturnProductsWhenGetSimilarProductsAndProductIdI2(){
        String productId= "2";
        String[] similarProductsIds = {"3", "100", "1000"};
        Product product1 = new Product("3", "Blazer", 29.99, false);
        Product product2 = new Product("100", "Trousers", 49.99, false);
        Product product3 = new Product("1000", "Coat", 89.99, true);
        when(productsClient.getSimilarProductsIds(productId)).thenReturn(similarProductsIds);
        when(productsClient.getProductById(similarProductsIds[0])).thenReturn(product1);
        when(productsClient.getProductById(similarProductsIds[1])).thenReturn(product2);
        when(productsClient.getProductById(similarProductsIds[2])).thenReturn(product3);
        List<Product> products = new ArrayList<>();
        products.add(product1);
        products.add(product2);
        products.add(product3);
        List<Product> response = similarProductsService.getSimilarProducts(productId);
        assertEquals(response, products);
    }

    @Test
    public void shouldReturnNullWhenGetSimilarProductsAndProductIdI6(){
        String productId= "6";
        when(productsClient.getSimilarProductsIds(productId)).thenReturn(null);
        List<Product> response = similarProductsService.getSimilarProducts(productId);
        assertEquals(response, null);
    }


}
