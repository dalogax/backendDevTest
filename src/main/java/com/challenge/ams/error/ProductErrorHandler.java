package com.challenge.ams.error;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import feign.FeignException;
import feign.FeignException.FeignClientException;

@RestControllerAdvice
public class ProductErrorHandler {

	@ExceptionHandler({FeignClientException.class})
	public ResponseEntity<Object> handleNotFound(FeignClientException e){
		return new ResponseEntity<Object>("El ID no es valido", HttpStatus.NOT_ACCEPTABLE);
	}
	
	@ExceptionHandler({FeignException.class})
	public ResponseEntity<Object> handleError(FeignException e){
		return new ResponseEntity<Object>("Se lanzo una excepcion: error.", HttpStatus.NOT_ACCEPTABLE);
	}
}
