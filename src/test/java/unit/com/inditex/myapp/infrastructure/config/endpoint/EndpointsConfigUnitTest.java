package unit.com.inditex.myapp.infrastructure.config.endpoint;

import com.inditex.myapp.infrastructure.config.endpoint.EndpointsConfig;
import com.inditex.myapp.infrastructure.config.endpoint.ExistingApiEndpointConfig;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class EndpointsConfigUnitTest {

    @Test
    void creationTest() {
        EndpointsConfig endpointsConfig = new EndpointsConfig();
        endpointsConfig.setExistingApi(new ExistingApiEndpointConfig());

        Assertions.assertNotNull(endpointsConfig);
        Assertions.assertNotNull(endpointsConfig.getExistingApi());
    }
}
