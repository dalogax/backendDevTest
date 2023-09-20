package com.zara.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;

@Configuration
public class WebClientConfig {

    private int timeout;

    @Bean
    public WebClient webClient(WebClient.Builder builder) {
        HttpClient client = HttpClient.create()
                .responseTimeout(Duration.ofSeconds(timeout));

        return builder
                .clientConnector(new ReactorClientHttpConnector(client))
                .build();
    }

    @Value("${rest-client.timeout}")
    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }
}
