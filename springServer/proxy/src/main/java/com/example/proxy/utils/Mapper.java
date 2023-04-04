package com.example.proxy.utils;

import com.example.proxy.dto.ProductDetailDTO;
import com.example.proxy.dto.SimilarProductsDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

public class Mapper {
    public static SimilarProductsDTO jsonResponseToSimilarProductsDTO(String json) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        SimilarProductsDTO similarProductsDTO = new SimilarProductsDTO();
        int[] ids = objectMapper.readValue(json, int[].class);
        for (int id : ids) {
            similarProductsDTO.getIds().add(String.valueOf(id));
        }

        return similarProductsDTO;
    }

    public static ProductDetailDTO jsonResponseToProductDetailDTO(String json) {
        Gson gson = new Gson();
        return gson.fromJson(json, ProductDetailDTO.class);
    }
}
