package test.backend.products.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import test.backend.products.repository.ProductRepository;
import test.backend.products.repository.RestProductRepository;

@Configuration
public class RepositoryConfiguration {

    @Bean
    public ProductRepository productRepository(
            RestTemplate restTemplate,
            @Value("${service.product.url}") String productUrl,
            @Value("${service.product.endpoint}") String productEndpoint
    ) {
        return new RestProductRepository(restTemplate, productUrl+productEndpoint);
    }

}
