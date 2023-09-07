package com.product.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class NotFoundException extends RuntimeException {
    private static final long serialVersionUID = 3628325430263261506L;

    private final String message;
}
