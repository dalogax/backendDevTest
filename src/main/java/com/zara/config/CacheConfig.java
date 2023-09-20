package com.zara.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

@Slf4j
@Configuration
public class CacheConfig {

    @CacheEvict(value = "product-details-cache", allEntries = true)
    @Scheduled(fixedRateString = "${cache.spring.product-detail-cache.ttl}")
    public void evictProductDetailsCache() {
        log.info("evicting Product details cache");
    }
}
