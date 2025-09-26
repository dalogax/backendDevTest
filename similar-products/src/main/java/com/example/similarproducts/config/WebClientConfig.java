package com.example.similarproducts.config;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import java.time.Duration;
import java.util.concurrent.TimeUnit;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

@Configuration
@EnableConfigurationProperties(ProductClientProperties.class)
public class WebClientConfig {

  @Bean
  public WebClient productsWebClient(ProductClientProperties props) {

    HttpClient httpClient = HttpClient.create()
        .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, props.getConnectTimeoutMs())
        .responseTimeout(Duration.ofMillis(props.getReadTimeoutMs()))
        .doOnConnected(conn -> conn
            .addHandlerLast(new ReadTimeoutHandler(props.getReadTimeoutMs(), TimeUnit.MILLISECONDS))
            .addHandlerLast(new WriteTimeoutHandler(props.getReadTimeoutMs(), TimeUnit.MILLISECONDS)));

    return WebClient.builder()
        .baseUrl(props.getBaseUrl())
        .clientConnector(new ReactorClientHttpConnector(httpClient))
        .build();

  }
}