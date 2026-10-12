package com.backendtest.similar.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import java.time.Duration;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CacheConfig {

    @Bean
    public CacheManager cacheManager(ProductProperties props) {
        CaffeineCacheManager manager = new CaffeineCacheManager("details");
        manager.setCaffeine(Caffeine.newBuilder()
                .maximumSize(props.cacheMaxSize())
                .expireAfterWrite(Duration.ofSeconds(props.cacheTtlSeconds())));
        return manager;
    }
}
