package com.diebuc.assessment.inditex.similarproducts.existingApis.client.api.v1;

import com.diebuc.assessment.inditex.similarproducts.existingApis.client.api.ApiClient;
import com.diebuc.assessment.inditex.similarproducts.existingApis.client.dto.ProductDetailClientDTO;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.reactive.function.client.WebClient.ResponseSpec;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;

@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen", date = "2023-06-16T13:43:15.618027100+02:00[Europe/Madrid]")
public class ExistingApis {
    private ApiClient apiClient;

    public ExistingApis() {
        this(new ApiClient());
    }

    @Autowired
    public ExistingApis(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    public ApiClient getApiClient() {
        return apiClient;
    }

    public void setApiClient(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    /**
     * Gets a product detail
     * Returns the product detail for a given productId
     * <p><b>200</b> - OK
     * <p><b>404</b> - Product Not found
     * @param productId The productId parameter
     * @return ProductDetailClientDTO
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    private ResponseSpec getProductProductIdRequestCreation(String productId) throws WebClientResponseException {
        Object postBody = null;
        // verify the required parameter 'productId' is set
        if (productId == null) {
            throw new WebClientResponseException("Missing the required parameter 'productId' when calling getProductProductId", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null, null, null);
        }
        // create path and map variables
        final Map<String, Object> pathParams = new HashMap<String, Object>();

        pathParams.put("productId", productId);

        final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<String, String>();
        final HttpHeaders headerParams = new HttpHeaders();
        final MultiValueMap<String, String> cookieParams = new LinkedMultiValueMap<String, String>();
        final MultiValueMap<String, Object> formParams = new LinkedMultiValueMap<String, Object>();

        final String[] localVarAccepts = { 
            "application/json"
        };
        final List<MediaType> localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
        final String[] localVarContentTypes = { };
        final MediaType localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

        String[] localVarAuthNames = new String[] {  };

        ParameterizedTypeReference<ProductDetailClientDTO> localVarReturnType = new ParameterizedTypeReference<ProductDetailClientDTO>() {};
        return apiClient.invokeAPI("/product/{productId}", HttpMethod.GET, pathParams, queryParams, postBody, headerParams, cookieParams, formParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }

    /**
     * Gets a product detail
     * Returns the product detail for a given productId
     * <p><b>200</b> - OK
     * <p><b>404</b> - Product Not found
     * @param productId The productId parameter
     * @return ProductDetailClientDTO
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    public Mono<ProductDetailClientDTO> getProductProductId(String productId) throws WebClientResponseException {
        ParameterizedTypeReference<ProductDetailClientDTO> localVarReturnType = new ParameterizedTypeReference<ProductDetailClientDTO>() {};
        return getProductProductIdRequestCreation(productId).bodyToMono(localVarReturnType);
    }

    public Mono<ResponseEntity<ProductDetailClientDTO>> getProductProductIdWithHttpInfo(String productId) throws WebClientResponseException {
        ParameterizedTypeReference<ProductDetailClientDTO> localVarReturnType = new ParameterizedTypeReference<ProductDetailClientDTO>() {};
        return getProductProductIdRequestCreation(productId).toEntity(localVarReturnType);
    }
    /**
     * Gets the ids of the similar products
     * Returns the similar products to a given one ordered by similarity
     * <p><b>200</b> - OK
     * @param productId The productId parameter
     * @return Set&lt;String&gt;
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    private ResponseSpec getProductSimilaridsRequestCreation(String productId) throws WebClientResponseException {
        Object postBody = null;
        // verify the required parameter 'productId' is set
        if (productId == null) {
            throw new WebClientResponseException("Missing the required parameter 'productId' when calling getProductSimilarids", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null, null, null);
        }
        // create path and map variables
        final Map<String, Object> pathParams = new HashMap<String, Object>();

        pathParams.put("productId", productId);

        final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<String, String>();
        final HttpHeaders headerParams = new HttpHeaders();
        final MultiValueMap<String, String> cookieParams = new LinkedMultiValueMap<String, String>();
        final MultiValueMap<String, Object> formParams = new LinkedMultiValueMap<String, Object>();

        final String[] localVarAccepts = { 
            "application/json"
        };
        final List<MediaType> localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
        final String[] localVarContentTypes = { };
        final MediaType localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

        String[] localVarAuthNames = new String[] {  };

        ParameterizedTypeReference<String> localVarReturnType = new ParameterizedTypeReference<String>() {};
        return apiClient.invokeAPI("/product/{productId}/similarids", HttpMethod.GET, pathParams, queryParams, postBody, headerParams, cookieParams, formParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }

    /**
     * Gets the ids of the similar products
     * Returns the similar products to a given one ordered by similarity
     * <p><b>200</b> - OK
     * @param productId The productId parameter
     * @return Set&lt;String&gt;
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    public Flux<String> getProductSimilarids(String productId) throws WebClientResponseException {
        ParameterizedTypeReference<String> localVarReturnType = new ParameterizedTypeReference<String>() {};
        return getProductSimilaridsRequestCreation(productId).bodyToFlux(localVarReturnType);
    }

    public Mono<ResponseEntity<List<String>>> getProductSimilaridsWithHttpInfo(String productId) throws WebClientResponseException {
        ParameterizedTypeReference<String> localVarReturnType = new ParameterizedTypeReference<String>() {};
        return getProductSimilaridsRequestCreation(productId).toEntityList(localVarReturnType);
    }
}
