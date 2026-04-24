package org.challenge.products.infrastructure.mapper;

import org.challenge.products.domain.model.Product;
import org.challenge.products.infrastructure.dto.ProductDetailDto;
import org.challenge.products.infrastructure.dto.ProductResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    ProductResponseDto toResponseDTO(Product product);

    @Mapping(source = "id", target = "productId")
    Product toModel(ProductDetailDto productDetailDto);
}
