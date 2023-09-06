package com.dev.test.mapper;

import com.dev.test.domain.aggregate.SimilarProducts;
import com.dev.test.dto.SimilarProductsDTO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)public interface SimilarProductsMapper {
  SimilarProductsDTO asSimilarProductsDTO(SimilarProducts similarProducts);
}
