package unit.com.inditex.myapp.infrastructure.config.endpoint;

import com.inditex.myapp.infrastructure.config.endpoint.ExistingApiEndpointConfig;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ExistingApiEndpointConfigUnitTest {

    @Test
    void creationTest() {
        ExistingApiEndpointConfig existingApiEndpointConfig = new ExistingApiEndpointConfig();
        existingApiEndpointConfig.setBasePath("TEST");
        existingApiEndpointConfig.setPort(0);

        Assertions.assertNotNull(existingApiEndpointConfig);
        Assertions.assertNotNull(existingApiEndpointConfig.getBasePath());
        Assertions.assertNotNull(existingApiEndpointConfig.getPort());
    }
}
