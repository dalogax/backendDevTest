package com.similar.infrastructure.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

@FeignClient(name = "similar", url = "localhost:3001")
public interface SimulatedClient {
    @GetMapping(path = "/product/{productId}/similarids")
    List<Integer> getSimilar(@RequestHeader HttpHeaders headers, @PathVariable Long productId);
}
