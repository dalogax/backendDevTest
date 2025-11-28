package dev.molaya.tests.application.service;

import dev.molaya.tests.application.in.dto.GetSimilarProductsInput;
import dev.molaya.tests.application.out.dto.ProductCommand;
import dev.molaya.tests.domain.exceptions.BadParametersException;
import jakarta.validation.constraints.NotNull;
import java.util.Optional;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ServiceSimilarProductsMapper {
    @Mapping(target = "id", source = "productId")
    ProductCommand toProductCommand(@NotNull GetSimilarProductsInput source);

    default ProductCommand toProductCommandSafe(GetSimilarProductsInput source) {
        return Optional.ofNullable(source)
                .map(this::toProductCommand)
                .orElseThrow(() -> new BadParametersException("Product id should not be null"));
    }
}
