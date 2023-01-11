package com.inditex.myapp.infrastructure.config;

import com.inditex.myapp.domain.service.ProductService;
import com.inditex.myapp.domain.service.impl.ProductServiceImpl;
import com.inditex.myapp.infrastructure.ApiClient;
import com.inditex.myapp.infrastructure.config.endpoint.EndpointsConfig;
import com.inditex.myapp.infrastructure.rest.DefaultApi;
import com.inditex.myapp.infrastructure.service.impl.ExistingApiServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class BeanConfig {

    @Bean
    public ProductService productService(ExistingApiServiceImpl existingApiService) {
        return new ProductServiceImpl(existingApiService);
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public DefaultApi defaultApi(EndpointsConfig endpointsConfig) {
        ApiClient apiClient = new ApiClient();
        apiClient.setBasePath(endpointsConfig.getExistingApi().getBasePath());
        return new DefaultApi(apiClient);
    }
}
