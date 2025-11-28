package dev.molaya.tests.adapters.in.rest.advice.dto;

import lombok.Builder;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@RequiredArgsConstructor
public enum ErrorType {
    INVALID_PARAMETER(HttpStatus.BAD_REQUEST, "MOLAR-400", "Invalid parameter provided"),
    UNKNOWN(HttpStatus.INTERNAL_SERVER_ERROR, "MOLAR-500", "Unknown error occurred"),
    EXTERNAL_SERVICE(HttpStatus.CONFLICT, "MOLAR-409", "Integration with external service failed");
    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    public ResponseEntity<@NonNull ApiError> getErrorResponseEntity(String customMessage) {
        return ResponseEntity.status(httpStatus.value())
                .body(ApiError.builder()
                        .code(code)
                        .message(message)
                        .detail(customMessage)
                        .build());
    }

    @Builder
    public record ApiError(String code, String message, String detail) {}
}
