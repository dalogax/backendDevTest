package com.similar.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.similar.application.exception.SimulatedClientExceptionHandler;
import com.similar.domain.SimilarProductService;
import com.similar.domain.model.Product;
import feign.FeignException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;


@ExtendWith(MockitoExtension.class)
class SimilarProductControllerTest {

    private MockMvc mvc;
    private JacksonTester<List<Product>> jsonSimilarProducts;

    @Mock
    private SimilarProductService similarProductService;

    @InjectMocks
    private SimilarProductController sut;

    @BeforeEach
    public void setup(){
        mvc = MockMvcBuilders.standaloneSetup(sut).setControllerAdvice(new SimulatedClientExceptionHandler()).build();
        JacksonTester.initFields(this, new ObjectMapper());
    }

    @Test
    public void returnsSimilarProductsById() throws Exception {
        // given
        given(similarProductService.getSimilarProductBy(1L)).willReturn(Collections.emptyList());

        // when
        MockHttpServletResponse response = mvc.perform(
                        get("/product/1/similar")
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        verify(similarProductService).getSimilarProductBy(1L);
    }

    @Test
    public void should_return_similar_products() throws Exception {
        // given
        given(similarProductService.getSimilarProductBy(1L)).willReturn(getMockSimilarProducts());

        // when
        MockHttpServletResponse response = mvc.perform(
                        get("/product/1/similar")
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        verify(similarProductService).getSimilarProductBy(1L);
        assertThat(response.getContentAsString()).isEqualTo(
                jsonSimilarProducts.write(getMockSimilarProducts()).getJson()
        );

    }

    private List<Product> getMockSimilarProducts() {
        return List.of(
                new Product( "2","Dress", BigDecimal.valueOf(19.99), true),
                new Product( "4","Boots", BigDecimal.valueOf(39.99), true)
        );
    }

    @Test
    public void should_return_404_when_not_found_products() throws Exception {
        // given
        given(similarProductService.getSimilarProductBy(1L)).willThrow(FeignException.NotFound.class);

        // when
        MockHttpServletResponse response = mvc.perform(
                        get("/product/1/similar")
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }


}