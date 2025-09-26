package com.example.similarproducts.mapper;

import com.example.similarproducts.controller.dto.ProductDetailDTO;
import com.example.similarproducts.model.ProductDetail;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductDetailMapper {

  ProductDetailDTO toDTO(ProductDetail productDetail);

}
