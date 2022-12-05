package com.similar.application;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/product")
public class SimilarProductController {


    @GetMapping(path = "/{productId}/similar")
    ResponseEntity<?> getSimilarProductBy(@PathVariable final Long productId) {
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
