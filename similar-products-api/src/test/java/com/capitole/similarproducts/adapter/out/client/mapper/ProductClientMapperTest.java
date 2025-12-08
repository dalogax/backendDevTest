package com.capitole.similarproducts.adapter.out.client.mapper;

import com.capitole.similarproducts.domain.model.Product;
import com.capitole.similarproducts.generated.model.ProductDetail;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class ProductClientMapperTest {

    private final ProductClientMapper mapper = Mappers.getMapper(ProductClientMapper.class);

    @Test
    void toDomain_ShouldMapAllFields() {
        ProductDetail detail = TestData.createProductDetail();

        Product result = mapper.toDomain(detail);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo("1");
        assertThat(result.name()).isEqualTo("Test Product");
        assertThat(result.price()).isEqualByComparingTo(new BigDecimal("99.99"));
        assertThat(result.availability()).isTrue();
    }

    @Test
    void toDomain_ShouldHandleNullInput() {
        Product result = mapper.toDomain(null);

        assertThat(result).isNull();
    }

    /**
     * Test data factory for ProductDetail.
     */
    private static class TestData {
        static ProductDetail createProductDetail() {
            ProductDetail detail = new ProductDetail();
            detail.setId("1");
            detail.setName("Test Product");
            detail.setPrice(new BigDecimal("99.99"));
            detail.setAvailability(true);
            return detail;
        }
    }
}
