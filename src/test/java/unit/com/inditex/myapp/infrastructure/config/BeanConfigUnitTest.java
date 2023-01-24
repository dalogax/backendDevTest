package unit.com.inditex.myapp.infrastructure.config;

import com.inditex.myapp.domain.service.ExistingApiService;
import com.inditex.myapp.infrastructure.config.BeanConfig;
import com.inditex.myapp.infrastructure.config.endpoint.EndpointsConfig;
import com.inditex.myapp.infrastructure.config.endpoint.ExistingApiEndpointConfig;
import com.inditex.myapp.infrastructure.service.impl.ExistingApiServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BeanConfigUnitTest {

    @InjectMocks
    private BeanConfig beanConfig;

    @Test
    void productServiceUnitTest() {
        Assertions.assertNotNull(beanConfig.productService(Mockito.mock(ExistingApiServiceImpl.class)));
    }

    @Test
    void restTemplateUnitTest() {
        Assertions.assertNotNull(beanConfig.restTemplate());
    }

    @Test
    void defaultApiUnitTest() {
        EndpointsConfig endpointsConfig = new EndpointsConfig();
        endpointsConfig.setExistingApi(new ExistingApiEndpointConfig());

        Assertions.assertNotNull(beanConfig.defaultApi(endpointsConfig));
    }
}
