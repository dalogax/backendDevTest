package test.backend.products.api.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import java.net.SocketTimeoutException;
import java.util.Date;

@Slf4j
@RestControllerAdvice
public class ServerExceptionHandler {

    @ExceptionHandler(SocketTimeoutException.class)
    @ResponseStatus(HttpStatus.REQUEST_TIMEOUT)
    public ErrorMessage handleSocketTimeoutException(Throwable e) {
        ErrorMessage error = new ErrorMessage(HttpStatus.REQUEST_TIMEOUT.value(), new Date(), e.getMessage(), "Request stopped due to timeout");
        log.error("Socket timeout: " + error);
        return error;
    }

    @ExceptionHandler(HttpServerErrorException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorMessage handleHttpServerErrorException(Throwable e) {
        ErrorMessage error = new ErrorMessage(HttpStatus.INTERNAL_SERVER_ERROR.value(), new Date(), e.getMessage(), "Internal Server Error");
        log.error("Internal Server Error: " + error);
        return error;
    }

    @ExceptionHandler(HttpClientErrorException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorMessage handleHttpClientErrorException(Throwable e) {
        ErrorMessage error = new ErrorMessage(HttpStatus.NOT_FOUND.value(), new Date(), e.getMessage(), "Not Found Error");
        log.error("Client Error: " + error);
        return error;
    }




}
