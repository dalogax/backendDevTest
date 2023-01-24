package com.inditex.myapp.application.service.impl;

import com.inditex.myapp.application.mapper.OutputProductDetailMapper;
import com.inditex.myapp.application.service.ProductApplicationService;
import com.inditex.myapp.domain.model.ProductDetail;
import com.inditex.myapp.domain.service.ProductService;
import com.inditex.myapp.infrastructure.controller.model.ProductDetailDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class ProductApplicationServiceImpl implements ProductApplicationService {

    @Autowired
    private ProductService productService;

    @Autowired
    private OutputProductDetailMapper outputProductDetailMapper;

    @Override
    public ResponseEntity<Set<ProductDetailDto>> getProductSimilar(String productId) {
        List<ProductDetail> similarProductDetailList = productService.productSimilar(productId);
        return ResponseEntity.ok(outputProductDetailMapper.map(similarProductDetailList));
    }
}
