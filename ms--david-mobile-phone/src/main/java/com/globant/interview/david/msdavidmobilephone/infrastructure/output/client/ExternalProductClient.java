package com.globant.interview.david.msdavidmobilephone.infrastructure.output.client;

import com.globant.interview.david.msdavidmobilephone.infrastructure.output.client.dto.ProductDetailResponse;
import com.globant.interview.david.msdavidmobilephone.infrastructure.output.client.dto.SimilarIdsResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "externalProductApi", url = "${external.api.base-url:http://localhost:3001}")
public interface ExternalProductClient {

    @GetMapping("/product/{productId}/similarids")
    SimilarIdsResponse getSimilarIds(@PathVariable("productId") String productId);

    @GetMapping("/product/{productId}")
    ProductDetailResponse getProductDetail(@PathVariable("productId") String productId);
}
