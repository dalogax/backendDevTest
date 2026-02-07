package com.globant.interview.david.msdavidmobilephone.infrastructure.output.client;

import com.globant.interview.david.msdavidmobilephone.infrastructure.output.client.dto.ProductDetailResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "externalProductApi", url = "${external.api.base-url:http://localhost:3001}")
public interface FeignProductClient {

    @GetMapping("/product/{productId}/similarids")
    List<String> getSimilarIds(@PathVariable("productId") String productId);

    @GetMapping("/product/{productId}")
    ProductDetailResponse getProductDetail(@PathVariable("productId") String productId);
}
