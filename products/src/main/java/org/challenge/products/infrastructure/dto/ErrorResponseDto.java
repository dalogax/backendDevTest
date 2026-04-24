package org.challenge.products.infrastructure.dto;

import java.time.LocalDateTime;

public record ErrorResponseDto(String message, LocalDateTime timestamp) {

}
