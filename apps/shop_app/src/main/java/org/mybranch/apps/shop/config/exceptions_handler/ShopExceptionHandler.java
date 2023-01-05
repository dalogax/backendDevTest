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

import java.util.Collections;

@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ShopExceptionHandler {

    @ExceptionHandler(value = ProductNotFoundError.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    @ResponseBody
    public ResponseEntity<?> notFoundHandler(ProductNotFoundError e) {

        return new ResponseEntity<>(
                Collections.singletonMap("message", "Product not found"),
                HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler(value = ProductBadRequestError.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ResponseEntity<?> badRequestErrorHandler(ProductBadRequestError e) {

        return new ResponseEntity<>(
                Collections.singletonMap("message", "Product bad request error"),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(value = ProductInternalError.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public ResponseEntity<?> internalErrorHandler(ProductInternalError e) {

        return new ResponseEntity<>(
                Collections.singletonMap("message", "Product internal error"),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

}
