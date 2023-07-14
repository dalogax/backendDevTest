package com.alejandro.api.exception;

import lombok.EqualsAndHashCode;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
@EqualsAndHashCode(callSuper = true)
public class ProductNotFoundException extends RuntimeException {
}
