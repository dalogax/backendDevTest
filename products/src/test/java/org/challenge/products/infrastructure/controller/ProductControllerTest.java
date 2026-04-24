package org.challenge.products.infrastructure.controller;

import org.challenge.products.application.port.in.GetSimilarProductsUseCase;
import org.challenge.products.domain.exception.ProductNotFoundException;
import org.challenge.products.domain.model.Product;
import org.challenge.products.infrastructure.dto.ProductResponseDto;
import org.challenge.products.infrastructure.mapper.ProductMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GetSimilarProductsUseCase getSimilarProductsUseCase;

    @MockitoBean
    private ProductMapper productMapper;


    @Test
    @DisplayName("Should return 200 with similar products")
    void shouldReturnSimilarProducts() throws Exception {
        String productId1 = "1";
        String productId2 = "2";
        String productName2 = "Product 2";
        Double productPrice2 = 19.99;
        Boolean productAvailability2 = true;

        Product product = new Product(productId2, productName2, productPrice2, productAvailability2);
        ProductResponseDto dto = new ProductResponseDto(productId2, productName2, productPrice2, productAvailability2);

        when(getSimilarProductsUseCase.getSimilarProducts(productId1)).thenReturn(List.of(product));
        when(productMapper.toResponseDTO(product)).thenReturn(dto);

        mockMvc.perform(get("/product/{productId}/similar", productId1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].product_id").value(productId2))
                .andExpect(jsonPath("$[0].name").value(productName2))
                .andExpect(jsonPath("$[0].price").value(productPrice2))
                .andExpect(jsonPath("$[0].availability").value(productAvailability2));
    }


    @Test
    @DisplayName("Should return 200 with empty list when no similar products")
    void shouldReturnEmptyList() throws Exception {

        String productId = "1";

        when(getSimilarProductsUseCase.getSimilarProducts(productId))
                .thenReturn(List.of());

        mockMvc.perform(get("/product/{productId}/similar", productId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    @DisplayName("Should return 404 when product not found")
    void shouldReturn404WhenProductNotFound() throws Exception {

        String productId = "4";
        String mockedResponseBody = "not found";
        String expectedErrorMessage = String.format("Product with id %s not found. Response body: %s", productId, mockedResponseBody);

        when(getSimilarProductsUseCase.getSimilarProducts(productId))
                .thenThrow(new ProductNotFoundException(productId, mockedResponseBody));

        mockMvc.perform(get("/product/{productId}/similar", productId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(expectedErrorMessage))
                .andExpect(jsonPath("$.timestamp").exists());
    }

}