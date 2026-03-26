package com.danivelasco.products.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * Configuration class for application caching.
 *
 * <p>We use Caffeine as an in-memory cache due to:
 * <ul>
 *     <li>High performance (near optimal hit latency)</li>
 *     <li>Low overhead compared to distributed caches</li>
 *     <li>Built-in eviction policies (size + time based)</li>
 * </ul>
 *
 * <p>This cache is especially useful for reducing repeated calls to the external
 * products API when the same productId is requested multiple times in a short period.
 *
 * <p>Trade-off: data may become slightly stale (max 3 minutes), but this is acceptable
 * given the performance gain and the nature of the use case.
 */
@Configuration
@EnableCaching
public class CacheConfig {

    /**
     * Defines the Caffeine cache configuration.
     *
     * @return configured Caffeine instance
     */
    @Bean
    public Caffeine<Object, Object> caffeineConfig() {
        return Caffeine.newBuilder()
                .expireAfterWrite(3, TimeUnit.MINUTES)
                .maximumSize(2_000);
    }

    /**
     * Creates the cache manager with a specific cache for similar products.
     *
     * @param caffeine caffeine configuration
     * @return CacheManager instance
     */
    @Bean
    public CacheManager cacheManager(Caffeine<Object, Object> caffeine) {
        CaffeineCacheManager caffeineCacheManager = new CaffeineCacheManager("similar_products_full");
        caffeineCacheManager.setCaffeine(caffeine);
        return caffeineCacheManager;
    }
}
