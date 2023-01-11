package unit.com.inditex.myapp.infrastructure.controller.impl;

import com.inditex.myapp.application.service.ProductApplicationService;
import com.inditex.myapp.infrastructure.controller.impl.ProductApiImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProductApiImplUnitTest {

    @InjectMocks
    private ProductApiImpl productApi;

    @Mock
    private ProductApplicationService productApplicationService;

    @Test
    void getProductSimilarUnitTest() {
        productApi.getProductSimilar("TEST");

        Mockito.verify(productApplicationService).getProductSimilar(Mockito.anyString());
    }
}
