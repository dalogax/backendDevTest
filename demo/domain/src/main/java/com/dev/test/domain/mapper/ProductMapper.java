package com.dev.test.domain.mapper;

import com.dev.test.domain.aggregate.ProductDetail;
import com.dev.test.mock.response.MockClientResponse;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)public interface ProductMapper {
  ProductDetail asProductDetail(MockClientResponse mockClientResponse);
}
