package org.challenge.products.infrastructure.client;

import org.challenge.products.domain.exception.ProductNotFoundException;
import org.challenge.products.domain.model.Product;
import org.challenge.products.infrastructure.dto.ProductDetailDto;
import org.challenge.products.infrastructure.mapper.ProductMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.concurrent.TimeoutException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductClientTest {

    @InjectMocks
    private ProductClient productClient;

    @Mock
    private RestClient productRestClientMock;

    @Mock
    private ProductMapper productMapperMock;

    @Mock
    private RestClient.RequestHeadersUriSpec<?> requestHeadersUriSpecMock;
    @Mock
    private RestClient.RequestHeadersSpec<?> requestHeadersSpecMock;
    @Mock
    private RestClient.ResponseSpec responseSpecMock;

    @Test
    @DisplayName("Should return list of similar product ids")
    void shouldReturnSimilarProductIds() {
        String productId = "1";
        Integer similarId2 = 2;
        Integer similarId3 = 3;

        mockGetChain();
        when(responseSpecMock.body(any(ParameterizedTypeReference.class)))
                .thenReturn(List.of(similarId2, similarId3));

        List<String> result = productClient.getSimilarProductIds(productId);

        assertThat(result).containsExactly(
                similarId2.toString(),
                similarId3.toString()
        );
    }

    @Test
    @DisplayName("Should return empty list when similarids body is null")
    void shouldReturnEmptyListWhenBodyIsNull() {
        String productId = "1";

        mockGetChain();
        when(responseSpecMock.body(any(ParameterizedTypeReference.class)))
                .thenReturn(null);

        List<String> result = productClient.getSimilarProductIds(productId);

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Should return product detail")
    void shouldReturnProductDetail() {
        String productId = "2";
        String productName = "Product 2";
        Double productPrice = 19.99;
        Boolean productAvailability = true;

        ProductDetailDto dto = new ProductDetailDto(productId, productName, productPrice, productAvailability);
        Product expected = new Product(productId, productName, productPrice, productAvailability);

        mockGetChain();
        when(responseSpecMock.body(ProductDetailDto.class)).thenReturn(dto);
        when(productMapperMock.toModel(dto)).thenReturn(expected);

        Product result = productClient.getProduct(productId);

        assertThat(result).isEqualTo(expected);
    }

    @Test
    @DisplayName("getSimilarProductIds fallback should rethrow ProductNotFoundException")
    void fallbackShouldRethrowNotFoundException() {
        String productId = "4";
        String errorBody = "not found";

        ProductNotFoundException ex = new ProductNotFoundException(productId, errorBody);

        assertThatThrownBy(() -> productClient.getSimilarProductIdsFallback(productId, ex))
                .isInstanceOf(ProductNotFoundException.class);
    }

    @Test
    @DisplayName("getSimilarProductIds fallback should return empty list for generic throwable")
    void fallbackShouldReturnEmptyListForGenericError() {
        String productId = "5";
        String errorMessage = "connection refused";

        List<String> result = productClient.getSimilarProductIdsFallback(productId,
                new Throwable(errorMessage));

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("getProduct fallback should return null")
    void getProductFallbackShouldReturnNull() {
        String productId = "2";
        String errorMessage = "timeout";

        Product result = productClient.getProductFallback(productId,
                new TimeoutException(errorMessage));

        assertThat(result).isNull();
    }

    private void mockGetChain() {
        doReturn(requestHeadersUriSpecMock).when(productRestClientMock).get();
        doReturn(requestHeadersSpecMock).when(requestHeadersUriSpecMock).uri(any(String.class), any(Object.class));
        doReturn(responseSpecMock).when(requestHeadersSpecMock).retrieve();
        doReturn(responseSpecMock).when(responseSpecMock).onStatus(any(), any());
    }

}