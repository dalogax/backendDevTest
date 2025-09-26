package com.example.similarproducts.exception.handler;

import com.example.similarproducts.exception.NotFoundException;
import io.netty.handler.timeout.ReadTimeoutException;
import java.util.concurrent.TimeoutException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@RestControllerAdvice
public class GlobalExceptionHandler {

  private static final String TITLE_NOT_FOUND = "Product not found";
  private static final String TITLE_TIMEOUT = "Request timeout";
  private static final String TITLE_UPSTREAM = "Upstream error";
  private static final String TITLE_INTERNAL = "Internal server error";
  private static final String DETAIL_TIMEOUT = "The request to the product service took too long";

  @ExceptionHandler(NotFoundException.class)
  public ProblemDetail handleNotFound(NotFoundException ex) {
    return pd(HttpStatus.NOT_FOUND, TITLE_NOT_FOUND, ex.getMessage());
  }

  @ExceptionHandler({TimeoutException.class, ReadTimeoutException.class})
  public ProblemDetail handleTimeout(Exception ex) {
    return pd(HttpStatus.GATEWAY_TIMEOUT, TITLE_TIMEOUT, DETAIL_TIMEOUT);
  }

  @ExceptionHandler(WebClientRequestException.class)
  public ProblemDetail handleWebClientRequest(WebClientRequestException ex) {
    Throwable cause = ex.getCause();
    if (cause instanceof ReadTimeoutException || cause instanceof TimeoutException) {
      return pd(HttpStatus.GATEWAY_TIMEOUT, TITLE_TIMEOUT, DETAIL_TIMEOUT);
    }
    return pd(HttpStatus.BAD_GATEWAY, TITLE_UPSTREAM, ex.getMessage());
  }

  @ExceptionHandler(WebClientResponseException.class)
  public ProblemDetail handleUpstream(WebClientResponseException ex) {
    if (ex.getStatusCode().is5xxServerError()) {
      return pd(HttpStatus.BAD_GATEWAY, TITLE_UPSTREAM,
          "Error from product service: " + ex.getStatusCode().value());
    }
    return pd(HttpStatus.INTERNAL_SERVER_ERROR, TITLE_INTERNAL, ex.getMessage());
  }

  @ExceptionHandler(Exception.class)
  public ProblemDetail handleGeneric(Exception ex) {
    return pd(HttpStatus.INTERNAL_SERVER_ERROR, TITLE_INTERNAL, ex.getMessage());
  }

  private static ProblemDetail pd(HttpStatus status, String title, String detail) {
    ProblemDetail p = ProblemDetail.forStatus(status);
    p.setTitle(title);
    p.setDetail(detail);
    return p;
  }
}
