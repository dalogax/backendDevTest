package com.alejandro.api.mapper;

import com.alejandro.api.dto.ProductDetailDTO;
import com.alejandro.api.model.ProductDetailEntity;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductDetailMapper {
    ProductDetailEntity productDtoToEntity(ProductDetailDTO productDetailDTO);

    ProductDetailDTO productEntityToDto(ProductDetailEntity productDetailEntity);

    List<ProductDetailDTO> mapProductEntityListToDtoList(List<ProductDetailEntity> productDetailEntityList);

    List<ProductDetailEntity> mapProductDtoListToEntityList(List<ProductDetailDTO> productDetailDTOList);
}
