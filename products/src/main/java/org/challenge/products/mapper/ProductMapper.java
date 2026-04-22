package org.challenge.products.mapper;

import org.challenge.products.domain.model.Product;
import org.challenge.products.infrastructure.dto.ProductDetailDto;
import org.challenge.products.infrastructure.dto.ProductResponseDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    ProductResponseDto toResponseDTO(Product product);
    Product toModel(ProductDetailDto productDetailDto);
}
