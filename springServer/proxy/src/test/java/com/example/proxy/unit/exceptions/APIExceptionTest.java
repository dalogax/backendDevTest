package com.example.proxy.unit.exceptions;

import com.example.proxy.exceptions.APIException;
import com.example.proxy.exceptions.NotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.net.http.HttpConnectTimeoutException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class APIExceptionTest {

    @Test
    public void testAPIExceptionThrowInternalError() {
        APIException ex = assertThrows(APIException.class, () -> {
            throw new APIException("An error", HttpStatus.INTERNAL_SERVER_ERROR.value());
        });

        assertEquals("An error", ex.getMessage());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getStatus());
    }

    @Test
    public void testAPIExceptionThrowException() {
        APIException ex = assertThrows(APIException.class, () -> {
            throw new APIException("An error", new HttpConnectTimeoutException("time-out"));
        });

        assertEquals("An error", ex.getMessage());
        assertEquals("time-out", ex.getCause().getMessage());
    }

    @Test
    public void APIExceptionThrowNotFoundException() {
        NotFoundException ex = assertThrows(NotFoundException.class, () -> {
            throw new NotFoundException("Product Not Found");
        });

        assertEquals("Product Not Found", ex.getMessage());
        assertEquals(HttpStatus.NOT_FOUND.value(), ex.getStatus());
    }
}
