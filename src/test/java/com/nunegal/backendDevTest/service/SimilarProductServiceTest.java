// package com.nunegal.backendDevTest.service;

// import com.nunegal.backendDevTest.client.ProductClient;
// import com.nunegal.backendDevTest.model.Product;
// import org.junit.jupiter.api.Test;
// import org.mockito.Mockito;

// import java.util.List;

// import static org.junit.jupiter.api.Assertions.assertEquals;
// import static org.mockito.Mockito.*;

// public class SimilarProductServiceTest {

// @Test
// public void testGetSimilarProducts_ReturnsProductList() {
// // Arrange
// ProductClient mockClient = mock(ProductClient.class);
// SimilarProductService service = new SimilarProductService(mockClient);

// String productId = "1";
// List<Integer> ids = List.of(2, 3);

// Product product1 = new Product("2", "Shirt", 9.99, true);
// Product product2 = new Product("3", "Shoes", 19.99, true);

// when(mockClient.getSimilarProductIds(productId)).thenReturn(ids);
// when(mockClient.getProductById("2")).thenReturn(product1);
// when(mockClient.getProductById("3")).thenReturn(product2);

// // Act
// List<Product> result = service.getSimilarProducts(productId);

// // Assert
// assertEquals(2, result.size());
// assertEquals("2", result.get(0).getId());
// assertEquals("3", result.get(1).getId());

// verify(mockClient).getSimilarProductIds(productId);
// verify(mockClient).getProductById("2");
// verify(mockClient).getProductById("3");
// }
// }
