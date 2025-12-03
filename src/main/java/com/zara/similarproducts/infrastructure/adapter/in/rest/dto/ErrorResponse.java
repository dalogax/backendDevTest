package com.zara.similarproducts.infrastructure.adapter.in.rest.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse(
        String error,
        String message,
        String path,
        Instant timestamp
) {
    
    public static ErrorResponse of(String error, String message, String path) {
        return new ErrorResponse(error, message, path, Instant.now());
    }
}