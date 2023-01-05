package org.mybranch.apps.shop.config.exceptions_handler;

import org.mybranch.shop.products.domain.ProductBadRequestError;
import org.mybranch.shop.products.domain.ProductInternalError;
import org.mybranch.shop.products.domain.ProductNotFoundError;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.LinkedHashMap;
import java.util.Map;

@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ShopExceptionHandler {

    @ExceptionHandler(value = ProductNotFoundError.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    @ResponseBody
    public ResponseEntity<?> notFoundHandler(ProductNotFoundError e) {

        Map<String, String> responseBody = new LinkedHashMap<>();
        responseBody.put("message", "Product not found");
        responseBody.put("developerMessage", e.getMessage());

        return new ResponseEntity<>(
                responseBody,
                HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler(value = ProductBadRequestError.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ResponseEntity<?> badRequestErrorHandler(ProductBadRequestError e) {

        Map<String, String> responseBody = new LinkedHashMap<>();
        responseBody.put("message", "Product bad request");
        responseBody.put("developerMessage", e.getMessage());

        return new ResponseEntity<>(
                responseBody,
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(value = ProductInternalError.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public ResponseEntity<?> internalErrorHandler(ProductInternalError e) {

        Map<String, String> responseBody = new LinkedHashMap<>();
        responseBody.put("message", "Product internal error");
        responseBody.put("developerMessage", e.getMessage());

        return new ResponseEntity<>(
                responseBody,
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

}
