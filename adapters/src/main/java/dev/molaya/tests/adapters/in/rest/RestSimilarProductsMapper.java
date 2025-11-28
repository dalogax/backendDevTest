package dev.molaya.tests.adapters.in.rest;

import dev.molaya.tests.adapters.in.rest.gen.openapi.dto.ProductDetail;
import dev.molaya.tests.application.in.dto.GetSimilarProductsInput;
import dev.molaya.tests.domain.Product;
import dev.molaya.tests.domain.exceptions.BadParametersException;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Mapper
public interface RestSimilarProductsMapper {

    @Mapping(target = "availability", source = "available")
    ProductDetail toRestProductDetail(Product product);

    default Mono<ResponseEntity<Flux<ProductDetail>>> wrapAsOkResponse(Flux<ProductDetail> dto) {
        return Mono.just(ResponseEntity.ok(dto));
    }

    default GetSimilarProductsInput toGetSimilarProductsInput(String productId) {
        return Optional.ofNullable(productId)
                .map(GetSimilarProductsInput::new)
                .orElseThrow(() -> new BadParametersException("Product id should not be null"));
    }
}
