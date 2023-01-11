package com.inditex.myapp.infrastructure.mapper;

import com.inditex.myapp.application.model.ProductDetailResponse;
import com.inditex.myapp.domain.model.ProductDetail;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class InputProductDetailMapper {

    @Autowired
    private ModelMapper modelMapper;

    public ProductDetail map(ProductDetailResponse productDetailResponse) {
        return modelMapper.map(productDetailResponse, ProductDetail.class);
    }
}
