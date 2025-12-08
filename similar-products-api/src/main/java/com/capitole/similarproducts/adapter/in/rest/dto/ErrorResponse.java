package com.capitole.similarproducts.adapter.in.rest.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;

/**
 * Standard error response DTO for REST API.
 */
public record ErrorResponse(
    @JsonProperty("timestamp")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    LocalDateTime timestamp,

    @JsonProperty("status")
    Integer status,

    @JsonProperty("error")
    String error,

    @JsonProperty("message")
    String message,

    @JsonProperty("path")
    String path
) {}
