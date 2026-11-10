package com.inditex.similarproducts.catalog;

import java.time.Duration;

import io.netty.channel.ChannelOption;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;

@Configuration(proxyBeanMethods = false)
class CatalogConfiguration {
    @Bean(destroyMethod = "dispose")
    ConnectionProvider catalogConnectionProvider(CatalogProperties properties) {
        return ConnectionProvider.builder("catalog")
                // The supplied Node catalog closes keep-alive sockets after five idle seconds.
                .maxIdleTime(properties.maxIdleTime())
                .evictInBackground(Duration.ofSeconds(1))
                .maxConnections(properties.maxConnections())
                .pendingAcquireMaxCount(properties.pendingAcquireMaxCount())
                .pendingAcquireTimeout(properties.connectTimeout())
                .build();
    }

    @Bean
    WebClient catalogWebClient(WebClient.Builder builder, CatalogProperties properties,
                               ConnectionProvider catalogConnectionProvider) {
        HttpClient httpClient = HttpClient.create(catalogConnectionProvider)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, (int) properties.connectTimeout().toMillis())
                .disableRetry(true);
        return builder.baseUrl(properties.baseUrl().toString())
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }
}
