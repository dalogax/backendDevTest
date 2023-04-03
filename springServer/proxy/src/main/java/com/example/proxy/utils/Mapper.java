package com.example.proxy.utils;

import com.example.proxy.dto.ProductDetailDTO;
import com.example.proxy.dto.SimilarProductsDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Mapper {

    public static SimilarProductsDTO jsonResponseToSimilarProductsDTO(String json) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(json, SimilarProductsDTO.class);
    }

    public static ProductDetailDTO jsonResponseToProductDetailDTO(String json) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(json, ProductDetailDTO.class);
    }
}
