package com.backendtest.similar.config;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TaskExecutorConfig {

    @Bean(destroyMethod = "close")
    public ExecutorService similarProductsExecutor() {
        return Executors.newVirtualThreadPerTaskExecutor();
    }
}
