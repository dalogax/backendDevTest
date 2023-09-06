package com.dev.test.controller;

import com.dev.test.domain.exceptions.InternalErrorException;
import com.dev.test.domain.exceptions.NotFoundException;
import com.dev.test.domain.usecase.ProductsUseCase;
import com.dev.test.dto.SimilarProductsDTO;
import com.dev.test.mapper.SimilarProductsMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ProductsController {
    private final ProductsUseCase productsUseCase;
    private final SimilarProductsMapper similarProductsMapper;
    
    @GetMapping(value="/product/{id}/similar")
    public ResponseEntity<SimilarProductsDTO> getProductsById(final @PathVariable("id") String id) {
        final SimilarProductsDTO result = similarProductsMapper.asSimilarProductsDTO(productsUseCase.provideSimilarProducts(id));
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }
    
    @ExceptionHandler(InternalErrorException.class)
    public ResponseEntity<String> handleInternalErrorException(
        final Exception exception) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(exception.getMessage());
    }
    
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<String> handleNotFoundException(
        final Exception exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(exception.getMessage());
    }
}
