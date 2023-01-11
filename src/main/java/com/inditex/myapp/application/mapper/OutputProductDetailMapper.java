package com.inditex.myapp.application.mapper;

import com.inditex.myapp.domain.model.ProductDetail;
import com.inditex.myapp.infrastructure.controller.model.ProductDetailDto;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class OutputProductDetailMapper {

    @Autowired
    private ModelMapper modelMapper;

    public ProductDetailDto map(ProductDetail productDetail) {
        return modelMapper.map(productDetail, ProductDetailDto.class);
    }

    public Set<ProductDetailDto> map(List<ProductDetail> productDetailList) {
        return productDetailList.stream().map(this::map).collect(Collectors.toSet());
    }
}
