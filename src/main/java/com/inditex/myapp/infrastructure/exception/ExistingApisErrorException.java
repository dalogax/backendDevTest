package com.inditex.myapp.infrastructure.exception;

import com.inditex.myapp.domain.exception.MyAppException;
import lombok.experimental.StandardException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_GATEWAY)
@StandardException
public class ExistingApisErrorException extends MyAppException {
}
