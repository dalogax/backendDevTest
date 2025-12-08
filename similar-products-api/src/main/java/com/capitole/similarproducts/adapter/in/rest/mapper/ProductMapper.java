package com.capitole.similarproducts.adapter.in.rest.mapper;

import com.capitole.similarproducts.adapter.in.rest.dto.ProductDto;
import com.capitole.similarproducts.domain.model.Product;
import java.util.List;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

/**
 * Product mapper for converting between domain models and DTOs.
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface ProductMapper {

    /**
     * Converts domain Product to ProductDto.
     *
     * @param product domain model
     * @return REST DTO
     */
    ProductDto toDto(Product product);

    /**
     * Converts list of domain Products to list of ProductDtos.
     *
     * @param products domain models
     * @return REST DTOs
     */
    List<ProductDto> toDtoList(List<Product> products);

    /**
     * Converts ProductDto to domain Product.
     *
     * @param dto REST DTO
     * @return domain model
     */
    Product toDomain(ProductDto dto);
}
