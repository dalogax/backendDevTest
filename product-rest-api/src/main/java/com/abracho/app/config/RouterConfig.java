package com.abracho.app.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.abracho.app.handler.ProductHandlerSelect;

@Configuration
public class RouterConfig {

    @Autowired
    private ProductHandlerSelect productHandler;

    @Bean
    public RouterFunction<ServerResponse> routesHandler() {

        return RouterFunctions.route()
                .path("product", this::routes)
                .build();
    }

    private RouterFunction<ServerResponse> routes() {
        return RouterFunctions.route()
                .GET("{productId}", productHandler::getProductById)
                .GET("{productId}/similarids", productHandler::getSimilarids)
                .build();
    }
}
