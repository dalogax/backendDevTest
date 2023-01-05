package org.mybranch.shop.products.infrastructure.clients.config;

import feign.Response;
import feign.codec.ErrorDecoder;
import org.apache.commons.io.IOUtils;
import org.mybranch.shop.products.domain.ProductBadRequestError;
import org.mybranch.shop.products.domain.ProductInternalError;
import org.mybranch.shop.products.domain.ProductNotFoundError;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class CustomErrorDecoder implements ErrorDecoder {

    @Override
    public Exception decode(String methodKey, Response response) {
        String requestUrl = response.request().url();
        Response.Body responseBody = response.body();
        HttpStatus responseStatus = HttpStatus.valueOf(response.status());
        String bodyMessage = "";
        if (responseBody != null) {
            try {
                bodyMessage = IOUtils.toString(responseBody.asReader(StandardCharsets.UTF_8));
            } catch (IOException e) {
                throw new ProductInternalError("Unknown error: " + bodyMessage);
            }
        }

        if (HttpStatus.NOT_FOUND.equals(responseStatus)) {
            throw new ProductNotFoundError("Resource not found: " + bodyMessage);
        }

        else if (responseStatus.is4xxClientError()) {
            throw new ProductBadRequestError("Client error: " + bodyMessage);
        }

        else if (responseStatus.is5xxServerError()) {
            throw new ProductInternalError("Server error: " + bodyMessage);
        }

        else {
            throw new ProductInternalError("Unknown error: " + bodyMessage);
        }

    }
}