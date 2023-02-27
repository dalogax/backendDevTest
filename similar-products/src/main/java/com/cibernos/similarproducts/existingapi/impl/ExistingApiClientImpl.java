package com.cibernos.similarproducts.existingapi.impl;

import com.cibernos.similarproducts.dto.ProductInfoDto;
import com.cibernos.similarproducts.exception.SimilarProductException;
import com.cibernos.similarproducts.existingapi.ExistingApiClient;
import com.google.gson.Gson;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.concurrent.TimeUnit;
import java.util.List;

@Component
public class ExistingApiClientImpl implements ExistingApiClient {
    private final Logger log = LoggerFactory.getLogger(ExistingApiClientImpl.class);

    @Value("${cibernos.existing-apis.base-url}")
    private String baseUrlSimilarIdsAPI;
    @Value("${cibernos.existing-apis.product-path}")
    private String productPath;
    @Value("${cibernos.existing-apis.similar-ids-path}")
    private String similarIdsPath;

    public List<String> getProductSimilarIds(String productId) {
        log.info("getProductSimilarIds(): {}", productId);
        String url = baseUrlSimilarIdsAPI + productPath + productId + similarIdsPath;
        log.info("Similar Ids Url: {}", url);

        OkHttpClient client = new OkHttpClient.Builder()
                .readTimeout(6, TimeUnit.SECONDS)
                .build();
        Request request = new Request.Builder()
                .url(url)
                .build();

        Call call = client.newCall(request);
        try (Response response = call.execute()){
            if (response.isSuccessful()) {
                assert response.body() != null;
                String responseBody = response.body().string();
                Gson gson = new Gson();
                String[] productSimilarIds = gson.fromJson(responseBody, String[].class);
                return List.of(productSimilarIds);
            } else {
                throw new IOException("Failed to retrieve data from " + url);
            }
        } catch (SocketTimeoutException e) {
            log.error("Timeout error: {}", e.getMessage());
            throw new SimilarProductException("TimeOut",HttpStatus.GATEWAY_TIMEOUT);
        } catch (IOException e) {
            log.error("Not found Error: {}", e.getMessage());
            throw new SimilarProductException("Product Not found",HttpStatus.NOT_FOUND);
        }
    }

    public ProductInfoDto getProductInfo(String productId) {
        log.info("getProductInfo(): {}", productId);
        String url = baseUrlSimilarIdsAPI + productPath + productId;
        log.info("Product Info Url: {}", url);

        OkHttpClient client = new OkHttpClient.Builder()
                .readTimeout(5, TimeUnit.SECONDS)
                .build();
        Request request = new Request.Builder()
                .url(url)
                .build();

        Call call = client.newCall(request);
        try (Response response = call.execute()) {
            if (response.isSuccessful()) {
                assert response.body() != null;
                String responseBody = response.body().string();
                Gson gson = new Gson();
                return gson.fromJson(responseBody, ProductInfoDto.class);
            } else {
                throw new IOException("Failed to retrieve data from " + url);
            }
        } catch (SocketTimeoutException e) {
            log.warn("{} to productId {}", e.getMessage(), productId);
        } catch (IOException e) {
            log.warn("Product not found: {}", e.getMessage());
        }
        return null;
    }
}
