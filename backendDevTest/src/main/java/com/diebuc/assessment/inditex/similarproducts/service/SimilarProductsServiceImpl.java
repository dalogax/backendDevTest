package com.diebuc.assessment.inditex.similarproducts.service;

import com.diebuc.assessment.inditex.similarproducts.dto.ProductDetailDTO;
import com.diebuc.assessment.inditex.similarproducts.existingApis.client.api.v1.ExistingApis;
import com.diebuc.assessment.inditex.similarproducts.existingApis.client.dto.ProductDetailClientDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class SimilarProductsServiceImpl implements  SimilarProductsService {

    //ToDo: Move to properties file i18n
    public static final String DESERALIZATION_ERROR_MESSAGE = "Error al deserializar los IDs de producto";

    @Override
    public Flux<ProductDetailDTO> getSimilarProducts(String productId) {
        ExistingApis existingApis = new ExistingApis();
        Flux<String> productSimilarids = existingApis.getProductSimilarids(productId);
        Mono<List<String>> productSimilarIdsList = productSimilarids
                .collectList()
                .map(jsonList -> {
                    try {
                        ObjectMapper mapper = new ObjectMapper();
                        return mapper.readValue(jsonList.get(0), new TypeReference<List<String>>(){});
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(DESERALIZATION_ERROR_MESSAGE, e);
                    }
                });
        Flux<String> productIds = productSimilarIdsList.flatMapMany(Flux::fromIterable);
        Flux<ProductDetailDTO> productDetails = productIds.flatMap(
                id -> existingApis.getProductProductId(id).map(this::toProductDetailDTO)
        );
        return productDetails;
    }

    private ProductDetailDTO toProductDetailDTO(ProductDetailClientDTO productDetail) {
        return new ProductDetailDTO()
                .builder()
                .id(productDetail.getId())
                .name(productDetail.getName())
                .price(productDetail.getPrice())
                .availability(productDetail.getAvailability())
                .build();
    }

}
