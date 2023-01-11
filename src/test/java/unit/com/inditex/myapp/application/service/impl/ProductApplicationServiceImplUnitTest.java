package unit.com.inditex.myapp.application.service.impl;

import com.inditex.myapp.application.mapper.OutputProductDetailMapper;
import com.inditex.myapp.application.service.impl.ProductApplicationServiceImpl;
import com.inditex.myapp.domain.service.ProductService;
import com.inditex.myapp.infrastructure.controller.model.ProductDetailDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.Set;

@ExtendWith(MockitoExtension.class)
class ProductApplicationServiceImplUnitTest {

    @InjectMocks
    private ProductApplicationServiceImpl productApplicationService;

    @Mock
    private ProductService productService;

    @Mock
    private OutputProductDetailMapper outputProductDetailMapper;

    @Test
    void getProductSimilarUnitTest() {
        Mockito.when(productService.productSimilar(Mockito.anyString())).thenReturn(Collections.emptyList());

        ResponseEntity<Set<ProductDetailDto>> response = productApplicationService.getProductSimilar("TEST");
        Assertions.assertNotNull(response);
        Assertions.assertNotNull(response.getStatusCode());
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());

        Mockito.verify(productService).productSimilar(Mockito.anyString());
        Mockito.verify(outputProductDetailMapper).map(Mockito.anyList());
    }
}
