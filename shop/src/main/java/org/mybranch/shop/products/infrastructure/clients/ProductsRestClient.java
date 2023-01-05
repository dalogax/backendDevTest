package org.mybranch.shop.products.infrastructure.clients;

import org.mybranch.shop.products.domain.ProductDetail;
import org.mybranch.shop.products.infrastructure.clients.config.FeignClientConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@FeignClient(name = "products-client", url = "${products.url}", configuration = FeignClientConfig.class)
public interface ProductsRestClient {

    @GetMapping("/product/{productId}/similarids")
    List<String> similarIds(@PathVariable String productId);

    @GetMapping("/product/{productId}")
    ProductDetail productDetail(@PathVariable String productId);

}
