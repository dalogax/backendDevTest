package com.capitole.similarproducts.adapter.in.rest.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.capitole.similarproducts.adapter.in.rest.dto.ProductDto;
import com.capitole.similarproducts.domain.model.Product;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class ProductMapperTest {

     private final ProductMapper mapper = Mappers.getMapper(ProductMapper.class);

    @Test
    void toDto_shouldConvertToDto() {
        Product source = TestData.getProduct();
        ProductDto actualResult = this.mapper.toDto(source);
        ProductDto expectedResult = TestData.getProductResponseDto();
        
        assertThat(actualResult).isEqualTo(expectedResult);
    }

    @Test
    void toDto_shouldReturnNull_whenInputIsNull() {
        ProductDto result = this.mapper.toDto(null);

        assertThat(result).isNull();
    }

    @Test
    void toDtoList_shouldConvertToDtoList() {
        List<Product> source = TestData.getProducts();
        List<ProductDto> actualResult = this.mapper.toDtoList(source);
        List<ProductDto> expectedResult = TestData.getProductResponseDtos();

        assertThat(actualResult).isEqualTo(expectedResult);
    }

    @Test
    void toDtoList_shouldReturnNull_whenInputIsNull() {
        List<ProductDto> result = this.mapper.toDtoList(null);

        assertThat(result).isNull();
    }

    @Test
    void toDomain_shouldConvertToDomain() {
        ProductDto source = TestData.getProductResponseDto();
        Product actualResult = mapper.toDomain(source);
        Product expectedResult = TestData.getProduct();

        assertThat(actualResult).isEqualTo(expectedResult);
    }

    @Test
    void toDomain_shouldReturnNull_whenInputIsNull() {
        Product result = this.mapper.toDomain(null);

        assertThat(result).isNull();
    }

    private static class TestData {
        private static final String ID = "1234";
        private static final String NAME = "productName";
        private static final BigDecimal PRICE = BigDecimal.ONE;
        private static final boolean AVAILABILITY = true;

        static Product getProduct() {
            return new Product(ID, NAME, PRICE, AVAILABILITY);
        }

        static List<Product> getProducts() {
            return List.of(getProduct(), getProduct());
        }

        static ProductDto getProductResponseDto() {
            return new ProductDto(ID, NAME, PRICE, AVAILABILITY);
        }

        static List<ProductDto> getProductResponseDtos() {
            return List.of(getProductResponseDto(), getProductResponseDto());
        }
    }
}
