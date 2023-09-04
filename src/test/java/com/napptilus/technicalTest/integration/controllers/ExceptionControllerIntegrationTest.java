package com.napptilus.technicalTest.integration.controllers;

import com.napptilus.technicalTest.application.services.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
class ExceptionControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @Test
    void shouldThrowNotFound() throws Exception {
        WebClientResponseException exception = new WebClientResponseException(404, "error message", null, null, null);

        when(productService.findRelatedProducts(2)).thenThrow(exception);

        mockMvc.perform(get("/product/2/similar"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("404 NOT_FOUND"))
                .andExpect(jsonPath("$.errorMessage").value("404 error message"))
                .andExpect(jsonPath("$.path").value("/product/2/similar"))
        ;

        verify(productService, description("Exception Controller - Integration test - Wrong service call"))
                .findRelatedProducts(2);

    }


}
