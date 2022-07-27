package com.abracho.app;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.reactive.function.client.WebClient;

import com.abracho.app.model.ProductDto;

public class GetSingleResponseTest extends BaseTest {

    @Autowired
    private WebClient client;

    @Test
    public void getTest() {

        ProductDto response = this.client.get()
                .uri("product/{productId}", 1)
                .retrieve()
                .bodyToMono(ProductDto.class)
                .block();

        System.out.println(response);
    }

}
