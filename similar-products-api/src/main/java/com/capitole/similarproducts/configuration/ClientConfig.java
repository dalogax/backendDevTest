package com.capitole.similarproducts.configuration;

import com.capitole.similarproducts.generated.api.ProductApi;
import com.capitole.similarproducts.generated.invoker.ApiClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
public class ClientConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClientConfig.class);

    @Bean
    public ProductApi productApi(@Value("${external.product-service.base-url}") String baseUrl,
                               RestClient.Builder restClientBuilder) {
        LOGGER.info("Initializing ProductApi (generated) with base URL: {}", baseUrl);

        var requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(3000);
        requestFactory.setReadTimeout(3000);

        ApiClient apiClient = new ApiClient(restClientBuilder
                .requestFactory(requestFactory)
                .build());
        apiClient.setBasePath(baseUrl);
        return new ProductApi(apiClient);
    }
}
