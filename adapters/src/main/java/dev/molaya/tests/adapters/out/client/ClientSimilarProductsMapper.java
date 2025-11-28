package dev.molaya.tests.adapters.out.client;

import dev.molaya.tests.adapters.out.client.dto.ProductDetail;
import dev.molaya.tests.domain.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface ClientSimilarProductsMapper {

    @Mapping(target = "available", source = "availability")
    Product toDomainProduct(ProductDetail source);
}
