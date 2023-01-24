package unit.com.inditex.myapp.infrastructure.service.impl;

import com.inditex.myapp.application.model.ProductDetailResponse;
import com.inditex.myapp.infrastructure.exception.ExistingApisErrorException;
import com.inditex.myapp.infrastructure.exception.ProductNotFoundException;
import com.inditex.myapp.infrastructure.mapper.InputProductDetailMapper;
import com.inditex.myapp.infrastructure.rest.DefaultApi;
import com.inditex.myapp.infrastructure.service.impl.ExistingApiServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import java.util.Collections;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class ExistingApiServiceImplUnitTest {

    @InjectMocks
    private ExistingApiServiceImpl existingApiService;

    @Mock
    private DefaultApi defaultApi;

    @Mock
    private InputProductDetailMapper inputProductDetailMapper;

    @Test
    void getProductUnitTest() {
        Mockito.when(defaultApi.getProductProductId(Mockito.anyString())).thenReturn(new ProductDetailResponse());

        existingApiService.getProduct("TEST");

        Mockito.verify(defaultApi).getProductProductId(Mockito.anyString());
        Mockito.verify(inputProductDetailMapper).map(Mockito.any());
    }

    @Test
    void getProductButClientErrorExceptionUnitTest() {
        Mockito.when(defaultApi.getProductProductId(Mockito.anyString())).thenThrow(HttpClientErrorException.class);
        Assertions.assertThrows(ProductNotFoundException.class, () -> existingApiService.getProduct("TEST"));
    }

    @Test
    void getProductButServerErrorExceptionUnitTest() {
        Mockito.when(defaultApi.getProductProductId(Mockito.anyString())).thenThrow(HttpServerErrorException.class);
        Assertions.assertThrows(ExistingApisErrorException.class, () -> existingApiService.getProduct("TEST"));
    }

    @Test
    void getSimilarProductsUnitTest() {
        Mockito.when(defaultApi.getProductSimilarids(Mockito.anyString())).thenReturn(Collections.emptySet());

        List<String> similarProducts = existingApiService.getSimilarProducts("TEST");

        Assertions.assertNotNull(similarProducts);
        Mockito.verify(defaultApi).getProductSimilarids(Mockito.any());
    }

    @Test
    void getSimilarProductsButClientErrorExceptionUnitTest() {
        Mockito.when(defaultApi.getProductSimilarids(Mockito.anyString())).thenThrow(HttpClientErrorException.class);
        Assertions.assertThrows(ProductNotFoundException.class, () -> existingApiService.getSimilarProducts("TEST"));
    }

    @Test
    void getSimilarProductsButServerErrorExceptionUnitTest() {
        Mockito.when(defaultApi.getProductSimilarids(Mockito.anyString())).thenThrow(HttpServerErrorException.class);
        Assertions.assertThrows(ExistingApisErrorException.class, () -> existingApiService.getSimilarProducts("TEST"));
    }
}
