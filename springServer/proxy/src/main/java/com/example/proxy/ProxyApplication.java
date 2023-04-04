package com.example.proxy;

import com.example.proxy.controller.ProductController;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ProxyApplication {
    private static final Logger logg = LogManager.getLogger(ProductController.class);

    public static void main(String[] args) {
        SpringApplication.run(ProxyApplication.class, args);
        logg.info("Server up");
    }
}
