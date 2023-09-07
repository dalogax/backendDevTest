package com.product.client;


import com.product.dto.ProductDetailDTO;
import com.product.exception.InternalErrorException;
import com.product.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;


import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductClient {

    @Value("${app.client.url}")
    private String url;


    @Value("${app.client.path}")
    private String path;

    private final RestTemplate restTemplate;
    
     private static final  String NOT_FOUND_PRODUCTS = "Could not found details product of id: %s";
     
     private static final  String NOT_FOUND_SIMILAR_PRODUCTS = "Could not found similar products of id: %s";
     
     private static final  String INTERNAL_ERROR= "could not obtain the request data";

    public List<Integer> findSimilarProductsIds(String productId) {
        try {
            ResponseEntity<List<Integer>> response = restTemplate.exchange(
                    url.concat(productId).concat(path),
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<>() {
                    }
            );

            if (response.getStatusCode() == HttpStatus.OK && !CollectionUtils.isEmpty(response.getBody())) {
                return response.getBody();
            }
            throw new NotFoundException(String.format(NOT_FOUND_PRODUCTS, productId));
        } catch (HttpClientErrorException e) {
            log.error(e.getMessage());
            throw new NotFoundException(String.format(NOT_FOUND_PRODUCTS, productId));
        } catch (HttpServerErrorException | ResourceAccessException e) {
            log.error(e.getMessage());
            throw new InternalErrorException(INTERNAL_ERROR);
        }
    }

    public ProductDetailDTO findProductById(Integer productId) {

        try {
            ResponseEntity<ProductDetailDTO> response = restTemplate.getForEntity(url.concat(String.valueOf(productId)), ProductDetailDTO.class);
            if(response.getBody() == null){
                log.debug("Similar ids not found, productId: {}", productId);
                throw new NotFoundException(String.format(NOT_FOUND_SIMILAR_PRODUCTS, productId));
            }
            return response.getBody();
        }catch (HttpClientErrorException e){
            log.error(e.getMessage());
            throw new NotFoundException(String.format(NOT_FOUND_SIMILAR_PRODUCTS, productId));
        }catch (HttpServerErrorException | ResourceAccessException e){
            log.error(e.getMessage());
            throw new InternalErrorException(INTERNAL_ERROR);
        }
    }
}
