package test.backend.products.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import test.backend.products.repository.ProductRepository;
import test.backend.products.useCase.RetrieveSortedSimilarProductsUseCase;

@Configuration
public class UseCaseConfiguration {

    @Bean
    public RetrieveSortedSimilarProductsUseCase retrieveSortedSimilarProductsUseCase(
            ProductRepository productRepository
    ) {
        return new RetrieveSortedSimilarProductsUseCase(productRepository);
    }

}
