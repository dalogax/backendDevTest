package com.nicolas_sancho.similar_products.dto;

import java.time.ZonedDateTime;

public record ApiErrorDTO(
        ZonedDateTime timestamp,
        int status,
        String error,
        String message,
        String path
) {}
