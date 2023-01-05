package org.mybranch.shop.products.infrastructure.clients.config;

import feign.Response;
import feign.codec.ErrorDecoder;
import org.mybranch.shop.products.domain.ProductBadRequestError;
import org.mybranch.shop.products.domain.ProductInternalError;
import org.mybranch.shop.products.domain.ProductNotFoundError;
import org.springframework.http.HttpStatus;

public class CustomErrorDecoder implements ErrorDecoder {

    @Override
    public Exception decode(String methodKey, Response response) {
        String requestUrl = response.request().url();
        Response.Body responseBody = response.body();
        HttpStatus responseStatus = HttpStatus.valueOf(response.status());

        try {
            if (HttpStatus.NOT_FOUND.equals(responseStatus)) {
                throw new ProductNotFoundError("Resource not found");
            }

            else if (responseStatus.is4xxClientError()) {
                throw new ProductBadRequestError("Client error");
            }

            else if (responseStatus.is5xxServerError()) {
                throw new ProductInternalError("Server error");
            }

            else {
                throw new ProductInternalError("Unknown error");
            }

        } catch (final Exception e) {
            throw new ProductInternalError("Malformed answer", e);
        }

    }
}