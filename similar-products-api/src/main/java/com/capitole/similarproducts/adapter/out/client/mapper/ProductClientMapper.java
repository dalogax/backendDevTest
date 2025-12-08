package com.capitole.similarproducts.adapter.out.client.mapper;

import com.capitole.similarproducts.domain.model.Product;
import com.capitole.similarproducts.generated.model.ProductDetail;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

/**
 * Product mapper for converting between external API DTOs and domain models.
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ProductClientMapper {
    
    /**
     * Maps from generated ProductDetail DTO to domain Product model.
     */
    Product toDomain(ProductDetail productDetail);
}
