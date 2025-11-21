package com.example.demo.products.infrastructure.controller_inputPort;

import com.example.demo.products.application.dto.ProductApplication;
import com.example.demo.shared.valueObject.ResponseDTO;

import java.util.List;

public interface ProductController {

    ResponseDTO<List<ProductApplication>> getSimilarProducts(String productId);
}
