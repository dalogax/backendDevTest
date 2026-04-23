package org.challenge.products.infrastructure.controller;

import java.time.LocalDateTime;

public record ErrorResponse (String message, LocalDateTime timestamp) {

}
