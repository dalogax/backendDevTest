package com.cibernos.prueba.infrastructure.mapper;

import com.cibernos.prueba.domain.Product;
import com.cibernos.prueba.infrastructure.entity.ProductEntity;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper (componentModel = "spring")
public interface ProductMapper {
    @Mappings({
           @Mapping(source = "id", target = "id"),
            @Mapping(source = "name", target = "name"),
            @Mapping(source = "price", target = "price"),
            @Mapping(source = "availability", target = "availability")
    })
    Product toProduct(ProductEntity productEntity);

    Iterable<Product> toProducts(Iterable<ProductEntity> productEntities);
    @InheritInverseConfiguration
    ProductEntity toProductEntity(Product product);
}
