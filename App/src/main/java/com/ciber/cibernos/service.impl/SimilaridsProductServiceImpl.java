package com.ciber.cibernos.service.impl;

import com.ciber.cibernos.dto.ProductDTO;
import com.ciber.cibernos.service.SimilaridsProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * @author samir alvarado
 */
@Service
@Slf4j
public class SimilaridsProductServiceImpl implements SimilaridsProductService {

    @Value("${similar-products-service-url}")
    private String similarProductsServiceUrl;

    @Value("${product-products-id-service-url}")
    private String productDetailsServiceUrl;
    @Autowired
    private RestTemplate restTemplate;



    public List<String> consultarSimilaridsId(Long productId) {
        try{
        String similarProductIdsUrl = similarProductsServiceUrl + "/product/" + productId + "/similarids";
        ResponseEntity<List<String>> similarProductIdsResponse = restTemplate.exchange(similarProductIdsUrl,
                HttpMethod.GET, null, new ParameterizedTypeReference<List<String>>(){});

            return similarProductIdsResponse.getBody();
        }catch(Exception e){
            log.error("el id", productId ," no pudo ser consultado.");
            return null;
        }
    }

    public ProductDTO consultarProducto(Long productId) {

        try {
            ResponseEntity<ProductDTO> response = restTemplate.getForEntity(productDetailsServiceUrl + productId, ProductDTO.class);
            return response.getBody();
        }catch  (Exception e) {
            log.error("el id", productId ," no pudo ser consultado.");
            return null;
        }

    }
}
