package unit.com.inditex.myapp.domain.service.impl;

import com.inditex.myapp.domain.service.ExistingApiService;
import com.inditex.myapp.domain.service.impl.ProductServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplUnitTest {

    @InjectMocks
    private ProductServiceImpl productService;

    @Mock
    private ExistingApiService existingApiService;

    @Test
    void productSimilarUnitTest() {
        List<String> similarIdList = Collections.singletonList("SIMILARTEST");

        Mockito.when(existingApiService.getSimilarProducts(Mockito.anyString())).thenReturn(similarIdList);

        productService.productSimilar("TEST");

        Mockito.verify(existingApiService).getSimilarProducts(Mockito.anyString());
        Mockito.verify(existingApiService, Mockito.times(similarIdList.size())).getProduct(Mockito.anyString());
    }
}
