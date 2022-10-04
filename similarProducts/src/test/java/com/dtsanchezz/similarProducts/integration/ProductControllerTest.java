package com.dtsanchezz.similarProducts.integration;

import com.dtsanchezz.similarProducts.consumer.api.ProductConsumer;
import com.dtsanchezz.similarProducts.exception.custom.NotFoundException;
import com.dtsanchezz.similarProducts.model.ProductDetail;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

;

@SpringBootTest
@WebAppConfiguration
@ExtendWith(MockitoExtension.class)
public class ProductControllerTest {

    public static final String URI = "http://localhost:";

    @Autowired
    private WebApplicationContext context;

    private MockMvc mvc;

    @Value("${server.port}")
    private String serverPort;

    @MockBean
    private ProductConsumer productConsumer;

    @BeforeEach
    public void setUp() {
        this.mvc = MockMvcBuilders.webAppContextSetup(this.context).build();
    }

    public static String PRODUCT_ID_TO_TEST = "1";

    final ProductDetail productOne = new ProductDetail("1", "Test ONE", 1.99, true);
    final ProductDetail productTwo = new ProductDetail("2", "Test TWO", 2.99, true);
    final ProductDetail productThree = new ProductDetail("3", "Test THREE", 3.99, true);
    final ProductDetail productFour = new ProductDetail("4", "Test FOUR", 4.99, true);

    final List<ProductDetail> productControllerList = List.of(this.productOne, this.productTwo, this.productThree, this.productFour);

    @Test
    public void getSimilarProductsSuccess_200() throws Exception {
        when(this.productConsumer.getSimilarIds(PRODUCT_ID_TO_TEST))
                .thenReturn(List.of("2", "3", "4"));

        when(this.productConsumer.getProductDetail("2"))
                .thenReturn(this.productOne);

        when(this.productConsumer.getProductDetail("3"))
                .thenReturn(this.productTwo);

        when(this.productConsumer.getProductDetail("4"))
                .thenReturn(this.productThree);

        final MvcResult mvcResult = this.mvc.perform(get(URI + this.serverPort + "/product/" + PRODUCT_ID_TO_TEST + "/similar"))
                .andExpect(status().isOk())
                .andReturn();

        Assertions.assertNotNull(mvcResult.getResponse().getContentAsString());
        Assertions.assertFalse(mvcResult.getResponse().getContentAsString().isEmpty());
    }

    @Test
    public void getSimilarProductsSuccess_404() throws Exception {
        when(this.productConsumer.getSimilarIds(PRODUCT_ID_TO_TEST))
                .thenReturn(List.of("2", "3", "4"));

        when(this.productConsumer.getProductDetail("2"))
                .thenThrow(NotFoundException.class);

        this.mvc.perform(get(URI + this.serverPort + "/product/" + PRODUCT_ID_TO_TEST + "/similar"))
                .andExpect(status().isNotFound());
    }
}