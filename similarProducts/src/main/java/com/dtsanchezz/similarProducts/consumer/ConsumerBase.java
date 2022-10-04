package com.dtsanchezz.similarProducts.consumer;


import com.dtsanchezz.similarProducts.exception.custom.ConsumerException;
import com.dtsanchezz.similarProducts.exception.custom.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;

public abstract class ConsumerBase {

    @Autowired
    protected RestTemplate restTemplate;

    protected Object consume(final String uri,
                             final Class clazz,
                             final Object... uriVariables) {

        ResponseEntity response = null;

        try {
            response = this.restTemplate.getForEntity(
                    uri,
                    clazz,
                    uriVariables
            );
        } catch (final HttpClientErrorException.NotFound notFound) {
            throw new NotFoundException();
        } catch (final Exception e) {
            throw new ConsumerException(e.getMessage());
        }

        if (!response.getStatusCode().equals(HttpStatus.OK)
                || Objects.isNull(response.getBody())) {
            throw new ConsumerException();
        }

        return response.getBody();
    }
}
