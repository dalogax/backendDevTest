package dev.molaya.tests.adapters.out.client.config;

import dev.molaya.tests.adapters.out.client.gen.openapi.DefaultGenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ClientWebClientConfiguration {
    @Bean
    public DefaultGenApi defaultGenApi() {
        return new DefaultGenApi();
    }
}
