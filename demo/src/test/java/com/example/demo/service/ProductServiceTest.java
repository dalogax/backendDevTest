package com.example.demo.service;

import com.example.demo.client.SimilarProductsClient;
import com.example.demo.model.Product;
import feign.FeignException;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
public class ProductServiceTest {

    @Mock
    private SimilarProductsClient client;
    @InjectMocks
    private ProductService underTest;

    @Test
    public void shouldGetSimilarProductsOk() {
        Product p1 = new Product("1", "Shirt", 9.99, true);
        Product p2 = new Product("2", "Dress", 19.99, true);
        Set<String> products = Set.of("1", "2");
        when(client.getSimilarProductIDs("1")).thenReturn(products);
        when(client.getProduct("1")).thenReturn(p1);
        when(client.getProduct("2")).thenReturn(p2);

        Set<Product> actual = underTest.getSimilarProducts("1");

        assertEquals(2, actual.size());
        assertTrue(actual.contains(p1));
        assertTrue(actual.contains(p2));
    }

    @Test
    public void shouldReturnNotFound_GetSimilarProductsWhenNotFoundProduct() {
        Product p1 = new Product("1", "Shirt", 9.99, true);
        Set<String> products = Set.of("1", "2");
        when(client.getSimilarProductIDs("1")).thenReturn(products);
        when(client.getProduct("1")).thenReturn(p1);
        when(client.getProduct("2")).thenThrow(FeignException.NotFound.class);

        try {
            underTest.getSimilarProducts("1");
        } catch (Exception e) {
            assertEquals(FeignException.NotFound.class, e.getClass());
        }
    }

    @Test
    public void shouldReturnException_GetSimilarProductsWhenInternalServerError() {
        Product p1 = new Product("1", "Shirt", 9.99, true);
        Set<String> products = Set.of("1", "2");
        when(client.getSimilarProductIDs("1")).thenReturn(products);
        when(client.getProduct("1")).thenReturn(p1);
        when(client.getProduct("2")).thenThrow(FeignException.InternalServerError.class);

        try {
            underTest.getSimilarProducts("1");
        } catch (Exception e) {
            assertEquals(FeignException.InternalServerError.class, e.getClass());
        }
    }
}