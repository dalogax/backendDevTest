package com.inditex.similarproducts;

import io.netty.channel.ChannelOption;
import org.springframework.boot.reactor.netty.NettyReactiveWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
class WebServerConfiguration {
    @Bean
    WebServerFactoryCustomizer<NettyReactiveWebServerFactory> connectionBacklog() {
        // The benchmark opens 200 connections at once; absorb that initial burst.
        return factory -> factory.addServerCustomizers(
                server -> server.option(ChannelOption.SO_BACKLOG, 1024));
    }
}
