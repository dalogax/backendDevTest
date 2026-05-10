package com.sngular.similarproducts.infrastructure;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * Configuration class for enabling Resilience4j aspects.
 * This enables AOP auto-proxy mode required by Resilience4j for annotations
 * like @CircuitBreaker, @Retry, @RateLimiter, etc.
 */
@Configuration
@EnableAspectJAutoProxy
public class ResilienceConfig {
}
