package com.backendtest.similar.api;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.backendtest.similar.application.port.in.SimilarProductsUseCase;
import com.backendtest.similar.dto.ProductDetail;
import com.backendtest.similar.exception.ProductNotFoundException;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(SimilarController.class)
@Import(GlobalExceptionHandler.class)
class SimilarControllerTest {

    @Autowired
    MockMvc mvc;

    @MockBean
    SimilarProductsUseCase service;

    @Test
    void returnsListInOrder() throws Exception {
        when(service.getSimilarProducts("1"))
                .thenReturn(List.of(
                        new ProductDetail("2", "Dress", new BigDecimal("19.99"), true),
                        new ProductDetail("3", "Blazer", new BigDecimal("29.99"), false)));

        mvc.perform(get("/product/1/similar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("2"))
                .andExpect(jsonPath("$[1].id").value("3"))
                .andExpect(jsonPath("$[0].price").value(19.99));
    }

    @Test
    void emptyListIs200() throws Exception {
        when(service.getSimilarProducts("3")).thenReturn(List.of());

        mvc.perform(get("/product/3/similar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void unknownProductIs404() throws Exception {
        when(service.getSimilarProducts("999")).thenThrow(new ProductNotFoundException("999"));

        mvc.perform(get("/product/999/similar")).andExpect(status().isNotFound());
    }
}
